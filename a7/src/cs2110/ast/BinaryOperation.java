package cs2110.ast;

import cs2110.ExpressionParser;
import java.util.function.BiFunction;

/**
 * An operation performed on two arithmetic expressions, `left` and `right`, that is represented
 * by the given `symbol` and modeled by the `BiFunction op`.
 */
public record BinaryOperation(Expression left, Expression right, char symbol,
                              BiFunction<Integer, Integer, Integer> op) implements Expression {

    /**
     * Returns the fully parenthesized infix String representation of this binary operation.
     * The format consists of an opening parenthesis, the left operand's infix string,
     * a single space, the operator symbol, a single space, the right operand's infix string,
     * and a closing parenthesis.
     */
    @Override
    public String infixString() {
        return "(" + this.left.infixString() + " " + this.symbol + " " +
                this.right.infixString() + ")";
    }

    /**
     * Recursively evaluates the left and right children and applies this BinaryOperation's
     * stored `op` BiFunction to their int results. Throws UnassignedVariable if either child
     * contains any Variable.
     */
    @Override
    public int evaluate() throws UnassignedVariable {
        int leftVal = left.evaluate();
        int rightVal = right.evaluate();
        return op.apply(leftVal, rightVal);
    }

    /**
     * Returns a new BinaryOperation in which every occurrence of the given `variable` has been
     * replaced by `expr` in both the left and right subtrees. The symbol and op are preserved.
     */
    @Override
    public Expression substitute(char variable, Expression expr) {
        Expression newLeft = left.substitute(variable, expr);
        Expression newRight = right.substitute(variable, expr);
        return new BinaryOperation(newLeft, newRight, symbol, op);
    }

    /**
     * Recursively simplifies both children. If the resulting BinaryOperation contains no
     * Variables (i.e., it is purely constant), folds it into a single Constant holding the
     * evaluated value. Otherwise returns a new BinaryOperation composed of the simplified
     * children. Uses `evaluate()` and catches UnassignedVariable to detect the fully-constant
     * case polymorphically without any dynamic type queries.
     */
    @Override
    public Expression simplify() {
        Expression leftS = left.simplify();
        Expression rightS = right.simplify();
        BinaryOperation candidate = new BinaryOperation(leftS, rightS, symbol, op);
        try {
            return new Constant(candidate.evaluate());
        } catch (UnassignedVariable e) {
            return candidate;
        }
    }

    /**
     * Returns a fully-expanded Expression mathematically equivalent to this BinaryOperation.
     * For `+` and `-`, recursively expands each child and rebuilds. For `*`, recursively
     * expands each child and then applies the distributive property via `multTimes`, giving
     * precedence to right distributivity (distributing the left operand first if it is a sum
     * or difference). Requires that this tree contains no UnaryOperations and only `+`, `-`,
     * and `*` BinaryOperations.
     */
    @Override
    public Expression expand() {
        Expression leftE = left.expand();
        Expression rightE = right.expand();
        if (symbol == '*') {
            return leftE.multTimes(rightE);
        }
        return new BinaryOperation(leftE, rightE, symbol, op);
    }

    /**
     * Returns a fully-expanded Expression mathematically equivalent to `this * other`. If this
     * BinaryOperation is a sum or difference (`+` or `-`), distributes the multiplication over
     * its children (right distributivity): `(a OP b) * other = (a * other) OP (b * other)`,
     * recursively applying `multTimes` to handle any further distribution. Otherwise (this is
     * `*`), delegates to `other.distributeFromLeft(this)` so that left distributivity may
     * occur on the right operand. Requires that `this` and `other` are already expanded.
     */
    @Override
    public Expression multTimes(Expression other) {
        if (symbol == '+' || symbol == '-') {
            // Right distributivity: distribute this sum/difference first.
            Expression newLeft = left.multTimes(other);
            Expression newRight = right.multTimes(other);
            return new BinaryOperation(newLeft, newRight, symbol, op);
        }
        // this is '*' — not a sum/diff, so try left distributivity on the right operand.
        return other.distributeFromLeft(this);
    }

    /**
     * Returns a fully-expanded Expression mathematically equivalent to `other * this`. If this
     * BinaryOperation is a sum or difference, distributes the multiplication over its children
     * (left distributivity): `other * (c OP d) = (other * c) OP (other * d)`, recursively
     * applying `multTimes` to handle any further distribution. Otherwise (this is `*`), returns
     * a plain BinaryOperation `other * this`. Requires that `this` and `other` are already
     * expanded.
     */
    @Override
    public Expression distributeFromLeft(Expression other) {
        if (symbol == '+' || symbol == '-') {
            Expression newLeft = other.multTimes(left);
            Expression newRight = other.multTimes(right);
            return new BinaryOperation(newLeft, newRight, symbol, op);
        }
        return new BinaryOperation(other, this, '*', ExpressionParser.MULTIPLICATION);
    }

    @Override
    public String[] treeStringLinesRecursive() {
        String[] leftLines = left.treeStringLinesRecursive();   // left subtree String
        String[] rightLines = right.treeStringLinesRecursive(); // right subtree String
        int rows = 2 + Math.max(leftLines.length, rightLines.length);
        StringBuilder[] sb = new StringBuilder[rows]; // we'll use StringBuilder to make repeated concatenations more performant
        for (int i = 0; i < rows; i++) {
            sb[i] = new StringBuilder(); // all rows start as empty strings
        }

        sb[1].append(" ".repeat(rootIndex(leftLines[0]))); // padding for left half of left subtree
        sb[1].append("┌"); // branch down to left subtree
        sb[1].append("─".repeat(leftLines[0].length() - rootIndex(leftLines[0]) - 1)); // horizontal bar
        sb[1].append("┴"); // branch up to root, both subtrees
        sb[0].append(" ".repeat(sb[1].length() - 1)); // left padding
        sb[0].append(symbol);
        for (int i = 0; i < leftLines.length; i++) {
            sb[i + 2].append(leftLines[i]); // left subtree's rows
            sb[i + 2].append(" "); // right padding
        }
        for (int j = leftLines.length + 2; j < rows; j++) {
            sb[j].append(" ".repeat(sb[0].length()));
        }

        sb[1].append("─".repeat(rootIndex(rightLines[0]))); // horizontal bar
        sb[1].append("┐"); // branch down to right subtree
        sb[1].append(" ".repeat(rightLines[0].length() - rootIndex(rightLines[0]) - 1)); // right padding
        sb[0].append(" ".repeat(sb[1].length() - sb[0].length())); // right padding
        for (int i = 0; i < rightLines.length; i++) {
            sb[i + 2].append(rightLines[i]); // right subtree's rows
        }
        for (int j = rightLines.length + 2; j < rows; j++) {
            sb[j].append(" ".repeat(rightLines[0].length()));
        }

        // convert StringBuilder[] array to String[] array
        String[] lines = new String[rows];
        int length = sb[0].toString().length();
        for (int i = 0; i < rows; i++) {
            lines[i] = sb[i].toString();
            assert length == lines[i].length();
        }
        return lines;
    }

    /**
     * Returns the index of the rootIndex of the String excluding its leading and trailing spaces.
     * Used to determine where to position the parent connection for the subtree root.
     */
    private static int rootIndex(String s) {
        int i = 0;
        /* Loop invariant: s[..i) = ' ' */
        while (i < s.length() && s.charAt(i) == ' ') {
            i++;
        }
        return i;
    }
}
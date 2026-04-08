package cs2110.ast;

import cs2110.ExpressionParser;
import java.util.function.Function;

/**
 * An operation performed on an arithmetic expression, `arg`, that is represented by the given
 * `symbol` and modeled by the `Function op`.
 */
public record UnaryOperation(Expression arg, char symbol, Function<Integer, Integer> op)
        implements Expression {

    /**
     * Returns the infix String representation of this unary operation.
     * The format consists of the operator symbol followed by the operand's
     * infix string representation, with no spaces and no surrounding parentheses.
     */
    @Override
    public String infixString() {
        return this.symbol + arg.infixString();
    }

    /**
     * Recursively evaluates the operand and applies this UnaryOperation's stored `op` Function
     * to its int result. Throws UnassignedVariable if the operand contains any Variable.
     */
    @Override
    public int evaluate() throws UnassignedVariable {
        int argVal = arg.evaluate();
        return op.apply(argVal);
    }

    /**
     * Returns a new UnaryOperation in which every occurrence of the given `variable` has been
     * replaced by `expr` in the operand. The symbol and op are preserved.
     */
    @Override
    public Expression substitute(char variable, Expression expr) {
        Expression newArg = arg.substitute(variable, expr);
        return new UnaryOperation(newArg, symbol, op);
    }

    /**
     * Recursively simplifies the operand. If the resulting UnaryOperation contains no
     * Variables, folds it into a single Constant holding the evaluated value. Otherwise
     * returns a new UnaryOperation composed of the simplified operand.
     */
    @Override
    public Expression simplify() {
        Expression argS = arg.simplify();
        UnaryOperation candidate = new UnaryOperation(argS, symbol, op);
        try {
            return new Constant(candidate.evaluate());
        } catch (UnassignedVariable e) {
            return candidate;
        }
    }

    /**
     * To limit the scope of the assignment, we do not support expansion over expressions involving
     * UnaryNegation. This presents its own set of challenges. You're welcome to implement this if
     * you'd like (under your own chosen refinement of the specs), but make sure that it doesn't
     * break any other functionality. The autograder won't ever call this method.
     */
    @Override
    public Expression expand() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a fully-expanded Expression mathematically equivalent to `this * other`. Since
     * expand()'s precondition excludes UnaryOperations, this is provided only to satisfy the
     * Expression interface. Delegates to `other.distributeFromLeft(this)` to allow any
     * distribution on the right operand; treats this UnaryOperation itself as a non-sum/diff.
     */
    @Override
    public Expression multTimes(Expression other) {
        return other.distributeFromLeft(this);
    }

    /**
     * Returns a new BinaryOperation representing `other * this`. Provided only to satisfy the
     * Expression interface; not expected to be invoked since expand()'s precondition excludes
     * UnaryOperations.
     */
    @Override
    public Expression distributeFromLeft(Expression other) {
        return new BinaryOperation(other, this, '*', ExpressionParser.MULTIPLICATION);
    }

    @Override
    public String[] treeStringLinesRecursive() {
        String[] subtreeLines = arg.treeStringLinesRecursive();
        int rows = 2 + subtreeLines.length;
        StringBuilder[] sb = new StringBuilder[rows]; // we'll use StringBuilder to make repeated concatenations more performant
        for (int i = 0; i < rows; i++) {
            sb[i] = new StringBuilder(); // all rows start as empty strings
        }

        // length of bar between root and left subtree root
        int lpad = rootIndex(subtreeLines[0]);
        int rpad = subtreeLines[0].length() - rootIndex(subtreeLines[0]) - 1;
        sb[0].append(" ".repeat(lpad)); // padding for left half of left subtree
        sb[0].append(symbol); // branch down to left subtree
        sb[0].append(" ".repeat(rpad)); // horizontal bar
        sb[1].append(" ".repeat(lpad)); // padding for left half of left subtree
        sb[1].append("│"); // branch down to left subtree
        sb[1].append(" ".repeat(rpad)); // horizontal bar
        for (int i = 2; i < rows; i++) {
            sb[i].append(subtreeLines[i - 2]); // left subtree's rows
        }

        // convert StringBuilder[] array to String[] array
        String[] lines = new String[rows];
        for (int i = 0; i < rows; i++) {
            lines[i] = sb[i].toString();
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

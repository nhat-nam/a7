package cs2110.ast;

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
     * Evaluates the expression of this unary operation before applying negation and returning
     * the resulting integer. If met with unary operations, this method is called recursively.
     */
    @Override
    public int evaluate() throws UnassignedVariable {
        int evaluatedOperand = this.arg.evaluate();
        return this.op.apply(evaluatedOperand);
    }

    /**
     * Returns a new Expression where all occurrences of the target variable
     * have been replaced with the replacement Expression.
     * In this case, return a new UnaryOperation with arg being substituted
     * appropriately as mentioned above.
     */
    @Override
    public Expression substitute(char variable, Expression expr) {
        Expression substituted = this.arg.substitute(variable, expr);
        return new UnaryOperation(substituted, this.symbol, this.op);
    }

    /**
     * Returns a simplified version of this unary operation.
     * It first simplifies its operand. If the resulting expression can be fully evaluated
     * (contains no variables), it folds it into a new Constant.
     * Otherwise, it returns a new UnaryOperation containing the simplified operand.
     */
    @Override
    public Expression simplify() {
        //we simplify using the same recursive logic in substitute()
        Expression simplifiedOperand = this.arg.simplify();
        Expression simplifiedNode = new UnaryOperation(simplifiedOperand, this.symbol, this.op);

        //then we try to evaluate it in case constant folding works. if not, we catch exception thrown
        //when encountering variable and return the simplified node
        try {
            int evaluated = simplifiedNode.evaluate();
            return new Constant(evaluated);
        } catch (UnassignedVariable e) {
            return simplifiedNode;
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

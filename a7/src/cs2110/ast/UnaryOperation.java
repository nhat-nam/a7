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

    @Override
    public int evaluate() throws UnassignedVariable {
        // TODO 4.1D: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression substitute(char variable, Expression expr) {
        // TODO 4.2D: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression simplify() {
        // TODO 4.3D: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
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

package cs2110.ast;

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

    @Override
    public int evaluate() throws UnassignedVariable {
        // TODO 4.1C: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression substitute(char variable, Expression expr) {
        // TODO 4.2C: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression simplify() {
        // TODO 4.3C: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression expand() {
        // TODO 4.4: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
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
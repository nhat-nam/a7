package cs2110.ast;

/**
 * Models an expression that is statically typed to an `int`.
 */
public interface Expression {

    /**
     * Return the infix form of this expression. Parentheses are included around all binary
     * operations in this expression to over-correct for the ambiguity of infix notation.
     */
    String infixString();

    /**
     * Returns the `int` value of this expression. Throws `UnassignedVariable` if the expression
     * contains any Variables.
     */
    int evaluate() throws UnassignedVariable;

    /**
     * Returns the Expression that results when every instance of the given `variable` is replaced
     * with the given `expr`ession.
     */
    Expression substitute(char variable, Expression expr);

    /**
     * Returns the simplified form of this expression, such that all BinaryOperation and
     * UnaryOperation nodes have at least one non-Constant child Expression.
     */
    Expression simplify();

    /**
     * Uses distributivity to return a mathematically equivalent expression tree in which no
     * multiplication BinaryOperation node has an addition or subtraction  BinaryOperation node
     * as its child. Requires that this tree does not contain any UnaryOperations, and its only
     * BinaryOperations correspond to the '+', '-', and '*' operations.
     */
    Expression expand();

    /**
     * Returns a String representation of the tree structure of this expression.
     */
    default String treeString() {
        StringBuilder sb = new StringBuilder();
        for (String line : treeStringLinesRecursive()) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns an array of Strings representing each line in the String representation of this
     * expression tree. All Strings in the returned array are guaranteed to have the same length.
     * This method works by laying out its subtrees' representations with adequate spacing to
     * prevent overlapping and using Unicode characters to draw the connections from these subtrees
     * to the root.
     */
    String[] treeStringLinesRecursive();
}


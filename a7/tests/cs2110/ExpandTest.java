package cs2110;

import static cs2110.ExpressionParser.*;
import cs2110.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the expand() method (challenge task 4.4). This file is intentionally kept separate
 * from ExpressionTest.java so that it is NOT submitted to the A7 Code Gradescope assignment, per
 * the PDF's instructions: "If you attempt this portion of the assignment, write any unit tests in
 * a separate file so they are not submitted with the rest of your ExpressionTests."
 */
public class ExpandTest {

    /**
     * Helper method to return a BinaryOperation representing the addition of the given `left` and
     * `right` operands.
     */
    public Expression addExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '+', ADDITION);
    }

    /**
     * Helper method to return a BinaryOperation representing the subtraction of the given `left`
     * and `right` operands.
     */
    public Expression subtractExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '-', SUBTRACTION);
    }

    /**
     * Helper method to return a BinaryOperation representing the multiplication of the given
     * `left` and `right` operands.
     */
    public Expression multExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '*', MULTIPLICATION);
    }

    /**
     * Helper method to return a UnaryOperation representing the negation of the given `arg`.
     */
    public Expression negExpr(Expression arg) {
        return new UnaryOperation(arg, '-', NEGATION);
    }

    /* *********************************************************************************
     * Tests for expand()                                                              *
     ***********************************************************************************/

    @DisplayName("WHEN expand() is called on a Constant, "
            + "THEN the same Constant is returned.")
    @Test
    void testExpandConstant() {
        Expression e = new Constant(7);
        Expression expected = new Constant(7);
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a Variable, "
            + "THEN the same Variable is returned.")
    @Test
    void testExpandVariable() {
        Expression e = new Variable('x');
        Expression expected = new Variable('x');
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a simple addition of two Variables with no "
            + "multiplications, THEN the expression is returned unchanged.")
    @Test
    void testExpandSimpleAdditionUnchanged() {
        Expression e = addExpr(new Variable('a'), new Variable('b'));
        Expression expected = addExpr(new Variable('a'), new Variable('b'));
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a simple subtraction of two Variables with no "
            + "multiplications, THEN the expression is returned unchanged.")
    @Test
    void testExpandSimpleSubtractionUnchanged() {
        Expression e = subtractExpr(new Variable('a'), new Variable('b'));
        Expression expected = subtractExpr(new Variable('a'), new Variable('b'));
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a sum that contains no inner multiplication nodes, "
            + "THEN the tree is returned unchanged.")
    @Test
    void testExpandAdditionNoMultiplication() {
        Expression e = addExpr(addExpr(new Variable('a'), new Variable('b')), new Variable('c'));
        Expression expected =
                addExpr(addExpr(new Variable('a'), new Variable('b')), new Variable('c'));
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a multiplication whose neither operand is a sum or "
            + "difference, THEN the multiplication is returned unchanged.")
    @Test
    void testExpandMultiplicationNoSumOperands() {
        Expression e = multExpr(new Variable('a'), new Variable('b'));
        Expression expected = multExpr(new Variable('a'), new Variable('b'));
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a multiplication of two Constants, "
            + "THEN the multiplication is returned unchanged (it is not folded).")
    @Test
    void testExpandMultiplicationOfConstants() {
        Expression e = multExpr(new Constant(2), new Constant(3));
        Expression expected = multExpr(new Constant(2), new Constant(3));
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on (a + b) * c, "
            + "THEN the multiplication right-distributes to ((a * c) + (b * c)).")
    @Test
    void testExpandRightDistributionOverAddition() {
        Expression e = multExpr(addExpr(new Variable('a'), new Variable('b')), new Variable('c'));
        Expression expected = addExpr(
                multExpr(new Variable('a'), new Variable('c')),
                multExpr(new Variable('b'), new Variable('c'))
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a * (b + c), "
            + "THEN the multiplication left-distributes to ((a * b) + (a * c)).")
    @Test
    void testExpandLeftDistributionOverAddition() {
        Expression e = multExpr(new Variable('a'), addExpr(new Variable('b'), new Variable('c')));
        Expression expected = addExpr(
                multExpr(new Variable('a'), new Variable('b')),
                multExpr(new Variable('a'), new Variable('c'))
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on (a - b) * c, "
            + "THEN the multiplication distributes to ((a * c) - (b * c)).")
    @Test
    void testExpandRightDistributionOverSubtraction() {
        Expression e = multExpr(
                subtractExpr(new Variable('a'), new Variable('b')),
                new Variable('c')
        );
        Expression expected = subtractExpr(
                multExpr(new Variable('a'), new Variable('c')),
                multExpr(new Variable('b'), new Variable('c'))
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a * (b - c), "
            + "THEN the multiplication distributes to ((a * b) - (a * c)).")
    @Test
    void testExpandLeftDistributionOverSubtraction() {
        Expression e = multExpr(
                new Variable('a'),
                subtractExpr(new Variable('b'), new Variable('c'))
        );
        Expression expected = subtractExpr(
                multExpr(new Variable('a'), new Variable('b')),
                multExpr(new Variable('a'), new Variable('c'))
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on the PDF example (a + b) * (c + d), "
            + "THEN right distributivity takes precedence and the result is "
            + "(((a * c) + (a * d)) + ((b * c) + (b * d))).")
    @Test
    void testExpandPdfExample() {
        Expression e = multExpr(
                addExpr(new Variable('a'), new Variable('b')),
                addExpr(new Variable('c'), new Variable('d'))
        );
        Expression expected = addExpr(
                addExpr(
                        multExpr(new Variable('a'), new Variable('c')),
                        multExpr(new Variable('a'), new Variable('d'))
                ),
                addExpr(
                        multExpr(new Variable('b'), new Variable('c')),
                        multExpr(new Variable('b'), new Variable('d'))
                )
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on (a - b) * (c + d), "
            + "THEN right distributivity takes precedence and the subtraction propagates to "
            + "produce (((a * c) + (a * d)) - ((b * c) + (b * d))).")
    @Test
    void testExpandSubtractionTimesAddition() {
        Expression e = multExpr(
                subtractExpr(new Variable('a'), new Variable('b')),
                addExpr(new Variable('c'), new Variable('d'))
        );
        Expression expected = subtractExpr(
                addExpr(
                        multExpr(new Variable('a'), new Variable('c')),
                        multExpr(new Variable('a'), new Variable('d'))
                ),
                addExpr(
                        multExpr(new Variable('b'), new Variable('c')),
                        multExpr(new Variable('b'), new Variable('d'))
                )
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a tree with Constants inside a distribution, "
            + "THEN the Constants are propagated through the distribution without being folded.")
    @Test
    void testExpandWithConstants() {
        Expression e = multExpr(addExpr(new Constant(2), new Variable('x')), new Constant(3));
        Expression expected = addExpr(
                multExpr(new Constant(2), new Constant(3)),
                multExpr(new Variable('x'), new Constant(3))
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a nested multiplication whose right operand is "
            + "itself an expandable expression, THEN the distribution is applied recursively.")
    @Test
    void testExpandNestedInnerExpansion() {
        Expression e = multExpr(
                new Variable('a'),
                multExpr(addExpr(new Variable('b'), new Variable('c')), new Variable('d'))
        );
        Expression expected = addExpr(
                multExpr(new Variable('a'), multExpr(new Variable('b'), new Variable('d'))),
                multExpr(new Variable('a'), multExpr(new Variable('c'), new Variable('d')))
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on an addition whose operands each contain expandable "
            + "multiplications, THEN each operand is expanded independently and recombined as an "
            + "addition.")
    @Test
    void testExpandAdditionWithExpandableOperands() {
        Expression e = addExpr(
                multExpr(addExpr(new Variable('a'), new Variable('b')), new Variable('c')),
                multExpr(new Variable('d'), addExpr(new Variable('e'), new Variable('f')))
        );
        Expression expected = addExpr(
                addExpr(
                        multExpr(new Variable('a'), new Variable('c')),
                        multExpr(new Variable('b'), new Variable('c'))
                ),
                addExpr(
                        multExpr(new Variable('d'), new Variable('e')),
                        multExpr(new Variable('d'), new Variable('f'))
                )
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a subtraction whose operands each contain expandable "
            + "multiplications, THEN each operand is expanded independently and recombined as a "
            + "subtraction.")
    @Test
    void testExpandSubtractionWithExpandableOperands() {
        Expression e = subtractExpr(
                multExpr(addExpr(new Variable('a'), new Variable('b')), new Variable('c')),
                multExpr(new Variable('d'), addExpr(new Variable('e'), new Variable('f')))
        );
        Expression expected = subtractExpr(
                addExpr(
                        multExpr(new Variable('a'), new Variable('c')),
                        multExpr(new Variable('b'), new Variable('c'))
                ),
                addExpr(
                        multExpr(new Variable('d'), new Variable('e')),
                        multExpr(new Variable('d'), new Variable('f'))
                )
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN expand() is called on a top-level multiplication whose left operand "
            + "contains a deeper sum-of-products, THEN the distribution propagates through to "
            + "produce a fully-expanded tree.")
    @Test
    void testExpandDeepRecursive() {
        Expression e = multExpr(
                addExpr(addExpr(new Variable('a'), new Variable('b')), new Variable('c')),
                new Variable('d')
        );
        Expression expected = addExpr(
                addExpr(
                        multExpr(new Variable('a'), new Variable('d')),
                        multExpr(new Variable('b'), new Variable('d'))
                ),
                multExpr(new Variable('c'), new Variable('d'))
        );
        Expression actual = e.expand();
        assertEquals(expected, actual);
    }
}

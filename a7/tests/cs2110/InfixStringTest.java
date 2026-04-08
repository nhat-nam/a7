package cs2110;

import static cs2110.ExpressionParser.*;
import cs2110.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InfixStringTest {

    /**
     * Helper method to return a BinaryOperation representing the addition of the given `left` and
     * `right` operands.
     */
    public Expression addExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '+', ADDITION);
    }

    /**
     *  Helper method to return a BinaryOperation representing the multiplication of the given
     *  `left` and `right` operands.
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

    /**
     * Helper method to return a BinaryOperation representing the subtraction of the given `left` and
     * `right` operands.
     */
    public Expression subtractExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '-', SUBTRACTION);
    }

    /* *********************************************************************************
     * Basic tests for infixString() involving at most one BinaryOperation             *
     ***********************************************************************************/

    @DisplayName("WHEN infixString() is called on a Constant with a single-digit value, "
            + "THEN the returned string is exactly that digit with no additional formatting.")
    @Test
    void testConstantBasic() {
        Expression c = new Constant(5);
        String expected = "5";
        String actual = c.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a Constant with a multi-digit value, "
            + "THEN the returned string is the full integer with no truncation or extra "
            + "formatting.")
    @Test
    void testConstantMultiDigit() {
        Expression c = new Constant(2110);
        String expected = "2110";
        String actual = c.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a Constant with a negative value, "
            + "THEN the returned string includes the negative sign as part of the number.")
    @Test
    void testConstantNegativeValue() {
        Expression c = new Constant(-2112);
        String expected = "-2112";
        String actual = c.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a Variable, THEN the returned string "
            + "is exactly the variable’s name.")
    @Test
    void testVariableBasic() {
        Expression v = new Variable('k');
        String expected = "k";
        String actual = v.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a UnaryOperation applied to a Constant, "
            + "THEN the returned string is the symbol followed directly by the constant’s string.")
    @Test
    void testUnaryConstant() {
        Expression u = negExpr(new Constant(67));
        String expected = "-67";
        String actual = u.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a UnaryOperation applied to a Variable, "
            + "THEN the returned string is the symbol followed directly by the variable’s name.")
    @Test
    void testUnaryVariable() {
        Expression u = negExpr(new Variable('d'));
        String expected = "-d";
        String actual = u.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on nested UnaryOperations, "
            + "THEN the returned string contains repeated symbols.")
    @Test
    void testUnaryDouble() {
        Expression u = negExpr(negExpr(new Constant(2110)));
        String expected = "--2110";
        String actual = u.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on unary negation of a negative constant, THEN "
            + "the returned string contains repeated negative signs.")
    @Test
    void testNegationNegative() {
        Expression u = negExpr(new Constant(-3110));
        String expected = "--3110";
        String actual = u.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a UnaryOperation besides negation, "
            + "THEN the returned string consists of the operation symbol followed by "
            + "the operand with no parentheses or spaces.")
    @Test
    void testOtherUnary() {
        Expression e = new UnaryOperation(new Constant(5), '$', x -> x * 100);
        String expected = "$5";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on any BinaryOperation, "
            + "THEN the returned string is enclosed in parentheses regardless of operator "
            + "precedence.")
    @Test
    void testBinaryAlwaysParenthesized() {
        // multiplication
        Expression m = multExpr(new Variable('x'), new Variable('y'));
        assertTrue(m.infixString().startsWith("("));
        assertTrue(m.infixString().endsWith(")"));

        // addition
        Expression a = addExpr(new Variable('x'), new Variable('y'));
        assertTrue(a.infixString().startsWith("("));
        assertTrue(a.infixString().endsWith(")"));

        // subtraction
        Expression s = subtractExpr(new Variable('x'), new Variable('y'));
        assertTrue(s.infixString().startsWith("("));
        assertTrue(s.infixString().endsWith(")"));
    }

    @DisplayName("WHEN infixString() is called on a BinaryOperation, "
            + "THEN the returned string contains exactly one space on each side of the operator.")
    @Test
    void testBinarySpacingAroundOperator() {
        Expression b = subtractExpr(new Constant(12), new Constant(3));
        String expected = "(12 - 3)";
        String actual = b.infixString();
        assertEquals(expected, actual);
        assertTrue(actual.contains(" - "));
    }

    @DisplayName("WHEN infixString() is called on a BinaryOperation representing addition "
            + "of two Constants, THEN the returned string is the parenthesized infix "
            + "expression with spaces around '+'.")
    @Test
    void testBinaryAddition() {
        Expression b = addExpr(new Constant(9), new Constant(10));
        String expected = "(9 + 10)";
        String actual = b.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a BinaryOperation representing subtraction "
            + "of two Constants, THEN the returned string is the parenthesized infix expression "
            + "with spaces around '-'.")
    @Test
    void testBinarySubtraction() {
        Expression b = subtractExpr(new Constant(834), new Constant(1));
        String expected = "(834 - 1)";
        String actual = b.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a BinaryOperation representing multiplication "
            + "of two Constants, THEN the returned string is the parenthesized infix expression "
            + "with spaces around '*'.")
    @Test
    void testBinaryMultiplication() {
        Expression b = multExpr(new Constant(24), new Constant(38));
        String expected = "(24 * 38)";
        String actual = b.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a BinaryOperation whose operands are "
            + "Variables, THEN the returned string includes both variable names with correct "
            + "spacing and parentheses.")
    @Test
    void testBinaryVariables() {
        Expression b = addExpr(new Variable('m'), new Variable('p'));
        String expected = "(m + p)";
        String actual = b.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a commutative BinaryOperation whose first "
            + "operand is a Variable and second is a Constant, THEN the returned string "
            + "includes the Variable first and the Constant second")
    @Test
    void testVarConst() {
        Expression b = addExpr(new Variable('a'), new Constant(5));
        String expected = "(a + 5)";
        String actual = b.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a BinaryOperation besides +,-,*, THEN "
            + "THEN the returned String still has the expected form.")
    @Test
    void testOtherBinary() {
        Expression e = new BinaryOperation(new Constant(5), new Constant(7), '^', (x,y) -> x ^ y);
        String expected = "(5 ^ 7)";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    /* *********************************************************************************
     * Complex tests for infixString() involving nested operations                     *
     ***********************************************************************************/

    @DisplayName("WHEN infixString() is called on a UnaryOperation whose argument is a "
            + "BinaryOperation, THEN the returned string prepends the symbol to the "
            + "fully parenthesized binary expression.")
    @Test
    void testUnaryOnBinary() {
        Expression ub = negExpr(multExpr(new Constant(17), new Constant(38)));
        String expected = "-(17 * 38)";
        String actual = ub.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a left-nested BinaryOperation tree, "
            + "THEN the returned string reflects the nesting with inner expressions fully "
            + "parenthesized.")
    @Test
    void testLeftNestedBinary() {
        Expression e = addExpr(addExpr(new Constant(1), new Constant(2)), new Constant(3));
        String expected = "((1 + 2) + 3)";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a right-nested BinaryOperation tree, "
            + "THEN the returned string reflects the nesting with inner expressions fully "
            + "parenthesized.")
    @Test
    void testRightNestedBinary() {
        Expression e = addExpr(new Constant(1), addExpr(new Constant(2), new Constant(3)));
        String expected = "(1 + (2 + 3))";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on an expression where multiplication is nested "
            + "inside addition, THEN the returned string preserves the AST structure with "
            + "parentheses around both operations.")
    @Test
    void testMultiplicationInsideAddition() {
        Expression e = addExpr(multExpr(new Constant(1), new Constant(2)), new Constant(3));
        String expected = "((1 * 2) + 3)";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on an expression where addition is nested inside "
            + "multiplication, THEN the returned string includes parentheses around the addition "
            + "subexpression.")
    @Test
    void testAdditionInsideMultiplication() {
        Expression e = multExpr(addExpr(new Constant(1), new Constant(2)), new Constant(3));
        String expected = "((1 + 2) * 3)";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a left-nested multiplication tree, "
            + "THEN the returned string reflects the nesting with inner expressions fully "
            + "parenthesized.")
    @Test
    void testLeftNestedMultiplication() {
        Expression e = multExpr(multExpr(new Constant(2), new Constant(3)), new Constant(4));
        String expected = "((2 * 3) * 4)";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a right-nested multiplication tree, "
            + "THEN the returned string reflects the nesting with inner expressions fully "
            + "parenthesized.")
    @Test
    void testRightNestedMultiplication() {
        Expression e = multExpr(new Constant(2), multExpr(new Constant(3), new Constant(4)));
        String expected = "(2 * (3 * 4))";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on an expression with nested subtraction "
            + "operations, THEN the returned string reflects the exact tree structure with "
            + "parentheses.")
    @Test
    void testNestedSubtraction() {
        Expression e = subtractExpr(new Constant(10),
                subtractExpr(new Constant(4), new Constant(1)));
        String expected = "(10 - (4 - 1))";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a BinaryOperation whose left operand is a "
            + "UnaryOperation, THEN the returned string includes the unary expression directly "
            + "as the left operand.")
    @Test
    void testUnaryOnLeftOperand() {
        Expression e = addExpr(negExpr(new Variable('x')), new Variable('y'));
        String expected = "(-x + y)";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a BinaryOperation whose right operand is a "
            + "UnaryOperation, THEN the returned string includes the unary expression directly "
            + "as the right operand.")
    @Test
    void testUnaryOnRightOperand() {
        Expression e = addExpr(new Variable('x'), negExpr(new Variable('y')));
        String expected = "(x + -y)";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a BinaryOperation representing subtraction "
            + "whose right operand is a UnaryOperation, THEN the returned string includes the "
            + "unary expression directly as the right operand.")
    @Test
    void testUnaryWithSubtraction() {
        Expression e = subtractExpr(new Variable('x'), negExpr(new Variable('y')));
        String expected = "(x - -y)";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a BinaryOperation whose operands are both "
            + "UnaryOperations, THEN the returned string includes both unary expressions with "
            + "correct formatting.")
    @Test
    void testUnaryOnBothOperands() {
        Expression e = addExpr(negExpr(new Variable('x')), negExpr(new Variable('y')));
        String expected = "(-x + -y)";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN infixString() is called on a deeply nested expression containing unary "
            + "and binary operations, THEN the returned string exactly reflects the full AST "
            + "structure with correct parentheses and spacing.")
    @Test
    void testBigNestedExpression() {
        Expression e = multExpr(
                addExpr(
                        negExpr(addExpr(new Variable('x'), new Constant(1))),
                        subtractExpr(
                                multExpr(new Constant(2), new Variable('y')),
                                negExpr(new Constant(3))
                        )
                ),
                subtractExpr(
                        negExpr(multExpr(new Variable('z'), new Constant(4))),
                        addExpr(new Constant(5), negExpr(new Variable('w')))
                )
        );
        String expected = "((-(x + 1) + ((2 * y) - -3)) * (-(z * 4) - (5 + -w)))";
        String actual = e.infixString();
        assertEquals(expected, actual);
    }
}


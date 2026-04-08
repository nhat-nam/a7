package cs2110;

import static cs2110.ExpressionParser.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cs2110.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Contains tests for the `ExpressionParser.parse()` method.
 */
public class ParserTest {

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

    /** Helper for subtraction */
    public Expression subExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '-', SUBTRACTION);
    }

    /** Helper for unary negation */
    public Expression negExpr(Expression operand) {
        return new UnaryOperation(operand, '-', NEGATION);
    }

    @DisplayName("WHEN an expression consists of a just a single-digit, THEN it is parsed "
            + "correctly to a Constant expression with the correct value.")
    @Test
    void testDigit() throws MalformedExpression {
        Expression expected = new Constant(0);
        Expression actual = ExpressionParser.parse("0");
        assertEquals(expected, actual);

        expected = new Constant(1);
        actual = ExpressionParser.parse("1");
        assertEquals(expected, actual);

        expected = new Constant(5);
        actual = ExpressionParser.parse("5");
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN we parse an expression containing one addition operation applied to two "
            + "single-digit operands, THEN a BinaryOperation expression is returned that contains "
            + "the correct symbol, operation BiFunction, and operands (in the correct order).")
    @Test
    void testAddConstants() throws MalformedExpression {
        Expression expected = addExpr(new Constant(2), new Constant(3));
        Expression actual = ExpressionParser.parse("2+3");
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN we parse an expression containing one multiplication operation applied to "
            + "two single-digit operands, THEN a BinaryOperation expression is returned that "
            + "contains the correct symbol, operation BiFunction, and operands (in the correct "
            + "order).")
    @Test
    void testMultiplyConstants() throws MalformedExpression {
        Expression expected = multExpr(new Constant(2), new Constant(3));
        Expression actual = ExpressionParser.parse("2*3");
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN we parse an expression containing addition followed by multiplication, THEN "
            + "the multiplication sub-expression appears as the right operand of the addition "
            + "operation.")
    @Test
    void testMultiplicationAfterAddition() throws MalformedExpression {
        Expression expected = addExpr(new Constant(1), multExpr(new Constant(2), new Constant(3)));
        Expression actual = ExpressionParser.parse("1+2*3");
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN we parse the expression \"2*(3+4)\", THEN the addition BinaryOperation "
            + "appears the right operand of the multiplication BinaryOperation.")
    @Test
    void testAddParensAfterMult() throws MalformedExpression {
        Expression expected = multExpr(new Constant(2), addExpr(new Constant(3), new Constant(4)));
        Expression actual = ExpressionParser.parse("2*(3+4)");
        assertEquals(expected, actual);
    }

    /*
     * TODO 3.2B-3.7B: Add additional tests to this method to improve coverage of the starter
     *  version features (correct handling of parentheses, addition, and multiplication)
     *  and provide full coverage of the features that you added (exception handling,
     *  variables, multi-digit constants, whitespace handling, subtraction, and negation).
     */

    //3.2
    @DisplayName("WHEN an expression is empty or essentially empty, THEN parse() throws MalformedExpression.")
    @Test
    void testEmptyExpressions() {
        //Truly empty
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse(""));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("   "));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse(null));

        // Essentially empty (only symbols, no constants/variables)
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("+-*(( ))"));
    }

    @DisplayName("WHEN an expression contains invalid characters, THEN parse() throws MalformedExpression.")
    @Test
    void testInvalidCharacters() {
        // Testing characters not in the allowed set {0-9, +, *, -, (, ),}
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("1&2"));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("3#4"));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("5!6"));
    }

    @DisplayName("WHEN parentheses are mismatched, THEN parse() throws MalformedExpression.")
    @Test
    void testMismatchedParentheses() {
        // Extra closing parenthesis
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("1+2)"));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("(1*2))"));

        // Extra opening parenthesis
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("(1+2"));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("((1*2)+3"));
    }

    @DisplayName("WHEN an operator is provided where an operand was expected, THEN parse() throws MalformedExpression")
    @Test
    void testExpectedOperand() {
        // Starting with a binary operator
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("+1"));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("*1"));

        // Consecutive operators
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("1++2"));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("1+*2"));

        // Operator immediately after an opening parenthesis
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("(+1)"));
    }

    @DisplayName("WHEN an expression ends with an operator, THEN parse() throws MalformedExpression.")
    @Test
    void testTrailingOperator() {
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("1+"));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("1*2+"));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("(1+2)*"));
    }

    @DisplayName("WHEN an operator is missing between operands, THEN parse() throws MalformedExpression.")
    @Test
    void testMissingOperator() {
        // Consecutive operands (without multi-digit support yet)
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("1 2"));

        // Operand immediately followed by an opening parenthesis
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("1(2+3)"));

        // Closing parenthesis immediately followed by an operand
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("(1+2)3"));
    }

    //3.3
    @DisplayName("WHEN an expression contains a variable, THEN it is parsed into a Variable AST node.")
    @Test
    void testVariable() throws MalformedExpression {
        Expression expected = new Variable('x');
        assertEquals(expected, ExpressionParser.parse("x"));

        expected = addExpr(new Variable('a'), new Constant(5));
        assertEquals(expected, ExpressionParser.parse("a+5"));
    }

    @DisplayName("WHEN an expression contains a multi-character variable name, THEN a MalformedExpression is thrown.")
    @Test
    void testMultiCharVariable() {
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("xy"));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("a+bc"));
    }

    //3.4
    @DisplayName("WHEN an expression contains multi-digit constants, THEN they are parsed as a single Constant node.")
    @Test
    void testMultiDigitConstants() throws MalformedExpression {
        Expression expected = new Constant(2110);
        assertEquals(expected, ExpressionParser.parse("2110"));

        expected = addExpr(new Constant(12), new Constant(345));
        assertEquals(expected, ExpressionParser.parse("12+345"));
    }

    //3.5
    @DisplayName("WHEN an expression contains general whitespace, THEN it is ignored.")
    @Test
    void testValidWhitespace() throws MalformedExpression {
        Expression expected = addExpr(new Constant(1), new Constant(2));
        assertEquals(expected, ExpressionParser.parse("1 + 2"));
        assertEquals(expected, ExpressionParser.parse("  1+2   "));

        expected = multExpr(new Constant(2), addExpr(new Constant(3), new Constant(4)));
        assertEquals(expected, ExpressionParser.parse("2 * ( 3 + 4 )"));
    }

    @DisplayName("WHEN an expression contains whitespace inside a number, THEN a MalformedExpression is thrown.")
    @Test
    void testInvalidInternalWhitespace() {
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("1 2"));
        assertThrows(MalformedExpression.class, () -> ExpressionParser.parse("12 3 + 4"));
    }

    //3.6
    @DisplayName("WHEN an expression contains subtraction, THEN it is parsed correctly with left-associativity.")
    @Test
    void testSubtraction() throws MalformedExpression {
        // Basic subtraction
        Expression expected = subExpr(new Constant(5), new Constant(3));
        assertEquals(expected, ExpressionParser.parse("5-3"));

        // Left-associativity
        expected = subExpr(subExpr(new Constant(10), new Constant(5)), new Constant(2));
        assertEquals(expected, ExpressionParser.parse("10-5-2"));

        // Subtraction and Addition have same precedence
        expected = subExpr(addExpr(new Constant(10), new Constant(5)), new Constant(2));
        assertEquals(expected, ExpressionParser.parse("10+5-2"));
        expected = addExpr(subExpr(new Constant(10), new Constant(5)), new Constant(2));
        assertEquals(expected, ExpressionParser.parse("10-5+2"));
    }

    //3.7
    @DisplayName("WHEN an expression contains unary negation, THEN it is parsed correctly with high precedence.")
    @Test
    void testUnaryNegation() throws MalformedExpression {
        // Basic negation
        Expression expected = negExpr(new Constant(5));
        assertEquals(expected, ExpressionParser.parse("-5"));

        // Negation of parentheses
        expected = negExpr(addExpr(new Constant(2), new Constant(3)));
        assertEquals(expected, ExpressionParser.parse("-(2+3)"));

        // High Precedence
        expected = multExpr(negExpr(new Constant(2)), new Constant(3));
        assertEquals(expected, ExpressionParser.parse("-2*3"));

        // Double negation (Right-associativity)
        expected = negExpr(negExpr(new Constant(5)));
        assertEquals(expected, ExpressionParser.parse("--5"));
    }

    @DisplayName("WHEN an expression contains both subtraction and negation, THEN they are disambiguated correctly.")
    @Test
    void testSubtractionAndNegationCombined() throws MalformedExpression {
        // 5 - -2 should be 5 - (-2)
        Expression expected = subExpr(new Constant(5), negExpr(new Constant(2)));
        assertEquals(expected, ExpressionParser.parse("5--2"));

        // -5 - 2 should be (-5) - 2
        expected = subExpr(negExpr(new Constant(5)), new Constant(2));
        assertEquals(expected, ExpressionParser.parse("-5-2"));
    }

    @DisplayName("WHEN a variable is negated, THEN a UnaryOperation wrapping the Variable is returned.")
    @Test
    void testNegatedVariable() throws MalformedExpression {
        // Negation of a single variable
        Expression expected = negExpr(new Variable('x'));
        assertEquals(expected, ExpressionParser.parse("-x"));

        // Negated variable combined with a binary op: a + -b
        expected = addExpr(new Variable('a'), negExpr(new Variable('b')));
        assertEquals(expected, ExpressionParser.parse("a+-b"));

        // Negated variable combined with multiplication: -x * y
        expected = multExpr(negExpr(new Variable('x')), new Variable('y'));
        assertEquals(expected, ExpressionParser.parse("-x*y"));
    }

    @DisplayName("WHEN a multi-digit constant is negated, THEN a UnaryOperation wrapping the whole Constant is returned.")
    @Test
    void testNegatedMultiDigitConstant() throws MalformedExpression {
        // Simple negated multi-digit constant
        Expression expected = negExpr(new Constant(12));
        assertEquals(expected, ExpressionParser.parse("-12"));

        // Larger value
        expected = negExpr(new Constant(2110));
        assertEquals(expected, ExpressionParser.parse("-2110"));

        // Negated multi-digit constant combined with addition
        expected = addExpr(new Constant(3), negExpr(new Constant(45)));
        assertEquals(expected, ExpressionParser.parse("3+-45"));
    }

    @DisplayName("WHEN negation is combined with parentheses and binary ops, THEN the AST is built correctly.")
    @Test
    void testNegationWithParentheses() throws MalformedExpression {
        // Negation inside parentheses: (-3+4) should be ((-3) + 4)
        Expression expected = addExpr(negExpr(new Constant(3)), new Constant(4));
        assertEquals(expected, ExpressionParser.parse("(-3+4)"));

        // Negation of a parenthesized sum following a binary op: 5 + -(3+2)
        expected = addExpr(new Constant(5), negExpr(addExpr(new Constant(3), new Constant(2))));
        assertEquals(expected, ExpressionParser.parse("5+-(3+2)"));

        // Negation of a parenthesized sub-expression following multiplication: 2 * -(a+b)
        expected = multExpr(new Constant(2), negExpr(addExpr(new Variable('a'), new Variable('b'))));
        assertEquals(expected, ExpressionParser.parse("2*-(a+b)"));
    }
}

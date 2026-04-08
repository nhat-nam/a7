package cs2110;

import static cs2110.ExpressionParser.*;
import cs2110.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTest {

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
     * Tests for evaluate()                                                            *
     ***********************************************************************************/

    @DisplayName("WHEN evaluate() is called on a Constant with value 0, "
            + "THEN the returned int is 0.")
    @Test
    void testEvaluateConstantZero() throws UnassignedVariable {
        Expression e = new Constant(0);
        int expected = 0;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a Constant with a positive single-digit value, "
            + "THEN the returned int is that value.")
    @Test
    void testEvaluateConstantSingleDigit() throws UnassignedVariable {
        Expression e = new Constant(7);
        int expected = 7;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a Constant with a multi-digit value, "
            + "THEN the returned int is that value.")
    @Test
    void testEvaluateConstantMultiDigit() throws UnassignedVariable {
        Expression e = new Constant(2110);
        int expected = 2110;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a Variable, "
            + "THEN an UnassignedVariable exception is thrown.")
    @Test
    void testEvaluateVariableThrows() {
        Expression e = new Variable('x');
        assertThrows(UnassignedVariable.class, () -> e.evaluate());
    }

    @DisplayName("WHEN evaluate() is called on a BinaryOperation representing addition of two "
            + "Constants, THEN the returned int is the sum of their values.")
    @Test
    void testEvaluateBinaryAddition() throws UnassignedVariable {
        Expression e = addExpr(new Constant(2), new Constant(3));
        int expected = 5;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a BinaryOperation representing subtraction of two "
            + "Constants, THEN the returned int is the difference of their values.")
    @Test
    void testEvaluateBinarySubtraction() throws UnassignedVariable {
        Expression e = subtractExpr(new Constant(10), new Constant(6));
        int expected = 4;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a BinaryOperation representing multiplication of "
            + "two Constants, THEN the returned int is the product of their values.")
    @Test
    void testEvaluateBinaryMultiplication() throws UnassignedVariable {
        Expression e = multExpr(new Constant(6), new Constant(7));
        int expected = 42;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a BinaryOperation whose subtraction yields a "
            + "negative result, THEN the returned int is that negative value.")
    @Test
    void testEvaluateBinarySubtractionNegativeResult() throws UnassignedVariable {
        Expression e = subtractExpr(new Constant(4), multExpr(new Constant(2), new Constant(6)));
        int expected = -8;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a BinaryOperation combining addition and "
            + "multiplication, THEN the returned int reflects the correct combined value.")
    @Test
    void testEvaluateCombinedBinary() throws UnassignedVariable {
        Expression e = multExpr(addExpr(new Constant(2), new Constant(3)), new Constant(4));
        int expected = 20;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a deeply-nested purely-Constant expression, "
            + "THEN the returned int is the correct evaluated value of the whole tree.")
    @Test
    void testEvaluateNestedConstantExpression() throws UnassignedVariable {
        Expression e = subtractExpr(
                multExpr(addExpr(new Constant(1), new Constant(2)),
                        addExpr(new Constant(3), new Constant(4))),
                new Constant(5));
        int expected = 16;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a UnaryOperation negating a positive Constant, "
            + "THEN the returned int is the negated value.")
    @Test
    void testEvaluateUnaryNegationConstant() throws UnassignedVariable {
        Expression e = negExpr(new Constant(5));
        int expected = -5;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a UnaryOperation negating a sub-expression, "
            + "THEN the returned int is the negated value of that sub-expression.")
    @Test
    void testEvaluateUnaryNegationSubExpression() throws UnassignedVariable {
        Expression e = negExpr(addExpr(new Constant(2), new Constant(3)));
        int expected = -5;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a double-negation of a Constant, "
            + "THEN the returned int is the original positive value.")
    @Test
    void testEvaluateDoubleNegation() throws UnassignedVariable {
        Expression e = negExpr(negExpr(new Constant(5)));
        int expected = 5;
        int actual = e.evaluate();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN evaluate() is called on a BinaryOperation with a Variable in its left "
            + "subtree, THEN an UnassignedVariable exception is thrown.")
    @Test
    void testEvaluateBinaryVariableInLeft() {
        Expression e = addExpr(new Variable('x'), new Constant(3));
        assertThrows(UnassignedVariable.class, () -> e.evaluate());
    }

    @DisplayName("WHEN evaluate() is called on a BinaryOperation with a Variable in its right "
            + "subtree, THEN an UnassignedVariable exception is thrown.")
    @Test
    void testEvaluateBinaryVariableInRight() {
        Expression e = multExpr(new Constant(3), new Variable('y'));
        assertThrows(UnassignedVariable.class, () -> e.evaluate());
    }

    @DisplayName("WHEN evaluate() is called on a BinaryOperation with a Variable deeply nested "
            + "inside one of its subtrees, THEN an UnassignedVariable exception is thrown.")
    @Test
    void testEvaluateBinaryVariableDeeplyNested() {
        Expression e = multExpr(addExpr(new Constant(1), new Constant(2)),
                addExpr(new Constant(3), new Variable('x')));
        assertThrows(UnassignedVariable.class, () -> e.evaluate());
    }

    @DisplayName("WHEN evaluate() is called on a UnaryOperation whose operand is a Variable, "
            + "THEN an UnassignedVariable exception is thrown.")
    @Test
    void testEvaluateUnaryVariable() {
        Expression e = negExpr(new Variable('x'));
        assertThrows(UnassignedVariable.class, () -> e.evaluate());
    }

    @DisplayName("WHEN evaluate() is called on a UnaryOperation wrapping a sub-expression that "
            + "contains a Variable, THEN an UnassignedVariable exception is thrown.")
    @Test
    void testEvaluateUnaryWithVariableInSubExpression() {
        Expression e = negExpr(addExpr(new Constant(2), new Variable('x')));
        assertThrows(UnassignedVariable.class, () -> e.evaluate());
    }

    /* *********************************************************************************
     * Tests for substitute()                                                          *
     ***********************************************************************************/

    @DisplayName("WHEN substitute() is called on a Constant, "
            + "THEN the Constant is returned unchanged regardless of the variable name.")
    @Test
    void testSubstituteConstant() {
        Expression e = new Constant(42);
        Expression expected = new Constant(42);
        Expression actual = e.substitute('x', new Constant(7));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a Variable whose name matches, "
            + "THEN the replacement expression is returned.")
    @Test
    void testSubstituteVariableMatching() {
        Expression e = new Variable('x');
        Expression expected = new Constant(9);
        Expression actual = e.substitute('x', new Constant(9));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a Variable whose name does not match, "
            + "THEN the Variable is returned unchanged.")
    @Test
    void testSubstituteVariableNonMatching() {
        Expression e = new Variable('y');
        Expression expected = new Variable('y');
        Expression actual = e.substitute('x', new Constant(9));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a Variable and the replacement is a non-Constant "
            + "Expression, THEN the full replacement subtree is returned.")
    @Test
    void testSubstituteVariableWithSubtree() {
        Expression e = new Variable('x');
        Expression expected = addExpr(new Constant(1), new Constant(2));
        Expression actual = e.substitute('x', addExpr(new Constant(1), new Constant(2)));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a BinaryOperation whose child contains the "
            + "matching Variable, THEN a new BinaryOperation with the replacement in place is "
            + "returned.")
    @Test
    void testSubstituteBinaryOperationRecurse() {
        Expression e = addExpr(new Variable('x'), new Constant(3));
        Expression expected = addExpr(new Constant(5), new Constant(3));
        Expression actual = e.substitute('x', new Constant(5));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a BinaryOperation with the matching Variable on "
            + "both sides, THEN both occurrences are replaced.")
    @Test
    void testSubstituteBinaryBothSides() {
        Expression e = multExpr(new Variable('x'), new Variable('x'));
        Expression expected = multExpr(new Constant(4), new Constant(4));
        Expression actual = e.substitute('x', new Constant(4));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a BinaryOperation containing multiple distinct "
            + "Variables, THEN only the targeted Variable is replaced and other Variables remain.")
    @Test
    void testSubstituteBinaryOnlyTarget() {
        Expression e = addExpr(new Variable('x'), new Variable('y'));
        Expression expected = addExpr(new Constant(5), new Variable('y'));
        Expression actual = e.substitute('x', new Constant(5));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a BinaryOperation with a Variable replacement, "
            + "THEN the replacement Variable appears in the result tree.")
    @Test
    void testSubstituteVariableWithVariable() {
        Expression e = addExpr(new Variable('x'), new Constant(3));
        Expression expected = addExpr(new Variable('y'), new Constant(3));
        Expression actual = e.substitute('x', new Variable('y'));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a BinaryOperation with a BinaryOperation "
            + "replacement, THEN the full replacement subtree is inserted at each variable site.")
    @Test
    void testSubstituteBinaryWithSubtreeReplacement() {
        Expression e = addExpr(new Variable('x'), new Constant(1));
        Expression expected = addExpr(multExpr(new Variable('a'), new Variable('b')),
                new Constant(1));
        Expression actual = e.substitute('x', multExpr(new Variable('a'), new Variable('b')));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a UnaryOperation containing the target Variable, "
            + "THEN the operand is recursively substituted.")
    @Test
    void testSubstituteUnaryOperation() {
        Expression e = negExpr(new Variable('x'));
        Expression expected = negExpr(new Constant(5));
        Expression actual = e.substitute('x', new Constant(5));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a UnaryOperation whose operand is a deeper tree, "
            + "THEN substitution descends into that operand.")
    @Test
    void testSubstituteUnaryOperationDeep() {
        Expression e = negExpr(addExpr(new Variable('x'), new Constant(2)));
        Expression expected = negExpr(addExpr(new Constant(7), new Constant(2)));
        Expression actual = e.substitute('x', new Constant(7));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called with a Variable name that does not appear in the "
            + "expression, THEN the result equals the original expression.")
    @Test
    void testSubstituteNoOccurrence() {
        Expression e = addExpr(new Variable('x'), new Variable('y'));
        Expression expected = addExpr(new Variable('x'), new Variable('y'));
        Expression actual = e.substitute('z', new Constant(9));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on a deeply-nested expression with multiple "
            + "occurrences of the target Variable, THEN every matching occurrence is replaced.")
    @Test
    void testSubstituteMultipleOccurrencesDeep() {
        Expression e = multExpr(addExpr(new Variable('x'), new Variable('y')),
                subtractExpr(new Variable('x'), new Constant(3)));
        Expression expected = multExpr(addExpr(new Constant(2), new Variable('y')),
                subtractExpr(new Constant(2), new Constant(3)));
        Expression actual = e.substitute('x', new Constant(2));
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN substitute() is called on an expression combining UnaryOperation and "
            + "BinaryOperation, THEN substitution descends through both node kinds.")
    @Test
    void testSubstituteMixedUnaryBinary() {
        Expression e = multExpr(negExpr(addExpr(new Variable('x'), new Constant(1))),
                new Variable('x'));
        Expression expected = multExpr(negExpr(addExpr(new Constant(4), new Constant(1))),
                new Constant(4));
        Expression actual = e.substitute('x', new Constant(4));
        assertEquals(expected, actual);
    }

    /* *********************************************************************************
     * Tests for simplify()                                                            *
     ***********************************************************************************/

    @DisplayName("WHEN simplify() is called on a Constant, "
            + "THEN the same Constant is returned.")
    @Test
    void testSimplifyConstant() {
        Expression e = new Constant(5);
        Expression expected = new Constant(5);
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a Variable, "
            + "THEN the same Variable is returned.")
    @Test
    void testSimplifyVariable() {
        Expression e = new Variable('x');
        Expression expected = new Variable('x');
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a BinaryOperation with addition over two Constant "
            + "children, THEN a single Constant holding the sum is returned.")
    @Test
    void testSimplifyBinaryAddConstants() {
        Expression e = addExpr(new Constant(2), new Constant(3));
        Expression expected = new Constant(5);
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a BinaryOperation with subtraction over two "
            + "Constant children, THEN a single Constant holding the difference is returned.")
    @Test
    void testSimplifyBinarySubConstants() {
        Expression e = subtractExpr(new Constant(10), new Constant(6));
        Expression expected = new Constant(4);
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a BinaryOperation with multiplication over two "
            + "Constant children, THEN a single Constant holding the product is returned.")
    @Test
    void testSimplifyBinaryMulConstants() {
        Expression e = multExpr(new Constant(3), new Constant(4));
        Expression expected = new Constant(12);
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a UnaryOperation with a Constant operand, "
            + "THEN a single Constant holding the negated value is returned.")
    @Test
    void testSimplifyUnaryConstant() {
        Expression e = negExpr(new Constant(5));
        Expression expected = new Constant(-5);
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a nested all-Constant expression, "
            + "THEN it folds to a single Constant with the correct value.")
    @Test
    void testSimplifyNestedAllConstants() {
        Expression e = multExpr(addExpr(new Constant(2), new Constant(3)), new Constant(4));
        Expression expected = new Constant(20);
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a nested all-Constant expression containing "
            + "subtraction and multiplication, THEN it folds to a single Constant.")
    @Test
    void testSimplifyNestedMixedConstants() {
        Expression e = subtractExpr(new Constant(4), multExpr(new Constant(2), new Constant(6)));
        Expression expected = new Constant(-8);
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a UnaryOperation wrapping a fully constant-foldable "
            + "subtree, THEN it folds all the way to a single Constant.")
    @Test
    void testSimplifyUnaryFoldedSubtree() {
        Expression e = negExpr(addExpr(new Constant(2), new Constant(3)));
        Expression expected = new Constant(-5);
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a BinaryOperation whose right side folds but whose "
            + "left side contains a Variable, THEN only the right side is folded.")
    @Test
    void testSimplifyPartialFoldingRight() {
        Expression e = addExpr(new Variable('x'), addExpr(new Constant(2), new Constant(3)));
        Expression expected = addExpr(new Variable('x'), new Constant(5));
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a BinaryOperation whose left side folds but whose "
            + "right side contains a Variable, THEN only the left side is folded.")
    @Test
    void testSimplifyPartialFoldingLeft() {
        Expression e = addExpr(multExpr(new Constant(2), new Constant(3)), new Variable('y'));
        Expression expected = addExpr(new Constant(6), new Variable('y'));
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on an expression that cannot be folded at any node, "
            + "THEN the returned expression equals the original.")
    @Test
    void testSimplifyNoFoldPossible() {
        Expression e = addExpr(new Variable('x'), new Variable('y'));
        Expression expected = addExpr(new Variable('x'), new Variable('y'));
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on a UnaryOperation whose operand is a Variable, "
            + "THEN the UnaryOperation is returned unchanged.")
    @Test
    void testSimplifyUnaryVariableUnchanged() {
        Expression e = negExpr(new Variable('x'));
        Expression expected = negExpr(new Variable('x'));
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on the expression \"x * (3 + 7) + (4 - (2 * 6))\", "
            + "THEN the returned tree corresponds to \"(x * 10) + -8\" with constant folding "
            + "applied.")
    @Test
    void testSimplifyPdfExample() {
        Expression e = addExpr(
                multExpr(new Variable('x'), addExpr(new Constant(3), new Constant(7))),
                subtractExpr(new Constant(4), multExpr(new Constant(2), new Constant(6))));
        Expression expected = addExpr(
                multExpr(new Variable('x'), new Constant(10)),
                new Constant(-8));
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on an expression whose subtrees contain a mix of "
            + "foldable and non-foldable parts, THEN only the foldable parts are reduced.")
    @Test
    void testSimplifyMixedDeepPartial() {
        Expression e = addExpr(
                multExpr(addExpr(new Constant(1), new Constant(2)), new Variable('x')),
                subtractExpr(new Variable('y'), multExpr(new Constant(3), new Constant(4))));
        Expression expected = addExpr(
                multExpr(new Constant(3), new Variable('x')),
                subtractExpr(new Variable('y'), new Constant(12)));
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN simplify() is called on an expression with a UnaryOperation whose operand "
            + "is non-foldable, THEN the UnaryOperation wraps the simplified operand and is not "
            + "collapsed to a Constant.")
    @Test
    void testSimplifyUnaryOverNonConstant() {
        Expression e = negExpr(addExpr(new Variable('x'), addExpr(new Constant(1), new Constant(2))));
        Expression expected = negExpr(addExpr(new Variable('x'), new Constant(3)));
        Expression actual = e.simplify();
        assertEquals(expected, actual);
    }
}

package cs2110;

import cs2110.ast.Constant;
import cs2110.ast.Expression;
import cs2110.ast.UnassignedVariable;
import cs2110.ast.Variable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Contains tests for the `evaluate()`, `substitute()`, and `simplify()` methods
 * for all the `Expression` subclasses.
 */
public class ExpressionTest {
    /*
     * TODO 4.1E-4.3E: Add unit tests to cover the `evaluate()`, `substitute()`, and `simplify()`
     *  definitions in the `Constant`, `Variable`, `BinaryOperation`, and `UnaryOperation`
     *  classes.
     */

    //4.1
    @DisplayName("WHEN evaluate() is called on an expression with only constants, THEN it returns the correct integer.")
    @Test
    void testEvaluateConstantsAndOperations() throws MalformedExpression, UnassignedVariable {
        // Base case: Constant
        assertEquals(5, ExpressionParser.parse("5").evaluate());

        // Simple operations
        assertEquals(5, ExpressionParser.parse("2+3").evaluate());
        assertEquals(6, ExpressionParser.parse("2*3").evaluate());
        assertEquals(2, ExpressionParser.parse("5-3").evaluate());
        assertEquals(-5, ExpressionParser.parse("-5").evaluate());

        // Complex operations
        assertEquals(14, ExpressionParser.parse("2*(3+4)").evaluate());
        assertEquals(1, ExpressionParser.parse("10-5-4").evaluate());
        assertEquals(-6, ExpressionParser.parse("---6").evaluate());
    }

    //4.2
    @DisplayName("WHEN substitute() is called, THEN occurrences of the target variable are replaced.")
    @Test
    void testSubstituteMatchingVariable() throws MalformedExpression {
        Expression replacement = new Constant(99);

        // Base case
        Expression expr = ExpressionParser.parse("x");
        assertEquals(replacement, expr.substitute('x', replacement));

        // Single replacement in operation
        expr = ExpressionParser.parse("x+5");
        Expression expected = ExpressionParser.parse("99+5");
        assertEquals(expected, expr.substitute('x', replacement));

        // Multiple replacements
        expr = ExpressionParser.parse("x*x");
        expected = ExpressionParser.parse("99*99");
        assertEquals(expected, expr.substitute('x', replacement));

        // Deep replacements
        expr = ExpressionParser.parse("2*(x-y)");
        expected = ExpressionParser.parse("2*(99-y)");
        assertEquals(expected, expr.substitute('x', replacement));
    }

    @DisplayName("WHEN substitute() is called, THEN constants and mismatched variables are unaffected.")
    @Test
    void testSubstituteNoMatch() throws MalformedExpression {
        Expression replacement = new Constant(99);

        // Constant unaffected
        Expression expr = ExpressionParser.parse("5");
        assertEquals(expr, expr.substitute('x', replacement));

        // Different variables unaffected
        expr = ExpressionParser.parse("y");
        assertEquals(expr, expr.substitute('x', replacement));

        // Operations without target variable unaffected
        expr = ExpressionParser.parse("a+b");
        assertEquals(expr, expr.substitute('x', replacement));
    }

    //4.3
    @DisplayName("WHEN simplify() is called on an expression with only constants, THEN it folds into a single Constant.")
    @Test
    void testSimplifyFullyFoldable() throws MalformedExpression {
        // Simple operations
        Expression expr = ExpressionParser.parse("2+3");
        Expression expected = new Constant(5);
        assertEquals(expected, expr.simplify());

        expr = ExpressionParser.parse("10-4");
        expected = new Constant(6);
        assertEquals(expected, expr.simplify());

        expr = ExpressionParser.parse("-8");
        expected = new Constant(-8);
        assertEquals(expected, expr.simplify());

        // Deep operations
        expr = ExpressionParser.parse("2*(3+4)");
        expected = new Constant(14);
        assertEquals(expected, expr.simplify());
    }

    @DisplayName("WHEN simplify() is called on an expression with variables, THEN it folds only the constant subtrees.")
    @Test
    void testSimplifyPartiallyFoldable() throws MalformedExpression {
        // Base cases
        Expression expr = new Constant(5);
        assertEquals(expr, expr.simplify());
        expr = new Variable('x');
        assertEquals(expr, expr.simplify());

        // Fold right subtree
        expr = ExpressionParser.parse("x+(2*3)");
        Expression expected = ExpressionParser.parse("x+6");
        assertEquals(expected, expr.simplify());

        // Fold left subtree
        expr = ExpressionParser.parse("(10-5)*y");
        expected = ExpressionParser.parse("5*y");
        assertEquals(expected, expr.simplify());

        // Fold multiple independent subtrees
        expr = ExpressionParser.parse("(1+2)*x+(4-1)");
        expected = ExpressionParser.parse("3*x+3");
        assertEquals(expected, expr.simplify());

        // Unfoldable tree remains unchanged structurally
        expr = ExpressionParser.parse("x+y");
        assertEquals(expr, expr.simplify());
    }
}

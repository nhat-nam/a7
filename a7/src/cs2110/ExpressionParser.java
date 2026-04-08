package cs2110;

import cs2110.ast.*;
import cs2110.lib.LinkedStack;
import cs2110.lib.Stack;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Provides a `parse()` utility method to convert expression Strings into ASTs of type `Expression`.
 */
public class ExpressionParser {

    /** Models the addition operation on integers. */
    public static BiFunction<Integer, Integer, Integer> ADDITION = (x,y) -> x + y;

    /** Models the subtraction operation on integers. */
    public static BiFunction<Integer, Integer, Integer> SUBTRACTION = (x,y) -> x - y;

    /** Models the multiplication operation on integers. */
    public static BiFunction<Integer, Integer, Integer> MULTIPLICATION = (x,y) -> x * y;

    /** Models the negation operation on integers. */
    public static Function<Integer, Integer> NEGATION = x -> -1 * x;

    /**
     * Constructs and returns an AST corresponding to the given expression String.
     * Throws `MalformedExpression` to indicate that the expression String was not well-formed.
     * A well-formed expression String will contain only digits, lowercase Latin characters,
     * whitespace characters, and the symbols '(', ')', '+', '-', and '*' in an order that gives
     * a valid mathematical expression.
     */
    public static Expression parse(String expr) throws MalformedExpression {

        // Empty string check
        if (expr == null || expr.isEmpty()) {
            throw new MalformedExpression("Empty expression");
        }

        Stack<Expression> operands = new LinkedStack<>();
        Stack<Character> operators = new LinkedStack<>(); // invariant: contains only '(', '+', '-', '*', and '~'
        boolean expectingOperator = false; // in infix notation, the first operand comes before an operator
        boolean foundAnyOperand = false; // expression must have digit or variable or else essentially empty
        boolean lastOperandWasVariable = false; // tracks whether the most recently pushed operand was a Variable

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            // ignore general whitespace
            if (Character.isWhitespace(c)) {
                continue;
            }

            //invalid characters check
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') ||
                    c == '+' || c == '*' || c == '(' || c == ')' || c == '-')) {
                throw new MalformedExpression("Invalid character: '" + c + "'");
            }

            if (c == '(') { // when we see '(', we simply push onto operators stack
                if (expectingOperator) {
                    throw new MalformedExpression("Expected operator, got '('");
                }
                operators.push('(');
            } else if (c == ')') {
                if (!expectingOperator) {
                    throw new MalformedExpression("Expected operand, got ')'");
                }
                // process operators until we find the matching '(' on the operators stack
                while (!operators.isEmpty() && operators.peek() != '(') {
                    oneStepSimplify(operands, operators);
                }
                if (operators.isEmpty()) {
                    throw new MalformedExpression("Mismatched parentheses, extra ')'");
                }
                operators.pop(); // remove '('
                lastOperandWasVariable = false; // a parenthesized expression is not a single variable
            } else if (c == '-' && !expectingOperator) {
                //push special ~ to express unary negation for oneStepSimplify()
                operators.push('~');
            } else if (c == '+' || c == '*' || c == '-') {
                if (!expectingOperator) {
                    throw new MalformedExpression("Expected operand, got '" + c + "'");
                }
                // Process earlier operations based on precedence (3.6: Subtraction = Addition)
                while (!operators.isEmpty() && operators.peek() != '(') {
                    char top = operators.peek();
                    boolean shouldPop = false;

                    if (c == '*') {
                        // '*' pops '*' and unary '~'
                        if (top == '*' || top == '~') shouldPop = true;
                    } else { // c == '+' or c == '-'
                        // '+' and '-' pop '*', '+', '-', and unary '~'
                        if (top == '*' || top == '+' || top == '-' || top == '~') shouldPop = true;
                    }

                    if (shouldPop) {
                        oneStepSimplify(operands, operators);
                    } else {
                        break;
                    }
                }
                operators.push(c);
                expectingOperator = false;
            } else if (c >= '0' && c <= '9') {
                if (expectingOperator) {
                    throw new MalformedExpression("Expected operator, got operand: '"+c+"'");
                }

                //processing multi-digit constants
                int value = 0;
                while (i < expr.length() && Character.isDigit(expr.charAt(i))) {
                    value = value * 10 + (expr.charAt(i) - '0');
                    i++;
                }

                //catch internal whitespace
                if (i < expr.length() && Character.isWhitespace(expr.charAt(i))) {
                    int tempI = i;
                    while (tempI < expr.length() && Character.isWhitespace(expr.charAt(tempI))) {
                        tempI++;
                    }
                    if (tempI < expr.length() && Character.isDigit(expr.charAt(tempI))) {
                        throw new MalformedExpression("Internal whitespace inside a multi-digit constant not allowed");
                    }
                }

                i--; // backtrack one character since the for-loop will increment it again

                operands.push(new Constant(value));
                foundAnyOperand = true;
                expectingOperator = true;
                lastOperandWasVariable = false;
            } else if (c >= 'a' && c <= 'z') {
                if (expectingOperator) {
                    // distinguish multi-character variable names from a simple missing operator
                    if (lastOperandWasVariable) {
                        throw new MalformedExpression(
                                "Multi-character variable names not allowed: '" + c + "'");
                    } else {
                        throw new MalformedExpression(
                                "Expected operator, got variable '" + c + "'");
                    }
                }
                operands.push(new Variable(c));
                foundAnyOperand = true;
                expectingOperator = true;
                lastOperandWasVariable = true;
            }
        }

        if (!foundAnyOperand) {
            throw new MalformedExpression("Essentially empty expression: no constant literals or variables");
        }
        if (!expectingOperator) {
            throw new MalformedExpression("Expression ends with operator");
        }

        while (!operators.isEmpty()) {
            if (operators.peek() == '(') {
                throw new MalformedExpression("Mismatched parentheses: extra '('");
            }
            oneStepSimplify(operands, operators);
        }

        return operands.pop();
    }

    /**
     * Helper method that partially simplifies the expression by `pop()`ping one operator from the
     * `operators` stack and `push()`ing the resulting Expression onto the `operands` stack. For
     * the binary operators '+', '-', and '*', this `pop()`s two operand Expressions from the
     * `operands` stack and `push()`es a new BinaryOperation representing the application of this
     * operator on these operands. For the unary negation marker '~', this `pop()`s one operand
     * Expression and `push()`es a new UnaryOperation representing its negation. Requires that
     * `operators.peek()` is one of '+', '-', '*', or '~', that `operands` has at least two
     * elements when the top operator is binary, and at least one element when the top operator
     * is '~'.
     */
    private static void oneStepSimplify(Stack<Expression> operands, Stack<Character> operators) {
        char op = operators.pop();

        if (op == '~') {
            // 3.7: Unary Negation mapped to our internal '~' symbol
            Expression o1 = operands.pop();
            operands.push(new UnaryOperation(o1, '-', NEGATION));
        } else {
            // Standard Binary Operations
            Expression o2 = operands.pop();
            Expression o1 = operands.pop();

            if (op == '+') {
                operands.push(new BinaryOperation(o1, o2, '+', ADDITION));
            } else if (op == '-') {
                // 3.6: Binary Subtraction
                operands.push(new BinaryOperation(o1, o2, '-', SUBTRACTION));
            } else if (op == '*') {
                operands.push(new BinaryOperation(o1, o2, '*', MULTIPLICATION));
            }
        }
    }

    /**
     * Repeatedly queries the user for an expression String and outputs a String representation of
     * the AST for that expression.
     */
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            while (true) { // repeat indefinitely
                System.out.print("Enter an expression, or enter \"q\" to quit: ");
                String expr = in.nextLine();
                if (expr.equals("q")) {
                    break; // exit loop
                }
                try {
                    System.out.println("= " + System.lineSeparator() + parse(expr).treeString());
                } catch (MalformedExpression e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }
}

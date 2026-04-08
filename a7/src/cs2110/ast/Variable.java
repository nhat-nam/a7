package cs2110.ast;

import cs2110.ExpressionParser;

/**
 * An expression representing a variable with a given name.
 */
public record Variable(char name) implements Expression {

    /** Constructs a variable with the given `name`. Requires that `name` is a lower-case
     *  Latin character.
     */
    public Variable {
        assert 'a' <= name && name <= 'z';
    }
    /* The above is special syntax that we use to insert defensive programming assertions into a
     * record class constructor. */

    /**
     * Returns the infix String representation of this variable which is just its name
     */
    @Override
    public String infixString() {
        return String.valueOf(this.name);
    }

    /**
     * Always throws UnassignedVariable because a Variable has no fixed int value; it must first
     * be substituted before it can be evaluated.
     */
    @Override
    public int evaluate() throws UnassignedVariable {
        throw new UnassignedVariable();
    }

    /**
     * Returns `expr` if this Variable's name equals `variable`, otherwise returns this Variable
     * unchanged.
     */
    @Override
    public Expression substitute(char variable, Expression expr) {
        if (this.name == variable) {
            return expr;
        }
        return this;
    }

    /**
     * Returns this Variable unchanged, since a Variable cannot be simplified further without
     * knowing its value.
     */
    @Override
    public Expression simplify() {
        return this;
    }

    /**
     * Returns itself, as variables cannot be expanded.
     */
    @Override
    public Expression expand() {
        return this;
    }

    /**
     * Returns a fully-expanded Expression mathematically equivalent to `this * other`. Since a
     * Variable is not a sum or difference, this delegates to `other.distributeFromLeft(this)` so
     * that any distribution can occur on the right operand.
     */
    @Override
    public Expression multTimes(Expression other) {
        return other.distributeFromLeft(this);
    }

    /**
     * Returns a new BinaryOperation representing `other * this`. Since a Variable is not a sum
     * or difference, no distribution occurs.
     */
    @Override
    public Expression distributeFromLeft(Expression other) {
        return new BinaryOperation(other, this, '*', ExpressionParser.MULTIPLICATION);
    }

    @Override
    public String[] treeStringLinesRecursive() {
        return new String[]{Character.toString(name)};
    }
}

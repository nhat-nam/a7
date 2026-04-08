package cs2110.ast;

import cs2110.ExpressionParser;

/**
 * An expression representing a fixed int value.
 */
public record Constant(int value) implements Expression {
    /**
     * Returns the infix String representation of this Constant which is simply the value
     * as a String
     */
    @Override
    public String infixString() {
        return String.valueOf(this.value);
    }

    /**
     * Returns the int value stored in this Constant. Never throws UnassignedVariable since a
     * Constant contains no Variables.
     */
    @Override
    public int evaluate() {
        return this.value;
    }

    /**
     * Returns this Constant unchanged, since a Constant contains no Variables to substitute.
     */
    @Override
    public Expression substitute(char variable, Expression expr) {
        return this;
    }

    /**
     * Returns this Constant unchanged, since a Constant is already in its simplest form.
     */
    @Override
    public Expression simplify() {
        return this;
    }

    /**
     * Returns itself; a constant cannot be expanded.
     */
    @Override
    public Expression expand() {
        return this;
    }

    /**
     * Returns a fully-expanded Expression mathematically equivalent to `this * other`. Since a
     * Constant is not a sum or difference, this delegates to `other.distributeFromLeft(this)` so
     * that any distribution can occur on the right operand.
     */
    @Override
    public Expression multTimes(Expression other) {
        return other.distributeFromLeft(this);
    }

    /**
     * Returns a new BinaryOperation representing `other * this`. Since a Constant is not a sum
     * or difference, no distribution occurs.
     */
    @Override
    public Expression distributeFromLeft(Expression other) {
        return new BinaryOperation(other, this, '*', ExpressionParser.MULTIPLICATION);
    }

    @Override
    public String[] treeStringLinesRecursive() {
        return new String[]{Integer.toString(value)};
    }
}



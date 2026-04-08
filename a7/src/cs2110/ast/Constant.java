package cs2110.ast;

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
     * Returns the integer value of this constant expression.
     */
    @Override
    public int evaluate() {
        return this.value;
    }

    /**
     * A constant is not a variable and therefore cannot be substituted.
     * When called, just returns the Constant itself.
     */
    @Override
    public Expression substitute(char variable, Expression expr) {
        return this;
    }

    /**
     * A Constant can be simplified no further and will just return itself
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

    @Override
    public String[] treeStringLinesRecursive() {
        return new String[]{Integer.toString(value)};
    }
}



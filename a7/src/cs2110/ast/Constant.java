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

    @Override
    public int evaluate() {
        // TODO 4.1A: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression substitute(char variable, Expression expr) {
        // TODO 4.2A: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression simplify() {
        // TODO 4.3A: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
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



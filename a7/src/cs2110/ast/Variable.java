package cs2110.ast;

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

    @Override
    public int evaluate() throws UnassignedVariable {
        // TODO 4.1B: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression substitute(char variable, Expression expr) {
        // TODO 4.2B: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression simplify() {
        // TODO 4.3B: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        throw new UnsupportedOperationException();
    }

    /**
     * Returns itself, as variables cannot be expanded.
     */
    @Override
    public Expression expand() {
        return this;
    }

    @Override
    public String[] treeStringLinesRecursive() {
        return new String[]{Character.toString(name)};
    }
}

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

    /**
     * Variables do not have an inherent numerical value, so they cannot be directly evaluated.
     * This method will throw an UnassignedVariable exception.
     */
    @Override
    public int evaluate() throws UnassignedVariable {
        throw new UnassignedVariable();
    }

    /**
     * If the target variable matches this Variable, then return expr. Otherwise, return the Variable itself.
     */
    @Override
    public Expression substitute(char variable, Expression expr) {
        if(variable==this.name){
            return expr;
        }
        return this;
    }

    /**
     * A Variable can be simplified no further and will just return itself
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

    @Override
    public String[] treeStringLinesRecursive() {
        return new String[]{Character.toString(name)};
    }
}

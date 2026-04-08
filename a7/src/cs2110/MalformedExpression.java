package cs2110;

/**
 * Indicates that an expression String supplied by client code is not
 * well-formed.
 */
public class MalformedExpression extends Exception {

    public MalformedExpression(String message) {
        super(message);
    }
}

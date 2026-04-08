package cs2110.ast;

/**
 *  An exception that is thrown when calling `Expression.evaluate()` to indicate that
 *  the expression contains a Variable (meaning the expression does not evaluate to a
 *  particular `int`).
 */
public class UnassignedVariable extends Exception {

}

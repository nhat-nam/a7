package cs2110.lib;

/**
 * An ordered collection of elements of type `T` that follows the LIFO order condition.
 */
public interface Stack<T> {

    /**
     * Adds `elem` at the "top" of this stack. Requires that `elem != null`.
     */
    void push(T elem);

    /**
     * Removes and returns the "top" element of this stack. Requires `!this.isEmpty()`.
     */
    T pop();

    /**
     * Returns the "top" element of this stack without removing it. Requires `!this.isEmpty()`.
     */
    T peek();

    /**
     * Returns `true` if there are currently no elements stored in this stack, otherwise returns
     * `false`.
     */
    boolean isEmpty();
}

package cs2110.lib;

/**
 * An implementation of the Stack ADT using a linked chain, developed via a composition relationship
 * with SinglyLinkedList.
 */
public class LinkedStack<T> implements Stack<T> {

    /**
     * Stores the elements of this stack.
     */
    private final SinglyLinkedList<T> list;

    public LinkedStack() {
        list = new SinglyLinkedList<>(); // establish the composition relationship
    }

    @Override
    public void push(T elem) {
        list.insert(0, elem);
    }

    @Override
    public T pop() {
        assert !isEmpty();
        return list.remove(0);
    }

    @Override
    public T peek() {
        assert !isEmpty();
        return list.get(0);
    }

    @Override
    public boolean isEmpty() {
        return list.size() == 0;
    }
}

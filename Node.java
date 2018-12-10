package MyADTS;
/**
 * A Node used in a doubley LinkedList. Stores the location of the next and previous Nodes in addition
 * to the generic item T.
 * memory-usage: O(T) Node stores a generic type that could grow at any rate. e.g if T is an Integer
 * then O(1) but if T is a List of any kind O(n).
 * @author Riley De Leacy
 *
 * @param <T> Item type to the stored.
 */
public class Node<T> {
	public Node<T> next;
	public Node<T> Previous;
	public T element;
	/**
	 * Creates a new Instance of Node storing an input item of type T.
	 * run-time:O(1)
	 * @param element the element to be stored in this Node.
	 */
	public Node(T element) {
		this.element = element;
	}
	/**
	 * String representation of a Node.
	 * run-time:O(1)
	 * @return returns a String containing the String representation of the generic item stored in this Node.
	 */
	@Override
	public String toString() {
		String string = "";
		return string+=element;
	}
}

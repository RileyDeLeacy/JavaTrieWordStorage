package MyADTS;

import java.util.Iterator;
/**
 * A doubley LinkedList that stores a generic item.
 * memory-usage: O(n) where n is the number of elements in the List.
 * @author Riley De Leacy
 *
 * @param <T> The generic item type to be stored in the LinkedList.
 */
public class LinkedList<T> implements Iterable<Node<T>>{
	private Node<T> front;
	private Node<T> end;
	private int size;
	/**
	 * creates a new instance of LinkedList
	 * 
	 * run-time: O(1)
	 */
	public LinkedList() {
		front = null;
		end = null;
		size=0;
	}
	/**
	 * adds a new item to the end of the LinkedList.
	 * run-time:O(1)
	 * 
	 * @param element element to the added to the LinkedList.
	 */
	public void add(T element) {
		if(front==null) {
			front=new Node<T>(element);
			end=front;
			size++;
			return;
		}
		end.next = new Node<T>(element);
		end.next.Previous = end;
		end = end.next;
		size++;
	}
	/**
	 * returns the first item in the linkedList.
	 * run-time:O(1)
	 * 
	 * @return returns the first item in the LinkedList or null if the List is empty.
	 */
	public Node<T> getFront() {
		return front;
	}
	/**
	 * returns the last item in the linkedList.
	 * run-time:O(1)
	 * @return returns the last item in the LinkedList or null if the List is empty.
	 */
	public Node<T> getLast() {
		return end;
	}
	/**
	 * The size of the LinkedList.
	 * run-time:O(1)
	 * 
	 * @return returns the number of items in the LinkedList.
	 */
	public int getSize() {
		return size;
	}
	/**
	 * Determines if an Item is in the LinkedList.
	 * run-time:O(n) n=number of items in the List.
	 * 
	 * @param element the element to be checked.
	 * @return true if the item is in the list, false otherwise.
	 */
	public Boolean contains(T element) {
		if(size==0) {
			return false;
		}
		Node<T> something;
		Iterator<Node<T>> itr = this.iterator();
		while(itr.hasNext()) {
			something = itr.next();
			if(something.element.equals(element)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Creates a deep copy of the List.
	 * run-time:O(n) n=number of items in the List.
	 * 
	 * @return returns a deep copy of the List.
	 */
	public LinkedList<T> deepCopy(){
		LinkedList<T> copy = new LinkedList<T>();
		MyIterator itr = new MyIterator();
		while(itr.hasNext()) {
			copy.add(itr.next().element);
		}
		return copy;
	}
	/**
	 * String representation of a LinkedList.
	 * run-time:O(n) n=number of items in the List.
	 * 
	 * @return returns a String the String representation of all elements in the list in the order they were added.
	 * The returned String will start with an open square bracket and finish with a close square bracket.
	 */
	@Override
	public String toString() {
		String returnString = "[";
		Node<T> current = front;
		for(int i=0;i<size;i++) {
			if(current.next!=null) {
				returnString +=""+current+", ";
				current = current.next;
			}else {
				returnString +=""+current;
			}
		}
		return returnString+"]";
	}
	/**
	 * Creates a new Iterator for this LinkedList.
	 * run-time:O(1)
	 * 
	 * @return returns a new instance of a LinkedList Iterator.
	 */
	@Override
	public Iterator<Node<T>> iterator() {
		return new MyIterator();
	}
	/**
	 * Creates a new instance of a LinkedList Iterator.
	 * memory-usage:O(1)
	 * 
	 * @author Riley De Leacy
	 *
	 */
	private class MyIterator implements Iterator<Node<T>>{
		private Node<T> currentItr;
		/**
		 * Creates a new instance of a LinkedList Iterator.
		 * run-time:O(1)
		 */
		public MyIterator() {
			currentItr = null;
		}
		/**
		 * Determines if there is an item after the current in the LinkedList.
		 * run-time:O(1)
		 * 
		 * @return true if there is an item following the current, false otherwise.
		 */
		@Override
		public boolean hasNext() {
			if(front==null && this.currentItr==null) {
				return false;
			}else {
				if(front!=null && this.currentItr==null) {
					return true;
				}
			}
			if(currentItr.next!=null) {
				return true;
			}
			return false;
		}
		/**
		 * Moves the iterator to the next item in the LinkedList.
		 * run-time:O(1)
		 * 
		 * @return returns the next item in the linkedList.
		 */
		@Override
		public Node<T> next() {
			if(front==null && this.currentItr==null) {
				return null;
			}if(front!=null && this.currentItr==null) {
				currentItr=front;
				return currentItr;
			}
			if(currentItr.next!=null) {
				currentItr=currentItr.next;
				return currentItr;
			}
			return null;
		}
	}
}

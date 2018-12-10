package MyADTS;
/**
 * Node within a Trie. Stores a character, reference to a LinkedList of children TrieNodes,
 * a LinkedList of prefix locations and a LinkedList of word locations.
 * 
 * memory usage:O(n+m+y) - n=size of children list, m=size of prefix list, y=size of word list
 * 
 * @author Riley De Leacy
 *
 */
public class TrieNode {
	private char character;
	private LinkedList<TrieNode> children;
	private LinkedList<Values> values;
	private LinkedList<Values> completeWords;
	/**
	 * Creates a new instance of TrieNode with the input character.
	 * run-time: O(1)
	 * @param character the character to be stored.
	 */
	public TrieNode(Character character) {
		this.character = character;
		children = new LinkedList<TrieNode>();
		values = new LinkedList<Values>();
		completeWords = new LinkedList<Values>();
	}
	/**
	 * returns the stored character.
	 * run-time: O(1)
	 * @return returns the stored character.
	 */
	public Character getCharacter() {
		return new Character(character);
	}
	/**
	 * Returns the locations of stored prefixes.
	 * run-time: O(1)
	 * @return returns a LinkList of prefix locations.
	 */
	public LinkedList<Values> getValues(){
		return values.deepCopy();
	}
	/**
	 * Returns the locations of stored words.
	 * run-time: O(1)
	 * @return returns a LinkList of word locations.
	 */
	public LinkedList<Values> getCompleteWords(){
		return completeWords.deepCopy();
	}
	/**
	 * Add a child to this TrieNode.
	 * run-time: O(1)
	 * @param newChar The new character to be stored in this child.
	 */
	public void addChild(TrieNode newChar) {
		children.add(newChar);
	}
	/**
	 * returns all children of a TrieNode.
	 * run-time: O(n) n=number of children stored in that TrieNode.
	 * @return returns an array of all children to this TrieNode.
	 */
	public TrieNode[] getChildren() {
		if(children.getSize()==0) {
			return null;
		}
		TrieNode[] trieNodeArray = new TrieNode[children.getSize()];
		Node<TrieNode> current = children.getFront();
		for(int i=0;i<children.getSize();i++) {
			trieNodeArray[i] = current.element;
			current = current.next;
		}
		return trieNodeArray;
	}
	/**
	 * Adds a prefix Occurrence to this TrieNode.
	 * run-time: O(1)
	 * 
	 * @param column Column the occurrence starts at.
	 * @param row Row the occurrence is on.
	 */
	public void addOccurrence(int column, int row) {
		values.add(new Values(column, row));
	}
	/**
	 * Adds a word Occurrence to this TrieNode.
	 * run-time: O(1)
	 * 
	 * @param column Column the occurrence starts at.
	 * @param row Row the occurrence is on.
	 */
	public void addFullWord(int column, int row) {
		completeWords.add(new Values(column, row));
	}
	/**
	 * String representation of a TrieNode.
	 * run-time:O(1)
	 * @return returns a String containing the character stored in this TrieNode.
	 */
	public String toString() {
		return character+"";
	}
}

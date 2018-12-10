package MyADTS;

import java.util.Iterator;
/**
 * A Trie that stores locations of word prefixes and complete word locations.
 * memory-usage:O(n) Where n is the number of Nodes in the Trie.
 * 
 * @author Riley De leacy
 *
 */
public class Trie {
	private int columnCounter;
	private TrieNode root;
	/**
	 * creates a new instance of Trie.
	 */
	public Trie() {
		//The root can be anything as it's just used as a reference to find its children.
		root = new TrieNode(',');
		columnCounter = 1;
	}
	/**
	 * Adds a word to the Trie. A words prefixes and complete word locations can be stored or not. If 
	 * complete word locations aren't stored they can be added later using addExistingWord.
	 * run-time:O(dm) m=size of word, d=number of acceptable characters.
	 * 
	 * @param word The word to be added.
	 * @param row the row the word occurred on.
	 * @param addOcc add prefix and complete word occurrences (true=yes, false=no)
	 * @param incrementNum total size of the unformatted word+1 for a space.
	 * @param leadingSymbols number of symbols before the word that were formatted out.
	 */
	public void addWord(String word, int row, Boolean addOcc, int incrementNum, int leadingSymbols) {
		char[] wordArray = word.toCharArray();
		columnCounter += leadingSymbols;
		TrieNode current = root;
		Boolean found = false;
		for(int i=0;i<wordArray.length;i++) {
			if(current.getChildren()!=null) {
				for(TrieNode j:current.getChildren()) {
					if(wordArray[i]==j.getCharacter()) {
						current = j;
						found = true;
						//A TrieNode with this character already exists so add a prefix occurrence if the addOcc==true
						if(addOcc) {j.addOccurrence(columnCounter, row);}
						break;
					}
				}
			}
			if(!found) {
				//The character was not found so a new Node will be made
				TrieNode temp = new TrieNode(wordArray[i]);
				current.addChild(temp);
				current = temp;
				if(addOcc) {current.addOccurrence(columnCounter, row);}
			}
			found=false;
			if(i==wordArray.length-1&&addOcc) {
				//This is the last character in the word so add a complete word occurrence
				current.addFullWord(columnCounter, row);
			}
		}
		columnCounter+=incrementNum;
	}
	/**
	 * Adds a complete word occurrence to the input TrieNode
	 * run-time:O(1)
	 * 
	 * @param word The word to be added.
	 * @param row The row the word occurred on.
	 * @param column The column the word started at.
	 */
	public void addExistingWord(TrieNode word, int row, int column) {
		word.addFullWord(column, row);
	}
	/**
	 * Adds an input amount to the columnCounter.
	 * run-time:O(1)
	 * 
	 * @param amount amount to be added.
	 */
	public void incrementColumnCounter(int amount) {
		columnCounter += amount;
	}
	/**
	 * Removes additional spaces from string (e.g. "  something   else" -> "something else")
	 * Removes symbols and punctuation from the beginning and end of every word and optionally turns 
	 * hyphons within words to spaces (e.g. ",to-be-; or>" -> "to-be or"||"to be or").
	 * run-time:O(n)
	 * 
	 * @param string String to be formatted.
	 * @param removeHyphons whether to change hyphons into spaces (true=yes, false=no).
	 * @return returns the formatted string.
	 */
	public static String formatString(String string, Boolean removeHyphons) {
		if(string==null) {
			return null;
		}
		if(string.length()<1) {
			return "";
		}
		char[] wordArray = string.toCharArray();
		//string consists of 1 symbol
		if(string.length()<2&&!Character.isLetter(wordArray[0])) {
			return "";
		}
		//string consists of 2 symbols
		if(string.length()<3&&!Character.isLetter(wordArray[0])&&!Character.isLetter(wordArray[1])) {
			return "";
		}
		Boolean lastSpace = false;
		char[] tempCharArray = new char[wordArray.length];
		int counter = 0;
		//shifts additional spaces  to the end e.g. "hello   hi" -> "hello hi  "
		for(int i=0;i<wordArray.length;i++) {
			if(wordArray[i]==' ') {
				if(!lastSpace) {
					tempCharArray[counter] = wordArray[i];
					counter++;
				}
				lastSpace=true;
			}else {
				tempCharArray[counter] = wordArray[i];
				counter++;
				lastSpace=false;
			}
		}
		//removes additional spaces
		counter = wordArray.length;
		while(counter>0&&!Character.isLetter(tempCharArray[counter-1])) {
			counter--;
		}
		wordArray = new char[counter];
		System.arraycopy(tempCharArray, 0, wordArray, 0, counter);
		//format every word (remove preceding and following symbols)
		String[] strings = new String(wordArray).split(" ");
		String stringBuilder = "";
		for(int i=0;i<strings.length;i++) {
			if(!strings[i].equals("")) {
				//format word runs in O(n) but it's only called on a substring of the input
				strings[i]=formatWord(strings[i],removeHyphons);
				if(i!=strings.length-1) {
					stringBuilder+=strings[i]+" ";
				}else {
					stringBuilder+=strings[i];
				}
				
			}
		}
		return stringBuilder.toLowerCase();
	}
	/**
	 * Removes symbols before and after a word (",.=word''" -> "word") and optionally changes hyphons to spaces.
	 * run-time:O(n) n=characters in the input string.
	 * 
	 * @param word String to be formatted.
	 * @param removeHyphons whether hyphons should be turned into spaces (true=yes, false=no).
	 * @return returns the formatted String.
	 */
	public static String formatWord(String word, Boolean removeHyphons) {
		if(word==null) {
			return "";
		}
		if(word.equals("")) {
			return "";
		}
		char[] wordArray = word.toCharArray();
		String stringBuilder = new String(word);
		//remove prefix symbols
		while(!Character.isLetter(wordArray[0])&&wordArray.length>1) {
			stringBuilder = stringBuilder.substring(1, stringBuilder.length());
			wordArray = stringBuilder.toCharArray();
		}
		//remove following symbols
		while(!Character.isLetter(wordArray[wordArray.length-1])&&wordArray.length>1) {
			stringBuilder = stringBuilder.substring(0, stringBuilder.length()-1);
			wordArray = stringBuilder.toCharArray();
		}
		//change hyphons within the word to spaces
		if(removeHyphons) {
			wordArray = stringBuilder.toCharArray();
			for(int i=0;i<wordArray.length;i++) {
				if(wordArray[i]=='-') {
					wordArray[i]=' ';
				}
			}
			return new String(wordArray);
		}
		return stringBuilder;
	}
	/**
	 * Finds a word in the Trie.
	 * run-time:O(dm) m=size of word, d=number of acceptable characters.
	 * 
	 * @param search word to be searched for.
	 * @return returns The TrieNode the word finishes at or null if the word was not found.
	 */
	public TrieNode find(String search) {
		char[] wordArray = formatString(search,false).toCharArray();
		TrieNode current = root;
		Boolean found = false;
		for(int i=0;i<wordArray.length;i++) {
			if(current.getChildren()!=null) {
				for(TrieNode j:current.getChildren()) {
					if(wordArray[i]==j.getCharacter()) {
						current = j;
						found=true;
						break;
					}
				}
				if(!found) {
					return null;
				}
				found=false;
			}else {
				return null;
			}
		}
		return current;
	}
	/**
	 * finds an input word.
	 * run-time:O(dm) m=size of word, d=number of acceptable characters.
	 * 
	 * @param search word to be searched for.
	 * @return returns a LinkedList of Values containing the locations of all words matching the input.
	 */
	public LinkedList<Values> findWord(String search) {
		char[] wordArray = formatString(search,false).toCharArray();
		TrieNode current = root;
		Boolean found = false;
		for(int i=0;i<wordArray.length;i++) {
			if(current.getChildren()!=null) {
				for(TrieNode j:current.getChildren()) {
					if(wordArray[i]==j.getCharacter()) {
						current = j;
						found=true;
						break;
					}
				}
				if(!found) {
					return null;
				}
				found=false;
			}else {
				return null;
			}
		}
		//make a deep copy so the Trie isn't changed
		LinkedList<Values> deepCopy = new LinkedList<Values>();
		Iterator<Node<Values>> deepItr = current.getCompleteWords().iterator();
		Node<Values> iteratorNode;
		while(deepItr.hasNext()) {
			iteratorNode = deepItr.next();
			deepCopy.add(new Values(iteratorNode.element.getColumn(),iteratorNode.element.getRow()));
		}
		return deepCopy;
	}
	/**
	 * returns the number of symbols before a word.
	 * run-time:O(n) = number of characters in the input string.
	 * 
	 * @param word word to be analysed.
	 * @return returns the number of symbols and punctuation marks before a word.
	 */
	public static int numLeadingSymbols(String word) {
		char[] wordArray = word.toCharArray();
		int counter = 0;
		for(char i:wordArray) {
			if(Character.isLetter(i)) {
				break;
			}else {
				if(i=='-') {
					i=',';
				}
			}
			counter++;
		}
		return counter;
	}
	/**
	 * resets the columnCounter in the Trie.
	 * run-time:O(1)
	 */
	public void resetColumnCounter() {
		columnCounter = 1;
	}
	/**
	 * gets the columnCounter in the Trie.
	 * run-time:O(1)
	 * 
	 * @return returns the columnCounter.
	 */
	public int getColumnCounter() {
		return columnCounter;
	}
	/**
	 * counts the number of spaces in a string.
	 * run-time:O(n) n= number of characters in the input string.
	 * 
	 * @param string the string to be counted.
	 * @param index the character index to stop at.
	 * @return returns the number of spaces up until the input character index.
	 */
	public static int countSpaces(String string, int index) {
		char[] wordArray = string.toCharArray();
		int counter = 0;
		for(int i=0;i<index&&i<wordArray.length;i++) {
			if(wordArray[i]==' ') {
				counter++;
			}
		}
		return counter;
	}
}

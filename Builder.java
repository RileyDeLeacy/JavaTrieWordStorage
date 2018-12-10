package MyADTS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

import comp3506.assn2.utils.Pair;
import comp3506.assn2.utils.Triple;
/**
 * Stores words, stop-words and indexes to be searched.
 * 
 * Memory usage: 2*n+a*s+i (n=size of words, a=average size of every non-stopword, s=number of non-stopwords,
 * i=size of index)(all words are stored twice, all prefixes are stored and the index is stored)
 * 
 * @author Riley De Leacy
 *
 */
public class Builder {
	Trie stopWords;
	Trie words;
	LinkedList<Pair<String,Integer>> index;
	String file;
	//used to count the lines so they can all be stored
	int lineCounter=0;
	//stores all the lines in the words file
	String[] allLines;
	/**
	 * Creates a new Instance of Builder with the input files.
	 * run-time:O(2*n+i+s*q*a+n*r*e*d) n=lines in words file, i=index in index file, s=lines in stop word file
	 * q=length of word, a=possible characters to store, average words on line of words file,
	 * e=possible characters to store, d=number of children
	 * (words file read+index read+stop words read and insert+word file read and insert every word)
	 * 
	 * @param file file containing words to be stored.
	 * @param stopWordsFile file containing words that will not be stored.
	 * @param indexFile file containing the index of the words file.
	 */
	public Builder(String file, String stopWordsFile, String indexFile) 
			throws FileNotFoundException, IllegalArgumentException{
		if(file==null||file.equals("")) {
			throw new IllegalArgumentException("The input file cannot be null");
		}
		words = new Trie();
		stopWords = new Trie();
		this.file = file;
		//adds all stop words in the stopWordsFile to its respective Trie
		addStopWords(stopWordsFile);
		//adds all stop words in the file to its respective Trie
		addWords(file);
		//creates an array to store all the lines and then iterates over the file again to store it
		//this given constant access time when looking up lines in the phraseOccurance method
		allLines = new String[lineCounter];
	    try {
		       BufferedReader br = new BufferedReader(new FileReader(file));
		       String thisLine;
		       int tempCounter=0;
		       while ((thisLine = br.readLine()) != null) {
		   			allLines[tempCounter] = thisLine;
		   			tempCounter++;
		       }
			   br.close();
		    } catch(Exception e) {
		    	throw new FileNotFoundException(file+" Could not be read");
		    }
	    //add the index titles and lines into a LinkedList of pairs
		index = new LinkedList<Pair<String,Integer>>();
		addIndexes(indexFile);
	}
	/**
	 * Fills the index variable with Pairs of Section title and line number Pair<String,Integer>
	 * @param fileName index file directory.
	 * @throws FileNotFoundException 
	 */
	private void addIndexes(String fileName) throws FileNotFoundException {
		if(fileName==null) {
			return;
		}
		String thisLine = null;
		String[] splitString;
	    try {
	       BufferedReader br = new BufferedReader(new FileReader(fileName));
	       while ((thisLine = br.readLine()) != null) {
	    	   splitString = thisLine.split(",");
	    	   index.add(new Pair<String,Integer>(splitString[0],Integer.parseInt(splitString[1])));
	       }
		   br.close();
	    } catch(Exception e) {
	    	throw new FileNotFoundException(fileName+" Could not be read");
	    }
	}
	/**
	 * Creates the stopWord Trie but does not add word occurances to the Trie.
	 * @param fileName stop words file directory.
	 * @throws FileNotFoundException 
	 */
	private void addStopWords(String fileName) throws FileNotFoundException {
		String thisLine = null;

	    try {
	       BufferedReader br = new BufferedReader(new FileReader(fileName));
	       while ((thisLine = br.readLine()) != null) {
	   			stopWords.addWord(thisLine.toLowerCase(),0,false,thisLine.length(),0);
	       }
		   br.close();
	    } catch(Exception e) {
	    	throw new FileNotFoundException(fileName+" Could not be read");
	    }
	}
	/**
	 * Populates the words Trie adding occurrences for prefixs of words in addition to complete words.
	 * @param fileName words file directory.
	 * @throws FileNotFoundException 
	 */
	private void addWords(String fileName) throws FileNotFoundException {
		words.resetColumnCounter();
		stopWords.resetColumnCounter();	
		int rowCounter = 1;
		String thisLine = null;
	    try {
	       BufferedReader br = new BufferedReader(new FileReader(fileName));
	       while ((thisLine = br.readLine()) != null) {
	    	   addWord(thisLine, rowCounter);
	    	   rowCounter++;
	    	   words.resetColumnCounter();
	    	   stopWords.resetColumnCounter();
	    	   lineCounter++;
	       }
		   br.close();
	    } catch(Exception e) {
	    	throw new FileNotFoundException(fileName+" Could not be read");
	    }
	}
	/**
	 * Adds all words and prefixes in the input string to the words Trie or just the final word 
	 * the stopwords Trie if they are in the stopWords Trie. All words are added in lower case
	 * with preceding and following symbols removed.
	 * 
	 * @param string string of words to add (seperated by spaces and hyphons).
	 * @param row row number the line occurs on.
	 */
	private void addWord(String string, int row) {
		int leadingSymbols;
		TrieNode tempNode;
		String[] splitString;
		String[] splitHyphon;
		String tempString;
		splitString = string.split(" ");
		for(String i:splitString) {
			if(i.equals("")) {words.incrementColumnCounter(1);continue;}
			//TrieNode tempTrieNode = stopWords.find(i);
			leadingSymbols = Trie.numLeadingSymbols(i);
			splitHyphon = i.split("-");
			if(splitHyphon.length>1) {
				for(String j:splitHyphon) {
					addWord(j, row);
				}
				continue;
			}
			tempString = Trie.formatString(i,false).toLowerCase();
			tempNode = stopWords.find(tempString);
			if(tempNode==null) {
				//not a stop word
   				words.addWord(tempString,row,true,i.length()+1-leadingSymbols,leadingSymbols);
   				stopWords.incrementColumnCounter(i.length()+1-leadingSymbols);
			}else {
				stopWords.addExistingWord(tempNode, row, words.getColumnCounter());
				words.incrementColumnCounter(i.length()+1);
			}
		}
	}
	/**
	 * Finds all occurrences of the phrase in the document.
	 * A phrase may be a single word or a sequence of words.
	 * run-time:O(dm+n*z)  d=number of possible characters, m=length of first word in phrase, n=number
	 * of lines the phrase goes over, z=characters in the string.
	 * (search Trie+format String)
	 * 
	 * @param phrase phrase to be searched for
	 * @return returns a LinkedList of Values containing the locations of all occurrences of the input phrase.
	 */
	public LinkedList<Values> phraseOccurrence(String phrase){
		LinkedList<Values> returnList = new LinkedList<Values>();
		if(phrase==null||phrase.equals("")) {
			return new LinkedList<Values>();
		}
		//phrase is 1 word
		String[] splitPhrase = phrase.split(" ");
		if(splitPhrase.length==1) {
			return words.findWord(splitPhrase[0]);
		}
		//uses the first word in the phrase to search
		LinkedList<Values> firstWordOccurances = words.findWord(splitPhrase[0]);
		//the first word never occurs in the text or is a stop word
		if(firstWordOccurances==null) {
			//the first word might be a stop word
			firstWordOccurances = stopWords.findWord(splitPhrase[0]);
			//the first word never appears in the text
			if(firstWordOccurances==null) {
				return returnList;
			}
		}
		//first word could be a prefix of another word stored so the node isn't null
		if(firstWordOccurances.getSize()<1) {
			firstWordOccurances = stopWords.findWord(splitPhrase[0]);
			if(firstWordOccurances==null) {
				return returnList;
			}
		}
		String thisLine = null;
		Iterator<Node<Values>> itr = firstWordOccurances.iterator();
		Values currentValue;
	    try {
			while(itr.hasNext()) {
				currentValue = itr.next().element;
				int rowCounter = currentValue.getRow()-1;
				thisLine = allLines[rowCounter];
				try {
					//take a substring of the line starting at the first word in the phrase
					thisLine = thisLine.substring(currentValue.getColumn()-1, thisLine.length());
				}catch(StringIndexOutOfBoundsException e) {
					continue;
				}
	    		String tempString;
	    		thisLine = Trie.formatString(thisLine,true);
	    		while(countSpaces(phrase,phrase.length())>countSpaces(thisLine,thisLine.length())) {
	    			//the phrase goes over 1 line so the next line must be added to the current substring
	    			if(rowCounter>=allLines.length-1) {
	    				//there are no more lines, this is the end of the words file
	    				break;
	    			}else {
	    				//add the next line to the current substring
	    				rowCounter++;
	    				tempString = allLines[rowCounter];
		    			thisLine = thisLine+" "+tempString;
		    			thisLine = Trie.formatString(thisLine,true);
	    			}
	    		}
	    		//format the new string
	    		thisLine = Trie.formatString(thisLine,true);
	    		if(thisLine.contains(phrase)) {
	    			returnList.add(currentValue);
	    		}
			}  
		} catch(Exception e) {
		    e.printStackTrace();
		}
		return returnList;
	}
	/**
	 * counts the number of times a word appears in the words file.
	 * run-time:O(d*w) d=number of possible characters that can be stored, w=length of word.
	 * 
	 * @param inputWord word to be counted.
	 * @return returns the number of times the word appears in the words file.
	 */
	public int wordCount(String inputWord) {
		if(words.findWord(inputWord)==null) {
			return 0;
		}
		return words.findWord(inputWord).getSize();
	}
	/**
	 * Searches the document for lines that contain all the words in the 'words' parameter.
	 * Implements simple "and" logic when searching for the words.
	 * The words do not need to be contiguous on the line.
	 * run-time: O(n*m+n*d*w) n=number of words input, m=average number of times words occur, 
	 * d=number of possible characters that can be stored, w=length of word.
	 * 
	 * @param words words to be searched
	 * @return returns a LinkedList of all the lines all the input words occur on.
	 */
	public LinkedList<Integer> wordsOnLine(String[] words){
		LinkedList<Integer> returnList = new LinkedList<Integer>();
		Iterator<Node<Integer>> itr;
		if(words.length==1) {
			return getRows(this.words.findWord(words[0]));
		}
		//uses the rows of the first word as a comparer
		LinkedList<Integer> first = getRows(this.words.findWord(words[0]));
		LinkedList<LinkedList<Integer>> listOfValues = new LinkedList<LinkedList<Integer>>();
		int counter = 0;
		//get a list of every time all of the words in the words array appears in the words file
		for(String i:words) {
			if(counter>0) {
				listOfValues.add(getRows(this.words.findWord(i)));
			}
			counter++;
		}
		itr = first.iterator();
		Node<Integer> current;
		Boolean found;
		//iterate over the rows the first words occurs on
		while(itr.hasNext()) {
			current = itr.next();
			found = true;
			Iterator<Node<LinkedList<Integer>>> listItr = listOfValues.iterator();
			Node<LinkedList<Integer>> currentList;
			while(listItr.hasNext()) {
				//checks every word to see if it also occurs on that line
				currentList = listItr.next();
				if(!currentList.element.contains(current.element)) {
					found=false;
				}
			}
			if(found&&!returnList.contains(current.element)) {
				returnList.add(current.element);
			}
		}

		return returnList;
	}
	//TODO
	/**
	 * Searches the document for lines that contain all the words in the 'wordsRequired' parameter
	 * and none of the words in the 'wordsExcluded' parameter.
	 * Implements simple "not" logic when searching for the words.
	 * The words do not need to be contiguous on the line.
	 * run-time:O(n+i+x+y) n=words returned by wordsOnLine, i=size of list, x=wordsOnLine run-time,
	 * y=someWordsOnLine run-time
	 * 
	 * @param words Array of words to be searched for.
	 * @param blockWords Array not words.
	 * @return returns a LinkedList of rows all the input words occur on that don't contain any not words.
	 */
	public LinkedList<Integer> wordsNotOnLine(String[] words, String[] blockWords){
		LinkedList<Integer> wordsLocation = wordsOnLine(words);
		LinkedList<Integer> blockWordsLocation = someWordsOnLine(blockWords);
		LinkedList<Integer> returnList = new LinkedList<Integer>();
		Iterator<Node<Integer>> itr = wordsLocation.iterator();
		Node<Integer> counter;
		while(itr.hasNext()) {
			counter = itr.next();
			if(!blockWordsLocation.contains(counter.element)) {
				returnList.add(counter.element);
			}
		}
		return returnList;
	}
	/**
	 * Takes a List of Values and returns a List of just the rows those values occured on without
	 * any duplicates.
	 * 
	 * @param values List of Values
	 * @return List of rows
	 */
	private LinkedList<Integer> getRows(LinkedList<Values> values){
		LinkedList<Integer> rows = new LinkedList<Integer>();
		if(values==null) {
			return rows;
		}
		Iterator<Node<Values>> valuesItr = values.iterator();
		Node<Values> current;
		while(valuesItr.hasNext()) {
			current = valuesItr.next();
			if(!rows.contains(current.element.getRow())) {
				rows.add(current.element.getRow());
			}
		}
		return rows;
	}
	/**
	 * Searches the document for lines that contain any of the words in the 'words' parameter.
	 * Implements simple "or" logic when searching for the words.
	 * The words do not need to be contiguous on the line.
	 * run-time:O(n*(d+w+o)) n=number of words input, d=number of possible characters that can be stored,
	 * w=length of word, o=number of word occurrences, o=items in the list.
	 * 
	 * @param words Array of words to be searched for.
	 * @return returns the lines that contain at least one of the words input.
	 */
	public LinkedList<Integer> someWordsOnLine(String[] words) {
		LinkedList<Integer> tempList = new LinkedList<Integer>();
		Iterator<Node<Values>> itr;
		Node<Values> tempNode;
		for(String i:words) {
			if(this.words.findWord(i)!=null) {
				itr = this.words.findWord(i).iterator();
				while(itr.hasNext()) {
					tempNode = itr.next();
					if(!tempList.contains(tempNode.element.getRow())) {
						tempList.add(tempNode.element.getRow());
					}
				}
			}
		}
		return tempList;
	}
	/**
	 * retrieves the start and end of every (in a pair) of every index in the input indexes.
	 * run-time:O(n*m) n=number of indexes, m=number of stored indexes
	 * 
	 * @param indexes
	 * @return
	 */
	private LinkedList<Pair<Integer,Integer>> getIndexLines(String[] indexes){
		LinkedList<Pair<Integer,Integer>> indexLines = new LinkedList<>();
		if(indexes!=null) {
			if(indexes.length>0) {
				for(String i:indexes) {
					for(Node<Pair<String,Integer>> y:index) {
						if(i.equals(y.element.getLeftValue())) {
							//found index
							if(y.next==null) {
								//next is null, set lower bound to end of document
								indexLines.add(new Pair<Integer,Integer>(y.element.getRightValue(),lineCounter+1));
							}else {
								//next is not null, set lower bound to the end of the section
								indexLines.add(new Pair<Integer,Integer>(y.element.getRightValue(),y.next.element.getRightValue()));
							}
							break;
						}
					}
				}
			}else {return null;}
		}else {return null;}
		return indexLines;
	}
	/**
	 * Checks if a number is between any pair of numbers within a List of Pairs but not equal to 
	 * the latter of the pair. (e.g. pair.first<=number&&number<pair.second) true if this condition
	 * is satisfied for any of the pairs in the input.
	 * run-time:O(n) n=number of input indexes
	 * 
	 * @param bounds List of Pairs.
	 * @param number number to be compared.
	 * @return returns true if the number of between any of the input pairs.
	 */
	private Boolean inBounds(LinkedList<Pair<Integer,Integer>> bounds, Integer number) {
		if(bounds==null) {
			return true;
		}
		if(bounds.getSize()<1) {
			return true;
		}
		if(number==null) {
			return false;
		}
		Iterator<Node<Pair<Integer,Integer>>> itr = bounds.iterator();
		Node<Pair<Integer,Integer>> tempNode;
		while(itr.hasNext()) {
			tempNode = itr.next();
			if(withinBound(tempNode.element,number)) {
			//if(tempNode.element.getLeftValue()<number&&number<tempNode.element.getRightValue()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * check if a number is between a pair of numbers.
	 * run-time:O(1)
	 * 
	 * @param bound pair of numbers.
	 * @param number Integer to check.
	 * @return returns true if the Integer is between the pair of Integers but not equal to the latter
	 * Integer
	 */
	private Boolean withinBound(Pair<Integer,Integer> bound, Integer number) {
		if(bound.getLeftValue()<=number&&number<bound.getRightValue()) {
			return true;
		}
		return false;
	}
	/**
	 * Searches the document for sections that contain all the words in the 'words' parameter.
	 * Implements simple "and" logic when searching for the words.
	 * The words do not need to be on the same lines.
	 * run-time:O(g+n*d*w+i*o+s) g=getIndexLines run-time, n=number of words input,
	 * d=number of possible characters that can be stored, w=length of word
	 * i=number of index entries, o=average number of times the words occurred
	 * s=run-time of simpleOrSearch.
	 * (getIndexLines, search for every word input, iterate over every index and words rows,simpleOrSearch)
	 * 
	 * @param words
	 * @param indexes
	 * @return
	 */
	public LinkedList<Triple<Integer,Integer,String>> simpleAndSearch(String[] words, String[] indexes){
		LinkedList<Pair<Integer,Integer>> indexLines = getIndexLines(indexes);
		return simpleAndSearch(words,indexLines);
	}

	private LinkedList<Triple<Integer,Integer,String>> simpleAndSearch(String[] words, LinkedList<Pair<Integer,Integer>> indexes){
		LinkedList<Pair<Integer,Integer>> newIndexLines = new LinkedList<>();
		LinkedList<LinkedList<Integer>> values = new LinkedList<LinkedList<Integer>>();
		for(String j:words) {
			values.add(getRows(this.words.findWord(j)));
		}
		Boolean wordWithin = false;
		Boolean wordsWithin = true;
		for(Node<Pair<Integer,Integer>> i:indexes) {
			wordsWithin=true;
			for(Node<LinkedList<Integer>> m:values) {
				for(Node<Integer> q:m.element) {
					if(withinBound(i.element,q.element)) {
						wordWithin = true;
						break;
					}
				}
				if(!wordWithin) {
					wordsWithin = false;
					break;
				}
				wordWithin = false;
			}
			if(wordsWithin) {
				newIndexLines.add(i.element);
			}
		}
		if(newIndexLines.getSize()<1) {
			return new LinkedList<Triple<Integer,Integer,String>>();
		}
		return simpleOrSearch(words,newIndexLines);
	}
	/**
	 * Searches the document for sections that contain any of the words in the 'words' parameter.
	 * Implements simple "or" logic when searching for the words.
	 * The words do not need to be on the same lines.
	 * run-time:O(g+n*(d*w+i)) g=getIndexLines run-time, n=number of words input,
	 * d=number of possible characters that can be stored, w=length of word
	 * i=number of indexes
	 * (getIndexLines, search for n words, run Inbounds n times)
	 * 
	 * @param words Array of words to be searched.
	 * @param indexes array of indexes.
	 * @return returns all occurrences of the words in words within the specified indexes.
	 */
	public LinkedList<Triple<Integer,Integer,String>> simpleOrSearch(String[] words, String[] indexes){
		LinkedList<Pair<Integer,Integer>> indexLines = getIndexLines(indexes);
		return simpleOrSearch(words,indexLines);
	}

	private LinkedList<Triple<Integer,Integer,String>> simpleOrSearch(String[] words, LinkedList<Pair<Integer,Integer>> indexes){
		LinkedList<Triple<Integer,Integer,String>> returnList = new LinkedList<>();
		LinkedList<Pair<Integer,Integer>> indexLines = indexes;
		Iterator<Node<Values>> itr;
		Node<Values> tempNode;
		for(String i:words) {
			TrieNode tempTrieNode = this.words.find(i);
			if(tempTrieNode!=null) {
				itr = tempTrieNode.getCompleteWords().iterator();
			}else {
				continue;
			}
			while(itr.hasNext()) {
				tempNode = itr.next();
				if(inBounds(indexLines,tempNode.element.getRow())) {
					returnList.add(new Triple<Integer,Integer,String>(tempNode.element.getRow(),tempNode.element.getColumn(),i));
				}
			}
		}
		return returnList;
	}
	/**
	 * Searches the document for sections that contain all the words in the 'wordsRequired' parameter
	 * and none of the words in the 'wordsExcluded' parameter.
	 * Implements simple "not" logic when searching for the words.
	 * The words do not need to be on the same lines.
	 * run-time:O(s+g+i*w) s=someWordsOnLine run-time, g=getIndexLines run-time, i=number of indexes,
	 * w=number of rows returned from someWordsOnLine
	 * (someWordsOnLine run-time, getIndexLines run-time, iterate over every Index and every row returned by someWordsOnLine)
	 * 
	 * @param words
	 * @param indexes
	 * @param excludedWords
	 * @return
	 */
	public LinkedList<Triple<Integer,Integer,String>> simpleNotSearch(String[] words, String[] indexes, String[] excludedWords){
		//simpleAndSearch will exclude all titles that contain a word in excludedWords
		LinkedList<Integer> rows = someWordsOnLine(excludedWords);
		LinkedList<Pair<Integer,Integer>> indexLines = getIndexLines(indexes);
		if(rows!=null) {
			if(rows.getSize()>0) {
				LinkedList<Pair<Integer,Integer>> tempIndexLines = new LinkedList<>();
				Boolean between = false;
				for(Node<Pair<Integer,Integer>> i:indexLines) {
					for(Node<Integer> j:rows) {
						if(withinBound(i.element,j.element)) {
							between = true;
							break;
						}
					}
					if(!between) {
						tempIndexLines.add(i.element);
					}
					between = false;
				}
				indexLines = tempIndexLines;
			}
		}
		return simpleAndSearch(words,indexLines);
	}
	/**
	 * Searches the document for sections that contain all the words in the 'wordsRequired' parameter
	 * and at least one of the words in the 'orWords' parameter.
	 * Implements simple compound "and/or" logic when searching for the words.
	 * The words do not need to be on the same lines.
	 * run-time:O(s+g+i*w+i*o+n*d*w+e) s=someWordsOnLine run-time, g=getIndexLines run-time,
	 * i=number of index entries, o=average number of times the words occurred,
	 * n=number of words input, d=number of possible characters that can be stored, w=length of word,
	 * w=number of Integers returned from someWordsOnline, e=number of results at the end)
	 * 
	 * (someWordsOnLine run-time, getIndexLines run-time, Iterate over the indexes and rows returned by someWordsOnLine,
	 * find values for every input word, iterate over every index and result of finding all values for all input words,
	 * iterate over results to copy)
	 * 
	 * @param words
	 * @param indexes
	 * @param orWords
	 * @return
	 */
	public LinkedList<Triple<Integer,Integer,String>> compoundAndOrSearch(String[] words, String[] indexes, String[] orWords){
		LinkedList<Integer> rows = someWordsOnLine(orWords);
		LinkedList<Pair<Integer,Integer>> indexLines = getIndexLines(indexes);
		//or algorithm
		LinkedList<Pair<Integer,Integer>> tempIndexLines;
		if(rows!=null) {
			if(rows.getSize()>0) {
				tempIndexLines = new LinkedList<>();
				Boolean between = false;
				for(Node<Pair<Integer,Integer>> i:indexLines) {
					for(Node<Integer> j:rows) {
						if(withinBound(i.element,j.element)) {
							between = true;
							break;
						}
					}
					if(between) {
						tempIndexLines.add(i.element);
					}
					between = false;
				}
				indexLines = tempIndexLines;
			}
		}
		//and algorithm
		LinkedList<LinkedList<Integer>> values = new LinkedList<LinkedList<Integer>>();
		for(String j:words) {
			values.add(getRows(this.words.findWord(j)));
		}
		Boolean wordWithin = false;
		Boolean wordsWithin = true;
		tempIndexLines = new LinkedList<>();
		for(Node<Pair<Integer,Integer>> i:indexLines) {
			wordsWithin=true;
			for(Node<LinkedList<Integer>> m:values) {
				for(Node<Integer> q:m.element) {
					if(withinBound(i.element,q.element)) {
						wordWithin = true;
						break;
					}
				}
				if(!wordWithin) {
					wordsWithin = false;
					break;
				}
				wordWithin = false;
			}
			if(wordsWithin) {
				tempIndexLines.add(i.element);
			}
		}
		//build new array to return
		LinkedList<String> tempLinkedList = new LinkedList<>();
		for(String i:words) {
			if(!tempLinkedList.contains(i)) {
				tempLinkedList.add(i);
			}
		}
		for(String i:orWords) {
			if(!tempLinkedList.contains(i)) {
				tempLinkedList.add(i);
			}
		}
		String[] tempArray = new String[tempLinkedList.getSize()];
		int counter = 0;
		for(Node<String> i:tempLinkedList) {
			tempArray[counter] = i.element;
			counter++;
		}
		//the titles have been limited to only those that contain at least 1 of the words in orWords
		return simpleOrSearch(tempArray,tempIndexLines);
	}
	/**
	 * Counts the number of spaces that are not touching other spaces in a string.
	 * @param string String to be counted.
	 * @param index index to stop at.
	 * @return returns the number of spaces not touching other spaces in the input string.
	 */
	private int countSpaces(String string, int index) {
		char[] wordArray = string.toCharArray();
		int counter = 0;
		Boolean lastSpace = false;
		for(int i=0;i<index&&i<wordArray.length;i++) {
			if(wordArray[i]==' '&&!lastSpace) {
				counter++;
				lastSpace=true;
			}else {
				lastSpace=false;
			}
		}
		return counter;
	}
	/**
	 * Returns prefix occurrences.
	 * run-time:O(d*w) d=number of possible characters that can be stored, w=length of word.
	 * 
	 * @param string prefix to be searched.
	 * @return returns a LinkedList of values containing the starting points of all prefix occurences.
	 */
	public LinkedList<Values> prefixOccurrence(String string){
		TrieNode tempNode = words.find(string);
		if(tempNode==null) {
			return new LinkedList<Values>();
		}
		return tempNode.getValues();
	}
}

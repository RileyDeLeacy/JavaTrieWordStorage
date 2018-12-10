package MyADTS;
/**
 * Stores the column and row of something.
 * memory usage:O(1)
 * 
 * @author Riley De Leacy
 *
 */
public class Values {
	/*
	 * near identical to Pair but has more methods and a toString method for testing (I can't change Pair)
	 */
	private int column;
	private int row;
	/**
	 * Creates a new instance of Values.
	 * run-time:O(1)
	 * 
	 * @param column column to be stored.
	 * @param row row to be stored.
	 */
	public Values(int column, int row) {
		this.column = column;
		this.row = row;
	}
	/**
	 * sets the row parameter.
	 * run-time:O(1)
	 * 
	 * @param row new row number.
	 */
	public void setRow(int row) {
		this.row = row;
	}
	/**
	 * sets the column parameter
	 * run-time:O(1)
	 * 
	 * @param column new column number.
	 */
	public void setColumn(int column) {
		this.column = column;
	}
	/**
	 * retrieves the row number.
	 * run-time:O(1)
	 * 
	 * @return returns the row number.
	 */
	public int getRow() {
		return row;
	}
	/**
	 * Adds 1 to the current row.
	 * run-time:O(1)
	 */
	public void incrementRow() {
		row++;
	}
	/**
	 * retrieves the column number.
	 * run-time:O(1)
	 * 
	 * @return returns the column number.
	 */
	public int getColumn() {
		return column;
	}
	/**
	 * adds 1 to the column number.
	 * run-time:O(1)
	 */
	public void incrementColumn() {
		column++;
	}
	/**
	 * String representation of Values. The String is of format "column-row".
	 * run-time:O(1)
	 * 
	 * @return returns the string representation of Values.
	 */
	public String toString() {
		return column+"-"+row;
	}
}

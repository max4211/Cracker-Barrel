import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Win Game, inspired by Cracker Barrel Pancakes in Outer Banks, NC
// 6 July 2019
// Written by Max Smith

public class AllStartPos {

	protected static int winCount = 0; // Keep track of how many wins we have
	protected static int iterations = 0; // Keep track of total iterations (check inefficiency @ scale)
	protected static int endCount = 0; // Keep track of total end games
	protected static int id = 1; // Identification number of game
	
	protected static char outsideTriangle = '.'; // Area in grid outside of triangle
	protected static char emptySpace = 'O'; // Space that is empty is an o (oh)
	protected static char occupiedSpace = 'X'; // Space that has a tack in it is an X (ex)
	protected static char separator = '-'; // separator between moves
	
	protected static Map<Integer, Integer> tackMap; // Map to keep track of tacks left
	// private static HashMap<Integer, String> moveHistory; // Hash map to keep track of ALL history of moves
	protected static HashMap<Integer, String> winningMoves; // Hash map to keep track of winning moves
	
	protected static int gridSize = 5; // Dimensions of square grid (DO NOT INCREASE ABOVE 6!! - big Oh got me :()
	
	// Right, up, left, down, up-right, down-right, up-left (NO DOWNLEFT) // Possible motions
	protected static int[] rowDelta = {1, 0, -1, 0, 1, -1}; 
	protected static int [] colDelta = {0, 1, 0, -1, 1, -1}; 
	
	// Vars from TestStartPosition.java
	private static int[] rowStart = {0, 1, 2, 2};
	private static int[] colStart = {0, 0, 0, 1};
	private static String[] positionNames = {"Corner point", "One offset", "Center of edge", "Center of board"};
	
	private static long[] totalTime = new long[rowStart.length];
	private static double[] winResults = new double[rowStart.length];
	
	/**
	 * Run all possible starting positions, output a table with these results
	 */
	public static void main (String[] args) {
		long startTime; long endTime;
		int row; int col;
		int lineWrite = 0;
		for (int i = 0; i < rowStart.length; i ++) {
			initializeGuys();
			
			startTime = System.nanoTime();
			row = rowStart[i];
			col = colStart[i];
			
			System.out.println("Starting grid #" + (i + 1));
			char[][] startBoard = removeOne(fillBoard(gridSize), row, col);
			// makeGridPretty(startBoard, solutions);
			// letsPlay(startBoard, id, "");
			
			try {
				String filename = "Solutions_" + Integer.toString(i) + ".txt";
				PrintWriter solutions = new PrintWriter(filename);
				System.out.println("Created PrintWriter: " + filename);
				// lineWrite += saveMoves(solutions);
				makeGridPretty(startBoard, solutions);
				letsPlay(startBoard, id, "", solutions);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			winResults[i] = getResultsFromMap(1);
			endTime = System.nanoTime();
			totalTime[i] = (endTime - startTime) / 1000000; // Time elapsed in milliseconds

		}
		try {
			String filename = "Summary.txt";
			PrintWriter summary = new PrintWriter(filename);
			System.out.println("Created PrintWriter: " + filename);
			// lineWrite += saveMoves(solutions);
			printTestResults(summary);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// printTestResults(summary);
	}
	
	// Create the board (triangle inside of square)
	// Fill all possible spaces
	public static char[][] fillBoard(int size){
		char[][] gameBoard = new char[size][size];
		
		for (int i = 0; i < size; i ++) { // i keeps track of row in grid
			for (int j = 0; j < size; j ++) { // Populate all first i items with an occupied space
				if (j <= i)
					gameBoard[i][j] = occupiedSpace;
				if (j > i)
					gameBoard[i][j] = outsideTriangle;
			}
		}
		return gameBoard;
	}
	
	// Convert the grid to a single string representation
	public static String gridToString(char[][] gameBoard) {
		String boardString = "";

		for (int i = 0; i < gridSize; i ++) {
			for (int j = 0; j < gridSize; j ++) {
				char guy = gameBoard[i][j];
				boardString += guy;
			}
		}
		return boardString;
	}
	
	// Convert standard processing grid to a vertical grid
	public static void makeGridPretty(char[][] gameBoard, PrintWriter solutions) {
		for (int i = 0; i < gridSize; i ++) {
			String guy = "";
			for (int j = 0; j < gridSize; j ++) {
				guy += gameBoard[i][j];
			}
			System.out.println(guy);
			solutions.println(guy);
			
		}
	}
	public static void makeStringPretty(String boardString, PrintWriter solutions) {
		makeGridPretty(stringToGrid(boardString), solutions);
	}
	
	// Make the grid look more realistic (double dimensions, center all parts of the board)
	// This doesn't work yet
	public static void makeGridReal(char[][] gameBoard) {
		char[][] realGrid = new char[gridSize*2][gridSize*2];
		for (int i = 0; i < gridSize; i ++) {
			for (int j = 0; j < gridSize; j ++) {
				realGrid[2*i][j] = gameBoard[i][j];
				realGrid[2*i+1][j] = gameBoard[i][j];
			}
		}
		
	}
	
	// Convert the string representation of the board back to a grid
	public static char[][] stringToGrid(String boardString) {
		char gameBoard[][] = new char[gridSize][gridSize];
		int index = 0;
		for (int i = 0; i < gridSize; i ++) {
			for (int j = 0; j < gridSize; j ++) {
				gameBoard[i][j] = boardString.charAt(index);
				index ++;
			}
		}
		return gameBoard;
	}
	
	// Remove a single tack, assign it a 'hopped space' char
	public static char[][] removeOne(char[][] myBoard, int row, int col) {
		myBoard[row][col] = emptySpace;
		return myBoard;
	}
	
	// Count the number of tacks in the grid
	public static int tackCount(char[][] myBoard) {
		int count = 0;
		String boardString = gridToString(myBoard);
		for (int i = 0; i < boardString.length(); i ++) {
			if (boardString.charAt(i) == occupiedSpace)
				count ++;
		}
		return count;
	}
	
	// Check if the desired position is in bounds of the board
	public static boolean inBounds(int row, int col) {
		if (row >= 0 && row < gridSize && col >= 0 && col < gridSize) 
			return true;
		return false;
	}
	
	// Append the tacks left list hash map, append hashmap of moves history
	public static void appendList(int tacksLeft) {
		
		int key = tacksLeft;
		if (!(tackMap.containsKey(key)))
			tackMap.put(key, 0);
		tackMap.put(key, tackMap.get(key) + 1);
	}
	
	// Verify that a move is possible (ran once during play function)
	public static boolean canMove(char[][] myGrid) {
		for (int i = 0; i < gridSize; i ++ ) { // Scan through grid, checking for moves
			for (int j = 0; j < gridSize; j ++) {
				// Now do a neighbor check to see which directions you can go

				for (int k = 0; k < rowDelta.length; k ++) {
					int nrow = i + rowDelta[k]; // Neighbor row
					int ncol = j + colDelta[k]; // Neighbor col
					int trow = i + 2 * rowDelta[k]; // Target row
					int tcol = j + 2 * colDelta[k]; // Target col
					
					// Check if everything is in bounds (no error out of bounds exceptions)
					if (inBounds(nrow, ncol) && inBounds(trow, tcol)) {
						// Now check if you are occupied, neighbor is occupied, and target is unoccupied
						if (myGrid[i][j] == occupiedSpace && myGrid[nrow][ncol] == occupiedSpace && myGrid[trow][tcol] == emptySpace) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	// Play the game, recursively calling yourself to generate a number of wincounts from a starting board
	public static void letsPlay (char[][] myGrid, int id, String priorMoves, PrintWriter solutions)  {
		iterations ++;
		boolean move = canMove(myGrid);
		int tacksLeft = tackCount(myGrid);
		
		if (!move) { // If you can't move, then do the following
			appendList(tacksLeft);
			endCount ++;
		}
		
		if( tacksLeft == 1 ) {// Check for a win
			// System.out.println("Solution reached, appending winningMoves");
			winCount ++; // Increment win count with one tack left
			winningMoves.put(id, priorMoves + separator); // Append winning move history
			solutions.println(priorMoves + separator);
		}
		
		if (!(tacksLeft == 1) && move) { // If no win and move possible, keep playing
			for (int i = 0; i < gridSize; i ++ ) { // Scan through grid, checking for moves
				for (int j = 0; j < gridSize; j ++) {
					// Now do a neighbor check to see which directions you can go
					for (int k = 0; k < rowDelta.length; k ++) {
						int nrow = i + rowDelta[k]; // Neighbor row
						int ncol = j + colDelta[k]; // Neighbor col
						int trow = i + 2 * rowDelta[k]; // Target row
						int tcol = j + 2 * colDelta[k]; // Target col
						
						// Check if everything is in bounds (no error out of bounds exceptions)
						if (inBounds(nrow, ncol) && inBounds(trow, tcol)) {
							// Now check if neighbor is occupied, and target is unoccupied
							if (myGrid[i][j] == occupiedSpace && myGrid[nrow][ncol] == occupiedSpace && myGrid[trow][tcol] == emptySpace) {							
								// Make the move, recursively call
								letsPlay(makeMove(myGrid, i, j, nrow, ncol, trow, tcol), id+1, priorMoves + separator + moveCoordinates(i, j, trow, tcol), solutions);
								// Undo move, keep going
								myGrid = undoMove(myGrid, i, j, nrow, ncol, trow, tcol);
							}
						}
					}
				}
			}
		}
		
	}
	
	// Convert row col values of you + target to a move with coordinates
	public static String moveCoordinates (int row, int col, int trow, int tcol) {
		int start = gridMapping(row, col);
		int end = gridMapping(trow, tcol);
		
		String thisMove = Integer.toString(start) + " to " + Integer.toString(end);
		
		return thisMove;
	}
	
	public static char[][] makeMove(char[][] nextGrid, int i, int j, int nrow, int ncol, int trow, int tcol) {
		nextGrid[i][j] = emptySpace;
		nextGrid[nrow][ncol] = emptySpace;
		nextGrid[trow][tcol] = occupiedSpace;
		return nextGrid;
	}
	
	public static char[][] undoMove(char[][] myGrid, int i, int j, int nrow, int ncol, int trow, int tcol) {
		myGrid[i][j] = occupiedSpace;
		myGrid[nrow][ncol] = occupiedSpace;
		myGrid[trow][tcol] = emptySpace;
		return myGrid;
	}
	
	// Print out summary of results
	public static void printSummary() {
		
		System.out.println("\nTotal number of wins: " + winCount);
		System.out.println("Total number of function calls: " + iterations);
		System.out.println("Total number of end game counts: " + endCount + "\n");
		
		// Need endCount as a double to do divisions
		double dubEnd = Double.valueOf(endCount);		
		
		// Print keyset of tack map (resulting % and counts)
		for (int key: tackMap.keySet()) {
			String guy = "were ";
			
			// Get creative to get percent to display
			int value = tackMap.get(key);
			double percent = (value/dubEnd)*100;
			BigDecimal bd = new BigDecimal(percent);
			bd = bd.round(new MathContext(3));
			double rounded = bd.doubleValue();
			
			if (key == 1)
				guy = "was ";	
			System.out.println("There " + guy +  key + " tack(s) left " + value + " times (" + rounded + "%).");
		}
	}
	
	// Print one of the winning sequencies
	public static void printWins() {
		// Get the first key
		Map.Entry<Integer, String> entry = winningMoves.entrySet().iterator().next();
		int keyID = entry.getKey();
		String valueMoves = entry.getValue();
		// System.out.println("ID: " + keyID);
		System.out.println("\nSteps to a solution: ");
		// Create array list of moves (pre allocate for memory later?)
		ArrayList<String> guy = parseMoves(valueMoves);
		
		for (int i = 1; i < guy.size(); i ++) {
			System.out.println("\nMove #" + i);
			System.out.println(guy.get(i));
		}
		
		System.out.println("\nWINNER WINNER CHICKEN DINNER!!!");
	}
	
	// Parse the winning moves (@ separator)
	public static ArrayList<String> parseMoves(String valueMoves) {
		String addMe = "";
		ArrayList<String> returnMe = new ArrayList<String>();
		
		for (int i = 0; i < valueMoves.length(); i ++) {
			char cur = valueMoves.charAt(i);
			if (cur == separator) {
				returnMe.add(addMe);
				addMe = "";
			} else {
			addMe += cur;
			}
		}
		return returnMe;
	}
	
	// Create a map that can convert coordinates to an int (index in grid for given set up)
	public static int gridMapping (int row, int col) {
		if (col > row) {
			throw new IllegalArgumentException("Nice try buddy, this doesn't fit on on the board");
		}
		int index = 0;
		for (int i = row; i >= 0; i --) {
			index += i;
		}
		return index + col + 1;
	}
	
	// Create an empty board to display row, col mapping indices
	public static void emptyBoard(int gridSize) {
		if (gridSize < 0)
			throw new IllegalArgumentException("Grid too small, can't be negative.");
		System.out.println("\nBoard coordinates: ");
		ArrayList thisGrid = new ArrayList<>();
		int index = 1;
		int curRow = 0;
		int spaces;
		for (int i = 0; i < gridSize; i ++) {
			String thisRow = "";
			spaces = (gridSize - i + 1);
			if (i == gridSize - 1) // This is sloppy, find a creative way to account for double digits and make it look clean
				spaces = 0;
			for (int k = 0; k < spaces; k ++) {
				thisRow += " ";
			}
			for (int j = curRow; j <= i; j ++) {
				thisRow += Integer.toString(index) + " ";
				index ++;
			}
			thisGrid.add(thisRow);
		}
		
		for (int i = 0; i < thisGrid.size(); i ++) {
			System.out.println(thisGrid.get(i));
		}
		
		
	}
	
	private static int saveMoves(PrintWriter solutions) throws IOException {
		int count = 0;
		String message = "Writing solutions to text file";
		System.out.println(message);
		solutions.println(message);
		for (Integer key: winningMoves.keySet()) {
			String sequence = winningMoves.get(key);
			solutions.println(sequence);
			count += 1;
		}
		System.out.println("Total lines written: " + Integer.toString(count));
		return count;
	}
	
	private static double getResultsFromMap(int key) {
		double dubEnd = Double.valueOf(endCount);
		int value = tackMap.get(key);
		double percent = (value/dubEnd)*100;
		BigDecimal bd = new BigDecimal(percent);
		bd = bd.round(new MathContext(3));
		double rounded = bd.doubleValue();
		return rounded;
	}
	
	private static void initializeGuys() {
		tackMap = new HashMap<>(); winningMoves = new HashMap<>();
		endCount = 0; winCount = 0; iterations = 0; id = 1;
	}

	// Print position, time, and percent results
	private static void printTestResults(PrintWriter summary) {
		System.out.println("Position: \t Time (ms): \t Results(%):");
		summary.println("Position: \t Time (ms): \t Results(%):");
		System.out.println("--------- \t ---------- \t -----------");
		summary.println("--------- \t ---------- \t -----------");
		for (int i = 0; i < rowStart.length; i ++) {
			System.out.println(positionNames[i] + "\t\t" + totalTime[i] + "\t\t" + winResults[i]);
			summary.println(positionNames[i] + "\t\t" + totalTime[i] + "\t\t" + winResults[i]);
		}
	}
	
}

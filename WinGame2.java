import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Win Game, inspired by Cracker Barrel Pancakes in Outer Banks, NC
// 6 July 2019
// Written by Max Smith

public class WinGame2 {

	private static int winCount = 0; // Keep track of how many wins we have
	private static int iterations = 0; // Keep track of total iterations (check inefficiency @ scale)
	private static int endCount = 0; // Keep track of total end games
	private static int id = 1; // Identification number of game
	
	private static char outsideTriangle = '.'; // Area in grid outside of triangle
	private static char emptySpace = 'O'; // Space that is empty is an o (oh)
	private static char occupiedSpace = 'X'; // Space that has a tack in it is an X (ex)
	private static char separator = '-'; // separator between moves
	
	private static Map<Integer, Integer> tackMap; // Map to keep track of tacks left
	// private static HashMap<Integer, String> moveHistory; // Hash map to keep track of ALL history of moves
	private static HashMap<Integer, String> winningMoves; // Hash map to keep track of winning moves
	
	private static int gridSize = 5; // Dimensions of square grid (DO NOT INCREASE ABOVE 6!! - big Oh got me :()
	
	// Right, up, left, down, up-right, down-right, up-left (NO DOWNLEFT) // Possible motions
	private static int[] rowDelta = {1, 0, -1, 0, 1, -1}; 
	private static int [] colDelta = {0, 1, 0, -1, 1, -1}; 
	
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
	public static void makeGridPretty(char[][] gameBoard) {
		for (int i = 0; i < gridSize; i ++) {
			String guy = "";
			for (int j = 0; j < gridSize; j ++) {
				guy += gameBoard[i][j];
			}
			System.out.println(guy);
		}
	}
	public static void makeStringPretty(String boardString) {
		makeGridPretty(stringToGrid(boardString));
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
	public static void letsPlay (char[][] myGrid, int id, String priorMoves) {
		iterations ++;
		boolean move = canMove(myGrid);
		int tacksLeft = tackCount(myGrid);
		
		if (!move) { // If you can't move, then do the following
			appendList(tacksLeft);
			endCount ++;
		}
		
		if( tacksLeft == 1) {// Check for a win
			winCount ++; // Increment win count with one tack left
			winningMoves.put(id, priorMoves + separator); // Append winning move history
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
								letsPlay(makeMove(myGrid, i, j, nrow, ncol, trow, tcol), id+1, priorMoves + separator + moveCoordinates(i, j, trow, tcol));
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

	public static void main (String [] args) {
		long startTime = System.nanoTime();		
		tackMap = new HashMap<>(); // Initialize tackMap, key is tacks left, value is quanitites of these
		winningMoves = new HashMap<>(); // Initialize array of move history (track winning moves)
		
		// Specify what places to remove (inputs to removeOne below)
		int row = 3;
		int col = 1;
		
		System.out.println("Starting grid: ");
		char[][] startBoard = removeOne(fillBoard(gridSize), row, col);
		makeGridPretty(startBoard);
		letsPlay(startBoard, id, "");
		
		// Print out game summaries
		// Change to 13 pairs of point moves
		printSummary();
		// emptyBoard(gridSize);
		// printWins();
		long endTime = System.nanoTime();
		System.out.println("Took: " + (endTime - startTime)/1000000 + " ms.");
	}
	
}

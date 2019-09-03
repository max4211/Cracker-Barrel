import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

public class TestStartPosition extends WinGame2 {
	
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
			makeGridPretty(startBoard);
			letsPlay(startBoard, id, "");
			
			winResults[i] = getResultsFromMap(1);
			endTime = System.nanoTime();
			totalTime[i] = (endTime - startTime) / 1000000; // Time elapsed in milliseconds
			try {
				String filename = "Solutions_" + Integer.toString(i) + ".txt";
				PrintWriter solutions = new PrintWriter(filename);
				System.out.println("Created PrintWriter" + filename);
				lineWrite += saveMoves(solutions);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		printTestResults();
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
	private static void printTestResults() {
		System.out.println("Position: \t Time (ms): \t Results(%):");
		System.out.println("--------- \t ---------- \t -----------");
		for (int i = 0; i < rowStart.length; i ++) {
			System.out.println(positionNames[i] + "\t\t" + totalTime[i] + "\t\t" + winResults[i]);
		}
	}
}

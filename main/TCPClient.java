import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;


public class TCPClient {
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		int portNumber = 55555;
		int matrixSize = 32;     // This should be either 16, 32, 64, 128, or 256
		int numberOfMatrices = 4; // This should be either 2, 4, 8, 16, or 32
		int threadCount = 31; // This should match the cores used by the TCPServer
		String hostName = "localhost";
		Socket socket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try {

			socket = new Socket(hostName, portNumber);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());

		} catch (IOException e) {
			System.err.println("Couldn't connect to router: " + hostName);
			System.exit(1);
		}

		System.out.println("Connected to ServerRouter.");


		// Generate matrices
		List<int[][]> fromClient = new ArrayList<>();
		generateMatrices(fromClient, matrixSize, numberOfMatrices);

		// First calculation (single thread)
		System.out.println("Client: Sending " + numberOfMatrices + ", " + matrixSize + " x " + matrixSize + " matrices for single-threaded calculation.");
		long singleThreadStart = System.nanoTime();
		out.writeObject(fromClient);

		int[][] singleThreadResult = (int[][]) in.readObject();
		long singleThreadEnd = System.nanoTime();

		// Reconnect for second calculation
		try {
			socket = new Socket(hostName, portNumber);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.err.println("Couldn't reconnect to router: " + hostName);
			System.exit(1);
		}

		// Second calculation (multi-thread)
		System.out.println("Client: Sending " + numberOfMatrices + ", " + matrixSize + " x " + matrixSize + " matrices for multi-threaded calculation with " + threadCount + " threads.");
		long multiThreadStart = System.nanoTime();
		out.writeObject(fromClient);

		int[][] multiThreadResult = (int[][]) in.readObject();
		long multiThreadEnd = System.nanoTime();

		// Calculate timings including network overhead
		double singleThreadTime = (singleThreadEnd - singleThreadStart) / 1_000_000.0;
		double multiThreadTime = (multiThreadEnd - multiThreadStart) / 1_000_000.0;

		// Print timing results
		System.out.println("\nClient-side Performance Results:");
		System.out.println("Single-threaded total time: " + singleThreadTime + " ms");
		System.out.println("Multi-threaded total time: " + multiThreadTime + " ms");
		System.out.println("Overall speedup: " + String.format("%.2fx", singleThreadTime / multiThreadTime));
		System.out.println("Efficiency: " + String.format("%.2fx", singleThreadTime / (multiThreadTime * threadCount)));

		// Verify results match
		boolean resultsMatch = compareMatrices(singleThreadResult, multiThreadResult);
		System.out.println("\nResults match: " + resultsMatch);

		// Optional: Print matrices for verification (commented out for large matrices)
		/*
        System.out.println("\nSingle-threaded result:");
        printMatrix(singleThreadResult, matrixSize);
        System.out.println("\nMulti-threaded result:");
        printMatrix(multiThreadResult, matrixSize);
		 */

		// Clean up
		out.close();
		in.close();
		socket.close();
	}

	private static void generateMatrices(List<int[][]> fromClient, int size, int matrixes) {
		for (int i = 0; i < matrixes; i++) {
			int[][] matrix = new int[size][size];
			Random rand = new Random();

			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					matrix[j][k] = rand.nextInt(5); // Random small values to ensure it doesn't get out of hand
				}
			}

			fromClient.add(matrix);
		}
	}

	private static void printMatrix(int[][] matrix, int size) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}

	private static boolean compareMatrices(int[][] matrix1, int[][] matrix2) {
		if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length) {
			return false;
		}

		for (int i = 0; i < matrix1.length; i++) {
			for (int j = 0; j < matrix1[0].length; j++) {
				if (matrix1[i][j] != matrix2[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
}

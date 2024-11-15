import java.io.*;
import java.net.*;
import java.util.*;

public class SThread extends Thread {
	private Socket clientSocket;
	private Socket serverSocket;
	private PrintWriter clientOut;
	private BufferedReader clientIn;

	public SThread(Socket clientSocket, Socket serverSocket) {
		this.clientSocket = clientSocket;
		this.serverSocket = serverSocket;
	}

	public void run() {
		try {
			clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
			clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String fromClient = clientIn.readLine();
			if ("MULTIPLY MATRICES".equals(fromClient)) {
				List<int[][]> matrices = readMatricesFromFile("file.txt");
				if (matrices.size() >= 2) {
					int[][] result = MatrixUtils.strassenMultiply(matrices.get(0), matrices.get(1));
					clientOut.println(matrixToString(result));
				} else {
					clientOut.println("Error: Not enough matrices found in file.");
				}
			}
		} catch (IOException e) {
			System.err.println("Communication error.");
			e.printStackTrace();
		} finally {
			try {
				clientIn.close();
				clientOut.close();
				clientSocket.close();
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("Couldn't close connection.");
			}
		}
	}

	private List<int[][]> readMatricesFromFile(String filename) throws IOException {
		List<int[][]> matrices = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			List<int[]> currentMatrix = new ArrayList<>();
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) {
					if (!currentMatrix.isEmpty()) {
						matrices.add(currentMatrix.toArray(new int[0][]));
						currentMatrix.clear();
					}
				} else {
					currentMatrix.add(Arrays.stream(line.split("\\s+")).mapToInt(Integer::parseInt).toArray());
				}
			}
			if (!currentMatrix.isEmpty()) {
				matrices.add(currentMatrix.toArray(new int[0][]));
			}
		}
		return matrices;
	}

	private String matrixToString(int[][] matrix) {
		StringBuilder sb = new StringBuilder();
		for (int[] row : matrix) {
			sb.append(Arrays.toString(row)).append("\n");
		}
		return sb.toString();
	}
}

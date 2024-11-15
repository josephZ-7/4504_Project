import java.net.*;
import java.io.*;
import java.util.List;

public class SThread extends Thread {
	private Socket clientSocket;
	private Socket serverSocket;

	private ObjectOutputStream clientOut;
	private ObjectInputStream clientIn;

	private ObjectOutputStream serverOut;
	private ObjectInputStream serverIn;


	public SThread(Socket clientSocket, Socket serverSocket) {
		this.clientSocket = clientSocket;
		this.serverSocket = serverSocket;
	}

	public void run() {
		try {
			clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
			clientIn = new ObjectInputStream(clientSocket.getInputStream());

			List<int[][]> fromClient;
			int[][] toClient;

			serverOut = new ObjectOutputStream(serverSocket.getOutputStream());
			serverIn = new ObjectInputStream(serverSocket.getInputStream());

			int[][] fromServer;
			List<int[][]> toServer;

			fromClient = (List<int[][]>) clientIn.readObject();

			if(fromClient != null){
				System.out.println("Thread: Received message from Client.");
			}

			toServer = fromClient;

			System.out.println("Thread: Forwarding message to Server.");
			serverOut.writeObject(toServer);

			/////

			fromServer = (int[][]) serverIn.readObject();

			if (fromServer != null) {
				System.out.println("Thread: Received response from Server.");
			}

			toClient = fromServer;

			System.out.println("Thread: Forwarding response to Client.");
			clientOut.writeObject(toClient);


		} catch (IOException e) {
			System.err.println("Error in communication.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
			try {
				clientIn.close();
				clientOut.close();
				clientSocket.close();
				serverIn.close();
				serverOut.close();
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("Couldn't close connection properly.");
			}
		}
	}
}

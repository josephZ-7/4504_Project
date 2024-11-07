import java.net.*;
import java.io.*;

public class SThread extends Thread {
	private Socket clientSocket;
	private Socket serverSocket;
	private Object[][] routingTable;

	private PrintWriter clientOut;
	private BufferedReader clientIn;

	private PrintWriter serverOut;
	private BufferedReader serverIn;

	public SThread(Socket clientSocket, Socket serverSocket) {
		this.clientSocket = clientSocket;
		this.serverSocket = serverSocket;
		this.routingTable = routingTable;
	}

	public void run() {
		try {
			clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
			clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String fromClient;
			String toClient;

			serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
			serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

			String fromServer;
			String toServer;

			fromClient = clientIn.readLine();

			if(fromClient != null){
				System.out.println("Thread: Received message from Client -> " + fromClient);
			}

			toServer = fromClient;

			System.out.println("Thread: Forwarding message to Server ->" + toServer);
			serverOut.println(toServer);

			/////

			fromServer = serverIn.readLine();

			if (fromServer != null) {
				System.out.println("Thread: Received message from Server -> " + fromServer);
			}

			toClient = fromServer;

			System.out.println("Thread: Forwarding message to Client -> " + toClient);
			clientOut.println(toClient);
		} catch (IOException e) {
			System.err.println("Error in communication.");
			e.printStackTrace();
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

import java.net.*;
import java.io.*;

public class TCPServerRouter {
    public static void main(String[] args) throws IOException {
        Socket clientSocket = null;
        int clientPortNum = 55555;
        Socket serverSocket = null;
        int serverPortNum = 55556;
        boolean running = true;

        ServerSocket clientListener = new ServerSocket(clientPortNum);
        System.out.println("Router is Listening for Clients on port: " + clientPortNum);

        ServerSocket serverListener = new ServerSocket(serverPortNum);
        System.out.println("Router is Listening for Servers on port: " + serverPortNum);

        while (running) {
            try {

                clientSocket = clientListener.accept();
                System.out.println("Accepted client connection.");

                serverSocket = serverListener.accept();
                System.out.println("Accepted server connection.");

                System.out.println("Commencing communication...");
                SThread t = new SThread(clientSocket, serverSocket);
                t.start();

            } catch (IOException e) {
                System.err.println("Client/Server failed to connect.");
                e.printStackTrace();
                System.exit(1);
            }
        }

        clientSocket.close();
        serverSocket.close();

    }
}

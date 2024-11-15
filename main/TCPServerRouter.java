import java.io.*;
import java.net.*;

public class TCPServerRouter {
    public static void main(String[] args) throws IOException {
        int clientPortNum = 55555;
        int serverPortNum = 55556;

        ServerSocket clientListener = new ServerSocket(clientPortNum);
        ServerSocket serverListener = new ServerSocket(serverPortNum);

        System.out.println("Router is listening for Clients on port: " + clientPortNum);
        System.out.println("Router is listening for Servers on port: " + serverPortNum);

        while (true) {
            try {
                Socket clientSocket = clientListener.accept();
                Socket serverSocket = serverListener.accept();

                System.out.println("Accepted client connection.");
                System.out.println("Accepted server connection.");

                new SThread(clientSocket, serverSocket).start();
            } catch (IOException e) {
                System.err.println("Client/Server failed to connect.");
                e.printStackTrace();
            }
        }
    }
}

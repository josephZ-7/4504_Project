import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        int portNumber = 55556;
        String hostName = "localhost";
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(hostName, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Couldn't connect to router: " + hostName);
            System.exit(1);
        }

        System.out.println("Connected to ServerRouter.");

        // Send request to ServerRouter to initiate matrix multiplication
        String request = "MULTIPLY MATRICES";
        out.println(request);

        // Read response from ServerRouter
        String fromServer;
        while ((fromServer = in.readLine()) != null) {
            System.out.println("Result from Server:\n" + fromServer);
        }

        out.close();
        in.close();
        socket.close();
    }
}

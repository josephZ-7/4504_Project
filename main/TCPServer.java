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

        String fromRouter;
        String fromServer;

        fromRouter = in.readLine();  // Receive Client message from Router
        if (fromRouter != null) {
            System.out.println("Server: Received message -> " + fromRouter);
        }

        assert fromRouter != null;
        fromServer = fromRouter.toUpperCase();

        System.out.println("Server: Sending message -> " + fromServer);
        out.println(fromServer);


        out.close();
        in.close();
        socket.close();
    }
}

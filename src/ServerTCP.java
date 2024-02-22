import java.io.*;
import java.net.*;

public class ServerTCP {
    public static void main(String[] args) throws Exception {

        // checks port number was provided as arg
        if (args.length != 1) throw new IllegalArgumentException("Parameter(s): <Port>");

        //receiving port
        int port = Integer.parseInt(args[0]);

        ServerSocket servSock = new ServerSocket(port);
        Socket clientSock = servSock.accept();

        RequestDecoder decoder = new RequestDecoderBin();
        Request receivedRequest = decoder.decodeRequest(clientSock.getInputStream());

        System.out.println("Received UTF-16BE-Encoded Request");
        System.out.println(receivedRequest);

        clientSock.close();
        servSock.close();
    }
}
//        try {
//            ServerSocket serverSocket = new ServerSocket(6789); // Create server socket with port 6789
//            System.out.println("Server started...");
//
//            while (true) {
//                Socket clientSocket = serverSocket.accept(); // Wait for a client connection
//                System.out.println("Client connected: " + clientSocket);
//
//                // Create input and output streams for the client socket
//                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
//
//                // Read data from client
//                String clientMessage = inFromClient.readLine();
//                System.out.println("Received from client: " + clientMessage);
//
//                // Convert received message to uppercase and send it back to client
//                String capitalizedMessage = clientMessage.toUpperCase() + '\n';
//                outToClient.writeBytes(capitalizedMessage);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
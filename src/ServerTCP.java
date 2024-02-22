import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;

public class ServerTCP {

    static int tml;
    static int requestResult;
    static int errorCode;
    static int requestID;
    static int port;
    static Response serverResponse;
    public static void main(String[] args) throws Exception {

        // Checks port number was provided as arg
        if (args.length != 1) throw new IllegalArgumentException("Parameter(s): <Port>");

        // Receiving port
        port = Integer.parseInt(args[0]);

        // Initialize socket and accept client connection
        ServerSocket serverSock = new ServerSocket(port);
        Socket clientSock = serverSock.accept();

        RequestEncoder encoder = new RequestEncoderBin();       // Initialize Request Encoder
        RequestDecoder decoder = new RequestDecoderBin();       // Initialize Response Decoder


        // Server Information
        InetAddress thisServer = InetAddress.getLocalHost();
        System.out.println("\n\n");
        System.out.println("+++++++++++++++++ SERVER INFO +++++++++++++++++");
        System.out.println("+ ServerTCP.status: Initialized               +");
        System.out.printf("+ ServerTCP.HostName: %-24s+%n", thisServer.getHostName());
        System.out.printf("+ ServerTCP.HostAddress: %-21s+%n", thisServer.getHostAddress());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");

        System.out.println();
        while (!clientSock.isClosed()) {
            System.out.println("Waiting for client request...\n\n");
            // wait for client request
            InputStream requestStream = clientSock.getInputStream();
            DataInputStream encodedRequest = new DataInputStream(requestStream);
            System.out.println("Client Request Received");

            // decode client request
            System.out.println("Initiating decoding sequence...\n");
            Request clientRequest = decoder.decodeRequest(requestStream);

            // display the request byte by byte
            System.out.println("================== REQUEST DATA ==================");
            String[] requestHex = new String[clientRequest.tml];
            for (int i = 0; i < clientRequest.tml; i++) {
                requestHex[i] = String.format("%02X", encodedRequest.readByte());
            }
            System.out.println("\n==================================================\n");


            // perform client request calculation
            System.out.println("\nCalculating client request operation...");
            RequestCalc calcRequest = new RequestCalc(clientRequest);
            tml = 8;
            requestResult = calcRequest.calcResult;
            errorCode = (clientRequest.tml == requestStream.readAllBytes().length) ? 0 : 127;
            requestID = clientRequest.requestID;
            serverResponse = new Response(tml, requestResult, errorCode, requestID);
            System.out.println("Calculating complete\n");

            // display the client request in readable text
            System.out.println("========== CLIENT REQUEST ==========");
            System.out.printf("         Request ID: %d%n", requestID);
            System.out.printf("         Request: (%d %s %d)%n", calcRequest.operand1, calcRequest.opSymbol, calcRequest.operand2);
            System.out.println("====================================");

            // send client request results back to client
            byte[] codedResponse = encoder.encode(serverResponse);
            OutputStream out = clientSock.getOutputStream();
            out.write(codedResponse);


        };

        clientSock.close();
        serverSock.close();
    }
}

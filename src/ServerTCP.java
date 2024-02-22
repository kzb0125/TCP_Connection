import java.io.*;
import java.net.*;

public class ServerTCP {

    static int tml;
    static int requestResult;
    static int errorCode;
    static int requestID;
    static InetAddress clientAddr;
    static int port;
    static Response serverResponse;
    static int MAX_WIRE_LENGTH  = 41;

    public static void main(String[] args) throws Exception {

        // Checks port number was provided as arg
        if (args.length != 1) throw new IllegalArgumentException("Parameter(s): <Port>");

        // Receiving port
        port = Integer.parseInt(args[0]);

        // Initialize socket and accept client connection
        ServerSocket serverSock = new ServerSocket(port);

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

        System.out.println("\nListening for client socket connection...\n\n");

        while (true) {
            Socket clientSock = serverSock.accept();
            clientAddr = clientSock.getInetAddress();
            System.out.println("Client socket connecting...\n");
            System.out.println("\n=========== CLIENT SOCKET ===========");
            System.out.println("      Status: Connected       ");
            System.out.printf("      Address: %s%n", clientAddr);
            System.out.printf("         Port: %s%n", port);
            System.out.println("=====================================\n");

            System.out.println("Waiting for client request...\n\n");
            // wait for client request
            InputStream inputStream = clientSock.getInputStream();
            byte[] inputBytes = new byte[MAX_WIRE_LENGTH];
            int bytesRead;
            while ((bytesRead = inputStream.read(inputBytes)) != -1) {
                // store client request into an encoded byte[]
                byte[] encodedBytes = new byte[bytesRead];
                System.arraycopy(inputBytes, 0, encodedBytes, 0, encodedBytes.length);

                // decode client request
                ByteArrayInputStream byteInputStream = new ByteArrayInputStream(encodedBytes);
                System.out.println("Initiating decoding sequence...\n");
                Request clientRequest = decoder.decodeRequest(byteInputStream);

                // display the request byte by byte
                System.out.println("================== REQUEST DATA ==================");
                String[] requestHex = new String[encodedBytes.length];
                for (int j = 0; j < encodedBytes.length; j++) {
                    requestHex[j] = String.format("%02X", encodedBytes[j]);
                    System.out.printf("%s ", requestHex[j]);
                }
                System.out.println("\n==================================================\n");


                // perform client request calculation
                System.out.println("\nCalculating client request operation...\n");
                RequestCalc calcRequest = new RequestCalc(clientRequest);
                tml = 8;
                requestResult = calcRequest.calcResult;
                errorCode = (clientRequest.tml == encodedBytes.length) ? 0 : 127;
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
                System.out.println("\nResults sent to Client\n\n");
                System.out.println("***************************************************\n\n");
                System.out.println("Waiting for client request...\n\n");
            }
            System.out.println("Client socket disconnecting...\n\n");
            System.out.println("=========== CLIENT SOCKET ===========");
            System.out.println("      Status: Disconnected       ");
            System.out.printf("      Address: %s%n", clientAddr);
            System.out.printf("         Port: %s%n", port);
            System.out.println("=====================================\n\n");
            clientSock.close();
            System.out.println("***************************************************\n\n");
            System.out.println("Listening for client socket connection...\n\n");

        }
    }
}

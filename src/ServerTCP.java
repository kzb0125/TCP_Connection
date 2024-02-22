import javax.xml.crypto.Data;
import java.io.*;
import java.lang.reflect.GenericDeclaration;
import java.net.*;
import java.util.Arrays;

public class ServerTCP {

    static int tml;
    static int requestResult;
    static int errorCode;
    static int requestID;
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
        while (true) {          //!clientSock.isClosed()
            System.out.println("Waiting for client request...\n\n");
            // wait for client request
            InputStream inputStream = clientSock.getInputStream();
            // store client request into temp byte[]
            byte[] inputBytes = new byte[MAX_WIRE_LENGTH];
            int bytesRead = inputStream.read(inputBytes);
            byte[] encodedBytes = new byte[bytesRead];
            System.arraycopy(inputBytes, 0, encodedBytes, 0, encodedBytes.length);
            System.out.println(Arrays.toString(encodedBytes));

            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(encodedBytes);
            /*
            ByteArrayInputStream payloadIn = new ByteArrayInputStream(clientSock.getInputStream().readAllBytes());
            payloadIn.mark(1);

            System.out.println(payloadIn.markSupported());

            System.out.println("available: " + payloadIn.available());
            int inputSize = payloadIn.available();
            System.out.println("inputSize: " + inputSize);

            byte[] encodedBytes = new byte[inputSize];
            int byteLength = payloadIn.readNBytes(encodedBytes,0,inputSize);

            System.out.println("byteLength: " + byteLength);
            System.out.println(Arrays.toString(encodedBytes));
            */
            // decode client request
            System.out.println("Initiating decoding sequence...\n");
            //payloadIn.reset();
            Request clientRequest = decoder.decodeRequest(byteInputStream);
            System.out.println("clientRequest.TML: " + clientRequest.tml);
            System.out.println("clientRequest.opLen: " + clientRequest.opNameLength);

            // display the request byte by byte
            System.out.println("================== REQUEST DATA ==================");
            String[] requestHex = new String[encodedBytes.length];
            for (int j = 0; j < encodedBytes.length; j++) {
                requestHex[j] = String.format("%02X", encodedBytes[j]);
                System.out.printf("%s ", requestHex[j]);
            }
            System.out.println("\n==================================================\n");


            // perform client request calculation
            System.out.println("\nCalculating client request operation...");
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


        }

        //clientSock.close();
        //serverSock.close();
    }
}

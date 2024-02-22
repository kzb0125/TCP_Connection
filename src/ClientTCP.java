import java.io.*;
import java.net.*;
import java.util.*;

public class ClientTCP {
    static Scanner scanInput = new Scanner(System.in);
    static Random rand = new Random();
    static int tml = 0;
    static int opCode = 0;
    static int operand1 = 0;
    static int operand2 = 0;
    static int requestID = rand.nextInt(65536);
    static int opNameLength = 0;
    static String opName = "";
    static HashMap<Integer, String> opCodeMap;
    static double totalTime;
    static double responseTime;
    static double maxTime = Double.MIN_VALUE;
    static double minTime = Double.MAX_VALUE;
    static double avgTime = 0.;
    static int numRequest = 0;
    static Socket sock;
    static int quit = 0;

    public static void main(String[] args) throws Exception {

        // check server name and port number was provided as arg
        if (args.length != 2)
            throw new IllegalArgumentException("Parameter(s): <Destination> <Port>");

        InetAddress destAddr = InetAddress.getByName(args[0]);  // Destination Address
        int destPort = Integer.parseInt(args[1]);               // Destination Port

        sock = new Socket(destAddr, destPort);                  // Initialize socket connection
        RequestEncoder encoder = new RequestEncoderBin();       // Initialize Request Encoder
        RequestDecoder decoder = new RequestDecoderBin();       // Initialize Response Decoder

        initializeOpCodeMap();                                  // initialize <OpCode,OpName> Map
        System.out.println("\n");
        System.out.println("+++++++++++++++++ CLIENT INFO +++++++++++++++++");
        System.out.println("+ ClientTCP.status: Connected                 +");
        System.out.printf("+ ClientTCP.LocalPort: %-22s +%n", sock.getLocalPort());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++\n");

        do {
            getRequestInfo();                                           // Prompt user for operation Request info
            if (quit == 1) { exitClient();}

            // create clientRequest object
            Request clientRequest = new Request(tml, opCode, operand1, operand2, requestID, opNameLength, opName);

            // Encode the request and convert to encoded byte[]
            byte[] codedRequest = encoder.encode(clientRequest);        // Encode client request

            // client Request data
            System.out.println("==================== CLIENT REQUEST =================================");
            System.out.printf("TML: %d\n", tml);
            System.out.printf("OPCODE: %d\n", opCode);
            System.out.printf("OPERAND1: %d\n", operand1);
            System.out.printf("OPERAND2: %d\n", operand2);
            System.out.printf("REQUESTID: %d\n", requestID);
            System.out.printf("OPNAMELEN: %d\n", opNameLength);
            System.out.printf("OPNAME: %s\n", opName);
            System.out.println("\n===================================================================\n");

            // display request data as hexadecimal
            System.out.println("==================== REQUEST DATA =================================");
            String[] requestHex = new String[codedRequest.length];
            for (int i = 0; i < codedRequest.length; i++) {
                requestHex[i] = String.format("%02X", codedRequest[i]);
                System.out.printf("%s ", requestHex[i]);
            }
            System.out.println("\n===================================================================\n");

            // send the encoded request to server
            long startTime = System.nanoTime();
            OutputStream out = sock.getOutputStream();                  // Initialize output stream handler
            out.write(codedRequest);                                    // Send client request to ServerTCP


            // wait for server response
            InputStream byteServerResponse = sock.getInputStream();

            // response time as ms
            long endTime = System.nanoTime();
            numRequest += 1;
            responseTime = (endTime - startTime) / 1000000.;
            totalTime += responseTime;
            if (responseTime > maxTime) {maxTime = responseTime;}
            if (responseTime < minTime) {minTime = responseTime;}
            avgTime = totalTime/numRequest;

            // display server reply as hexadecimal
            System.out.println("======== REPLY DATA ========");
            byte[] encodedReply = new byte[8];
            int bytesRead = byteServerResponse.read(encodedReply);
            String[] replyHex = new String[bytesRead];
            for (int i = 0; i < encodedReply.length; i++) {
                replyHex[i] = String.format("%02X", encodedReply[i]);
                System.out.printf("%s ", replyHex[i]);
            }
            System.out.println("\n============================");


            // decode server reply
            ByteArrayInputStream codedResponse = new ByteArrayInputStream(encodedReply);
            Response serverResponse = decoder.decodeResponse(codedResponse);

            // display the server reply in readable text
            System.out.println("\n=========== SERVER REPLY ===========");
            System.out.printf("Request ID: %d%n", serverResponse.requestID);
            System.out.printf("Result: %d%n", serverResponse.result);
            System.out.println("Error Code: " + ((serverResponse.errorCode == 0) ? "OK" : "127"));
            System.out.printf("Response Time: %.3f ms%n", responseTime);
            System.out.printf("min_Time: %.3f ms%n", minTime);
            System.out.printf("avg_Time: %.3f ms%n", avgTime);
            System.out.printf("max_Time: %.3f ms%n", maxTime);
            System.out.println("====================================\n");

            // New request or quit (numTries = 3)
            int numTries = 3;
            while(true) {
                try {
                    System.out.println("NEW REQUEST: 'Y'");
                    System.out.println("       QUIT: 'Q'");
                    System.out.println("----------------");
                    System.out.print("Enter Input: ");
                    String userInput = scanInput.next();
                    userInput = userInput.toUpperCase();
                    if(userInput.equals("Y")) {break;
                    } else if (userInput.equals("Q")) { quit = 1; break;
                    } else {throw new IllegalArgumentException();}
                } catch (IllegalArgumentException | NullPointerException e) {
                    if(--numTries < 1) {
                        System.out.println("Maximum number of tries attempted (3): \nClientUDP closing...");
                        quit = 1;
                        break;
                    }
                    System.out.println("\n****** INVALID INPUT ******");
                    System.out.println("Enter a valid input: Y || Q\n");
                }

            }

        } while (quit == 0);
    }

    // get userInput
    public static void getRequestInfo() {
        opCodeTable();
        int maxTries = 3;
        boolean opCodeinput = false;
        while(!opCodeinput) {
            System.out.print("Enter OpCode: ");
            try {
                List<Integer> validInput = Arrays.asList(0,1,2,3,4,5);
                opCode = scanInput.nextInt();
                if (validInput.contains(opCode)) {
                    maxTries = 3;
                    opCodeinput = true;
                } else {
                    maxTries--;
                    if(maxTries <1){throw new NoSuchElementException();}
                    System.out.println("\n****** INVALID INPUT ******");
                }
            }
            catch (NoSuchElementException e){
                if(--maxTries < 1) {
                    System.out.println("Maximum number of tries attempted (3): \nClientUDP closing...");
                    quit = 1;
                    opCodeinput = false;
                    break;
                }
                System.out.println("\n****** INVALID INPUT ******");
                scanInput.nextLine();
            }
        }
        boolean op1Input = false;
        while(!op1Input) {
            System.out.print("Enter 1st Operand: ");
            try {
                operand1 = scanInput.nextInt();
                if (operand1 != Integer.MIN_VALUE || operand1 != Integer.MAX_VALUE) {
                    maxTries = 3;
                    op1Input = true;
                } else {
                    maxTries--;
                    if(maxTries <1){throw new NoSuchElementException();}
                    System.out.println("\n****** INVALID INPUT ******");
                }
            } catch (NoSuchElementException e) {
                if (--maxTries < 1) {
                    System.out.println("Maximum number of tries attempted (3): \nClientUDP closing...");
                    quit = 1;
                    op1Input = true;
                    break;
                }
                System.out.println("\n****** INVALID INPUT ******");
                scanInput.nextLine();
            }
        }
        boolean op2Input = false;
        while(!op2Input) {
            System.out.print("Enter 2nd Operand: ");
            try {
                operand2 = scanInput.nextInt();
                if (operand2 != Integer.MIN_VALUE || operand2 != Integer.MAX_VALUE) {
                    maxTries = 3;
                    op2Input = true;
                } else {
                    maxTries--;
                    if(maxTries <1){throw new NoSuchElementException();}
                    System.out.println("\n****** INVALID INPUT ******");
                }
            } catch (NoSuchElementException e) {
                if (--maxTries < 1) {
                    System.out.println("Maximum number of tries attempted (3): \nClientUDP closing...");
                    quit = 1;
                    op2Input = true;
                    break;
                }
                System.out.println("\n****** INVALID INPUT ******");
                scanInput.nextLine();
            }
        }
        requestID += 1;
        opName = opCodeMap.get(opCode);
        opNameLength = opName.length() * 2;              //get length of opName * 2 -> 2 bytes per character
        tml = 13 + opNameLength;
        System.out.println();
    }

    // displays opCode as readable Table for user
    private static void opCodeTable() {
        System.out.printf("--------------------------------------%n");
        System.out.printf("|        Operation Code Table        |%n");
        System.out.printf("--------------------------------------%n");
        System.out.printf("| %-10s | %1s | %1s | %1s | %1s | %1s | %1s |%n", "Operation", "*", "/", "|", "&", "-", "+");
        System.out.printf("--------------------------------------%n");
        System.out.printf("| %-10s | %1d | %1d | %1d | %1d | %1d | %1d |%n", "OpCode", 0, 1, 2, 3, 4, 5);
        System.out.printf("--------------------------------------%n%n");
    }

    // initialize a hash map < # : opName >
    private static void initializeOpCodeMap() {
        opCodeMap = new HashMap<>(7);
        opCodeMap.put(0, "multiplication");
        opCodeMap.put(1, "division");
        opCodeMap.put(2, "or");
        opCodeMap.put(3, "and");
        opCodeMap.put(4, "subtraction");
        opCodeMap.put(5, "addition");
    }

    private static void exitClient() throws IOException {
//        OutputStream out = sock.getOutputStream();                  // Initialize output stream handler
//        out.write(-1);
        sock.close();
        System.out.println("\n ***** ClientTCP Disconnected *****\n");
    }
}


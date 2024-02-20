import java.io.*;  // for ByteArrayInputStream
import java.net.*; // for DatagramPacket

public class RequestDecoderBin implements RequestDecoder, RequestBinConst {

    private String encoding;  // Character encoding

    public RequestDecoderBin() {
        encoding = DEFAULT_ENCODING;
    }

    public RequestDecoderBin(String encoding) {
        this.encoding = encoding;
    }

    public Request decodeRequest(InputStream wire) throws IOException {
        DataInputStream src = new DataInputStream(wire);
        int tml = src.readByte();
        int opCode = src.readByte();
        int operand1 = src.readInt();
        int operand2 = src.readInt();
        int requestID = src.readChar();
        int opNameLength = src.readByte();

        //Deal with the Op Name
        int stringLength = src.read(); // Returns an unsigned byte as an int
        if (stringLength == -1)
            throw new EOFException();
        byte[] stringBuf = new byte[stringLength];
        src.readFully(stringBuf);
        String opName = new String(stringBuf, encoding);

        return new Request(tml,opCode, operand1, operand2, requestID, opNameLength, opName);
    }

    public Request decodeRequest(DatagramPacket p) throws IOException {
        ByteArrayInputStream payload =
                new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
        return decodeRequest(payload);
    }

    public Response decodeResponse(InputStream wire) throws IOException {
        DataInputStream src = new DataInputStream(wire);
        int tml = src.readByte();
        int result = src.readInt();
        int errorCode = src.readByte();
        int requestID = src.readChar();

        return new Response(tml, result, errorCode, requestID);
    }
    public Response decodeResponse(DatagramPacket p) throws IOException {
        ByteArrayInputStream payload =
                new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
        return decodeResponse(payload);
    }
}
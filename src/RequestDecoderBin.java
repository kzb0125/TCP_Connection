import java.io.*;  // for ByteArrayInputStream

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
        byte[] stringBuf = new byte[opNameLength];
        src.readFully(stringBuf);
        String opName = new String(stringBuf, encoding);
        return new Request(tml,opCode, operand1, operand2, requestID, opNameLength, opName);
    }

    public Response decodeResponse(InputStream wire) throws IOException {
        DataInputStream src = new DataInputStream(wire);
        int tml = src.readByte();
        int result = src.readInt();
        int errorCode = src.readByte();
        int requestID = src.readChar();

        return new Response(tml, result, errorCode, requestID);
    }

}

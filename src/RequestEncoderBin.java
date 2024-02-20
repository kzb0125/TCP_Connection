import java.io.*;  // for ByteArrayOutputStream and DataOutputStream

public class RequestEncoderBin implements RequestEncoder, RequestBinConst {

    private String encoding;  // Character encoding
    public int size;

    public RequestEncoderBin() {
        encoding = DEFAULT_ENCODING;
    }

    public RequestEncoderBin(String encoding) {
        this.encoding = encoding;
    }

    public byte[] encode(Request clientRequest) throws Exception {

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        out.writeByte(clientRequest.tml);
        out.writeByte(clientRequest.opCode);
        out.writeInt(clientRequest.operand1);
        out.writeInt(clientRequest.operand2);
        out.writeChar(clientRequest.requestID);
        out.writeByte(clientRequest.opNameLength);

        byte[] encodedOpName = clientRequest.opName.getBytes(encoding);
        if (encodedOpName.length > MAX_OPNAME_LEN)
            throw new IOException("Request Op Name exceeds encoded length limit");
        out.write(encodedOpName, 0, encodedOpName.length);
        size = out.size();
        out.flush();
        return buf.toByteArray();
    }

    public byte[] encode(Response serverResponse) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        out.writeByte(serverResponse.tml);
        out.writeInt(serverResponse.result);
        out.writeByte(serverResponse.errorCode);
        out.writeChar(serverResponse.requestID);
        out.flush();
        return buf.toByteArray();
    }
}

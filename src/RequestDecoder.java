import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;

public interface RequestDecoder {
    Request decodeRequest(InputStream source) throws IOException;
    Request decodeRequest(DatagramPacket packet) throws IOException;
    Response decodeResponse(InputStream source) throws IOException;
    Response decodeResponse(DatagramPacket packet) throws IOException;
}

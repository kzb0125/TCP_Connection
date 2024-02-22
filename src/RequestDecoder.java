import java.io.IOException;
import java.io.InputStream;

public interface RequestDecoder {
    Request decodeRequest(InputStream source) throws IOException;
    Response decodeResponse(InputStream source) throws IOException;
}

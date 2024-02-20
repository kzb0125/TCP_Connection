public interface RequestEncoder {
    byte[] encode(Request clientRequest) throws Exception;
    byte[] encode(Response serverResponse) throws Exception;
}

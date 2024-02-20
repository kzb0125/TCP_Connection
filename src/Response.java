public class Response {
    public int tml;
    public int result;
    public int errorCode;
    public int requestID;

    public Response(int tml_in, int result_in, int errorCode_in, int requestID_in) {
        this.tml = tml_in;
        this.result = result_in;
        this.errorCode = errorCode_in;
        this.requestID = requestID_in;
    }
}

public class Request {
    public int tml;
    public int opCode;
    public int operand1;
    public int operand2;
    public int requestID;
    public int opNameLength;
    public String opName;

    public Request(int tml_in, int opCode_in, int operand1_in, int operand2_in, int requestID_in, int opNameLength_in, String opName_in) {
        this.tml = tml_in;
        this.opCode = opCode_in;
        this.operand1 = operand1_in;
        this.operand2 = operand2_in;
        this.requestID = requestID_in;
        this.opNameLength = opNameLength_in;
        this.opName = opName_in;
    }
}

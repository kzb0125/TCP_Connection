public class RequestCalc {
    int opCode;
    int operand1;
    int operand2;
    int calcResult;
    String opSymbol;
    public RequestCalc(Request serverRequest) {
        this.opCode = serverRequest.opCode;
        this.operand1 = serverRequest.operand1;
        this.operand2 = serverRequest.operand2;

        // perform calculation on initialization
        performCalc();
    }

    private void performCalc() {
        switch (opCode) {
            case 0: // multiplication
                calcResult = operand1 * operand2;
                opSymbol = "*";
                break;
            case 1: // division
                calcResult = operand1 / operand2;
                opSymbol = "/";
                break;
            case 2: // bitwise OR
                calcResult = operand1 | operand2;
                opSymbol = "|";
                break;
            case 3: // bitwise AND
                calcResult = operand1 & operand2;
                opSymbol = "&";
                break;
            case 4: // subtraction
                calcResult = operand1 - operand2;
                opSymbol = "-";
                break;
            case 5: // addition
                calcResult = operand1 + operand2;
                opSymbol = "+";
                break;
            default: throw new IllegalStateException("Unexpected Op Code: " + opCode);
        }
    }
}

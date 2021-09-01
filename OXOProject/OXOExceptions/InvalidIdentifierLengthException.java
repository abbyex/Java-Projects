package OXOExceptions;

public class InvalidIdentifierLengthException extends InvalidIdentifierException{

    public InvalidIdentifierLengthException(int invalid, int length){
        super(invalid, length);
        toString();
    }

    public String toString(){
        int length = getColumn();
        return "InvalidIdentifierLengthException: The length of the cell is " + length + "." + " Length must be equal to 2!";
    }

}

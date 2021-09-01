package OXOExceptions;

public class InvalidIdentifierCharacterException extends InvalidIdentifierException{

    public InvalidIdentifierCharacterException(int row, int col){
        super(row, col);
        toString();
    }

    public String toString(){
        char row = (char)getRow();
        char col = (char)getColumn();
        return "InvalidIdentifierCharacterException: The characters of the cell " + row + " , " + col + " are invalid!";
    }

}

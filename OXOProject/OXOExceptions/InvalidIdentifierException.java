package OXOExceptions;

public class InvalidIdentifierException extends CellDoesNotExistException{

    public InvalidIdentifierException(int row, int col){
        super(row, col);
        toString();
    }

    public String toString(){
        char row = (char)getRow();
        char col = (char)getColumn();
        return "InvalidIdentifierException: The cell " + row + " , " + col + " is invalid!";
    }
}

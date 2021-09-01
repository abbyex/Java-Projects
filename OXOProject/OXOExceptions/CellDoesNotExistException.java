package OXOExceptions;

public class CellDoesNotExistException extends OXOMoveException{

    public CellDoesNotExistException(int row, int col){
        super(row, col);
        toString();
    }

    public String toString(){
        char row = (char)getRow();
        char col = (char)getColumn();
        return "CellDoesNotExistException: The cell " + row + " , " + col + " does not exist!";
    }

}

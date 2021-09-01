package OXOExceptions;

public class CellAlreadyTakenException extends OXOMoveException{

    public CellAlreadyTakenException(int row, int col){
        super(row, col);
        toString();
    }

    public String toString(){
        char row = (char)getRow();
        char col = (char)getColumn();
        return "CellAlreadyTakenException: That cell " + row + " , " +  col + " is already taken!";
    }


}

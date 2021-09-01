package OXOExceptions;

public class OutsideCellRangeException extends CellDoesNotExistException{

    public OutsideCellRangeException(int row, int col){
        super(row, col);
        toString();
    }

    public String toString(){
        char row = (char)getRow();
        char col = (char)getColumn();
       return "OutsideCellRangeException: That cell " +  row + " , " + col + " is outside the range of this board!";
    }

}

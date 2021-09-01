import OXOExceptions.*;
import java.lang.reflect.Array;

class OXOController
{
    OXOModel gameModel;
    public int curPlayerNumber = 0;
    private int gameCounter = 0;

    public int rowNumber;
    public int colNumber;

    private int colCounter;
    private int rowCounter;

    private final char[] NumberValueLowerLetters = {'a','b','c','d','e','f','g','h','i'};
    private final char[] NumberValueUpperLetters = {'A','B','C','D','E','F','G','H','I'};

    public OXOController(OXOModel model)
    {
        gameModel = model;
        whoseTurnIsItNext();
    }

    public void handleIncomingCommand(String command) throws OXOMoveException
    {
        lengthNotRight(command);
        setInputFromPlayer(command);
        characterNotRight(command);
        cellDoesNotExist();
        cellIsAlreadyTaken();
        gameModel.setCellOwner(gameModel.getCurrentRow(), gameModel.getCurrentCol(), gameModel.getCurrentPlayer());
        gameCounter++;

        checkForWinner();
        isItADraw();
        whoseTurnIsItNext();
    }

    public void setInputFromPlayer(String command)
    {
        //Sets the input from user into an array, and takes array[0] as Row, and array[1] as Column
        char[] inputArray = new char[command.length()];
        for (int i = 0; i < command.length(); i++) {
            inputArray[i] = command.charAt(i);
        }
        rowNumber = command.charAt(0);
        colNumber = command.charAt(1);
        int arraySize = NumberValueLowerLetters.length;
        char charRow = Array.getChar(inputArray, 0);
        char charCol = Array.getChar(inputArray, 1);

        //Changes Row and Column to an int with the same indexing as an array to make it easier to work
        // with (eg, Row A => 0, Col 1 => 0.)
        gameModel.setCurrentCol((Character.getNumericValue(charCol) - 1));
        for (int i = 0; i < arraySize; i++){
            if((charRow==NumberValueLowerLetters[i]) || (charRow==NumberValueUpperLetters[i])){
                gameModel.setCurrentRow(i);
            }
        }
    }

    public void whoseTurnIsItNext(){
        OXOPlayer curOXOPlayer = gameModel.getPlayerByNumber(curPlayerNumber);
        gameModel.setCurrentPlayer(curOXOPlayer);
        // Sets Players back to 0 once it's reached the end.
        if ((gameModel.getNumberOfPlayers() -1 )== curPlayerNumber){
            curPlayerNumber = 0;
        }
        else {
            curPlayerNumber++;
        }
    }

    public boolean checkForWinner(){
        if ((WinByRow()) || (WinByCol()) || (diagSetValueStr()) || diagSetValueRev()){
            gameModel.setWinner(gameModel.getCurrentPlayer());
        }
        return false;
    }

    public boolean WinByCol(){
        rowCounter = 0;
        int nextRow = rowCounter + 1;
        int winCheck = 1;
        while (winCheck < gameModel.getWinThreshold()) {
            if (nextRow == gameModel.getNumberOfRows()) {
                return false;
            }
            // Prevents a false win => eg a line of empty columns
                if ((!RowSet(rowCounter, gameModel.getCurrentCol())) || (!RowSet(nextRow, gameModel.getCurrentCol()))) {
                    rowCounter++;
                    nextRow++;
                    winCheck = 1;
                }
                else {
                    if ((gameModel.getCellOwner(rowCounter, gameModel.getCurrentCol())) ==
                            (gameModel.getCellOwner(nextRow, gameModel.getCurrentCol()))) {
                        winCheck++;
                    }
                    else{
                        winCheck = 1;
                    }
                    rowCounter++;
                    nextRow++;
                }
            }
        if (winCheck == gameModel.getWinThreshold()){
            return true;
        }
        return false;
    }

    public boolean WinByRow(){
        colCounter = 0;
        int nextCol = colCounter + 1;
        int winCheck = 1;
        while (winCheck < gameModel.getWinThreshold()) {
            if (nextCol == gameModel.getNumberOfColumns()) {
                return false;
            }
            // Prevents a false win => eg a line of empty rows
            if ((!RowSet(gameModel.getCurrentRow(), colCounter)) || (!RowSet(gameModel.getCurrentRow(), nextCol))) {
                colCounter++;
                nextCol++;
                winCheck = 1;
            }
            else {
                // If the cells have the same cell owner, continues checking
                if ((gameModel.getCellOwner(gameModel.getCurrentRow(), colCounter)) ==
                        (gameModel.getCellOwner(gameModel.getCurrentRow(), nextCol))) {
                    winCheck++;
                }
                else {
                    winCheck = 1;
                }
                colCounter++;
                nextCol++;
            }
        }
        if (winCheck == gameModel.getWinThreshold()){
            return true;
        }
        return false;
    }

    public boolean RowSet(int rowNumber, int colNumber){
        int numberOfPlayers = gameModel.getNumberOfPlayers();
        int initPlayer = 0;
        //Checks the row is owned by any of the players.
        while (initPlayer < numberOfPlayers){
            if ((gameModel.getCellOwner(rowNumber, colNumber)) == (gameModel.getPlayerByNumber(initPlayer))){
                return true;
            }
            initPlayer++;
        }
        return false;
    }


    public boolean diagSetValueStr(){
        int initCol = gameModel.getNumberOfColumns() - gameModel.getWinThreshold();
        int initRow = 0;
        int nextRow = initRow + 1;
        while (initCol != 0){
            int nextCol = initCol + 1;
            if (WinByStrDig(initCol, initRow, nextRow, nextCol)){
                return true;
            }
           initCol = initCol - 1;
            initRow = 0;

        }
        initCol = 0;
        initRow = gameModel.getNumberOfRows() - gameModel.getWinThreshold();
        int nextCol = initCol + 1;
        while (initRow != 0){
            nextRow = initRow + 1;
            if (WinByStrDig(initCol, initRow, nextRow, nextCol)){
                return true;
            }
            initRow = initRow - 1;
            initCol = 0;
        }
        initCol = 0;
        initRow = 0;
        nextRow = initRow + 1;
        nextCol = initCol + 1;
        if (WinByStrDig(initCol, initRow, nextRow, nextCol)){
            return true;
        }
        return false;
    }

    public boolean WinByStrDig(int initCol, int initRow, int nextRow, int nextCol){
        int winCheck = 1;
        while (winCheck < gameModel.getWinThreshold()) {
        if ((nextCol == gameModel.getNumberOfColumns()) || (nextRow == gameModel.getNumberOfRows())){
            return false;
        }
            // Prevents a false win => eg a line of empty cells
            if (!RowSet(initRow, initCol)){
                initRow++;
                initCol++;
                nextRow++;
                nextCol++;
                winCheck = 1;
            }
            else {
                // Compares the initial cells + the ones next to see if they are the same
                if ((gameModel.getCellOwner(initRow, initCol)) == (gameModel.getCellOwner(nextRow, nextCol))) {
                    winCheck++;
                }
                initRow++;
                initCol++;
                nextRow++;
                nextCol++;
            }
        }
        //It's a win if by the end the column & row count is the same as Win threshold
        if (winCheck == gameModel.getWinThreshold()){
            return true;
        }
        return false;
    }

    public boolean diagSetValueRev(){
        int initRow = gameModel.getWinThreshold() - 1;
        int initCol = 0;
        while (initRow != gameModel.getNumberOfRows()){
            int nextRow = initRow - 1;
            int nextCol = initCol + 1;
            if (WinByRevDig(initCol, initRow, nextRow, nextCol)){
                return true;
            }
            initRow = initRow + 1;
            initCol = 0;
        }
        initCol = 1;
        initRow = gameModel.getNumberOfRows() - 1;
        int nextCol = initCol + 1;
        while (nextCol != gameModel.getNumberOfColumns()){
            int nextRow = initRow - 1;
            if (WinByRevDig(initCol, initRow, nextRow, nextCol)){
                return true;
            }
            initRow = gameModel.getNumberOfRows() - 1;
            initCol = initCol + 1;
            nextCol = initCol + 1;
        }
        return false;
    }

    public boolean WinByRevDig(int initCol, int initRow, int nextRow, int nextCol){
        int winCheck = 1;
        while (winCheck < gameModel.getWinThreshold()) {
            if ((nextRow < 0) || (nextCol == gameModel.getNumberOfColumns())){
                return false;
            }
                // Prevents a false win => eg a line of empty cells
                if (!RowSet(initRow, initCol)){
                    initRow--;
                    nextRow--;
                    initCol++;
                    nextCol++;
                }
                else {
                    // Compares the initial cells + the ones next to see if they are the same
                    if ((gameModel.getCellOwner(initRow, initCol)) == (gameModel.getCellOwner(nextRow, nextCol))) {
                        winCheck++;
                    }
                    initRow--;
                    nextRow--;
                    initCol++;
                    nextCol++;
                }
            }
        //It's a win if by the end the column count is the same as Win threshold (since reverse diag)
        if (winCheck == gameModel.getWinThreshold()){
            return true;
        }
        return false;
    }

    public void isItADraw(){
        int boardSize = (gameModel.getNumberOfRows()) * (gameModel.getNumberOfColumns());
        if (gameCounter == boardSize){
            gameModel.setGameDrawn();
        }
    }

    public void cellIsAlreadyTaken() throws CellAlreadyTakenException {
        if (RowSet(gameModel.getCurrentRow(), gameModel.getCurrentCol())){
            throw new CellAlreadyTakenException(rowNumber, colNumber);
        }
    }

    public boolean cellDoesNotExist() throws CellDoesNotExistException{
        int totalRows = gameModel.getNumberOfRows();
        int totalCols = gameModel.getNumberOfColumns();
         if ((gameModel.getCurrentRow() >= totalRows) || (gameModel.getCurrentRow() < 0) ||
                 (gameModel.getCurrentCol() >= totalCols) || (gameModel.getCurrentCol() < 0)){
             if (!outsideCellRange()){
                 throw new CellDoesNotExistException(rowNumber, colNumber);
             }
         }
         return true;
    }

    public boolean outsideCellRange() throws OutsideCellRangeException {
        if ((gameModel.getCurrentRow() >= gameModel.getNumberOfRows()) || (gameModel.getCurrentCol() >= gameModel.getNumberOfColumns())){
            throw new OutsideCellRangeException(rowNumber, colNumber);
        }
        return false;
    }

    //INVALID IDENTIFIER - Either the length is too long or short
    public boolean lengthNotRight(String command) throws InvalidIdentifierLengthException{
        if (command.length() != 2){
            int length = command.length();
            int invalid = 0;
            throw new InvalidIdentifierLengthException(invalid, length);
        }
        return true;
    }

    //INVALID CHARACTER - the character is not recognised
    public boolean characterNotRight(String command) throws InvalidIdentifierCharacterException {
        if ((!rowNotRight(command)) || (!colNotRight(command))){
            throw new InvalidIdentifierCharacterException(rowNumber, colNumber);
        }
        return true;
    }

       public boolean colNotRight(String command){
            char[] inputArray = new char[command.length()];
            for (int i = 0; i < command.length(); i++) {
                inputArray[i] = command.charAt(i);
            }
            int col = (Character.getNumericValue(inputArray[1]));
            int j = 0;
            while(j < 10){
                if (j == col){
                    return true;
                }
                j++;
            }
           return false;
        }

       public boolean rowNotRight(String command){
            char[] inputArray = new char[command.length()];
            for (int i = 0; i < command.length(); i++) {
                inputArray[i] = command.charAt(i);
            }
            for (int i = 0; i < NumberValueLowerLetters.length; i++){
                if ((NumberValueLowerLetters[i] == inputArray[0]) || (NumberValueUpperLetters[i] == inputArray[0])){
                    return true;
                }
            }
           return false;
        }
}

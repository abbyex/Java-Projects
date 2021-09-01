import java.util.*;

class OXOModel
{
    public ArrayList<ArrayList<OXOPlayer>> cells;
    public ArrayList<OXOPlayer> players;
    private OXOPlayer currentPlayer;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;
    private int playerCounter = 0;
    private int currentRow;
    private int currentCol;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh)
    {
        setWinThreshold(winThresh);
        cells = new ArrayList<ArrayList<OXOPlayer>>();
        for (int i = 0; i < numberOfRows; i++){
            ArrayList<OXOPlayer> row = new ArrayList<OXOPlayer>(Arrays.asList(new OXOPlayer[numberOfColumns]));
            cells.add(row);
        }
        players = new ArrayList<OXOPlayer>();
    }

    public int getNumberOfPlayers()
    {
        return players.size();
    }

    public void addPlayer(OXOPlayer player)
    {
        players.add(playerCounter, player);
        playerCounter++;
        return;
    }

    public OXOPlayer getPlayerByNumber(int number)
    {
        return players.get(number);
    }

    public OXOPlayer getWinner()
    {
        return winner;
    }

    public void setWinner(OXOPlayer player)
    {
        winner = player;
    }

    public OXOPlayer getCurrentPlayer()
    {
        return currentPlayer;
    }

    public void setCurrentPlayer(OXOPlayer player)
    {
        currentPlayer = player;
    }

    public int getNumberOfRows()
    {
        return cells.size();
    }

    public int getNumberOfColumns()
    {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber)
    {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player)
    {
        cells.get(rowNumber).set(colNumber, player);
    }

    public void setWinThreshold(int winThresh)
    {
        winThreshold = winThresh;
    }

    public int getWinThreshold()
    {
        return winThreshold;
    }

    public void setGameDrawn()
    {
        gameDrawn = true;
    }

    public boolean isGameDrawn()
    {
        return gameDrawn;
    }

    public void setCurrentRow(int row){
        currentRow = row;
    }

    public void setCurrentCol(int col){
        currentCol = col;
    }

    public int getCurrentRow(){
        return currentRow;
    }

    public int getCurrentCol(){
        return currentCol;
    }

}
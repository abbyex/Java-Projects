package StagExceptions;
import java.io.BufferedWriter;
import java.io.*;

public class CommandCantBePerformedException extends Exception {
    public String objName;

    public CommandCantBePerformedException(String objName){
        this.objName = objName;
    }

    public void toScreen(String objName, BufferedWriter out){
        try {
            out.write("The command" + objName + " cannot be performed in the game at this time.\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}

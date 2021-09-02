import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;

public class actionParser {

    public actionParser(String jsonFile, GameModel model){
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(jsonFile)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray actionList = (JSONArray) jsonObject.get("actions"); // Gets everything within file under "actions"

            for (Object action : actionList){
                Action newAction = new Action();
                parseTriggers(newAction, (JSONObject) action);
                model.setActionObject(newAction); // Stores all actions within the GameModel
            }
        }
        catch (IOException | ParseException e){
            e.printStackTrace();
        }
    }

    // Sets the parameters for each set of actions (The trigger words, the subjects, entities consumed, entities produced,
    // narration).
    public Action parseTriggers(Action newAction, JSONObject action) {
        newAction.setTriggers((JSONArray) action.get("triggers"));
        newAction.setSubjects((JSONArray) action.get("subjects"));
        newAction.setConsumed((JSONArray) action.get("consumed"));
        newAction.setProduced((JSONArray) action.get("produced"));
        newAction.setNarration((String) action.get("narration"));
        return newAction;
    }
}

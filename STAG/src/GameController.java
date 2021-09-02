import StagExceptions.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

public class GameController {
    private GameModel model;
    private String keyword;
    private String currentPlayer;
    private String narration;

    public GameController(GameModel model){
        this.model = model;
    }

    // Handles all user commands and outputs back to the screen
    public void handleIO(String incomingCommand, BufferedWriter out){
        String[] userCommand = incomingCommand.split(":");
        if (!model.playerExist(userCommand[0])){
            createNewPlayer(userCommand[0]);
        }
        setCurrentPlayerName(userCommand[0]);
        checkCommand(userCommand[1], out);
    }

    private void checkCommand(String userCommand, BufferedWriter out){
        Player playerObject = getCurrentPlayer();
        try {
            callCommand(userCommand, playerObject, out);
        }
        catch (CommandCantBePerformedException e){
            e.toScreen(userCommand, out);
        }
    }

    // Checks if the command is valid, or throws an exception
    private boolean callCommand(String userCommand, Player playerObject, BufferedWriter out) throws CommandCantBePerformedException {
        if  (basicCommand(userCommand, playerObject, out) || complexCommand(userCommand)){
            updateGameModel(keyword, out);
            return true;
        }
        throw new CommandCantBePerformedException(userCommand);
    }

    // Basic Commands : GoTo, Look, Inventory, Get, Drop, Health
    private boolean basicCommand(String userCommand, Player playerObject, BufferedWriter out){
        return gotoCommand(userCommand, playerObject, out) || lookCommand(userCommand, playerObject, out) ||
                inventoryCommand(userCommand, playerObject, out) || getCommand(userCommand, playerObject)
                || dropCommand(userCommand, playerObject) || healthCommand(playerObject, userCommand, out);
    }

    // Complex Commands : Any trigger word + Any subject word in the Action object
    private boolean complexCommand(String userCommand){
        if (checkForTrigger(userCommand)){
            String tokenCommand = tokenifyCommand(userCommand, keyword);
            return checkForSubject(keyword, tokenCommand);
        }
        return false;
    }

    // Checks if the actions in GameModel contain at least one trigger word.
    private boolean checkForTrigger(String userCommand) {
        for (Action actionObject : model.getActions()) {
            for (String trigger : actionObject.getTriggers()){
                if (userCommand.contains(trigger)){
                    keyword = trigger;
                    return true;
                }
            }
        }
        return false;
    }

    // Checks if the actions in GameModel contains at least one subject from the user's command
    private boolean checkForSubject(String trigger, String incomingCommand) {
        Action actionObject = getActionObject(trigger);
        assert actionObject != null;
        for (String subject : actionObject.getSubjects()){
            String[] tokenCommand = tokenifyWords(incomingCommand);
            for (String word : tokenCommand){
                if (word.equals(subject)) {
                    if (checkEntities(keyword)) {
                        setNarration(actionObject);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Checks that all the entities are there in order to perform the action
    private boolean checkEntities(String trigger){
        for (String subject : Objects.requireNonNull(getActionObject(trigger)).getSubjects()){
            if (!model.isObjectInInventory(subject, getCurrentPlayerName()) &&
                    !model.isEntityAtLocation(subject, Objects.requireNonNull(getCurrentPlayer()).getCurrentLocation())){
                return false;
            }
        }
        return true;
    }

    // GOTO: Moves the Player from one location to another
    private boolean gotoCommand(String userCommand, Player playerObject, BufferedWriter out){
        if (userCommand.contains("goto") || userCommand.contains("go to")){
            String commandArray = tokenifyCommand(userCommand, "goto");
            String[] tokenCommand = tokenifyWords(commandArray);
            for (String word : tokenCommand){
                if (model.locationExist(word)){
                    model.moveEntityToLocation(playerObject.getPlayerName(), word, model); // Moves the player to/from locations as an entity
                }
                if (playerObject.setCurrentLocation(word, model)) {
                    keyword = "goto";
                    lookCommand("look", playerObject, out);
                    return true;
                }
            }
        }
        return false;
    }

    // LOOK: Describes the current location, and displays the descriptions of entities in the location, and other players.
    private boolean lookCommand(String userCommand, Player playerObject, BufferedWriter out){
        if (userCommand.contains("look")){
            if (model.locationExist(playerObject.getCurrentLocation())) {
                toScreen("Your location is: " + model.getOneLocation(playerObject.getCurrentLocation())
                        .getLocationDescription() + "\n", out);
                toScreen("At this location: " + model.getEntitiesAtLocation(playerObject.getCurrentLocation(),
                        playerObject).toString().replaceAll("\\[|]", "") + "\n", out);
                keyword = "look";
                return true;
            }
        }
        return false;
    }

    // INVENTORY: Displays player's inventory on screen
    private boolean inventoryCommand(String userCommand, Player currentPlayer, BufferedWriter out){
        if (userCommand.contains("inventory") || userCommand.contains("inv")) {
            toScreen("INVENTORY " + currentPlayer.getInventory().toString() + "\n", out);
            keyword = "inventory";
            return true;
        }
        return false;
    }

    // GET: picks up an artefact, and puts it in the player's inventory.
    private boolean getCommand(String userCommand, Player currentPlayer){
        if (userCommand.contains("get")){
            String commandArray = tokenifyCommand(userCommand,"get");
            String[] tokenCommand = tokenifyWords(commandArray);
            for (String word : tokenCommand) {
                if (currentPlayer.addToInventory(word, model)) {
                    keyword = "get";
                    return true;
                }
            }
        }
        return false;
    }

    // DROP: removes an artefact from a player's inventory, and puts it back in location.
    private boolean dropCommand(String userCommand, Player playerObject){
        if (userCommand.contains("drop")){
            String commandArray = tokenifyCommand(userCommand, "drop");
            String[] tokenCommand = tokenifyWords(commandArray);
            for (String word : tokenCommand){
                keyword = "drop";
                if (playerObject.removeFromInventory(word, model, keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    // HEALTH: Displays on-screen the player's health level.
    private boolean healthCommand(Player playerObject, String userCommand, BufferedWriter out){
        if (userCommand.contains("health")){
            toScreen("Your health is " + playerObject.getHealthLevel() + "\n", out);
            keyword = "health";
            return true;
        }
        return false;
    }

    // Removes the consumed, and updates the produced so it displays when look calls
    private void updateGameModel(String trigger, BufferedWriter out){
        if (!isBasicCommand(trigger)) {
            updateLocation(Objects.requireNonNull(getCurrentPlayer()).getCurrentLocation(),
                    trigger, getCurrentPlayerName(), out);// Updates the location, eg removing or adding things
            toScreen("NARRATION " + narration + "\n", out); // Displays narration on-screen
        }
    }

    // Calls for consumed and produced entities to be removed or updated from location (This includes Players).
    private void updateLocation(String locationName, String keyword, String currentPlayer, BufferedWriter out){
        getConsumed(keyword, currentPlayer, locationName, out);
        getProduced(keyword, currentPlayer, locationName);
    }

    // Looks for the trigger word in the list of actions to see if valid.
    private void getProduced(String keyword, String currentPlayer, String locationName){
        if (containsTrigger(keyword)) {
            for (String produced : Objects.requireNonNull(getActionObject(keyword)).getProduced()) {
                if (produced.equals("health")) {
                    model.checkPlayerHealth(currentPlayer, "add", model);
                }
                // Check if the produced is a location first. if it is, add to the path.
                model.moveEntityToLocation(produced, locationName, model);
            }
        }
    }

    // Looks for the trigger word in the list of actions to see if valid.
    private void getConsumed(String keyword, String currentPlayer, String locationName, BufferedWriter out){
        if (containsTrigger(keyword)) {
            for (String consumed : Objects.requireNonNull(getActionObject(keyword)).getConsumed()) {
                if (consumed.equals("health")) { // If health is consumed, takes away from player
                    if (!model.checkPlayerHealth(currentPlayer, "remove", model))
                        toScreen(model.getPlayerDeadMessage(), out);
                }
                // Removes entities from inv/location as appropriate
                model.removeLocationFromPath(consumed, Objects.requireNonNull(getCurrentPlayer()).getCurrentLocation());
                model.updatePlayerInventory(consumed, currentPlayer, model);
                model.updateLocationObjects(locationName, consumed, model, currentPlayer);
            }
        }
    }

    // Sets the narration to be printed to screen
    private void setNarration(Action actionObject){
        narration = actionObject.getNarration();
    }

    private void setCurrentPlayerName(String playerName){
        this.currentPlayer = playerName;
    }

    private String getCurrentPlayerName(){
        return currentPlayer;
    }

    // Returns the player object from GameModel
    private Player getCurrentPlayer(){
        for (Player player : model.getPlayers()){
            if (currentPlayer.equals(player.getPlayerName())){
                return player;
            }
        }
        return null;
    }

    private void createNewPlayer(String playerName){
        Player newPlayer = new Player();
        newPlayer.setPlayerName(playerName);
        resetPlayer(newPlayer); // Resets the player --> puts in start location, sets health level
        model.addPlayer(newPlayer);
        model.setNewEntity(newPlayer.getPlayerName(), newPlayer.getPlayerName(),
                "player"); // Defines player's entity parameters in GameModel
        model.getOneLocation(model.getStartLocation()).addEntity(newPlayer.getPlayerName(),
                model); // Adds the player as an entity in the location
    }

    // Resets a player back to its original state eg after dying, or when a new player is created
    private void resetPlayer(Player playerName){
        if (!playerName.getInventory().isEmpty()){
            Iterator<String> itr = playerName.getInventory().iterator();
            while (itr.hasNext()) { // Removes all items from player's inventory
                model.getOneLocation(playerName.getCurrentLocation()).addEntity(itr.next(), model);
                itr.remove();
            }
        }
        playerName.setStartLocation(model.getStartLocation()); // Gets the first location from the location list
        playerName.setHealthLevel(); // Sets health level back to original level
    }

    private boolean isBasicCommand(String trigger) {
        return trigger.equals("inventory") || trigger.equals("look") || trigger.equals("goto")
                || trigger.equals("get") || trigger.equals("drop") || trigger.equals("health");
    }

    // Checks that in the GameModel, the trigger is actually valid
    private boolean containsTrigger(String trigger){
        for (Action action : model.getActions()){
            for (String word : action.getTriggers()){
                if (word.equals(trigger)){
                    return true;
                }
            }
        }
        return false;
    }

    // Returns the object Action from GameModel
    private Action getActionObject(String trigger) {
        for (Action action : model.getActions()){
            if (action.containsTrigger(trigger)){
                return action;
            }
        }
        return null;
    }

    // Displays output onto the player's screen.
    private void toScreen(String object, BufferedWriter out) {
        try {
            out.write(object);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Disregards all words before the 'trigger' command --> so that if the user says 'Please open key' the command
    // will just parse as 'open key'
    private String tokenifyCommand(String userCommand, String word){
        String[] newCommand = userCommand.split(word + " ");
        if (newCommand.length == 2) {
            return newCommand[1];
        }
        return newCommand[0];
    }

    // Replaces all punctuation in the word --> so that 'open key!' or 'open    key' would pass.
    private String[] tokenifyWords(String userCommand){
        return userCommand.replaceAll("\\s+(?=[\\p{Punct}&&[^.]])", "")
                .replaceAll("\t", "").split(" ");
    }
}
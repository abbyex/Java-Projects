import java.util.*;

public class GameModel {
    private final HashMap<String, Location> gameLocations;
    private final ArrayList<Action> gameActions;
    private final ArrayList<Player> playerList;
    private String startLocation;
    private final HashMap<String, Entity>entityList;
    private String deathMessage;

    public GameModel(){
        gameLocations = new HashMap<>();
        gameActions = new ArrayList<>();
        playerList = new ArrayList<>();
        entityList = new HashMap<>();
    }

    public void setStartLocation(String location){
        startLocation = location;
    }

    public String getStartLocation(){
        return startLocation;
    }

    public void setActionObject(Action newAction){
        gameActions.add(newAction);
    }

    public ArrayList<Action> getActions(){
        return gameActions;
    }

    // Creates a new location in the GameModel
    public void createLocation(Location location, String locationName){
        // If the list is empty, it's the first location --> starting location.
        if (gameLocations.isEmpty()){
            setStartLocation(locationName);
        }
        gameLocations.put(locationName, location);
    }

    // Returns a Location object from the GameModel
    public Location getOneLocation(String locationName){
        if (gameLocations.containsKey(locationName)){
            return gameLocations.get(locationName);
        }
        return null;
    }

    // Checks if the locations exists
    public boolean locationExist(String locationName){
        return gameLocations.containsKey(locationName);
    }

    // Checks if the player exists
    public boolean playerExist(String name){
        for (Player player : playerList){
            if (player.getPlayerName().equals(name)){
                return true;
            }
        }
        return false;
    }

    // For LOOK command : Returns the entities to the screen to display what's in location.
    public ArrayList<String> getEntitiesAtLocation(String locationName, Player currentPlayer) {
        ArrayList<String> descriptionList = new ArrayList<>();
        for (Entity entity : gameLocations.get(locationName).getEntityList()){
            if (!currentPlayer.getPlayerName().equals(entity.getName())) { // Don't return the current player as an entity.
                if (!descriptionList.contains(entity.getDescription())) { // Stops entities from being added twice - such as players.
                    descriptionList.add(entity.getDescription());
                }
            }
        }
        return descriptionList;
    }

    // Checks if the object is in the inventory of a specific player.
    public boolean isObjectInInventory(String entity, String currentPlayer){
        for (Player player : playerList){
            if (player.getPlayerName().equals(currentPlayer)){
                if (player.getInventory().contains(entity)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Removes the entity from the player's inventory (If it's been consumed).
    public void updatePlayerInventory(String entityName, String currentPlayer, GameModel model){
        for (Player player : playerList){
            if (player.getPlayerName().equals(currentPlayer)){
                if (player.isInInventory(entityName)){
                    player.removeFromInventory(entityName, model, "consumed");
                }
            }
        }
    }

    // Checks if the entity is in the location
    public boolean isEntityAtLocation(String entity, String location){
        return gameLocations.get(location).containsEntity(entity);
    }

    // Removes the object from the location specified. If it's not there, checks the unplaced.
    public void updateLocationObjects(String locationName, String entityName, GameModel model, String currentPlayer){
        if (isEntityAtLocation(entityName, locationName)){
            gameLocations.get(locationName).removeEntity(entityName);
        }
        if (gameLocations.get("unplaced").containsEntity(entityName)){
            if (isObjectInInventory(entityName, currentPlayer)) {
                Entity newEntity = gameLocations.get("unplaced").getEntity(entityName);
                gameLocations.get(locationName).addEntity(newEntity.getName(), model);
                gameLocations.get("unplaced").removeEntity(entityName);
            }
        }
    }

    public void removeLocationFromPath(String consumed, String currentLocation){
        for(Map.Entry<String, Entity> entry : entityList.entrySet()) {
            Entity entity = entry.getValue();
            if (entity.getName().equals(consumed) && entity.getType().equals("location")) {
                gameLocations.get(currentLocation).removePaths(consumed);
            }
        }
    }

   // Moves entities from one location to another --> removes from current, adds to next.
    // if it's a location, adds to the path.
    public void moveEntityToLocation(String produced, String locationName, GameModel model){
        for(Map.Entry<String, Entity> entry : entityList.entrySet()) {
            Entity entity = entry.getValue();
            if (entity.getName().equals(produced)){
                if (entity.getType().equals("location")) {
                    if (!gameLocations.get(locationName).isItAPath(produced)) {
                        gameLocations.get(locationName).setPaths(produced); // Adds the location to the path
                    }
                }
            else {
                removeEntityFromLocation(entity.getName());
                gameLocations.get(locationName).addEntity(produced, model); // Adds the entity into the game
                }
            }
        }
    }

    // Removes an entity from the location.
    public void removeEntityFromLocation(String entity){
        for(Map.Entry<String, Location> entry : gameLocations.entrySet()) {
            Location location = entry.getValue();
            if (location.containsEntity(entity)) {
                gameLocations.get(location.getLocationName()).removeEntity(entity);
            }
        }
    }

    // Checks and updates the player's health as according to the healthLevel --> 'add' or 'remove'.
    public boolean checkPlayerHealth(String playerName, String healthLevel, GameModel model){
        for (Player player : playerList){
            if (player.getPlayerName().equals(playerName)){
                int index = playerList.indexOf(player);
                if (healthLevel.equals("add")){
                    player.addToHealth();
                }
                if (healthLevel.equals("remove")){
                    player.subtractHealth();
                }
                if (player.isPlayerDead()){
                    resetPlayer(player, model);
                    setPlayerDeadMessage(getStartLocation());
                    return false;
                }
                playerList.set(index, player);
            }
        }
        return true;
    }

    public void setPlayerDeadMessage(String locationName){
        deathMessage = "Your health level reached 0 and you were killed. You have returned to " + locationName + "\n";
    }

    public String getPlayerDeadMessage(){
        return deathMessage;
    }

    public void addPlayer(Player newPlayer){
        playerList.add(newPlayer);
    }

    public ArrayList<Player> getPlayers(){
        return playerList;
    }

    // Returns an entity object from the GameModel.
    public Entity getEntity(String name){
        for(Map.Entry<String, Entity> entry : entityList.entrySet()) {
            Entity entity = entry.getValue();
            if (entity.getName().equals(name)) {
                return entity;
            }
        }
        return null;
    }

    public boolean containsEntity(String name){
        for(Map.Entry<String, Entity> entry : entityList.entrySet()) {
            Entity entity = entry.getValue();
            if (entity.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    // Sets the name, description and types of entities in the game
    public void setNewEntity(String entity, String description, String type){
        Entity newEntity = new Entity();
        newEntity.setName(entity);
        newEntity.setDescription(description);
        newEntity.setType(type);
        if (!containsEntity(entity)) {
            entityList.put(entity, newEntity);
        }
    }

    // Resets a player back to its original state eg after dying, or when a new player is created
    public void resetPlayer(Player playerName, GameModel model){
        if (!playerName.getInventory().isEmpty()){
            Iterator<String> itr = playerName.getInventory().iterator();
            while (itr.hasNext()) { // Removes all items from player's inventory
                model.getOneLocation(playerName.getCurrentLocation()).addEntity(itr.next(), model);
                itr.remove();
            }
        }
        playerName.setStartLocation(getStartLocation()); // Gets the first location from the location list
        playerName.setHealthLevel(); // Sets health level back to original level
    }
}

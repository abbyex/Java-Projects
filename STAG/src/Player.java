import java.util.*;

public class Player {
    private String currentLocation;
    private String playerName;
    private final ArrayList<String> inventory;
    private int healthLevel;

    public Player(){
        inventory = new ArrayList<>();
    }

    public boolean setCurrentLocation(String location, GameModel model) {
        if (!model.locationExist(location)) { // Check that it's actually a valid location
            return false;
        }
        if (!model.getOneLocation(getCurrentLocation()).isItAPath(location)) {
            return false; // The location is not on the path
        }
        currentLocation = location;
        return true;
    }

    public void setStartLocation(String location){
        currentLocation = location;
    }

    public String getCurrentLocation(){
        return currentLocation;
    }

    public void setPlayerName(String playerName){
        this.playerName = playerName;
    }

    public String getPlayerName(){
        return playerName;
    }

    public boolean addToInventory(String object, GameModel model) {
        // Check that the entity is in the location and the type is 'artefact' before adding
        if (model.getOneLocation(getCurrentLocation()).isEntityInLocation(object)) {
            if (model.getOneLocation(getCurrentLocation()).getEntity(object).getType().equals("artefacts")) {
                    inventory.add(object);
                    model.getOneLocation(getCurrentLocation()).removeEntity(object); // Removes from location
                    return true;
                }
            }
        return false;
    }

    public boolean removeFromInventory(String object, GameModel model, String command) {
        if (isInInventory(object)) {
            inventory.remove(object);
            if (command.equals("drop")) {
                model.getOneLocation(getCurrentLocation()).addEntity(object, model); // Puts entity back in the location
            }
            return true;
        }
        return false;
    }

    public ArrayList<String> getInventory(){
        return inventory;
    }

    public boolean isInInventory(String object){
        return inventory.contains(object);
    }

    public void setHealthLevel(){
        healthLevel = 3;
    }

    public int getHealthLevel(){
        return healthLevel;
    }

    public void addToHealth(){
        healthLevel = healthLevel + 1;
    }

    public void subtractHealth(){
        healthLevel = healthLevel - 1;
    }

    public boolean isPlayerDead(){
        if (healthLevel == 0){
            return true;
        }
        return false;
    }
}


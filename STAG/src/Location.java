import java.util.*;

public class Location {
    private String locationName;
    private String locationDescription;
    private final ArrayList<String> locationPaths;
    private final ArrayList<Entity> entityList = new ArrayList<>();

    public Location(){
        locationPaths = new ArrayList<>();
    }

    public void setLocationName(String name){
        locationName = name;
    }

    public String getLocationName(){
        return locationName;
    }

    public void setLocationDescription(String description){
        locationDescription = description;
    }

    public String getLocationDescription(){
        return locationDescription;
    }

    public ArrayList<Entity> getEntityList(){
        return entityList;
    }

    // Sets the possible paths that the Player could follow from the current Location
    public void setPaths(String path){
        locationPaths.add(path);
    }

    public boolean isItAPath(String location){
        if (locationPaths.contains(location)){
            return true;
        }
        return false;
    }

    public void removePaths(String location){
        if (isItAPath(location)){
            locationPaths.remove(location);
        }
    }

    // check for entity in the location
    public boolean isEntityInLocation(String entity){
        for (Entity entityObject : entityList){
            if (entityObject.getName().equals(entity)){
                return true;
            }
        }
        return false;
    }

    public Entity getEntity(String entity){
        for (Entity entityObject : entityList){
            if (entityObject.getName().equals(entity)){
                return entityObject;
            }
        }
        return null;
    }

    // Removes an entity from the location
    public void removeEntity(String entity){
        entityList.removeIf(entityObject -> entityObject.getName().equals(entity));
    }

    public void createEntity(String name, String type, String description, GameModel model){
        Entity newEntity = new Entity();
        newEntity.setName(name);
        newEntity.setType(type);
        newEntity.setDescription(description);
        model.setNewEntity(name, description, type);
        entityList.add(newEntity);
    }

    public void addEntity(String name, GameModel model){
        if (model.containsEntity(name)) {
            Entity entity = model.getEntity(name);
            entityList.add(entity);
        }
    }

    public boolean containsEntity(String name){
        for (Entity entity : entityList){
            if (entity.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
}

public class Entity {
    private String entityName;
    private String entityDescription;
    private String type;

    public Entity(){}

    public void setName(String name){
        entityName = name;
    }

    public void setDescription(String description){
        entityDescription = description;
    }

    public String getName(){
        return entityName;
    }

    public String getDescription(){
        return entityDescription;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}

public class StagParser {

    public StagParser(String entityFile, String actionFile, GameModel model){
        new actionParser(actionFile, model);
        new entityParser(entityFile, model);
    }
}

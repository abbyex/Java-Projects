import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class entityParser {

    public entityParser(String dotFile, GameModel model){
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(dotFile);
            parser.parse(reader);
            ArrayList<Graph> graphs = parser.getGraphs();
            ArrayList<Graph> firstSubgraph = graphs.get(0).getSubgraphs();

            // Iterates through each graph creating a new location, and setting it's paths.
            for(Graph locOrPath : firstSubgraph){
                ArrayList<Graph> secondSubgraph = locOrPath.getSubgraphs();
                for (Graph location : secondSubgraph){
                    ArrayList<Node> nodeArray = location.getNodes(false); //gets all the nodes describing location
                    Node nodeLoc = nodeArray.get(0);
                    createNewLocation(model, location, nodeLoc);
                }
                setPaths(model, locOrPath);
            }
        } catch (FileNotFoundException | ParseException f) {
            System.out.println(f);
        }
    }

    public void createNewLocation(GameModel model, Graph location, Node nodeLoc){
        Location newLocation = new Location();
        // Sets the parameters of the location (name, description).
        newLocation.setLocationName(nodeLoc.getId().getId());
        newLocation.setLocationDescription(nodeLoc.getAttribute("description"));
        model.setNewEntity(nodeLoc.getId().getId(), nodeLoc.getAttribute("description"), "location");

        // Sets the entities within the location
        ArrayList<Graph> thirdSubgraph = location.getSubgraphs();
        for (Graph entities : thirdSubgraph) {
            ArrayList<Node> nodeArray = entities.getNodes(false);
            for (Node nodeEntity : nodeArray) {
                setEntitiesAtLocation(entities, nodeEntity, newLocation, model);
            }
        }
        model.createLocation(newLocation, newLocation.getLocationName()); // Adds location to be stored in the GameModel
    }

    // Adds possible pathways to that location
    public void setPaths(GameModel model, Graph paths){
        ArrayList<Edge> edges = paths.getEdges();
        for (Edge edgePath : edges){
            if (model.locationExist(edgePath.getSource().getNode().getId().getId())) {
                model.getOneLocation(edgePath.getSource().getNode().getId().getId()).setPaths(edgePath.getTarget().getNode().getId().getId());
            }
        }
    }

    // Sets the parameters for entities at the location (name, type, description).
    public void setEntitiesAtLocation(Graph entities, Node nodeEntity, Location newLocation, GameModel model){
        String name = nodeEntity.getId().getId();
        String type = entities.getId().getId().toLowerCase();
        String description = nodeEntity.getAttribute("description").toLowerCase();
        newLocation.createEntity(name, type, description, model);
    }

}

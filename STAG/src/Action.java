import java.util.*;

public class Action {

    private ArrayList<String> triggerList;
    private ArrayList<String> subjectsList;
    private ArrayList<String> consumedList;
    private ArrayList<String> producedList;
    private String narration;

    public Action(){
        triggerList = new ArrayList<>();
        subjectsList = new ArrayList<>();
        consumedList = new ArrayList<>();
        producedList = new ArrayList<>();
    }

    public void setTriggers(ArrayList<String> trigger){
        triggerList = trigger;
    }

    public ArrayList<String> getTriggers(){
        return triggerList;
    }

    public boolean containsTrigger(String trigger){
        return triggerList.contains(trigger);
    }

    public void setSubjects(ArrayList<String> subject){
        subjectsList = subject;
    }

    public ArrayList<String> getSubjects(){
        return subjectsList;
    }

    public void setConsumed(ArrayList<String> consume){
        consumedList = consume;
    }

    public ArrayList<String> getConsumed(){
        return consumedList;
    }

    public void setProduced(ArrayList<String> produce){
        producedList = produce;
    }

    public ArrayList<String> getProduced(){
        return producedList;
    }

    public void setNarration(String narration){
        this.narration = narration;
    }

    public String getNarration(){
        return narration;
    }
}

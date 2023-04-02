package lab1.Dictionary;
import java.util.ArrayList;

public class FormWord {
    public String word;
    public ArrayList<String> grammemes = new ArrayList<>();
    public FormWord(){
    }

    @Override
    public String toString(){
        return '|' + "grammemes(" +  word + "): " + grammemes + '|';
    }
}

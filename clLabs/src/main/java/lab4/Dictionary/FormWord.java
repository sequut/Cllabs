package lab4.Dictionary;
import java.util.ArrayList;

public class FormWord {
    public String word;
    public ArrayList<String> grammemes = new ArrayList<>();
    public FormWord(){
    }

    public FormWord(String word){
        this.word = word;
    }

    @Override
    public String toString(){
        return '|' + "grammemes(" +  word + "): " + grammemes + '|';
    }
}

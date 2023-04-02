package lab1.Dictionary;

public class Lemma {
    public Integer id;
    public FormWord startingForm;

    public Lemma(){
    }

    public Lemma(Integer id){
        this.id = id;
    }

    @Override
    public String toString(){
        return "|" + startingForm + "|";
    }
}

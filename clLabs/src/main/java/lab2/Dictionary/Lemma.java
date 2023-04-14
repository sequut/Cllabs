package lab2.Dictionary;

public class Lemma {
    public Integer id;
    public FormWord startingForm;

    public Lemma(){
    }

    public Lemma(Integer id){
        this.id = id;
    }

    public Lemma(Integer id, FormWord startingForm){
        this.id = id;
        this.startingForm = startingForm;
    }

    @Override
    public String toString(){
        return "|" + startingForm + "|";
    }
}

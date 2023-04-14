package lab2.Dictionary;

import java.util.ArrayList;

public class LemmaSupposed{
    ArrayList<FormWord> wordForms = new ArrayList<>();
    ArrayList<Lemma> lemmasSupposed = new ArrayList<>();

    public LemmaSupposed(){
    }

    @Override
    public String toString() {
        return "| wordForms: " + wordForms + "| supposedLemmas: " + getNormalizedLemmas() + " |";
    }

    public ArrayList<Lemma> getNormalizedLemmas() {
        ArrayList<Lemma> result = new ArrayList<>();
        for (Lemma lemma : lemmasSupposed)
            if (!result.contains(lemma))
                result.add(lemma);
        return result;
    }
}
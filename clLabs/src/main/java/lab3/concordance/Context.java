package lab3.concordance;

import lab3.Dictionary.Lemma;

import java.util.ArrayList;
import java.util.Objects;

public class Context{
    public final ArrayList<Lemma> lemmata;

    public Context(ArrayList<Lemma> lemmata){
        this.lemmata = lemmata;
    }

    @Override
    public boolean equals(Object object){
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;

        Context context = (Context) object;
        if (this.lemmata.size() != context.lemmata.size())
            return false;

        for (int i = 0; i < this.lemmata.size(); i++)
            if (!this.lemmata.get(i).equals(context.lemmata.get(i)))
                return false;

        return true;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (Lemma lemma : lemmata)
            stringBuilder.append(lemma.startingForm.word).append(" ");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode(){
        return Objects.hash(lemmata);
    }
}

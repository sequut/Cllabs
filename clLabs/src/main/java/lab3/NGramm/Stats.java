package lab3.NGramm;

import java.util.ArrayList;

public class Stats {
    public Integer count = null;
    public Double tf_idf = 0.0;
    public ArrayList<Integer> indexesInTexts = new ArrayList<>();
    public ArrayList<Integer> indexesOfTexts = new ArrayList<>();
    public Integer countOfWords = 0;
    public Double stability = null;
    public Integer maxExtensionFrequency = null;

    public Stats(int count, int firstIdx, int firstTextIdx){
        this.count = count;
        this.indexesInTexts.add(firstIdx);
        this.indexesOfTexts.add(firstTextIdx);
    }

    public void setTf_idf(double tf_idf) {
        this.tf_idf = tf_idf;
    }
}

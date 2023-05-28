package lab4.concordance;

import java.util.ArrayList;

public class Stats {
    public Integer count = null;
    public ArrayList<Integer> indexes = new ArrayList<>();

    public Stats(int count, int startIndex){
        this.indexes.add(startIndex);
        this.count = count;
    }
}

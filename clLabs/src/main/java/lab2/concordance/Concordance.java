package lab2.concordance;

import lab2.Dictionary.Dictionary;
import lab2.Dictionary.Lemma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Concordance {
    int number;
    String ph;
    List<String> phTokens;
    List<Lemma> normalizedPhLemmas;
    List<String> tokensCorpora;
    List<Lemma> normalizedLemmasCorpora;
    ArrayList<Integer> indexesMatch = null;
    HashMap<Context, Stats> leftContexts = new HashMap<>();
    HashMap<Context, Stats> rightContexts = new HashMap<>();

    public Concordance(String ph, String tokensCorpora, int number, Dictionary dictionary){
        this.ph = ph;
        this.tokensCorpora = tokenize(tokensCorpora);
        this.phTokens = tokenize(ph);
        this.number = number;

        this.normalizedPhLemmas = dictionary.lemmatizeTokens(this.phTokens);
        this.normalizedLemmasCorpora = dictionary.lemmatizeTokens(this.tokensCorpora);

        compute(number);
    }

    private void compute(int number){
        if (indexesMatch == null) {
            this.indexesMatch = new ArrayList<>();
            for (int i = 0; i < normalizedLemmasCorpora.size() - normalizedPhLemmas.size(); i++) {
                boolean match = true;
                for (int j = 0; j < normalizedPhLemmas.size(); j++) {
                    Lemma pl = normalizedPhLemmas.get(j);
                    Lemma cl = normalizedLemmasCorpora.get(i + j);
                    if (!pl.equals(cl)) {
                        match = false;
                        break;
                    }
                }
                if (match)
                    indexesMatch.add(i);
            }
        }

        for (Integer i : indexesMatch){
            int startIdx = Math.max(i - number, 0);
            int endIdx = i + normalizedPhLemmas.size();
            for (; startIdx < i; startIdx++)
                makeContexts(i, startIdx, endIdx, leftContexts);
        }

        for (Integer i : indexesMatch){
            int startIdx = i;
            int endIdx = Math.min(i + normalizedPhLemmas.size() + number, normalizedLemmasCorpora.size());
            for (int endIdx2 = i + normalizedPhLemmas.size(); endIdx2 <= endIdx; endIdx2++)
                makeContexts(i, startIdx, endIdx2, rightContexts);
        }
    }

    private void makeContexts(Integer i, int startIdx, int endIdx2, HashMap<Context, Stats> rightContexts) {
        ArrayList<Lemma> contextLemmas = new ArrayList<>();
        for (int j = startIdx; j<endIdx2; j++)
            contextLemmas.add(normalizedLemmasCorpora.get(j));
        Context c = new Context(contextLemmas);
        if (rightContexts.get(c) == null)
            rightContexts.put(c, new Stats(1, i));
        else
            rightContexts.get(c).count ++;
    }

    public void printStats(String path, Integer threshold) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        final Integer t;
        if (threshold != null && threshold >= 0)
            t = threshold;
        else
            t = 0;
        var entries = leftContexts.entrySet().stream().filter(e -> e.getValue().count >= t).
                sorted((e1, e2) -> -1 * e1.getValue().count.compareTo(e2.getValue().count)).toList();

        for (Map.Entry<Context, Stats> e : entries) {
            writer.write("l: " + e.getKey() + ": " + e.getValue().count + "\n");
        }
        entries = rightContexts.entrySet().stream().filter(e -> e.getValue().count >= t).
                sorted((e1, e2) -> -1 * e1.getValue().count.compareTo(e2.getValue().count)).toList();
        for (Map.Entry<Context, Stats> e : entries) {
            writer.write("r: " + e.getKey() + ": " + e.getValue().count + "\n");
        }
        writer.close();
    }

    public static List<String> tokenize(String text) {
        List<String> tokens = Arrays.stream(text.replace("\n", "").
                        toLowerCase().split("[«;»  \"—()?:!'+/-]")).
                        filter((x) -> x.length() > 0).toList();

        List<String> tokensNe = new ArrayList<>();
        for (String i : tokens){
            StringTokenizer stringTokenizer = new StringTokenizer(i, "[.,]", true);
            while (stringTokenizer.hasMoreTokens())
                tokensNe.add(stringTokenizer.nextToken());
        }
        return tokensNe;
    }
}

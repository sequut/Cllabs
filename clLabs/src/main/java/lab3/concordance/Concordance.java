package lab3.concordance;

import lab3.Dictionary.Dictionary;
import lab3.Dictionary.Lemma;
import lab3.concordance.Stats;
import lab3.concordance.Context;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Concordance {
    private ArrayList<Integer> indexesOfTexts;
    int number;
    String ph;
    List<String> phTokens;
    List<Lemma> normalizedPhLemmas;
    List<String> tokensCorpora;
    List<Lemma> normalizedLemmasCorpora;
    List<ArrayList<Lemma>> normalizedTextsLemmas;
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

    public Concordance(List<Lemma> ph, List<Lemma> corpora, int n, Dictionary dictionary,
                       ArrayList<Integer> indexesMatch){
        this.number = n;
        this.normalizedPhLemmas = ph;
        this.normalizedLemmasCorpora = corpora;
        this.indexesMatch = indexesMatch;
        compute(n);
    }

    public Concordance(List<Lemma> phrase, ArrayList<ArrayList<Lemma>> corporaLemmas, int n, Dictionary dict, ArrayList<Integer> indexesInTexts, ArrayList<Integer> indexesOfTexts){
        this.number = n;
        this.normalizedPhLemmas = phrase;
        this.normalizedTextsLemmas = corporaLemmas;
        this.indexesMatch = indexesInTexts;
        this.indexesOfTexts = indexesOfTexts;
        computeConcordancesInTexts(n);
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

    private void computeConcordancesInTexts(int n) {
        // finding left contexts
        for (int idx = 0; idx < indexesMatch.size(); idx++){
            List<Lemma> textLemmas = normalizedTextsLemmas.get(indexesOfTexts.get(idx));
            Integer i = indexesMatch.get(idx);

            int startIdx = Math.max(i - n, 0);
            int endIdx = i + normalizedPhLemmas.size();
            for (; startIdx < i; startIdx++){
                ArrayList<Lemma> contextLemmas = new ArrayList<>();
                for (int j = startIdx; j<endIdx; j++)
                    contextLemmas.add(textLemmas.get(j));
                Context c = new Context(contextLemmas);
                if (leftContexts.get(c) == null)
                    leftContexts.put(c, new Stats(1, 0));
                else
                    leftContexts.get(c).count ++;
            }
        }

        for (int idx = 0; idx < indexesMatch.size(); idx++){
            int startIdx = indexesMatch.get(idx);
            List<Lemma> textLemmas = normalizedTextsLemmas.get(indexesOfTexts.get(idx));
            int endIdx = Math.min(startIdx + textLemmas.size() + n, textLemmas.size());
            for (int endIdx2 = startIdx + textLemmas.size(); endIdx2 <= endIdx; endIdx2++){
                ArrayList<Lemma> contextLemmas = new ArrayList<>();
                for (int j = startIdx; j<endIdx2; j++)
                    contextLemmas.add(textLemmas.get(j));
                Context c = new Context(contextLemmas);
                if (rightContexts.get(c) == null)
                    rightContexts.put(c, new Stats(1, 0));
                else
                    rightContexts.get(c).count ++;
            }
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

    public HashMap<Context, Stats> getLeftContexts() {
        return leftContexts;
    }

    public HashMap<Context, Stats> getRightContexts() {
        return rightContexts;
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

    public static List<String> splitTextsNewLine(String corpora) {
        List<String> basedTexts = Arrays.stream(corpora.toLowerCase().split("[\n]")).
                filter((x) -> x.length() > 0).toList();

        assert (basedTexts.size() % 2 == 0);
        ArrayList<String> texts = new ArrayList<>();
        for (int i = 0; i < basedTexts.size()-1; i+=2)
            texts.add(basedTexts.get(i) + '\n' + basedTexts.get(i+1));
        return texts;
    }

    public static List<List<String>> splitTextsToken(String corpora) {
        List<String> texts = splitTextsNewLine(corpora);
        List<List<String>> tokens = new ArrayList<>();
        for (String token : texts)
            tokens.add(tokenize(token));

        return tokens;
    }
}

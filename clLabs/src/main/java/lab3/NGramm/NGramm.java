package lab3.NGramm;

import lab3.Dictionary.Dictionary;
import lab3.Dictionary.Lemma;
import lab3.concordance.Concordance;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.lang.Math;

public class NGramm {
    private final Dictionary dictionary;
    private final int MAX_NUM_LIMIT = 20;
    private final int MIN_NUM_LIMIT = 2;
    private final ArrayList<ArrayList<Lemma>> normalizedLemmas;
    private HashMap<List<Lemma>, Stats> ngrammsStats;
    private int countOfTexts = 0;
    private double threshold;
    private List<Map.Entry<List<Lemma>, Stats>> getFiltered;

    public NGramm(String corpora, Dictionary dict, Double threshold) throws IOException {
        int totalWithoutFilter = 0;
        int totalWithFilter = 0;

        this.dictionary = dict;
        this.threshold = threshold;
        List<List<String>> textTokens = Concordance.splitTextsToken(corpora);
        this.normalizedLemmas = dict.lemmatizeTextTokens(textTokens);

        System.out.println("normalizedLemmas size: " + normalizedLemmas.size());
        countOfTexts = normalizedLemmas.size();
        int maxn = MIN_NUM_LIMIT;
        for (int n = MIN_NUM_LIMIT; n <= MAX_NUM_LIMIT; n++) {
            ngrammsStats = new HashMap<>();
            boolean founded = false;
            for (int textIdx = 0; textIdx < normalizedLemmas.size(); textIdx++) {
                ArrayList<Lemma> textLemmas = normalizedLemmas.get(textIdx);
                for (int i = 0; i < textLemmas.size()-n+1; i++){
                    List<Lemma> ngramm = textLemmas.subList(i, i+n);
                    Stats stats = ngrammsStats.get(ngramm);
                    if (stats == null)
                        ngrammsStats.put(ngramm, new Stats(1, i, textIdx));
                    else {
                        stats.countOfWords = textLemmas.size();
                        stats.count ++;
                        stats.indexesInTexts.add(i);
                        stats.indexesOfTexts.add(textIdx);
                        founded = true;
                    }
                }
            }
            if (!founded)
                break;
            System.out.println("n = " + n);
            filterStability(n);

            String absPathToTexts = "C:\\Users\\User\\IdeaProjects\\Cllabs\\clLabs\\src\\Main\\java\\source\\ngramms\\";
            printOverallStatistics(absPathToTexts + n + "-gramms.txt");

            if (getFiltered != null){
                printFiltered(absPathToTexts + n + "-filtered.txt");
                totalWithFilter += getFiltered.size();
            }

            totalWithoutFilter += ngrammsStats.size();


            getFiltered = null;
            ngrammsStats = null;
            maxn = n;
        }
        System.out.println("Ngramms founded, n = [2.." + maxn + "]");
        System.out.println("ngramms passed after filter: " + totalWithFilter + " / " + totalWithoutFilter);
    }

    private void filterStability(int n) {
        var entries = ngrammsStats.entrySet().stream().filter(e -> e.getValue().count>1).
                sorted((e1, e2) -> -1 * e1.getValue().count.compareTo(e2.getValue().count)).toList();

        this.getFiltered = new ArrayList<>();
        for (Map.Entry<List<Lemma>, Stats> entry : entries){
            if (entry.getValue().count < 2)
                continue;
            Concordance concordance = new Concordance(entry.getKey(), this.normalizedLemmas, 1, dictionary, entry.getValue().indexesInTexts, entry.getValue().indexesOfTexts);
            var axn = concordance.getLeftContexts().entrySet().stream().filter(e -> e.getKey().lemmata.size() >= n+1).
                    max(Comparator.comparing(e -> e.getValue().count));
            var xnb = concordance.getRightContexts().entrySet().stream().filter(e -> e.getKey().lemmata.size() >= n+1).
                    max(Comparator.comparing(e -> e.getValue().count));
            Integer fax;
            Integer fxb;
            if (axn.isPresent())
                fax = axn.get().getValue().count;
            else fax = 0;
            if (xnb.isPresent())
                fxb = xnb.get().getValue().count;
            else fxb = 0;

            entry.getValue().stability = Double.max((1. * fax / entry.getValue().count), (1. * fxb / entry.getValue().count));
            entry.getValue().maxExtensionFrequency = Integer.max(fax, fxb);
            if (entry.getValue().stability <= threshold){
                getFiltered.add(entry);
            }
        }
    }

    public void printOverallStatistics(String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        var beforeSort = ngrammsStats.entrySet().stream().sorted((e1, e2) -> -1 * e1.getValue().count.compareTo(e2.getValue().count)).toList();
        for (Map.Entry<List<Lemma>, Stats> e : beforeSort) {
            if (e.getValue().count < 2)
                continue;
            double tf = (double) e.getValue().indexesInTexts.size() / e.getValue().countOfWords;
            double idf = Math.log10((double) countOfTexts / e.getValue().indexesOfTexts.stream().distinct().count());
            double tf_idf = tf * idf;
            e.getValue().setTf_idf(tf_idf);
        }

        var entries = ngrammsStats.entrySet().stream().sorted(Comparator.comparing(e -> -1 * e.getValue().tf_idf)).toList();
        for (Map.Entry<List<Lemma>, Stats> e : entries) {
            if (e.getValue().count < 2)
                continue;
            writer.write("[");
            for (Lemma l : e.getKey())
                writer.write(l.startingForm.word + " ");
            writer.write("]");
            double tf = (double) e.getValue().indexesInTexts.size() / e.getValue().countOfWords;
            double idf = Math.log10((double) countOfTexts / e.getValue().indexesOfTexts.stream().distinct().count());
            double tf_idf = tf * idf;
            writer.write(":  count: " + e.getValue().count + "  textCnt: " +
                    e.getValue().indexesOfTexts.stream().distinct().count() +
                    " tf-idf: " + tf_idf + " tf: " + tf + " idf: " + idf + "\n");
        }
        writer.close();
    }

    public void printFiltered(String path) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write("stability with threshold: " + threshold + "\n");
        var entries = getFiltered.stream().sorted((e1, e2) -> -1 * e1.getValue().count.compareTo(e2.getValue().count)).toList();
        for (Map.Entry<List<Lemma>, Stats> e : entries) {
            writer.write("[");
            for (Lemma l : e.getKey())
                writer.write(l.startingForm.word + " ");
            long textsCount = e.getValue().indexesOfTexts.stream().distinct().count();
            writer.write("] count:" + e.getValue().count + " textsCount:" + textsCount + " stability:" + e.getValue().stability + "\n");
        }
        writer.close();
    }
}

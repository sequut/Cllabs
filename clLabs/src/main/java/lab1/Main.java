package lab1;

import lab1.Dictionary.Dictionary;
import lab1.Dictionary.Lemma;
import lab1.Dictionary.LemmaSupposed;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws XMLStreamException, IOException {
        String absPath = "C:\\Users\\User\\IdeaProjects\\Cllabs\\clLabs\\src\\Main\\java\\source\\";
        Dictionary dictionary = new Dictionary(absPath + "dict.opcorpora.xml");
        System.out.println("mapping size: " + dictionary.lemmaSupposed.size());
        HashMap<Lemma, Integer> frequency = new HashMap<>();
        HashMap<String, Integer> errorTokenFreq = new HashMap<>();

        File file = new File(absPath + "forKirill.txt");
        Scanner reader = new Scanner(file, "windows-1251");
        int totalCount = 0;
        int definitelyCount = 0;
        int ambiguousCount = 0;
        int possibleLemmasCount = 0;
        BufferedWriter writerErr = new BufferedWriter(new FileWriter(absPath + "errors.txt"));
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            List<String> tokens = tokenize(removeAllDigit(data));

            for (String token : tokens) {
                totalCount++;
                LemmaSupposed possibleLemmas = dictionary.lemmaSupposed.get(token);
                if (possibleLemmas != null) {
                    ArrayList<Lemma> lemmas = possibleLemmas.getNormalizedLemmas();
                    if (lemmas.size() == 1)
                        definitelyCount++;
                    else if (lemmas.size() > 1)
                        ambiguousCount++;
                    possibleLemmasCount += lemmas.size();
                    for (Lemma curr_lemma : lemmas) {
                        frequency.merge(curr_lemma, 1, Integer::sum);
                    }
                }
                else
                    errorTokenFreq.merge(token, 1, Integer::sum);
            }
        }
        reader.close();

        System.out.println("Tokens count: " + totalCount);
        System.out.println("Found unique lemmas :" + frequency.size());
        System.out.println("Found lemmas for " + (definitelyCount + ambiguousCount) + " tokens over total "
                + totalCount + " tokens (" + (1. * definitelyCount + ambiguousCount)/totalCount + ")");
        System.out.println("Definite tokens: " + definitelyCount);
        System.out.println("Ambiguous tokens: " + ambiguousCount);
        System.out.println("Possible lemmas count: " + possibleLemmasCount);
        System.out.println("Not lemmatized: " + (totalCount - definitelyCount - ambiguousCount));

        var sortedFrequency = frequency.entrySet().stream().sorted((o1, o2) -> -1 * o1.getValue().compareTo(o2.getValue())).toList();
        var sortedFreqErr = errorTokenFreq.entrySet().stream().sorted((o1, o2) -> -1 * o1.getValue().compareTo(o2.getValue())).toList();

        BufferedWriter writer = new BufferedWriter(new FileWriter(absPath + "output.txt"));
        BufferedWriter writerN = new BufferedWriter(new FileWriter(absPath + "nouns.txt"));
        BufferedWriter writerP = new BufferedWriter(new FileWriter(absPath + "prepositions.txt"));
        BufferedWriter writerV = new BufferedWriter(new FileWriter(absPath + "verbs.txt"));
        BufferedWriter writerA = new BufferedWriter(new FileWriter(absPath + "adjectives.txt"));

        for (Map.Entry<Lemma, Integer> e : sortedFrequency) {
            writeFrequency(e, writer);
            switch (e.getKey().startingForm.grammemes.get(0)) {
                case "NOUN" -> writeFrequency(e, writerN);
                case "PREP" -> writeFrequency(e, writerP);
                case "VERB" -> writeFrequency(e, writerV);
                case "ADJF" -> writeFrequency(e, writerA);
            }
        }
        for (Map.Entry<String, Integer> e : sortedFreqErr) {
            writerErr.write("Error: \"" + e.getKey() + "\" count: " + e.getValue() + '\n');
        }
        writerErr.close();
        writer.close();
        writerN.close();
        writerP.close();
        writerV.close();
        writerA.close();
        System.out.println("that's all");
    }


    private static List<String> tokenize(String text) {
        return Arrays.stream(text.replace("\n", "").toLowerCase().
                        split("[«;»  \",—.()?:!'+/-]")).
                        filter((x) -> x.length() > 0).collect(Collectors.toList());
    }

    private static void writeFrequency(Map.Entry<Lemma, Integer> e, BufferedWriter writer) {
        try {
            writer.write('\"' + e.getKey().startingForm.word + '\"' + ", " +
                    e.getKey().startingForm.grammemes.get(0) + ", " +
                    e.getValue() + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String removeAllDigit(String str) {
        char[] charArray = str.toCharArray();
        StringBuilder result = new StringBuilder();

        for (char c : charArray)
            if (!Character.isDigit(c))
                result.append(c);

        return result.toString();
    }
}

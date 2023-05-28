package lab4.model;

import lab4.Dictionary.*;
import lab4.Dictionary.Dictionary;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.time.Duration;

public class Main {
    public static void main(String[] args) throws IOException, XMLStreamException {
        String absPathToTexts = "C:\\Users\\User\\IdeaProjects\\Cllabs\\clLabs\\src\\Main\\java\\source\\";
        System.out.println(absPathToTexts);
        String corpora = Files.readString(Paths.get(absPathToTexts + "texts.txt"), Charset.forName("windows-1251"));
        Dictionary dictionary = new Dictionary(absPathToTexts + "dict.opcorpora.xml");

        String absPathToLab4 = "C:\\Users\\User\\IdeaProjects\\Cllabs\\clLabs\\src\\Main\\java\\lab4\\";

        System.out.println("Finding n-gramms...");
        Instant start = Instant.now();

        ArrayList<Model> models = Model.loadModels(absPathToLab4 + "models.txt",
                absPathToLab4 + "model_elements.txt", dictionary);
        List<String> sentences = Helper.splitOnSentences(corpora);
        List<List<String>> sentencesTokens = Helper.splitSentencesTokens(sentences);
        ArrayList<ArrayList<Lemma>> sentencesLemmas = dictionary.lemmatizeTextTokens(sentencesTokens);
        HashMap<Model, ArrayList<Integer>> foundedSentences = Model.findModels(sentencesLemmas);
        Model.printFoundModels(foundedSentences, sentences, absPathToLab4 + "output.txt");

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Finished");
        System.out.println("Elapsed time: " + timeElapsed + "ms");
    }
}

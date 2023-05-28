package lab3.NGramm;

import lab3.Dictionary.Dictionary;
import lab3.concordance.Concordance;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) throws IOException, XMLStreamException{
        System.out.println("running program");
        String absPathToTexts = "C:\\Users\\User\\IdeaProjects\\Cllabs\\clLabs\\src\\Main\\java\\source\\";

        Path path = Paths.get(absPathToTexts + "texts.txt");
        String text = Files.readString(path, Charset.forName("windows-1251"));
        Dictionary dictionary = new Dictionary(absPathToTexts + "dict.opcorpora.xml");
        System.out.println("Finding n-gramms");
        Instant start = Instant.now();

        NGramm nGramm = new NGramm(text, dictionary, 0.1);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Finished");
        System.out.println("Elapsed time: " + timeElapsed + "ms");
    }
}

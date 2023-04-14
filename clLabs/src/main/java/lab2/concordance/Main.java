package lab2.concordance;

import lab2.Dictionary.Dictionary;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) throws IOException, XMLStreamException {
        String absPath = "C:\\Users\\User\\IdeaProjects\\Cllabs\\clLabs\\src\\main\\java\\source\\";

        BufferedReader reader = new BufferedReader(
                new FileReader(absPath + "input.txt"));
        String phrase = reader.readLine();
        int n = Integer.parseInt(reader.readLine());
        Integer threshold = Integer.parseInt(reader.readLine());
        String corpora = Files.readString(Paths.get(absPath + "forKirill.txt"), Charset.forName("windows-1251"));
        Dictionary dictionary = new Dictionary(absPath + "source/dict.opcorpora.xml");

        System.out.println("Finding concordances...");
        Instant start = Instant.now();
        Concordance concordance = new Concordance(phrase, corpora, n, dictionary);
        concordance.printStats(absPath + "output.txt", threshold);
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Finished");
        System.out.println("Elapsed time: " + timeElapsed + "ms");
    }
}

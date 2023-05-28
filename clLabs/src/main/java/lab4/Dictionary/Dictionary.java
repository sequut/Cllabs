package lab4.Dictionary;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dictionary {
    ArrayList<Lemma> lemmata = new ArrayList<>();
    public HashMap<String, LemmaSupposed> lemmaSupposed = new HashMap<>();
    ArrayList<Grammeme> grammemes = new ArrayList<>();

    public Dictionary(String pathXML) throws IOException, XMLStreamException{
        XMLInputFactory streamFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = streamFactory.createXMLStreamReader(new FileInputStream(pathXML), "utf-8");

        Grammeme grammeme = new Grammeme();
        Lemma lemma = new Lemma();
        FormWord formWord = new FormWord();

        while (reader.hasNext()){
            int eventType = reader.getEventType();
            switch (eventType) {
                case XMLStreamConstants.START_ELEMENT -> {
                    switch (reader.getLocalName()) {
                        case "dictionary" -> System.out.println("parsing starts");
                        case "grammeme" -> grammeme = new Grammeme();
                        case "name" -> {
                            reader.next();
                            if (reader.getEventType() == XMLStreamConstants.CHARACTERS)
                                grammeme.name = reader.getText().trim();
                            else
                                throw new IllegalStateException("tag without text");
                        }
                        case "alias" -> {
                            reader.next();
                            if (reader.getEventType() == XMLStreamConstants.CHARACTERS)
                                grammeme.alias = reader.getText().trim();
                            else
                                throw new IllegalStateException("tag without text");
                        }
                        case "description" -> {
                            reader.next();
                            if (reader.getEventType() == XMLStreamConstants.CHARACTERS)
                                grammeme.description = reader.getText().trim();
                            else
                                throw new IllegalStateException("tag without text");
                        }
                        case "lemma" -> lemma = new Lemma(Integer.valueOf(reader.getAttributeValue(0)));
                        case "l" -> {
                            formWord = new FormWord();
                            if (reader.getAttributeCount() == 1 && reader.getAttributeLocalName(0).equals("t")) {
                                lemma.startingForm = formWord;
                                formWord.word = reader.getAttributeValue(0);
                            }
                            else
                                throw new IllegalStateException("error with handling <l> tag");
                        }
                        case "f" -> {
                            formWord = new FormWord();
                            if (reader.getAttributeCount() == 1 && reader.getAttributeLocalName(0).equals("t"))
                                formWord.word = reader.getAttributeValue(0);
                            else
                                throw new IllegalStateException("error with handling <f> tag");
                        }
                        case "g" -> {
                            if (reader.getAttributeCount() == 1 && reader.getAttributeLocalName(0).equals("v"))
                                formWord.grammemes.add(reader.getAttributeValue(0));
                            else
                                throw new IllegalStateException("error with handling <g> tag");
                        }
                    }
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    switch (reader.getLocalName()) {
                        case "grammeme" -> this.grammemes.add(grammeme);
                        case "lemma" -> this.lemmata.add(lemma);
                        case "l", "f" -> {
                            if (formWord.word == null)
                                throw new NullPointerException("word in formWord is null");

                            LemmaSupposed lemmaSupposed = this.lemmaSupposed.get(formWord.word);
                            if (lemmaSupposed == null) {
                                LemmaSupposed pos = new LemmaSupposed();
                                pos.wordForms.add(formWord);
                                pos.lemmasSupposed.add(lemma);
                                this.lemmaSupposed.put(formWord.word, pos);
                            }
                            else {
                                lemmaSupposed.wordForms.add(formWord);
                                lemmaSupposed.lemmasSupposed.add(lemma);
                            }
                        }
                        case "dictionary" -> System.out.println("parsing finished" +
                                "\ngrammemes number: " + grammemes.size() +
                                "\nlemmas number: " + lemmata.size());
                    }
                }
            }
            reader.next();
        }

        lemmata.add(new Lemma(1000000, new FormWord(".")));
        lemmata.add(new Lemma(1000001, new FormWord(",")));

        LemmaSupposed commaS = new LemmaSupposed();
        commaS.lemmasSupposed.add(new Lemma(1000001, new FormWord(",")));
        commaS.wordForms.add(new FormWord(","));

        LemmaSupposed dotS = new LemmaSupposed();
        dotS.lemmasSupposed.add(new Lemma(1000000, new FormWord(".")));
        dotS.wordForms.add(new FormWord("."));

        lemmaSupposed.put(".", dotS);
        lemmaSupposed.put(",", commaS);
    }

    public ArrayList<Lemma> getSupposedLemmas(String word){
        var result = lemmaSupposed.get(word);
        if (result == null){
            LemmaSupposed lemmaSupposed1 = new LemmaSupposed();
            lemmaSupposed1.lemmasSupposed.add(new Lemma(null, new FormWord(word)));
            lemmaSupposed1.wordForms.add(new FormWord(word));

            lemmaSupposed.put(word, lemmaSupposed1);
            return lemmaSupposed1.lemmasSupposed;
        }
        return result.lemmasSupposed;
    }

    public Lemma lemmatizeWord(String word){
        return getSupposedLemmas(word).get(0);
    }

    public ArrayList<Lemma> lemmatizeTokens(List<String> tokens){
        ArrayList<Lemma> result = new ArrayList<>();
        for (String i : tokens)
            result.add(lemmatizeWord(i));
        return result;
    }

    public ArrayList<ArrayList<Lemma>> lemmatizeTextTokens (List<List<String>> textTokens) {
        ArrayList<ArrayList<Lemma>> res = new ArrayList<>();
        for (List<String> text : textTokens) {
            ArrayList<Lemma> lemmas = new ArrayList<>();
            for (String w : text) {
                lemmas.add(lemmatizeWord(w));
            }
            res.add(lemmas);
        }
        return res;
    }
}

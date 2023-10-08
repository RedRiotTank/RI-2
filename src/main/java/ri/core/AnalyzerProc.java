package ri.core;

import moodAnalyzer.Mood;
import moodAnalyzer.MoodAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.*;

public class AnalyzerProc {

    String example = "esto es un !!! textito de ## ejemplillo para probar ? el analizador de textazos esto es texto de textos ejemplos";






    private static final Analyzer standardAnalyzer = new StandardAnalyzer();
    private static final Analyzer keywordAnalyzer = new KeywordAnalyzer();
    private static final Analyzer whitespaceAnalyzer = new WhitespaceAnalyzer();
    private static final Analyzer simpleAnalyzer = new SimpleAnalyzer();
    private static final SpanishAnalyzer spanishAnalyzer = new SpanishAnalyzer();

    private static final Analyzer positiveAnalyzer = new MoodAnalyzer(Mood.POSITIVE);
    private static final Analyzer negativeAnalyzer = new MoodAnalyzer(Mood.NEGATIVE);


    public static Analyzer getStandardAnalyzer() {
        return standardAnalyzer;
    }

    public static Analyzer getKeywordAnalyzer() {
        return keywordAnalyzer;
    }

    public static Analyzer getWhitespaceAnalyzer() {
        return whitespaceAnalyzer;
    }

    public static Analyzer getSimpleAnalyzer() {
        return simpleAnalyzer;
    }

    public static SpanishAnalyzer getSpanishAnalyzer() {
        return spanishAnalyzer;
    }

    public static Analyzer getPositiveAnalyzer() {
        return positiveAnalyzer;
    }

    public static Analyzer getNegativeAnalyzer() {
        return negativeAnalyzer;
    }



    public static Map<String, Integer> countTokenFrequencies(Analyzer analyzer, String text) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream(null, text);
        Map<String, Integer> tokenFrequencyMap = new HashMap<>();

        tokenStream.reset();

        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);


        while (tokenStream.incrementToken()) {
            String token = charTermAttribute.toString();
            tokenFrequencyMap.put(token, tokenFrequencyMap.getOrDefault(token, 0) + 1);
        }

        tokenStream.end();
        tokenStream.close();

        return tokenFrequencyMap;

    }


    private void iterateText(Analyzer analyzer, String text) throws IOException {
        TokenStream result = analyzer.tokenStream(null, text);


        result.reset();

        while (result.incrementToken())
            System.out.println(result.getAttribute(CharTermAttribute.class).toString());

        result.end();
        result.close();
    }

    public static ArrayList<Analyzer> getAnalyzers(){
        return  new ArrayList<>(
                Arrays.asList(
                        getStandardAnalyzer(),
                        getKeywordAnalyzer(),
                        getWhitespaceAnalyzer(),
                        getSimpleAnalyzer(),
                        getSpanishAnalyzer()
                ));
    }





}

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
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilterFactory;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizerFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.SpanishStemmer;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.*;


import java.io.IOException;
import java.util.*;

public class AnalyzerProc extends Analyzer{


    private static String tokenFilterString = "";
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


    public AnalyzerProc(String tokenFilter) {
        tokenFilterString = tokenFilter;
    }
    // metodo de analyzer sobrecargado para poder cambiarle el tokenFilter
    @Override
    protected Analyzer.TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenFilter tokenFilter;


       CharArraySet stopWords = createWordsToDelete();

        SynonymMap synonymMap = null;
        try {
            synonymMap = generateSynonymsMap();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        tokenFilter = switch (tokenFilterString) {
            case "lowercase" -> new LowerCaseFilter(source); // pasa a minusculas
            case "stop" -> new StopFilter(source, stopWords); // elimina las palabras que queramos
            case "snowball" -> new SnowballFilter(source, new SpanishStemmer()); // se queda con la raiz de la palabra
            case "shingle" -> new ShingleFilter(source); // hace combinaciones de tokens para la busqueda
            case "edgeN" -> new EdgeNGramTokenFilter(source,1,3); // crea bigramas de tamaño entre min y max, DESDE LOS BORDES . El booleano es para conservar el original o no
            case "Ngram" -> new NGramTokenFilter(source,1,3); // crea bigramas sin importarle los bordes, genera todas las combinaciones del tamaño pasado
            case "commom" -> new CommonGramsFilter(source, stopWords); // genera bigramas con las palabras comunes pasadas que se encuentren en el texto
            case "synonym" -> new SynonymGraphFilter(source, synonymMap, true);
            default -> new StandardFilter(source);
        };
        return new Analyzer.TokenStreamComponents(source, tokenFilter);
    }
    // editar para cambiar las palaabras a eliminar en el StopFilter
    private CharArraySet createWordsToDelete(){ // para el StopFilter
        CharArraySet stopWords = new CharArraySet(4, true);
        stopWords.add("la");
        stopWords.add("el");
        stopWords.add("un");
        stopWords.add("y");
        return stopWords;
    }
    // para el synonym: cambiar para añadir o quitar sinonimos
    private SynonymMap generateSynonymsMap() throws IOException {

        SynonymMap.Builder builder = new SynonymMap.Builder(true);

        builder.add(new CharsRef("gato"), new CharsRef("felino"), true);
        builder.add(new CharsRef("perro"), new CharsRef("canino"), true);

        return builder.build();
    }
    // metodo que saca por pantalla el resultado de analizar un texto con un tokenFilter en especifico
    public void printTokenFilterResults(File file) throws IOException {
        TokenStream tokenStream = this.tokenStream("field", TextProc.getFileText(file));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        while (tokenStream.incrementToken()){
            System.out.println(charTermAttribute.toString());
        }
        tokenStream.end();
        tokenStream.close();
    }

}

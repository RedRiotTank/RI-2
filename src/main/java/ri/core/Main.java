package ri.core;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilterFactory;
import org.apache.lucene.analysis.standard.ClassicTokenizerFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        if(args.length < 2){
            System.out.println("Faltan parametros");
            return;
        }
        String analyzer = args[0];
        String text = args[1];
        StringBuilder analyzed_text = new StringBuilder();

        Analyzer an = createAnalyzer(analyzer);
        TokenStream sf = an.tokenStream(null, text);
        int tam = 0;
        sf.reset();
        while (sf.incrementToken()) { // se obtienen los tokens tras pasarle el analizador
            tam++;
            analyzed_text.append(sf.getAttribute(CharTermAttribute.class)).append(" "); // separados con un espacio para luego poder contar los términos, el \n lo pone junto
        }
        // se obtiene el numero de tokens
        System.out.println("Tras pasar el " + analyzer + " analyzer se obtienen " + tam + " tokens");
        System.out.println(analyzed_text);
        Map<String, Integer> cuenta_terminos = countTermFrequencies(analyzed_text.toString()); // se saca el map con los tokens y sus frecuencias. (objetivo de la practica)

        // TODO: ordenar los tokens por frecuencia y poder pintarlo o guardarlo en archivo
        sf.end();
        sf.close();
        //showText(an, text);
    }

    private static Analyzer createAnalyzer(String analyzer){
        return switch (analyzer) {
            case "standard" -> new StandardAnalyzer();
            case "keyword" -> new KeywordAnalyzer();
            case "whitespace" -> new WhitespaceAnalyzer();
            case "simple" -> new SimpleAnalyzer();
            default -> null;
        };
    }
    public static void showText(Analyzer an, String text) throws IOException {
        TokenStream sf = an.tokenStream(null, text);

        sf.reset();
        while (sf.incrementToken()) {
            System.out.println(sf.getAttribute(CharTermAttribute.class));
        }
        sf.end();
        sf.close();
    }

    public static List<Map.Entry<String, Integer>> getOrderedWordCount(String content){
        Map<String, Integer> termFrequencyMap = countTermFrequencies(content);

        return orderTerms(termFrequencyMap);
    }

    public static Analyzer crearAnalyzer() throws IOException {

        Analyzer lineaAnalyzer = CustomAnalyzer.builder()
                .addCharFilter(PatternReplaceCharFilterFactory.NAME, "pattern", "[/(/)/{/};]", "replacement", "X")
                .withTokenizer(ClassicTokenizerFactory.NAME)
                .addTokenFilter(LowerCaseFilterFactory.NAME)
                .build();
        System.out.println(lineaAnalyzer.getClass());
        return lineaAnalyzer;

    }

    private static Map<String, Integer> countTermFrequencies(String content) {
        Map<String, Integer> termFrequencyMap = new HashMap<>();
        String[] words = content.split(RegexController.SPACE.getRegex());

        for (String word : words)
            termFrequencyMap.put(word, termFrequencyMap.getOrDefault(word, 0) + 1);

        return termFrequencyMap;
    }

    private static List<Map.Entry<String, Integer>> orderTerms(Map<String, Integer> termFrequencyMap){
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(termFrequencyMap.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        return sortedEntries;
    }

    /*
    public void generateFCSVwc(List<Map.Entry<String, Integer>> sortedEntries, String filename){
        String csvPath = FOLDER_PATH + filename + ".csv";

        try (FileWriter csvWriter = new FileWriter(csvPath)) {
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                csvWriter.write(entry.getKey() + ";" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error con el fichero " + csvPath);
        }

        System.out.println("CSV generado para " + filename);
    }

     */

}
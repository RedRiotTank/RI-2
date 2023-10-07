package ri.core;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.SpanishStemmer;

import java.io.IOException;

public class CustomAnalyzerTF extends Analyzer {
    private final String tokenFilterString;

    public CustomAnalyzerTF(String tokenFilter) {
        tokenFilterString = tokenFilter;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {

        Tokenizer source = new StandardTokenizer();
        TokenFilter tokenFilter;

        CharArraySet stopWords = createWordsToDelete();
        SynonymMap synonymMap = null;
        try{
            synonymMap = generateSynonymsMap();
        } catch (IOException e){
            e.printStackTrace();
        }


        tokenFilter = switch (tokenFilterString) {
            case "lowercase" -> new LowerCaseFilter(source); // pasa a minusculas
            case "stop" -> new StopFilter(source, stopWords); // elimina las palabras que queramos
            case "snowball" -> new SnowballFilter(source, new SpanishStemmer()); // se queda con la raiz de la palabra
            case "shingle" -> new ShingleFilter(source); // hace combinaciones de tokens para la busqueda
            case "edgeN" -> new EdgeNGramTokenFilter(source,1,2,true); // crea bigramas de tamaño entre min y max, DESDE LOS BORDES . El booleano es para conservar el original o no
            case "Ngram" -> new NGramTokenFilter(source,1,2,true); // crea bigramas sin importarle los bordes, genera todas las combinaciones del tamaño pasado
            case "commom" -> new CommonGramsFilter(source, stopWords); // genera bigramas con las palabras comunes pasadas que se encuentren en el texto
            case "synonym" -> new SynonymGraphFilter(source, synonymMap, true);
            default -> new StandardFilter(source);
        };
        return new TokenStreamComponents(source, tokenFilter);
    }

    private CharArraySet createWordsToDelete(){ // para el StopFilter
        CharArraySet stopWords = new CharArraySet(4, true);
        stopWords.add("la");
        stopWords.add("el");
        stopWords.add("un");
        stopWords.add("y");
        return stopWords;
    }

    private SynonymMap generateSynonymsMap() throws IOException {

        SynonymMap.Builder builder = new SynonymMap.Builder(true);

        builder.add(new CharsRef("gato"), new CharsRef("felino"), true);
        builder.add(new CharsRef("perro"), new CharsRef("canino"), true);

        SynonymMap synonymMap = builder.build();
        return synonymMap;
    }

}


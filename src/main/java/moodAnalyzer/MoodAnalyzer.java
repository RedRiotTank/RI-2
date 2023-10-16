package moodAnalyzer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.hunspell.Dictionary;
import org.apache.lucene.analysis.hunspell.HunspellStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.store.FSDirectory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;

public class MoodAnalyzer extends Analyzer {

    private Mood mood;    //positive-negative-neutral



    public MoodAnalyzer(Mood mood){
        this.mood = mood;
    }

    public Mood getMood() {
        return mood;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        Dictionary dictionary = null;

        try {
            InputStream affixStream = getClass().getResourceAsStream("/es_ES.aff");
            InputStream dictStream = getClass().getResourceAsStream("/es_ES.dic");
            FSDirectory directorioTemp;

            directorioTemp = FSDirectory.open(Paths.get("/temp"));
            if(affixStream != null)
                dictionary = new Dictionary(directorioTemp, "temporalFile", affixStream, dictStream);

        } catch (Exception ignored) {}


        TokenStream result;

        result = new HunspellStemFilter(source, dictionary, true, true);

        try {
            result = new MoodFilter(result, mood);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        return new TokenStreamComponents(source, result);
    }

}

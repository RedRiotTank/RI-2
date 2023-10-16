package moodAnalyzer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import ri.core.FileProc;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

public class MoodFilter extends TokenFilter {
    private final CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);
    private final Set<String> positiveWords;
    private final Set<String> negativeWords;
    Mood mood;

    protected MoodFilter(TokenStream input, Mood mood) throws FileNotFoundException {
        super(input);
        positiveWords = FileProc.getMapedWords(System.getProperty("user.dir") + "\\src\\main\\resources\\positiveWords.txt");
        negativeWords = FileProc.getMapedWords(System.getProperty("user.dir") + "\\src\\main\\resources\\negativeWords.txt");
        this.mood = mood;
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (input.incrementToken()) {
            String word = charTermAttribute.toString();


            switch (mood){
                case POSITIVE:
                    if (positiveWords.contains(word))
                        return true;
                    break;
                case NEGATIVE:
                    if (negativeWords.contains(word))
                        return true;
                    break;
                case NEUTRAL:
                    if (positiveWords.contains(word) || negativeWords.contains(word))
                        return true;
                    break;
            }


        }
        return false;
    }


}

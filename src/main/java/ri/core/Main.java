package ri.core;

import moodAnalyzer.Mood;
import moodAnalyzer.MoodAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, TikaException {

        if(args.length < 3){
            System.out.println("No hay suficientes argumentos");
            return;
        } else if(args.length > 3){
            System.out.println("Más argumentos de los necesarios");
            return;
        }
        String analyzer = args[0];
        String text = args[1];
        StringBuilder analyzed_text = new StringBuilder();

        String dir = args[0];
        String option = args[1];
        String filter = args[2];

        FileProc fp = new FileProc(dir);
        TextProc tp = new TextProc(fp);
        AnalyzerProc ap = new AnalyzerProc(filter);

        ConsoleProc cp = new ConsoleProc(fp, tp, ap);

        switch (option){
            case "-d":
                cp.printTable();

                break;
            case "-l":
                //tp.getAllLinks();
                cp.showAFLinks();

                //cp.showFileLinks();
                break;
            case "-t":
                fp.generateAllFilesTextProcWordCount();
                break;

            case "-allAnalyzers":
                fp.generateAllFilesAllAnalyzersWordCount();
                break;
            case "-tokenFilter":
                cp.processFolderPrintTokenFilter();
                break;

            case "-checkMood":
                fp.generateAllFilesMoodWordCount();
        }


        System.out.println();
        System.out.println("El programa se ha ejecutado correctamente");
    }
}
package ri.core;

import org.apache.tika.exception.TikaException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, TikaException {

        if(args.length < 2){
            System.out.println("Error in argument number");
            Utils.printHelp();
            return;
        }

        String dir = args[0];
        String option = args[1];
        String filter = "";

        if(option.equals("-tokenFilter")){
            if(args.length < 3){
                System.out.println("Error in argument number");
                Utils.printHelp();
                return;
            } else filter = args[2];
        } else if(args.length > 2){
            System.out.println("Error in argument number");
            Utils.printHelp();
            return;
        }

        FileProc fp = new FileProc(dir);
        TextProc tp = new TextProc(fp);
        AnalyzerProc ap = new AnalyzerProc(filter);

        ConsoleProc cp = new ConsoleProc(fp, tp, ap);

        switch (option){
            case "-d":
                cp.printTable();
                break;
            case "-l":
                cp.showAFLinks();
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
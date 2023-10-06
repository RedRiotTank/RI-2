package ri.core;

import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, TikaException {

        if(args.length < 2){
            System.out.println("No hay suficientes argumentos");
            return;
        } else if(args.length > 2){
            System.out.println("Más argumentos de los necesarios");
            return;
        }

        String dir = args[0];
        String option = args[1];

        FileProc fp = new FileProc(dir);
        TextProc tp = new TextProc(fp);
        AnalyzerProc ap = new AnalyzerProc();

        ConsoleProc cp = new ConsoleProc(fp, tp);

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

        }

        System.out.println();
        System.out.println("El programa se ha ejecutado correctamente");
    }
}
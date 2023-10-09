package ri.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class ConsoleProc {
    private final FileProc fp;
    private final TextProc tp;

    private final AnalyzerProc ap;


    ConsoleProc(FileProc fp, TextProc tp, AnalyzerProc ap){
        this.fp = fp;
        this.tp = tp;
        this.ap = ap;
    }

    public void printTable() {
        ArrayList<String> names = fp.getAFNames();
        ArrayList<String> formats = tp.getAFFormats();
        ArrayList<String> encodings = tp.getEncodings();
        ArrayList<String> languages = tp.getLanguages();

        int maxSize = names.size(); // todos los arrays tienen el mismo tamaño

        System.out.printf("%-50s %-20s %-35s %-15s%n", "Name", "Format", "Encoding", "Language");

        System.out.println("------------------------------------------------------------" +
                "------------------------------------------------------");

        for (int i = 0; i < maxSize; i++) {
            String name = i < names.size() ? names.get(i) : "";
            String format = i < formats.size() ? formats.get(i) : "";
            String encoding = i < encodings.size() ? encodings.get(i) : "";
            String language = i < languages.size() ? languages.get(i) : "";

            Locale locale = new Locale(language);
            language = locale.getDisplayLanguage();

            System.out.printf("%-50s %-20s %-35s %-15s%n", name, format, encoding, language);
        }
    }

    public void showAFLinks() {
        System.out.println("-------------------------------------------------------------");

        for (File file : fp.getFiles()) {

            System.out.println("File " + file.getName());
            Set<String> links = tp.getFlinks(file);
            for (String link : links)
                System.out.println("    " + link + "\n");
            System.out.println("-------------------------------------------------------------");
        }
    }

    public void processFolderPrintTokenFilter() throws IOException {
        File folder = new File(fp.getFolferPath());
        if(folder.isDirectory()){
            File[] files = folder.listFiles();

            if(files != null){
                for(File file : files){
                    if(file.isFile()){
                        System.out.println("Resultados tokenFilters en el fichero " + file.getName() + "\n");
                        ap.printTokenFilterResults(file);
                    }
                }
            }
        }
    }

}

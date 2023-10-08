package ri.core;

import moodAnalyzer.MoodAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class FileProc {
    private final String FOLDER_PATH;

    private final Set<File> files = new HashSet<>();

    FileProc(String folderPath) {
        this.FOLDER_PATH = folderPath;
        loadFolderFiles(folderPath);
    }

    public static Set<String> getMapedWords(String filePath){
        Set<String> words = new HashSet<>();

        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                words.add(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    private void loadFolderFiles(String folderPath){
        File folder = new File(folderPath);

        boolean isfolder = folder.isDirectory();

        if (!isfolder){ System.out.println("Path is not a folder."); return;}

        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                files.add(file);
                if (TextProc.getTika().getMaxStringLength() < file.length())
                    TextProc.getTika().setMaxStringLength((int) file.length());

            } else System.out.println("No se pudo abrir el directorio");

        }
    }

    public ArrayList<String> getAFinfo(Function<File, String> function){

        ArrayList<String> info = new ArrayList<>();

        for(File file : files) info.add(function.apply(file));

        return info;
    }
    public ArrayList<String> getAFNames(){
        return getAFinfo( (file) -> file.getName());
    }





    public void generateAllFilesTextProcWordCount() {
        for (File file : files) {
            generateTextProcWordCount(file);
        }
    }

    public void generateAllFilesMoodWordCount() throws TikaException, IOException {
        generateAllFilesAnalyzerProcWordCount(AnalyzerProc.getPositiveAnalyzer());
        generateAllFilesAnalyzerProcWordCount(AnalyzerProc.getNegativeAnalyzer());
    }

    public void generateAllFilesAllAnalyzersWordCount(){
        for(Analyzer analyzer : AnalyzerProc.getAnalyzers()){
            try {
                generateAllFilesAnalyzerProcWordCount(analyzer);
            } catch (TikaException | IOException e) {
                System.err.println("Error al procesar los ficheros");
            }
        }
    }

    public void generateAllFilesAnalyzerProcWordCount(Analyzer analyzer) throws TikaException, IOException {
        for (File file : files) {
            generateAnalyzerProcWordCount(file, analyzer);
        }
    }


    public void generateAnalyzerProcWordCount(File file,Analyzer analyzer){

        String lucene = "Lucene-";

        Map<String, Integer> frequencyMap = new HashMap<>();
        try {
            frequencyMap = AnalyzerProc.countTokenFrequencies(analyzer, TextProc.getFileText(file));
        } catch (IOException e) {
            System.err.println("Error al procesar los ficheros");
        }

        if(analyzer instanceof MoodAnalyzer)
            lucene += "Mood-" + ((MoodAnalyzer) analyzer).getMood();

        generateWordCountCSV(frequencyMap, file.getName(), lucene + analyzer.getClass().getSimpleName());
    }
    public void generateTextProcWordCount(File file){

        if(file.getName().endsWith(".csv")) return;

        Map<String, Integer> frequencyMap = new HashMap<>();
        try {
            frequencyMap = TextProc.countTermFrequencies(file);
        } catch (TikaException | IOException e) {
            System.err.println("Error al procesar los ficheros");
        }
        generateWordCountCSV(frequencyMap, file.getName(), "TextProc");
    }



    //generate file csv wordcount
    private void generateWordCountCSV(Map<String, Integer> frequencyMap, String filename, String type){
        String csvPath = FOLDER_PATH + type + " " + Utils.rmExtension(filename) + ".csv";

        try (FileWriter csvWriter = new FileWriter(csvPath)) {
            List<Map.Entry<String, Integer>> sortedEntries = Utils.orderFrequencyMap(frequencyMap);
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                csvWriter.write(entry.getKey() + ";" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error con el fichero " + csvPath);
        }

        System.out.println("CSV generado para " + filename);
    }





    public Set<File> getFiles() {
        return files;
    }

}

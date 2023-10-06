package ri.core;

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

    //generate all files csv wordcount
    public void generateAFCSVwc() throws TikaException, IOException {
        for (File file : files) {
            String filename = utils.rmExtension(file.getName());
            generateFCSVwc(TextProc.getOrderedWordCount(file), filename);
        }
    }
    //generate file csv wordcount
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

    public Set<File> getFiles() {
        return files;
    }

}

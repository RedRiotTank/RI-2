package ri.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {

    public static String rmExtension(String filename){
        return filename.substring(0, filename.lastIndexOf('.'));
    }

    public static List<Map.Entry<String, Integer>> orderFrequencyMap(Map<String, Integer> frequencyMap){
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(frequencyMap.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        return sortedEntries;
    }

    public static boolean isHTML(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".html") || name.endsWith(".htm");
    }

    public static void printHelp() {
        System.out.println("Usage: java -jar RI2.jar <directory> [option]  [filter]");

        System.out.println();

        System.out.println("Options:");

        System.out.println("    -d: Print the table of the files in the directory");
        System.out.println("    -l: Print the table of the links in the directory");
        System.out.println("    -t: Generate the word count of the files in the directory in CSV format");
        System.out.println("    -allAnalyzers: Generate the word count of the files in the directory in CSV format for all analyzers");
        System.out.println("    -tokenFilter: Print the results of analyzing the files contained in a directory with the specified token filter ");
        System.out.println("    -checkMood: Generate the Mood word count of the files in the directory in CSV format");

    }
}
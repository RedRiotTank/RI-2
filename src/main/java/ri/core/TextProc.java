package ri.core;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;

public class TextProc {
    private static final Tika tika = new Tika();
    private FileProc fp;

    public TextProc(FileProc fp){
        this.fp = fp;
    }

    public static Tika getTika(){
        return tika;
    }

    // metodo que devuelve los formatos de los archivos
    public ArrayList<String> getAFFormats(){
        return fp.getAFinfo((file) -> {
            String format = null;
            try {
                format = tika.detect(file);
            } catch (IOException e) {
                System.err.println("Error con el fichero " + file.getName());
            }
            return format;
        });
    }

    public ArrayList<String> getEncodings(){
        return fp.getAFinfo((file) -> {
            String encoding = null;

            try {
                InputStream is = new FileInputStream(file);
                Metadata metadata = new Metadata();
                BodyContentHandler ch = new BodyContentHandler(tika.getMaxStringLength());
                ParseContext parseContext = new ParseContext();

                AutoDetectParser parser = new AutoDetectParser();
                parser.parse(is, ch, metadata, parseContext);

                encoding = metadata.get("Content-Encoding");

                if(encoding == null) encoding = "without encoding (ex: txt)";

                is.close();
            } catch (IOException e) {
                System.err.println("Error con el fichero " + file.getName());
            } catch (SAXException | TikaException e) {
                throw new RuntimeException(e);
            }

            return encoding;
        });
    }


    public ArrayList<String> getLanguages(){

        return fp.getAFinfo((file) -> {
            String language = null;
            try {
                InputStream is = new FileInputStream(file);
                Metadata metadata = new Metadata();
                BodyContentHandler ch = new BodyContentHandler(tika.getMaxStringLength());

                AutoDetectParser parser = new AutoDetectParser();
                parser.parse(is, ch, metadata);

                language = detectLanguage(ch.toString());

                is.close();
            } catch (IOException e) {
                System.err.println("Error con el fichero " + file.getName());
            } catch (TikaException | SAXException e) {
                throw new RuntimeException(e);
            }

            return language;
        });


    }

    private String detectLanguage(String text) throws IOException {
        LanguageDetector languageDetector = LanguageDetector.getDefaultLanguageDetector().loadModels();

        return languageDetector.detect(text).getLanguage();
    }

    public Set<String> getFlinks(File file){
        Set<String> links = new HashSet<>();
        try{
            InputStream is = new FileInputStream(file);
            Metadata metadata = new Metadata();
            // Si es html se necesita otro parser y por lo tanto otros handlde...
            if(Utils.isHTML(file)){

                LinkContentHandler linkContentHandler = new LinkContentHandler();
                ContentHandler textHandler = new BodyContentHandler(tika.getMaxStringLength());
                ToHTMLContentHandler toHTMLContentHandler = new ToHTMLContentHandler();

                TeeContentHandler teeContentHandler = new TeeContentHandler(linkContentHandler, textHandler, toHTMLContentHandler);
                ParseContext parseContext = new ParseContext();
                HtmlParser parser = new HtmlParser();
                parser.parse(is,teeContentHandler, metadata, parseContext);

                // para obtener todos los enlaces en una lista
                List<Link> linksList = linkContentHandler.getLinks();
                // para cada enlace contenido en la lista, nos quedamos con aquellos que sean etiquetas <a> y tengan href
                for(Link link : linksList){
                    String link_text = link.toString();
                    if(link_text.contains("<a href=\"")){
                        Document doc = Jsoup.parse(link_text);
                        Elements hrefElements = doc.select("a[href]");
                        for (Element element : hrefElements) {
                            String href = element.attr("href");
                            links.add(href);
                        }
                    }
                }
                // en caso de que no sea html:
            } else {
                // se necesitan otros contentHandler
                BodyContentHandler bodyContentHandler = new BodyContentHandler(tika.getMaxStringLength());
                AutoDetectParser parser = new AutoDetectParser();
                parser.parse(is, bodyContentHandler, metadata);
                String text = bodyContentHandler.toString(); // se obtiene todoo el texto

                String[] linksArray = text.split("\\s"); // se hacen "tokens"
                // y nos quedamos con aquellos que parezcan un enlace
                for(String link : linksArray){
                    if(link.startsWith("http://") || link.startsWith("https://")){
                        links.add(link);
                    }
                }
            }
        } catch (TikaException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return links;
    }


    //TODO: optimizar, hace lectura de código por cada iteración.
    private static String processText(String text){

        String processedText;

        processedText = text.toLowerCase();

        processedText = processedText.replaceAll("[\n\t]", " ");

        processedText = processedText.replaceAll(RegexController.PUNCTUATION.getRegex(), "");

        // Eliminar "." solo si está seguido por un espacio
        processedText = processedText.replaceAll(RegexController.DOTANDSPACE.getRegex(), " ");

        processedText = processedText.replaceAll(RegexController.EXPRESIONSIGS.getRegex(), "");

        processedText = processedText.replaceAll(RegexController.DELIMITORS.getRegex(), "");

        processedText = processedText.replaceAll(RegexController.QUOTES.getRegex(), "");

        processedText = processedText.replaceAll(RegexController.EMDASH.getRegex(), " ");

        processedText = processedText.replaceAll("\\s+", " ");

        return processedText;
    }



    public static Map<String, Integer> countTermFrequencies(File file) throws TikaException, IOException {
        String content = processText(tika.parseToString(file));

        Map<String, Integer> termFrequencyMap = countTermFrequencies(content);

        return termFrequencyMap;
    }

    private static Map<String, Integer> countTermFrequencies(String content) {
        Map<String, Integer> termFrequencyMap = new HashMap<>();
        String[] words = content.split(RegexController.SPACE.getRegex());

        for (String word : words)
            termFrequencyMap.put(word, termFrequencyMap.getOrDefault(word, 0) + 1);

        return termFrequencyMap;
    }

    public static String getFileText(File file){
        String text = null;
        try {
            text = tika.parseToString(file);
        } catch (IOException | TikaException e) {
            throw new RuntimeException(e);
        }
        return text;
    }


}

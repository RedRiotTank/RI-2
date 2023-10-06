package ri.core;

public enum RegexController {
        ALL_WORDS("([a-zA-Z]+)"),
        EMDASH("—|-"),
        EXPRESIONSIGS("¿|\\?|¡|!"),
        PUNCTUATION(",|;|:|\\.{2,}|…"),
        QUOTES("“|”|\"|«|»|'|`"),
        DOTANDSPACE("\\.\\s"),
        DELIMITORS("[\\[\\]\\(\\)\\{\\}]"),
        SPACE(" "),
        ANYOSSEPARATOR("[\\\\\\/]");

    private String regex;
    RegexController(String regex){
        this.regex = regex;
    }

    public String getRegex(){
        return this.regex;
    }
}

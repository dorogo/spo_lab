
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    public static final String VAR_KW = "VAR_KW";
    public static final String SM = "SM";
    
    public static final String ASSIGN_OP = "ASSIGN_OP";
    public static final String ADD_OP = "ADD_OP";
    public static final String DEC_OP = "DEC_OP";
    public static final String MULTI_OP = "MULTI_OP";
    public static final String DIGIT = "DIGIT";
    public static final String VAR_NAME = "VAR_NAME";
    public static final String WS = "WS";

    String accum = "";

    private Pattern sw = Pattern.compile("^;$");
    private Pattern var_kw = Pattern.compile("^var$");
    private Pattern assign_op = Pattern.compile("^=$");
    //private Pattern op = Pattern.compile("^'-'|'+'|'/'|'*'$");
    private Pattern addOp = Pattern.compile("^\\+$");
    private Pattern decOp = Pattern.compile("^\\-$");
    private Pattern multOp = Pattern.compile("^\\*$");
    private Pattern digit = Pattern.compile("^0|[1-9]{1}[0-9]*$");
    private Pattern var = Pattern.compile("^[a-zA-Z]*$");
    private Pattern ws = Pattern.compile("^\\s*$");

    private Map<String, Pattern> keyWordsMap = new HashMap<String, Pattern>();
    private Map<String, Pattern> regularTerminals = new HashMap<String, Pattern>();

    private String currentLucky = null;

    private int i;
    private int numCurrLine = 0;

    private List<Token> tokens = new ArrayList<Token>();

    public Lexer() {
        keyWordsMap.put(Lexer.VAR_KW, var_kw);
        regularTerminals.put(Lexer.SM, sw);
        regularTerminals.put(Lexer.ASSIGN_OP, assign_op);
//		regularTerminals.put("OP", op);
        regularTerminals.put(Lexer.ADD_OP, addOp);
        regularTerminals.put(Lexer.DEC_OP, decOp);
        regularTerminals.put(Lexer.MULTI_OP, multOp);
        regularTerminals.put(Lexer.DIGIT, digit);
        regularTerminals.put(Lexer.VAR_NAME, var);
        regularTerminals.put(Lexer.WS, ws);
    }

    public void processFile(String fileName) throws IOException {
        File file = new File(fileName);
        Reader reader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(reader);
        String line;

        while ((line = bReader.readLine()) != null) {
            numCurrLine++;
            processLine(line);
        }
    }

    private void processLine(String line) throws IOException {
        for (i = 0; i < line.length(); i++) {
            accum = accum + line.charAt(i);
            if(line.charAt(i) == '\b')
                throw new Error("asdas");
            processAccum();
        }
        if (currentLucky == null) {
            String errStrTmp = "";
            for (i = 0; i < line.length() - accum.length(); i++) {
                errStrTmp += " ";
            }
            throw new IOException("Error. Wrong input at line " + numCurrLine + ":\n"
                    + "\t\t\t" + line + "\n"
                    + "\t\t\t" + errStrTmp + "^\n");
        } else {
            tokens.add(new Token(currentLucky, accum, numCurrLine));
            System.out.println("TOKEN(" + currentLucky + ") recognized with value : " + accum);
            accum = "";
            currentLucky = null;
        }
    }

    private void processAccum() {
        boolean found = false;

        if (!(found = checkMatchesInMap(keyWordsMap))) {
            found = checkMatchesInMap(regularTerminals);
        }

        if (currentLucky != null && !found) {
            System.out.println("TOKEN(" + currentLucky + ") recognized with value : " + accum.substring(0, accum.length() - 1));
            tokens.add(new Token(currentLucky, accum.substring(0, accum.length() - 1),numCurrLine));
            i--;
            accum = "";
            currentLucky = null;
        }
    }

    private boolean checkMatchesInMap(Map<String, Pattern> srcMap) {
        boolean found = false;
        for (String regExpName : srcMap.keySet()) {
            Pattern currentPattern = srcMap.get(regExpName);
            Matcher m = currentPattern.matcher(accum);
            if (m.matches()) {
                currentLucky = regExpName;
                found = true;
            }
        }
        return found;
    }

    public List<Token> getTokens() {
        return tokens;
    }
    
}

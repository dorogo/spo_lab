
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
    public static final String FOR_KW = "FOR_KW";
    public static final String SWITCH_KW = "SWITCH_KW";
    public static final String CASE_KW = "CASE_KW";
    public static final String BREAK_KW = "BREAK_KW";
    public static final String SM = "SM";
    public static final String ASSIGN_OP = "ASSIGN_OP";
    public static final String ADD_OP = "ADD_OP";
    public static final String DEC_OP = "DEC_OP";
    public static final String MULTI_OP = "MULTI_OP";
    public static final String DIV_OP = "DIV_OP";
    public static final String DIGIT = "DIGIT";
    public static final String VAR_NAME = "VAR_NAME";
    public static final String WS = "WS";
    public static final String BRACKET_OPEN = "BRACKET_OPEN";
    public static final String BRACKET_CLOSE = "BRACKET_CLOSE";
    public static final String LARGER_OP = "LARGER_OP";
    public static final String LESS_OP = "LESS_OP";
    public static final String EQUAL_OP = "EQUAL_OP";
    public static final String NOT_EQUAL_OP = "NOT_EQUAL_OP";
    public static final String LARGE_N_EQUAL_OP = "LARGE_N_EQUAL_OP";
    public static final String LESS_N_EQUAL_OP = "LESS_N_EQUAL_OP";
    public static final String F_BRACKET_OPEN = "F_BRACKET_OPEN";
    public static final String F_BRACKET_CLOSE = "F_BRACKET_CLOSE";
    public static final String NL = "NL";
    public static final String FGO = "FGO";
    public static final String GO = "GO";
    public static final String ADRESS = "ADRESS";
    public static final String CL = "CL";
    
    
    String accum = "";

    private Pattern sm = Pattern.compile("^;$");
    private Pattern var_kw = Pattern.compile("^var$");
    private Pattern for_kw = Pattern.compile("^for$");
    private Pattern switch_kw = Pattern.compile("^switch$");
    private Pattern case_kw = Pattern.compile("^case$");
    private Pattern break_kw = Pattern.compile("^break$");
    private Pattern assign_op = Pattern.compile("^=$");
    //private Pattern op = Pattern.compile("^'-'|'+'|'/'|'*'$");
    private Pattern addOp = Pattern.compile("^\\+$");
    private Pattern decOp = Pattern.compile("^\\-$");
    private Pattern multOp = Pattern.compile("^\\*$");
    private Pattern divOp = Pattern.compile("^\\/$");
    private Pattern digit = Pattern.compile("^0|[1-9]{1}[0-9]*$");
    private Pattern var = Pattern.compile("^[a-zA-Z]*$");
    private Pattern ws = Pattern.compile("^\\s*$");
    private Pattern bracketOpen = Pattern.compile("^\\($");
    private Pattern bracketClose = Pattern.compile("^\\)$");
    private Pattern largerOp = Pattern.compile("^>$");
    private Pattern lessOp = Pattern.compile("^<$");
    private Pattern equalOp = Pattern.compile("^==$");
    private Pattern notEqualOp = Pattern.compile("^!=$");
    private Pattern lessNEqualOp = Pattern.compile("^<=$");
    private Pattern largerNEqualOp = Pattern.compile("^>=$");
    private Pattern fBracketOpen = Pattern.compile("^\\{$");
    private Pattern fBracketClose = Pattern.compile("^\\}$");
    private Pattern nl = Pattern.compile("^\n$");
    private Pattern cl = Pattern.compile("^:$");

    private Map<String, Pattern> keyWordsMap = new HashMap<String, Pattern>();
    private Map<String, Pattern> regularTerminals = new HashMap<String, Pattern>();

    private String currentLucky = null;

    private int i;
    private int numCurrLine = 0;

    private List<Token> tokens = new ArrayList<Token>();

    public Lexer() {
        keyWordsMap.put(Lexer.VAR_KW, var_kw);
        keyWordsMap.put(Lexer.FOR_KW, for_kw);
        keyWordsMap.put(Lexer.SWITCH_KW, switch_kw);
        keyWordsMap.put(Lexer.CASE_KW, case_kw);
        keyWordsMap.put(Lexer.BREAK_KW, break_kw);
        regularTerminals.put(Lexer.SM, sm);
        regularTerminals.put(Lexer.ASSIGN_OP, assign_op);
//		regularTerminals.put("OP", op);
        regularTerminals.put(Lexer.ADD_OP, addOp);
        regularTerminals.put(Lexer.DEC_OP, decOp);
        regularTerminals.put(Lexer.MULTI_OP, multOp);
        regularTerminals.put(Lexer.DIV_OP, divOp);
        regularTerminals.put(Lexer.DIGIT, digit);
        regularTerminals.put(Lexer.VAR_NAME, var);
        regularTerminals.put(Lexer.WS, ws);
        regularTerminals.put(Lexer.BRACKET_OPEN, bracketOpen);
        regularTerminals.put(Lexer.BRACKET_CLOSE, bracketClose);
        regularTerminals.put(Lexer.LARGER_OP, largerOp);
        regularTerminals.put(Lexer.LESS_OP, lessOp);
        regularTerminals.put(Lexer.EQUAL_OP, equalOp);
        regularTerminals.put(Lexer.NOT_EQUAL_OP, notEqualOp);
        regularTerminals.put(Lexer.LARGE_N_EQUAL_OP, largerNEqualOp);
        regularTerminals.put(Lexer.LESS_N_EQUAL_OP,lessNEqualOp);
        regularTerminals.put(Lexer.F_BRACKET_OPEN, fBracketOpen);
        regularTerminals.put(Lexer.F_BRACKET_CLOSE, fBracketClose);
        regularTerminals.put(Lexer.NL, nl);
        regularTerminals.put(Lexer.CL, cl);
    }

    public void processFile(String fileName) throws IOException {
        File file = new File(fileName);
        Reader reader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(reader);
        String line;

        while ((line = bReader.readLine()) != null) {
            numCurrLine++;
            if(line.length() > 0)
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
            throw new IOException(" Error. Wrong input at line " + numCurrLine + ":\n"
                    + "\t\t\t" + line + "\n"
                    + "\t\t\t" + errStrTmp + "^\n");
        } else {
            tokens.add(new Token(currentLucky, accum, numCurrLine));
//            System.out.println("TOKEN(" + currentLucky + ") recognized with value : " + accum);
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
//            System.out.println("TOKEN(" + currentLucky + ") recognized with value : " + accum.substring(0, accum.length() - 1));
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

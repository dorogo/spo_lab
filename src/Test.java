
import java.io.IOException;
import java.util.List;

public class Test {

    static FileHelper fileHelper = new FileHelper();

    public static void main(String args[]) throws Exception {
        validInput();
//        wrongInput();
    }

    static void wrongInput() throws Exception {
        fileHelper.testRead(System.getProperty("user.dir") + "/wrong-test.input");
        process(System.getProperty("user.dir") + "/wrong-test.input");
    }

    static void validInput() throws Exception {
        fileHelper.testRead(System.getProperty("user.dir") + "/valid-test.input");
        process(System.getProperty("user.dir") + "/valid-test.input");
    }

    static void process(String fileName) throws Exception {
        Lexer lexer = new Lexer();
        lexer.processFile(fileName);
        List<Token> tokens = lexer.getTokens();
        Parser parser = new Parser();
        parser.setTokens(tokens);
        parser.lang();
        List<PostfixToken> postfixTokens = parser.getPoliz();
        PolizProcessor polizProc = new PolizProcessor(postfixTokens);
        polizProc.calcPoliz();
        
    }
}

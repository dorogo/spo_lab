
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Parser {

    private List<Token> tokens;
    private Iterator<Token> iteratorTokens;
    private Token currentToken;

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
        iteratorTokens = this.tokens.iterator();
    }

    public void lang() throws Exception {
        boolean activated = false;
        
        
        
//            System.out.println("Tokens in Parser.java:");
//            for (Token token: tokens) {
//            	System.out.println(token);
//            }

        while (nextToken()) {
            if (expr()) {
                if (!sm()) {
                    throw new Exception("Error at line:"+currentToken.getNumLine() +".\n\t\t \';\" expected, but \"" + currentToken + "\" found.");
                }
            } else {
                throw new Exception("Error at line:"+currentToken.getNumLine() +".\n\t\t \"expr\" expected, but \"" + currentToken + "\" found.");
            }
            activated = true;
        }
        System.out.println("completed!!11");
        if (!activated) {
            throw new Exception("Error. No expr found.");
        }
    }

    private boolean expr() throws Exception {
        if (!(declare() || assign())) {
            throw new Exception("Error at line:"+currentToken.getNumLine() +".\n\t\t \"declare or assign\" expected");//, but \""+currentToken+"\" found.");
        }
        return true;
    }

    private boolean sm() {
        return currentToken.getName().equals(Lexer.SM);
    }

    private boolean declare() throws Exception {
        if (varKw()) {
            nextToken();
            if (varName()) {
                nextToken(); // для ";"
            } else {
                throw new Exception("Error at line:"+currentToken.getNumLine() +".\n\t\t \"var name\" expected, but \"" + currentToken + "\" found.");
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean assign() throws Exception {
        if (varName()) {
            nextToken();
            if (assignOp()) {
                nextToken();
                if (!smth()) {
                    throw new Exception("Error at line:"+currentToken.getNumLine() +".\n\t\t \"var name, digit, or its with op\" expected, but \"" + currentToken + "\" found.");
                }
            } else {
                throw new Exception("Error at line:"+currentToken.getNumLine() +".\n\t\t \"assign op\" expected, but \"" + currentToken + "\" found.");
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean smth() throws Exception {
        if (smthUnit()) {
            nextToken();
            if (op()) {
                do {
                    nextToken();
                    if (smthUnit()) {
                        nextToken();
                    } else {
                        throw new Exception("Error at line:"+currentToken.getNumLine() +".\n\t\t \"digit or var name\" expected, but \"" + currentToken + "\" found.");
                    }
                } while (!currentToken.getName().equals(Lexer.SM));
            }
        } else {
            throw new Exception("Error at line:"+currentToken.getNumLine() +".\n\t\t \"digit or var name\" expected, but \"" + currentToken + "\" found.");
        }
        return true;
    }

    private boolean smthUnit() {
        return (currentToken.getName().equals(Lexer.DIGIT) || currentToken.getName().equals(Lexer.VAR_NAME));
    }

    private boolean varName() {
        return currentToken.getName().equals(Lexer.VAR_NAME);
    }

    private boolean assignOp() {
        return currentToken.getName().equals(Lexer.ASSIGN_OP);
    }

    private boolean varKw() {
        return currentToken.getName().equals(Lexer.VAR_KW);
    }

    private boolean op() {
        return (currentToken.getName().equals(Lexer.ADD_OP) || currentToken.getName().equals(Lexer.DEC_OP) || currentToken.getName().equals(Lexer.MULTI_OP));
    }

    private boolean nextToken() {
        while (iteratorTokens.hasNext()) {
            do {
                currentToken = iteratorTokens.next();
            } while (iteratorTokens.hasNext() && currentToken.getName().equals(Lexer.WS));
            return true;
        }
        return false;
    }

}

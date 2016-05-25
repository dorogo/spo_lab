
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

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

        while (nextToken()) {
            if (expr()) {
                if (!sm()) {
                    throw new Exception("\n\t\tError at line:" + (currentToken.getNumLine()-1) + ".\n\t\t Missing \";\". ");
                }
            } else {
                throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"expr\" expected, but \""
                        + currentToken.getValue() + "\" found.");
            }
            activated = true;
        }
        if (!activated) {
            throw new Exception("\n\t\tError. No expr found.");
        }
        System.out.println("completed!!11");
    }

    private boolean expr() throws Exception {
        if (!(declare() || assign())) {
            throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"declare or assign\" expected, but \""
                    + currentToken.getValue() + "\" found.");
        }
        return true;
    }

    private boolean declare() throws Exception {
        if (varKw()) {
            nextToken();
            if (varName()) {
                nextToken(); // для ";"
            } else {
                throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"var name\" expected, but \""
                        + currentToken.getValue() + "\" found.");
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
                    throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"var name, digit, or its with op\" expected, but \""
                            + currentToken.getValue() + "\" found.");
                }
            } else {
                throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"assign op\" expected, but \""
                        + currentToken.getValue() + "\" found.");
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean smth() throws Exception {
        if (operand()) {
            nextToken();
                while (!sm() && op()){
                    nextToken();
                    if (operand()) {
                        if (!nextToken()) {
                            throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t Missing \";\"");
                        }
                    } else {
                        throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"digit or var name\" expected, but \""
                                + currentToken.getValue() + "\" found.");
                    }
                }
        } else {
            throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"digit or var name\" expected, but \""
                    + currentToken.getValue() + "\" found.");
        }
        return true;
    }

    private boolean operand() throws Exception {
        if (bracketOpen()) {
            nextToken();
            if (smthUnit() || operand()) {
                nextToken();
                while (!bracketClose()) {
                    if (op()) {
                        nextToken();
                        if ((smthUnit() || operand())) {
                            nextToken();
                        } else {
                            return false;
                        }
                    } else if (sm()) {
                        throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t Missing closing bracket.");
                    } else {
                        throw new Exception("\n\t\tError at line:" + currentToken.getNumLine()
                                + ".\n\t\t \"operation\" expected, but \"" + currentToken.getValue() + "\" found.");
                    }
                }
            } else {
                throw new Exception("\n\t\tError at line:" + currentToken.getNumLine()
                        + ".\n\t\t \"operand\" expected, but \"" + currentToken.getValue() + "\" found.");
            }
        } else if (smthUnit()) {
            return true;
        } else if (bracketClose()) {
            throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t Missing openning bracket.");
        } else {
            return false;
        }
        return true;
    }

    private boolean bracketOpen() {
        return currentToken.getName().equals(Lexer.BRACKET_OPEN);
    }

    private boolean bracketClose() {
        return currentToken.getName().equals(Lexer.BRACKET_CLOSE);
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

    private boolean sm() {
        return currentToken.getName().equals(Lexer.SM);
    }
    
    private boolean nextToken() throws Exception{
        if (iteratorTokens.hasNext()) {
            do {
                currentToken = iteratorTokens.next();
            } while (iteratorTokens.hasNext() && currentToken.getName().equals(Lexer.WS));
            return true;
        }
        return false;
    }

    public List<PostfixToken> getPoliz() throws Exception{
        List<PostfixToken> poliz = new ArrayList<PostfixToken>();
        Stack<PostfixToken> stack = new Stack<PostfixToken>();
        int lastOpPriority = 0;
        iteratorTokens = this.tokens.iterator();

        while (nextToken()) {
            if (smthUnit()) {
                poliz.add(new PostfixToken(currentToken.getName(), currentToken.getValue()));
            } else if (op() || assignOp()) {
                if (!stack.empty()) {
                    while (currentToken.getOpPriority() <= lastOpPriority) {
                        poliz.add(stack.pop());
                        lastOpPriority = stack.peek().getOpPriority();
                    }
                }
                stack.push(new PostfixToken(currentToken.getName(), currentToken.getValue()));
                lastOpPriority = currentToken.getOpPriority();
            } else if (sm()) {
                while (!stack.empty()) {
                    poliz.add(stack.pop());
                }
            } else if (bracketOpen()) {
                stack.push(new PostfixToken(currentToken.getName(), currentToken.getValue()));
                lastOpPriority = currentToken.getOpPriority();
            } else if (bracketClose()) {
                while (stack.peek().getName().equals(Lexer.BRACKET_OPEN)) {
                    poliz.add(stack.pop());
                }
                lastOpPriority = stack.peek().getOpPriority();
            }
        }
        //outupt poliz
        System.out.print("Poliz of curr file: ");
        for (int q = 0; q < poliz.size(); q++) {
            System.out.print(poliz.get(q).getValue());

        }
        return poliz;
    }

}

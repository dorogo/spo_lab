
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import sun.misc.Queue;

public class Parser {

    private List<Token> tokens;
    private ListIterator<Token> iteratorTokens;
    private Token currentToken;
    private Token prevToken;
    private boolean forCompleted = false;
    private boolean switchCompleted = false;
    private boolean structCompleted = false;
    private boolean isForHead = false;

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
//        for (Token t : tokens) {
//            System.out.println(t.getValue());
//        }
        iteratorTokens = this.tokens.listIterator();
    }

    public void lang() throws Exception {
        boolean activated = false;

        while (nextToken()) {
            if (expr()) {
                if (!sm()) {
                    if(forCompleted){
                        forCompleted = false;
                    } else if (switchCompleted) {
                        switchCompleted = false;
                    } else if (structCompleted) {
                        structCompleted = false;
                    } else {
                        throw new Exception("\n\t\tError at line:" + (currentToken.getNumLine() - 1) + ".\n\t\t Missing \";\". ");
                    }
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
        if (!(declare() || assign() || forExpr() || switchExpr() || structExpr())) {
            throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"declare or assign\" expected, but \""
                    + currentToken.getValue() + "\" found.");
        }
        return true;
    }
    
    private boolean structExpr() throws Exception {
        if (structKw()) {
            nextToken();
            if (structHead()) {
                nextToken();
                if (!structBody()) {
                    throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"struct body\" expected, but \""
                    + currentToken.getValue() + "\" found.");
                }
            } else {
                throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"struct head\" expected, but \""
                    + currentToken.getValue() + "\" found.");
            }
        } else {
            return false;
        }
        structCompleted = true;
        return true;
    }
    
    private boolean structBody() throws Exception {
        if (fBracketOpen()) {
            nextToken();
            while (!fBracketClose()) {                
                if (assign()) {
                    if (sm()) {
                        nextToken();
                    } 
                } else {
                    throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"assign\" expected, but \""
                    + currentToken.getValue() + "\" found.");
                }
            }
        } else {
            return false;
        }
        return true;
    }
    
    private boolean structHead() throws Exception {
        if (varName()) {
            nextToken();
            if (!assignOp()) {
                throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"assign op\" expected, but \""
                    + currentToken.getValue() + "\" found.");
            }
        } else {
            return false;
        }
        return true;
    }
    
    private boolean switchExpr() throws Exception {
        if (switchKw()) {
            nextToken();
            if (switchHead()) {
                nextToken();
                if(switchBody()){
                    
                }
            }
        } else {
            return false;
        }
        switchCompleted = true;
        return true;
    }
    
    private boolean switchBody() throws Exception {
        if (fBracketOpen()) {
            nextToken();
            while (!fBracketClose()) {
                if (caseBlock()) {
                    nextToken();
                } else {
                    throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"case block or }\" expected, but \""
                    + currentToken.getValue() + "\" found.");
                }
            }
        } else {
            return false;
        }
        return true;
    }
    
    private boolean caseBlock() throws Exception {
        if (caseKw()) {
            nextToken();
            if (smthUnit()) {
                nextToken();
                if (cl()) {
                    nextToken();
                    if (caseBody()) {
                        nextToken();
                        if (breakKw()) {
                            nextToken();
                            if (!sm()){
                                throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \";\" expected, but \""
                                + currentToken.getValue() + "\" found.");
                            }
                        } else {
                            throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"break\" expected, but \""
                            + currentToken.getValue() + "\" found.");
                        }
                    } else {
                        throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"assign or declare\" expected, but \""
                    + currentToken.getValue() + "\" found.");
                    }
                } else {
                    throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \":\" expected, but \""
                    + currentToken.getValue() + "\" found.");
                }
            } else {
                throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"var or digit\" expected, but \""
                    + currentToken.getValue() + "\" found.");
            }
        } else {
             return false;
        }
        return true;
    }
    
    private boolean caseBody() throws Exception {
        if( !(declare() || assign())) {
            throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"declare or assign\" expected, but \""
                    + currentToken.getValue() + "\" found.");
        }
        return true;
    }
    
    private boolean switchHead() throws Exception {
        if (bracketOpen()) {
            nextToken();
            if (smthUnit()) {
                nextToken();
                if (!bracketClose()) {
                    throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \")\" expected, but \""
                    + currentToken.getValue() + "\" found.");
                }
            } else {
                 throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"var or digit\" expected, but \""
                    + currentToken.getValue() + "\" found.");
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean forExpr() throws Exception {
        if (forKw()) {
            isForHead = true;
            nextToken();
            if (forHead()) {
                isForHead = false;
                nextToken();
                if (forBody()) {
//                    nextToken();
                } 
            } 
        } else {
            return false;
        }
        forCompleted = true;
        return true;
    }

    private boolean forHead() throws Exception {
        if (bracketOpen()) {
            nextToken();
            if (smthUnit()) {
                assign();
//                nextToken();
                System.out.println("__________Parser.forHead()"+currentToken.getName());
                if (sm()) {
                    nextToken();
                    if (forLimit()) {
                        nextToken();
                        if (sm()) {
                            nextToken();
                            if (assign()) {
//                                nextToken();
                                if (!bracketClose()) {
                                    throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \")\" expected, but \""
                                            + currentToken.getValue() + "\" found.");
                                }
                            } else {
                                throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"changing limit\" expected, but \""
                                        + currentToken.getValue() + "\" found.");
                            }
                        } else {
                            throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \";\" expected, but \""
                                    + currentToken.getValue() + "\" found.");
                        }
                    } else {
                        throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"for limit\" expected, but \""
                                + currentToken.getValue() + "\" found.");
                    }
                } else {
                    throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \";\" expected, but \""
                            + currentToken.getValue() + "\" found.");
                }
            } else {
                throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"var name or assign\" expected, but \""
                        + currentToken.getValue() + "\" found.");
            }
        } else {
            throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"(\" expected, but \""
                    + currentToken.getValue() + "\" found.");
        }
        return true;
    }

    private boolean forLimit() throws Exception {
        if (smthUnit()) {
            nextToken();
            if (comparingOp()) {
                nextToken();
                if (!smthUnit()) {
                    throw new Exception("miss smth");
                }
            } else {
                throw new Exception("miss comparing");
            }
        } else {
            throw new Exception("miss smth");
        }
        return true;
    }

    private boolean forBody() throws Exception {
        if (fBracketOpen()) {
            nextToken();
            while (!fBracketClose()) {
                if (expr()) {
                    nextToken();
                } else if (sm()) {
                    throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"}\" expected, but \""
                        + currentToken.getValue() + "\" found.");
                } else {
                    throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"expr\" expected, but \""
                        + currentToken.getValue() + "\" found.");
                }
            }
        } else {
            throw new Exception("\n\t\tError at line:" + currentToken.getNumLine() + ".\n\t\t \"{\" expected, but \""
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
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean smth() throws Exception {
        if (operand()) {
            nextToken();
            while (!sm() && op()) {
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

    private boolean smthUnit() throws Exception{
//        if (digit()) {
//            
//        } else if (varName()) {
////            if (!isForHead) {
//                nextToken();
//                if(dotOp()) {
//                    nextToken();
//                    if(!varName()){
//                        throw new Exception("\n\t\tError at line:" + currentToken.getNumLine()
//                                    + ".\n\t\t \"varName\" expected, but \"" + currentToken.getValue() + "\" found.");
//                    }
//                } else if (iteratorTokens.hasPrevious()) {
////                    System.out.println("Parser.smthUnit()prev"+iteratorTokens.previous());
////                        currentToken = iteratorTokens.previous();
//                    do {
//                        currentToken = iteratorTokens.previous();
//                        System.out.println("Parser.smthUnit()"+currentToken);
//                    } while (iteratorTokens.hasPrevious()&& currentToken.getName().equals(Lexer.WS));
//                    
//                }
////            }
//        } else {
//            return false;
//        }
//        System.out.println("Parser.smthUnit()"+currentToken.getName());
//        return true;
        return (currentToken.getName().equals(Lexer.DIGIT) || currentToken.getName().equals(Lexer.VAR_NAME));
    }
    
    
    private boolean dotOp() {
        return currentToken.getName().equals(Lexer.DOT_OP);
    }

    private boolean digit() {
        return currentToken.getName().equals(Lexer.DIGIT);
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
        return (currentToken.getName().equals(Lexer.ADD_OP) || currentToken.getName().equals(Lexer.DEC_OP)
                || currentToken.getName().equals(Lexer.MULTI_OP) || currentToken.getName().equals(Lexer.DIV_OP));
    }

    private boolean sm() {
        return currentToken.getName().equals(Lexer.SM);
    }

    private boolean forKw() {
        return currentToken.getName().equals(Lexer.FOR_KW);
    }

    private boolean comparingOp() {
        return (currentToken.getName().equals(Lexer.LESS_OP) || currentToken.getName().equals(Lexer.LARGER_OP)
                || currentToken.getName().equals(Lexer.EQUAL_OP) || currentToken.getName().equals(Lexer.NOT_EQUAL_OP)
                || currentToken.getName().equals(Lexer.LESS_N_EQUAL_OP) || currentToken.getName().equals(Lexer.LARGE_N_EQUAL_OP));
    }

    private boolean fBracketOpen() {
        return currentToken.getName().equals(Lexer.F_BRACKET_OPEN);
    }

    private boolean fBracketClose() {
        return currentToken.getName().equals(Lexer.F_BRACKET_CLOSE);
    }
    
    private boolean switchKw() {
        return currentToken.getName().equals(Lexer.SWITCH_KW);
    }
    
    private boolean caseKw() {
        return currentToken.getName().equals(Lexer.CASE_KW);
    }
    
    private boolean breakKw() {
        return currentToken.getName().equals(Lexer.BREAK_KW);
    }
    
    private boolean cl() {
        return currentToken.getName().equals(Lexer.CL);
    }

    private boolean structKw() {
        return currentToken.getName().equals(Lexer.STRUCT_KW);
    }
    
    private boolean nextToken() throws Exception {
        prevToken = currentToken;
        if (iteratorTokens.hasNext()) {
            do {
                currentToken = iteratorTokens.next();
            } while (iteratorTokens.hasNext() && currentToken.getName().equals(Lexer.WS));
            System.out.println(currentToken);
            return true;
        }
        return false;
    }

    public List<Token> getPoliz() throws Exception {
        List<Token> poliz = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();
        Stack<Integer> adrStack = new Stack<>();
        LinkedList<Integer> adrList = new LinkedList<Integer>();
        boolean searchStartAdrFor = false;
        boolean searchAdrAfterLimitFor = false;
        boolean isFor = false;
        boolean isSwitch = false;
        boolean isSwitchInit = false;
        boolean isStruct = false;
        Token tmp = new Token();
        int countCases = 0;
        
        int lastOpPriority = 0;
        iteratorTokens = this.tokens.listIterator();

        while (nextToken()) {
            if (digit() || varName()) {
                poliz.add(currentToken);
            } else if (op() || assignOp() || comparingOp()) {
                if (!stack.empty()) {
                    while (currentToken.getOpPriority() <= lastOpPriority) {
                        poliz.add(stack.pop());
                        lastOpPriority = stack.peek().getOpPriority();
                    }
                }
                stack.push(currentToken);
                lastOpPriority = currentToken.getOpPriority();
            } else if (sm()) {
                while (!stack.empty() && !stack.peek().getName().equals(Lexer.BRACKET_OPEN)) {
                    poliz.add(stack.pop());
                }
                if (searchAdrAfterLimitFor){
                    searchAdrAfterLimitFor = false;
                    poliz.add(new Token(Lexer.ADRESS, "0", -1));
                    poliz.add(new Token(Lexer.FGO, null, -1));
                } else if (searchStartAdrFor) {
                   searchStartAdrFor = false;
                   adrStack.push(poliz.size()-1);
                   searchAdrAfterLimitFor = true;
                }
            } else if (bracketOpen() && !isFor && !isSwitchInit) {
                stack.push(currentToken);
                lastOpPriority = currentToken.getOpPriority();
            } else if (bracketClose() && !isFor && !isSwitchInit) {
                while (!stack.peek().getName().equals(Lexer.BRACKET_OPEN)) {
                    poliz.add(stack.pop());
                }
                stack.pop();
                lastOpPriority = stack.peek().getOpPriority();
            } else if (forKw()) {
                searchStartAdrFor = true;
                isFor = true;
            } else if (fBracketOpen() && !isSwitch) {
                isSwitchInit = false;
                isFor = false;
                while (!stack.empty()) {
                    poliz.add(stack.pop());
                }
                poliz.add(currentToken);
            } else if (fBracketClose()) {
                if (isSwitch) {
                    isSwitch = false;
                    adrList.pop();
                    while (countCases >= 0) {
                        adrList.add(poliz.size()-1);
                        countCases--;
                    }
                } else if (isStruct) {
                    isStruct = false;
                    poliz.add(currentToken);
                } else {
                    poliz.add(new Token(Lexer.ADRESS, "0", -1));
                    poliz.add(new Token(Lexer.GO, null, -1));
                    adrStack.push(poliz.size());
                    poliz.add(currentToken);
                }
            } else if (switchKw()) {
                isSwitch = true;
                isSwitchInit = true;
                nextToken();
                nextToken();
                tmp = currentToken;
                nextToken();
            } else if (cl()) {
                poliz.add(tmp);
                adrList.add(poliz.size() - 3);
                poliz.add(new Token(Lexer.EQUAL_OP, "==", -1));
                poliz.add(new Token(Lexer.ADRESS, "-1", -1));
                poliz.add(new Token(Lexer.FGO, null, -1));
            } else if (breakKw()) {
                countCases++;
                poliz.add(new Token(Lexer.ADRESS, "-1", -1));
                poliz.add(new Token(Lexer.GO, null, -1));
            } else if (structKw()) {
                poliz.add(currentToken);
                isStruct = true;
            }
        }
        
        for (Token t:poliz) {
            if(t.getName().equals(Lexer.ADRESS)){
                if(t.getValue().equals("-1")) {
                    t.setVAlue(adrList.pop().toString());
                } else {
                    t.setVAlue(adrStack.pop().toString());
                }
            }
        }
        
        //output poliz
        System.out.print("Poliz of curr file: ");
        for (int q = 0; q < poliz.size(); q++) {
            System.out.print(poliz.get(q).getValue());

        }
        System.out.println("\nadr"+adrStack);
        return poliz;
    }

}

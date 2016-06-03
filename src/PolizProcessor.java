
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class PolizProcessor {

    private Stack<Object> stack;
    private Map<String, Integer> varMap;
//    private Map<Integer, HashMap> varMap;
    private List<Token> poliz;
    private Token currentToken;
    private int index;
    private int size;
    private int tmpValue1;
    private Object tmpValue2;
    private int currentFBrackets;

    public PolizProcessor(List<Token> srcPoliz) {
        varMap = new HashMap<String, Integer>();
//        varMap = new HashMap<Integer, HashMap>();
//        varMap.put(0, new HashMap<String, Integer>());
//        currentFBrackets = 0;
        stack = new Stack<Object>();
        this.poliz = srcPoliz;
    }

    public void calcPoliz() throws Exception {
        index = 0;
        size = this.poliz.size();
        while (index < size) {
            currentToken = this.poliz.get(index);
            switch (currentToken.getName()) {
                case Lexer.DIGIT:
                case Lexer.ADRESS:
                    stack.push(Integer.parseInt(currentToken.getValue()));
                    break;
                case Lexer.VAR_NAME:
                    stack.push(currentToken.getValue());
                    break;
                case Lexer.ASSIGN_OP:
                    tmpValue1 = getOperand(stack.pop());
//                    varMap.get(currentFBrackets).put((String) stack.pop(), tmpValue1);
                    varMap.put((String) stack.pop(), tmpValue1);
                    break;
                case Lexer.ADD_OP:
                    stack.push(getOperand(stack.pop()) + getOperand(stack.pop()));
                    break;
                case Lexer.DEC_OP:
                    tmpValue1 = getOperand(stack.pop());
                    stack.push(getOperand(stack.pop()) - tmpValue1);
                    break;
                case Lexer.MULTI_OP:
                    stack.push(getOperand(stack.pop()) * getOperand(stack.pop()));
                    break;
                case Lexer.DIV_OP:
                    tmpValue1 = getOperand(stack.pop());
                    stack.push(getOperand(stack.pop())/ tmpValue1);
                    break;
                case Lexer.LESS_OP:
                    stack.push(getOperand(stack.pop()) > getOperand(stack.pop()));
                    break;
                case Lexer.LARGER_OP:
                    stack.push(getOperand(stack.pop()) < getOperand(stack.pop()));
                    break;
                case Lexer.EQUAL_OP:
                    stack.push(getOperand(stack.pop()) == getOperand(stack.pop()));
                    break;
                case Lexer.NOT_EQUAL_OP:
                    stack.push(getOperand(stack.pop()) != getOperand(stack.pop()));
                    break;
                case Lexer.LESS_N_EQUAL_OP:
                    stack.push(getOperand(stack.pop()) >= getOperand(stack.pop()));
                    break;
                case Lexer.LARGE_N_EQUAL_OP:
                    stack.push(getOperand(stack.pop()) <= getOperand(stack.pop()));
                    break;
                case Lexer.FGO:
                    tmpValue1 = (Integer) stack.pop();
                    if (!(boolean)stack.pop()) {
                        index = tmpValue1;
                    }
                    break;
                case Lexer.GO:
                    index = (Integer)stack.pop();
                    break;
//                case Lexer.FO
            }
//                        System.out.println(currentToken);
//			System.out.println(stack);
            index++;
        }
        System.out.print("\n\nvar map:" + varMap);
    }

    private int getOperand(Object tmp) throws Exception {
        if (tmp.getClass().equals(String.class)) {
            if (varMap.containsKey((String) tmp)) {
//            if (varMap.get(currentFBrackets).containsKey((String) tmp)) {
//                return (Integer)varMap.get(currentFBrackets).get((String) tmp);
                return varMap.get((String) tmp);
            } else {
//                for (int i = 0; i < varMap.size(); i++) {
//                    if (varMap.get(i).containsKey((String) tmp)) 
//                        return (Integer)varMap.get(i).get((String) tmp);
//                }
                throw new Exception("\n\t\tMissing declare of \"" + (String) tmp + "\"");
            }
        }
        return (Integer) tmp;
    }

}

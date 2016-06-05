
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class PolizProcessor {

    private Stack<Object> stack;
    private Map<String, Integer> varMap;
    private Map<String, HashMap<String, Integer>> varMapStruct;
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
        varMapStruct = new HashMap<String, HashMap<String, Integer>>();
//        varMap = new HashMap<Integer, HashMap>();
//        varMap.put(0, new HashMap<String, Integer>());
//        currentFBrackets = 0;
        stack = new Stack<Object>();
        this.poliz = srcPoliz;
    }

    public void calcPoliz() throws Exception {
        index = 0;
        size = this.poliz.size();
        boolean isStruct = false;
        boolean isStructHead = false;
        String currStructName = "";
        
         while (index < size) {
            currentToken = this.poliz.get(index);
            if (currentToken.getName().equals(Lexer.DIGIT) || currentToken.getName().equals(Lexer.ADRESS)) {
                if(isStructHead) {
                    currStructName = poliz.get(index + 1).getValue();
                    isStructHead = false;
                } else {
                    stack.push(Integer.parseInt(currentToken.getValue()));
                }
            } else if (currentToken.getName().equals(Lexer.VAR_NAME)) {
                stack.push(currentToken.getValue());
            } else if (currentToken.getName().equals(Lexer.ASSIGN_OP)) {
                if (isStructHead) {
                    currStructName = (String) stack.pop();
                    varMapStruct.put(currStructName, new HashMap<String, Integer>());
                } else if (isStruct) {
                    tmpValue1 = getOperand(stack.pop());
                    varMapStruct.get(currStructName).put((String) stack.pop(), tmpValue1);
                } else {
                    tmpValue1 = getOperand(stack.pop());
                    varMap.put((String) stack.pop(), tmpValue1);    
                }
            } else if (currentToken.getName().equals(Lexer.ADD_OP)) {
                stack.push(getOperand(stack.pop()) + getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.DEC_OP)) {
                tmpValue1 = getOperand(stack.pop());
                stack.push(getOperand(stack.pop()) - tmpValue1);
            } else if (currentToken.getName().equals(Lexer.MULTI_OP)) {
                stack.push(getOperand(stack.pop()) * getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.DIV_OP)) {
            	tmpValue1 = getOperand(stack.pop());
                stack.push(getOperand(stack.pop())/ tmpValue1);
            } else if (currentToken.getName().equals(Lexer.LESS_OP)) {
            	stack.push(getOperand(stack.pop()) > getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.LARGER_OP)) {
            	stack.push(getOperand(stack.pop()) < getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.EQUAL_OP)) {
            	stack.push(getOperand(stack.pop()) == getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.NOT_EQUAL_OP)) {
            	stack.push(getOperand(stack.pop()) != getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.LESS_N_EQUAL_OP)) {
            	stack.push(getOperand(stack.pop()) >= getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.LARGE_N_EQUAL_OP)) {
            	stack.push(getOperand(stack.pop()) <= getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.FGO)) {
            	tmpValue1 = (Integer) stack.pop();
                if (!(Boolean) stack.pop()) {
                    index = tmpValue1;
                }
            } else if (currentToken.getName().equals(Lexer.GO)) {
            	index = (Integer)stack.pop();
            } else if (currentToken.getName().equals(Lexer.STRUCT_KW)) {
                 isStruct = true;
                 isStructHead = true;
            } else if (currentToken.getName().equals(Lexer.F_BRACKET_OPEN) && isStructHead) {
                isStructHead = false;
            } else if (currentToken.getName().equals(Lexer.F_BRACKET_CLOSE) && isStruct) {
                isStruct = false;
            }
//			System.out.println(currentToken+" - "+stack);
            index++;
        }

        System.out.print("\n\nvar map:" + varMap);
        System.out.print("\n\nvar map struct:" + varMapStruct);
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

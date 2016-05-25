
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class PolizProcessor {

    private Stack<Object> stack;
    private Map<String, Integer> varMap;
    private List<Token> poliz;
    Token currentPfxToken;
    int index;
    int size;
    int tmpValue1;
    Object tmpValue2;

    public PolizProcessor(List<Token> srcPoliz) {
        varMap = new HashMap<String, Integer>();
        stack = new Stack<Object>();
        this.poliz = srcPoliz;
    }

    public void calcPoliz() throws Exception {
        index = 0;
        size = this.poliz.size();
        while (index < size) {
            currentPfxToken = this.poliz.get(index);
            if (currentPfxToken.getName().equals(Lexer.DIGIT)) {
                stack.push(Integer.parseInt(currentPfxToken.getValue()));
            } else if (currentPfxToken.getName().equals(Lexer.VAR_NAME)) {
                stack.push(currentPfxToken.getValue());
            } else if (currentPfxToken.getName().equals(Lexer.ASSIGN_OP)) {
                tmpValue1 = getOperand(stack.pop());
                varMap.put((String) stack.pop(), tmpValue1);
            } else if (currentPfxToken.getName().equals(Lexer.ADD_OP)) {
                stack.push(getOperand(stack.pop()) + getOperand(stack.pop()));
            } else if (currentPfxToken.getName().equals(Lexer.DEC_OP)) {
                tmpValue1 = getOperand(stack.pop());
                stack.push(getOperand(stack.pop()) - tmpValue1);
            } else if (currentPfxToken.getName().equals(Lexer.MULTI_OP)) {
                stack.push(getOperand(stack.pop()) * getOperand(stack.pop()));
            }
//			System.out.println(stack);
            index++;
        }
        System.out.println("\n" + varMap);
    }

    private int getOperand(Object tmp) throws Exception {
        if (tmp.getClass().equals(String.class)) {
            if (varMap.containsKey((String) tmp)) {
                return varMap.get((String) tmp);
            } else {
                throw new Exception("\n\t\tMissing declare of \"" + (String) tmp + "\"");
            }
        }
        return (Integer) tmp;
    }

}

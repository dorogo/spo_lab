import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class PolizProcessor {
	private Stack<Object> stack;
	private Map<String, Integer> varMap;
	private List<PostfixToken> poliz;
	PostfixToken currentPfxToken;
	int index;
	int size;
	int tmpValue1;
	Object tmpValue2;
	
	public PolizProcessor( List<PostfixToken> srcPoliz) {
		varMap = new HashMap<String, Integer>();
		stack = new Stack<Object>();
		this.poliz = srcPoliz;
	}
	
	public void calcPoliz(){
		index = 0;
		size = this.poliz.size();
		while (index < size) {
			currentPfxToken = this.poliz.get(index);
			if (currentPfxToken.getName().equals(Lexer.DIGIT)) {
				stack.push(Integer.parseInt(currentPfxToken.getValue()));
			} else if (currentPfxToken.getName().equals(Lexer.VAR_NAME)) {
				stack.push(currentPfxToken.getValue());
			} else if (currentPfxToken.getName().equals(Lexer.ASSIGN_OP)) {
				tmpValue1 = (Integer) stack.pop();
				varMap.put((String) stack.pop(), (Integer)tmpValue1);
			} else if (currentPfxToken.getName().equals(Lexer.ADD_OP)) {
				stack.push(getOperand(stack.pop()) + getOperand(stack.pop()));
			} else if (currentPfxToken.getName().equals(Lexer.DEC_OP)) {
				tmpValue1 = getOperand(stack.pop());
				stack.push(getOperand(stack.pop()) - tmpValue1);
			} else if (currentPfxToken.getName().equals(Lexer.MULTI_OP)) {
				stack.push(getOperand(stack.pop()) * getOperand(stack.pop()));
			}
			System.out.println(stack);
			index++;
		}
		System.out.println("\n"+varMap);
	}
	
	private int getOperand(Object tmp) {
		if(tmp.getClass().equals(String.class)){
			return varMap.get((String) tmp);
		}
		return (Integer) tmp;
	}

}

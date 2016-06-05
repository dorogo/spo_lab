
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
    private Object tmpValue3;
    private int currentFBrackets;
    private boolean opStruct;

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
        String tmpK1 = "";
        String tmpK2 = "";
        String currStructName = "";
        opStruct = false;
        tmpMap = new HashMap<String, Integer>();

        while (index < size) {
            currentToken = this.poliz.get(index);
            if (currentToken.getName().equals(Lexer.DIGIT) || currentToken.getName().equals(Lexer.ADRESS)) {
                if (isStructHead) {
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
                    tmpValue1 = (Integer) getOperand(stack.pop());
                    varMapStruct.get(currStructName).put((String) stack.pop(), tmpValue1);
                } else if (opStruct) {
                    varMapStruct.put((String) stack.pop(), tmpMap);
                    opStruct = false;
                } else {
                    tmpValue1 = (Integer) getOperand(stack.pop());
                    tmpValue2 = stack.pop();
                    if (tmpValue2.getClass() == Integer.class) {
                        varMapStruct.get(tmpK1).put(tmpK2, tmpValue1);
                    } else {
//                        tmpValue1 = getOperand(stack.pop());
                        varMap.put((String) tmpValue2, tmpValue1);
                    }
                }
            } else if (currentToken.getName().equals(Lexer.ADD_OP)) {
                tmpValue2 = getOperand(stack.pop());
                tmpValue3 = getOperand(stack.pop());
                if (!opStruct) {
                    stack.push((Integer) tmpValue2 + (Integer) tmpValue3);
                } else {
                    doOpStruct(0, (String) tmpValue3, (String) tmpValue2);
                }
            } else if (currentToken.getName().equals(Lexer.DEC_OP)) {
                tmpValue2 = getOperand(stack.pop());
                tmpValue3 = getOperand(stack.pop());
                if (!opStruct) {
                    stack.push((Integer) tmpValue3 - (Integer) tmpValue2);
                } else {
                    doOpStruct(0, (String) tmpValue3, (String) tmpValue2);
                }
            } else if (currentToken.getName().equals(Lexer.MULTI_OP)) {
                tmpValue2 = getOperand(stack.pop());
                tmpValue3 = getOperand(stack.pop());
                if (!opStruct) {
                    stack.push((Integer) tmpValue3 * (Integer) tmpValue2);
                } else {
                    doOpStruct(0, (String) tmpValue3, (String) tmpValue2);
                }
            } else if (currentToken.getName().equals(Lexer.DIV_OP)) {
                tmpValue2 = getOperand(stack.pop());
                tmpValue3 = getOperand(stack.pop());
                if (!opStruct) {
                    stack.push((Integer) tmpValue3 / (Integer) tmpValue2);
                } else {
                    doOpStruct(0, (String) tmpValue3, (String) tmpValue2);
                }
            } else if (currentToken.getName().equals(Lexer.LESS_OP)) {
                stack.push((Integer) getOperand(stack.pop()) > (Integer) getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.LARGER_OP)) {
                stack.push((Integer) getOperand(stack.pop()) < (Integer) getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.EQUAL_OP)) {
                stack.push((Integer) getOperand(stack.pop()) == (Integer) getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.NOT_EQUAL_OP)) {
                stack.push((Integer) getOperand(stack.pop()) != (Integer) getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.LESS_N_EQUAL_OP)) {
                stack.push((Integer) getOperand(stack.pop()) >= (Integer) getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.LARGE_N_EQUAL_OP)) {
                stack.push((Integer) getOperand(stack.pop()) <= (Integer) getOperand(stack.pop()));
            } else if (currentToken.getName().equals(Lexer.FGO)) {
                tmpValue1 = (Integer) stack.pop();
                if (!(Boolean) stack.pop()) {
                    index = tmpValue1;
                }
            } else if (currentToken.getName().equals(Lexer.GO)) {
                index = (Integer) stack.pop();
            } else if (currentToken.getName().equals(Lexer.STRUCT_KW)) {
                isStruct = true;
                isStructHead = true;
            } else if (currentToken.getName().equals(Lexer.F_BRACKET_OPEN) && isStructHead) {
                isStructHead = false;
            } else if (currentToken.getName().equals(Lexer.F_BRACKET_CLOSE) && isStruct) {
                isStruct = false;
            } else if (currentToken.getName().equals(Lexer.DOT_OP)) {
                if (stack.size() == 2) {
                    tmpK2 = (String) stack.pop();
                    tmpK1 = (String) stack.pop();
                    stack.push(getOperandStruct(tmpK1, tmpK2));
                } else {
                    tmpValue2 = stack.pop();
                    stack.push(getOperandStruct((String) stack.pop(), (String) tmpValue2));
                }

            }
//			System.out.println(stack);
            index++;
        }

        System.out.print("\n\nvar map:" + varMap);
        System.out.print("\n\nvar map struct:" + varMapStruct);
    }
    private HashMap<String, Integer> tmpMap;

    private void doOpStruct(int i, String op1, String op2) throws Exception {
        if (varMapStruct.containsKey(op1) && varMapStruct.containsKey(op2)) {
            if (varMapStruct.get(op1).keySet().hashCode() == varMapStruct.get(op2).keySet().hashCode()) {
                for (String key : varMapStruct.get(op1).keySet()) {
                    if (varMapStruct.get(op2).containsKey(key)) {
                        switch (i) {
                            case 0:
                                tmpMap.put(key, (varMapStruct.get(op1).get(key) + varMapStruct.get(op2).get(key)));
                                break;
                            case 1:
                                tmpMap.put(key, (varMapStruct.get(op1).get(key) - varMapStruct.get(op2).get(key)));
                                break;
                            case 2:
                                tmpMap.put(key, (varMapStruct.get(op1).get(key) * varMapStruct.get(op2).get(key)));
                                break;
                            case 3:
                                tmpMap.put(key, (varMapStruct.get(op1).get(key) / varMapStruct.get(op2).get(key)));
                                break;
                        }
                    }
                }
            } else {
                throw new Exception("Structs isnt equal.");
            }
        }
    }

    private Object getOperand(Object tmp) throws Exception {
        if (tmp.getClass().equals(String.class)) {
            if (varMap.containsKey((String) tmp)) {
//            if (varMap.get(currentFBrackets).containsKey((String) tmp)) {
//                return (Integer)varMap.get(currentFBrackets).get((String) tmp);
                return varMap.get((String) tmp);
            } else if (varMapStruct.containsKey((String) tmp)) {
                opStruct = true;
                return tmp;
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

    private int getOperandStruct(String mapKey, String varKey) throws Exception {
        if (varMapStruct.containsKey(mapKey)) {
            if (varMapStruct.get(mapKey).containsKey(varKey)) {
                return varMapStruct.get(mapKey).get(varKey);
            } else {
                throw new Exception("\n\t\tMissing declare of var \"" + varKey + "\" in struct " + mapKey);
            }
        } else {
            throw new Exception("\n\t\tMissing declare of struct \"" + mapKey + "\"");
        }
    }

}

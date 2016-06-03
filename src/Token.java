
public class Token {

    private String name;
    private String value;
    private int numLine;
    private int opPriority;
    
    public Token() {
        
    }

    public Token(String name, String value, int numLine) {
        this.name = name;
        this.value = value;
        this.numLine = numLine;
        
        //create opPrio
        if(this.name.equals(Lexer.ASSIGN_OP)){
        	this.opPriority = 0;
        } else if (this.name.equals(Lexer.ADD_OP) || this.name.equals(Lexer.DEC_OP) ) {
        	this.opPriority = 1;
        } else if (this.name.equals(Lexer.MULTI_OP) || this.name.equals(Lexer.DIV_OP) ) {
        	this.opPriority = 2;
        } else {
        	this.opPriority = -1;
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getValue() {
        return this.value;
    }

    public void setVAlue(String vl) {
        this.value = vl;
    }
    
    public int getNumLine() {
        return this.numLine;
    }

    public void setNumLine(int ln) {
        this.numLine = ln;
    }
    
    public int getOpPriority() {
        return this.opPriority;
    }

    public void setOpPriority(int i) {
        this.opPriority = i;
    }
    @Override
    public String toString() {
        return ("Token{ name=\'" + name + "\'value=\'" + value + "\' }");
    }

}

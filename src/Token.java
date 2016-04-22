
public class Token {

    private String name;
    private String value;
    private int numLine;

    public Token(String name, String value, int numLine) {
        this.name = name;
        this.value = value;
        this.numLine = numLine;
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

    @Override
    public String toString() {
        return ("Token{name=\'" + name + "\'value=\'" + value + "\'}");
    }

}

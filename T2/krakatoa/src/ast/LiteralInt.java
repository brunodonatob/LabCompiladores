/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class LiteralInt extends Expr {
    
    public LiteralInt( int value ) { 
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    public void genCpp( PW pw, boolean putParenthesis ) {
        pw.print("" + value);
    }
    
	public void genKra(PW pw,boolean putParenthesis) {
        pw.print("" + value);
	}
    
    public Type getType() {
        return Type.intType;
    }
    
    private int value;
}

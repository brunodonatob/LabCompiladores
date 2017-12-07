/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class LiteralString extends Expr {
    
    public LiteralString( String literalString ) { 
        this.literalString = literalString;
    }
    
    public void genCpp( PW pw, boolean putParenthesis ) {
        pw.print("\""+literalString+"\"");
    }
    
    public void genKra(PW pw,boolean putParenthesis) {
        pw.print("\""+literalString+"\"");
	}
    
    public Type getType() {
        return Type.stringType;
    }
    
    private String literalString;
}

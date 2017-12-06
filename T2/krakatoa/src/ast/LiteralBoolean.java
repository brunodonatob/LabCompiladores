/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class LiteralBoolean extends Expr {

    public LiteralBoolean( boolean value ) {
        this.value = value;
    }

    @Override
	public void genCpp( PW pw, boolean putParenthesis ) {
       pw.print( value ? "1" : "0" );
    }

    @Override
	public Type getType() {
        return Type.booleanType;
    }

	public void genKra(PW pw,boolean putParenthesis) {
	       pw.print( value ? "true" : "false" );		
	}
	
    public static LiteralBoolean True  = new LiteralBoolean(true);
    public static LiteralBoolean False = new LiteralBoolean(false);

    private boolean value;
}

/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

import lexer.*;

public class SignalExpr extends Expr {

    public SignalExpr( Symbol oper, Expr expr ) {
       this.oper = oper;
       this.expr = expr;
    }

    @Override
	public void genCpp( PW pw, boolean putParenthesis ) {
    	if ( putParenthesis )
            pw.print("(");
         pw.print( oper == Symbol.PLUS ? "+" : "-" );
         expr.genCpp(pw, true);
         if ( putParenthesis )
            pw.print(")");
    }
    
    public void genKra(PW pw,boolean putParenthesis) {
    	if ( putParenthesis )
            pw.print("(");
         pw.print( oper == Symbol.PLUS ? "+" : "-" );
         expr.genKra(pw, true);
         if ( putParenthesis )
            pw.print(")");
	}

    @Override
	public Type getType() {
       return expr.getType();
    }

    private Expr expr;
    private Symbol oper;
}

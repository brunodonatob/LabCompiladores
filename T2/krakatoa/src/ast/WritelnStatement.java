/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

import java.util.ArrayList;

public class WritelnStatement extends WriteStatement {

	public WritelnStatement(ExprList exprList) {
		super(exprList);
	}

	@Override
	public void genCpp(PW pw) {
		pw.printIdent("cout << ");
		
		ArrayList<Expr> list = super.getExprList().getList();
		int size = super.getExprList().getSize();
        for ( Expr e : list ) {
        	e.genCpp(pw, false);
        	
        	pw.print(" << \" \"");
        	
            if ( --size > 0 )
                pw.print(" << ");
        }

		pw.println(" << \"\\n\";");

		
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("writeln ( ");
		super.getExprList().genKra(pw);
		pw.println(" );");
	}
}

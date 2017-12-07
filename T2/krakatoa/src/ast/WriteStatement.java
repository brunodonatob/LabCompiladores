/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

import java.util.ArrayList;

public class WriteStatement extends Statement {
	
	public WriteStatement(ExprList exprList) {
		this.exprList = exprList;
	}
	
	public ExprList getExprList() {
		return exprList;
	}

	@Override
	public void genCpp(PW pw) {
		pw.printIdent("cout << ");
		
		ArrayList<Expr> list = exprList.getList();
		int size = exprList.getSize();
        for ( Expr e : list ) {
        	e.genCpp(pw, false);
        	
        	pw.print(" << \" \"");
        	
            if ( --size > 0 )
                pw.print(" << ");
        }
		pw.println(";");
		
		
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("write ( ");
		exprList.genKra(pw);
		pw.println(" );");
		
	}

	private ExprList exprList;
}

/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class IfStatement extends Statement {

	public IfStatement(Expr e, Statement ifStmt, Statement elseStmt) {
		this.expr = e;
		this.ifStmt = ifStmt;
		this.elseStmt = elseStmt;
	}

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("if ( ");
		expr.genKra(pw, false);
		pw.print(" )");
		pw.add();
		ifStmt.genKra(pw);
		pw.sub();
		if(elseStmt != null) {
			pw.printIdent("else");
			pw.add();
			elseStmt.genKra(pw);
			pw.sub();
		}
		
	}
	
	private Expr expr;
	private Statement ifStmt;
	private Statement elseStmt;
}

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
	public void genCpp(PW pw) {
		pw.printIdent("if ( ");
		expr.genCpp(pw, false);
		pw.print(" )");
		pw.add();
		if(!(ifStmt instanceof CompositeStatement)) {
			pw.println("");
		}
		ifStmt.genCpp(pw);
		pw.sub();
		if(elseStmt != null) {
			pw.printIdent("else");
			pw.add();
			if(!(elseStmt instanceof CompositeStatement)) {
				pw.println("");
			}
			elseStmt.genCpp(pw);
			pw.sub();	
		}	
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("if ( ");
		expr.genKra(pw, false);
		pw.print(" )");
		pw.add();
		if(!(ifStmt instanceof CompositeStatement)) {
			pw.println("");
		}
		ifStmt.genKra(pw);
		pw.sub();
		if(elseStmt != null) {
			pw.printIdent("else");
			pw.add();
			if(!(elseStmt instanceof CompositeStatement)) {
				pw.println("");
			}
			elseStmt.genKra(pw);
			pw.sub();
		}
		
	}
	
	private Expr expr;
	private Statement ifStmt;
	private Statement elseStmt;
}

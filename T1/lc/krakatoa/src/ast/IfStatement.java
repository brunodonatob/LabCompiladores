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
		// TODO Auto-generated method stub
		
	}
	
	private Expr expr;
	private Statement ifStmt;
	private Statement elseStmt;
}

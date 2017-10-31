package ast;

public class WhileStatement extends Statement {

	public WhileStatement(Expr e, Statement s) {
		this.expr = e;
		this.statement = s;
	}

	public Expr getExpr() {
		return expr;
	}
	
	public Statement getStatement() {
		return statement;
	}

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void genKra(PW pw) {
		pw.printIdent("while ( ");
		expr.genKra(pw,false);
		pw.println(" ) ");
		pw.add();
		this.statement.genKra(pw);
		pw.sub();
	}
	
	private Expr expr;
	private Statement statement;
}

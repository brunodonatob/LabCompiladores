package ast;

public class ReturnStatement extends Statement {

	public ReturnStatement(Expr expr) {
		this.expr = expr;
	}
	
	public Expr getExpr() {
		return expr;
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

}

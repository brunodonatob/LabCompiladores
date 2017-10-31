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
		pw.printIdent("return ");
		expr.genKra(pw,false);
		pw.println("");
	}
	
	private Expr expr;

}

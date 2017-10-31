package ast;

public class AssignExpr extends Statement {

	public AssignExpr(Expr exprLeft, Expr exprRight) {
		this.exprLeft = exprLeft;
		this.exprRight = exprRight;
	}

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void genKra(PW pw) {
		exprLeft.genKra(pw);
		pw.print(" = ");
		exprRight.genKra(pw);
		//pw.print(";");
	}

	private Expr exprLeft;
	private Expr exprRight;
}

package ast;

public class DoWhileStatement extends Statement {

	public DoWhileStatement(CompositeStatement compStatement, Expr expr) {
		this.compStatement = compStatement;
		this.expr = expr;
	}

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw) {
		// TODO Auto-generated method stub
		
	}

	private CompositeStatement compStatement;
	private Expr expr;
}

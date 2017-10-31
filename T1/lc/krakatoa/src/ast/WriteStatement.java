package ast;

public class WriteStatement extends Statement {
	
	public WriteStatement(ExprList exprList) {
		this.exprList = exprList;
	}

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw) {
		// TODO Auto-generated method stub
		
	}

	private ExprList exprList;
}

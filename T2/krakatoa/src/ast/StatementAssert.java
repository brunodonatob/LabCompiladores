/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class StatementAssert extends Statement {
	public StatementAssert(Expr expr, int lineNumber, String message) {
		this.expr = expr;
		this.lineNumber = lineNumber;
		this.message = message;
	}
	@Override
	public void genCpp(PW pw) {

	}
	
	@Override
	public void genKra(PW pw) {
		// TODO Auto-generated method stub
		
	}

	public Expr getExpr() {
		return expr;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getMessage() {
		return message;
	}

	private Expr expr;
	private int lineNumber;
	private String message;
}

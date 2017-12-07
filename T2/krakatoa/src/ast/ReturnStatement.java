/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class ReturnStatement extends Statement {

	public ReturnStatement(Expr expr) {
		this.expr = expr;
	}
	
	public Expr getExpr() {
		return expr;
	}
	
	@Override
	public void genCpp(PW pw) {
		pw.printIdent("return ");
		expr.genCpp(pw,false);
		pw.println(";");
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("return ");
		expr.genKra(pw,false);
		pw.println(";");
	}
	
	private Expr expr;

}

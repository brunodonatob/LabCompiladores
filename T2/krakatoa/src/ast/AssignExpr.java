/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class AssignExpr extends Statement {

	public AssignExpr(Expr exprLeft, Expr exprRight) {
		this.exprLeft = exprLeft;
		this.exprRight = exprRight;
	}

	@Override
	public void genCpp(PW pw) {
		pw.printIdent("");
		exprLeft.genCpp(pw, false);
		
		if(this.exprRight != null) {
			pw.print(" = ");
			exprRight.genCpp(pw, false);
		}
		pw.println(";");

	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("");
		exprLeft.genKra(pw, false);
		
		if(this.exprRight != null) {
			pw.print(" = ");
			exprRight.genKra(pw, false);
		}
		pw.println(";");
	}

	private Expr exprLeft;
	private Expr exprRight;
}

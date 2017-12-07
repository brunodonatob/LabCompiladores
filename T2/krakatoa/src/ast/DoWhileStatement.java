/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class DoWhileStatement extends Statement {

	public DoWhileStatement(CompositeStatement compStatement, Expr expr) {
		this.compStatement = compStatement;
		this.expr = expr;
	}

	@Override
	public void genCpp(PW pw) {
		pw.printIdent("do");
		pw.add();

		compStatement.genCpp(pw);
		pw.sub();
		pw.printIdent("while ( ");
		expr.genCpp(pw,false);
		pw.println(" );");
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("do");
		pw.add();

		compStatement.genKra(pw);
		pw.sub();
		pw.printIdent("while ( ");
		expr.genKra(pw,false);
		pw.println(" );");
	}

	private CompositeStatement compStatement;
	private Expr expr;
}

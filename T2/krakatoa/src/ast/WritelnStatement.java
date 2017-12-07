/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class WritelnStatement extends WriteStatement {

	public WritelnStatement(ExprList exprList) {
		super(exprList);
	}

	@Override
	public void genCpp(PW pw) {
		pw.printIdent("cout << ");
		super.getExprList().genCpp(pw);
		pw.println(";");
		
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("writeln ( ");
		super.getExprList().genKra(pw);
		pw.println(" );");
	}
}

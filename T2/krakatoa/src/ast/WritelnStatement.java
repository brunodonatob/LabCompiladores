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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("writeln ( ");
		super.getExprList().genKra(pw);
		pw.println(" );");
	}
}

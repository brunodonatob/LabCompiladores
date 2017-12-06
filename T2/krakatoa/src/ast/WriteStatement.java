/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class WriteStatement extends Statement {
	
	public WriteStatement(ExprList exprList) {
		this.exprList = exprList;
	}
	
	public ExprList getExprList() {
		return exprList;
	}

	@Override
	public void genCpp(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("write ( ");
		exprList.genKra(pw);
		pw.println(" );");
		
	}

	private ExprList exprList;
}

/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class NewObjectExpr extends Expr {

	private KraClass className;

	public NewObjectExpr(KraClass aClass) {
		this.className = aClass;
	}

	@Override
	public void genC(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub

	}

	@Override
	public void genKra(PW pw,boolean putParenthesis) {
		pw.printIdent("new ");
		pw.print(className.getCname());
		pw.print("();");
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return className;
	}

}

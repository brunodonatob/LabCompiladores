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
	public void genCpp(PW pw, boolean putParenthesis) {
		pw.print("new ");
		pw.print(className.getCname());
		pw.print("()");
	}

	@Override
	public void genKra(PW pw,boolean putParenthesis) {
		pw.print("new ");
		pw.print(className.getCname());
		pw.print("()");
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return className;
	}

}

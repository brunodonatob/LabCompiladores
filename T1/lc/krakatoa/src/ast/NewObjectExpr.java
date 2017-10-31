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
	public void genKra(PW pw) {
		pw.printIdent("new ");
		className.genKra(pw);
		pw.print("();");
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return className;
	}

}

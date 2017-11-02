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
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("do {");
		pw.add();
		compStatement.genKra(pw);
		pw.sub();
		pw.print("");
		pw.print("}");
		pw.print("while ( ");
		expr.genKra(pw,false);
		pw.print(")");
		
		
	}

	private CompositeStatement compStatement;
	private Expr expr;
}

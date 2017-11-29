/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */

package ast;

import lexer.*;

public class UnaryExpr extends Expr {

	public UnaryExpr(Expr expr, Symbol op) {
		this.expr = expr;
		this.op = op;
	}

	@Override
	public void genCpp(PW pw, boolean putParenthesis) {
		switch (op) {
		case PLUS:
			pw.print("+");
			break;
		case MINUS:
			pw.print("-");
			break;
		case NOT:
			pw.print("!");
			break;
		default:
			pw.print(" internal error at UnaryExpr::genC");

		}
		expr.genCpp(pw, false);
	}

    public void genKra(PW pw,boolean putParenthesis) {
    	switch (op) {
		case PLUS:
			pw.print("+");
			break;
		case MINUS:
			pw.print("-");
			break;
		case NOT:
			pw.print("!");
			break;
		default:
			pw.print(" internal error at UnaryExpr::genKra");

		}
		expr.genKra(pw, false);
	}
    
	@Override
	public Type getType() {
		return expr.getType();
	}

	private Expr	expr;
	private Symbol	op;
}

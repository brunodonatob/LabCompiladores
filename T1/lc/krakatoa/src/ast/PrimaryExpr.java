package ast;

/* PrimaryExpressions Cases
 * 
 * 1. Id
 * 2. Id “.” Id | 
 * 3. Id “.” Id “(” [ ExpressionList ] ”)” |		
 * 4. Id “.” Id “.” Id “(” [ ExpressionList ] “)” | 
 * 5. “this” | 
 * 6. “this” “.” Id | 
 * 7. “this” ”.” Id “(” [ ExpressionList ] “)” | “super” “.” Id “(” [ ExpressionList ] “)” | 
 * 8. “this” ”.” Id “.” Id “(” [ ExpressionList ] “)”
 * 
 */

public class PrimaryExpr extends Expr {

	// 1. Id
	public PrimaryExpr( Variable v ) {
        this.id = v;
        this.primaryExprCase = 1;
    }
	
	
	// 2. Id "." Id 
	public PrimaryExpr(Variable avar, Variable v) {
		this.id = avar;
		this.var2 = v;
		this.primaryExprCase = 2;
	}
	
	
	// 3. Id "." Id "(" [ ExpressionList ] ")"
	public PrimaryExpr(Variable avar, MethodDec amethod, ExprList exprList) {
		this.id = avar;
		this.exprs = exprList;
		this.method = amethod;
		this.primaryExprCase = 3;
	}
	
	// 4. Id "." Id "." Id "(" [ ExpressionList ] ")"
	public PrimaryExpr(Variable avar, Variable var2, MethodDec method) {
		this.id = avar;
		this.var2 = var2;
		this.method = method;
		this.primaryExprCase = 4;
	}

	
	// 5. "this" 
	public PrimaryExpr(String string, KraClass kraClass) {
		this.str = string;
		this.kraClass = kraClass;
		this.primaryExprCase = 5;
	}
	
	// 6. "this" "." Id
	public PrimaryExpr(String string, Variable var) {
		this.id = var;
		this.str = string;
		this.primaryExprCase = 6;
	}

	// 7. "this" "." Id "(" [ ExpressionList ] ")" e  "super" "." Id "(" [ ExpressionList ] ")"
	public PrimaryExpr(String string, MethodDec amethod, ExprList exprList) {
		this.exprs = exprList;
		this.str = string;
		this.method = amethod;
		this.primaryExprCase = 7;
	}

	// 8. "this" "." Id "." Id "(" [ ExpressionList ] ")"
	public PrimaryExpr(String string,Variable var2, MethodDec amethod, ExprList exprList) {
		this.exprs = exprList;
		this.str = string;
		this.method = amethod;
		this.id = var2;
		this.primaryExprCase = 8;
	}



	@Override
	public void genC(PW pw, boolean putParenthesis) {
	}
	
	@Override
	public void genKra(PW pw,boolean putParenthesis) {
		if(str!= null)
			pw.printIdent(str+".");
		
		id.genKra(pw);
		if(var2 != null) {
			pw.print(".");
			var2.genKra(pw);
		}
		
		if(method!= null) {
			pw.print(".");
			method.genKra(pw);
		}
			
	}

	@Override
	public Type getType() {
		
		switch(primaryExprCase) {
		case 1:
			return id.getType();
		case 2:
			return var2.getType();
		case 3:
			return method.getReturnType();
		case 4:
			return method.getReturnType();
		case 5:
			return kraClass;
		case 6:
			return id.getType();
		case 7:
			return method.getReturnType();
		case 8:
			return method.getReturnType();
		default:
			return null;
		}
	}
	
	private Variable id = null;
	private Variable var2 = null;
	private Variable var3 = null;
	private ExprList exprs = null;
	private MethodDec method = null;
	private KraClass kraClass = null;
	private String str = null;
	private int primaryExprCase;
}

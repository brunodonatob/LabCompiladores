package ast;

public class PrimaryExpr extends Expr {
	
	Variable id = null;
	Variable var2 = null;
	Variable var3 = null;
	private ExprList exprs = null;
	private MethodDec method = null;
	private String str = null;

	// Id
	public PrimaryExpr( Variable v ) {
        this.id = v;
    }
	
	
	// Id "." Id 
	public PrimaryExpr(Variable avar, Variable v) {
		this.var2 = v;
		this.id = avar;
	}
	
	
	// Id "." Id "(" [ ExpressionList ] ")"
	public PrimaryExpr(Variable avar,MethodDec amethod, ExprList exprList) {
		this.id = avar;
		this.exprs = exprList;
		this.method = amethod;
	}
	
	// Id "." Id "." Id "(" [ ExpressionList ] ")"

	public PrimaryExpr(Variable avar, Variable var2, Variable var3) {
		this.id = avar;
		this.var2 = var2;
		this.var3 = var3;
	}

	
	// "this" 
	public PrimaryExpr(String string) {
		this.str = string;
	}

	// "this" "." Id "(" [ ExpressionList ] ")" e  "super" "." Id "(" [ ExpressionList ] ")"
	public PrimaryExpr(String string, MethodDec amethod, ExprList exprList) {
		this.exprs = exprList;
		this.str = string;
		this.method = amethod;
	}


	// "this" "." Id
	public PrimaryExpr(String string, Variable var) {
		this.id = var;
		this.str = string;
	}


	 // "this" "." Id "." Id "(" [ ExpressionList ] ")"
	public PrimaryExpr(String string,Variable var2, MethodDec amethod, ExprList exprList) {
		this.exprs = exprList;
		this.str = string;
		this.method = amethod;
		this.id = var2;
	}



	@Override
	public void genC(PW pw, boolean putParenthesis) {
	}
	
	@Override
	public void genKra(PW pw) {
		
	}

	@Override
	public Type getType() {
		return id.getType();
	}

}

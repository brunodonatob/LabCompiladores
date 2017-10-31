package ast;

public class PrimaryExpr extends Expr {
	
	Variable id = null;
	private ExprList exprs = null;
	private MethodDec method = null;
	private String str = null;

	// Id
	public PrimaryExpr( Variable v ) {
        this.id = v;
    }
	
	
	// Id "." Id 
	// .....................
	
	
	// Id "." Id "(" [ ExpressionList ] ")"
	public PrimaryExpr(Variable avar,MethodDec amethod, ExprList exprList) {
		this.id = avar;
		this.exprs = exprList;
		this.method = amethod;
	}
	
	// Id "." Id "." Id "(" [ ExpressionList ] ")"
	// ....................
	

	public PrimaryExpr(String string) {
		this.str = string;
	}


	public PrimaryExpr(String string, MethodDec amethod, ExprList exprList) {
		this.exprs = exprList;
		this.str = string;
		this.method = amethod;
	}


	@Override
	public void genC(PW pw, boolean putParenthesis) {
		
		// Isso aqui tá um caos ainda
		if(str!=null)
			pw.print(str);

			if(id != null)
				pw.print( id.getName() );
			if(method != null)
				method.genKra(pw);
			if(exprs != null)
				exprs.genC(pw);
			
		

	}

	@Override
	public Type getType() {
		return id.getType();
	}

}

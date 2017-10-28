package ast;

abstract public class Expr {
    
	abstract public void genC( PW pw, boolean putParenthesis );

	public void genKra(PW pw) {};
	
	// new method: the type of the expression
    abstract public Type getType();

}
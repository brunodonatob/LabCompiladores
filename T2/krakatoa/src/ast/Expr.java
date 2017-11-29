/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

abstract public class Expr {
    
	abstract public void genCpp( PW pw, boolean putParenthesis );

	abstract public void genKra(PW pw, boolean putParenthesis);
	
	// new method: the type of the expression
    abstract public Type getType();

}
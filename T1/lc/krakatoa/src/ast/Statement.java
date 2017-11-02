/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

abstract public class Statement {

	abstract public void genC(PW pw);
	
	abstract public void genKra(PW pw);

}

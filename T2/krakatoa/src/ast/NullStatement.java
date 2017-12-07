/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class NullStatement extends Statement {

	@Override
	public void genCpp(PW pw) {
		pw.printlnIdent(";");		
	}

	@Override
	public void genKra(PW pw) {
		pw.printlnIdent(";");		
	}

}

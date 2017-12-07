/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class BreakStatement extends Statement {

	@Override
	public void genCpp(PW pw) {
		pw.printIdent("break;");
		pw.println("");
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("break;");
		pw.println("");
	}

}

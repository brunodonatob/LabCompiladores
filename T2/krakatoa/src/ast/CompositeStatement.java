/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

import java.util.ArrayList;
import java.util.Iterator;

public class CompositeStatement extends Statement {

	public CompositeStatement(ArrayList<Statement> statementList) {
		this.statementList = statementList;
	}

	@Override
	public void genCpp(PW pw) {

		pw.println(" {");
		
		for(Statement statement : this.statementList) {
			statement.genCpp(pw);
		}

		pw.sub();
		pw.printIdent("}\n");
		pw.add();
		
	}

	@Override
	public void genKra(PW pw) {

		pw.println(" {");
		
		for(Statement statement : this.statementList) {
			statement.genKra(pw);
		}

		pw.sub();
		pw.printIdent("}\n");
		pw.add();
	}

	private ArrayList<Statement> statementList;
}

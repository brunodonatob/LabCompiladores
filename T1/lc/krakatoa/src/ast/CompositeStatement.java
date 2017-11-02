package ast;

import java.util.ArrayList;
import java.util.Iterator;

public class CompositeStatement extends Statement {

	public CompositeStatement(ArrayList<Statement> statementList) {
		this.statementList = statementList;
	}

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw) {

		pw.printIdent("{\n");
		
		//for(Statement statement : this.statementList) {
			//statement.genKra(pw);
		//}
		pw.printIdent("}\n");
	}

	private ArrayList<Statement> statementList;
}

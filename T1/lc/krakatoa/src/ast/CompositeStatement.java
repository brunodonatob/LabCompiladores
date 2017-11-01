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
		Iterator<Statement> sList = statementList.iterator();

		pw.printIdent("{ ");
		Statement s = sList.next();
		s.genKra(pw);
		
		while(sList.hasNext()) {
			s = sList.next();
			s.genKra(pw);
		}
		pw.print(" }");
	}

	private ArrayList<Statement> statementList;
}

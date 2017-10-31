package ast;

import java.util.ArrayList;

public class LocalDec extends Statement {

	public LocalDec(Type type, ArrayList<String> idList) {
		this.type = type;
		this.idList = idList;
	}

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void genKra(PW pw) {
		// TODO Auto-generated method stub

	}

	private Type type;
	private ArrayList<String> idList;
}

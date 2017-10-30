package ast;

import java.util.ArrayList;

import lexer.Symbol;

public class MethodDec {
	
	public String getName() {
		return name;
	}

	public Type getReturnType() {
		return returnType;
	}

	public Symbol getQualifier() {
		return qualifier;
	}
	
	public void addStatement(Statement statement) {
		statementList.add(statement);
	}

	public MethodDec(String name, Type returnType, Symbol qualifier) {
		this.name = name;
		this.returnType = returnType;
		this.qualifier = qualifier;
		this.statementList = new ArrayList<>();
	}
	
	private String name;
	private Type returnType;
	private Symbol qualifier;
	private ArrayList<Statement> statementList;
}

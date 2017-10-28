package ast;

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

	public MethodDec(String name, Type returnType, Symbol qualifier) {
		this.name = name;
		this.returnType = returnType;
		this.qualifier = qualifier;
	}
	
	private String name;
	private Type returnType;
	private Symbol qualifier;
}

/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
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
	
	public void addParameter(Parameter parameter) {
		this.paramList.addElement(parameter);
	}
	
	public int getNumberOfParameters() {
		return paramList.getSize();
	}
	
	public ParamList getParamList() {
		return this.paramList;
	}
	
	public void setReturnStatement() {
		this.hasReturnStatement = true;
	}
	
	public boolean hasReturnStatement() {
		return this.hasReturnStatement;
	}

	public MethodDec(String name, Type returnType, Symbol qualifier) {
		this.name = name;
		this.returnType = returnType;
		this.qualifier = qualifier;
		this.statementList = new ArrayList<>();
		this.paramList = new ParamList();
		this.hasReturnStatement = false;
	}
	
	public void genKra(PW pw) {
		pw.printIdent(qualifier.toString());
		pw.print(" "+ returnType.getName() +" ");
		pw.print(name +"(");
		this.paramList.genKra(pw);
		pw.println(") {");
		pw.add();
		
		for(Statement statement : this.statementList) {
			statement.genKra(pw);
		}
		
		pw.sub();
		pw.printIdent("}\n");
		
	}
	
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}
	
	private String name;
	private Type returnType;
	private Symbol qualifier;
	private ParamList paramList;
	private boolean hasReturnStatement;
	private ArrayList<Statement> statementList;

}

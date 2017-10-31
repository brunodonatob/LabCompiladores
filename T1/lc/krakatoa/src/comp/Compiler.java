/*
  Universidade Federal de Sao Carlos

   Bruno Donato  RA 587460
   Ingrid Santos RA 620300

*/

/*
 AssignExprLocalDec := Expression [ “=” Expression ] | LocalDec
 BasicType := “void” | “int” | “boolean” | “String”
 BasicValue := IntValue | BooleanValue | StringValue
 BooleanValue := “true” | “false”
 ClassDec := “class” Id [ “extends” Id ] “{” MemberList “}”
 CompStatement := “{” { Statement } “}“
 Digit := “0” | ... | “9”
 DoWhileStat := “do” CompStatement “while” “(” Expression “)”
 Expression := SimpleExpression [ Relation SimpleExpression ]
 ExpressionList := Expression { “,” Expression }
 Factor := BasicValue | “(” Expression “)” | “!” Factor | “null” | ObjectCreation | PrimaryExpr
 FormalParamDec := ParamDec { “,” ParamDec }
 HighOperator := “∗” | “/” | “&&”
 Id := Letter { Letter | Digit | “ ” }
 IdList := Id { “,” Id }
 IfStat := “if” “(” Expression “)” Statement [ “else” Statement ]
 InstVarDec := Type IdList “;”
 IntValue := Digit { Digit }
 LeftValue := [ (“this” | Id ) “.” ] Id
 Letter := “A” | ... | “Z” | “a” | ... | “z”
 LocalDec := Type IdList “;”
 LowOperator := “+” | “−” | “||”
 MemberList := { Qualifier Member }
 Member := InstVarDec | MethodDec
 MethodDec := Type Id “(” [ FormalParamDec ] “)” “{” StatementList “}”
 MOCall := “@” Id [ “(” { MOParam } “)” ]
 MOParam := IntValue | StringValue | Id
 ObjectCreation := “new” Id “(” “)”
 ParamDec := Type Id
 Program := { MOCall } ClassDec { ClassDec }
 Qualifier := [ “final” ] [ “static” ] ( “private” | “public”)
 ReadStat := “read” “(” LeftValue { “,” LeftValue } “)”
 PrimaryExpr := “super” “.” Id “(” [ ExpressionList ] “)” | Id | Id “.” Id | Id “.” Id “(” [ ExpressionList ] ”)” |
		Id “.” Id “.” Id “(” [ ExpressionList ] “)” | “this” | “this” “.” Id | “this” ”.” Id “(” [ ExpressionList ] “)” |
		“this” ”.” Id “.” Id “(” [ ExpressionList ] “)”
 Relation := “==” | “<” | “>” | “<=” | “>=” | “! =”
 ReturnStat := “return” Expression
 RightValue := “this” [ “.” Id ] | Id [ “.” Id ]
 Signal := “+” | “−”
 SignalFactor := [ Signal ] Factor
 SimpleExpression := Term { LowOperator Term }
 Statement := AssignExprLocalDec “;” | IfStat | WhileStat | ReturnStat “;” | ReadStat “;” | WriteStat “;” |
  	“break” “;” | “;” | CompStatement | DoWhileStat
 StatementList := { Statement }
 Term := SignalFactor { HighOperator SignalFactor }
 Type := BasicType | Id
 WriteStat := “write” “(” ExpressionList “)”
 WhileStat := “while” “(” Expression “)” Statement

 */

package comp;

import ast.*;
import lexer.*;
import java.io.*;
import java.util.*;

public class Compiler {

	// compile must receive an input with an character less than
	// p_input.lenght
	public Program compile(char[] input, PrintWriter outError) {

		ArrayList<CompilationError> compilationErrorList = new ArrayList<>();
		signalError = new ErrorSignaller(outError, compilationErrorList);
		symbolTable = new SymbolTable();
		lexer = new Lexer(input, signalError);
		signalError.setLexer(lexer);

		Program program = null;
		lexer.nextToken();
		program = program(compilationErrorList);
		return program;
	}

	//  Program := { MOCall } ClassDec { ClassDec }
	private Program program(ArrayList<CompilationError> compilationErrorList) {
        // Program ::= KraClass { KraClass }
        ArrayList<MetaobjectCall> metaobjectCallList = new ArrayList<>();
        ArrayList<KraClass> kraClassList = new ArrayList<>();
        
        Program program = new Program(kraClassList, metaobjectCallList, compilationErrorList);
        try {
            while ( lexer.token == Symbol.MOCall ) {
                metaobjectCallList.add(metaobjectCall());
            }
            kraClassList.add(classDec());
            while ( lexer.token == Symbol.CLASS )
            	kraClassList.add(classDec());
            if ( lexer.token != Symbol.EOF ) {
                signalError.showError("End of file expected");
            }
        }
        catch( CompilerError e) {
            // if there was an exception, there is a compilation signalError
        }
        catch ( RuntimeException e ) {
            e.printStackTrace();
        }
        
        // Verifica se existe a classe Program
        Boolean programExists = false;
        for(KraClass k: kraClassList) {
        	if(k.getName().equals("Program")) {
        		programExists = true;
        		
        		MethodDec method = k.searchMethod("run");
        		// Verifica se existe metodo 'run' na classe Program
        		if(method == null) {
        			signalError.showError("There must be a public method called 'run' in class Program");
        		}
        		
        		// Verifica se o metodo run nao tem parametros
        		if(method.getNumberOfParameters() != 0) {
        			signalError.showError("Run method must be parameterless");
        		}
        		
        		// Verifica se o metodo run eh public
        		if(method.getQualifier() != Symbol.PUBLIC) {
        			signalError.showError("Run method must be public");
        		}
        		
        		// Verifica se o metodo run retorna void
        		if(method.getReturnType() != Type.voidType) {
        			signalError.showError("Run method must return void");
        		}
        	}
        }
        if(programExists == false) {
        	signalError.showError("There must be a class called 'Program'");
        }
        
        return program;
    }

	/**  parses a metaobject call as <code>{@literal @}ce(...)</code> in <br>
     * <code>
     * @ce(5, "'class' expected") <br>
     * clas Program <br>
     *     public void run() { } <br>
     * end <br>
     * </code>
     *

	 */
	@SuppressWarnings("incomplete-switch")
	private MetaobjectCall metaobjectCall() {
		String name = lexer.getMetaobjectName();
		lexer.nextToken();
		ArrayList<Object> metaobjectParamList = new ArrayList<>();
		if ( lexer.token == Symbol.LEFTPAR ) {
			// metaobject call with parameters
			lexer.nextToken();
			while ( lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING ||
					lexer.token == Symbol.IDENT ) {
				switch ( lexer.token ) {
				case LITERALINT:
					metaobjectParamList.add(lexer.getNumberValue());
					break;
				case LITERALSTRING:
					metaobjectParamList.add(lexer.getLiteralStringValue());
					break;
				case IDENT:
					metaobjectParamList.add(lexer.getStringValue());
				}
				lexer.nextToken();
				if ( lexer.token == Symbol.COMMA )
					lexer.nextToken();
				else
					break;
			}
			if ( lexer.token != Symbol.RIGHTPAR )
				signalError.showError("')' expected after metaobject call with parameters");
			else
				lexer.nextToken();
		}
		if ( name.equals("nce") ) {
			if ( metaobjectParamList.size() != 0 )
				signalError.showError("Metaobject 'nce' does not take parameters");
		}
		else if ( name.equals("ce") ) {
			if ( metaobjectParamList.size() != 3 && metaobjectParamList.size() != 4 )
				signalError.showError("Metaobject 'ce' take three or four parameters");
			if ( !( metaobjectParamList.get(0) instanceof Integer)  )
				signalError.showError("The first parameter of metaobject 'ce' should be an integer number");
			if ( !( metaobjectParamList.get(1) instanceof String) ||  !( metaobjectParamList.get(2) instanceof String) )
				signalError.showError("The second and third parameters of metaobject 'ce' should be literal strings");
			if ( metaobjectParamList.size() >= 4 && !( metaobjectParamList.get(3) instanceof String) )
				signalError.showError("The fourth parameter of metaobject 'ce' should be a literal string");

		}

		return new MetaobjectCall(name, metaobjectParamList);
	}

	//  ClassDec := “class” Id [ “extends” Id ] “{” MemberList “}”
	private KraClass classDec() {
		// Note que os metodos desta classe nao correspondem exatamente as
		// regras
		// da gramatica. Este metodo classDec, por exemplo, implementa
		// a producao KraClass (veja abaixo) e partes de outras producoes.

		/*
		 * KraClass ::= ``class'' Id [ ``extends'' Id ] "{" MemberList "}"
		 * MemberList ::= { Qualifier Member }
		 * Member ::= InstVarDec | MethodDec
		 * InstVarDec ::= Type IdList ";"
		 * MethodDec ::= Qualifier Type Id "("[ FormalParamDec ] ")" "{" StatementList "}"
		 * Qualifier ::= [ "static" ]  ( "private" | "public" )
		 */
		if ( lexer.token != Symbol.CLASS ) signalError.showError("'class' expected");
		lexer.nextToken();

		// Class name
		if ( lexer.token != Symbol.IDENT )
			signalError.show(ErrorSignaller.ident_expected);
		String className = lexer.getStringValue();
		
		this.currentClass = new KraClass(className);
		
		// Verifica se essa classe ja existe
		if(symbolTable.getInGlobal(className) != null) {
			signalError.showError("A class with name '"+ className +"' was already declared");
		}
		
		symbolTable.putInGlobal(className, currentClass);
		lexer.nextToken();
		
		// Extends
		if ( lexer.token == Symbol.EXTENDS ) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show(ErrorSignaller.ident_expected);
			String superclassName = lexer.getStringValue();
			
			// Verifica se a superclasse eh igual a classe atual
			if(className.equals(superclassName)) {
				signalError.showError("A class cannot inherit from itself");
			}
			
			// Verifica se a superclasse existe
			if(symbolTable.getInGlobal(superclassName) == null) {
				signalError.showError("Superclass '"+ className +"' must be declared before");
			}

			lexer.nextToken();
		}
		
		if ( lexer.token != Symbol.LEFTCURBRACKET )
			signalError.showError("{ expected", true);
		lexer.nextToken();

		while (lexer.token == Symbol.PRIVATE || lexer.token == Symbol.PUBLIC) {

			Symbol qualifier;
			switch (lexer.token) {
			case PRIVATE:
				lexer.nextToken();
				qualifier = Symbol.PRIVATE;
				break;
			case PUBLIC:
				lexer.nextToken();
				qualifier = Symbol.PUBLIC;
				break;
			default:
				signalError.showError("private, or public expected");
				qualifier = Symbol.PUBLIC;
			}
			
			Type t = type();
			
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");
			String name = lexer.getStringValue();
			lexer.nextToken();
			
			if ( lexer.token == Symbol.LEFTPAR )
				methodDec(t, name, qualifier);
			else if ( qualifier != Symbol.PRIVATE )
				signalError.showError("Attempt to declare a public instance variable");
			else
				instanceVarDec(t, name);
		}
		
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.showError("public/private or \"}\" expected");
		lexer.nextToken();
		
		return currentClass;

	}

	private void instanceVarDec(Type type, String name) {
		// InstVarDec ::= [ "static" ] "private" Type IdList ";"

		// Verifica se a variavel ja foi declarada na classe
		if(this.currentClass.searchInstanceVariable(name) != null) {
			signalError.showError("Instance variable '"+ name +"' has already been declared");
		}
		
		InstanceVariable instanceVariable = new InstanceVariable(name, type);
		this.currentClass.addInstanceVariable(instanceVariable);
		
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");
			String variableName = lexer.getStringValue();
			
			// Verifica se a variavel ja foi declarada na classe
			if(this.currentClass.searchInstanceVariable(variableName) != null) {
				signalError.showError("Instance variable '"+ variableName +"' has already been declared");
			}
			instanceVariable = new InstanceVariable(variableName, type);
			this.currentClass.addInstanceVariable(instanceVariable);
			lexer.nextToken();
		}
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

	private void methodDec(Type type, String name, Symbol qualifier) {
		/*
		 * MethodDec ::= Qualifier Return Id "("[ FormalParamDec ] ")" "{"
		 *                StatementList "}"
		 */
		
		this.currentMethod = new MethodDec(name, type, qualifier);
		
		lexer.nextToken();
		if ( lexer.token != Symbol.RIGHTPAR ) 
			formalParamDec();
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTCURBRACKET ) signalError.showError("{ expected");

		lexer.nextToken();
		statementList();

		if ( lexer.token != Symbol.RIGHTCURBRACKET ) signalError.showError("} expected");

		lexer.nextToken();
		
		this.currentClass.addMethod(this.currentMethod);
		
		this.currentMethod = null;
		
		// Remove todas as variaveis locais da localTable
		this.symbolTable.removeLocalIdent();

	}

	private void localDec() {
		// LocalDec ::= Type IdList ";"

		Type type = type();
		if ( lexer.token != Symbol.IDENT ) signalError.showError("Identifier expected");
		Variable v = new Variable(lexer.getStringValue(), type);
		this.symbolTable.putInLocal(v.getName(), v);
		lexer.nextToken();
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");
			v = new Variable(lexer.getStringValue(), type);
			lexer.nextToken();
		}
	}

	private void formalParamDec() {
		// FormalParamDec ::= ParamDec { "," ParamDec }

		this.currentMethod.addParameter(paramDec());
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			this.currentMethod.addParameter(paramDec());
		}
	}

	private Parameter paramDec() {
		// ParamDec ::= Type Id

		Type t = type();
		if ( lexer.token != Symbol.IDENT ) signalError.showError("Identifier expected");
		
		Parameter parameter = new Parameter(lexer.getStringValue(), t);
		
		// Verifica se parametro ja existe
		if(symbolTable.getInLocal(parameter.getName()) != null) {
			signalError.showError("A variable with name '"+ parameter.getName() +"' has already been declared");
		}
		this.symbolTable.putInLocal(parameter.getName(), parameter);
		
		lexer.nextToken();
		
		return parameter;
	}

	private Type type() {
		// Type ::= BasicType | Id
		Type result;

		switch (lexer.token) {
		case VOID:
			result = Type.voidType;
			break;
		case INT:
			result = Type.intType;
			break;
		case BOOLEAN:
			result = Type.booleanType;
			break;
		case STRING:
			result = Type.stringType;
			break;
		case IDENT:
			// IDENT deve ser uma classe.
			result = this.symbolTable.getInGlobal(lexer.getStringValue());
			
			if(result == null) {
				this.signalError.showError("Class with identifier '"+ lexer.getStringValue() +"' not found");
			}
			break;
		default:
			signalError.showError("Type expected");
			result = Type.undefinedType;
		}
		lexer.nextToken();
		return result;
	}

	private void compositeStatement() {

		lexer.nextToken();
		statementList();
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.showError("} expected");
		else
			lexer.nextToken();
	}

	private void statementList() {
		// CompStatement ::= "{" { Statement } "}"
		Symbol tk;
		// statements always begin with an identifier, if, read, write, ...
		while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET
				&& tk != Symbol.ELSE)
			this.currentMethod.addStatement(statement());
	}

	private Statement statement() {
		/*
		 * Statement ::= Assignment ``;'' | IfStat |WhileStat | MessageSend
		 *                ``;'' | ReturnStat ``;'' | ReadStat ``;'' | WriteStat ``;'' |
		 *               ``break'' ``;'' | ``;'' | CompStatement | LocalDec
		 */

		switch (lexer.token) {
		case THIS:
		case IDENT:
		case SUPER:
		case INT:
		case BOOLEAN:
		case STRING:
			assignExprLocalDec();
			break;
		case ASSERT:
			assertStatement();
			break;
		case RETURN:
			return returnStatement();
		case READ:
			readStatement();
			break;
		case WRITE:
			writeStatement();
			break;
		case WRITELN:
			writelnStatement();
			break;
		case IF:
			ifStatement();
			break;
		case BREAK:
			return breakStatement();
		case WHILE:
			return whileStatement();
		case SEMICOLON:
			nullStatement();
			break;
		case LEFTCURBRACKET:
			compositeStatement();
			break;
		default:
			signalError.showError("Statement expected");
		}
		
		return null;
	}

	private Statement assertStatement() {
		lexer.nextToken();
		int lineNumber = lexer.getLineNumber();
		Expr e = expr();
		if ( e.getType() != Type.booleanType )
			signalError.showError("boolean expression expected");
		if ( lexer.token != Symbol.COMMA ) {
			this.signalError.showError("',' expected after the expression of the 'assert' statement");
		}
		lexer.nextToken();
		if ( lexer.token != Symbol.LITERALSTRING ) {
			this.signalError.showError("A literal string expected after the ',' of the 'assert' statement");
		}
		String message = lexer.getLiteralStringValue();
		lexer.nextToken();
		if ( lexer.token == Symbol.SEMICOLON )
			lexer.nextToken();

		return new StatementAssert(e, lineNumber, message);
	}

	/*
	 * retorne true se 'name' � uma classe declarada anteriormente. � necess�rio
	 * fazer uma busca na tabela de s�mbolos para isto.
	 */
	private boolean isType(String name) {
		return this.symbolTable.getInGlobal(name) != null;
	}

	/*
	 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
	 */
	private Expr assignExprLocalDec() {
		if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
				|| lexer.token == Symbol.STRING ||
				// token eh uma classe declarada textualmente antes desta
				// instrucao
				(lexer.token == Symbol.IDENT && isType(lexer.getStringValue())) ) {
			/*
			 * uma declaracao de variavel. 'lexer.token' eh o tipo da variavel
			 *
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
			 * LocalDec ::= Type IdList ``;''
			 */
			localDec();
			
			if ( lexer.token != Symbol.SEMICOLON )
				signalError.showError("';' expected", true);
			else
				lexer.nextToken();
		}
		else {
			/*
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ]
			 */
			
			Expr expressao = expr();	
			
			
			if ( lexer.token == Symbol.ASSIGN ) {
				lexer.nextToken();
				
				Expr e = expr();
				
				// Ainda tem que organizar classes pra fazer isso
				if(e.getType() != expressao.getType()) {
					signalError.showError("Wrong type error");
				} 
				
				if ( lexer.token != Symbol.SEMICOLON )
					signalError.showError("';' expected", true);
				else
					lexer.nextToken();
			}
		}
		return null;
	}

	private ExprList realParameters() {
		ExprList anExprList = null;

		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		if ( startExpr(lexer.token) ) anExprList = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		return anExprList;
	}

	// WhileStat := “while” “(” Expression “)” Statement
	private WhileStatement whileStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		Expr e = expr();
		if(e.getType() != Type.booleanType) {
			signalError.showError("boolean expression expected.");
		}
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		
		Statement s = statement();
		
		return new WhileStatement(e, s);
	}

	private void ifStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		Expr e = expr();
		if(e.getType() != Type.booleanType) {
			signalError.showError("boolean expression expected.");
		}
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		statement();
		if ( lexer.token == Symbol.ELSE ) {
			lexer.nextToken();
			statement();
		}
	}

	private ReturnStatement returnStatement() {

		lexer.nextToken();
		Expr e = expr();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
		
		if( this.currentMethod.getReturnType() == Type.voidType ) {
			this.signalError.showError("This method cannot return a value");
		}
		
		if(!e.getType().isCompatible(this.currentMethod.getReturnType())) {
			this.signalError.showError("This expression is not compatible with the method return type");			
		}
		
		return new ReturnStatement(e);
	}

	private void readStatement() {
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		while (true) {
			if ( lexer.token == Symbol.THIS ) {
				lexer.nextToken();
				if ( lexer.token != Symbol.DOT ) signalError.showError(". expected");
				lexer.nextToken();
			}
			if ( lexer.token != Symbol.IDENT )
				signalError.show(ErrorSignaller.ident_expected);

			String name = lexer.getStringValue();
			lexer.nextToken();
			if ( lexer.token == Symbol.COMMA )
				lexer.nextToken();
			else
				break;
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

	private void writeStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

	private void writelnStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

	// "break" ";"
	private BreakStatement breakStatement() {
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
		
		return new BreakStatement();
	}

	private void nullStatement() {
		lexer.nextToken();
	}

	private ExprList exprList() {
		// ExpressionList ::= Expression { "," Expression }

		ExprList anExprList = new ExprList();
		anExprList.addElement(expr());
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			anExprList.addElement(expr());
		}
		return anExprList;
	}

	private Expr expr() {

		Expr left = simpleExpr();
		Symbol op = lexer.token;
		if ( op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE
				|| op == Symbol.LT || op == Symbol.GE || op == Symbol.GT ) {
			lexer.nextToken();
			Expr right = simpleExpr();
			
			// Ainda precisa arrumar classes pra isso funcionar
			if(left.getType() != right.getType() && (op == Symbol.NEQ || op == Symbol.EQ))
				signalError.showError("Incompatible types cannot be compared");
			
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}


	private Expr simpleExpr() {
		Symbol op;

		Expr left = term();
		while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
				|| op == Symbol.OR) {
			lexer.nextToken();
			Expr right = term();
			
			if(left.getType().getName().equals("boolean") && op != Symbol.OR)
				signalError.showError("Type boolean does not support this operation");
				
			if(left.getType() != right.getType()) 
				signalError.showError("Type error");

			
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr term() {
		Symbol op;

		Expr left = signalFactor();
		while ((op = lexer.token) == Symbol.DIV || op == Symbol.MULT
				|| op == Symbol.AND) {
			lexer.nextToken();
			Expr right = signalFactor();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr signalFactor() {
		Symbol op;
		if ( (op = lexer.token) == Symbol.PLUS || op == Symbol.MINUS ) {
			lexer.nextToken();
			return new SignalExpr(op, factor());
		}
		else
			return factor();
	}

	/*
	 * Factor ::= BasicValue | "(" Expression ")" | "!" Factor | "null" |
	 *      ObjectCreation | PrimaryExpr
	 *
	 * BasicValue ::= IntValue | BooleanValue | StringValue
	 * BooleanValue ::=  "true" | "false"
	 * ObjectCreation ::= "new" Id "(" ")"
	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  |
	 *                 Id  |
	 *                 Id "." Id |
	 *                 Id "." Id "(" [ ExpressionList ] ")" |
	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
	 *                 "this" |
	 *                 "this" "." Id |
	 *                 "this" "." Id "(" [ ExpressionList ] ")"  |
	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
	 */
	private Expr factor() {

		Expr anExpr;
		ExprList exprList;
		String messageName, id;

		switch (lexer.token) {
		// IntValue
		case LITERALINT:
			return literalInt();
			// BooleanValue
		case FALSE:
			lexer.nextToken();
			return LiteralBoolean.False;
			// BooleanValue
		case TRUE:
			lexer.nextToken();
			return LiteralBoolean.True;
			// StringValue
		case LITERALSTRING:
			String literalString = lexer.getLiteralStringValue();
			lexer.nextToken();
			return new LiteralString(literalString);
			// "(" Expression ")" |
		case LEFTPAR:
			lexer.nextToken();
			anExpr = expr();
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
			lexer.nextToken();
			return new ParenthesisExpr(anExpr);

			// "null"
		case NULL:
			lexer.nextToken();
			return new NullExpr();
			// "!" Factor
		case NOT:
			lexer.nextToken();
			anExpr = expr();
			if(anExpr.getType().getName().equals("int"))
				signalError.showError("Operator ! does not accept int values");
			return new UnaryExpr(anExpr, Symbol.NOT);
			// ObjectCreation ::= "new" Id "(" ")"
		case NEW:
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");

			String className = lexer.getStringValue();
			KraClass aClass = this.symbolTable.getInGlobal(className);
			if(aClass == null) {
				this.signalError.showError("Class '" + className + "' does not exist");
			}
			/*
			 * // encontre a classe className in symbol table KraClass
			 *      aClass = symbolTable.getInGlobal(className);
			 *      if ( aClass == null ) ...
			 */

			lexer.nextToken();
			if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
			lexer.nextToken();
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
			lexer.nextToken();
			/*
			 * return an object representing the creation of an object
			 */
			return null;
			/*
          	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  |
          	 *                 Id  |
          	 *                 Id "." Id |
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 "this" |
          	 *                 "this" "." Id |
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  |
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
		case SUPER:
			// "super" "." Id "(" [ ExpressionList ] ")"
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				signalError.showError("'.' expected");
			}
			else
				lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");
			messageName = lexer.getStringValue();
			/*
			 * para fazer as confer�ncias sem�nticas, procure por 'messageName'
			 * na superclasse/superclasse da superclasse etc
			 */
			lexer.nextToken();
			exprList = realParameters();
			break;
		case IDENT:
			/*
          	 * PrimaryExpr ::=
          	 *                 Id  |
          	 *                 Id "." Id |
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
			 */

			String firstId = lexer.getStringValue();
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				Variable avar = this.symbolTable.getInLocal(firstId);
				if(avar == null) {
					this.signalError.showError("Variable '" + firstId + "' was not declared");
				}
				// Id
				// retorne um objeto da ASA que representa um identificador
				return  new PrimaryExpr(avar);
			}
			else { // Id "."
				lexer.nextToken(); // coma o "."
				if ( lexer.token != Symbol.IDENT ) {
					signalError.showError("Identifier expected");
				}
				else {
					// Id "." Id
					lexer.nextToken();
					id = lexer.getStringValue();
					if ( lexer.token == Symbol.DOT ) {
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																													
						/*
						 * se o compilador permite vari�veis est�ticas, � poss�vel
						 * ter esta op��o, como
						 *     Clock.currentDay.setDay(12);
						 * Contudo, se vari�veis est�ticas n�o estiver nas especifica��es,
						 * sinalize um erro neste ponto.
						 */
						
						Variable avar = this.symbolTable.getInLocal(firstId);
						if(avar == null) {
							this.signalError.showError("Variable '" + firstId + "' was not declared");
						}

						Type typeVar = avar.getType();
						
						KraClass classVar = (KraClass ) typeVar;
						InstanceVariable var = classVar.searchInstanceVariable(id);
						
						if(var == null) {
							this.signalError.showError("Variable '" + id + "' does not exist in class '"+classVar.getName()+"'");								
						}
						
						lexer.nextToken();
						if ( lexer.token != Symbol.IDENT )
							signalError.showError("Identifier expected");
						messageName = lexer.getStringValue();
						
						Variable var2 = this.symbolTable.getInLocal(id);
						if(var2 == null) {
							this.signalError.showError("Variable '" + id + "' was not declared");
						}
						
						Type tvar = var2.getType();
						KraClass cvar = (KraClass) tvar;
						
						InstanceVariable v3 = classVar.searchInstanceVariable(messageName);
						Variable var3 = this.symbolTable.getInLocal(messageName);
						
						if(v3 == null) {
							this.signalError.showError("Variable '" + messageName + "' does not exist in class '"+cvar.getName()+"'");								
						}
						
						return new PrimaryExpr(avar,var2,var3);

					}
					else if ( lexer.token == Symbol.LEFTPAR ) {
						// Id "." Id "(" [ ExpressionList ] ")"
						
						Variable avar = this.symbolTable.getInLocal(firstId);
						if(avar == null) {
							this.signalError.showError("Variable '" + firstId + "' was not declared");
						}
						Type typeVar = avar.getType();
						if(!(typeVar instanceof KraClass)) {
							this.signalError.showError("Attempt to call a method on a variable of a basic type");							
						}
						
						KraClass classVar = (KraClass ) typeVar;
						// method is id
						MethodDec amethod = classVar.searchPublicMethod(id);
						if(amethod == null) {
							this.signalError.showError("Method '" + id + "' is not a public method of '" + 
									classVar.getName() + "' which is the type of '" + firstId + "'");								
						}
						
						exprList = this.realParameters();
						
						return new PrimaryExpr(avar,amethod,exprList);
					}
					else {
						// retorne o objeto da ASA que representa Id "." Id
						Variable avar = this.symbolTable.getInLocal(firstId);
						if(avar == null) {
							this.signalError.showError("Variable '" + firstId + "' was not declared");
						}
						Type typeVar = avar.getType();
						
						KraClass classVar = (KraClass ) typeVar;
						InstanceVariable var = classVar.searchInstanceVariable(id);
						Variable v = this.symbolTable.getInLocal(id);

						
						if(var == null) {
							this.signalError.showError("Variable '" + id + "' does not exist in class '"+classVar.getName()+"'");								
						}
						
						
						return new PrimaryExpr(avar,v);
					}	}
			}
			break;
		case THIS:
			/*
			 * Este 'case THIS:' trata os seguintes casos:
          	 * PrimaryExpr ::=
          	 *                 "this" |
          	 *                 "this" "." Id |
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  |
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				// only 'this'
				// retorne um objeto da ASA que representa 'this'
				// confira se n�o estamos em um m�todo est�tico
				return new PrimaryExpr("this");
			}
			else {
				lexer.nextToken();
				if ( lexer.token != Symbol.IDENT )
					signalError.showError("Identifier expected");
				id = lexer.getStringValue();
				lexer.nextToken();
				// j� analisou "this" "." Id
				if ( lexer.token == Symbol.LEFTPAR ) {
					// "this" "." Id "(" [ ExpressionList ] ")"
					/*
					 * Confira se a classe corrente possui um m�todo cujo nome �
					 * 'ident' e que pode tomar os par�metros de ExpressionList
					 */
					
					MethodDec amethod = currentClass.searchPublicMethod(id);
					if(amethod == null) {
						this.signalError.showError("Method '" + id + "' is not a public method of '" + 
								currentClass.getName() +"'");								
					}
					
					exprList = this.realParameters();
					
					return new PrimaryExpr("this",amethod,exprList);
				}
				else if ( lexer.token == Symbol.DOT ) {
					// "this" "." Id "." Id "(" [ ExpressionList ] ")"
					lexer.nextToken();
					if ( lexer.token != Symbol.IDENT )
						signalError.showError("Identifier expected");
					
					InstanceVariable var = currentClass.searchInstanceVariable(id);
					
					if(var == null) {
						this.signalError.showError("Variable '" + id + "' does not exist in the current class");								
					}
					
					Variable v = this.symbolTable.getInLocal(id);

					lexer.nextToken();
					
					MethodDec amethod = currentClass.searchPublicMethod(lexer.getStringValue());
					if(amethod == null) {
						this.signalError.showError("Method '" + lexer.getStringValue() + "' is not a public method of '" + 
								currentClass.getName() +"'");								
					}
					exprList = this.realParameters();
					
					return new PrimaryExpr("this",v,amethod,exprList);
				}
				else {
					// retorne o objeto da ASA que representa "this" "." Id
					/*
					 * confira se a classe corrente realmente possui uma
					 * vari�vel de inst�ncia 'ident'
					 */
					InstanceVariable var = currentClass.searchInstanceVariable(id);
					
					if(var == null) {
						this.signalError.showError("Variable '" + id + "' does not exist in the current class");								
					}
					
					Variable v = this.symbolTable.getInLocal(id);

					
					return new PrimaryExpr("this",v);
				}
			}
		default:
			signalError.showError("Expression expected");
		}
		return null;
	}

	private LiteralInt literalInt() {

		LiteralInt e = null;

		// the number value is stored in lexer.getToken().value as an object of
		// Integer.
		// Method intValue returns that value as an value of type int.
		int value = lexer.getNumberValue();
		lexer.nextToken();
		return new LiteralInt(value);
	}

	private static boolean startExpr(Symbol token) {

		return token == Symbol.FALSE || token == Symbol.TRUE
				|| token == Symbol.NOT || token == Symbol.THIS
				|| token == Symbol.LITERALINT || token == Symbol.SUPER
				|| token == Symbol.LEFTPAR || token == Symbol.NULL
				|| token == Symbol.IDENT || token == Symbol.LITERALSTRING;

	}

	private SymbolTable		symbolTable;
	private Lexer			lexer;
	private ErrorSignaller	signalError;
	private MethodDec 		currentMethod;
	private KraClass 		currentClass;

}

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
            
        }
        catch( CompilerError e) {
            // if there was an exception, there is a compilation signalError
        }
        catch ( RuntimeException e ) {
            e.printStackTrace();
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
     *
     * MOCall := “@” Id [ “(” { MOParam } “)” ]
     * MOParam := IntValue | StringValue | Id
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
			
			KraClass superClass = symbolTable.getInGlobal(superclassName);
			
			// Verifica se a superclasse existe
			if(superClass == null) {
				signalError.showError("Superclass '"+ className +"' must be declared before");
			}

			this.currentClass.setSuperclass(superClass);
			
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
		
		ArrayList<Statement> statementList;
		
		this.currentMethod = new MethodDec(name, type, qualifier);
		
		lexer.nextToken();
		if ( lexer.token != Symbol.RIGHTPAR ) 
			formalParamDec();
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTCURBRACKET ) signalError.showError("{ expected");

		this.currentClass.addMethod(this.currentMethod);
		
		KraClass superClass = this.currentClass.getSuperclass();
		
		if(superClass != null) {
			MethodDec amethod = superClass.searchMethod(name);
			
			if(amethod == null) {
				superClass = superClass.getSuperclass();
				
				while(superClass != null) {
					amethod = superClass.searchPublicMethod(name);
					
					if(amethod != null)
						break;
					else
						superClass = superClass.getSuperclass();
				}
			}
	
			// Verifica se retorno e parametros sao iguais
			if(amethod != null) {
				if(this.currentMethod.getReturnType() != amethod.getReturnType())
					signalError.showError("Redefined method must have the same signature");
				
				if(this.currentMethod.getNumberOfParameters() != amethod.getNumberOfParameters())
					signalError.showError("Redefined method must have the same signature");
				
				if(!this.currentMethod.getParamList().compareParameters(amethod.getParamList()))
					signalError.showError("Redefined method must have the same signature");
			}
		}
		
		// Verifica se metodo esta sendo redeclarado
		//if(this.currentClass.searchMethod(name) != null)
			//signalError.showError("Method '"+ name +"' cannot be redeclared");

		lexer.nextToken();
		statementList = statementList();
		
		for(Statement stmt : statementList) {
			this.currentMethod.addStatement(stmt);
		}

		if ( lexer.token != Symbol.RIGHTCURBRACKET ) signalError.showError("} expected");

		lexer.nextToken();
		
		this.currentMethod = null;
		
		// Remove todas as variaveis locais da localTable
		this.symbolTable.removeLocalIdent();

	}

	private Statement localDec() {
		// LocalDec ::= Type IdList ";"
		
		ArrayList<String> idList = new ArrayList<>();
		
		Type type = type();
		if ( lexer.token != Symbol.IDENT ) signalError.showError("Identifier expected");
		
		Variable v = new Variable(lexer.getStringValue(), type);
		idList.add(lexer.getStringValue());
		
		if(this.symbolTable.getInLocal(v.getName()) != null) {
			signalError.showError("A variable with name '"+ v.getName() +"' has already been declared");
		}
		this.symbolTable.putInLocal(v.getName(), v);
		
		lexer.nextToken();
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");
			v = new Variable(lexer.getStringValue(), type);
			idList.add(lexer.getStringValue());
			
			if(this.symbolTable.getInLocal(v.getName()) != null) {
				signalError.showError("A variable with name '"+ v.getName() +"' has already been declared");
			}
			this.symbolTable.putInLocal(v.getName(), v);
			
			lexer.nextToken();
		}
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.showError("';' expected", true);
		else
			lexer.nextToken();
		
		return new LocalDec(type, idList);
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

	private CompositeStatement compositeStatement() {

		CompositeStatement compStatement;
		
		lexer.nextToken();
		
		compStatement = new CompositeStatement(statementList());
		
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.showError("} expected");
		else
			lexer.nextToken();
		
		return compStatement;
	}

	private ArrayList<Statement> statementList() {
		// CompStatement ::= "{" { Statement } "}"
		ArrayList<Statement> statementList = new ArrayList<>();
		
		Symbol tk;
		// statements always begin with an identifier, if, read, write, ...
		while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET
				&& tk != Symbol.ELSE)
			statementList.add(statement());
		
		return statementList;
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
			return assignExprLocalDec();
		case ASSERT:
			assertStatement();
			break;
		case RETURN:
			return returnStatement();
		case READ:
			return readStatement();
		case WRITE:
			return writeStatement();
		case WRITELN:
			return writelnStatement();
		case IF:
			return ifStatement();
		case BREAK:
			return breakStatement();
		case WHILE:
			return whileStatement();
		case DO:
			return doWhileStatement();
		case SEMICOLON:
			nullStatement();
			break;
		case LEFTCURBRACKET:
			return compositeStatement();
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
	 * AssignExprLocalDec := Expression [ “=” Expression ] | LocalDec
	 */
	private Statement assignExprLocalDec() {
		if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
				|| lexer.token == Symbol.STRING ||
				(lexer.token == Symbol.IDENT && isType(lexer.getStringValue()) && 
				lexer.nextSymbol() != Symbol.ASSIGN && lexer.nextSymbol() != Symbol.DOT) ) {
			/*
			 * uma declaracao de variavel. 'lexer.token' eh o tipo da variavel
			 *
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
			 * LocalDec ::= Type IdList ``;''
			 */
			return localDec();
		}
		else {
			/*
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ]
			 */
			
			Expr exprLeft = expr();
			Expr exprRight = null;
			
			if ( lexer.token == Symbol.ASSIGN ) {
				lexer.nextToken();
				
				exprRight = expr();
				
				if(exprRight.getType() == Type.voidType)
					signalError.showError("Void cannot be assigned");
				
				if(exprLeft.getType().isClassType() && exprRight.getType().isClassType()) {
					if(!exprLeft.getType().isCompatible(exprRight.getType()))
						signalError.showError("Wrong type error");
				}
				
				if(!exprLeft.getType().isCompatible(exprRight.getType()))
					if(!exprLeft.getType().isClassType() && exprRight.getType() == Type.undefinedType)
						signalError.showError("Wrong type error");
				
				if ( lexer.token != Symbol.SEMICOLON )
					signalError.showError("';' expected", true);
				else
					lexer.nextToken();
			}
			
			return new AssignExpr(exprLeft, exprRight);
		}
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
		
		this.isInLoop.push(1);
		
		Statement s = statement();
		
		this.isInLoop.pop();
		
		return new WhileStatement(e, s);
	}

	// DoWhileStat := “do” CompStatement “while” “(” Expression “)”
	private DoWhileStatement doWhileStatement() {
				
		lexer.nextToken();
		
		this.isInLoop.push(1);
		
		CompositeStatement compStatement = compositeStatement();
		
		this.isInLoop.pop();
		
		if(lexer.token != Symbol.WHILE)
			signalError.showError("'while' expected");
		lexer.nextToken();
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		
		Expr e = expr();
		
		if(e.getType() != Type.booleanType) {
			signalError.showError("boolean expression expected.");
		}
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		
		return new DoWhileStatement(compStatement, e);
	}
	
	// IfStat := “if” “(” Expression “)” Statement [ “else” Statement ]
	private IfStatement ifStatement() {

		Statement ifStmt = null;
		Statement elseStmt = null;
		
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		
		Expr e = expr();
		if(e.getType() != Type.booleanType) {
			signalError.showError("boolean expression expected.");
		}
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		ifStmt = statement();
		if ( lexer.token == Symbol.ELSE ) {
			lexer.nextToken();
			elseStmt = statement();
		}
		
		return new IfStatement(e, ifStmt, elseStmt);
	}

	// ReturnStat := “return” Expression
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

	// ReadStat := “read” “(” LeftValue { “,” LeftValue } “)”
	private ReadStatement readStatement() {
		/*
		 * ReadStat := “read” “(” LeftValue { “,” LeftValue } “)”
		 * LeftValue := [ (“this” | Id ) “.” ] Id
		 * Id := Letter { Letter | Digit | “ ” }
		 * 
		 * "variables (local, parameter, static or instance)
		 *  of the type int ou String"
		 * 
		 * */
		
		ArrayList<Variable> idList = new ArrayList<>();
		boolean isInstanceVariable = false;
		
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		
		while (true) {
			if ( lexer.token == Symbol.THIS ) {
				lexer.nextToken();
				if ( lexer.token != Symbol.DOT ) signalError.showError(". expected");
				lexer.nextToken();
				isInstanceVariable = true;
			}
			if ( lexer.token != Symbol.IDENT )
				signalError.show(ErrorSignaller.ident_expected);

			String name = lexer.getStringValue();
			
			if(isInstanceVariable) {
				InstanceVariable instVar = this.currentClass.searchInstanceVariable(name);
				
				if(instVar == null) {
					signalError.showError("Instance variable '"+ name +"' has not been declared");
				}
				
				if(instVar.getType() == Type.booleanType) {
					signalError.showError("Read statement does not accept boolean variables");
				}
				
				idList.add(instVar);
			}
			else {
				Variable var = this.symbolTable.getInLocal(name);
				
				if(var == null) {
					signalError.showError("Variable '"+ name +"' has not been declared");
				}
				
				if(var.getType() == Type.booleanType) {
					signalError.showError("Read statement does not accept boolean variables");
				}
							
				idList.add(var);
			}
			
			lexer.nextToken();
			if ( lexer.token == Symbol.COMMA )
				lexer.nextToken();
			else
				break;
			
			isInstanceVariable = false;
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
		
		if(idList.size() < 1) {
			signalError.showError("Read statement must have at least one parameter");
		}
		
		return new ReadStatement(idList);
	}

	// WriteStat := “write” “(” ExpressionList “)”
	private WriteStatement writeStatement() {
		/*
		 *  ExpressionList := Expression { “,” Expression }
		 *  Expression := SimpleExpression [ Relation SimpleExpression ]
		 *  SimpleExpression := Term { LowOperator Term }
		 * 
		 * */

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		
		ExprList exprList = exprList();
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
		
		if(exprList.getSize() < 1) {
			signalError.showError("Write statement must have at least one parameter");
		}
		
		if(exprList.hasBoolean()) {
			signalError.showError("Boolean expressions cannot be parameters to write");
		}
		
		return new WriteStatement(exprList);
	}

	// WriteStat := “writeln” “(” ExpressionList “)”
	private WritelnStatement writelnStatement() {
		/*
		 *  ExpressionList := Expression { “,” Expression }
		 *  Expression := SimpleExpression [ Relation SimpleExpression ]
		 *  SimpleExpression := Term { LowOperator Term }
		 * 
		 * */

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		
		ExprList exprList = exprList();
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
		
		if(exprList.getSize() < 1) {
			signalError.showError("Write statement must have at least one parameter");
		}
		
		if(exprList.hasBoolean()) {
			signalError.showError("Boolean expressions cannot be parameters to write");
		}
		
		return new WritelnStatement(exprList);
	}

	// "break" ";"
	private BreakStatement breakStatement() {
		
		if(this.isInLoop.isEmpty()) {
			signalError.showError("'break' statement found outside a 'while' statement");
		}
		
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
		
		return new BreakStatement();
	}

	private void nullStatement() {
		lexer.nextToken();
	}

	// ExpressionList := Expression { “,” Expression }
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

	// Expression := SimpleExpression [ Relation SimpleExpression ]
	private Expr expr() {
		/*
		 *  SimpleExpression := Term { LowOperator Term }
		 *  Relation := “==” | “<” | “>” | “<=” | “>=” | “! =”
		 *  
		 * */

		Expr left = simpleExpr();
		Symbol op = lexer.token;
		
		if ( op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE
				|| op == Symbol.LT || op == Symbol.GE || op == Symbol.GT ) {
			lexer.nextToken();
			Expr right = simpleExpr();
			
			// The comparison operators <, <=, >, >= can only be applied to int values
			if((op == Symbol.LE || op == Symbol.LT || op == Symbol.GE 
					|| op == Symbol.GT) && left.getType() != Type.intType)
				signalError.showError("Comparison operators can only be applied to int values");
			
			Type l = left.getType();
			Type r = right.getType();
			
			if(l != r)
				if(l == Type.booleanType || l == Type.intType || l == Type.stringType)
					if(r == Type.booleanType || r == Type.intType ||  r == Type.stringType)
						if(!(l == Type.stringType && r == Type.undefinedType))
							signalError.showError("Incompatible types cannot be compared");
			
			if(!l.isCompatible(r) && op == Symbol.NEQ)
				if(!(l == Type.stringType && r == Type.undefinedType))
					if(!l.isClassType() && r == Type.undefinedType)
						signalError.showError("Incompatible types cannot be compared");
			
			if(!l.isCompatible(r) && (op == Symbol.NEQ ||op == Symbol.EQ))
				signalError.showError("Incompatible types cannot be compared");
				
			left = new CompositeExpr(left, op, right);
		}
		
		return left;
	}

	// SimpleExpression := Term { LowOperator Term }
	private Expr simpleExpr() {
		/*
		 * Term := SignalFactor { HighOperator SignalFactor }
		 * LowOperator := “+” | “−” | “||”
		 * 
		 * */
		Symbol op;

		Expr left = term();
		while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
				|| op == Symbol.OR) {
			lexer.nextToken();
			Expr right = term();
			
			if((left.getType() == Type.booleanType) && op != Symbol.OR)
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

	// Term := SignalFactor { HighOperator SignalFactor }
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
		KraClass superClass;

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
			return new NewObjectExpr(aClass);
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
			 * para fazer as conferencias semanticas, procure por 'messageName'
			 * na superclasse/superclasse da superclasse etc
			 */

			superClass = this.currentClass.getSuperclass();
			MethodDec aSuperMethod = null;
			
			if(superClass == null) {
				this.signalError.showError("There is no superclass of " + this.currentClass.getName());
			}
			
			do {
				aSuperMethod = superClass.searchMethod(messageName);
				if(aSuperMethod != null)
					break;
				
				superClass = superClass.getSuperclass();
			} while(superClass != null);
			
			if(aSuperMethod == null) {
				this.signalError.showError("Method '" + messageName + "' does not exist in any superclass");								
			}
			
			lexer.nextToken();
			exprList = realParameters();
			
			return new PrimaryExpr("super", aSuperMethod, exprList);
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
						MethodDec aMethod = this.currentClass.searchMethod(messageName);
						
						if(v3 == null) {
							this.signalError.showError("Variable '" + messageName + "' does not exist in class '"+cvar.getName()+"'");								
						}
						
						return new PrimaryExpr(avar, var2, aMethod);

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
							superClass = classVar.getSuperclass();
							
							while(superClass != null) {
								amethod = superClass.searchPublicMethod(id);
								
								if(amethod != null)
									break;
								else
									superClass = superClass.getSuperclass();
							}
						}
						
						if(amethod == null) {
							this.signalError.showError("Method '" + id + "' is not a public method of '" + 
									classVar.getName() + "' which is the type of '" + firstId + "'");								
						}
						
						exprList = this.realParameters();
						
						return new PrimaryExpr(avar, amethod, exprList);
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
				// confira se nao estamos em um metodo estatico
				return new PrimaryExpr("this", this.currentClass);
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
					
					if(amethod == null)
						amethod = currentClass.searchPrivateMethod(id);
					
					if(amethod == null) {
						superClass = this.currentClass.getSuperclass();
						
						while(superClass != null) {
							amethod = superClass.searchPublicMethod(id);
							
							if(amethod != null)
								break;
							else
								superClass = superClass.getSuperclass();
						}
					}
					
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
					
					InstanceVariable thisClass = currentClass.searchInstanceVariable(id);
					
					if(thisClass == null) {
						this.signalError.showError("Variable '" + id + "' does not exist in the current class");								
					}
					
					Variable thisId = this.currentClass.searchInstanceVariable(id);
					KraClass instanceVar = (KraClass ) thisId.getType();

					lexer.nextToken();
					
					MethodDec amethod = instanceVar.searchPublicMethod(lexer.getStringValue());
					if(amethod == null) {
						superClass = this.currentClass.getSuperclass();
						
						while(superClass != null) {
							amethod = superClass.searchPublicMethod(id);
							
							if(amethod != null)
								break;
							else
								superClass = superClass.getSuperclass();
						}
					}
					if(amethod == null) {
						this.signalError.showError("Method '" + lexer.getStringValue() + "' is not a public method of '" + 
								currentClass.getName() +"'");								
					}
					exprList = this.realParameters();
					
					return new PrimaryExpr("this", thisId, amethod, exprList);
				}
				else {
					// retorne o objeto da ASA que representa "this" "." Id
					/*
					 * confira se a classe corrente realmente possui uma
					 * variavel de instancia 'ident'
					 */
					InstanceVariable var = currentClass.searchInstanceVariable(id);
					
					if(var == null) {
						this.signalError.showError("Variable '" + id + "' does not exist in the current class");								
					}
					
					InstanceVariable instVar = this.currentClass.searchInstanceVariable(id);

					
					return new PrimaryExpr("this", instVar);
				}
			}
		default:
			signalError.showError("Expression expected");
		}
		return null;
	}

	private LiteralInt literalInt() {
		
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

	private SymbolTable 	symbolTable;
	private Lexer			lexer;
	private ErrorSignaller	signalError;
	private MethodDec 		currentMethod;
	private KraClass 		currentClass;
	private Stack<Integer>	isInLoop = new Stack<>();

}

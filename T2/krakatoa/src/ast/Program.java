/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

import java.util.*;
import comp.CompilationError;

public class Program {

	public Program(ArrayList<KraClass> classList, ArrayList<MetaobjectCall> metaobjectCallList, 
			       ArrayList<CompilationError> compilationErrorList) {
		this.classList = classList;
		this.metaobjectCallList = metaobjectCallList;
		this.compilationErrorList = compilationErrorList;
	}

	public void genKra(PW pw) {
		for(MetaobjectCall m : metaobjectCallList) {
			m.genKra(pw);
		}
		
		for(KraClass k : classList) {
			k.genKra(pw);
		}
	}

	public void genCpp(PW pw) {
		pw.println("#include <iostream>");
		pw.println("using namespace std;");
		pw.println("");	
		
		for(KraClass k: classList) {
			k.genCpp(pw);
		}
		
		pw.println("int main() {");
		pw.add();
		pw.printlnIdent("Program *p = new Program();");
		pw.printlnIdent("p->run();");
		pw.sub();
		pw.printIdent("}");
	}
	
	public ArrayList<KraClass> getClassList() {
		return classList;
	}


	public ArrayList<MetaobjectCall> getMetaobjectCallList() {
		return metaobjectCallList;
	}
	

	public boolean hasCompilationErrors() {
		return compilationErrorList != null && compilationErrorList.size() > 0 ;
	}

	public ArrayList<CompilationError> getCompilationErrorList() {
		return compilationErrorList;
	}

	
	private ArrayList<KraClass> classList;
	private ArrayList<MetaobjectCall> metaobjectCallList;
	
	ArrayList<CompilationError> compilationErrorList;

	
}
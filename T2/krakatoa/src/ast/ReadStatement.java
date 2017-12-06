/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

import java.util.*;

public class ReadStatement extends Statement {

	public ReadStatement(ArrayList<Variable> idList) {
		this.idList = idList;
	}
	
	@Override
	public void genCpp(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw) {
		Iterator<Variable> varList = idList.iterator();
		
		pw.printIdent("read( ");
		
		Variable var = varList.next();
		
		if(var instanceof InstanceVariable) {
			pw.print("this.");
		}
		pw.print(var.getName());
		
		while(varList.hasNext()) {
			pw.print(", ");
			var = varList.next();
			if(var instanceof InstanceVariable) {
				pw.print("this.");
			}
			pw.print(var.getName());
		}
		
		pw.println(" );");
	}

	ArrayList<Variable> idList;
}

/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

import java.util.ArrayList;
import java.util.Iterator;

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
		pw.printIdent(type.getName());
		
		Iterator<String> iList = idList.iterator();
		
		if(iList.hasNext()) {
			String id = iList.next();
			pw.print(" "+ id);
			
			while(iList.hasNext()) {
				id = iList.next();
				pw.print(", "+ id);
			}
			
			pw.println(";");
		}

	}

	private Type type;
	private ArrayList<String> idList;
}

/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

import java.util.*;

public class InstanceVariableList {

    public InstanceVariableList() {
       instanceVariableList = new ArrayList<InstanceVariable>();
    }

    public void addElement(InstanceVariable instanceVariable) {
       instanceVariableList.add( instanceVariable );
    }

    public Iterator<InstanceVariable> elements() {
    	return this.instanceVariableList.iterator();
    }

    public int getSize() {
        return instanceVariableList.size();
    }
    
    public void genCpp(PW pw) {
    	if(!instanceVariableList.isEmpty()) {
        	pw.printlnIdent("private:");
        	pw.add();
    		for(InstanceVariable instVar : instanceVariableList) {
    			instVar.genCpp(pw);
    		}
    		pw.sub();
    		
    		if(!this.instanceVariableList.isEmpty())
    			pw.println("");	
    	}
    }
    
	public void genKra(PW pw) {
		for(InstanceVariable instVar : instanceVariableList) {
			instVar.genKra(pw);
		}
		
		if(!this.instanceVariableList.isEmpty())
			pw.println("");
		
	}

    private ArrayList<InstanceVariable> instanceVariableList;
}

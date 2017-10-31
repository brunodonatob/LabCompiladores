package ast;

import java.util.*;

public class ParamList {

    public ParamList() {
       paramList = new ArrayList<Parameter>();
    }

    public void addElement(Parameter v) {
       paramList.add(v);
    }

    public Iterator<Parameter> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }
    
	public void genKra(PW pw) {
		Iterator<Parameter> pList = this.elements();
		
		if(pList.hasNext()) {
			Parameter p = pList.next();
			p.genKra(pw);
			
			while(pList.hasNext()) {
				p = pList.next();
				pw.print(", ");
				p.genKra(pw);				
			}
		}
	}
	
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

    private ArrayList<Parameter> paramList;
}

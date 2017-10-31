package ast;

import java.util.*;

public class ParamList {

    public ParamList() {
       paramList = new ArrayList<Variable>();
    }

    public void addElement(Variable v) {
       paramList.add(v);
    }

    public Iterator<Variable> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }
    
	public void genKra(PW pw) {
		// TODO Auto-generated method stub
		
	}
	
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

    private ArrayList<Variable> paramList;
}

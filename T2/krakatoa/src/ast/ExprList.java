/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

import java.util.*;

public class ExprList {

    public ExprList() {
        exprList = new ArrayList<Expr>();
    }

    public void addElement( Expr expr ) {
        exprList.add(expr);
    }
    
	public int getSize() {
		return exprList.size();
	}

	
	public ArrayList<Expr> getList(){
		return exprList;
	}
	
	public boolean hasBoolean() {
		for(Expr e : exprList) {
			if(e.getType() == Type.booleanType)
				return true;
		}
		return false;
	}
	
	public boolean hasObjects() {
		for(Expr e : exprList) {
			if(e.getType().isClassType())
				return true;
		}
		return false;
	}
	
	public Iterator<Expr> elements() {
		return this.exprList.iterator();
	}
	
	public void genKra(PW pw) {
		 int size = exprList.size();
	        for ( Expr e : exprList ) {
	        	e.genKra(pw, false);
	            if ( --size > 0 )
	                pw.print(", ");
	        }
	}

    public void genCpp( PW pw ) {

        int size = exprList.size();
        for ( Expr e : exprList ) {
        	e.genCpp(pw, false);
            if ( --size > 0 )
                pw.print(", ");
        }
    }

    private ArrayList<Expr> exprList;
}

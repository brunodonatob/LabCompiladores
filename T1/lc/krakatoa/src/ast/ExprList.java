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

	public boolean hasBoolean() {
		for(Expr e : exprList) {
			if(e.getType() == Type.booleanType)
				return true;
		}
		return false;
	}
	
	public void genKra(PW pw) {}

    public void genC( PW pw ) {

        int size = exprList.size();
        for ( Expr e : exprList ) {
        	e.genC(pw, false);
            if ( --size > 0 )
                pw.print(", ");
        }
    }

    private ArrayList<Expr> exprList;
}

package ast;


public class Parameter extends Variable {

    public Parameter( String name, Type type ) {
        super(name, type);
    }
    
    @Override
    public void genKra(PW pw) {
    	pw.print(super.getType().toString());
    	pw.print(" ");
    	pw.print(super.getName());
    }
    
    @Override
    public void genC(PW pw) {
    	
    }


}
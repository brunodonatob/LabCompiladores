package ast;

public class InstanceVariable extends Variable {

    public InstanceVariable( String name, Type type ) {
        super(name, type);
    }
    
    @Override
    public void genKra(PW pw) {
    	pw.printIdent("private "+ super.getType().toString() +" "+ super.getName()+";");
    	pw.println("");
    }

}
/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class InstanceVariable extends Variable {

    public InstanceVariable( String name, Type type ) {
        super(name, type);
    }
    
    @Override
    public void genCpp(PW pw) {
    	if(super.getType().isClassType())
    		pw.printIdent(super.getType().getCname() +" *"+ super.getName()+";");
    	else
    		pw.printIdent(super.getType().getCname() +" "+ super.getName()+";");
    	pw.println("");
    }
    
    @Override
    public void genKra(PW pw) {
    	pw.printIdent("private "+ super.getType().getName() +" "+ super.getName()+";");
    	pw.println("");
    }

}
/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;


public class Parameter extends Variable {

    public Parameter( String name, Type type ) {
        super(name, type);
    }
    
    @Override
    public void genKra(PW pw) {
    	pw.print(super.getType().getName());
    	pw.print(" ");
    	pw.print(super.getName());
    }
    
    @Override
    public void genCpp(PW pw) {
    	pw.print(super.getType().getCname());
    	if(super.getType().isClassType())
    		pw.print(" *");
    	else
    		pw.print(" ");
    	pw.print(super.getName());
    }


}
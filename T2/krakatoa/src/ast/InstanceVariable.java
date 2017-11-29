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
    	
    }
    
    @Override
    public void genKra(PW pw) {
    	pw.printIdent("private "+ super.getType().getName() +" "+ super.getName()+";");
    	pw.println("");
    }

}
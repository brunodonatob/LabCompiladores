/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;


public class MessageSendToVariable extends MessageSend { 

    public Type getType() { 
        return null;
    }
    
    public void genCpp( PW pw, boolean putParenthesis ) {
        
    }

    public void genKra(PW pw,boolean putParenthesis) {
		
	}
}    
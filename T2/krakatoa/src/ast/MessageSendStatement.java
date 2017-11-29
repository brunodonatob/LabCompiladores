/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class MessageSendStatement extends Statement { 


   public void genC( PW pw ) {
      pw.printIdent("");
      // messageSend.genC(pw);
      pw.println(";");
   }

   @Override
   public void genKra(PW pw) {
   	// TODO Auto-generated method stub
   	
   }
   @Override
   public void genCpp(PW pw) {
   	// TODO Auto-generated method stub
   	
   }
   
   
   private MessageSend  messageSend;
}



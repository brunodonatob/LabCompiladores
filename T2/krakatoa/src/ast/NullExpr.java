/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class NullExpr extends Expr {
    
   public void genCpp( PW pw, boolean putParenthesis ) {
      pw.print("NULL");
   }
   
   public void genKra(PW pw,boolean putParenthesis) {
	   pw.print("null");
	}
   
   public Type getType() {
      return Type.undefinedType;
   }
}
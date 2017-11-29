/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class TypeString extends Type {
    
    public TypeString() {
        super("String");
    }
    
   public String getCname() {
      return "String";
   }

}
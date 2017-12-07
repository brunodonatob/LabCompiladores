/* Universidade Federal de Sao Carlos
 * 
 * 	Bruno Donato Banhos
 * 	Indrid Maria Santos Pires
 * 
 * */
package ast;

public class TypeBoolean extends Type {

   public TypeBoolean() { super("boolean"); }

   @Override
   public String getCname() {
      return "bool";
   }

}

package ast;

import java.util.ArrayList;

/*
 * Krakatoa Class
 */
public class KraClass extends Type {
	
   public KraClass( String name ) {
      super(name);
      publicMethodList = new ArrayList<>();
      instanceVariableList = new InstanceVariableList();
   }
   
   public String getCname() {
      return getName();
   }
   
   public KraClass getSuperclass() {
	   return superclass;
   }

   public void setSuperclass(KraClass superclass) {
	   this.superclass = superclass;
   }
   
   public boolean isSubclassOf(Type other) {
	   KraClass current = this;
	   
	   while(current != other) {
		   current = current.getSuperclass();
		   if(current == null) {
			   return false;
		   }
	   }
	   
	   return true;
   }
   
   public void addMethod(MethodDec aMethod) {
	   publicMethodList.add(aMethod);
   }
   
   public void addInstanceVariable(InstanceVariable instanceVariable) {
	   instanceVariableList.addElement(instanceVariable);
   }
   
   public MethodDec searchPublicMethod(String methodName) {
	   
	   for(MethodDec m: this.publicMethodList) {
		   if(m.getName().equals(methodName)) {
			   return m;
		   }
	   }
	   
	   return null;
   }
   
   private String name;
   private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private ArrayList<MethodDec> publicMethodList;
   // private MethodList publicMethodList, privateMethodList;
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
}

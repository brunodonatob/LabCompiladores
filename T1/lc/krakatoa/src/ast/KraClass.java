package ast;

import java.util.ArrayList;
import java.util.Iterator;

import lexer.Symbol;

/*
 * Krakatoa Class
 */
public class KraClass extends Type {
	
   public KraClass( String name ) {
      super(name);
      publicMethodList = new ArrayList<>();
      privateMethodList = new ArrayList<>();
      instanceVariableList = new InstanceVariableList();
      this.superclass = null;
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
	   if(aMethod.getQualifier() == Symbol.PUBLIC)
		   publicMethodList.add(aMethod);
	   else if(aMethod.getQualifier() == Symbol.PRIVATE)
		   privateMethodList.add(aMethod);
   }
   
   public InstanceVariable searchInstanceVariable(String variableName) {
	   Iterator<InstanceVariable> instVarList =  this.instanceVariableList.elements();
	   InstanceVariable var;
	   
	   while(instVarList.hasNext()) {
		   var = instVarList.next();
		   
		   if(var.getName().equals(variableName)) {
			   return var;
		   }
	   }
	   
	   return null;
   }
   
   public void addInstanceVariable(InstanceVariable instanceVariable) {
	   instanceVariableList.addElement(instanceVariable);
   }
   
   public MethodDec searchMethod(String methodName) {
	   
	   for(MethodDec m: this.publicMethodList) {
		   if(m.getName().equals(methodName)) {
			   return m;
		   }
	   }
	   
	   for(MethodDec m: this.privateMethodList) {
		   if(m.getName().equals(methodName)) {
			   return m;
		   }
	   }
	   
	   return null;
   }
   
   public MethodDec searchPublicMethod(String methodName) {
	   
	   for(MethodDec m: this.publicMethodList) {
		   if(m.getName().equals(methodName)) {
			   return m;
		   }
	   }
	   
	   return null;
   }
   
   public MethodDec searchPrivateMethod(String methodName) {
	   
	   for(MethodDec m: this.privateMethodList) {
		   if(m.getName().equals(methodName)) {
			   return m;
		   }
	   }
	   
	   return null;
   }
   
   public void genKra(PW pw) {
	   pw.print("class "+ this.name);
	   
	   if(this.superclass != null) {
		   pw.print(" extends " + superclass.getName());
	   }
	   
	   pw.println(" {");
	   pw.add();
	   
	   // Imprime as variaveis de instancia
	   this.instanceVariableList.genKra(pw);
	   
	   // Imprime os metodos privados
	   for(MethodDec pvMethod : this.privateMethodList) {
		   pvMethod.genKra(pw);
	   }
	   
	   // Imprime os metodos publicos
	   for(MethodDec pbMethod : this.publicMethodList) {
		   pbMethod.genKra(pw);
	   }
	   
	   pw.sub();
	   pw.println("}");
   }
   
   private String name;
   private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private ArrayList<MethodDec> publicMethodList;
   private ArrayList<MethodDec> privateMethodList;
   // private MethodList publicMethodList, privateMethodList;
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
}

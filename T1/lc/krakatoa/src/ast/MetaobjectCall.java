/**
  
 */
package ast;

import java.util.ArrayList;
import java.util.Iterator;

/** This class represents a metaobject call as <code>{@literal @}ce(...)</code> in <br>
 * <code>
 * @ce(5, "'class' expected") <br>
 * clas Program <br>
 *     public void run() { } <br>
 * end <br>
 * </code>
 * 
   @author Jos�
   
 */
public class MetaobjectCall {

	public MetaobjectCall(String name, ArrayList<Object> paramList) {
		this.name = name;
		this.paramList = paramList;
	}
	
	public ArrayList<Object> getParamList() {
		return paramList;
	}
	public String getName() {
		return name;
	}
	
	public void genKra(PW pw) {
		pw.print("@"+ name);
		
		if(!paramList.isEmpty()) {
			pw.print("(");
			
			Iterator<Object> obj = this.paramList.iterator();
			Object o = obj.next();
			
			if(o instanceof Integer) {
				pw.print(o.toString());
			}
			else {
				pw.print("\""+o.toString()+"\"");
			}
			
			while(obj.hasNext()) {
				pw.print(", ");
				o = obj.next();
				if(o instanceof Integer) {
					pw.print(o.toString());
				}
				else {
					pw.print("\""+o.toString()+"\"");
				}
			}
			
			pw.print(")");
		}
		
		pw.println("\n");
	}


	private String name;
	private ArrayList<Object> paramList;

}

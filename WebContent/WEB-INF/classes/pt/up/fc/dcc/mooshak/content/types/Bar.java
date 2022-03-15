package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;



public class Bar extends PersistentContainer<Foo> {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute (name="ID")
	public String name = null;
	
	
	
	public String getID() {
		return name;
	}


	public void setID(String name) {
		this.name = name;
	}
	

}

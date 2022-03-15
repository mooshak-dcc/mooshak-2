package pt.up.fc.dcc.mooshak.message;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MooshakTypeFields {
	public List<MooshakField> fields = new ArrayList<>();
	
	public static class MooshakField  {
		public String name = null;
		public String type = null;
		public List<String> values = null;		
		
		public String tip = null;
		public String help = null;
		public String complement;
		public String conditionalField = null;
		public String conditionalValue = null;
		
		public	MooshakField() {}		
	}
	
	public MooshakTypeFields() {}
	
	public MooshakField newField() {
		MooshakField mooshakField = new MooshakField();
		fields.add(mooshakField);
		return mooshakField;
	}

}

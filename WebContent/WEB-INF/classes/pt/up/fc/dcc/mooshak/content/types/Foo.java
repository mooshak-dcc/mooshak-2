package pt.up.fc.dcc.mooshak.content.types;

import java.util.Map;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;


/**
 * This class is just for testing purposes
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Foo extends PersistentObject {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute (name="Name")
	private String name = null;
	
	private String other = null;
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getOther() {
		return other;
	}


	public void setOther(String other) {
		this.other = other;
	}

	@MooshakOperation (name = "echo", inputable = true)
	private CommandOutcome echo(MethodContext context) {
		StringBuilder builder = new StringBuilder();
		String sep = "";
		for(String name: context.getNames()) {
			builder.append(sep);
			builder.append(name);
			builder.append(':');
			builder.append(context.getValue(name));
			sep=",";
		}
		
		return new CommandOutcome(builder.toString());
	}
	

	@MooshakOperation (name = "who")
	private CommandOutcome who() {
		
		return new CommandOutcome(name);
	}
	
	@MooshakOperation (name = "wrongReturn")
	private String wrongReturn() {
		
		return name;
	}

	@MooshakOperation (name = "wrongParameterType1", inputable = true)
	private CommandOutcome wrongParameterType1(String parameter) {
		
		return new CommandOutcome(name);
	}

	@MooshakOperation (name = "wrongParameterType3", inputable = true)
	private CommandOutcome wrongParameterType2(Map<Integer,String> parameter) {
		
		return new CommandOutcome(name);
	}
	
	@MooshakOperation (name = "wrongParameterType3", inputable = true)
	private CommandOutcome wrongParameterType3(Map<String,Integer> parameter) {
		
		return new CommandOutcome(name);
	}
}

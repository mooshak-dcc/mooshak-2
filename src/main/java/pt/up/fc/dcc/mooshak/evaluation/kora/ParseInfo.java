/**
 * 
 */
package pt.up.fc.dcc.mooshak.evaluation.kora;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyValue;

/**
 * @author User
 *
 */
public class ParseInfo {

	/**
	 * 
	 */
	public ParseInfo() {
		
	}
	
	public  JSONObject createJsonEdgeTemporary(Edge edge,String temporaryCode){
		JSONObject edgeJson = new JSONObject();
		String types[] = edge.getType().split(" ");
		
//		try {
//			edgeJson.put("id", 0);//Integer.parseInt(edge.getId())
//		} catch (Exception e) {
//			edgeJson.put("id", -1);
//		}
		
		if(temporaryCode.equals("insert"))
			edgeJson.put("id", 0);
		else
			edgeJson.put("id", Integer.parseInt(edge.getId()));
		
		
		edgeJson.put("type", types[0]);
		
		
		if(edge.getSource()==null  )
			edgeJson.put("source",-1);
		else
			edgeJson.put("source", 
				Integer.parseInt(edge.getSource().getId()));
		
		if(edge.getTarget()==(null) )
			edgeJson.put("target",-1);
		else
			edgeJson.put("target", 
				Integer.parseInt(edge.getTarget().getId()));
		
		JSONArray properties = new JSONArray();
		for (int j = 1; j < types.length; j++) {
			JSONObject property = new JSONObject();
			property.put("name", types[j]);
			property.put("value", true);
			properties.put(property);
		}
		edgeJson.put("temporary", temporaryCode);
		edgeJson.put("features", properties);
		return edgeJson;
	}
	
	
	public JSONObject createJsonNodeTemporary(Node node, String temporary)  {
		JSONObject nodeJson = new JSONObject();
		String types[] = node.getType().split(" ");
		
		if(temporary.equals("insert"))
			nodeJson.put("id", 0);
		else{
			nodeJson.put("id", Integer.parseInt(node.getId()));
		}
//		nodeJson.put("id", 0);
		
		nodeJson.put("type", types[0]);
		nodeJson.put("label", node.getName());
		JSONArray properties = new JSONArray();
		Map<PropertyName, PropertyValue> mapProperties = node.getProperties();
		for (PropertyName propertyname : mapProperties.keySet()) {
			if (propertyname.isSimple()) {
				String name = ((SimplePropertyName) propertyname).getName();
				String value = ((SimplePropertyValue) mapProperties.get(propertyname)).getValue();
				JSONObject property = new JSONObject();

				// property.put(name, value);
				property.put("name", name);
				if ("true".equals(value))
					property.put("value", true);
				else if ("false".equals(value))
					property.put("value", false);
				else
					property.put("value", value);

				properties.put(property);
			}
		}

		for (int i = 1; i < types.length; i++) {
			if (!types[i].equals("-") && !types[i].equalsIgnoreCase("composite")) {
				JSONObject property = new JSONObject();
				property.put("name", types[i]);
				property.put("value", true);

				properties.put(property);
			}
		}

		nodeJson.put("temporary", temporary);
		nodeJson.put("features", properties);
		return nodeJson;
	}

}

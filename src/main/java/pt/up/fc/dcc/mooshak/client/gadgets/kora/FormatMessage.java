/**
 * 
 */
package pt.up.fc.dcc.mooshak.client.gadgets.kora;

import com.google.gwt.core.client.GWT;

import pt.up.fc.dcc.mooshak.client.guis.kora.i18n.KoraMessages;

/**
 * @author hcorreia
 *
 */
public class FormatMessage {

	/**
	 * to convert messages to text
	 */
	private KoraMessages messages = GWT.create(KoraMessages.class);
	
	public FormatMessage() {
		
	}
	
	public String getMessage(String textCode){
		
		String[] multilines= textCode.split("@");
		if(multilines.length>0)
			return processMultilines(multilines);
		
		return processMessage(textCode);
	}
	
public String processMessage(String textCode){
		
		char code=textCode.charAt(0);
		switch (code) {
		case '0':
			return errorSyntaxe(textCode);
		case '1':
			return missingElement(textCode);
		case '2':
			return deleteElement(textCode);
		case '3':
			return differentElement(textCode);
		case '4':
			return property(textCode);
		case '5':
			return incompleteEvaluation(textCode);
			

		default:
			break;
		}
		return textCode;
	}
	
	


	private String missingElement(String string){
		
		String[] result = string.split(";");
		
		switch (result[1]) {
		case "insertNode":
			return messages.insertNode(result[2],result[3]);
		case "insertNode1":
			return messages.insertNode1(result[2],result[3],result[4]);
		case "insertNode2":
			return messages.insertNode2(result[2],result[3]);
		case "insertNode3":
			return messages.insertNode3(result[2],result[3],result[4]);
		case "insertNode4":
			return messages.insertNode4(result[2],result[3],result[4],result[5]);
		case "insertEdge":
			return messages.insertEdge(result[2]);
		case "insertEdge1":
			return messages.insertEdge1(result[2],result[3],result[4]);
		case "insertEdge2":
			return messages.insertEdge2(result[2],result[3],result[4]);
		case "insertEdge3":
			return messages.insertEdge3(result[2],result[3],result[4],result[5],result[6]);
		case "insertEdge4":
			return messages.insertEdge4(result[2],result[3],result[4],result[5],result[6],result[7]);
		case "includeNode":
			return messages.includeNode(result[2],result[3]);
			

		default:
			return string;
		}
		
	}
	
	private String deleteElement(String textCode) {
		String[] result = textCode.split(";");
		
		switch (result[1]) {
		case "deleteNode":
			return messages.deleteNode(result[2]);
		case "deleteNode1":
			return messages.deleteNode1(result[2],result[3],result[4]);
		case "deleteNode2":
			return messages.deleteNode2(result[2],result[3],result[4]);
		case "deleteNode3":
			return messages.deleteNode3(result[2],result[3],result[4]);
		case "deleteNode4":
			return messages.deleteNode4(result[2],result[3],result[4],result[5]);
		case "deleteEdge":
			return messages.deleteEdge(result[2]);
		case "deleteEdge1":
			return messages.deleteEdge1(result[2],result[3]);
		case "deleteEdge2":
			return messages.deleteEdge2(result[2],result[3],result[4]);
		case "deleteEdge3":
			return messages.deleteEdge3(result[2],result[3],result[4],result[2],result[3],result[4]);
		case "deleteEdge4":
			return messages.deleteEdge4(result[2]);
		case "includeDeletion":
			return messages.includeDeletion(result[2],result[3]);
		default:
			return textCode;
		}
	}
	
	
	private String differentElement(String textCode) {
		String[] result = textCode.split(";");
		
		switch (result[1]) {
		case "differentType1":
			return messages.differentType1(result[2]);
		case "differentType2":
			return messages.differentType2(result[2]);
		case "differentNodeType":
			return messages.differentNodeType(result[2],result[3]);
		case "differentNodeType1":
			return messages.differentNodeType1(result[2],result[3],result[4],result[5]);
		case "differentEdgeType":
			return messages.differentEdgeType(result[2],result[3],result[4]);
		case "differentEdgeType1":
			return messages.differentEdgeType1(result[2],result[3],result[4],result[5],result[6]);
		default:
			return textCode;
		}
	}
	
	private String property(String textCode) {
		String[] result = textCode.split(";");
		
		switch (result[1]) {
		case "wrongCardinality":
			return messages.wrongCardinality(result[2]);
		case "wrongCardinality1":
			return messages.wrongCardinality1(result[2],result[3],result[4]);
		case "wrongCardinality2":
			return messages.wrongCardinality2(result[2],result[3],result[4],result[5],result[6]);
		case "wrongCardinality3":
			return messages.wrongCardinality3(result[2],result[3],result[4],result[5],result[6],result[7],result[8]);
		case "deleteCardinality":
			return messages.deleteCardinality(result[2],result[3],result[4],result[5]);
		case "deleteCardinality1":
			return messages.deleteCardinality1(result[2],result[3],result[4],result[5],result[6]);
		case "insertCardinality":
			return messages.insertCardinality(result[2],result[3],result[4],result[5]);
		case "insertCardinality1":
			return messages.insertCardinality1(result[2],result[3],result[4],result[5],result[6],result[7]);
		case "modifyProperty":
			return messages.modifyProperty(result[2]);
		case "modifyProperty1":
			return messages.modifyProperty1(result[2],result[3],result[4]);
		case "modifyProperty2":
			return messages.modifyProperty2(result[2],result[3]);
		case "modifyProperty3":
			return messages.modifyProperty3(result[2],result[3],result[4],result[5]);
		case "modifyProperty4":
			return messages.modifyProperty4(result[2],result[3],result[4],result[5],result[6]);
		case "modifyProperty5":
			return messages.modifyProperty5(result[2],result[3],result[4],result[5],result[6],result[7]);
		case "modifyProperty6":
			return messages.modifyProperty6(result[2],result[3],result[4],result[5],result[6],result[7],result[8],result[9]);	
		case "insertProperty":
			return messages.insertProperty(result[2],result[3],result[4]);
		case "deleteProperty":
			return messages.deleteProperty(result[2],result[3],result[4]);
			
			
		default:
			return textCode;
		}
}
	
	
	private String errorSyntaxe(String string){
		if(string==null) return "";
	
		String[] result = string.split(";");
		
		switch (result[1]) {
		case "nodeDisconnected":
			return messages.nodeDisconnected(result[2],result[3]);
		case "invalidDegree":
			return messages.invalidDegree(result[2],result[3],result[4]);
		case "invalidConnection":
			return messages.invalidConnection(result[2],result[3],result[4],result[5],result[6]);
		case "invalidTypeConnection":
			return messages.invalidTypeConnection(result[2],result[3]);
		case "invalidExtension":
			return messages.invalidExtension(result[2],result[3]);
		case "invalidContainer":
			return messages.invalidContainer(result[2],result[3],result[4],result[5]);
		case "invalidNumberComponent":
			return  messages.invalidNumberComponent(result[2]);

		default:
			return string;
		}
		
	}


	private String incompleteEvaluation(String string) {
		if(string==null) return "";
		
		String[] result = string.split(";");
		
		switch (result[1]) {
		case "addNode":
			return messages.addNode(result[2]);
		case "addEdge":
			return messages.addEdge(result[2]);
		case "removeNode":
			return messages.removeNode(result[2]);
		case "removeEdge":
			return messages.removeEdge(result[2]);
		case "nodeType":
			return messages.nodeType(result[2],result[3],result[4]);
		case "edgeType":
			return messages.edgeType(result[2],result[3],result[4]);

		default:
			return string;
		}
		
	}

	private String processMultilines(String[] multilines){
		String text="";
		for(String s : multilines){
			if(s!=null && !s.isEmpty())
				text +=processMessage(s) +"\n";
		}
		return text;
	}

	public KoraMessages getMessages() {
		return messages;
	}

	public void setMessages(KoraMessages messages) {
		this.messages = messages;
	}
	
	public String getMarkMessage(String mark){
		return messages.marktext(mark);
	}

}

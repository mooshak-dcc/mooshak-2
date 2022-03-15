/**
* Define the parse for the configuration file. 
* @method  XMLParser
* @param {string} xmlFile - quiz configuration file xml
* @return null;
*/
function XMLParser(xmlFile){
	this.xmlDoc = new DOMParser().parseFromString(xmlFile,"text/xml");
	console.log(this.xmlDoc)
} 

XMLParser.prototype = {
		
		
		getXML : function() {
			return this.xmlDoc;
		},
		
		getGroups:function() {
			return this.xmlDoc.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","group");
		},
		
		
		getQA:function(group) {
			return this.xmlDoc.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","QA");
		},
		
		getConfig:function() {
			return this.xmlDoc.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","config")[0];
		},
		
		 
		
		
		
}



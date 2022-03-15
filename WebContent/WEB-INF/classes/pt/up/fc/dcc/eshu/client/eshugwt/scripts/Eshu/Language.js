
function Language(name,nodes,edges){
	this.name=name;
	this.nodes=nodes;
	this.edges=edges;

} 

Language.prototype = {

	getName : function() {
		return this.name;
	},

	getNodes : function() {
		return this.nodes;
	},

	getEdges : function() {
		return this.edges
	},

	getNodesEdges : function() {
		var aux = new Array();
		aux = this.nodes;
		
		for (var i = 0; i < this.edges.length; i++) {
			
			aux.push(this.edges[i]);
		}
		return aux;
	},
}


function Command(){
	
	this.element;
} 

Command.prototype = {

	getElement : function() {
		return this.element;
	},

	setElement : function(element) {
		this.element=element;
	},
	
	execute : function(element) {
		throw new Error('not implemented');
	},
	
	undo : function() {
		throw new Error('not implemented');
	},
	
	redo : function() {
		throw new Error('not implemented');
	},
	
}
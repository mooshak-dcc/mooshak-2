
function CommandStack(limit){
	this.listUndo=[];
	this.listRedo=[];
	this.limit=limit;
}

CommandStack.prototype = {
		
	push  : function(value) {
		if(this.listUndo.length==this.limit){
			this.listUndo.shift();
		}
		this.listUndo.push(value);
		},
		
	popUndo  : function() {
			var element= this.listUndo.pop();
			if(this.listRedo.length==this.limit){
				this.listRedo.shift();
			}
			this.listRedo.push(element);
			return element;
		},
		
	popRedo  : function() {
		var element= this.listRedo.pop();
		this.listUndo.push(element);
		return element;
		},
	
	isEmptyListUndo: function(){
		return this.listUndo.length==0;
	},
		
	isEmptyListRedo: function(){
			return this.listRedo.length==0;
	}	
}

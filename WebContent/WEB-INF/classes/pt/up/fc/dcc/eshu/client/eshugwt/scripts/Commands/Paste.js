
function Paste(){
	Command.call(this);
	this.type;
}

Paste.prototype = {
		execute : function(graph, generateID) {
			
			if(graph.structCopy.type==1){
				this.element=graph.pasteNodeSelected(generateID);
				this.type=1;
				return 1;
			}
			
			this.element=graph.pasteRectangleSelection(generateID).copy();
			this.type=2;
			return graph.rectangleSelection.length();
			
			
		},
		
		undo  : function(graph) {
			if(this.type==1){
				graph.deleteNode(this.element);
			}
			else 
			{
				var nodes=this.element.getListOfSelected();
				graph.deleteArrayNodes(nodes);
				graph.unselectNodesRectangleSelection();
			}
			return 0;
		},
		
		redo : function (graph){
			
			if(this.type==1){
				graph.addNode(this.element);
				graph.select(this.element);
				return 0;
			}
			else{
				var nodes=this.element.getListOfSelected();
				for(var i in nodes){
					nodes[i].selected();
					graph.addNode(nodes[i]);
				}
				graph.rectangleSelection=this.element.copy();
				return 3;
			}
		}
}



function Remove(){ // delete is a word reserved
	Command.call(this);
	this.type;
}

Remove.prototype = {

		execute : function(graph) {
			if(graph.isNodeSelection()){
				this.type=1;
				this.element=graph.selection;
			}
			else if(graph.isEdgeSelection()) {
				this.type=2;
				this.element=graph.selection;
			}
			else if(graph.isSelectionRectangleSelection()) {
				this.type=3;
				this.element=graph.rectangleSelection.copy()//new RectangleSelection();
			}
			graph.deleteElment();
			
		},
		
		undo  : function(graph) {
			if(this.type==1){
				graph.addNode(this.element);
				graph.selectNode(this.element);
				return 0;
			}
			else if(this.type==2){
				graph.addEdge(this.element);
				this.element.source.setEdgesConnected(this.element);
				this.element.target.setEdgesConnected(this.element);
				graph.selectEdge(this.element);
				return 5; 
			}
			else if(this.type==3){
				var nodes = this.element.getListOfSelected();
				for(var i in nodes){
					nodes[i].selected();
					graph.addNode(nodes[i]);
				}
				graph.rectangleSelection=this.element.copy();
				return 3;
			}
		},
		
		redo : function (graph){
			
			if(this.type==1){
				graph.deleteNode(this.element);
			}
			else if(this.type==2) {
				graph.deleteEdge(this.element);
			}
			else if(this.type==3) {
				var nodes=this.element.getListOfSelected()
				graph.deleteArrayNodes(nodes);
			}
		}
}


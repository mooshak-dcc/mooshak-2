

function Resize(){ // delete is a word reserved
	Command.call(this);
	this.type;
	this.handlerPreviousCoordinate;
	this.handlerNewCoordinate;
	this.handler;
	
	this.previousNode={};
	
}

Resize.prototype = {

		execute : function(graph,args) {
			
			if(graph.isNodeSelection()){
				this.type=1;
				this.element=graph.selection;
				this.previousNode.x=args.x;
				this.previousNode.y=args.y;
				this.previousNode.width=args.width;
				this.previousNode.height=args.height;
				graph.nodes.remove(this.element);
				graph.nodes.insert(this.element);
				
			}
			else if(graph.isEdgeSelection()) {
				this.type=2;
				this.element=graph.selection;
				this.handler=graph.selection.handlerSelected;
				this.handlerNewCoordinate={x:graph.selection.handlerSelected.x,y:graph.selection.handlerSelected.y};
				this.handlerPreviousCoordinate={x:args.x,y:args.y};
			}
			
		},
		
		undo  : function(graph) {
			if(this.type==1){
				var previous={x:this.element.x,y:this.element.y,width:this.element.width,height:this.element.height}
				this.element.x=this.previousNode.x;
				this.element.y=this.previousNode.y;
				this.element.width =this.previousNode.width;
				this.element.height =this.previousNode.height;
				graph.nodes.remove(this.element);
				graph.nodes.insert(this.element);
				graph.unselectGraph();
				graph.select(this.element);
				this.previousNode=previous;
				
				
				return 5;
			}
			else if(this.type==2){
				this.handler.x=this.handlerPreviousCoordinate.x;
				this.handler.y=this.handlerPreviousCoordinate.y;
				this.element.updateBorderSize();
				graph.nodes.remove(this.element);
				graph.nodes.insert(this.element);
				graph.unselectGraph();
				graph.select(this.element);
				return 5; 
			}
			
			
		},
		
		redo : function (graph){
			
			if(this.type==1){
				var previous={x:this.element.x,y:this.element.y,width:this.element.width,height:this.element.height}
				previous=JSON.parse(JSON.stringify(previous));
				this.element.x=this.previousNode.x;
				this.element.y=this.previousNode.y;
				this.element.width =this.previousNode.width;
				this.element.height =this.previousNode.height;
				graph.unselectGraph();
				graph.select(this.element);
				this.previousNode=previous;

			}
			else if(this.type==2) {
				this.handler.x=this.handlerNewCoordinate.x;
				this.handler.y=this.handlerNewCoordinate.y;
				this.element.updateBorderSize();
				graph.unselectGraph();
				graph.select(this.element);
				return 5; 
			}
			
		}
}





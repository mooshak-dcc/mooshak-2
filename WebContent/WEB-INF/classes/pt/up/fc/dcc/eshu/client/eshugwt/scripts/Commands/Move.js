

function Move(){ 
	Command.call(this);
	this.type;
	this.previousCoordinate;
	this.newCoordinate;
	this.nodesPosition=[];
	
}

Move.prototype = {

		execute : function(graph,args) {
			if(graph.isNodeSelection()){
				this.executeNode(graph,args);
			}
			else if(graph.isSelectionRectangleSelection()) {
				this.executeSelectionRectangleSelection(graph,args);
			}
		},
		
		undo  : function(graph) {
			if(this.type==1){
				this.undoNode(graph);
				return 0;
			}
			else if(this.type==2){
				this.undoRectangleSelection(graph);
				return 3; 
			}
		},
		
		redo : function (graph){
			
			if(this.type==1){
				this.redoNode(graph);
				return 0;
			}
			else if(this.type==2) {
				this.redoRectangleSelection(graph);
				return 3; 
			}
			
			
		},
		
		executeSelectionRectangleSelection:function(graph,args){
			this.type=2;
			this.element=graph.rectangleSelection.copy();
			this.newCoordinate={x:graph.rectangleSelection.getXMin(),y:graph.rectangleSelection.getYMin()};
			this.previousCoordinate={x:args.x,y:args.y};
		
			var nodes=graph.rectangleSelection.getListOfSelected();
			for(var i in nodes){
				graph.nodeUpdateEdgesConnected(nodes[i]);
				this.nodesPosition[i]={x:nodes[i].x-this.newCoordinate.x, 
									   y:nodes[i].y-this.newCoordinate.y};
			}
	},
	
		executeNode:function (graph,args){
			this.type=1;
			this.element=graph.selection;
			this.newCoordinate={x:this.element.x,y:this.element.y};
			this.previousCoordinate={x:args.x,y:args.y};
			graph.nodeUpdateEdgesConnected(this.element);
		},
		
		
		undoNode  : function(graph) {
				this.element.x=this.previousCoordinate.x;
				this.element.y=this.previousCoordinate.y;
				//this.element.updatePositionEdgesConnected();
				if(graph.isSelectionRectangleSelection())
					graph.unselectNodesRectangleSelection();
				graph.selectNode(this.element);
		},
		
		undoRectangleSelection  : function(graph) {
				graph.unselectGraph();
				this.element.updatePosition(this.previousCoordinate.x,this.previousCoordinate.y);
				this.updatePositionNodes(graph,this.previousCoordinate.x,this.previousCoordinate.y);
				graph.rectangleSelection=this.element.copy();				
		},
		
		redoNode : function (graph){
					
				this.element.update(this.newCoordinate.x,this.newCoordinate.y);
				graph.nodeUpdateEdgesConnected(this.element);
				if(graph.isSelectionRectangleSelection())
					graph.unselectNodesRectangleSelection();
				graph.selectNode(this.element);
					
		},
		
		redoRectangleSelection : function (graph){
					
					this.element.updatePosition(this.newCoordinate.x,this.newCoordinate.y);
					this.updatePositionNodes(graph,this.newCoordinate.x,this.newCoordinate.y);
					graph.rectangleSelection=this.element.copy();
		},
		
		updatePositionNodes: function(graph,x,y){
			var nodes = this.element.getListOfSelected();
			for(var i in nodes){
				var node=nodes[i];
				graph.nodes.remove(node);
				node.selected();
				node.x=x+this.nodesPosition[i].x;
				node.y=y+this.nodesPosition[i].y;
				node.updatePositionEdgesConnected();
				graph.addNode(node);
			}
		},
}



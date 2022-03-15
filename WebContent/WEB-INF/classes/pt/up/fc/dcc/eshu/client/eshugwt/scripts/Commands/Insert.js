
function Insert(){
	Command.call(this);
	this.isNode;
}

Insert.prototype = {

		execute : function(graph,args,ctx) {
			if(args.isNode){
				var node= graph.createNode(args.nodeType, Math.floor(currentCoordinate.x), Math.floor(currentCoordinate.y), args.id);
				this.element=node;
				graph.addNode(this.element);
				graph.selectNode(this.element);
			}
			else{
				
				var edge= graph.createEdge(args.nodeType, args.id, args.source, args.target, args.pointsEdge);
				graph.addEdge(edge);
				graph.selectEdge(edge);
				this.element=edge;
			}
			this.isNode=args.isNode;
			
		},
		
		undo  : function(graph) {
			if(this.isNode)
				graph.deleteNode(this.element);
			else 
				graph.deleteEdge(this.element);
			graph.unselectNodesRectangleSelection();
			return 0;
		},
		
		redo : function (graph){
			
			if(this.isNode){
				graph.addNode(this.element);
				graph.select(this.element);
				return 0;
			}
			else{
				var sourceExist=graph.insideNode(this.element.source.x,this.element.source.y);
				var targetExist=graph.insideNode(this.element.target.x,this.element.target.y);
				if(sourceExist && targetExist)
				{	
//					this.element.source.setEdgesConnected(this.element);
//					this.element.target.setEdgesConnected(this.element);
					graph.addEdge(this.element);
					graph.select(this.element);
				}
				return 5; 
			}
		}
		
}

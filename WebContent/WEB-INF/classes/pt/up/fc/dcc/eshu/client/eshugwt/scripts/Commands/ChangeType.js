
function ChangeType(){ // delete is a word reserved
	Command.call(this);
	this.type;
	this.pastElement;
	this.ctx;
}

ChangeType.prototype = {

		execute : function(graph,args,ctx) {
			this.ctx=ctx;
			if(graph.isNodeSelection()){
				this.type=1;
				this.pastElement=graph.selection;
//				graph.deleteElment();
				this.pastElement.unSelected();
				graph.nodes.remove(this.pastElement);
				currentCoordinate.x=this.pastElement.x+this.pastElement.width/2;
				currentCoordinate.y=this.pastElement.y+this.pastElement.height/2;
				
				this.element= graph.createNode(args.nodeType, Math.floor(currentCoordinate.x), Math.floor(currentCoordinate.y), args.id);
				this.element.importNodeVariant(this.pastElement);
				this.element.adjustLabel(ctx);
				graph.addNode(this.element);
				graph.selectNode(this.element);
				var edges =graph.edges.getAll();
				for(var i in edges){
					if(edges[i].source.id==this.element.id ){
						var edge= graph.createEdge(edges[i].edgeType, edges[i].id, this.element, edges[i].target, edges[i].listHandler);
						graph.deleteEdge(edges[i]);
						graph.addEdge(edge);
						
					}
					
					if(edges[i].target.id==this.element.id){
						var edge= graph.createEdge(edges[i].edgeType, edges[i].id, edges[i].source,this.element, edges[i].listHandler);
						graph.deleteEdge(edges[i]);
						graph.addEdge(edge);
					}
					
				}
			}
			else if(graph.isEdgeSelection()) {
				this.type=2;
				var cardinality={cs:"",ct:""};
				this.pastElement=graph.selection;
				if(this.pastElement.isCardinality){
					 cardinality={cs:this.pastElement.cardinalitySource,ct:this.pastElement.cardinalityTarget};
				}
				graph.deleteElment();
				var edge= graph.createEdge(args.nodeType, args.id, args.source, args.target, args.pointsEdge);
				if(edge.isCardinality){
						edge.cardinalitySource=cardinality.cs;
						edge.cardinalityTarget=cardinality.ct;
				}
				graph.addEdge(edge);
				graph.selectEdge(edge);
				this.element=edge;
				
			}
			//graph.deleteElment();
			
		},
		
		undo  : function(graph) {
			if(this.type==1){
				
				this.element.unSelected();
				graph.nodes.remove(this.element);
				var node=this.element;
				this.element= this.pastElement;
				this.element.importNodeVariant(node);
				this.pastElement=node;
				
				this.element.adjustLabel(this.ctx);
				graph.addNode(this.element);
				graph.selectNode(this.element);
				var edges =graph.edges.getAll();
				for(var i in edges){
					if(edges[i].source.id==this.element.id )
						edges[i].source=this.element;
					
					if(edges[i].target.id==this.element.id)
						edges[i].target=this.element;
					
				}
				
				return 0;
			}
			else if(this.type==2){
				
				var cardinality={cs:"",ct:""};
				var edgeAux=this.element;
				
				if(this.element.isCardinality){
					 cardinality={cs:this.element.cardinalitySource,ct:this.element.cardinalityTarget};
				}
				this.element.unSelected();
				graph.edges.remove(this.element);
				
				this.element= this.pastElement;
				if(this.element.isCardinality){
					this.element.cardinalitySource=cardinality.cs;
					this.element.cardinalityTarget=cardinality.ct;
				}
				graph.addEdge(this.element);
				graph.selectEdge(this.element);
				this.pastElement=edgeAux;
				
				return 5; 
			}
			
		},
		
		redo : function (graph){
			
//			if(this.type==1){
//				graph.deleteNode(this.element);
//			}
//			else if(this.type==2) {
//				graph.deleteEdge(this.element);
//			}
			this.undo(graph);
		}
}


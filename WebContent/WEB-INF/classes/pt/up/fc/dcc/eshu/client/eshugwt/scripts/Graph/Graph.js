
Graph.prototype.nodes; //= new QuadTree(0,0,700,400);
Graph.prototype.edges; //= new Array();
Graph.prototype.selection;

function Graph(width,height) {
	
	this.nodes= new QuadTree(0,0,width,height);
	this.edges= new QuadTree(0,0,width,height);
	this.rectangleSelection= new RectangleSelection();
	this.width=width;
	this.height=height;
	this.reducibles=[];
}

Graph.prototype = {
		
	addNode : function(node) {
		this.nodes.insert(node);
	},
	
	addEdge : function(edge) {
		edge.source.setEdgesConnected(edge);
		edge.target.setEdgesConnected(edge);
		edge.updateBorderSize();
		edge.conectNodes();
		this.edges.insert(edge);

	},
	
	selectNode: function(node){
		if(this.selection!=undefined)
			this.selection.unSelected();
		this.selection=node;
		this.selection.selected();	
		//this.selection.selectedLabel();
		//this.selection.drawBorder(this.ctx);
		
		
	},
	
	selectEdge: function(edge){
		if(this.selection!=undefined)
			this.selection.unSelected();
		edge.selected();
		this.selection=edge;
		
	},
	
	select: function(element){
		if(this.selection!=undefined)
			this.selection.unSelected();
		element.selected();
		this.selection=element;
		
	},
	
	unselect: function(){
		if(this.selection!=undefined)
			this.selection.unSelected();
		this.selection=undefined;
		
	},
	unselectNodesRectangleSelection: function(){
		if(!this.isSelectionRectangleSelection()) return;
		var nodes=this.rectangleSelection.getListOfSelected();
		for ( var i in nodes) {
			nodes[i].unSelected();
			}
		this.rectangleSelection.reset();
		
	},
	
	unselectGraph:function(){
		this.unselect();
		this.unselectNodesRectangleSelection();
	},
	
	insideNode : function(x,y) {
		var nodes=this.nodes.search(x,y);
		var node=undefined;
		if (nodes.length>0){ 
			node = nodes.pop();
			for(var i in nodes){
				if(nodes[i].order>node.order)
					node=nodes[i];
			}
		}
		return node;
	},
	
	checkClickNode : function(node){
		this.selectNode(node);
		
	},
	
	
	deleteNode : function(node) {
		if(node==undefined) return;
			
		var listEdgeDelete = new Array();
//		var index;
		var edges=this.edges.getAll();
				for ( var k in edges) {
					if (node.id == edges[k].source.id
							|| node.id == edges[k].target.id) {
						listEdgeDelete.push(edges[k]);
						
					}
				}
////			
//				listEdgeDelete=node.getEdgesConnected();
				
		for ( var i in listEdgeDelete) {
			this.deleteEdge(listEdgeDelete[i]);
		}
		
		node.unSelected();
		node.removeHiddenCanvas();
		this.nodes.remove(node);
		
			
	},
	
	deleteEdge : function(edge) {
		if(edge==undefined) return;
		
		
		var edges=this.edges.getAll();

		this.edges=new QuadTree(0,0,this.width,this.height);
		for(var i in edges){
			if(edge.id!=edges[i].id)
				this.edges.insert(edges[i]);
		}
		
		
		edge.source.removeEdgesConnected(edge);
		edge.source.decremenDegreeOut();
		edge.target.removeEdgesConnected(edge);
		edge.target.decremenDegreeIn();
		edge.unSelected();
		
//		this.edges.remove(edge);
	},
	
	deleteSelectedElement : function(element) {
		if(!element) return;
		if(element.isEdge()){
			this.deleteEdge(element);
		}
		else
			this.deleteNode(element);
		this.selection=undefined;
	},
	
	deleteElment : function() {
		
		if(!this.rectangleSelection.isEmpty()){
			var nodes = this.rectangleSelection.getListOfSelected();
			for(var i in nodes){
				this.deleteNode(nodes[i]);
			}
			this.rectangleSelection.reset();
		}
		else
			this.deleteSelectedElement(this.selection);
	},
	
	
	deleteRectangleSelection : function() {
		
			var nodes = this.rectangleSelection.getListOfSelected();
			for(var i in nodes){
				this.deleteNode(nodes[i]);
			}
			this.rectangleSelection.reset();
	},
	
	deleteArrayNodes : function(nodes) {
		
		for(var i in nodes){
			this.deleteNode(nodes[i]);
		}
},
	
updateEdges : function() {
	var edges=this.edges.getAll();
	
			this.edges=new QuadTree(0,0,this.width,this.height);
			for(var i in edges){
					this.edges.insert(edges[i]);
			}
},
	
	
	insideEdge : function(x,y) {
		var edges=this.edges.search(x, y);
		for (var i in edges) {
			if (edges[i].contains(x, y)) {
				return edges[i];
			}
		}
		return undefined;
	},
	checkcenter : function(ctx) {
		/*
		var nodes=this.nodes.getAll();	
		for(var n in nodes){
			if(this.selection.isPositionCenterEqual(nodes[n]) ){
				var n1=this.selection.getCenter();
				var n2=nodes[n].getCenter();
				
				ctx.beginPath();
				ctx.moveTo(n1.x,n1.y);	
				ctx.lineTo(n2.x,n2.y);
				ctx.strokeStyle ="blue";
				ctx.lineWidth=1.5;
				ctx.stroke();
			}
		}*/
	},
	getSelection: function(){
		if(this.selection!=undefined){
			return this.selection;
		}
		else {
			this.rectangleSelection;
		}
		
	},
	
	nodeUpdateEdgesConnected : function(node) {
		var edges=node.getEdgesConnected();
		for(var i in edges)
			edges[i].updateBorderSize();
	},
	
	isElementSelection: function(){
		return this.selection!=undefined ;
	},
	
	isNodeSelection: function(){
			return this.selection!=undefined && this.selection.group=="Node";
	},
	
	
	isEdgeSelection: function(){
		return this.selection!=undefined && this.selection.group=="EDGE";
	},
	
	isNodeEdgeSelection : function(){
		return this.isNodeSelection() || this.isEdgeSelection();
	},
	
	isSelectionRectangleSelection: function(){
		return !this.rectangleSelection.isEmpty() ;
	},
	
	isSelected: function () {
		return (this.selection!=undefined || !this.rectangleSelection.isEmpty());
	},
	
	clearGraph :function (){
		this.nodes= new QuadTree(0,0,this.width,this.height);
		this.edges= new QuadTree(0,0,this.width,this.height);
//		alert("this.width,this.height "+this.width +" "+this.height)
	},
	
	clearNodes :function (){
		this.nodes= new QuadTree(0,0,this.width,this.height);
	},
	
	clearEdges :function (){
		this.edges= new QuadTree(0,0,this.width,this.height);
	},
	
	createNode : function(type,x, y,id) {
		var node;
		if(type.isConfigurable)
			node = new Vertices(x, y, id);
		else{
			//node =new type.type;
			if (typeof (window[type.type]) === "function")
				 node =  new window[type.type](x, y, id);
			else
				node =  new ComplexVertice(x, y, id);
//				throw new Error("type node: "+ type.type +" doesn't exists ");
			
		}
		
			node.setNodeType(type);
		return node;
	},
	
	createNodeImport : function(type,nodeParam) {
		var node = this.createNode(type,nodeParam.x, nodeParam.y, nodeParam.id);
		node.x=nodeParam.x+(node.width/25);
		node.y=nodeParam.y+(node.height/25);
		node.width=nodeParam.width;
		node.height=nodeParam.height;
		node.label.label=nodeParam.label;
		node.unSelected();
		node.label.selectLabel=false; //depois tem que eliminar
		if(nodeParam.containers && nodeParam.containers.length>0){
			node.resetContainer();
			node.importContainer(nodeParam.containers);
		}
		node.updateHandles();
		return node;
	},
	
	createEdgeImport : function(type,edge) {
		
		var sourceId=this.getNodeById(edge.source);
		var targetId=this.getNodeById(edge.target);
		
		if(sourceId && targetId){
			var list=new Array();
			list.push(sourceId.anchors[0])
			list.push(targetId.anchors[0])
			var edgeAux=this.createEdge(type,edge.id,sourceId,
					targetId,list);
			edgeAux.setFeatures(edge.features);
			edgeAux.modify=edge.temporary;
			
			this.addEdge(edgeAux);
			}
		 },
	
	getNodeById : function(id){
		var nodes = this.getAllNodes();
		for(var i in nodes)
			if(nodes[i].id==id)
				return nodes[i];
	},
	
	createNodeImportDiff : function(type,nodeParam) {
		
		var node = this.createNode(type,250, 150, nodeParam.id);
		node.widthDefault=140;
		node.heightDefault=80;
		node.width=140;
		node.height=80;
		node.selected();
		node.label.selectLabel=false; //depois tem que eliminar
//		node.modify="insert";
		this.addNode(node);
		return node;
	},
	
	createEdge : function(type,id,source,target,points) {
		

		if(type.variant=="Line"){
		if(source.type>target.type){
			var aux=source;
				source=target;
				target=aux;
			
			}
	}
		var edge= new Edge(id, source, target, points);//new Association(id, source, target, points);
			
			source.updateHandles();
			target.updateHandles();
			edge.setEdgeType(type);
			edge.conectNodes();
			edge.updateBorderSize();
		return edge;
	},
	updateBorderEdgeSelected : function(){
		if(this.isEdgeSelection())
			this.selection.updateBorderSize();
	},
	getAllNodes:function(){
		return this.nodes.getAll();
		
	},
	
	getAllEdges:function(){
		return this.edges.getAll();
	},
	
	copy : function() {
	
		if (this.selection != undefined) {
			if (this.selection.group=="Node"){
	 			this.structCopy= new StructCopy(1,this.selection.clone());
	 			
			}
		}
		else
			this.copyRectangleSelection();
	},
	
	copyNode : function(node) {
		  if (node != undefined) {
				if (node.group=="Node"){
		 			this.structCopy= new StructCopy(1,node.clone());
				}
			
		}
	},
	
	copyRectangleSelection : function() {
		if (this.isSelectionRectangleSelection()) {
			var nodes=this.rectangleSelection.clone();
			this.structCopy = new StructCopy(2,nodes);
		} 
	},	
	
	paste:function(generateID) {
		if(this.structCopy==undefined) return;
		if(this.structCopy.type==1){
			this.pasteNodeSelected(generateID);
		}
		else{
			this.pasteRectangleSelection(generateID);
		}
	},

	pasteNodeSelected : function(generateID) {
		var nodeInfo=this.structCopy.nodesCopied,node;
		node=this.createNode(nodeInfo.type,nodeInfo.x, nodeInfo.y,generateID++)
		node.x+=node.width+50;
		node.y+=node.height+30;
		node.label.label=nodeInfo.label;
		this.selectNode(node);
		this.addNode(node);
		return node;
	
	},
	
	pasteRectangleSelection : function(generateID) {
		var nodesInfo=this.structCopy.nodesCopied,node;
		this.unselectGraph();
		
		this.rectangleSelection.reset();
		this.rectangleSelection.setCoordinates(nodesInfo.xStart + nodesInfo.width,
				nodesInfo.yStart + nodesInfo.height,nodesInfo.xStart + nodesInfo.width*2,
				nodesInfo.yStart + nodesInfo.height*2); 
		
		var nodesSelected=nodesInfo.nodes,node,clone;
		
		for(var i in nodesSelected){
			node=nodesSelected[i].infoNode;
				clone=this.createNode(node.type,node.x, node.y,generateID++);
				clone.x=nodesInfo.xStart + nodesInfo.width+nodesSelected[i].dx;
				clone.y=nodesInfo.yStart + nodesInfo.height+nodesSelected[i].dy;
				clone.label.label=node.label;
				clone.selected();
				this.rectangleSelection.addElementListOfSelected(clone);
				this.addNode(clone);
				}
		return this.rectangleSelection;
	},

	resizeGraph : function(width,height) {
		this.width=width;
		this.height=height;
		var nodes=this.nodes.getAll();
		var edges=this.edges.getAll();

		this.nodes=new QuadTree(0,0,width,height);
		this.edges=new QuadTree(0,0,width,height);
		for(var i in nodes)
			this.nodes.insert(nodes[i]);
		for(var i in edges)
			this.edges.insert(edges[i]);
		
	},

	isLabelEditable: function(){
		return (this.isNodeEdgeSelection() && 
				this.selection.islabelEditable() );
	},
	getPropertiesViewSelected : function() {
		
		if(this.isNodeSelection())
			return this.selection.nodeType.properties;
		else if(this.isEdgeSelection()){
			return this.selection.edgeType.properties;
		}
		return undefined;
	},
	
	getPropertyElement : function (property){
		if(this.isElementSelection()){
			if(property=="label"){
				return this.selection.label.label;
				
			}
			else if(property=="sourceType")
				return this.selection.source.type;
			else if(property=="targetType")
				return this.selection.target.type;
			else
				return  this.selection[property];
		}
		return undefined;
	},
	
	setPropertyElement : function (property,value,type){
		if(this.isElementSelection()){
			if(property=="total"){
				this.selection.total=value;
			}
			var t=type.toLowerCase();
			if(t=="number" || t=="integer" || t=="integer" )
				 this.selection[property]=parseInt(value);
			else if(t.type=="booleam" || t.type=="bool")
				this.selection[property]=convertStringBoolen(value);
			else{
				if(property=="label"){
				//this.selection.label.showBar=false;
				 this.selection.label.label=value;
				 //this.selection.label.showBar=true;
				}
				else	
				 this.selection[property]=value;
			}
		}
	},
	adjustLabelSelection : function(ctx){
		if(this.isElementSelection()){
			this.selection.adjustLabel(ctx);
			this.selection.label.restartCursorPosition();
		}
	},
	
		getInfoUrl: function(){
			if(this.isNodeSelection()){
				return this.selection.nodeType.infoUrl;
			
			}
			else if(this.isEdgeSelection())
				return this.selection.edgeType.infoUrl;
	},
	
	getGraph : function() {	
		
		try {
			var graphJson = {
					reducibles:[],
				    nodes: [], 
				    edges: []
				};
		
			var nodeAux;
			var nodes=this.nodes.getAll();
			
			for (var i in nodes) {
				var jsonNode=nodes[i].getJson(nodes); // nodes for case include elements
				graphJson.nodes.push(jsonNode);
			}
				
			var edgeAux;
			var edges=this.edges.getAll();
			for (var i in edges) {
				graphJson.edges.push(edges[i].getJson());
			}
			
			for(var i in nodes){
				if(nodes[i].nodeType.includeElement)
					for(k in nodes){
						if(nodes[i].containsNode(nodes[k])){
							var id= parseInt(k)+500;
							graphJson.edges.push({
									   "type": "include",
									   "id":id ,
									   "label": "text",
									   "source": nodes[i].id,
									   "target": nodes[k].id,
									   "listHandler": [],
									   "features": []
									  }
							);
						}
					}
					
			}
			//if type diagram is eer
//			graphJson.reducibles.push("Attribute","AttributeDerived","AttributeMultivalue","AttributeKey","AttributeKeyWeak")
			
//			graphJson.reducibles=this.reducibles;
			for(var i in this.reducibles)
				graphJson.reducibles.push(this.reducibles);
			
//			return JSON.stringify(graphJson,null,1);
			return graphJson;
			}
		catch(err) {
		   console.log("Error in function getGraph(): "+ err);
		}
	},
	
	createConnection: function (node){
//		var nodes=this.nodes.getAll();
//		for (var i in nodes) {
//			if(nodes.containsNode(nodes[i]))
//				this.createConnection(nodes[i]);
//		}
	},
	
	setGraph : function(graph) {
		//this.importDiff(textJsonGraph);
		
//		this.clearGraph(); 
//		var objt = JSON.parse(graph);
//
//		var nodes=objt.nodes;
//		var edges=objt.edges;
//		var note=objt.note;
//		for (var i = 0; i < nodes.length; i++) {
//			var n = this.creatObjectNode(nodes[i]);
//			n.adjustLabelStart(this.ctx);
//			this.nodes.insert(n);
//					
//		}
		//this.adjustSizeCanvasGraph(nodes);
		//this.creatObjectEdge(edges);
		//this.draw();
		//this.adjustGenerateID();
		
	},
	createNodeSetGraph:function(nameNodetype){
		
		var node = this.createNode(type,x, y,id)
		
	},
	setReduciblesName : function(nodeName){
		this.reducibles.push(nodeName);
	},
	
	setReducibles : function(listReducibles){
		this.reducibles = listReducibles;
	},
	
	equalize : function(min){
		if(this.isSelectionRectangleSelection()){
			this.rectangleSelection.equalize(min);
		}
	}, 
	
	alignHorizontally:function(value){
		if(this.isSelectionRectangleSelection()){
			this.rectangleSelection.alignHorizontally(value);
		}
	},
	
	alignVertically:function(value){
		if(this.isSelectionRectangleSelection()){
			this.rectangleSelection.alignVertically(value);
		}
	},
	
	setWidthElement(value){
		
		if(this.isNodeSelection() && !this.selection.isAutoresize()){
			this.selection.setWidth(value);
		}
	},
	
	
	getWidthElement(){
		if(this.isNodeSelection() ){ //&& !this.selection.isAutoresize()
			return this.selection.getWidthValue();
		}
	},
	
	setHeightElement(value){
		if(this.isNodeSelection() && !this.selection.isAutoresize()){
			this.selection.setHeight(value);
		}
	},
	
	getHeightElement(){
		if(this.isNodeSelection() ){ //&& !this.selection.isAutoresize()
			return this.selection.getHeight();
		}
	},
	
	setXElement(value){
		
		if(this.isNodeSelection()){
			this.selection.updateX(value);
		}
	},
	
	setYElement(value){
		if(this.isNodeSelection()){
			this.selection.updateY(value);
		}
	},
	
	getXElement(){
		if(this.isNodeSelection()){
			return this.selection.getX();
		}
	},
	
	getYElement(){
		if(this.isNodeSelection()){
			return this.selection.getY();
		}
	},
		
	getName(){
		if(this.isNodeEdgeSelection()){
			return this.selection.getName();
		}
	},
	
	getTypeSeleted(){
		if(this.isNodeEdgeSelection()){
			return this.selection.getType();
		}
	},
	
	getCardinalitySource(){
		if(this.isEdgeSelection()){
			return this.selection.getCardinalitySource();
		}
	},
	
	setCardinalitySource(value){
		if(this.isEdgeSelection()){
			this.selection.setCardinalitySource(value);
		}
	},
	

	getCardinalityTarget(){
		if(this.isEdgeSelection()){
			return this.selection.getCardinalityTarget();
		}
	},
	
	setCardinalityTarget(value){
		if(this.isEdgeSelection()){
			this.selection.setCardinalityTarget(value);
		}
	},
	
	setName(text,ctx){
		
		if(this.isNodeEdgeSelection()){
			this.selection.setName(text);
		}
		if(ctx){
			this.selection.adjustLabel(ctx);
		}
	},
	
	setNameContainer(text,idContainer, idTextBox,ctx){
		
		if(this.isNodeSelection() && !this.selection.isConfigurable()){
			this.selection.setNameContainer(text,idContainer, idTextBox);
		}
		
		if(ctx && this.selection){
			this.selection.adjustLabel(ctx);
		}
	},
	
	removeTextBoxContainer (idContainer, idTextBox){
		if(this.isNodeSelection() && !this.selection.isConfigurable()){
			this.selection.removeTextBoxContainer (idContainer, idTextBox);
		}
	},
	
	addTextBoxContainer (idContainer){
		if(this.isNodeSelection() && !this.selection.isConfigurable()){
			this.selection.addTextBoxContainer (idContainer);
		}
	},
	
	getUrlElementSeleted(){
		if(this.isNodeEdgeSelection()){
			return this.selection.getUrl();
		}
	},
	
	getEdgeSourceName(){
		if(this.isEdgeSelection()){
			return this.selection.source().getName;
		}
	},
	
	getEdgeTargetName(){
		if(this.isEdgeSelection()){
			return this.selection.target().getName;
		}
	},
	showCardinality(){
		if(this.isEdgeSelection()){
			if( this.selection.target.getVariant()=="Relationship" &&
					this.selection.source.getVariant()=="Entity")
				return true;
		}
		return false;
	},
	
	showTotal(){
		if(this.isEdgeSelection()){
			if( this.selection.source.getVariant()!="Attribute" )
				return true;
		}
		return false;
	},
	
	getTotal(){
		if(this.isEdgeSelection()){
			return this.selection.total;
		}
	},
	
	setTotal(value){
	
		if(this.isEdgeSelection()){
//			alert(this.selection)
			this.selection.setTotal(value);
		}
	}
	
};

function StructCopy(type,nodes) {
	this.type=type;
	this.nodesCopied=nodes;
	this.nodeMarked;
}

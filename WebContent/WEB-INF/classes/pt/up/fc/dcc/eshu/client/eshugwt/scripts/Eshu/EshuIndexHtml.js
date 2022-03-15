var currentCoordinate;

Eshu.prototype.canvas = undefined;
Eshu.prototype.elementDraw=undefined;
Eshu.prototype.typeNodedrawLook=false;
Eshu.prototype.ctx = undefined;
Eshu.prototype.valid = false; // when set to true, the canvas will redraw //							// // everything
Eshu.prototype.graphState = 0; // Keep track of when we are dragging the // 0  false, 1-move node 2-create rectangle selection 3-rectangle selected
Eshu.prototype.language;												// 4-draw line edges //5 element selected // 6- resize element // 7- move rectangle selected
Eshu.prototype.resizeDragging = false;//  								//8-context menu active
Eshu.prototype.language;												
Eshu.prototype.menuAtive=0;
Eshu.prototype.information=false;
Eshu.prototype.isShowNodeInformation=false;
Eshu.prototype.positionHorizontal=true;
Eshu.prototype.generateID;
Eshu.prototype.imagesSVG;
Eshu.prototype.err= new Language( "err", ["attribute","entity"],[]);

function Eshu(div,width,heigth) {
//	var editorStyle=editorConf.editorStyle;
//	var toobarStyle=editorConf.toobarStyle;
	this.div = div;
	this.setLanguage("err");

	/*Create editor*/
	if(width!= undefined)this.WIDTH=width;
	else this.WIDTH=400;
	if(heigth!= undefined)this.WIDTH=heigth;
	else this.HEIGHT=400;
	this.GRIDVISIBLE=false;
	this.GRID_LINE_COLOR="blue"
	
	// create Toolbar
	this.toolbar = document.createElement("div");
	this.toolbar.setAttribute("id", "toolbar");
	this.toolbar.style.padding= "5px"; 
	this.toolbar.style.display= "block";
	this.toolbar.style.cssFloat= "left";
	this.toolbar.style.border= 1+"px solid";
	this.toolbar.style.borderColor="black";
	this.toolbar.style.backgroundColor ="WhiteSmoke";
	this.toolbar.style.position = "relative";
	
	
	this.graphEditor = document.createElement("div");
	this.graphEditor.setAttribute("id", "graphEditor");
	
	this.canvas = document.createElement("canvas");
	this.canvas.setAttribute("id", "eshuCanvaEditor");

	div.appendChild(this.toolbar);
	this.graphEditor.appendChild(this.canvas)
	div.appendChild(this.graphEditor);

	this.canvas.width= this.WIDTH;
	this.canvas.height=this.HEIGHT*0.80; 
	this.ctx = this.canvas.getContext("2d");
	this.ctx.fillStyle = this.BACKGROUND_COLOR;
	this.canvas.style.border= this.BORDER_WIDTH+"px solid";
	this.canvas.style.borderColor=this.BORDER_COLOR;
	//this.initShadows();
	this.ctx.fillRect(0, 0, this.WIDTH, this.HEIGHT);

	this.information=true;
		
	this.canvas.addEventListener('click', this.click.bind(this), false);
	document.addEventListener('dblclick', this.dblclick.bind(this), false);
	this.canvas.addEventListener('mousedown', this.mousedown.bind(this), true);
	this.canvas.addEventListener('mousemove', this.mousemove.bind(this), true);
	this.canvas.addEventListener('mouseup', this.mouseup.bind(this), true);
	document.addEventListener("keydown", this.handleSpecialKeys.bind(this), false); 
	document.addEventListener("keypress", this.handleNormalKeys.bind(this), false); 
	document.addEventListener("contextmenu", this.contextMenu.bind(this));
	document.oncontextmenu=disableContextMenu;

	
	div.style.width = this.WIDTH+"px";
	div.style.height= this.HEIGHT+"px";
	div.style.border = "1px solid"; 

	this.drawToolbar();
	document.body.style.cursor = "auto";
	this.generateID=0;
	this.graphState=0;
	setInterval(function(){ if(this.graph.isNodeSelection()){this.editableName()} }.bind(this), 800);
	this.limitUndo=5;
	this.commandStack=new CommandStack(this.limitUndo);
	this.graph=new Graph(this.canvas.width,this.canvas.height);
	this.elementsTypes=new Map();
	this.imagesSVG= new Map();
	//this.edgeTypes=[];
	
	//this.setPositionVertical();
}

Eshu.prototype = {
		
	setLanguage : function(language){
		if (language == "err")
			this.language = new Language( "err", ["Actor","UseCase","Note"],["Association"]);
		else {
			this.language = language;
		}
	},	
		
	
	draw : function(){
		this.ctx.clearRect(0, 0, this.WIDTH, this.HEIGHT);
		this.ctx.fillRect(0, 0, this.WIDTH, this.HEIGHT);
		
		if (this.getGridVisible()) {
			this.showGrid();
		}
		
		this.drawNodes();
		this.drawEdges();
		
		
		
		if(this.graphState==2 || this.graphState==3|| this.graphState==7){
			this.graph.rectangleSelection.draw(this.ctx);
		}
		else if(this.graphState==4){
			this.temporaryPathEdge();
		}
		
	},
	
	temporaryPathEdge: function (){
		var l=this.pointsEdge.length;
		if( l<2) return;
		for(var i=0; i< l-1;i++){
			this.drawSimpleLine (this.pointsEdge[i],this.pointsEdge[i+1],"red")
		}
	},
	drawNodes : function(){
		var nodes=this.graph.nodes.getAll();
		for ( var n in nodes)
			if (nodes[n].draw != undefined) {
				nodes[n].draw(this.ctx);
			}
	},
	
	drawEdges: function(){
		var edges=this.graph.edges.getAll();
		for ( var e in edges)
			if (edges[e].draw != undefined) {
				edges[e].draw(this.ctx);
			}
	},
	
	drawSimpleLine :function (point1,point2,color){
		
		this.ctx.save();
		this.ctx.beginPath();
		this.ctx.lineCap="round";
		this.ctx.moveTo(point1.x, point1.y);
		this.ctx.lineTo(point2.x, point2.y);
		this.ctx.strokeStyle =color;
		this.ctx.lineWidth=1;
		this.ctx.stroke();
		this.ctx.restore();
	},
	
	clear:function(){
		this.graph.clearGraph();
		this.draw();
	},
	
	deleteElement:function(){
		this.graph.deleteElement();
		this.draw();
		
	},
	
	addNodeType:function(nodetype){
		
		this.createTypeNodeToolbar(nodetype);
		//this.nodeTypes.push(nodetype);
		this.elementsTypes.put(nodetype.type, nodetype);
	},
	
	addEdgeType:function(edgetype){
		this.createTypeEdgeToolbar(edgetype);
		//this.edgeTypes.push(edgetype);
		this.elementsTypes.put(edgetype.type, edgetype)
	},
	
	
	createNodeTypes:function(listNodetype){
		var nodeTypes;
		try {
			 nodeTypes=JSON.parse(listNodetype);
			 for(var i in nodeTypes){
				 var nodetypeObj =new NodeType(nodeTypes[i]);
				 nodetypeObj.setImageSVG(this.imagesSVG.get("actor.svg"))
					this.addNodeType(nodetypeObj);
				}
		} catch (e) {
			throw "Error in convert string node type to JSON: " +e;
		}
	},
	
	createEdgeType:function(listEdgetype){
		var edgeTypes;
		try {
			edgeTypes=JSON.parse(listEdgetype);
			for(var i in edgeTypes){
				this.addEdgeType(new EdgeType(edgeTypes[i]));
			}
		} catch (e) {
			throw "Error in convert string edge type to JSON: " +e;
		}
	},
	
	
	
	setImageSVG : function(key, imageSVG){
		var image= new Image();
		image.src= "data:image/svg+xml;charset=utf-8," +imageSVG;
		alert(key);
		this.imagesSVG.put(key,image);
		
		//alert(key + " : " +image);
	},
};



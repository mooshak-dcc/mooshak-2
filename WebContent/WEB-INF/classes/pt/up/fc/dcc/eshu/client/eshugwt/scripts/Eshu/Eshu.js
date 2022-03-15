/**
 * @author Helder Correia
 */

var currentCoordinate;

Eshu.prototype.canvas = undefined;
Eshu.prototype.elementDraw=undefined;
Eshu.prototype.typeNodedrawLook=false;
Eshu.prototype.ctx = undefined;
Eshu.prototype.valid = false; // when set to true, the canvas will redraw //							// // everything
Eshu.prototype.graphState = 0; // Keep track of when we are dragging the // 0  false, 1-move node 2-create rectangle selection 3-rectangle selected
Eshu.prototype.language;												// 4-draw line edges //5 element selected // 6- resize element // 7- move rectangle selected
Eshu.prototype.resizeDragging = false;//  								//8-context menu active //10- rotation
Eshu.prototype.language;												
Eshu.prototype.menuAtive=0;
Eshu.prototype.information=false;
Eshu.prototype.isShowNodeInformation=false;
Eshu.prototype.validationSyntaxe=false;
Eshu.prototype.positionHorizontal=true;
Eshu.prototype.generateID;
Eshu.prototype.imagesSVG;
Eshu.prototype.imagesSVGObjects;
Eshu.prototype.maxOrder=0;
Eshu.prototype.err= new Language( "err", ["attribute","entity"],[]);
Eshu.prototype.editorHTML=false;
Eshu.prototype.reducibles;
Eshu.prototype.syntaxValidation;


function Eshu(div,width,height) {
	while(div.hasChildNodes()) {
		div.removeChild(div.firstChild);
	    languageOptionCanvas=[];
	}
	this.div = div;
	this.setLanguage("err");

	/*Create editor*/
	if(width!= undefined)this.WIDTH=width;
	else this.WIDTH=400;
	if(height!= undefined)this.HEIGHT=height;
	else this.HEIGHT=400;
	
	this.GRIDVISIBLE=false;
	this.GRID_LINE_COLOR="blue"
	this.wrapper = document.createElement('div');
	this.wrapper.setAttribute("id", "wrapper"); 
	this.wrapper.overflow= "hidden"; 
	this.wrapper.style.display= "table"; 
//	console.log(" this.WIDTH "+ this.WIDTH)
	this.wrapper.style.width =1000 +"px";
	this.wrapper.style.height=700 + "px";
	// create Toolbar
	this.toolbar = document.createElement('div');
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
	
	this.editor = document.createElement("div");
	this.editor.setAttribute("id", "editor_");
	this.editor.width=this.WIDTH;
	this.editor.height=this.HEIGHT;
	
	
	this.editor.appendChild(this.canvas);
	this.graphEditor.appendChild(this.editor);
	//	this.graphEditor.appendChild(this.canvas)
	this.wrapper.appendChild(this.graphEditor);
	div.appendChild(this.wrapper);

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
	this.canvas.addEventListener("mouseenter", this.list.bind(this),false);

	
	div.style.width = this.WIDTH+"px";
	div.style.height= this.HEIGHT+"px";
	//div.style.border = "1px solid"; 

	//this.drawToolbar();
	document.body.style.cursor = "auto";
	this.generateID=1;
	this.graphState=0;
	setInterval(function(){ if(this.graph.isNodeEdgeSelection() ){this.editableName()} }.bind(this), 800);  //this.graph.isNodeSelection()
	this.limitUndo=5;
	this.commandStack=new CommandStack(this.limitUndo);
	this.graph=new Graph(this.canvas.width,this.canvas.height);
	this.elementsTypes=new Map();
	this.imagesSVG = new Map();
	this.imagesSVGObjects= new Map();
	this.reducibles=[];
	
	this.formatPanel; // WINDOWS PROPERTY
	this.formatPanelVisibly;
	
//	this.formatPanel=new FormatPanel(this);
//	this.formatPanelVisibly=false;
//	this.formatPanel.init();
//	this.formatPanel.createFormatPanel();
	
	/*
	var btn = document.createElement("BUTTON");
	
//	 var t = document.createTextNode("Sair");
//	    btn.appendChild(t);
	btn.id='btnformatpanel';
    btn.style.width = '30px';
    btn.style.height = '30px';
    btn.style.position = 'absolute';
    btn.style.margin = '4px';
    btn.style.top=0;
    btn.style.right=0;
    btn.style.cursor='pointer';
//  
//    var icon = document.createElement("span");
//    icon.className ="material-icons";
//    btn.appendChild(icon);
    
    var icon = document.createElement("i");
    icon.className ="material-icons";
    icon.innerHTML="menu";
    btn.appendChild(icon);
    
    
    btn.addEventListener('click', function(event) {
//		this.setWidth(210);
    	if(this.formatPanelVisibly){
			this.formatPanelVisibly=false;
			this.formatPanel.hide();
			this.setPositionHorizontal();
    	}
    	else{
			this.formatPanel.show();
			this.formatPanelVisibly=true;
			this.setPositionHorizontal();
    	}
//		document.getElementById("btnformatpanel").style.display='none';
	}.bind(this), false);
    
    this.graphEditor.appendChild(btn);
    this.div.style.position= 'relative'
    this.buttomFormatPanel=btn;
    
    
    */
    
    //this.formatPanel.createFormatPanel();
	
	//this.edgeTypes=[];
}

Eshu.prototype = {
		
	
	init : function (){
		//this.formatPanel.createFormatPanel();
//		this.createBtnMenu()
		
		this.formatPanel=new FormatPanel(this);
		this.formatPanelVisibly=false;
		this.createBtnMenu()
		this.formatPanel.createFormatPanel();
	},
	
	createBtnMenu : function (){
		
		var btn = document.createElement("BUTTON");
		
//		 var t = document.createTextNode("Sair");
//		    btn.appendChild(t);
		btn.id='btnformatpanel';
	    btn.style.height = '30px';
	    btn.style.position = 'absolute';
	    btn.style.margin = '4px';
	    btn.style.marginTop='30px';
	    btn.style.top=0;
	    btn.style.right=0;
	    btn.style.cursor='pointer';
	//  
//	    var icon = document.createElement("span");
//	    icon.className ="material-icons";
//	    btn.appendChild(icon);
	    
	    var icon = document.createElement("i");
	    icon.className ="material-icons";
	    icon.innerHTML="menu";
	    btn.appendChild(icon);
	    
	    
	    btn.addEventListener('click', function(event) {
//			this.setWidth(210);
	    	if(this.formatPanelVisibly){
				this.formatPanelVisibly=false;
				this.formatPanel.hide();
				this.setPositionHorizontal();
	    	}
	    	else{
				this.formatPanel.show();
				this.formatPanelVisibly=true;
				this.setPositionHorizontal();
			
	    	}
//			document.getElementById("btnformatpanel").style.display='none';
		}.bind(this), false);
	    
	    this.graphEditor.appendChild(btn);
	    this.div.style.position= 'relative';
	    this.buttomFormatPanel=btn;
	    
		
		
	},
	
	setLanguage : function(language){
//		if (language == "err")
//			this.language = new Language( "err", ["Actor","UseCase","Note"],["Association"]);
//		else {
//			this.language = language;
//		}
	},	
		
	
	list:function(){
		if(this.groupShow!=undefined)
			this.hideGroup2(this.groupShow);
	
	},
	
	/**  
	 *  Draw the nodes and Edge
	 *  @return {null}
	*/
	draw : function(){
		this.ctx.clearRect(0, 0, this.WIDTHCANVAS, this.HEIGHTCANVAS);
		this.ctx.fillRect(0, 0, this.WIDTHCANVAS, this.HEIGHTCANVAS);
		
		if (this.getGridVisible()) {
			this.showGrid();
		}
		
//		this.drawNodes();
//		this.drawEdges();
		var nodes=this.graph.nodes.getAll();
		var edges=this.graph.edges.getAll()
		var newArray =newArray=nodes;
		newArray.push.apply(newArray, edges);
		newArray.sort(function(a, b) {
			  return a.order - b.order;
			});
		
		for ( var n in newArray)
			if (newArray[n].draw != undefined) {
				newArray[n].draw(this.ctx);
			}
		
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
	
	/**  
	 *  Draw the nodes of graph
	 *  @return {null}
	*/
	drawNodes : function(){
		var nodes=this.graph.nodes.getAll();
		nodes.sort(function(a, b) {
			  return a.order - b.order;
			});
		for ( var n in nodes)
			if (nodes[n].draw != undefined) {
				nodes[n].draw(this.ctx);
			}
	},
	
	/**  
	 *  Draw the edges of graph
	 *  @return {null}
	*/
	drawEdges: function(){
		var edges=this.graph.edges.getAll();
		for ( var e in edges)
			if (edges[e].draw != undefined) {
				edges[e].draw(this.ctx);
			}
	},
	
	/**  
	 *  Draw the simple line after edge
	 *  @return {null}
	*/
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
	
	/**  
	 *  clear the graph
	 *  @return {null}
	*/
	clear:function(){
		this.deleteElement();
		this.draw();
		
	},
	
	/**  
	 *  Delete a element in graph
	 *  @return {null}
	*/
	deleteElement:function(){
		this.graph.deleteElement();
		this.draw();
		
	},
	
	/**  
	 *  create a node option in toolbar and add a nodeType in the map elementsTypes
	 *  @param {JSON} nodetype - a json with configuration
	 *  @return {null}
	*/
	addNodeType:function(nodetype){
		this.createTypeNodeToolbar(nodetype);
		this.elementsTypes.put(nodetype.type, nodetype);
	},
	
	/**  
	 *  create a edge option in toolbar and add a edgeType in the map elementsTypes
	 *  @param {JSON} edgetype - a json with configuration
	 *  @return {null}
	*/
	addEdgeType:function(edgetype){
		this.createTypeEdgeToolbar(edgetype);
		this.elementsTypes.put(edgetype.type, edgetype)
	},
	
	
	/**  
	 *  create and add a nodeType in the map elementsTypes
	 *  @param {JSON} nodeTypeString - a nodeType with all json with configuration
	 *  @return {null}
	*/
	createNodeType:function(nodeTypeString){
		try {
//			console.log(nodeTypeString)
			var nodeType=JSON.parse(nodeTypeString);
			var nodetypeObj=new NodeType(nodeType);
			 nodetypeObj.setImageSVG(this.imagesSVGObjects.get(nodeType.imgSVGPath.replace(/\s/g, '')));
			 nodetypeObj.setImageIconTollBarSVG(this.imagesSVG.get(nodeType.style.iconTollbarSVGPath.replace(/\s/g, '')));
			 this.addNodeType(nodetypeObj);
		} catch (e) {
			throw "Error in convert string node type to JSON: " +e;
		}
	},
	
	/**  
	 *  create and add a edgeType in the map elementsTypes
	 *  @param {JSON} edgeTypeString - a list with all json with configuration
	 *  @return {null}
	*/
	createEdgeType:function(edgeTypeString){
		
		try {
			var edgeType=JSON.parse(edgeTypeString);
			var edgeTypeObj= new EdgeType(edgeType);
			edgeTypeObj.setImageIconTollBarSVG(this.imagesSVG.get( edgeType.iconTollbarSVGPath.replace(/\s/g, '')));
			this.addEdgeType(edgeTypeObj);
		} catch (e) {
			throw "Error in convert string edge type to JSON: " +e;
		}
	},
	
	/**  
	 *  create and add a list of nodeType in the map elementsTypes
	 *  @param {JSON} listNodetype - a list of json with configuration of nodes
	 *  @return {null}
	*/
	createNodeTypes:function(listNodetype){
		var nodeTypes;
		try {
			console.log(listNodetype);
			 nodeTypes=JSON.parse(listNodetype);
			 
			 for(var i in nodeTypes){
				 var nodetypeObj=new NodeType(nodeTypes[i]);
				 nodetypeObj.setImageSVG(this.imagesSVGObjects.get(nodeTypes[i].style.imgSVGPath.replace(/\s/g, '')));
				 nodetypeObj.setImageIconTollBarSVG(this.imagesSVG.get(nodeTypes[i].style.iconTollbarSVGPath.replace(/\s/g, '')));
				 this.addNodeType(nodetypeObj);
				}
		} catch (e) {
			throw "Error in convert string node type to JSON: " +e;
		}
	},
	
	/**  
	 *  create and add a list of edgeType in the map elementsTypes
	 *  @param {JSON} listEdgetype - a list of json with configuration of Edges
	 *  @return {null}
	*/
	createEdgeTypes:function(listEdgetype){
		var edgeTypes;
		try {
			edgeTypes=JSON.parse(listEdgetype);
			for(var i in edgeTypes){
				var edgeTypeObj= new EdgeType(edgeTypes[i]);
				edgeTypeObj.setImageIconTollBarSVG(this.imagesSVG.get( edgeTypes[i].style.iconTollbarSVGPath.replace(/\s/g, '')));
				this.addEdgeType(edgeTypeObj);
			}
		} catch (e) {
			throw "Error in convert string edge type to JSON: " +e;
		}
	},
	
	/**  
	 *  create and set the image in map, receive the image in base64 
	 *  @param {string} key - the key, usually is a path define in DL2
	 *  @param {string} imageSVG - the string with svg image in base64
	 *  @return {null}
	*/
	setImageSVG : function(key, imageSVG){
		var image= new Image();
		image.dataName = key;
		image.src= "data:image/svg+xml;base64," +btoa(imageSVG);
//		image.src= "data:image/svg+xml;charset=utf-8," +imageSVG;
//		this.imagesSVG.put(key,image);
//		console.log(key +"    ENTRE   "+ imageSVG)
		this.imagesSVGObjects.put(key,image);
		this.imagesSVG.put(key,imageSVG);
		this.editorHTML=false;
	},
	
	/**  
	 *  create and set the image in map, receive the path of image, load and create the image 
	 *  @param {string} key - the key, usually is a path define in DL2
	 *  @param {string} imageSVG - the path of image 
	 *  @return {null}
	*/
	setImage : function(key, imagePath){
		var image = new Image();
		image.src= imagePath; 
		this.imagesSVGObjects.put(key,image);
		this.editorHTML=true;
		this.editorHTML=true;
	},
	
	
	/**  
	 *  set the name of nodes reducibles in the list of nodes reducibles 
	 *  @param {string} key - the key, usually is a path define in DL2
	 *  @param {string} imageSVG - the string with svg image in base64
	 *  @return {null}
	*/
	setReducibles: function (reducibles){
//		var listReducibles = reducibles.split(";");
//		for(var i in listReducibles){
//			this.reducibles.push(listReducibles[i]);
//		}
		this.reducibles=reducibles;
		this.graph.setReducibles(reducibles);
		
//		console.log("TEST "+this.reducibles);
//		console.log(typeof(reducibles));
	},
	
	setSyntaxValidation: function (rules){
		
		console.log("RULESSS "+ rules);
		try {
			var rulesObject=JSON.parse(rules);
			this.syntaxValidation = rulesObject;
			this.setReducibles(rulesObject.reducibles)
		} catch (e) {
			throw "Error in convert string Syntax Validation to JSON: " +e;
		}
	}
	
};
/**
 * the image of log formatPanel
 */
var iconFormatPanel='url(data:image/png;base64,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)'

var leftIcon= 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABHNCSVQICAgIfAhkiAAAAE1JREFUSIljYBgFgw38h2KqASZqGjZqQQM1LMQVyQ04xKliQQMOcapYgGw4KRgOaB7J6IDuQYRuCcmAhUh1DeQYjg2MlkX0t2AUDDwAAFg2JHT75OsYAAAAAElFTkSuQmCC';	

var centerIcon= 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABHNCSVQICAgIfAhkiAAAAF9JREFUSIljYBgFQw38h2KiARONHDICLGggwyyS9PzHoQFXJDfgEMdrATZLsFnQgEMcq0ZsuAGLOmyGY8O0j2RcgOpBhM0CdMNxWYBsCdEAm+H4LMCnhyQwWhaNguEIAPqgMOc4LmkaAAAAAElFTkSuQmCC';

var rightIcon= 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABHNCSVQICAgIfAhkiAAAAE9JREFUSIljYBgFww38h2I4YKK1jSPXggZqOgIb+I/DEoxIpsQCbJYQtOA/GbgBi344oHkkkwvIDiJSLEA3nKoWYDOcqhbgAqNl0SgYigAAUWUmbldIKbcAAAAASUVORK5CYII=';


var currentCoordinate;
var nodeSelected=edgeSelected=firstNodeSelected= false;
var selectionColor = "blue";//'#ff0000';
var typeEdge=1;
var cursorX;
var cursorY;

Graph.prototype.WIDTH=700;
Graph.prototype.HEIGHT=400;
Graph.prototype.BACKGROUNDCOLOR='#F5F5F5';
Graph.prototype.nodes; //= new QuadTree(0,0,700,400);
Graph.prototype.edges; //= new Array();
Graph.prototype.canvas = undefined;
Graph.prototype.typeNodedraw=undefined;
Graph.prototype.typeNodedrawLook=false;
Graph.prototype.ctx = undefined;
Graph.prototype.nodeInicialSelc = undefined;
Graph.prototype.valid = false; // when set to true, the canvas will redraw //							// // everything
Graph.prototype.dragging = false; // Keep track of when we are dragging the //
Graph.prototype.language;
Graph.prototype.dragoffx = 0; // See edown and mousemove events for // //								// explanation
Graph.prototype.dragoffy = 0;
Graph.prototype.resizeDragging = false;
Graph.prototype.expectResize = -1;
Graph.prototype.selection = undefined;
Graph.prototype.redesign = false;
Graph.prototype.gridVisible = false;
Graph.prototype.drawEdges = false;
Graph.prototype.menuAtive=0;
Graph.prototype.information=false;
Graph.prototype.isShowNodeInformation=false;
Graph.prototype.reDrawLineEGC=false;
Graph.prototype.positionHorizontal;
Graph.prototype.generateID=0;
Graph.prototype.selectionRectangle=[0,0,0,0];// x start,y start,x end,y end
Graph.prototype.isSelectionMultiples=false;
Graph.prototype.moveSelectionMultiples=false;
Graph.prototype.listOfSelected=new Array();
Graph.prototype.drawSelectionRectangle=false;
Graph.prototype.generateID=0;
Graph.prototype.NodeCopy=undefined;
Graph.prototype.event=undefined;
Graph.prototype.selectShowAnchor;
Graph.prototype.edgesMoved=[];
Graph.prototype.coorsNodesSelRectangle=[];
Graph.prototype.allNodes=[];


function Graph(div,w,h) {
	this.div = div;
	
	this.WIDTH=w;
	this.HEIGHT=h;
	//this.nodes= new QuadTree(0,0,w,h);
	//this.edges= new QuadTree(0,0,w,h);
	this.option = document.createElement("div");
	this.option.setAttribute("id", "option");
	this.option.style.padding= "5px"; 
	this.option.style.display= "block";
	this.option.style.cssFloat= "left";
	this.setLanguage("err");
	//this.drawoption();
	this.graphEditor = document.createElement("div");
	this.graphEditor.setAttribute("id", "graphEditor");
	
	
	this.canvas = document.createElement("canvas");
	this.canvas.setAttribute("id", "eshuCanvaEditor");
	this.canvas.style.backgroundColor ='#F5F5F5';

	
	div.appendChild(this.option);
	this.graphEditor.appendChild(this.canvas)
	div.appendChild(this.graphEditor);

	this.canvas.width= w;
	this.canvas.height=h*0.80; 
	this.ctx = this.canvas.getContext("2d");
	this.ctx.fillStyle = 'red';
	this.initShadows();
	this.ctx.fillRect(0, 0, this.WIDTH, this.HEIGHT);
	//this.ctx.font = 49;
	//this.informationBox= new InformationBox(this.ctx);
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
	//this.creatMenus();	

	this.cursorPosition;
	setInterval(function(){ if(nodeSelected && !this.drawEdges){this.editableName()} }.bind(this), 600);
	
	div.style.width = w+"px";
	div.style.height= h+"px";
	//div.style.border = "1px solid";  depois eliminar
	
	this.graphEditor.style.width = w*0.97+"px";
	this.graphEditor.style.height= h*0.80+"px";
	//this.graphEditor.style.border = "1px solid";
	this.graphEditor.style.overflowY="auto"; 
	this.graphEditor.style.margin="1%";
	
//	this.divNote.style.width = w*0.98+"px";
//	this.divNote.style.height= h*0.125+"px";
//	this.divNote.style.border = "1px solid";
//	this.divNote.style.overflowY="auto"; 
//	this.divNote.style.margin="1%";
	this.positionHorizontal=true;
}

Graph.prototype.initShadows = function() {
	this.ctx.shadowBlur=5;
	this.ctx.shadowOffsetX = 3;
	this.ctx.shadowOffsetY = 3;
	this.ctx.shadowColor="#AAAAAA";
}
 
Graph.prototype.editableName = function() {
	
	this.draw();
	if (nodeSelected && this.selection != undefined && this.selection.group=="Node")
		this.selection.label1(this.ctx);
}


Graph.prototype.contextMenu = function(e) {
	if(this.menuAtive == 2) return;
     cursorX = e.pageX;
     cursorY = e.pageY;
     if(this.selection!=undefined && e.target.id=="eshuCanvaEditor"){
		 this.draw();
		 if(this.selection.group=="EDGE"){
			 this.showContextMenu(cursorX , cursorY, 2)
		 }
			else{
				this.showContextMenu(cursorX , cursorY  +this.selection.height/2, 2)
			}
		 
				this.showProperties();
			
	 }
   //  if (!e)
    	    event.returnValue = false;
    	  // Firefox, Safari, Opera
	 event.preventDefault();
}

Graph.prototype.insertCardinality = function(listCardinality) {
	
	for(var i=0;i<listCardinality.length;i++)
		this.selection.insertCardinality(i,listCardinality[i]);
	
}

Graph.prototype.handleSpecialKeys = function(event) {
	var keyId = event.keyCode;
	switch (keyId) {
	case 65:
		if (event.ctrlKey) {
			this.selectedALL();
			event.preventDefault();
		}
		break;
	case 27:
		if(this.isSelectionMultiples)
			this.unselectSelectionMultiples();
		else if(this.selection!=undefined &&this.selection.group=="Node"){
			currentCoordinate.x=0;
			this.unSelectedNode();
		}
		else if(this.selection!=undefined){
			currentCoordinate.x=0;
			this.unSelectedEdge();
		}
		
		this.draw();
		event.preventDefault();
		break;
	case 46:
			this.delet();
			return;
		break;
	case 86:
		this.paste(event);
		break;	
	case 67:
	this.copy(event);
		break;
	case 88:
	this.cut(event);
		break;
	default:
		this.handleSpecialKeysNodeSelected(event);
		break;
	
	}
}
Graph.prototype.handleSpecialKeysNodeSelected = function(event) {
	var keyId = event.keyCode;
	if(this.selection != undefined && this.menuAtive==0 && keyId!=16){
		if(this.selection.group=="EDGE" && keyId!=46) return;
		
		switch(keyId)
		{	

		case 8:
			this.selection.deleteLastCharName();
			event.preventDefault();
			this.draw();
			break; 
		case 37:
			//selectedALL
//			if(event.shiftKey){
//	console.log("decrease")
			//	  this.selection.decreaseCursorPositionShift();
//			}
//			else
			this.selection.decreaseCursorPosition();
			event.preventDefault();
			break;
		case 39:
//			if(event.shiftKey){
//			this.selection.increaseCursorPositionShift
//			console.log("increase")
//			}
//			else
			this.selection.increaseCursorPosition();
			event.preventDefault();
			break; 
			
			case 67:
				
			break;	
			
			
			
		default:
			// ignore regular characters that must be received by keyPress
			// to correctly handle key sequences and keyboard mapping
			break;

		}
		/*
		 * 
		 * This code is not required for special keys, is it?
		 * 
		if (this.selection.group == "Node") {
			this.cursorPosition = this.selection.getCursorPosition()
			this.editableName();
		}
		*/
	}
}

Graph.prototype.handleNormalKeys = function(event) {
	var keyId = event.keyCode || event.charCode;
	
	if(this.selection != undefined && this.menuAtive==0 && keyId!=16){
		if(this.selection.group=="EDGE") return;

		
		if (event.shiftKey && keyId != 16)
			this.selection.addCharLabel(String.fromCharCode(keyId), this.cursorPosition);
		else if (keyId >= 65 && keyId <= 90) {
			this.selection.addCharLabel(String.fromCharCode(keyId + 32), this.cursorPosition);
		} else {
			this.selection.addCharLabel(String.fromCharCode(keyId), this.cursorPosition);
		}
		
		if (this.selection.group == "Node") {
			this.cursorPosition = this.selection.getCursorPosition()
			this.editableName();
		}
		
	}
}


function getKeyLetterlower(key){
	
    var letters="abcdefghijklmnopqrstuvwxyz";
    if(key>64 && key<91){
        return letters.substring(key-64,key-65);
    }
}


Graph.prototype.click = function(event) {
	this.event=event;

	currentCoordinate = windowToCanvas(this.canvas,event);
	var isLineDraw=true;
	if(this.typeNodedraw!=undefined){
		if (this.typeNodedraw != "line" && this.typeNodedraw != "lineEGC") {
			this.addNode(this.createNodes(this.typeNodedraw, currentCoordinate.x, currentCoordinate.y, this.generateID++));
			this.unSelectOperation();
			
		}
		else if (this.typeNodedraw == "line" || this.typeNodedraw == "lineEGC"){
		
			if(firstNodeSelected && !this.reDrawLineEGC){
				
				this.clickSelectedFinalNode();
				return;
			}
			else if(firstNodeSelected && this.reDrawLineEGC && !this.drawEdges){
			
				var nodes = this.nodes.search(currentCoordinate.x, currentCoordinate.y);
				for (var i in nodes) {
					if (nodes[i].insideAnchor(currentCoordinate.x, currentCoordinate.y)) {
						if(this.selection.handleSelected==0){
							this.selection.source = nodes[i];
							this.selection.AI = nodes[i].handleSelected;
						}
						else if(this.selection.handleSelected==2){
							
							this.selection.target = nodes[i];
							this.selection.AF = nodes[i].handleSelected;
					}
						firstNodeSelected = false;
						nodes[i].selectHanderEdge = false;
						this.reDrawLineEGC=false;
						this.draw();
						this.typeNodedraw=undefined;
						if(this.selection!=undefined){
							this.selection.source.backgroundColor='#B8B8B8';
							this.selection.target.backgroundColor='#B8B8B8';
							this.selection.source.unSelected();
							this.selection.target.unSelected();
						} 
						
						return;

					}
				}
				
				 this.reDrawLineEGC=false;
				 firstNodeSelected=false;
				 this.typeNodedraw=undefined;
				 this.delet();
				 if(this.selection!=undefined) {
					 this.insideNode()
					this.unSelectedNode();
					}

				 this.draw();
					
				 return;
				
			}
			else{
				this.insideHandlesNode();
				
				if (!firstNodeSelected) {
					this.drawEdges=false;
					this.unSelectOperation();
					 return;
						
				}
			}
		}
		
		this.draw();
		//this.isSelectionMultiples=false;
	}
	if(this.typeNodedraw==undefined){
		if(this.isSelectionMultiples) return;
		
		 if (this.selection!=undefined &&this.selection.group =="EDGE" && !this.selection.simpleLine && this.selection.insideHandleMiddle(currentCoordinate.x,currentCoordinate.y) )
		 {	this.selection.selec=true;
		
		 }
		
		 else if(isLineDraw&&this.selection!=undefined &&this.drawEdges==false && this.selection.group=="EDGE" && (this.selection.insideHandleExtreme(currentCoordinate.x,currentCoordinate.y))){
			if(this.selection.simpleLine){
			
			 var node;
			this.typeNodedraw=this.selection.type;
			this.selection.handleSelected==2?node=this.selection.source:node=this.selection.target;
			//this.deleteEdge(this.selection);
			this.edges.remove(this.selection);
			firstNodeSelected=true;
			this.nodeInicialSelc=node;
			this.drawEdges=true;
			}
			else{
				if(this.selection.handleSelected==0){
					this.dragoffx = currentCoordinate.x - this.selection.xI;
					this.dragoffy = currentCoordinate.y- this.selection.yI;
				}
				else if(this.selection.handleSelected==2){
					this.dragoffx = currentCoordinate.x - this.selection.xF;
					this.dragoffy = currentCoordinate.y- this.selection.yF;
					
				}
				this.reDrawLineEGC=true;
				this.typeNodedraw =this.selection.type;
				return;
			}
		}
		
		else {
			if (this.selection != undefined) {
				if (this.selection.group == "Node"){
					this.unSelectedNode();
				}
				else{
					this.unSelectedEdge();
					this.insideNode();
				}
			}
		}
	}
	 if (this.selection==undefined ) {
		 
		this.insideEdge();
	}
	 if(!this.isSelectionMultiples && this.selection == undefined){
			this.unSelectedNode();
			this.listOfSelected=new Array();
		}
			
	this.draw();
	
}

Graph.prototype.dblclick = function(event) {
	if(this.menuAtive == 2) return;
	 cursorX = event.pageX;
     cursorY = event.pageY;
	if(this.selection!=undefined){
		if(this.selection.group=="EDGE"){
			if(this.selection.simpleLine){
				this.selection.simpleLine=false
			}
			else 
				this.selection.simpleLine=true;
			this.draw();
		}
			//this.showContextMenu(cursorX , cursorY, 2)
		else{
			this.showContextMenu(cursorX , cursorY  +this.selection.height/2, 2)
			this.showProperties();
		}
	}
}

Graph.prototype.mouseup = function(event) {
	currentCoordinate = windowToCanvas(this.canvas,event);
	this.dragging = false;
	this.allNodes=[];
	
	if (this.resizeDragging) {
		this.resizeDragging = false;
		this.selection=this.updateSizeEdge(this.selection);
	}

    this.expectResize = -1;
    this.moveSelectionMultiples=false;
    if(this.selection)this.selection.selec=false;
	this.drawCanvasSelectionRectangleMove(this.ctx);
	if(this.drawSelectionRectangle){
		this.selectNodeInsideSelectionRectangle();
	}
	this.draw();
}

Graph.prototype.mousedown = function(event) {
	var coord = windowToCanvas(this.canvas, event);
	this.event=event;
	var mx = coord.x;
	var my = coord.y;
	//this.hideContextMenu();

	if (this.isSelectionMultiples
			&& this.insideCanvasSelectionRectangle(mx, my)) {

		if (!event.ctrlKey) {
			this.moveSelectionMultiples = true;
			this.dragoffx = mx - this.selectionRectangle[0];
			this.dragoffy = my - this.selectionRectangle[1];
			return;
		} else {
			this.unselectedCTRL(coord.x, coord.y);
			this.unSelectedNode()
			return;
		}
	} else {

		if (!event.ctrlKey ) {
			this.unselectSelectionMultiples();
		} else {
			this.selectedCTRL(event);
			return;
		}
	}
	
	
	if(this.selection!=undefined && this.selection.group=="EDGE"){
		if(this.selection.insideHandleExtreme(currentCoordinate.x,currentCoordinate.y))return;
		else if (this.selection.insideHandleMiddle(currentCoordinate.x,currentCoordinate.y) && !this.selection.simpleLine){
			{this.selection.selec=true;
			if(this.selection.handleSelected==1)
				this.dragoffy = my - this.selection.yM;
			else if (this.selection.handleSelected == 3)
				this.dragoffx = mx - this.selection.xMVet1;
			else if (this.selection.handleSelected == 4)
				this.dragoffx = mx - this.selection.xMVet2;
			this.resizeDragging=true;
				
			}
			return;
	}
return;
}
	if(this.selection==undefined && this.typeNodedraw==undefined && !this.isSelectionMultiples ){
		this.selectionRectangle[0]=currentCoordinate.x;
		this.selectionRectangle[1]=currentCoordinate.y;
		this.selectionRectangle[2]=currentCoordinate.x;
		this.selectionRectangle[3]=currentCoordinate.y;
		this.drawSelectionRectangle=true;
	}	
	
//	var l = this.nodes.length;
//	for (var i = l - 1; i >= 0; i--) {
//		if (this.nodes[i].contains(mx, my)) {
//			if(this.selection!=undefined)this.selection.unSelected();
//			this.nodes[i].selected();
//			var mySel = this.nodes[i];
//			this.dragoffx = mx - mySel.x;
//			this.dragoffy = my - mySel.y;
//			this.dragging = true;
//			this.selection = this.nodes[i];
//			this.valid = false;
//			return;
//		}
//	}
	
	
	//var l = this.nodes.length;
	//for (var i = l - 1; i >= 0; i--) {
	var node= this.insideVertices();	
	if (node!=undefined) {
			if(this.selection!=undefined)this.selection.unSelected();
			node.selected();
			//var mySel = this.nodes[i];
			this.dragoffx = mx - node.x;
			this.dragoffy = my - node.y;
			this.dragging = true;
			this.selection = node;
			this.valid = false;
			this.allNodes=this.nodes.getAll();
			return;
		}
	//}
	this.draw();
}

Graph.prototype.mousemove = function(event) {
	if (this.menuAtive != 0) return;
	
		currentCoordinate = windowToCanvas(this.canvas, event);
		document.body.style.cursor = "auto";
		if (this.dragging && !this.isSelectionMultiples) {
			this.draggingMove(event);
			this.checkcenter(this.ctx);
		}
		if (this.drawEdges) {
			this.showAnchors(event);
		}
		if (firstNodeSelected) {
			this.drawEdgePosCurrent(event);

		}
		
		if(this.reDrawLineEGC){
			
			var x=(currentCoordinate.x - this.dragoffx);
			var y=(currentCoordinate.y - this.dragoffy);
			this.selection.risize2(x,y);
			var node;
			this.selection.handleSelected==2?node=this.selection.source:node=this.selection.target;
			firstNodeSelected=true;
			this.nodeInicialSelc=this.selection.target;
			this.showAnchors(event);
			this.draw();
			if(this.insideNodeAnchors(event)){this.selection.color="blue";}
			else
				this.selection.color="red"
			return;
			
		}
		if (this.resizeDragging) {
			if (this.selection.handleSelected == 1)
				this.selection.risize(currentCoordinate.y - this.dragoffy);
			else
				this.selection.risize(currentCoordinate.x - this.dragoffx);
			
			this.draw();
		}
		
		
		

		if (this.selection == undefined && !this.drawEdges && this.isShowNodeInformation) {
			var node = this.insideVertices();
			if (node != undefined) {
				this.showNodeInformation(node);
				this.information = true;
			} else {
				if (this.information) {
					this.information = false;
					this.draw();
				}
			}

		}
		if(this.drawSelectionRectangle){
			this.selectionRectangle[2]=currentCoordinate.x;
			this.selectionRectangle[3]=currentCoordinate.y;
			this.drawCanvasSelectionRectangleMove(this.ctx);
		}
		
		if(this.isSelectionMultiples && this.moveSelectionMultiples){
			
			this.moveSelectionRectangle(currentCoordinate);
		}

		
	
	
	event.stopPropagation();
}

Graph.prototype.showNodeInformation=function(node){
		var properties = this.filterPropertiesNodeToContextMenu(node);
		delete properties.id;
		delete properties.color;
		delete properties.temporary;
		if(properties.type="relationship"){
			delete properties.idEdgeCardinalityUp;
			delete properties.idEdgeCardinalityDown;
			delete properties.idEdgeCardinalityLeft;
			delete properties.idEdgeCardinalityRight;
		}
		this.informationBox.drawInfo(node.x,node.y,properties);
}
Graph.prototype.checkcenter= function(ctx) {
	for(var n in this.allNodes){
		if(this.selection.isPositionCenterEqual(this.allNodes[n]) ){
			var n1=this.selection.getCenter();
			var n2=this.allNodes[n].getCenter();
			ctx.beginPath();
			ctx.moveTo(n1.x,n1.y);	
			ctx.lineTo(n2.x,n2.y);
			ctx.strokeStyle ="blue";
			ctx.lineWidth=1.5;
			ctx.stroke();
		}
	}
}
Graph.prototype.d= function(event) {
	if(firstNodeSelected&&!this.reDrawLineEGC){
		if(this.selection!=undefined &&this.selection.group=="EDGE"){
		//	this.deleteEdge(this.selection);
			//this.selection.unSelected();
		}
		
		this.draw();
		edgeAux={label:"",nodeI:0,handleI:0,nodeF:0,handleF:0,type:"line"};
		edgeAux.source=this.nodeInicialSelc;
		edgeAux.anchorSource=this.nodeInicialSelc.handleSelected;
		edgeAux.target=new SimpleNode(currentCoordinate.x, currentCoordinate.y,-1);
		edgeAux.anchorTarget=4;
		var edgeCurrent =this.creatEdges(this.typeNodedraw, edgeAux);
		//alert(JSON.stringify(edgeCurrent));
		edgeCurrent.showHandlesIniFin();
		edgeCurrent.selec=true;
		//edgeCurrent.handles[0].fillstyle="blue";
		if(this.insideNodeAnchors(event)){edgeCurrent.color="blue";}
		else edgeCurrent.color='#FF6633';
		edgeCurrent.draw(this.ctx);
	}
}

Graph.prototype.insideNodeAnchors= function(event) {
	currentCoordinate = windowToCanvas(this.canvas, event);
	var nodes=this.nodes.search(currentCoordinate.x, currentCoordinate.y)
	for(var n in nodes){
		if (nodes[n].insideAnchor2(currentCoordinate.x, currentCoordinate.y)){
			return true;
		}
	}
	return false ;
}
Graph.prototype.showAnchors= function(event) {
	 currentCoordinate = windowToCanvas(this.canvas, event);
	
	
	
	
	
	
	
	
	
//	
//	
//	for (var i = 0; i < l; i++) {
//	this.nodes[i].unSelected();
//	this.nodes[i].selectHanderEdge = false;
//	if (this.nodes[i].contains(currentCoordinate.x, currentCoordinate.y)) {
//		document.body.style.cursor = "crosshair";
//		
//		var typeN = this.nodes[i].type;
//		this.nodes[i].backgroundColor = "#3399FF";
//		if (this.typeNodedraw == "lineEGC"
//				&& (typeN == "entity" || typeN == "espGenCat")) {
//			this.nodes[i].selectHanderEdge = true;
//			this.nodes[i].backgroundColor = "#3399FF";
//		} else if (this.typeNodedraw == "line" ) {
//			var firtNoSelType = this.nodes[i].type;
//			if (firstNodeSelected && this.nodeInicialSelc.canBeconnectedTo(firtNoSelType)) {
//				this.nodes[i].selectHanderEdge = true;
//				
////				switch (firtNoSelType) {
////				case "entity":
////					if (this.nodeInicialSelc.type != "entity")
////						this.nodes[i].selectHanderEdge = true;
////					
////					break;
////				case "attribute":
////					if ((this.nodeInicialSelc.type == "espGenCat")
////							|| this.nodes[i].isConected()){
////						break;
////					}
////					this.nodes[i].selectHanderEdge = true;
////					break;
////				case "relationship":
////					if (this.nodeInicialSelc.type != "espGenCat")
////						this.nodes[i].selectHanderEdge = true;
////					break;
////				case "espGenCat":
////
////					if (this.nodeInicialSelc.type == "entity")
////						this.nodes[i].selectHanderEdge = true;
////					break;
////				}
//			}
//			else{
//				if(firtNoSelType=="attribute" && this.nodes[i].isConected()){
//					this.nodes[i].selectHanderEdge = false;
//					alert("in");
//				}
//				else
//					
//					if(!firstNodeSelected){
//						this.nodes[i].selectHanderEdge = true;
//					}
//			}
//		}
//		}
//	
//	this.draw();
//	
//	}
//	
//	
//	
	 
	 
	 if (this.selectShowAnchor!= undefined) {
		 this.selectShowAnchor.selectHanderEdge = false;
		 this.selectShowAnchor.unSelected();
	 }
	
	  this.selectShowAnchor=this.nodes.search(currentCoordinate.x, currentCoordinate.y)[0]
	
		if (this.selectShowAnchor!= undefined) {
			 this.selectShowAnchor.selectHanderEdge = false;
			 this.selectShowAnchor.unSelected();
			document.body.style.cursor = "crosshair";

			var typeN = this.selectShowAnchor.type;
			
			
			//this.selectionShowAnchor=nodes[i];
			this.selectShowAnchor.backgroundColor = "#3399FF";
			if (this.typeNodedraw == "lineEGC" && (typeN == "entity" || typeN == "espGenCat")) {
				this.selectShowAnchor.selectHanderEdge = true;
				this.selectShowAnchor.backgroundColor = "#3399FF";

			} else if (this.typeNodedraw == "line") {
				var firtNoSelType = this.selectShowAnchor.type;
				
				

				if (firstNodeSelected) {
					if(this.nodeInicialSelc.canBeconnectedTo(firtNoSelType))
						this.selectShowAnchor.selectHanderEdge = true;
					else
						this.selectShowAnchor.selectHanderEdge = false;
					/*switch (firtNoSelType) {
					case "entity":
						if (this.nodeInicialSelc.type != "entity") {
							this.selectShowAnchor.selectHanderEdge = true;
						}

						break;
					case "attribute":
						if ((this.nodeInicialSelc.type == "espGenCat")
								|| this.selectShowAnchor.isConected()) {
							break;
						}
						this.selectShowAnchor.selectHanderEdge = true;
						break;
					case "relationship":
						if (this.nodeInicialSelc.type != "espGenCat")
							this.selectShowAnchor.selectHanderEdge = true;
						break;
					case "espGenCat":

						if (this.nodeInicialSelc.type == "entity")
							this.selectShowAnchor.selectHanderEdge = true;
						break;
					}
				} else {*/
//					if (firtNoSelType == "attribute" && this.selectShowAnchor.isConected()) {
//						this.selectShowAnchor.selectHanderEdge = false;
//						alert("in");
//					} else
//						this.selectShowAnchor.selectHanderEdge = true;
				}
				else{
					this.selectShowAnchor.selectHanderEdge = true;
				
				}
			}
		
		
	}
		
	  
	  this.draw();
	
}

Graph.prototype.draggingMove= function(event) {
	currentCoordinate = windowToCanvas(this.canvas, event);
	var newX = currentCoordinate.x - this.dragoffx;
	var newY = currentCoordinate.y - this.dragoffy;
	//var node=this.selection;
	this.nodes.remove(this.selection);
	(newX<0)?this.selection.x=5:this.selection.x=newX;
	(newY<0)?this.selection.y=5:this.selection.y=newY;
	this.nodes.insert(this.selection);
	this.valid = false; // Something's dragging so we must redraw
	this.selection.updateHandles();
	document.body.style.cursor = "all-scroll";
	this.resizeCanvas();
	this.drawSelectionRectangle=false;
	this.draw();
}

Graph.prototype.chooseTypeEdge = function(type) {
	typeEdge=type;
	
	if (nodeSelected) {
		
		this.nodeInicialSelc.shape=typeEdge;
		this.draw();
	} else {
		alert("Edge not selected");
	}
}

Graph.prototype.addNode = function(node) {
	node.adjustLabelStart(this.ctx);
	this.nodes.insert(node);
	//this.insideNode();
};

Graph.prototype.addEdge = function(edge) {
	edge.updateSize();
	this.edges.insert(edge);
	//this.edges.push(edge);

};

Graph.prototype.draw = function() {
	
	this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
	
	if (this.gridVisible) {
		this.showGrid();

	}
	this.initShadows();
	var nodes=this.nodes.getAll();
	for ( var n in nodes)
		if (nodes[n].draw != undefined) {
			nodes[n].draw(this.ctx);
		}
	
	var edges=this.edges.getAll();
	for ( var e in edges) {
		edges[e].draw(this.ctx);
	}
	if(this.isSelectionMultiples){
		this.drawCanvasSelectionRectangle(this.ctx);
		for(var i in this.listOfSelected){
			this.listOfSelected[i].node.draw(this.ctx);
		}
	}
};

Graph.prototype.insideVertices = function() {
	
	var nodes=this.nodes.search(currentCoordinate.x, currentCoordinate.y)
	if (nodes.length>0) {
		return nodes.pop();
	}
	
	return undefined;
//var l = this.nodes.length;
//for (var i = 0; i < l; i++) {
//	
//	if (this.nodes[i].contains(currentCoordinate.x, currentCoordinate.y)) {
//		return i;
//	}
//}
	
	
	
return -1;
}

Graph.prototype.insideNode = function() {
	if(this.event.ctrlKey) return;	
	if(this.selection!=undefined && this.selection.group=="Node"){
		this.unSelectedNode();
	}
			
	var nodes=this.nodes.search(currentCoordinate.x, currentCoordinate.y);
		if (nodes.length>0) {
			this.selection = nodes.pop();
			this.selection.selected();
			this.cursorPosition= this.selection.label.length;
			this.selection.setCursorPosition(this.cursorPosition);
			nodeSelected = true;
			if(this.isSelectionMultiples)
				this.unselectSelectionMultiples();
			var edges = this.edges.getAll();
			if (this.selection != undefined && edges.length>0) {
			// update box edges conected
				this.edgesMoved = [];
				for ( var i in edges) {
					var edge = edges[i];
					if (edges[i].source.id == this.selection.id
							|| edges[i].target.id == this.selection.id) {
						this.edgesMoved.push(edges[i]);
					}
				}
		}
		
		}
}

Graph.prototype.insideHandlesNode = function() {
	//for (var i in this.selectShowAnchor ) {
		
		if (this.selectShowAnchor!=undefined && this.selectShowAnchor.insideAnchor(currentCoordinate.x, currentCoordinate.y)) {
			this.nodeInicialSelc = this.selectShowAnchor;
			firstNodeSelected = true;
			//break;
		}
	//}
}

Graph.prototype.insideEdge = function() {
	
	var edges=this.edges.search(currentCoordinate.x, currentCoordinate.y);
	for (var i in edges) {
		if (edges[i].contains(currentCoordinate.x, currentCoordinate.y)) {
			this.selection = edges[i];
			edges[i].selected();
			edgeSelected = true;
			return i;
		}
	}
	return -1;
}

Graph.prototype.selected = function() {
	var l = this.nodes.length;
	var l = this.edges.length;
	if (nodeSelected) {
		for (var i = 0; i < l; i++) {
			if (this.edges[i].insideHandle(currentCoordinate.x,
					currentCoordinate.y)) {
				this.redesign = true;
				this.nodeInicialSelc = this.edges[i];
			}
		}
	}
	
}

Graph.prototype.insideHandlesEdge = function() {
	var edges=this.edges.search(currentCoordinate.x, currentCoordinate.y)
	for (var i in edges) {
		if (edges[i].insideHandle(currentCoordinate.x, currentCoordinate.y)) {
			this.nodeInicialSelc = edges[i];
			nodeSelected = true;
			return true;
		}
	}
	return false;

}

//Graph.prototype.unSelected = function() {
//	var l = this.nodes.length;
//
//	for (var i = 0; i < l; i++) {
//		if (this.nodes[i].select) {
//			this.nodes[i].unSelected();
//			//alert("saiu");
//		}
//	}
//	var l = this.edges.length;
//	for (var i = 0; i < l; i++) {
//		
//		this.edges[i].unSelected();
//	}
//
//	//this.isResizeDrag = false;
//	this.redesign = false;
//	nodeSelected = false;
//	this.selection= undefined;
//	firstNodeSelected=false;
//	edgeSelected=false;
//	this.drawEdges=false;
//	alert(222);
//	this.insideNode();
//}

Graph.prototype.unSelectedNode = function() {
	
	
	if (this.selection != undefined && this.selection.group == "Node") {
		for ( var i in this.edgesMoved) {

			var edges2 = this.edges.search(this.edgesMoved[i].x,
					this.edgesMoved[i].y);
			for ( var e in edges2) {
				var edge = edges2[e];
				if (edge.source.id == this.selection.id
						|| edge.target.id == this.selection.id) {
					this.updateSizeEdge(edge);
				}
			}

		}
	}
	
	this.redesign = false;
	nodeSelected = false;
	if(this.selection!=undefined){
		//this.nodes.remove(this.selection);
		this.selection.unSelected();
		this.selection.selectHanderEdge=false;
		//this.nodes.insert(this.selection);
		this.selection= undefined;
	}
	
	firstNodeSelected=false;
	
	if(this.drawEdges)
		this.drawEdges=false;
	else{
	this.insideNode();
	}
	
}





Graph.prototype.unSelectedEdge = function() {
	if(this.selection!=undefined&&this.selection.group=="EDGE"){
		this.edges.remove(this.selection);
		this.selection.unSelected();
		this.edges.insert(this.selection);
		this.selection = undefined;
	}
	
	firstNodeSelected = false;
	edgeSelected = false;
	this.drawEdges = false;

}


Graph.prototype.selectedFinalNode = function(x,y) {
if(this.selectShowAnchor==undefined) return undefined;
		if (this.selectShowAnchor.insideAnchor(x,y)) {
			edgeAux = {};
			edgeAux.source = this.nodeInicialSelc;
			edgeAux.anchorSource = this.nodeInicialSelc.handleSelected;
			edgeAux.target = this.selectShowAnchor;
			edgeAux.anchorTarget = this.selectShowAnchor.handleSelected;
			edgeAux.id = this.generateID++;
			edgeAux.source.degreeOut++;
			edgeAux.target.degreeIn++;
			var edge=this.creatEdges(this.typeNodedraw, edgeAux);
			this.addEdge(edge);
			firstNodeSelected = false;
			this.selectShowAnchor.selectHanderEdge = false;
			return edge;

		} else {
			firstNodeSelected = false;
			this.unSelectedNode();

		}
//	}
	return undefined;

}

Graph.prototype.getNodeById = function(id) {
	for (var i = 0; i < this.nodes.length; i++) {
		if (this.nodes[i].id == id)
			return this.nodes[i];
	}
	return undefined;

}

Graph.prototype.resize = function() {
	var oldx = this.selection.x;
	var oldy = this.selection.y;
	var mx = currentCoordinate.x;
	var my = currentCoordinate.y;
	var sizeMin = 20;
	switch (this.expectResize) {
	case 0:
		this.selection.y = my;
		this.selection.height += oldy - my;
		document.body.style.cursor = "n-resize";
		break;
	case 1:
		this.selection.width = mx - oldx;
		document.body.style.cursor = "e-resize";
		break;
	case 2:
		this.selection.height = my - oldy;
		document.body.style.cursor = "n-resize";
		break;
	case 3:
		this.selection.x = mx;
		this.selection.width += (oldx - mx);
		document.body.style.cursor = "e-resize";

		break;
	case 4:
		this.selection.x = mx;
		this.selection.y = my;
		this.selection.width += oldx - mx;
		this.selection.height += oldy - my;
		document.body.style.cursor = "nw-resize";
		break;
	case 5:
		this.selection.y = my;
		this.selection.width = mx - oldx;
		this.selection.height += oldy - my;
		document.body.style.cursor = "ne-resize";
		break;
	case 6:
		this.selection.width = mx - oldx;
		this.selection.height = my - oldy;
		document.body.style.cursor = "nw-resize";
		break;
	case 7:
		this.selection.x = mx;
		this.selection.width += oldx - mx;
		this.selection.height = my - oldy;
		document.body.style.cursor = "ne-resize";
		break;

	}
	if (this.selection.width < sizeMin) {
		this.selection.width = sizeMin;
		this.selection.x = oldx
	}
	if (this.selection.height < sizeMin) {
		this.selection.height = sizeMin;
		this.selection.y = oldy
	}
	;

	this.valid = false;
	this.selection.updateHandles();

	this.draw();
}


Graph.prototype.typeCursorResize = function() {
	
	if (this.selection.insideHandle(currentCoordinate.x, currentCoordinate.y)) {
		this.expectResize = this.selection.handleSelected;
		switch (this.expectResize) {
		case 0:
			document.body.style.cursor = "n-resize";
			break;
		case 1:
			document.body.style.cursor = "e-resize";
			break;
		case 2:
			document.body.style.cursor = "n-resize";
			break;
		case 3:
			document.body.style.cursor = "e-resize";
			break;
		case 4:
			document.body.style.cursor = "nw-resize";
			break;
		case 5:
			document.body.style.cursor = "ne-resize";
			break;
		case 6:
			document.body.style.cursor = "nw-resize";
			break;
		case 7:
			document.body.style.cursor = "ne-resize";
			break;
		}
	}
}	

	

Graph.prototype.resizeEdge = function() {

	var edges=this.edges.search(currentCoordinate.x, currentCoordinate.y);
	
	for (var i in edges) {

		var mx = currentCoordinate.x;
		var my = currentCoordinate.y;
		if (edges[i].select) {
			this.nodeInicialSelc.xM = mx;
			this.nodeInicialSelc.yM = my;
		}
	}
	this.valid = false;
	this.draw();

}
Graph.prototype.showGrid = function() {

	var l = this.canvas.height / 10;
	for (var i = 0; i < l; i++) {
		this.ctx.beginPath();
		this.initShadows();
		this.ctx.moveTo(0, i * 20);
		this.ctx.lineTo(this.canvas.width, i * 20);
		this.ctx.strokeStyle = '#c0c0c0';
		;
		this.ctx.lineWidth = 1;
		this.ctx.stroke();

	}
	var l = this.canvas.width / 10
	for (var i = 0; i < l; i++) {
		this.ctx.beginPath();
		this.initShadows();
		this.ctx.moveTo(i * 20, 0);
		this.ctx.lineTo(i * 20, this.canvas.height);
		this.ctx.strokeStyle = '#c0c0c0';
		;
		this.ctx.lineWidth = 1;
		this.ctx.stroke();
	}

}
Graph.prototype.grid = function(value) {
	
	this.gridVisible = value;
	this.draw();
}

Graph.prototype.err= new Language( "err", ["attribute","entity"],[]);

Graph.prototype.setLanguage = function(language) {

	if (language == "err")
		this.language = this.err;
	else {
		this.language = language;
	}
}

Graph.prototype.delet = function() {
	if (this.isSelectionMultiples) {
		for ( var a in this.listOfSelected) {
			this.deleteNode(this.listOfSelected[a].node);
			
		}
		this.listOfSelected=[];
		this.unselectSelectionMultiples();
		this.draw();
		return;
	}
	else if (nodeSelected) {
		//this.selection.unSelected();
		//this.nodes.remove(this.selection);
		this.deleteNode(this.selection);
		nodeSelected = false;

	} else if (edgeSelected) {
		this.deleteEdge(this.selection);
	} else {
		alert("Node or Edge not selected");
	}
	
	this.hideContextMenu();
	this.menuAtive=0;
	this.isSelectionMultiples=false;
	this.draw();
}

Graph.prototype.deleteNode = function(node) {
	var listEdgeDelete = new Array();
	var index;
	var edges=this.edges.getAll();
			for ( var k in edges) {
				if (node.id == edges[k].source.id
						|| node.id == edges[k].target.id) {
					listEdgeDelete.push(edges[k]);
				}
			}

	for ( var i in listEdgeDelete) {
		//this.deleteEdge(listEdgeDelete[i])
		this.edges.remove(listEdgeDelete[i]);
	}
	this.nodes.remove(node);
	this.selection=undefined;
	
}
	
Graph.prototype.deleteEdge= function(edge){
		edge.source.degreeOut--;
		edge.target.degreeIn--;
//		var edges=this.edges.getAll();
//		for (var r in edges)
//			if (edge === edges[r]) {
//				this.edges.splice(r, 1);
//			}
//		return;
		this.unSelectedEdge();
		this.edges.remove(edge);
	}
Graph.prototype.clearGraph = function() {
	nodeSelected=false;
	this.selection=undefined;
	this.nodes=new QuadTree(0,0,this.WIDTH,this.HEIGHT);
	this.edges=new QuadTree(0,0,this.WIDTH,this.HEIGHT);
	this.unselectSelectionMultiples()
	this.draw();
}
Graph.prototype.getNodeSelected = function() {
	return this.selection;
}

Graph.prototype.changeNodeSelectedValue = function(object) {
			this.selection.label=object.label;
			this.selection.id=object.id;	
			this.selection.type=object.type;
			this.selection.x=parseInt(object.x);
			this.selection.y=parseInt(object.y);
			this.selection.color=object.color;
			this.selection.backgroundColor="transparent";
			
			if(object.type=="relationship" ){
				this.selection.setIdentifier(object.identifier);
				
			}
			else if(object.type=="attribute"){
				this.selection.setDerived(object.derived);
				this.selection.setMultivalued(object.multivalued);
				this.selection.setKey(object.key); 
				this.selection.setWeakKey(object.weakKey); 
			}
			else if(object.type=="entity"){
				this.selection.setWeak(object.weak);
				
			}
			this.draw();	
}

Graph.prototype.changeEdgeSelectedValue = function(object) {

	if(this.checkAnchorSource(JSON.parse(object.anchorSource))){
			this.selection.AI=JSON.parse(object.anchorSource);
	}
	
	if(this.checkAnchorSource(JSON.parse(object.anchorTarget)))
			this.selection.AF=JSON.parse(object.anchorTarget);
	
	this.selection.color = object.color;
	this.selection.simpleLine = JSON.parse(object.simpleLine);
	
	if(this.selection.hasOwnProperty('total')){
		this.selection.total = JSON.parse(object.total);
	}
	if(this.selection.hasOwnProperty('cardinality')){
		this.selection.updateCardinality(object.cardinality);
	}
	this.draw();
}

Graph.prototype.copyValueNode = function(nodeSource) {
	//if(nodeSource==undefined)
	
	var node = this.createNodes(nodeSource.type,(nodeSource.x+ nodeSource.width/2),(nodeSource.y+nodeSource.height+10),this.generateID++)
	node.label=nodeSource.label;
	node.color=nodeSource.color;
	node.backgroundColor="transparent";
	
	if(nodeSource.type=="relationship" ){
		node.setIdentifier(nodeSource.identifier);

	}
	else if(nodeSource.type=="attribute"){
		node.setDerived(nodeSource.derived);
		node.setMultivalued(nodeSource.multivalued);
		node.setKey(nodeSource.key); 
		node.setWeakKey(nodeSource.weakKey); 
	}
	else if(nodeSource.type=="entity"){
		node.setWeak(nodeSource.weak);
		
	}
	node.adjustLabelStart(this.ctx);
	return node;	
}

Graph.prototype.isTypeNodedrawDefined= function(){
	return (this.typeNodedraw!=undefined)?true:false;
}

Graph.prototype.setDrawEdges= function(value){
	this.drawEdges=value;
	
}
Graph.prototype.getWidth= function(){
	return this.WIDTH;
	
}
Graph.prototype.getHeight= function(){
	return this.HEIGHT;
}

Graph.prototype.resizeCanvas = function() {
	var adjust=false;
	if (this.selection != undefined) {

		if (this.selection.x + this.selection.width > this.canvas.width) {
			this.canvas.width += this.selection.width+200;
			adjust=true;
		}

		if (this.selection.y + this.selection.height > this.canvas.height) {
			this.canvas.height += this.selection.height+200;
			adjust=true;
		}

	}
	if(adjust){
		this.canvasSizeReset();
		
	}
}


Graph.prototype.resizeGraph = function(w,h) {
	(w<400)?w=400:w=w;
	(h<400)?h=400:h=h;
	
	this.HEIGHT=h;
	this.WIDTH=w;
	
	this.div.style.width=w+"px";
	this.div.style.height=h+"px";
	
	if (this.positionHorizontal) {
		this.graphEditor.style.width = w+"px";
		this.graphEditor.style.height= this.HEIGHT-this.option.offsetHeight*1.5+"px";
		this.setPositionHorizontal();
	}
	else {
		this.graphEditor.style.width = this.WIDTH-this.option.offsetWidth*1.5+"px";
		this.graphEditor.style.height= h*0.95+"px";
	}
	var that = this;
	setTimeout(function(){
		that.canvas.width = that.graphEditor.clientWidth;
		that.canvas.height = that.graphEditor.clientHeight-5;
	
		that.draw();
	}, 200);
	
}


Graph.prototype.setGrid = function(value) {

	this.grid(value);
}

Graph.prototype.showOption = function(value) {
	if (value) {
		this.option.style.visibility = 'visible';
	} else {
		this.option.style.visibility = 'hidden';
	}
}




Graph.prototype.setPositionHorizontal=function() {
		this.div.style.cssFloat = "left";
		//var languageOpt = this.language.getNodesEdges();
		for(var i in this.languageOptionCanvas){
//			document.getElementById(languageOpt[i]).style.display="inline";
//			document.getElementById(languageOpt[i]).style.marginLeft = "5px";
			this.languageOptionCanvas[i].style.display="inline";
			this.languageOptionCanvas[i].style.marginLeft = "5px";
		}
		this.graphEditor.style.height= (this.HEIGHT-(this.option.offsetHeight*1.4))+"px";
		this.graphEditor.style.width = this.WIDTH*0.97+"px";
		this.draw();
		this.positionHorizontal=true;
}
Graph.prototype.setPositionVertical=function() {
		this.div.style.cssFloat = "none";
		//var languageOpt = this.language.getNodesEdges();
		
		//	this.graphEditor.style.width = this.WIDTH-this.option.offsetHeight*1.5+"px";
		for(var i in this.languageOptionCanvas){
			this.languageOptionCanvas[i].style.display="block"; 
			this.languageOptionCanvas[i].style.marginTop = "5px";
		}
		this.graphEditor.style.width = this.WIDTH-this.option.offsetWidth*1.5+"px";
		this.graphEditor.style.height=(this.HEIGHT*0.95)+"px";
		if(this.canvas.height<this.graphEditor.offsetHeight)
			this.canvas.height=this.HEIGHT*0.95;
		
		this.positionHorizontal=false;
		this.draw();
}

Graph.prototype.drawCanvasSelectionRectangleMove= function(ctx) {
	if(this.selectionRectangle[0]== this.selectionRectangle[2] &&
			this.selectionRectangle[1]==this.selectionRectangle[3] ) return;
	var xStart=Math.min(this.selectionRectangle[0],this.selectionRectangle[2]);
	var yStart=Math.min(this.selectionRectangle[1],this.selectionRectangle[3]);
	var xEnd=Math.max(this.selectionRectangle[0],this.selectionRectangle[2]);
	var yEnd=Math.max(this.selectionRectangle[1],this.selectionRectangle[3]);
	this.draw();
	ctx.save();
	ctx.globalAlpha=0.1;
	ctx.beginPath();
	ctx.strokeStyle = "AAAAAA";
	ctx.rect(xStart, yStart, (xEnd-xStart), (yEnd-yStart));
	ctx.fillStyle = "blue";
	ctx.lineWidth=1;
	ctx.fill();
	ctx.stroke();
	ctx.restore();
	
	
}



Graph.prototype.adjustCanvasSelectionRectangle= function(ctx) {
	
	var xStart=100000;
	var yStart=100000;
	var xEnd=0;
	var yEnd=0;
	this.draw();
	
	for(var i in this.listOfSelected){
		var node=this.listOfSelected[i].node;
		
		if(node.x<xStart)
			xStart=node.x;
		if(node.y<yStart)
			yStart=node.y;
		if(node.x+node.width>xEnd)
			xEnd=node.x+node.width;
		if(node.y+node.height>yEnd)
			yEnd=node.y+node.height;
	}

	this.selectionRectangle[0]=xStart;
	this.selectionRectangle[1]=yStart;
	this.selectionRectangle[2]=xEnd;
	this.selectionRectangle[3]=yEnd;
	
	for(var i in this.listOfSelected){
		this.listOfSelected[i].dx=this.listOfSelected[i].node.x-this.selectionRectangle[0];
		this.listOfSelected[i].dy=this.listOfSelected[i].node.y-this.selectionRectangle[1];
	}
}


Graph.prototype.clickSelectedFinalNode = function() {

	var edge = this.selectedFinalNode(currentCoordinate.x, currentCoordinate.y);
	isLineDraw = false;
	if (this.selection != undefined && this.selection.group == "EDGE")
		this.deleteEdge(this.selection)
	this.unSelectOperation();
	if (edge != undefined) {
		this.selection = edge;
		this.selection.selected();
		edgeSelected = true;
		this.selection.source.backgroundColor = '#B8B8B8';
		this.selection.target.backgroundColor = '#B8B8B8';
		this.selection.source.unSelected();
		this.selection.target.unSelected();
	}

	this.draw();
}


Graph.prototype.drawCanvasSelectionRectangle= function(ctx) {
	ctx.save();
	//ctx.globalAlpha=0.1;
	ctx.beginPath();
	ctx.strokeStyle ="blue";
	ctx.rect(this.selectionRectangle[0] - 5+0.5, this.selectionRectangle[1] - 5+0.5,
			(this.selectionRectangle[2] - this.selectionRectangle[0] + 10),
			(this.selectionRectangle[3] - this.selectionRectangle[1] + 10));
	ctx.fillStyle =  "transparent";
	ctx.lineWidth=1;
	ctx.fill();
	ctx.stroke();
	ctx.restore();
	
}

Graph.prototype.unselectSelectionMultiples=function(){
	if(!this.isSelectionMultiples) return;
	this.isSelectionMultiples=false;
	this.selection=undefined;
	this.selectionRectangle=[];
	
	for(var i in this.listOfSelected){
		var node=this.listOfSelected[i].node;
		node.unSelected();
		this.nodes.insert(node)
		this.unSelectedNodeRectangle(node);
	}
	
//	for(var i in this.listOfSelected){
//		
//		console.log(this.NodeCopy.multipleNode);	
//	}
	
	this.listOfSelected=[];
}

Graph.prototype.addNodeSelectSelectionMultiples=function(nod){
	nod.selected();
	var EstrNode = {
			dx: 0,
			dy: 0,
			node:nod,
			coorInitial:this.getCoorNode(nod)
	};
	this.listOfSelected.push(EstrNode);
	this.isSelectionMultiples=true;
	//this.firstNodeSelected=false;
	this.nodes.remove(nod);
	
	
}


Graph.prototype.selectNodeInsideSelectionRectangle = function() {
	

	if (this.selectionRectangle[0] == this.selectionRectangle[2]) {
		this.drawSelectionRectangle = false;
		return;
	}
	
	var xMin=Math.min(this.selectionRectangle[0],this.selectionRectangle[2]);
	var yMin=Math.min(this.selectionRectangle[1],this.selectionRectangle[3]);
	var xMax=Math.max(this.selectionRectangle[0],this.selectionRectangle[2]);
	var yMax=Math.max(this.selectionRectangle[1],this.selectionRectangle[3]);
	
	//var nodeSelected=this.nodes.locate(xMin, yMin,xMax,yMax);
	
	var nodeSelected=this.nodes.getAll();	
	for ( var i in nodeSelected) {
		var x=nodeSelected[i].x;
		var y=nodeSelected[i].y;
		var width=nodeSelected[i].width;
		var height=nodeSelected[i].height;
		if(((x >= xMin && x <= xMax) || (x+width >= xMin && x+width <= xMax)  ||  
				(x+width <= xMin && x+width >= xMax) || (x <= xMin && x >= xMax) ) &&  
				((y >= yMin && y <= yMax) ||(y +height>= yMin && y+height <= yMax) ||
				(y +height<= yMin && y+height >= yMax)|| (y<= yMin && y>= yMax)))
			this.addNodeSelectSelectionMultiples(nodeSelected[i])
	}
	

	//console.log(this.selectionRectangle[0]+" "+ this.selectionRectangle[1]+" "+ this.selectionRectangle[2]+" "+this.selectionRectangle[3])
	
	this.drawSelectionRectangle = false;
	if (this.isSelectionMultiples) {
		this.adjustCanvasSelectionRectangle();
		for ( var i in this.listOfSelected) {
			this.listOfSelected[i].dx = this.listOfSelected[i].node.x
					- this.selectionRectangle[0];
			this.listOfSelected[i].dy = this.listOfSelected[i].node.y
					- this.selectionRectangle[1];
		}
	}
}

Graph.prototype.positionInListOfSelected = function(x,y){
	for(var i in this.listOfSelected){
		if(this.listOfSelected[i].node.contains(x,y)) return i;
		
	}
	return -1;
	
}
Graph.prototype.insideCanvasSelectionRectangle = function(px, py) {
	var width = this.selectionRectangle[2] - this.selectionRectangle[0];
	var height = this.selectionRectangle[3] - this.selectionRectangle[1];
	return (this.selectionRectangle[0] <= px)
			&& (this.selectionRectangle[0] + width + 10 >= px)
			&& (this.selectionRectangle[1] <= py)
			&& (this.selectionRectangle[1] + height + 10 >= py)
}


Graph.prototype.moveSelectionRectangle = function(currentCoordinate) {
	document.body.style.cursor = "all-scroll";
	var x = (currentCoordinate.x - this.dragoffx);
	var y = (currentCoordinate.y - this.dragoffy);
	
	var width = this.selectionRectangle[2] - this.selectionRectangle[0];
	var height = this.selectionRectangle[3] - this.selectionRectangle[1];
	this.selectionRectangle[0] = x;
	this.selectionRectangle[1] = y;

	this.selectionRectangle[2] = this.selectionRectangle[0] + width;
	this.selectionRectangle[3] = this.selectionRectangle[1] + height;

	for ( var i in this.listOfSelected) {
		//this.nodes.remove(this.listOfSelected[i].node);
		this.listOfSelected[i].node.x = x + this.listOfSelected[i].dx;
		this.listOfSelected[i].node.y = y + this.listOfSelected[i].dy;
		//this.nodes.insert(this.listOfSelected[i].node);
	}
	
	this.adjustSizeCanvasRectangleSelection(this.selectionRectangle);
	this.drawCanvasSelectionRectangleMove(this.ctx);

}

Graph.prototype.selectedALL = function() {
	nodeSelected=false;
	var nodes=this.nodes.getAll();
	for(var i in nodes)
		this.addNodeSelectSelectionMultiples(nodes[i]);
	this.adjustCanvasSelectionRectangle(this.ctx);
	this.draw();
}

Graph.prototype.selectedCTRL = function(event) {
		var node =this.insideVertices();
		if (node != undefined) {
			if(this.selection!=undefined){
				this.addNodeSelectSelectionMultiples(this.selection);
				this.selection=undefined;
			}
			this.addNodeSelectSelectionMultiples(node);
			this.adjustCanvasSelectionRectangle(this.ctx);
			return;
	}
}
Graph.prototype.unselectedCTRL = function(x,y) {			
	var nodeIndex = this.positionInListOfSelected(x,y);
	if(nodeIndex!=-1 && this.event.ctrlKey){
		var id=this.listOfSelected[nodeIndex].node.id;
		//this.nodes[this.getIndexNode(id)].unSelected();
		var node=this.nodes.search(x, y)[0];
		if(node!=undefined)
			node.unSelected();
		this.listOfSelected.splice(nodeIndex, 1);
		if(this.listOfSelected.length==0)
			this.isSelectionMultiples=false;
		else
		this.adjustCanvasSelectionRectangle(this.ctx);
}
	this.draw();
	
}


Graph.prototype.copy_paste = function() {
	if(this.isSelectionMultiples && this.NodeCopy.multipleNode || this.NodeCopy.multipleNode){
//		for(var i in this.listOfSelected)
//			this.listOfSelected[i].node.unSelected();
//		this.unSelectedEdge();
		this.pasteSelectedRectangle();
	}
	else{
		
//			if(this.NodeCopy.multipleNode){
//				//this.unSelectedNode();
//				//this.unSelectedEdge();
//			//	this.pasteSelectedRectangle();
//				return;
//			}
		currentCoordinate.x=0;
		currentCoordinate.y=0;
		var nodeSource=this.NodeCopy.node;
		var node=this.copyValueNode(nodeSource)	
		this.unSelectedNode();
		//this.unSelectedEdge();
		this.selection = node;
		this.selection.selected();
		this.cursorPosition= this.selection.label.length;
		this.selection.setCursorPosition(this.cursorPosition);
		nodeSelected = true;
		this.selection.label=this.NodeCopy.label;
		if(!this.NodeCopy.nodeDeleted){
			this.selection.x-=this.selection.width/2;
			this.selection.y-=this.selection.height+10;
		}
		this.addNode(this.selection);
		this.isSelectionMultiples=false;
	}
}
Graph.prototype.pasteSelectedRectangle = function(){
	if(this.selection!=undefined){
	currentCoordinate.x=0;
	if(nodeSelected&&this.selection.group=="Node")
		this.unSelectedNode();
	else if(this.selection.group=="EDGE")
		this.unSelectedEdge();
}
	
	var listNode =this.NodeCopy.listNode;
	var selectedArea=this.NodeCopy.seletionArray;
	
	
	if(this.NodeCopy.multipleNode){
		this.unselectSelectionMultiples();
	this.selectionRectangle[0]= selectedArea[0];
	this.selectionRectangle[1]= selectedArea[1];
	this.selectionRectangle[2]= selectedArea[2];
	this.selectionRectangle[3]= selectedArea[3];
	}
	
	var width=selectedArea[2]-selectedArea[0]+10;
	var height=selectedArea[3]-selectedArea[1]+10;
	var listNodeSelected=new Array();
	var listNodeToUpdateCanvasSize=[];
		for(var i in listNode){
			var node=this.copyValueNode(listNode[i].node);
			node.x=listNode[i].node.x+width ;
			node.y=listNode[i].node.y+height ;
			node.selected();
			
			var EstrNode = {
					dx: listNode[i].dx,
					dy: listNode[i].dy,
					node:node
			};
			listNodeSelected.push(EstrNode);
		}
		
		this.listOfSelected=[];
		this.listOfSelected=listNodeSelected;
//	}
	
	
//	if(this.selection!=undefined){
//		currentCoordinate.x=0;
//		if(nodeSelected&&this.selection.group=="Node")
//			this.unSelectedNode();
//		else if(this.selection.group=="EDGE")
//			this.unSelectedEdge();
//	}
	
	this.isSelectionMultiples=true;
}

//	else{
//		
//		for(var i in listNode){
//			var node=listNode[i].node;
//			node.x=listNode[i].node.x + width;
//			node.y=listNode[i].node.y + height;
//			node.selected();
//			var EstrNode = {
//					dx: listNode.dx,
//					dy: listNode.dy,
//					node:node,
//					coorInitial:this.getCoorNode()
//			};
//			listNodeSelected.push(EstrNode);
//		}
//		
//		this.isSelectionMultiples=true;
//		
////		for(var i in listNodeSelected){
////			this.nodes.push(listNodeSelected[i].node);
////		}
//		
//		this.listOfSelected=[];
//		this.listOfSelected=listNodeSelected;
//		
//		this.adjustCanvasSelectionRectangle(this.ctx);
//		
//		return;
//	}
//	console.log(this.nodes.getAll().length+"after2")
//	
//	
////	for(var i in listNodeSelected){
////		this.nodes.insert(listNodeSelected[i].node);
////	}
	
//}

Graph.prototype.copy = function(event) {
	if (this.isSelectionMultiples) {
		var width=this.selectionRectangle[2]-this.selectionRectangle[0]+10;
		var height=this.selectionRectangle[3]-this.selectionRectangle[1]+10;

	var zoneSelected={}
	zoneSelected[0]= this.selectionRectangle[0]+width;
	zoneSelected[1]= this.selectionRectangle[1]+height;
	zoneSelected[2]= this.selectionRectangle[2]+width;
	zoneSelected[3]= this.selectionRectangle[3]+height;
	var listCopy=[];
	for(var i in this.listOfSelected){
		//var node=this.copyValueNode(this.listOfSelected[i].node);
	//	this.listOfSelected[i].node=node;
		var node=this.copyValueNode(this.listOfSelected[i].node);
		node.x=this.listOfSelected[i].node.x;
		node.y=this.listOfSelected[i].node.y;
		
		var EstrNodeCopy = {
				dx: this.listOfSelected[i].dx,
				dy: this.listOfSelected[i].dy,
				node:node,
		};
		listCopy.push(EstrNodeCopy);
	}
		
		var selectionEstr = {
			listNode : listCopy,
			seletionArray : zoneSelected,
			nodeDeleted:false,
			multipleNode:true,
		};
		
		this.NodeCopy=selectionEstr;
	}
	else{
		
		if (nodeSelected && (!event || event.ctrlKey) && this.selection.group=="Node" ) {
			this.NodeCopy={
					node: this.copyValueNode(this.selection), 
					label:this.selection.label,
					nodeDeleted:false,
					multipleNode:false,
			}
		}
		
	}
	
}

Graph.prototype.cut = function(event) {
	
	 if (this.isSelectionMultiples) {
	 return;
	 }
	 /*
	if (this.isSelectionMultiples) {
		var width=this.selectionRectangle[2]-this.selectionRectangle[0]+10;
		var height=this.selectionRectangle[3]-this.selectionRectangle[1]+10;

	var zoneSelected={}
	zoneSelected[0]= this.selectionRectangle[0]+width;
	zoneSelected[1]= this.selectionRectangle[1]+height;
	zoneSelected[2]= this.selectionRectangle[2]+width;
	zoneSelected[3]= this.selectionRectangle[3]+height;
	var listN= new Array() ;
	for(var i in this.listOfSelected){
		console.log(this.listOfSelected[i].node.id)
		var n=this.copyValueNode(this.listOfSelected[i].node);
		var EstrNode = {
				dx: this.listOfSelected[i].dx,
				dy: this.listOfSelected[i].dy,
				node:n
		};
		listN.push(EstrNode);
	}
	
		var selectionEstr = {
			listNode : listN,
			seletionArray : zoneSelected,
			nodeDeleted:true,
			multipleNode:true,
		};
		
		this.NodeCopy=selectionEstr;
		this.delet();
	
	}
	else {
	*/	
	if (event.ctrlKey && nodeSelected && this.selection.group=="Node") {
		this.NodeCopy={
				node:this.copyValueNode(this.selection),
				label:this.selection.label,
				nodeDeleted:true,
				multipleNode:false,
		}
		this.delet();
		
	}
	//}
	
}

Graph.prototype.paste = function(event) {
	if (this.NodeCopy!=undefined && (!event || event.ctrlKey)) {
		//if(this.isSelectionMultiples && this.NodeCopy.nodeDeleted) return;
		this.copy_paste();
		//this.adjustSizeCanvasGraph(this.nodes.getAll());
		this.draw();
	}
	
}

Graph.prototype.updateSizeEdge = function(edge) {
	
	this.edges.remove(edge);
	edge.updateSize();
	this.edges.insert(edge);
	return edge; 

}





Graph.prototype.unSelectedNodeRectangle = function(node) {
	
	var edges2 = this.edges.getAll();
			for ( var e in edges2) {
				var edge = edges2[e];
				if (edge.source.id == node.id
						|| edge.target.id == node.id) {
					this.updateSizeEdge(edge);
				}
			}
		node.unSelected();
}

Graph.prototype.adjustSizeCanvasRectangleSelection = function(rectangle) {
	var width=this.canvas.width;
	var height=this.canvas.height;
	var adjust=false;
	if (rectangle[2] >= width) {
		width = (rectangle[2] + 300);
		adjust=true;
	}
	if (rectangle[3] >= height) {
		height = (rectangle[3] + 300);
		adjust=true;
	}
	this.canvas.width = width;
	this.canvas.height = height;
	if(adjust){
		this.canvasSizeReset();
	}
}


Graph.prototype.canvasSizeReset = function() {
	var nodes=this.nodes.getAll();
	var edges=this.edges.getAll();
	this.nodes=new QuadTree(0,0,this.canvas.width,this.canvas.height);
	this.edges=new QuadTree(0,0,this.canvas.width,this.canvas.height);
	for(var i in nodes)
		this.nodes.insert(nodes[i]);
	for(var i in edges)
		this.edges.insert(edges[i]);
}
Graph.prototype.getPositionHorizontal = function() {
	return this.positionHorizontal;
}

Graph.prototype.getTypeNodedrawLook = function() {
	return this.typeNodedrawLook;
}

Graph.prototype.setTypeNodedrawLook = function(value) {
	this.typeNodedrawLook=value;
}
Graph.prototype.setShowNodeInfo = function(value) {
	this.isShowNodeInformation=value;
}
Graph.prototype.getCoorNode = function(node) {
	var coorNode={x:node.x,y:node.y,width:node.width,height:node.height,id:node.id}
	return coorNode;

}

Graph.prototype.checkAnchorSource = function(anchor) {
	if(anchor==1|| anchor==2||anchor==3||anchor==4)
		return true;
	
	return false;;	
	
}



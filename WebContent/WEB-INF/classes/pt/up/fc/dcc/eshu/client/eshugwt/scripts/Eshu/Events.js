

Eshu.prototype.mousedowElementSelected=false;
Eshu.prototype.pointsEdge=[];
Eshu.prototype.dragoffx = 0; // See edown and mousemove events for // //								// explanation
Eshu.prototype.dragoffy = 0;
Eshu.prototype.structCopy; // 1 node; 2 rectangleSelection
Eshu.prototype.positionSelected={move:false,x:0,y:0};
//Eshu.prototype.lineCurrentPosition=new Line();
Eshu.prototype.typeEdgeCreate; //use while create path of edge
Eshu.prototype.nodeMarked;


Eshu.prototype.click = function(event) {
//	if(this.graph.isNodeSelection() ){
//		//console.log(this.graph.selection.insideArcRotation(currentCoordinate.x , currentCoordinate.y));
//		var xA=this.x+this.width/2;
//		var yA=this.y-20;
//		console.log(Math.atan2(this.graph.selection.y - currentCoordinate.y, this.graph.selection.x - currentCoordinate.x));
//	}
	//currentCoordinate = windowToCanvas(this.canvas, event);
//	var nodes=this.graph.getAllNodes();
//	for(var i in nodes)
//		console.log(nodes[i].x + " "+ nodes[i].y + " "+nodes[i].type)
//		console.log(currentCoordinate.x + " " + currentCoordinate.y );
	if(this.graph.isNodeSelection() && this.graph.selection.existContainer()){
		this.graph.selection.insideListTextBox(currentCoordinate.x,currentCoordinate.y);
		this.draw();
		//this.graph.selection.addOperation();
	}
	
	//var expression=/ df/;
	//console.log(/+|#\w/.test("+aa"));
}
	
Eshu.prototype.mousedown = function(event) {
		if(this.graphState==8){ 
			this.mousedownHideContextMenu();
			return;
		}
		
		currentCoordinate = windowToCanvas(this.canvas, event);
		var mx = currentCoordinate.x;
		var my = currentCoordinate.y;
		if(this.elementDraw!=undefined){
			this.createElement();
			return;
	}
		
		if(this.graph.isNodeSelection()  && this.graph.selection.insideArcRotation(currentCoordinate.x , currentCoordinate.y)){
//			console.log(this.graph.selection.insideArcRotation(currentCoordinate.x , currentCoordinate.y))
			console.log("ratation");
			this.graphState = 10;
			return;
		}
		
		var node=this.graph.insideNode(mx,my),edge;
		switch (this.graphState) {
		case 5:
			this.mousedownEdgeSelected(event);
			break;
		case 4:
			this.createHandlerEdge(node);	
			break;
		case 3:
			this.mousedownSelectionRectangle();
			break;
		default:
			this.selectElement(node);
			break;
		}
	}

Eshu.prototype.mousemove = function(event) {
//		if(this.graphState==8 || this.graphState==10) return;
	
//	console.log("ta lembra " +this.graph.selection)
	if(this.graphState==10) {
		if(this.graph.isNodeSelection() ){
			//console.log(this.graph.selection.insideArcRotation(currentCoordinate.x , currentCoordinate.y));
			var xA=this.graph.selection.x+this.width/2;
			var yA=this.graph.selection.y-20;
			var angle=Math.atan2(this.graph.selection.y+ - currentCoordinate.y, this.graph.selection.x - currentCoordinate.x) * (180 / Math.PI);
//			console.log(Math.atan2(this.graph.selection.y - currentCoordinate.y, this.graph.selection.x - currentCoordinate.x)*Math.PI / 180);
			this.graph.selection.setAngleRotation(angle);
			this.draw();
		}
		return;
	}
	
		if(this.graphState==8 ) return;
		currentCoordinate = windowToCanvas(this.canvas, event);
		
		if (this.graphState==1 || this.graphState==7 ) { // moving element
			this.draggingMove(event);
		
		}
		else if(this.graphState==2){ //create selection rectangle 
			this.graph.rectangleSelection.setCoordinateEnd(
					Math.floor(currentCoordinate.x),Math.floor(currentCoordinate.y));
			this.draw();
			
		}
		else if(this.graphState==4){  // create 4-draw line edges
			this.mousemoveDrawLine();
			
		}
		else if(this.graphState == 6){ // resize element
			// verificar se o tipo for egde
			if(this.graph.selection.group =="Node")
				this.graph.selection.resize(currentCoordinate.x, currentCoordinate.y);
			else
				this.redefineEdge();
			this.draw();
			
		}
		
		if(this.elementDraw && this.elementDraw.type=="Edge"){
			this.draw();
			var node;
			if(node=this.graph.insideNode(currentCoordinate.x,currentCoordinate.y)){
				node.showStyleMouseIn(this.ctx);
				this.nodeMarked=node;
			}
			
		}
		//event.stopPropagation();
	}	
	
Eshu.prototype.mouseup = function(event) {
	
	if(this.graphState==10) {
		if(this.graph.isNodeSelection() ){
			//console.log(this.graph.selection.insideArcRotation(currentCoordinate.x , currentCoordinate.y));
			var xA=this.graph.selection.x+this.width/2;
			var yA=this.graph.selection.y-20;
			var angle=Math.atan2(this.graph.selection.y+ - currentCoordinate.y, this.graph.selection.x - currentCoordinate.x) * (180 / Math.PI);
//			console.log(Math.atan2(this.graph.selection.y - currentCoordinate.y, this.graph.selection.x - currentCoordinate.x)*Math.PI / 180);
			console.log(angle);
			this.graph.selection.setAngleRotation(angle);
		
		}
		return;
	}
	
		if(this.graphState==4) return;
			currentCoordinate = windowToCanvas(this.canvas,event);
		
		if(this.graphState==1||this.graphState==7){
			this.mouseupNode();
		}
		else if(this.graphState==2){
			this.mouseupSelectionRectangle();
		}
		else if(this.graphState == 5){
			this.graph.updateBorderEdgeSelected();
		}
		else if(this.graphState == 6){ // resize element
			this.resizeElement();
			document.body.style.cursor = "auto";
		}
		this.mouseupUnselect();
			
	}
	

Eshu.prototype.dblclick = function(event) {
	
	if(this.graphState == 8) return;
	if(this.graphState ==4){
		this.unSelectOperation();
		this.pointsEdge=[];
		this.graphState=0;
		document.body.style.cursor = "auto";
		this.draw();
	}
	
	
	this.graphState=0;
	
	
	if(this.graph.isNodeSelection() && this.graph.selection.existContainer()){
		this.graph.selection.addElement(currentCoordinate.x,currentCoordinate.y);
		this.draw();
		//this.graph.selection.addOperation();
	}
	
	

	
	
	
	
	//alert(this.imagesSVG.size);
	
	/*if(this.menuAtive == 2) return;
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
	}*/
}
	
Eshu.prototype.selectElement = function(node) {
	 var edge;
	 if (node != undefined) {
		this.graph.selectNode(node);
		this.dragoffx = currentCoordinate.x;
		this.dragoffy = currentCoordinate.y ;
		this.positionSelected={move:false,x:node.x,y:node.y};
		this.graphState = 1;
		this.valid = false;
		this.mousedowElementSelected = true;
		
		this.formatPanel.updateNode();
		this.draw();
	} else if ((edge = this.graph.insideEdge(currentCoordinate.x, currentCoordinate.y)) != undefined) {
		this.graph.selectEdge(edge);
		this.mousedowElementSelected = true;
		this.graphState=5;
		this.formatPanel.updateEdge();
		this.draw();
	} else {
		this.mousedowElementSelected = false;
		this.graphState = 2;
		this.graph.rectangleSelection.reset();
		this.graph.rectangleSelection.setCoordinateStart(Math
				.floor(currentCoordinate.x), Math.floor(currentCoordinate.y));
		
	}
		}
	
Eshu.prototype.createHandlerEdge = function(node) {	
	
	 if (node==undefined ) {
		this.pointsEdge.push({x:currentCoordinate.x, y:currentCoordinate.y});
	}
	else  {
		 if(this.validationSyntaxe && !this.sourceTemporary.connectedTo(node.nodeType,this.typeEdgeCreate.elementType)){
			 this.graphState=0;
			 
		 }
		 else{
			var box={x:currentCoordinate.x, y:currentCoordinate.y};
			this.pointsEdge.push(box);
			if(node.id==this.sourceTemporary.id){
				this.elementDraw=this.typeEdgeCreate; 
				this.unSelectOperation();
				this.pointsEdge=[];
				this.unselectElementESC();
				this.draw();
				return;
			}
//				console.log("pointsEdge "+JSON.stringify(this.pointsEdge));
				var insert=new Insert();
				var args={nodeType:this.typeEdgeCreate.elementType,id:this.generateID++,isNode:false,
							source:this.sourceTemporary,target:node,pointsEdge:this.pointsEdge};//,pointsEdge:ep
				insert.execute(this.graph,args);
				this.commandStack.push(insert);
				this.graphState=5;
				this.formatPanel.updateEdge();
		 }
//		 }
//		 else{
//			 this.graphState=0;
//		 }
		this.elementDraw=this.typeEdgeCreate; 
		this.unSelectOperation();
		this.pointsEdge=[];
		this.draw();
		
	}
	}
	
Eshu.prototype.draggingMove= function(event) {
	document.body.style.cursor = "move";
	var increaseX = currentCoordinate.x - this.dragoffx;
	var increaseY = currentCoordinate.y - this.dragoffy;
	if(this.graphState==7){ //move rectangle selected
		var nodes=this.graph.rectangleSelection.getListOfSelected();
		for(var i in nodes){
			this.moveUpdateNode(nodes[i],increaseX,increaseY);
		}
		this.graph.rectangleSelection.move(increaseX,increaseY);
		this.resizeCanvasRectangleSelection();
	}

	else if(this.graph.selection.insideHandle(currentCoordinate.x,currentCoordinate.y)){ // resize node
		if(this.graph.selection.isNode() && this.nodeAutoresize() ) return;
		this.graphState=6;
		this.positionSelected={move:true,x:this.graph.selection.x,y:this.graph.selection.y,
				width:this.graph.selection.width,height:this.graph.selection.height};
		}
		else{
		this.moveUpdateNode(this.graph.selection,increaseX,increaseY);
//		(newX<0)?this.graph.selection.x=5:this.graph.selection.x=newX;
//		(newY<0)?this.graph.selection.y=5:this.graph.selection.y=newY;
	}
	
	this.dragoffx= currentCoordinate.x; 
	this.dragoffy= currentCoordinate.y ;
	this.resizeCanvasNode();
	this.positionSelected.move=true;
	this.draw();
}	

Eshu.prototype.nodeAutoresize = function() {
	return this.graph.selection.isAutoresize();
}


Eshu.prototype.moveUpdateNode = function(node,x, y) {
		this.graph.nodes.remove(node);
		node.updatePosition(x,y)
		this.graph.addNode(node);
}


Eshu.prototype.redefineEdge = function() {
	
	if(this.graph.selection.insideHandlerSource(currentCoordinate.x,currentCoordinate.y,10) ){
		this.redifineEdgeBySourceTarget(1);
		return;
	}
	else if(this.graph.selection.insideHandlerTarget(currentCoordinate.x,currentCoordinate.y,10) ){
		this.redifineEdgeBySourceTarget(2);
		return;
	}
	else{
		this.graph.selection.setCorrdinateHandlerSelected(currentCoordinate.x,currentCoordinate.y);
		this.graph.selection.updateBorderSize();
		this.positionSelected.move=true;
	}
	
}

Eshu.prototype.redifineEdgeBySourceTarget = function(type) {

	var pointsEdgeSelected=this.graph.selection.getListHandler();
	this.pointsEdge=[];
	if(type==1){
		for(var i=pointsEdgeSelected.length-1;i>0;i--){
			this.pointsEdge.push({x:pointsEdgeSelected[i].x,y:pointsEdgeSelected[i].y });
		}
		this.sourceTemporary=this.graph.selection.target;	
		}
	else{
		for(var i=0;i< pointsEdgeSelected.length-1;i++){
			this.pointsEdge.push({x:pointsEdgeSelected[i].x,y:pointsEdgeSelected[i].y });
		}
		this.sourceTemporary=this.graph.selection.source;
	}
	
	var remove=new Remove();
	remove.execute(this.graph);
	this.commandStack.push(remove);
	this.graphState = 4;
	this.draw();
}


Eshu.prototype.mousedownSelectionRectangle = function() {
		
	if(this.graph.rectangleSelection.contains(currentCoordinate.x,currentCoordinate.y)){
		this.dragoffx = currentCoordinate.x;
		this.dragoffy = currentCoordinate.y ;
		this.positionSelected={move:false,x:this.graph.rectangleSelection.getXMin(),
								y:this.graph.rectangleSelection.getYMin()};
		this.graphState=7;
	}
	else{
		this.graph.unselectNodesRectangleSelection();
		this.graphState=0;
		this.formatPanel.updateMain()
		this.draw();
	}
	}
	

Eshu.prototype.showHideFormatPanel = function() {
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
}

Eshu.prototype.handleSpecialKeys = function(event) {

	var keyId = event.keyCode;
	if(this.graphState==8) {
		this.exitEvents(keyId);
		//event.preventDefault();
		return;
	}
		
		switch (keyId) {
		case 65:
			if (event.ctrlKey) {
				this.selectedALL();
				event.preventDefault();
			}
			break;
		case 68: //d
			if (event.ctrlKey) {
				this.copy();
				this.paste();
				event.preventDefault();
			}
			break;	
		case 27:
			this.unselectElementESC();
			event.preventDefault();
			break;
		case 46:
			if(event.shiftKey){
				this.deleteTextBoxContainer();
			}
			else{
				this.deleteElement();
			}
			break;
		case 67: //c
			if (event.ctrlKey) {
				this.copy();
				//event.preventDefault();
			}
			break;
		case 69://e
			if (event.ctrlKey) {
				this.clearGraph();
				event.preventDefault();
			}
			break;	
		case 86: //v
			if (event.ctrlKey) {
				this.paste(event);
				//event.preventDefault();
			}
			break;	
		case 88: //x
			if (event.ctrlKey) {
				this.cut(event);
				//event.preventDefault();
				}
			break;
		case 90: //z
			if (event.ctrlKey) {
				this.undo();
				//this.redo();
				//this.draw();
				event.preventDefault();
			}
				break;	
		case 81: //q
			if (event.ctrlKey) {
				this.redo();
				//this.draw();
				event.preventDefault();
			}
				break;
				
		case 93: //q
				this.showHideFormatPanel();
				event.preventDefault();
				break;		
				
				
		default:
			this.handleSpecialKeysNodeSelected(event);
			break;
		}
		
	}
	
Eshu.prototype.handleSpecialKeysNodeSelected = function(event) {
		var keyId = event.keyCode;
//		if(this.selection != undefined && this.menuAtive==0 && keyId!=16){
//			if(this.selection.group=="EDGE" && keyId!=46) return;
		if(this.graph.isNodeEdgeSelection() && keyId!=16 && !this.formatPanelVisibly){
			switch(keyId)
			{	

			case 8:
				this.graph.selection.label.deleteLastCharName();
				event.preventDefault();
				this.draw();
				break; 
			case 37:
				//selectedALL
//				if(event.shiftKey){
//		console.log("decrease")
				//	  this.selection.decreaseCursorPositionShift();
//				}
//				else
				this.graph.selection.label.decreaseColumn();
				event.preventDefault();
				break;
			case 39:
				this.graph.selection.label.increaseColumn();
				event.preventDefault();
				break; 
			case 38: //acima
				this.graph.selection.label.decreaseLine();
				event.preventDefault();
				break; 	
			case 40: //abaixo
				this.graph.selection.label.increaseLine();
				event.preventDefault();
				break; 	
						
				
			default:
				// ignore regular characters that must be received by keyPress
				// to correctly handle key sequences and keyboard mapping
				break;

			}
			this.draw();
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

Eshu.prototype.handleNormalKeys = function(event) {
//    if(this.graphState==8) return;

	var keyId = event.keyCode || event.charCode;
	if(this.graph.isNodeEdgeSelection() && keyId!=16 && !this.formatPanelVisibly){
//		if(this.graph.isEdgeSelection()) return;
		
		if (event.shiftKey && keyId != 16)
			this.graph.selection.label.addCharLabel(String.fromCharCode(keyId), this.cursorPosition);
		else if (keyId >= 65 && keyId <= 90) {
			
			this.graph.selection.label.addCharLabel(String.fromCharCode(keyId + 32), this.cursorPosition);
		} 
		else if (keyId == 13) { 
			
			this.graph.selection.label.addCharLabel('\n', this.cursorPosition);
		}else {
			this.graph.selection.label.addCharLabel(String.fromCharCode(keyId), this.cursorPosition);
		}
		
//			if (this.graph.selection.group == "Node") {
//				this.cursorPosition = this.graph.selection.label.getCursorPosition()
//				this.editableName();
//			}
		
		this.draw();
	}
	}	
	
Eshu.prototype.selectNodeInsideRectangleSelection = function() {
	
		this.graph.unselect();	
	var nodes=this.graph.getAllNodes();	
	for ( var i in nodes) {
		if(this.graph.rectangleSelection.isInsideRectangleSelection(nodes[i])){
			this.graph.rectangleSelection.addElementListOfSelected(nodes[i]);
			nodes[i].selected();
		}
	}
	
	if(this.graph.rectangleSelection.sizeListOfSelected()==1){
		this.graphState = 0;
		this.graph.selection=this.graph.rectangleSelection.removelastElement();
		this.graph.rectangleSelection.reset();
		this.mousedowElementSelected=true;
		this.formatPanel.updateNode();
		this.draw();
	}
	else
		this.formatPanel.updateRectangleSelection();/**/
	
	
	
	}	

Eshu.prototype.createElement = function() {
	this.graph.unselectGraph(); // to unselect element

	if(this.elementDraw.type=="NODE"){ // new node
		var insert=new Insert();
		var args={nodeType:this.elementDraw.elementType,id:this.generateID++,isNode:true}
		insert.execute(this.graph,args);
		insert.element.adjustLabel(this.ctx);
		this.commandStack.push(insert);
		this.unSelectOperation();
		//alert(22)
		//console.log(this.formatPanel)
//		console.log(this.formatPanel)
		this.formatPanel.refresh();
		this.formatPanel.updateNode();
		
	}
	else { // new edge
			var node=this.graph.insideNode(currentCoordinate.x,currentCoordinate.y);
			 if (node!=undefined) {
				if(this.validationSyntaxe && !node.connectWith(this.elementDraw.elementType )){
						this.unSelectOperation();
					 	this.graphState=0;
				}
				else{
				 	this.pointsEdge=[];
					this.pointsEdge.push({x:currentCoordinate.x, y:currentCoordinate.y});
					this.sourceTemporary=node;
					this.graphState=4;
					this.typeEdgeCreate=this.elementDraw;
					this.elementDraw = undefined;
				 }
			 }
	}
	this.mousedowElementSelected=true;
	this.draw();
}

Eshu.prototype.mouseupNode= function() {
	
	if(this.positionSelected.move){
		var move=new Move();
		move.execute(this.graph,args={x:this.positionSelected.x,y:this.positionSelected.y});
		this.commandStack.push(move);
		this.positionSelected.move=false;
		this.formatPanel.updatePosition();
		}
	else if(this.graphState==6){
		
	}
	(this.graphState==7)?this.graphState=3:this.graphState = 0;
	this.positionSelected.move=false;
	document.body.style.cursor = "auto";
}

Eshu.prototype.mouseupUnselect = function (){
	if(!this.mousedowElementSelected){
		this.graph.unselect();
		this.mousedowElementSelected=false;
		this.graphState=0;
		this.draw();
		this.formatPanel.updateMain();
	}
		
}

Eshu.prototype.mousedownHideContextMenu = function (){
	
	//if(this.graphState==8){
		this.hideContextMenu();
//		this.graphState=0;
		this.draw();
	//}
}

Eshu.prototype.mouseupSelectionRectangle = function() {
	
	this.graph.rectangleSelection.setCoordinateEnd(
			Math.floor(currentCoordinate.x),Math.floor(currentCoordinate.y));
	
	this.selectNodeInsideRectangleSelection();
	
	if(this.graph.rectangleSelection.isEmpty()){
		this.graphState = 0;
		//this.mousedowElementSelected = false;
	}
	else{
		this.graph.rectangleSelection.adjustRectangleToElements();
		this.graphState = 3;
		this.mousedowElementSelected = true;
		this.draw();
	}
	
}

Eshu.prototype.mousemoveDrawLine = function() {
	
	var l=this.pointsEdge.length-1;
	if(l>=0){
		//console.log(this.pointsEdge);
		this.lineCurrentPosition=0;
		this.draw();
		this.drawSimpleLine (this.pointsEdge[l],
				{x:currentCoordinate.x,y:currentCoordinate.y},"red");
	}
	var node;
	if(node=this.graph.insideNode(currentCoordinate.x,currentCoordinate.y)){
		node.showStyleMouseIn(this.ctx);
		
	}
}

Eshu.prototype.mousedownEdgeSelected = function(event) {
	
	if(this.graph.selection.insideHandle(currentCoordinate.x,currentCoordinate.y,10)){
		this.mousedowElementSelected=true;
		this.graphState = 6;
		this.positionSelected={move:false,x:this.graph.selection.handlerSelected.x,
				y:this.graph.selection.handlerSelected.y};
	}
	else if(event.button==0){
		this.graphState = 0;
		this.mousedowElementSelected=false;
	}
}

Eshu.prototype.editableName = function() {
	if(!this.graph.isLabelEditable()) return;
	this.graph.selection.showBarLabel(this.ctx);
	this.draw();
}

Eshu.prototype.selectedALL = function(event) {
	var nodes=this.graph.getAllNodes();
	this.graph.rectangleSelection=new RectangleSelection(this.ctx);
	this.graph.rectangleSelection.setCoordinates(0,0,this.WIDTH,this.HEIGHT);
	this.selectNodeInsideRectangleSelection();
	this.graph.rectangleSelection.adjustRectangleToElements();
	this.mousedowElementSelected=true;
	this.graphState=3;
	this.draw();
}

Eshu.prototype.unselectElementESC = function() {
	this.graph.unselectGraph();
	this.graphState=0;
	this.draw();
}

Eshu.prototype.copy = function() {
		this.graph.copy();
}

Eshu.prototype.cut = function() {
	if (this.graphState==3) {
		this.graph.copyRectangleSelection();
		this.graphState=0;
		
	} else if (this.graph.isNodeSelection()) {
			this.graph.copyNode(this.graph.selection);
	}
	this.deleteElement();
	this.draw();
}

Eshu.prototype.paste = function() {
	if(this.graph.structCopy==undefined) return;
	
	
	var paste=new Paste();
	this.generateID+=paste.execute(this.graph ,this.generateID);
	this.commandStack.push(paste);
	
	
	if(this.graph.structCopy.type==1)
		this.graphState=0;
	else
	{
		this.graphState=3;
		this.mousedowElementSelected=true;
	}
	
	this.resizeCanvasRectangleSelection();
	this.draw();

}


Eshu.prototype.undo = function() {
	if(!this.commandStack.isEmptyListUndo()){
		this.unselectOperationCommand();
		var command=this.commandStack.popUndo();
		this.graphState=command.undo(this.graph);
		this.draw();
	}
}

Eshu.prototype.redo = function() {
	if(!this.commandStack.isEmptyListRedo()){
		this.unselectOperationCommand();
		var command=this.commandStack.popRedo();
		this.graphState=command.redo(this.graph);
		this.draw();
	}
}


Eshu.prototype.unselectOperationCommand = function() {
	if(this.graphState==4||this.graphState==6){ 
			this.unSelectOperation();
	}
	
}

Eshu.prototype.deleteElement = function() {
	if(this.graph.isSelected()){
		var remove=new Remove();
		remove.execute(this.graph);
		this.commandStack.push(remove);
		this.graphState=0;
		this.formatPanel.updateMain();
		this.draw();
	}

}
Eshu.prototype.resizeElement = function() {
	
	if(this.positionSelected.move){
		var resize=new Resize();
		resize.execute(this.graph,args={x:this.positionSelected.x,y:this.positionSelected.y,width:this.positionSelected.width,height:this.positionSelected.height});
		this.commandStack.push(resize);
		this.positionSelected.move=false;
		this.formatPanel.updateSize();
	}
	this.graphState=5;
}

Eshu.prototype.deleteTextBoxContainer=function(){
	if(this.graph.isNodeSelection() && this.graph.selection.isDeleteBoxContainer()){
		this.graph.selection.deleteBoxContainer();
		this.draw();
	}
	
}

Eshu.prototype.resizeNode = function() {
	var oldx = this.graph.selection.x;
	var oldy = this.graph.selection.y;
	var mx = currentCoordinate.x;
	var my = currentCoordinate.y;
	var sizeMin = 20;
	

	switch (this.graph.selection.handlerSelected) {
	case 0:
		this.graph.selection.y = my;
		this.graph.selection.height += oldy - my;
		document.body.style.cursor = "n-resize";
		break;
	case 1:
		this.graph.selection.width = mx - oldx;
		document.body.style.cursor = "e-resize";
		break;
	case 2:
		this.graph.selection.height = my - oldy;
		document.body.style.cursor = "n-resize";
		break;
	case 3:
		this.graph.selection.x = mx;
		this.graph.selection.width += (oldx - mx);
		document.body.style.cursor = "e-resize";

		break;
	case 4:
		this.graph.selection.x = mx;
		this.graph.selection.y = my;
		this.graph.selection.width += oldx - mx;
		this.graph.selection.height += oldy - my;
		document.body.style.cursor = "nw-resize";
		break;
	case 5:
		this.graph.selection.y = my;
		this.graph.selection.width = mx - oldx;
		this.graph.selection.height += oldy - my;
		document.body.style.cursor = "ne-resize";
		break;
	case 6:
		this.graph.selection.width = mx - oldx;
		this.graph.selection.height = my - oldy;
		document.body.style.cursor = "nw-resize";
		break;
	case 7:
		this.graph.selection.x = mx;
		this.graph.selection.width += oldx - mx;
		this.graph.selection.height = my - oldy;
		document.body.style.cursor = "ne-resize";
		break;

	}
	if (this.graph.selection.width < sizeMin) {
		this.graph.selection.width = sizeMin;
		this.graph.selection.x = oldx
	}
	if (this.graph.selection.height < sizeMin) {
		this.graph.selection.height = sizeMin;
		this.graph.selection.y = oldy
	}
	;

//	this.valid = false;
	this.graph.selection.updateHandles();

	this.draw();
}




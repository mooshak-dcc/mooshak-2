/**
 * Define the Vertice, the superclass used to create nodes.
 * 
 * @method Vertice
 * @param {Number}
 *            str - Coordinate Y of the Vertices
 * @param {Number}
 *            obj - Coordinate Y of the Vertices
 * @param {id}
 *            callback - Identifier of the Vertices.
 * @return null;
 */
function Vertices(x, y,id) {
	
	/* Width of the Vertices, Default 70px. */
	this.widthDefault = 70; 
	
	/* Height of the Vertices, Default 70px. */
	this.heightDefault = 40; 
	
	/* current Width of the Vertices, Default 70px. */
	this.width = 70 ; 
	
	/* current Height of the Vertices, DeFfault 70px. */
	this.height = 40 ; 
	
	
	/* Coordinate Y of the Vertices */
	this.x = x-this.width/2;
	(this.x<0)?this.x = 3:this.x;
	/* Coordinate Y of the Vertices */
	this.y = y-this.height/2;  
	(this.y<0)?this.y = 3:this.y;
	/* Identifier of the Vertices */
	this.id=id;	
	
	
	/*
	 * String that defines type of the node, example of types:Entity,class,ator
	 */
	this.type="";
	
	this.nodeType;
	
	/* String that define the element group. */
	this.group ="Node"; 
	
	/* Boolean that define if this node is selected */
	this.select = false;
	
	
	 /*
	 * Array of handler that define the points in node where is possible connect
	 * with other
	 */
	this.anchors=new Array();
	
	/*
	 * Array of anchors that define the points in node where is possible select
	 * to resize
	 */
	this.handlers=new Array();

	/*
	 * Boolean define if some handler of this node is selected. Value default is
	 * false
	 */
	this.handlerSelected = false;
	
	/*
	 * Boolean define if some anchors of this node is selected. Value default is
	 * false
	 */
	this.anchorSelected = false;
	
	/**
	 * Number define the Number of Handles
	 */
	this.numberHandles = 17;
	
	/* Number define the Number of Anchor */
	this.anchorSize=4;
	
	/* Number define size handler */
	this.selectionBoxSize = 6;
	
	/* String that define the color of the bohurder box */
	this.borderColor="blue";
	
	/* Number that define the width of the border box */
	this.borderWidth = 1;
	
	/*
	 * String that define the background color of the Vertice. default white.
	 */
	this.backgroundColor="white";
	
	/* String storage the feedback given by validator */
	this.temporary="";
	
	/* Number degree out of the Node */
	this.degreeOut=0;
	
	/* Number degree input of the Node */
	this.degreeIn=0;

	/* Update the positions of the handlers */
	// this.updateHandles();
	
	/* Array with nodes type that this node can be connected */
	this.listTypeCanBeconnected=[];
	
	/* String URL with information of this node */ 
	this.URLinfo="";
   
	/* Value used in border refinement */
	this.middle=0.5;
	
	/* path SVG with icon format for toolbar */ 
	this.pathIconTollBarSVG="";
	
	/* image with icon format for toolbar */ 
	this.imageIconTollBarSVG="";
	
	/* path of SVG with image */ 
	this.pathimgSVG="";
	
	/* SVG with format of this node */ 
	this.imageSVG="";
	
	/*
	 * Array of edges connected with node
	 */
	this.edgesConnected;
	
	/*
	 * String with info of node
	 */
	this.label;
	
	/*
	 * Bolean marked if node is moved
	 */
	this.isMoved;
	
	this.labelPosition;
	
	this.listIndexAchor=[];
	
	this.modify="";
	
	this.autoresize;
	
	this.order=0;
	
	this.containers=[];

	this.IdGenerete=1;
	
	this.isLabelEditable=false;
	
	this.variant;
	
	this.margin;
	
	this.angleRotation=0;
	
	this.rotation=false;
	
	this.stereotype;
	
	this.stereotypeConfig;
}


Vertices.prototype = {
		
	/**
	 * Create an hidden canvas for this vertex
	 */
	createHiddenCanvas() {
		if(!this.nodeType || !this.nodeType.isConfigurable || document.getElementById("eshuHiddenCanvas_" + this.id))
			return;
		
		var hiddenCanvas = document.createElement("canvas");
		hiddenCanvas.setAttribute("id", "eshuHiddenCanvas_" + this.id);
		hiddenCanvas.style.position = "absolute";
		hiddenCanvas.style.zIndex = -10;
		
		var graphEditor = document.getElementById("graphEditor");
		graphEditor.insertAdjacentElement('afterbegin', hiddenCanvas);
	},
	
	/**
	 * Remove hidden canvas of this vertex
	 */
	removeHiddenCanvas() {
		if(!this.nodeType || !this.nodeType.isConfigurable)
			return;
		
		var hiddenCanvas = document.getElementById("eshuHiddenCanvas_" + this.id);
		if(hiddenCanvas)
			hiddenCanvas.parentNode.removeChild(hiddenCanvas);
	},

	/**
	 * Determine if a point is inside the Vertices's bounds
	 * 
	 * @param {Number}
	 *            mx - coordinate X
	 * @param {Number}
	 *            my - coordinate Y
	 * @return {Boolean}- some bool
	 */
	contains (mx, my) {
		var x1 = this.x;
		var y1 = this.y;
		this.update(this.x, this.y);
		return (x1 <= mx) && (x1 + this.width + 10 >= mx) && (y1 <= my)
				&& (y1 + this.height + 10 >= my);

	},
	
	containsNode (node){
//		console.log(this.insideArcRotation(mx, my))
		if(node.id==this.id ) return false;
// this.update(this.x, this.y);
		var x = this.x;
		var y = this.y;
		return ( (node.x+node.width) < (x+this.width)
		        && (node.x) > (x)
		        && (node.y) > (y)
		        && (node.y+node.height) < (y+this.height)
		        );
		
	},
	
	insideArcRotation (mx, my) {
		
		var x=this.x+this.width/2;
		var y =this.y-20;
		var radius=20;
		return this.pointInCircle(mx, my, x, y, radius)
	},
	// x,y is the point to test
	// cx, cy is circle center, and radius is circle radius
	 pointInCircle(x, y, cx, cy, radius) {
	  var distancesquared = (x - cx) * (x - cx) + (y - cy) * (y - cy);
	  return distancesquared <= radius * radius;
	},
	
// /**
// * Determine if a point is inside the handle's bounds
// * @param {Number} mx - coordinate X
// * @param {Number} my - coordinate Y
// * @return {Boolean}- some bool
// */
// insideHandle (mx, my) {
//
// for (var i = 0; i < this.numberHandles; i++) {
//
// if (this.handlers[i].contains(mx, my)) {
// this.handlerSelected = i;
// console.log(this.handlerSelected + " this.handlerSelected")
// return true;
//
// }
// }
// return false;
//
// },
	
	/**
	 * Determine if a point is inside the handle's bounds
	 * 
	 * @param {Number}
	 *            mx - coordinate X
	 * @param {Number}
	 *            my - coordinate Y
	 * @return {Boolean}- some bool
	 */
	insideHandle (mx, my) {

//		for (var i = 0; i < this.numberHandles; i++) {
		for (var i = 0; i < this.handlers.length; i++) {
			
//			if (this.handlers[i].contains(mx, my) &&(i%2!=0)) {
			if (this.handlers[i].contains(mx, my) ) {
				this.handlerSelected = this.handlers[i].getId();
				return true;

			}
		}
		return false;

	},
	
	/**
	 * update position x and y * of vertice
	 * 
	 * @param {Number}
	 *            x - coordinate X
	 * @param {Number}
	 *            y - coordinate Y
	 * @return {null}
	 */
	update (x, y) {
		this.x = x-this.width/2;
		this.y = y-this.height/2;
	},
	
	/**
	 * update position x of vertice
	 * 
	 * @param {Number}
	 *            x - coordinate X
	 * @return {null}
	 */
	updateX(x) {
		this.x = x;
		
	},
	
	/**
	 * update position y * of vertice
	 * 
	 * @param {Number}
	 *            y - coordinate Y
	 * @return {null}
	 */
	updateY(y) {
		this.y = y;
	},
	
	updateSize(width, height){
//		(height > this.heightDefault)? this.height=height:this.height=this.heightDefault;
//		(width >   this.widthDefault)?  this.width=width: this.width=this.widthDefault;
		if(!this.isAutoresize()){
			this.height=height;
			this.width=width;
			this.updateHandles();
			this.updateAnchor();
		}
	},
	
	/**
	 * update position x and y * of vertice
	 * 
	 * @param {Number}
	 *            x - coordinate X
	 * @param {Number}
	 *            y - coordinate Y
	 * @return {null}
	 */
	updatePosition (x, y) {
		this.x +=x;
		(this.x<0)?this.x = 3:this.x;
		/* Coordinate Y of the Vertices */
		this.y += y;  
		(this.y<0)?this.y = 3:this.y;
	},
	
	/**
	 * change the node layout when is selected
	 * 
	 * @return {null}
	 */
	selected () {
		this.borderSelected = this.borderColor;
		this.select = true;
		if(this.existLabel()){
			this.isLabelEditable=!this.label.isDisable();
		}
		
	},
	
	isLabelSectedEditable(){
		return this.label.isDisable();
	},
	/**
	 * change the node layout when is unselected
	 * 
	 * @return {null}
	 */
	unSelected () {
		this.borderSelected = '#3366CC';
		this.select = false;
		this.selectHanderEdge = false;
		this.backgroundColor=this.color;
		if(this.islabelEditable()){
			this.label.showBar=false;
			this.label.selectLabel=false;
		}
	},

	selectedLabel () {
		this.label.selectLabel=true;
		
	},
	
	/**
	 * Get the value of coordinate x and y
	 * 
	 * @return {Object}
	 */
	getCenter () {
		var a = {};
		a.x = parseInt(this.x + this.width / 2);
		a.y = parseInt(this.y + this.height / 2);
		return a;
	},
	
	/**
	 * check if the center coordinate of some node is equals of center
	 * coordinate this node
	 * 
	 * @param {Node}
	 *            no - some node
	 * @return {Boolean}- some boolean
	 */
	isPositionCenterEqual (no) {	
			if((Math.abs(no.getCenter().x-this.getCenter().x)<3 || Math.abs(no.getCenter().y-this.getCenter().y)<3) && this.id!=no.id)
			return true;
		false;
	},
	
	
	/**
	 * draw the border of the node
	 * 
	 * @param {Context}
	 *            ctx - getContext('2d')
	 * @return {null}-
	 */
	drawBorder (ctx) {
//		this.updateHandles();
		ctx.save();
		ctx.beginPath();
		ctx.strokeStyle = this.borderSelected;
		ctx.rect(this.x+this.middle, this.y+this.middle, this.width, this.height);
		ctx.fillStyle = "transparent";
		ctx.lineWidth = this.borderWidth;
		ctx.fill();
		ctx.stroke();
//		ctx.moveTo(this.x, this.y);
//		ctx.lineTo(this.x+this.width, this.y+this.height);
		
		ctx.restore();
		if (this.select)
//			for (var i = 1; i < this.numberHandles; i+=2) {
			for (var i = 0; i < this.handlers.length; i++) {
				this.handlers[i].draw(ctx);
				
			}
//		if(this.rotation)
//			this.drawLineRotation(ctx);
	},
	
	
	/**
	 * Show anchor of the node
	 * 
	 * @param {Context}
	 *            ctx - getContext('2d')
	 * @return {null}-
	 */
	showAnchors (ctx) {
		
		for (var i = 0; i < this.anchors.length; i++) {
			this.anchors[i].draw(ctx);
		}
	},
	
	
	showStyleMouseIn(ctx){
		
		this.drawBorder(ctx);
		if(this.isAnchorFixed())
			this.showAnchors (ctx);
		
	},
	
	/**
	 * update the positions of the box element
	 * 
	 * @return {null}-
	 */
	
	
	updateBoxElement (box) {
		var half = this.selectionBoxSize / 2;
		var x1 = this.x-half;
		var y1 = this.y-half;
		var halfWidth=this.width / 2;
		var halfHeight = this.height / 2;
		var quarterWidth =this.width / 4;
		var quarterHeight =this.height / 4;
		
		
//			console.log("this.handlers[i].getId() "+this.handlers[i].getId())
			switch (box.getId()) {
			case 0: //north N
				box.updatePosition(x1 + halfWidth , y1);
				break;
			case 1: //North-northeast NNE
				box.updatePosition( (x1 + quarterWidth + halfWidth), y1  );
			break;
			case 2: //northeast NE
				box.updatePosition(x1 + this.width , y1 );
				break;
			case 3: //East-northeast ENE
				box.updatePosition( (x1 + this.width), (y1 + quarterHeight)  );
			break;
			case 4: //east E
				box.updatePosition(x1 + this.width , y1 + halfHeight);
			break;
			case 5: //East-southeast ESE
				box.updatePosition((x1 + this.width), (y1 + quarterHeight + halfHeight)  );
			break;
			
			case 6: //southeast SE
				box.updatePosition(x1 + this.width , y1 + this.height);
				break;
			
			case 7: //South-southeast SSE 
				box.updatePosition( (x1 + quarterWidth + halfWidth), (y1 + this.height));
			break;	
			
			case 8: // south S
				box.updatePosition(x1 + halfWidth , y1 + this.height );
				break;
			
			case 9: //South-southwest SSW 
				box.updatePosition( (x1 + quarterWidth), (y1 + this.height));
			break;
			
			case 10: // southwest SW
				box.updatePosition(x1 , y1 + this.height);
				break;
			
			case 11: //West-southwest WSW
				box.updatePosition( x1 , (y1 + quarterHeight + halfHeight));
			break;
				
			case 12: // west
				box.updatePosition(x1 , y1 + halfHeight);
				break;
				
			case 13: //West-northwest WNW
				box.updatePosition(x1, y1+quarterHeight );
			break;
			
			case 14: //northwest NW
				box.updatePosition(x1 , y1 );
				break;
				
			case 15: //North-northwest NNW
				box.updatePosition(x1 +quarterWidth, y1);
			break;
			
			case 16: // center C
				box.updatePosition(x1+halfWidth , y1+halfHeight );
				break;
				
			default:
				throw new Error("Error on update element type Box in Vertice.js "+type);
			
			
		}
	},
	
	
	
	createElementBox (handler) {
		
		var half = this.selectionBoxSize / 2;
		var x1 = this.x-half;
		var y1 = this.y-half;
		var halfWidth=this.width / 2;
		var halfHeight = this.height / 2;
		var quarterWidth =this.width / 4;
		var quarterHeight =this.height / 4;
		var idHandler=this.convertDirection(handler);
	
		
			switch (idHandler) {
			case 0: //north N
				return (new Box(x1 + halfWidth , y1, idHandler));
				break;
			case 1: //North-northeast NNE
				return (new Box( (x1 + quarterWidth + halfWidth), y1 ,idHandler ));
			break;
			case 2: //northeast NE
				return (new Box(x1 + this.width , y1 ,idHandler));
				break;
			case 3: //East-northeast ENE
				return (new Box( (x1 + this.width), (y1 + quarterHeight) ,idHandler ));
			break;
			case 4: //east E
				return (new Box(x1 + this.width , y1 + halfHeight  ,idHandler) );
			break;
			case 5: //East-southeast ESE
				return (new Box( (x1 + this.width), (y1 + quarterHeight + halfHeight) ,idHandler ));
			break;
			
			case 6: //southeast SE
				return (new Box(x1 + this.width , y1 + this.height ,idHandler));
				break;
			
			case 7: //South-southeast SSE 
				return (new Box( (x1 + quarterWidth + halfWidth), (y1 + this.height)  ,idHandler));
			break;	
			
			case 8: // south S
				return (new Box(x1 + halfWidth , y1 + this.height ,idHandler));
				break;
			
			case 9: //South-southwest SSW 
				return (new Box( (x1 + quarterWidth), (y1 + this.height)  ,idHandler));
			break;
			
			case 10: // southwest SW
				return (new Box(x1 , y1 + this.height ,idHandler));
				break;
			
			case 11: //West-southwest WSW
				return (new Box( x1 , (y1 + quarterHeight + halfHeight) ,idHandler));
			break;
				
			case 12: // west
				return (new Box(x1 , y1 + halfHeight ,idHandler));
				break;
				
			case 13: //North-northwest NNW
				return (new Box(x1, y1+quarterHeight  ,idHandler));
			break;
			
			case 14: //northwest NW
				return (new Box(x1 , y1 ,idHandler));
				break;
				
			case 15: //West-northwest WNW
				return (new Box(x1 +quarterWidth, y1  ,idHandler));
			break;
			
			case 16: // center C
				return (new Box(x1+halfWidth , y1+halfHeight  ,idHandler));
				break;
				
			default:
				throw new Error("Error on create element type Box in Vertice.js "+type);
			}
			
		
	},
	
	
	
	/**
	 * Create the Handlers 
	 */
	createHandlers(handlers){
		this.handlers = new Array();
		for (var i in handlers)
			this.handlers.push(this.createElementBox(handlers[i]));
	},
	
	/**
	 * update the positions of the handlers
	 * 
	 * @return {null}-
	 */
	updateHandles(){
		for (var i = 0; i < this.handlers.length; i++) {
			this.updateBoxElement(this.handlers[i]);
		}
	},
	
	/**
	 * Create the Anchors
	 * 
	 * @return {null}-
	 */
	createAnchor(anchors){
		this.anchors = new Array();
		for (var i in anchors)
			this.anchors.push(this.createElementBox(anchors[i]));
	},

	/**
	 * Update the position of the Anchors
	 * 
	 * @return {null}-
	 */
	updateAnchor(){
		
		for (var i in  this.anchors) {
			this.updateBoxElement(this.anchors[i]);
		}
	},
	
	
	

	/**
	 * Show the Anchors of node
	 * 
	 * @return {null}-
	 */
	showAnchor(ctx){
// this.updateAnchor();
		for (var i = 0; i < this.anchors.length ; i++) {
			this.anchors[i].draw(ctx);
		}
	},
	
	/**
	 * check if the coordinate is inside anchor area, and select the anchors
	 * selected
	 * 
	 * @param {Number}
	 *            x - coordinate X
	 * @param {Number}
	 *            y - coordinate Y
	 * @return {Boolean}- some boolean
	 */
	insideAnchor (mx, my) {
		for (var i = 0; i < this.anchors.length; i++) {

			if (this.anchors[i].contains(mx, my, 4)) {
				this.handlerSelected = this.anchors[i].getId(); //****************verificar
				return true;
			}
		}
		return false;

	},
	
	/**
	 * check if the coordinate is inside anchor area do not select the anchor
	 * 
	 * @param {Number}
	 *            x - coordinate X
	 * @param {Number}
	 *            y - coordinate Y
	 * @return {Boolean}- some boolean
	 */
	insideAnchor2 (mx, my) {
		for (var i = 0; i < this.anchors.length; i++) {
			if (this.anchors[i].contains(mx, my, 10)) {
				return true;
			}
		}
		return false;
	},
	
	
	/**
	 * check if the certain node type can be connected with this node
	 * 
	 * @param {Number}
	 *            x - coordinate X
	 * @param {Number}
	 *            y - coordinate Y
	 * @return {Boolean}- some boolean
	 */
	connectedTo(nodeType,edgeType){
	        if (nodeType==undefined ||edgeType==undefined)
	            	return false;
	         return (this.nodeType.isNodeConnect(nodeType,edgeType) && this.checkDegree());
		
	},
	
	connectWith  (edgeType){
		if (edgeType==undefined)
         		return false;
		 return (this.nodeType.isEdgeConnect(edgeType) && this.checkDegree());
	},
	
	
	 /**
		 * Draw the image Icon
		 * 
		 * @return {null}-
		 */
	drawIcon (ctx) {
		var height=this.height;
		var width=this.width;
		 ctx.drawImage(this.imageIconTollBarSVG, 0,0,width,height); 

	},

	
	
	/**
	 * return list of edges Connected
	 * 
	 * @return {Array}- edges connected
	 */
	getEdgesConnected() {
		return this.edgesConnected;
	},
	
	/**
	 * add a edge in list of edges Connected
	 * 
	 * @return {null}-
	 */
	setEdgesConnected(edge) {
		this.edgesConnected.push(edge);
	},
	
	/**
	 * delete a edge in list of edges Connected
	 * 
	 * @return {null}-
	 */
	removeEdgesConnected(edge) {
		for(var i in this.edgesConnected){
			if(this.edgesConnected[i].id==edge.id){
				this.edgesConnected.splice(i, 1);
			}
		}
	},
	
	updatePositionEdgesConnected (node) {
		var edges=this.edgesConnected;
		for(var i in edges){
			edges[i].updateBorderSize();
		}
	},
	
	 /**
		 * calcule distance min to handler of node
		 * 
		 * @return {Box}-
		 */
	anchorsDistanceMin (box) {
		
		var boxDistanceMin={
				dist:10000,
				index:-1,
			};
		if(!this.isAnchorFixed()){
			
			var anchorAUX=this.anchors[0];
			for(var i in this.anchors ){
				var dist =this.anchors[i].distance(box);
				if(dist+(this.height/2)<boxDistanceMin.dist){
					boxDistanceMin.dist=dist;
					boxDistanceMin.index=i;
					anchorAUX=this.anchors[i];
				}
				else if(dist==boxDistanceMin.dist){
					boxDistanceMin.index=Math.min(i,boxDistanceMin.index);
					anchorAUX=this.anchors[boxDistanceMin.index];
				}
			}
			if(boxDistanceMin.index==-1)
				return undefined;
			return anchorAUX;
		}
		else{
			var anchorAUX=this.anchors[0];
			for(var i in this.anchors ){
				var dist =this.anchors[i].distance(box);
				if(dist<boxDistanceMin.dist){
					boxDistanceMin.dist=dist;
					boxDistanceMin.index=i;
					anchorAUX=this.anchors[i];
				}
			}
			return anchorAUX;
			
			
		}
		
		
	},
	
	showBarLabel(ctx) {
		
		if(!this.label.showBar)
			this.label.showBar=true;
		else
			this.label.showBar=false;
		
			},
	
	clone() {

		return {
				type:this.nodeType,
				x:this.x,
				y:this.y,
				label:this.label.label,
		};
	},
	draw (ctx){
		this.updateHandles();
		this.updateAnchor();
		
		if(this.checkBrowserFirefox()){
			var last = this.imageSVG.onload;
			this.imageSVG.onload = function(){
				if(last) last();
				this.drawVertex(ctx);
			}.bind(this);
			if (this.imageSVG.complete) 
				this.drawVertex(ctx);
			
		}
		else {
//			var xA=this.x+this.width/2;
//			var yA=this.y-20;
//			console.log("NotVERTEX "+Math.atan2(this.y - yA, (this.x+this.width/2) - xA));
			this.updateLabelPosition();
			 ctx.save();
			 if(this.modify!="")
				 ctx.globalAlpha  = 0.3;
/*			 if(this.rotation){
				 ctx.translate(this.x+this.width/2,this.y+this.height/2); 
				 ctx.rotate(this.angleRotation * (180 / Math.PI)); //
				 ctx.translate(-(this.x+this.width/2),-(this.y+this.height/2));
				 
				 ctx.drawImage(this.imageSVG, this.x,this.y,this.width,this.height);
				 
				 ctx.translate(this.x+this.width/2,this.y+this.height/2); 
				 ctx.rotate(-(this.angleRotation * (180 / Math.PI))); //
				 ctx.translate(-(this.x+this.width/2),-(this.y+this.height/2));
			 }
			 else{*/
				 ctx.drawImage(this.imageSVG, this.x,this.y,this.width,this.height);
//			 }
			 ctx.fillStyle = "rgba(50, 35, 255, 0.5)";
			 if(this.select)
					this.drawBorder(ctx);
			 this.adjustLabel(ctx);
			 if(this.existLabel())
				this.label.draw(ctx);
			this.drawStereotype(ctx);
			 
//			 this.drawLineRotation(ctx);
			 ctx.restore();
		}
	},
	
	drawStereotype(ctx){
		if(this.stereotype){
			this.updateLatelPositionGlobal(this.stereotype)
			this.stereotype.draw(ctx);
//			console.log(this.stereotypeConfig.marginWidth +" "+ this.stereotypeConfig.marginHeight)
		}
		
	},
	setAngleRotation(angle){
		this.angleRotation=angle;
//		 console.log("this.angleRotation "+ this.angleRotation)
	},
	drawLineRotation(ctx) {
		
		ctx.save();
		ctx.beginPath();
		ctx.strokeStyle = "blue";
		ctx.moveTo(this.x+this.width/2, this.y);
		ctx.lineTo(this.x+this.width/2, this.y-20);
//		ctx.translate(this.x+this.width/2, this.y-20);
//		cxt.moveTo(this.x+this.width/2, this.y-20);
		ctx.arc(this.x+this.width/2, this.y-20,7, 0, 2 * Math.PI*180);
		ctx.lineWidth=1;
		ctx.stroke();
		
//		this.drawSpiral(ctx);
		ctx.restore();
		
	},
	
	
	
	drawVertex(ctx) {
//		this.updateHandles();
		 this.updateLabelPosition();
		 ctx.save();
		 if(this.modify!="")
			 ctx.globalAlpha  = 0.3;		 
		 
		 var h=this.height;
		 var w =this.width;
		 var hiddenCanvas = document.getElementById("eshuHiddenCanvas_" + this.id);
		 var hiddenCtx = hiddenCanvas.getContext("2d");
		 hiddenCtx.clearRect(0, 0, hiddenCanvas.width, hiddenCanvas.height);
		 
//		 console.log("hiddenCanvas "+hiddenCanvas.id)
		 var ratio = Math.max(this.width/this.imageSVG.naturalWidth, this.height/this.imageSVG.naturalHeight);
		 this.width = this.imageSVG.naturalWidth*ratio;
		 this.height = this.imageSVG.naturalHeight*ratio;
		 hiddenCanvas.width = this.width;
		 hiddenCanvas.height = this.height//+(this.label.getHeight());
		 
		 hiddenCtx.save();
		 hiddenCtx.scale(ratio, ratio);
		 hiddenCtx.drawImage(this.imageSVG, 0, 0);
		 hiddenCtx.restore();
		
//		 console.log(this.id + " -> " + this.width + " " + this.height)
		 this.height=h//+(this.label.getHeight());
		 this.width=w;
//		 console.log("print 2 "+this.id + " -> " + this.width + " " + this.height);
		 ctx.drawImage(hiddenCanvas, this.x, this.y, this.width, this.height);
		 ctx.fillStyle = "rgba(50, 35, 255, 0.5)";
//		 this.height=this.height;
		 if(this.select)
			this.drawBorder(ctx);
		 this.adjustLabel(ctx);
		 if(this.existLabel())
			this.label.draw(ctx);
		 this.drawStereotype(ctx);
		 ctx.restore();
		 
		 
	},
	
	
	checkBrowser(){ 
		c = navigator.userAgent.search("Chrome"); 
		f = navigator.userAgent.search("Firefox"); 
		m8 = navigator.userAgent.search("MSIE 8.0"); 
		m9 = navigator.userAgent.search("MSIE 9.0"); 
		if (c > -1) { 
			browser = "Chrome";
		} 
		else if (f > -1) { 
			browser = "Firefox"; 
			} 
		else if (m9 > -1) { 
			browser ="MSIE 9.0"; 
		} 
		else if (m8 > -1) { 
			browser ="MSIE 8.0"; 
		} 
		return browser; 
		},
	
		checkBrowserFirefox(){ 
			if (navigator.userAgent.search("Firefox") > -1) 
				return true; 
			 
				return false; 
			},	
	
	setNodeType  (nodeType){
		this.nodeType=nodeType;
		this.type=nodeType.type;
		this.widthDefault = nodeType.width; 
		this.heightDefault =nodeType.height;
		this.width = nodeType.width ; 
		this.height = nodeType.height ; 
		this.rotation=nodeType.rotation;
		this.imageIconTollBarSVG = nodeType.getImageIconTollBarSVG();
		this.imageSVG  = nodeType.getImageSVG();
		this.listTypeCanBeconnected=["actor","usecase"];
		this.variant=nodeType.variant;
		this.edgesConnected=[];
		nodeType.labelConf.disabled=!nodeType.labelConf.disabled;
		this.createLabel(nodeType.labelConf);
		this.createHandlers(nodeType.listHandlers);
		this.createAnchor(nodeType.listAnchors.anchor);
//		console.log("nodeType.listAnchors.anchor "+ nodeType.listAnchors.anchor)
		this.createFeatures(nodeType.features);
		this.stereotype=this.createStereotype(nodeType.stereotype);
		if(nodeType.isConfigurable)
			this.createHiddenCanvas();
		
//		console.log(nodeType);
		
		
	},
	

	createLabel  (labelConf){
		
//		alert(!convertStringBoolen(labelConf.disabled));
		this.isLabelEditable=!convertStringBoolen(labelConf.disabled);
		
		var position =labelConf.position.toLowerCase();
		(labelConf.margin)?this.margin=toInteger(labelConf.margin):this.margin=0;
		var x=this.x;
		
		if(position=="top") 
			this.label= new TextBox(x,this.y-5,this.width,labelConf);
		else if(position=="center")
			this.label=new TextBox(x, this.y+this.height/2,this.width,labelConf);
		else if (position=="bottom")
			this.label= new TextBox(x,this.y+this.height+10,this.width,labelConf);
		else
			throw new Error("Invalid position to label, choose: top, center or bottom:"+type);
		
		this.labelPosition=position;
	},
	
	
	
	createStereotype  (stereotypeConf){
		if(!stereotypeConf || stereotypeConf=="" || stereotypeConf=="null" ) {
			this.stereotype=false;
			return false;
		}
		
		this.stereotypeConfig={
			name:stereotypeConf.name,
	  		marginHeight:stereotypeConf.marginHeight, // convert to percent
	  		marginWidth:stereotypeConf.marginWidth,  // convert to percent
	  		position:stereotypeConf.position,
	  		
	  			
		}
		var x=this.x;
		var args={	
					defaultValue:"<<"+this.stereotypeConfig.name+">>",
					underlined:0,
					alignment:"center",
					id:-1,
					position:stereotypeConf.position, 
					marginWidth:toInteger(stereotypeConf.marginWidth), 
					marginHeight:toInteger(stereotypeConf.marginHeight)
				};
		var stereotype = new TextBox(x+this.width/2,this.y+6,this.width,args);
		this.updateLatelPositionGlobal(stereotype)
		stereotype.selectLabel=false;
		return stereotype;
	},
	
	
	updateLatelPositionGlobal(label){
		var x = this.x, y=this.y;
		var width = this.width;
		var height = this.height;
		var position = label.position;
		var marginWidth = label.marginWidth;
		var marginHeight = label.marginHeight;
//		console.log("width "+width + " "+ " height " + height + " "+ "position " + position);
//		console.log("marginWidth "+typeof(marginWidth) +" "+ " marginHeight "+ typeof(marginHeight) + " x "+x + " " + " y "+y)

		
//		console.log("marginWidth "+typeof(marginWidth))
//		console.log("marginHeight "+typeof(marginHeight))
		
		position= position.toLowerCase();
		if(position=="top"){
			label.updatePosition(x+marginWidth, y+marginHeight,this.width);
		}
		else if(position=="center")
			label.updatePosition( (x+marginWidth), (y + this.height/2 + marginHeight), this.width);	
		else if  (position=="bottom")
			label.updatePosition( (x+marginWidth), (y + this.height + marginHeight) , this.width);
		else	
			throw new Error("Invalid position to label, choose: top, center or bottom:"+position);
				
		
		
		
//		if(position=="top")
//			return label= new TextBox(x,y-5,this.width,labelConf);
//		else if(position=="center")
//			return label=new TextBox(x, y+this.height/2,this.width,labelConf);
//		else if (position=="bottom")
//			return label= new TextBox(x,y+this.height+10,this.width,labelConf);
//		else
//			throw new Error("Invalid position to label, choose: top, center or bottom:"+position);
	},
	
	
	
//	updateLatelPositionGlobal(label,position){
//		var x=this.x, y=this.y;
//		
//		if(position=="top"){
//			label.updatePosition(x,y-5);
//		}
//		else if(position=="center"){
//			// var yCenter=y+height/2-this.label.getHeight()/3;
//			var yCenter=y+height/2-label.getHeight()/3;
//			if(label.getNumberLine()==1)
//				yCenter=y+height/2;
//			label.updatePosition(x,yCenter,this.width);
//		}
//		else if (position=="bottom"){
//			label.updatePosition(x,y+height+10,this.width);
//		}
//		
//	},
	
	updateLabelPosition  (){
//		var x=this.x;
//		var y=this.y;
//		var height= this.height;
//		if(this.labelPosition=="top"){
//			this.label.updatePosition(x,y-5);
//		}
//		
//		else if(this.labelPosition=="center"){
//			// var yCenter=y+height/2-this.label.getHeight()/3;
//			var yCenter=y+height/2-this.label.getHeight()/3;
//			if(this.label.getNumberLine()==1)
//				yCenter=y+height/2;
//			this.label.updatePosition(x,yCenter,this.width);
//		}
//		else if (this.labelPosition=="bottom"){
//			this.label.updatePosition(x,y+height+10,this.width);
//		}
//		
		this.updateLatelPositionGlobal(this.label)
		
	}, 
	
	
	adjustLabel(ctx) {
		
		if(this.labelPosition=="center"){
			var width =this.label.getMaxWidthLine(ctx);
		    if( this.select && this.isAutoresize() ){ 
		    	this.width=width+30+this.margin;
		    	if(this.width<this.widthDefault)
		    		this.width=this.widthDefault;
//		    	if(this.width<=70){this.width=this.widthDefault;this.height=this.heightDefault;}
		    	 this.height=this.heightDefault+this.label.getHeight()-10;
		    }
//		    if(!this.isAutoresize())
//		    	return;
		   
		}
	},
	
	createFeatures  (features){
		if(!features||features=="null"){
			this.features=[];
			
			}
		this.features=features;
		
		for (var i in features )
			this[features[i].name]= convertType(features[i].type, features[i].value);
		
		
	},
	getTextlabel (){
		return this.label.getText();

	},	
	
	getJson  (nodes){
		var nodeAux = {};
		var properties=this.nodeType.properties;
		for (var i in properties)
			if(convertStringBoolen(properties[i].view)){
				nodeAux[properties[i].name]=this.getProperties(properties[i]);
			}
		nodeAux.features=[];
		for (var i in this.features){
			if(this.features[i].type=="Number")
				nodeAux[this.features[i].name]=parseInt(this[this.features[i].name]);
			else
				nodeAux[this.features[i].name]=this[this.features[i].name];
		}
			
		
// if(this.nodeType.includeElement){
// for(var k in nodes)
// if(this.containsNode(nodes[k]))
// nodeAux.features.push({name:"include",value:nodes[k].type})
// }
			
		
		var container=[];
		for( var i in this.containers){
			if(!this.containers[i].isEmpty())
			container.push(	this.getJSONContainer(this.containers[i]));
		} 
		nodeAux.containers=container;
		nodeAux.isConfigurable=this.nodeType.isConfigurable;
		return nodeAux;
	},
	
	getJSONContainer(container){
		var listTextBox=container.listTextBox;
		var listTextBoxAux=[];
		for(var i in listTextBox){
			listTextBoxAux.push(listTextBox[i].getText());
		}
		return {"name" : container.name, value: listTextBoxAux};
	},
	
	setContainers(node){
		if(!this.nodeType.containers || !node.containers )
			return;
	
		var containerNode=node.containers;
		var containers=this.nodeType.containers;
		
		for(var i in containerNode){
			for(var k in containers ){
				if(containerNode[i].name==containers[k].name){
					for(j in this.containers)
						if(this.containers[j].name==containers[k].name){
							this.containers[j].reset();
							var listBoxs = containerNode[i].listTextBox;
							for (l in listBoxs)
								this.containers[j].addTextBox(this.IdGenerete++,listBoxs[l].label)
							}
			}
		}
		}
		
	},
	
	getProperties(property){
		if(property.name=="label"){
			return this.getTextlabel();
			}
		else
			{
				if(property.type=="Number"){
					return parseInt(this[property.name]);
			}
			else
				return this[property.name];
			}
	},
	
	importContainer (listcontainers){
		for ( var i in listcontainers) {
			var name =listcontainers[i].name;
			var value=listcontainers[i].value;
			for(var i in this.containers)
				if(this.containers[i].name==name)
					this.importTextBox(this.containers[i],value);
		}
	},
	
	importTextBox  (container, listValue){
		for ( var i in listValue) {
			container.addTextBox(this.IdGenerete++, listValue[i])
		}
		
	},
	
	importNodeVariant(node){
		if(node.nodeType.isConfigurable)
			this.label.label=node.label.label;
		else
			this.containerName.getTextBox(0).label=node.containerName.getTextBox(0).label;
		
		this.setContainers(node);
		
		var list=node.edgesConnected;
		this.edgesConnected=[];
		for(var i in list)
			this.edgesConnected.push(list[i]);
		
		
	},
	
	incremenDegreeOut(){
		this.degreeOut++;
	},
	
	incremenDegreeIn(){
		this.degreeIn++;
	},
	
	decremenDegreeOut(){
		this.degreeOut--;
		if(this.degreeOut<0)
			this.degreeOut=0;
	},
	
	decremenDegreeIn(){
		this.degreeIn--;
		if(this.degreeIn<0)
			this.degreeIn=0;
	},
	
	checkDegreeIn() {
		if(this.nodeType.degreeIn=="unbounded" ||
				this.degreeIn<this.nodeType.degreeIn)
			return true;
		return false;
	},
	
	checkDegreeOut() {
		if(this.nodeType.degreeOut=="unbounded" ||
				this.degreeOut<this.nodeType.degreeOut)
			return true;
		return false;
	},
	
	checkDegree() {
		if(this.checkDegreeIn() && this.checkDegreeOut())
			return true;
		return false;
	},
	
	existContainer(){
		return (this.containers.length>0)
	},
	
	/* Coordinate Y of the Vertices */
	setX(x){ 
		this.x = x-this.width/2;
		(this.x<0)?this.x = 3:this.x;
	},
	/* Coordinate Y of the Vertices */
	setY(y){
		this.y = y-this.height/2;  
		(this.y<0)?this.y = 3:this.y;
	},
	
	/* get Coordinate Y of the Vertices */
	getX(){ 
		return this.x;
	},
	
	/* get Coordinate Y of the Vertices */
	getY(){
		return this.y;
	},
	isConfigurable(){
	 return this.nodeType.isConfigurable;
	},
	
	convertDirection (direc){
		var d=direc.toLowerCase();
		
		
		if(d.startsWith('south') || d.startsWith('west')  ){
			return this.southAndWest(d);
		}
			
			switch (d) {
			case "north" :
				return 0 ;
				break;
			case "northnortheast" :
				return 1 ;
				break;	
			case "northeast" :
				return 2 ;
				break;
			case "eastnortheast" :
				return 3 ;
				break;		
			case "east" :
				return 4 ;
				break;
			case "eastsoutheast" :
				return 5 ;
				break;
			case "northnorthwest" :
				return 15 ;
				break;	
			case "northwest" :
				return 14 ;
				break;
			case "center" :
				return 16 ;
				break;	
			default:
				throw new Error("Direction invalid:"+type);
				break;
			} 

	},
	
	
	
	southAndWest(direction){
		switch (direction) {
			case "southsoutheast" :
				return 7 ;
				break;	
			case "south" :
				return 8 ;
				break;
			case "southsouthwest" :
				return 9 ;
				break;	
			case "southwest" :
				return 10 ;
				break;
			case "southeast" :
				return 6 ;
				break;
			case "westsouthwest" :
				return 11 ;
				break;
			case "westnortwest" :
				return 13 ;
				break;
			case "west" :
				return 12 ;
				break;	
			default:
				throw new Error("Direction invalid:"+type);
				break;	
		}
	},
	
	resize (mx,my) {
		var oldx = this.x;
		var oldy = this.y;
		var sizeMin = 20;
		if(this.handlerSelected<8){
			
			switch (this.handlerSelected) {
				case 0:
					this.y = my;
					this.height += oldy - my;
					document.body.style.cursor = "n-resize";
					break;
				case 1:
					this.y = my;
					this.height += oldy - my;
					document.body.style.cursor = "n-resize";
					break;	
					
				case 2:
					this.y = my;
					this.width = mx - oldx;
					this.height += oldy - my;
					document.body.style.cursor = "ne-resize";
					break;
					
				case 3:
					this.width = mx - oldx;
					document.body.style.cursor = "e-resize";
					break;
				case 4:
					this.width = mx - oldx;
					document.body.style.cursor = "e-resize";
					break;
				case 5:
					this.width = mx - oldx;
					document.body.style.cursor = "e-resize";
					break;
					
				case 6:
					this.width = mx - oldx;
					this.height = my - oldy;
					document.body.style.cursor = "nw-resize";
					break;
					
				case 7:
					this.height = my - oldy;
					document.body.style.cursor = "n-resize";
					break;
			}
		}
		else{
			switch (this.handlerSelected) {
				case 8:
					this.height = my - oldy;
					document.body.style.cursor = "n-resize";
					break;	
				case 9:
					this.height = my - oldy;
					document.body.style.cursor = "n-resize";
					break;
					
				case 10:
					this.x = mx;
					this.width += oldx - mx;
					this.height = my - oldy;
					document.body.style.cursor = "ne-resize";
					break;
				
				case 11:
					this.x = mx;
					this.width += (oldx - mx);
					document.body.style.cursor = "e-resize";
					break;

				case 12:
					this.x = mx;
					this.width += (oldx - mx);
					document.body.style.cursor = "e-resize";
					break;
				case 13:
					this.x = mx;
					this.width += (oldx - mx);
					document.body.style.cursor = "e-resize";
					break;
					
				case 14:
					this.x = mx;
					this.y = my;
					this.width += oldx - mx;
					this.height += oldy - my;
					document.body.style.cursor = "nw-resize";
					break;
				case 14:
					this.x = mx;
					this.y = my;
					this.width += oldx - mx;
					this.height += oldy - my;
					document.body.style.cursor = "nw-resize";
					break;
				case 15:
					this.y = my;
					this.height += oldy - my;
					document.body.style.cursor = "n-resize";
					break;
			}
		}
		
		if (this.width < this.width) {
			this.width = sizeMin;
			this.x = oldx
		}
		if (this.height < sizeMin) {
			this.height = sizeMin;
			this.y = oldy
		}
// this.updateHandles();

	},
	
	
	resizeNorth(mx,my){
		this.y = my;
		this.height += this.y - my;
		document.body.style.cursor = "n-resize";
	},
	
	
	resizeEast(mx,my){
		this.width = mx - this.x;
		document.body.style.cursor = "e-resize";
	},
	resizeSouth(mx,my){
		this.height = my - this.y;
		document.body.style.cursor = "n-resize";
		
	},
	
	resizeWest(mx,my){
		this.x = mx;
		this.width += (this.x - mx);
		document.body.style.cursor = "e-resize";
	},
		
	
	isAutoresize(){
		return (this.nodeType.autoresize=='true' || this.nodeType.autoresize==true);
	},
	
	isNode(){
		return true;
	},
	
	isEdge(){
		return false;
	},
	
	existLabel(){
		return this.label!=undefined;
	},
	
	increaseOrder(){
		this.order++;
	},
	
	decreaseOrder(){
			this.order--;
	},
	
	islabelEditable(){
		return this.existLabel() && this.isLabelEditable;
	},
	
	existContainers(){
		return this.containers.length>0;
	},
	isDeleteBoxContainer() {
		if(this.containers.length>0)
			return true;
		return false;
	},
	isAnchorFixed(){
	    if(this.nodeType.isAnchorFixed())
	    	return true;
	    return false;
	},
	 
	setWidth(value){
//		if(value>this.widthDefault)
//		this.unSelected()
		this.width=value;
//		this.selected()
//		 this.updateHandles();
	},
	
	getWidthValue(){
		return this.width;
	},
	
	setHeight(value){ 
		this.height=value;
	},
	
	getHeight(){ 
		return this.height;
	},
	
	getName(){
		if(this.existLabel()){
			if(this.isConfigurable())
				return this.label.getText();
			
			return this.containerName.getTextBox(0).getText();
		}
		return "";
	},
	
	setName(text){
		if(this.islabelEditable())
			this.label.setText(text);
	},
	
	
	
	getPropertyContainers(){
		return false;
	},
	
	getType(){
		return this.type;
	},
	getUrl(){
		return this.nodeType.infoUrl;
	},
	
	getVariant(){
		return this.variant;
	},
}



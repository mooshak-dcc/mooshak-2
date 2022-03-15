/**
 * @author Helder Correia
 */



/**  
 *  show the grid in canvas in Eshu 
 *  @return {null}
*/
Eshu.prototype.showGrid = function() {
	
	var l = this.canvas.height / 10;
	for (var i = 0; i < l; i++) {
		
		this.ctx.save();
		this.ctx.beginPath();
		//this.initShadows();
		this.ctx.moveTo(0, i * this.GRID_SPACING);
		this.ctx.lineTo(this.canvas.width, i * this.GRID_SPACING);
		this.ctx.strokeStyle = this.GRID_LINE_COLOR; 
		;
		this.ctx.lineWidth = 1;
		this.ctx.stroke();
		this.ctx.restore();

	}
	var l = this.canvas.width / 10
	for (var i = 0; i < l; i++) {
		
		this.ctx.save();
		this.ctx.beginPath();
		//this.initShadows();
		this.ctx.moveTo(i * this.GRID_SPACING, 0);
		this.ctx.lineTo(i * this.GRID_SPACING, this.canvas.height);
		this.ctx.strokeStyle = this.GRID_LINE_COLOR;
		;
		this.ctx.lineWidth = 1;
		this.ctx.stroke();
		this.ctx.restore();
	}

}


Eshu.prototype.getheightGraphEditor=function() {
	return this.graphEditor.style.height;
	//return (this.HEIGHT-(this.toolbar.offsetHeight*1.4))
}

/**  
 *  set the position of toolbar to  Horizontal
 *  @return {null}
*/
Eshu.prototype.setPositionHorizontal=function() {
	this.div.style.cssFloat = "left";
	for(var i in this.languageOptionCanvas){
	//	this.languageOptionCanvas[i].style.display="inline";
		this.languageOptionCanvas[i].style.marginLeft = "5px";
	}
//	console.log( "formatPanel "+this.formatPanel.getWidth())
	var formatPanelWidth;
	(this.formatPanelVisibly)?formatPanelWidth=220:formatPanelWidth=0;
//	this.formatPanel.setWidth(formatPanelWidth);
//	this.formatPanel.createFormatPanel()
//	console.log(formatPanelWidth + " this.formatPanelVisibly")
	this.toolbarHeight=this.toolbar.offsetHeight;
	this.graphEditor.style.height= (this.HEIGHT-(this.toolbar.offsetHeight*1.4))+"px";
	this.graphEditor.style.width = (this.WIDTH*0.97 - formatPanelWidth)+"px"; //60 +'%';
	
	
	
	
	this.ctx.fillStyle = this.BACKGROUND_COLOR;
	this.canvas.style.border= this.BORDER_WIDTH+"px solid";
	this.canvas.style.borderColor=this.BORDER_COLOR;
	this.graphEditor.style.overflowY="auto"; 
	this.graphEditor.style.margin="1%";
	
	this.draw();
	this.positionHorizontal=true;
	this.TOOLBAR_POSITION="horizontal";
	
	this.wrapper.style.width =( this.WIDTH ) + "px";
	this.wrapper.style.height=( this.HEIGHT ) + "px";
	 
}

/**  
 *  set the position of toolbar to Vertical
 *  @return {null}
*/
Eshu.prototype.setPositionVertical=function() {
	this.div.style.cssFloat = "none";
	for(var i in this.languageOptionCanvas){
		this.languageOptionCanvas[i].style.display="block"; 
		this.languageOptionCanvas[i].style.marginTop = "1px";
	}
	this.graphEditor.style.width = this.WIDTH-this.toolbar.offsetWidth*1.5+"px";
	this.graphEditor.style.height=(this.HEIGHT*0.95)+"px";
	
	if(this.canvas.height<this.graphEditor.offsetHeight)
		this.canvas.height=this.HEIGHT*0.95;
	
	this.ctx.fillStyle = this.BACKGROUND_COLOR;
	this.canvas.style.border= this.BORDER_WIDTH+"px solid";
	this.canvas.style.borderColor=this.BORDER_COLOR;
	this.graphEditor.style.overflowY="auto"; 
	this.graphEditor.style.margin="1%";
	
	this.positionHorizontal=false;
	this.TOOLBAR_POSITION="vertical";
	
	this.wrapper.style.width =( this.WIDTH ) + "px";
	this.wrapper.style.height=( this.HEIGHT ) + "px";
	
	this.draw();
}


/**
 * set the the position of the toolbar
 *  @param {string} position (vertical or horizontal)
 *  @return {null}
*/
Eshu.prototype.setPosition = function(position) {
	//if(position =="vertical")
	//	this.setPositionVertical();
//	else
		this.setPositionHorizontal();
}


/**  
 *  resize the canvas of eshu by node or rectangleSelection
 *  @return {null}
*/
Eshu.prototype.resizeCanvasNode = function() {
	var adjust=false;
	if (this.graph.isElementSelection()) {

		if (this.graph.selection.x  + this.graph.selection.width > this.canvas.width) {
			this.canvas.width += this.graph.selection.width+200;
			this.WIDTHCANVAS=this.canvas.width;
			adjust=true;
		}
		if (this.graph.selection.y + this.graph.selection.height > this.canvas.height) {
			this.canvas.height += this.graph.selection.height+200;
			this.HEIGHTCANVAS=this.canvas.height;
			adjust=true;
		}
	}
	if(adjust){
//		this.graph.resizeGraph(this.canvas.width,this.canvas.height);
//		this.WIDTH=this.canvas.width;
//		this.HEIGHT=this.canvas.height;
//		wid=this.canvas.width;
		this.graphEditor.width=this.div.offsetWidth +"px";
		this.graphEditor.height=this.div.offsetHeight+"px";
		this.ctx.fillStyle = this.BACKGROUND_COLOR;
		this.canvasSizeReset();
		this.updateCanvasSize();
	}
	
}

/**  
 *  update the canvas of eshu by node 
 *  @return {null}
*/
Eshu.prototype.updateCanvasSize = function() {
	
	this.canvas.width=this.WIDTHCANVAS;
	this.canvas.height=this.HEIGHTCANVAS;
	this.graph.resizeGraph(this.WIDTHCANVAS, this.HEIGHTCANVAS);
	this.ctx.fillStyle = this.BACKGROUND_COLOR;
	
}

/**  
 *  update the canvas of eshu by node 
 *  @return {null}
*/
Eshu.prototype.updateCanvasSizeImport = function() {
//	alert(wid);
	
	var nodes=this.graph.nodes.getAll();
	for(var i in nodes){
	
			if (nodes[i].x + nodes[i].width > this.canvas.width) {
				this.canvas.width += nodes[i].width+200;
				this.WIDTHCANVAS=this.canvas.width;
				adjust=true;
			}
			if (nodes[i].y + nodes[i].height > this.canvas.height) {
				this.canvas.height += nodes[i].height+200;
				this.HEIGHTCANVAS=this.canvas.height;
				adjust=true;
		}
	}
	
	this.updateCanvasSize();
	
}

/**  
 *  resize the canvas of eshu by rectangleSelection
 *  @return {null}
*/
Eshu.prototype.resizeCanvasRectangleSelection = function() {
	var adjust=false;
	if (!this.graph.rectangleSelection.isEmpty()) {
		if (this.graph.rectangleSelection.getXMax()  > this.canvas.width) {
			this.canvas.width += this.graph.rectangleSelection.width+200;
			adjust=true;
		}
		if (this.graph.rectangleSelection.getYMax() > this.canvas.height) {
			this.canvas.height += this.graph.rectangleSelection.height+200;
			adjust=true;
		}
	}
	if(adjust){
		this.graph.resizeGraph(this.canvas.width,this.canvas.height);
		this.WIDTHCANVAS=this.canvas.width;
		this.HEIGHTCANVAS=this.canvas.height;
		this.ctx.fillStyle = this.BACKGROUND_COLOR;
	}
}


/**  
 *  reset the canvas to the default width and height   
 *  @return {null}
*/
Eshu.prototype.canvasSizeReset = function() {
	var nodes=this.graph.nodes.getAll();
	var edges=this.graph.edges.getAll();

	this.nodes=new QuadTree(0,0,this.canvas.width,this.canvas.height);
	this.edges=new QuadTree(0,0,this.canvas.width,this.canvas.height);
	for(var i in nodes)
		this.nodes.insert(nodes[i]);
	for(var i in edges)
		this.edges.insert(edges[i]);
	
	
}

/**  
 *  resize the canvas to the by the width and height  
 *  @param {Number} width
 *  @param {Number} height 
 *  @return {null}
*/
Eshu.prototype.resize = function(width,height) {
	this.HEIGHT=height;
	this.WIDTH=width;
	var update=false;
	if(this.WIDTHCANVAS<width){
		this.WIDTHCANVAS=width;
		update=true;
	}
	if(this.HEIGHTCANVAS<height){
		this.HEIGHTCANVAS=height;
		update=true;
	}
	
	this.wrapper.style.width =( this.WIDTH ) + "px";
	this.wrapper.style.height=( this.HEIGHT ) + "px";
	
	if(update){
		this.canvas.width=  this.WIDTHCANVAS;
		this.canvas.height= this.HEIGHTCANVAS*0.80; 
		this.graph.resizeGraph(this.canvas.width,this.canvas.height);
	}
	this.div.style.width = this.WIDTH+"px";
	this.div.style.height= this.HEIGHT+"px";
	this.setPosition(this.TOOLBAR_POSITION);
	
//	alert(this.formatPanel);
	if(this.formatPanel)
		this.formatPanel.updateWindowsHeight();
	
	this.graphEditor.width=this.div.offsetWidth +"px";
	this.graphEditor.height=this.div.offsetHeight+"px";
	
}


/**  
 *  Depending on the value in the parameter, displays or hides the tool bar 
 *  @param {Number} width
 *  @param {Number} height 
 *  @return {null}
*/
Eshu.prototype.showToolbar = function(value) {
	if (value) {
		this.toolbar.style.visibility = 'visible';
		this.toolbar.style.display = 'block';
		this.drawToolbar();
		this.setPosition(this.TOOLBAR_POSITION);
	} else {
		this.canvas.width= this.WIDTH;
		this.canvas.height=this.HEIGHT; 
		this.toolbar.style.visibility = 'hidden';
		this.toolbar.style.display = 'none';
		this.graphEditor.style.width = this.WIDTH+"px";
		this.graphEditor.style.height= this.HEIGHT+"px";
		this.ctx.fillStyle = this.BACKGROUND_COLOR;
		this.graph.resizeGraph(this.WIDTH,this.HEIGHT);
		;
		
	}
	
	
}






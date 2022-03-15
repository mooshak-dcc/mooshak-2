/**
 * @author Helder Correia
 */

Eshu.prototype.WIDTH=700;
Eshu.prototype.HEIGHT=400;
Eshu.prototype.WIDTHCANVAS=700;
Eshu.prototype.HEIGHTCANVAS=400;
Eshu.prototype.BACKGROUND_VISIBLE=false;
Eshu.prototype.BACKGROUND_COLOR='#FFFFFF';
Eshu.prototype.BORDER_WIDTH=1;
Eshu.prototype.BORDER_COLOR='#000';
Eshu.prototype.GRIDVISIBLE=false;
Eshu.prototype.GRID_LINE_COLOR='#000';
Eshu.prototype.GRID_SPACING=20;
Eshu.prototype.TOOLBAR_POSITION="horizontal";



/**  
 *  set the width of Eshu 
 *  @param {Number} width
 *  @return {null}
*/
Eshu.prototype.setWidth = function(width) {
	this.WIDTH=width;
	this.div.style.width = this.WIDTH+"px";
}

/**
 *  get the width of Eshu 
 *  @return {Number} width
*/
Eshu.prototype.getWidth = function() {
	return this.WIDTH;
}


/**
 *  set the height of Eshu 
 *  @param {Number} height
 *  @return {null}
*/
Eshu.prototype.setHeigth = function(height) {
	this.HEIGHT=height;
	this.div.style.height = (height )+"px";
}

/**
 *  get the height of Eshu 
 *  @return {Number} width
*/
Eshu.prototype.getHeigth = function() {
	return this.HEIGHT;
}


/**
 *  get the height of canvas in Eshu 
 *  @return {Number} width
*/
Eshu.prototype.getHeigthEditor = function() {
	return this.graphEditor.style.width
}

/**
 *  set the grid in eshu as visible or not
 *  @param {Boolean} gridVisible
 *  @return {null}
*/
Eshu.prototype.setGridVisible = function(gridVisible) {
	this.GRIDVISIBLE=gridVisible;
}

/**
 *  get the the field that define if the grid is visible or not
 *  @return {Boolean} GRIDVISIBLE
*/
Eshu.prototype.getGridVisible = function() {
	return this.GRIDVISIBLE;
}


/**
 *  set the line color of grid in eshu 
 *  @param {String} gridLineColor
 *  @return {null}
*/
Eshu.prototype.setGridLineColor = function(gridLineColor) {
	this.GRID_LINE_COLOR=gridLineColor;
}

/**
 *  get the line color of grid in eshu 
 *  @return {String} GRID_LINE_COLOR
*/
Eshu.prototype.getGridLineColor = function() {
	return this.GRID_LINE_COLOR;
}

/**
 * set the line  spacing of grid in eshu 
 *  @param {String} gridSpacing
 *  @return {null}
*/
Eshu.prototype.setGridSpacing = function(gridSpacing) {
	this.GRID_SPACING = gridSpacing;
}


/**
 * get the line spacing of grid defined in eshu 
 *  @return {String} GRID_SPACING
*/
Eshu.prototype.getGridSpacing = function() {
	return this.GRID_SPACING;
}

/**
 * set the background visible or disable the background  (white) 
 *  @param {Boolean} visible
 *  @return {null}
*/
Eshu.prototype.setBackgroundVisible = function(visible) {
	this.BACKGROUND_VISIBLE = visible;
}

/**
 * get the background visibility of Eshu
 *  @return {Boolean} BACKGROUND_VISIBLE
*/
Eshu.prototype.getBackgroundVisible = function() {
	return this.BACKGROUND_VISIBLE;
}

/**
 * set the background color of the canvas in eshu
 *  @param {Boolean} visible
 *  @return {null}
*/
Eshu.prototype.setBackgroundColor = function(color) {
	this.BACKGROUND_COLOR = color;
}

/**
 * get the background color of canvas in Eshu
 *  @return {Boolean} BACKGROUND_VISIBLE
*/
Eshu.prototype.getBackgroundColor = function() {
	return this.BACKGROUND_COLOR;
}

/**
 * reset the background color to white ('#FFFFFF')
 *  @return {null}
*/
Eshu.prototype.resetBackgroundColor = function() {
	this.BACKGROUND_COLOR='#FFFFFF';
}


/**
 * set the border with of the toolbar
 *  @param {Number} borderWidth
 *  @return {null}
*/
Eshu.prototype.setToolbarBorderWidth = function(borderWidth) {
	this.toolbar.style.border=borderWidth+"px solid";
}

/**
 * get the border with of the toolbar
 *  @param {Number} borderWidth
 *  @return {null}
*/
Eshu.prototype.getToolbarBorderWidth = function() {
	return this.toolbar.style.border;
}

/**
 * set the border color of the toolbar
 *  @param {Number} borderColor
 *  @return {null}
*/
Eshu.prototype.setToolbarBorderColor = function(borderColor) {
	this.toolbar.style.borderColor=borderColor;
}

/**
 * get the border with of the toolbar
 *  @return {Number} borderColor
*/
Eshu.prototype.getToolbarBorderWidth = function() {
	return this.toolbar.style.borderColor;
}

/**
 * set the background color of the toolbar
 *  @param {Number} backgroundColor
 *  @return {null}
*/
Eshu.prototype.setToolbarBackgroundColor = function(backgroundColor) {
	this.toolbar.style.backgroundColor=backgroundColor;
}


/**
 * get the background color of the toolbar
 *  @return {Number} backgroundColor
*/
Eshu.prototype.getToolbarBackgroundColor = function() {
	return this.toolbar.style.backgroundColor;
}


/**
 * set the ValidationSyntaxe
 *  @param {Number} value
 *  @return {null}
*/
Eshu.prototype.setValidationSyntaxe = function(value) {
	this.validationSyntaxe=value;
	
}


/**
 * increase the order of a element selected in Eshu
 *  @return {null}
*/
Eshu.prototype.increaseOrder = function (){
	if(this.graph.isNodeEdgeSelection()){
		this.graph.selection.increaseOrder();
		if(this.graph.selection.order>this.maxOrder)
			this.maxOrder++;
		this.draw();
	}
}

/**
 * decrease the order of a element selected in Eshu
 *  @return {null}
*/
Eshu.prototype.decreaseOrder = function (){
	if(this.graph.isNodeEdgeSelection())
		this.graph.selection.decreaseOrder();
	this.draw();
}

/**
 * set the width of a element selected in Eshu
 *  @param {Number} value
 *  @return {null}
*/
Eshu.prototype.setWidthElement = function (width){
		this.graph.setWidthElement(width);
		this.draw();
}

/**
 * get the width of a element selected in Eshu
 *  @return {Number} 
*/
Eshu.prototype.getWidthElement = function (){
	return this.graph.getWidthElement();
}

/**
 * set the height of a element selected in Eshu
 *  @param {Number} value
 *  @return {null} 
*/
Eshu.prototype.setHeightElement = function (value){
	this.graph.setHeightElement(value);
	this.draw();
}

/**
 * get the height of a element selected in Eshu
 *  @param {Number} value
 *  @return {Number} 
*/
Eshu.prototype.getHeightElement = function (value){
	return this.graph.getHeightElement();
}


/**
 * set the position X of a element selected in Eshu
 *  @param {Number} value
 *  @return {null} 
*/
Eshu.prototype.setXElement = function (value){
	this.graph.setXElement(value);
	this.draw();
}

/**
 * set the position Y of a element selected in Eshu
 *  @param {Number} value
 *  @return {null} 
*/
Eshu.prototype.setYElement = function (value){
	this.graph.setYElement(value);
	this.draw();
}

/**
 * get the position X of a element selected in Eshu
 *  @return {Number} 
*/
Eshu.prototype.getXElement = function (){
	return this.graph.getXElement();
}

/**
 * get the position Y of a element selected in Eshu
 *  @return {Number} 
*/
Eshu.prototype.getYElement = function (){
	return this.graph.getYElement();
}

/**
 * set the align Horizontally  to nodes selected  in Eshu
 *  @param {Number} value (top center and bottom )
 *  @return {Number} 
*/
Eshu.prototype.alignHorizontally = function (align){
	this.graph.alignHorizontally(align);
	this.draw();
}

/**
 * set the align Vertically to nodes selected  in Eshu
 *  @param {Number} value (left, middle and right)
 *  @return {Number} 
*/
Eshu.prototype.alignVertically = function (value){
	this.graph.alignVertically(value);
	this.draw();
}

/**
 *  Equalize the size of nodes  in Eshu to size maximum  of nodes selected
 *  @return {null} 
*/
Eshu.prototype.equalizeMax = function (){
	this.graph.equalize();
	this.draw();
}

/**
 *  Equalize the size of nodes  in Eshu to size minimum  of nodes selected
 *  @return {null} 
*/
Eshu.prototype.equalizeMin = function (){
	this.graph.equalize(true);
	this.draw();
}

/**
 *  get the name in the label selected in eshu
 *  @return {string} the name
*/
Eshu.prototype.getNameNodeSeleted = function (){
	return this.graph.getName();
}

/**
 *  add a textbox in label, if the type is  ComplexType
 *  @param {Number} idContainer the id of container
 *  @return {string} the name
*/
Eshu.prototype.addTextBoxContainer = function (idContainer){
	
	this.graph.addTextBoxContainer(idContainer);
	this.draw();
}


/**
 *  remove a textbox in label, if the type is  ComplexType
 *  @param {Number} idContainer the id of container
 *  @param {Number} idTextBox the id of textBox
 *  @return {null} 
*/
Eshu.prototype.removeTextBoxContainer = function (idContainer, idTextBox){
	this.graph.removeTextBoxContainer(idContainer, idTextBox);
	this.draw();
}

/**
 *  get the urls of the node or edge selected in eshu
 *  @return {Array-string} urls
*/
Eshu.prototype.getUrlElementSeleted = function (){
	return this.graph.getUrlElementSeleted();
}


/**
 *  reset the eshu, remove all components and create a new
 *  @return {null} 
*/
Eshu.prototype.resetEshu= function() {
	
//	if(this.div){
//	while(this.div.hasChildNodes()) {
//		this.div.removeChild(this.div.firstChild);
//	   
//	    this.languageOptionCanvas=[];
//	}
//	}
	
	if(this.toolbar){
		while(this.toolbar.hasChildNodes()) {
			this.toolbar.removeChild(this.toolbar.firstChild);
		   
		    this.languageOptionCanvas=[];
		}
		}
	this.clear();
	
	this.commandStack=new CommandStack(this.limitUndo);
	this.graph=new Graph(this.canvas.width,this.canvas.height);
	this.elementsTypes=new Map();
	this.imagesSVG= new Map();
	this.graphState=0;
	this.unSelectOperation();

}




//**********Auxiliary function*************************

function removeSpaceString(string){
		if(!string) return string;
	return string.replace(/\s/g, '')
}

function toInteger(string){
	var resul=0;
	try {
		resul=parseInt(string);
	} catch (e) {
		console.log("Error convert string to integer: "+ e);
	}
	
	return resul;
}


function tointeger(value){
	var valueConverted;
	if((valueConverted=parseInt(value)))
		return valueConverted;
		throw new Error("Erro in convert value to integer"+value);
	
}


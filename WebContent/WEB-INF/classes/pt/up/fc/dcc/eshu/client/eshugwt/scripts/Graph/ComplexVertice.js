ComplexVertice.prototype = Object.create(Vertices.prototype);
ComplexVertice.prototype.roundCorner=0;

function ComplexVertice(x, y, id) {
	Vertices.call(this, x, y, id);
	
}

ComplexVertice.prototype.draw = function(ctx) {
//	this.updateHandles();
//	
	
	this.updatePositionContainers();
	ctx.save();
	if(this.modify!=""){
		 ctx.globalAlpha  = 0.3;
		 this.stereotype.alpha=true;
		 this.containerName.alpha=true;
	}
	ctx.beginPath();
	ctx.strokeStyle = this.lineColor;
	ctx.fillStyle="white";
	if(this.nodeType.multiElement){
		
		var startHeight=this.containerName.getHeight()+5;
		var halfWidth =this.width/2; 
		
		ctx.fillRect(this.x+this.middle, this.y+this.middle, this.width, startHeight);
		ctx.strokeRect(this.x+this.middle, this.y+this.middle, this.width, startHeight);
		
	     ctx.setLineDash([3]);
		 ctx.moveTo(this.x+halfWidth,this.y+startHeight);
		 ctx.lineTo(this.x+halfWidth,this.y+this.height);
		 ctx.fillStyle = 'white';
	}
	else if(this.roundCorner==0){
		ctx.fillRect(this.x+this.middle, this.y+this.middle, this.width, this.height);
		ctx.strokeRect(this.x+this.middle, this.y+this.middle, this.width, this.height);
	}
	else{
		//	(ctx, x, y, width, height, radius, fill, stroke)
		this.roundRect(ctx,this.x+this.middle, this.y+this.middle, this.width, this.height,this.roundCorner,false,true);
	}
	
//	ctx.fillRect(this.x+this.middle, this.y+this.middle, this.width, startHeight);
//	ctx.strokeRect(this.x+this.middle, this.y+this.middle, this.width, startHeight);
//
	ctx.lineWidth=1;
	ctx.stroke();
	ctx.restore();
	 
	for(var i in this.containers){
		this.drawLine(ctx,this.containers[i]);	
		
	}
	
	if(this.select){
		this.drawBorder(ctx);
		 this.adjustLabel(ctx);
	}
//	if(this.isLabelEditable){
//		this.label.showBar=false;
//		this.label.selectLabel=false;
//	}
	this.containerName.draw(ctx);
	for(var i in this.containers){
		this.containers[i].draw(ctx);
	}
	
	if(this.stereotype )
		this.stereotype.draw(ctx);
	
	
	
//	var startHeight=this.containerName.getHeight()+5;
	
	 
	ctx.restore();
	
	
}


ComplexVertice.prototype.drawLine = function (ctx,container){
	
	ctx.save();
	ctx.beginPath();
	ctx.strokeStyle = this.lineColor;

	if(container.lineDash){
		ctx.setLineDash([3]);
	}
	
	ctx.moveTo(this.x+this.middle, container.positionStart);
	ctx.lineTo(this.x+this.width, container.positionStart);
	
	
	
	if(this.modify!=""){
		this.container.alpha=true;
	}
	ctx.lineWidth=1;
	ctx.stroke();
	ctx.restore();
}


ComplexVertice.prototype.setNodeType = function (nodeType){
//	this.isLabelEditable=true;
	this.nodeType=nodeType;
	this.type=nodeType.type;
	this.imageIconTollBarSVG = nodeType.getImageIconTollBarSVG();
	this.widthDefault = nodeType.width; 
	this.heightDefault =nodeType.height; 
	this.width = nodeType.width ; 
	this.height = nodeType.height ; 
	this.listTypeCanBeconnected=[];
	this.variant=nodeType.variant;
	this.edgesConnected=[];
//	this.updateHandles();
	this.createLabel(nodeType.labelConf);	
//	this.updateHandles();
	this.createHandlers(nodeType.listHandlers);
	this.createAnchor(nodeType.listAnchors.anchor);
	this.nodeType=nodeType;
	this.createFeatures(nodeType.features);
	this.createContainer(nodeType.containers);
	this.stereotype=this.createStereotype(nodeType.stereotype);
	this.updateHandles();
	this.roundCorner=nodeType.roundCorner;
}

ComplexVertice.prototype.createLabel=function(labelConf){
	this.containerName= new ContainerTextBox(this.x,this.y,labelConf.defaultValue,labelConf.defaultValue);
	this.containerName.underlined=parseInt(labelConf.underlined);
	this.containerName.alignment=labelConf.alignment;
	this.containerName.addTextBox(0);
	this.containerName.disabled=labelConf.disabled;
	this.label=this.containerName.getTextBox(0);
	this.label.showBar=true;
	this.label.selectLabel=true;
	
	this.containerSelected=0;
	this.isLabelEditable=!labelConf.disabled;
	
	
	//this.containerName.setConfig(labelConf)
	
}

ComplexVertice.prototype.addElement = function (x,y){

	this.updatePositionContainers();
	for(var i in this.containers){
		if(this.containers[i].addTextBoxes && this.containers[i].contains(x,y,this.width)){
			this.label.selectLabel=false;
			this.label.showBar=false;
			this.label=this.containers[i].addTextBox(this.IdGenerete++);
			this.containerSelected=i;
//			this.label.showBar=true;
//			this.label.selectLabel=true;
			this.label.selected();
		}
	}
}

ComplexVertice.prototype.getPropertyContainers = function (){
	var container=[];
	if(this.containers.length==0) return false;
	for( var i in this.containers){
//		if(!this.containers[i].isEmpty())
			container.push(	this.getPropertyContainer(this.containers[i],i));
	} 
	return container;
}

ComplexVertice.prototype.getPropertyContainer= function (container ,index){
	var listTextBox=container.listTextBox;
	var listTextBoxAux=[];
	for(var i in listTextBox){
		listTextBoxAux.push({
			'id':listTextBox[i].getId(),
			'text':listTextBox[i].getText()});
	}
	return {"name" : container.name, 'index':index, 'addTextBoxes':container.addTextBoxes, disabled:container.disabled,value: listTextBoxAux};
},



ComplexVertice.prototype.updatePositionContainers = function() {

	var oldHeight=JSON.parse(JSON.stringify( this.height));
	this.updateHandles();
	this.updateAnchor();
	if(this.isAutoresize()){
		var valueIncrease=2;
		var heightStereotype=0;
		var nameHeight=this.containerName.getHeight();
		if(this.stereotype ){
			this.stereotype.updatePosition(this.x,this.y+this.stereotype.height,this.width);
			heightStereotype=this.stereotype.height;
		}
		
		if(this.containers.length==0){
			(nameHeight<this.heightDefault)?this.height = this.heightDefault : this.height=nameHeight
			this.containerName.updatePosition(this.x,this.y+nameHeight/2+heightStereotype/2,this.width);
			return;
		}
		this.containerName.updatePosition(this.x,this.y+nameHeight/2+heightStereotype-valueIncrease,this.width); //nameHeight
		this.height=nameHeight+valueIncrease+heightStereotype;
	
		
		for(var i=0; i<this.containers.length;i++){
//			alert(this.x + " Complexvertr");
			this.containers[i].positionStart=this.y+this.height+0.5;
			this.containers[i].updatePosition((this.x-this.width/2),this.containers[i].positionStart,this.width);
			this.height+=this.containers[i].getHeight();
		}
			this.height+=valueIncrease;
	}
	else{
		
		if(this.containers.length==0){
			this.containerName.updatePosition(this.x,this.y+this.containerName.getHeight()/2,this.width);
			return;
		}
		
		this.containerName.updatePosition(this.x,this.y+this.containerName.getHeight()/2,this.width);
		var height=this.containerName.getHeight();//this.containerName.height;
		var valueIncrease=this.height-this.containerName.getHeight();
		var total=this.getNumberTotalTextBox();
		
		for(var i=0; i<this.containers.length;i++){
			this.containers[i].positionStart=this.y+0.5+height;
			this.containers[i].updatePosition(this.x,this.containers[i].positionStart)+height;
			
//			var numverTextBox=this.containers[i].numberTextBox();
//			if(numverTextBox==0)
//				numverTextBox=1;
			height+=( this.containers[i].numberTextBox()/total)*valueIncrease;
		}
		
	}
	if(this.height<this.heightDefault){
		this.height=this.heightDefault
	}
		
}


ComplexVertice.prototype.getNumberTotalTextBox = function() {
	var total=0;
	for(var i=0; i<this.containers.length;i++)
		total+=this.containers[i].numberTextBox();
	return total;
	
}

ComplexVertice.prototype.insideListTextBox = function(x,y) {
	
		for(var i in this.containers){
			label=this.containers[i].insideListTextBox(x,y);
			if(label!=undefined){
				this.containerSelected=i;
				break;
			}
		}
	
	if(label==undefined){
			label=this.containerName.insideListTextBox(x,y);
			this.containerSelected=-1;
		}
		
	if(label!=undefined && label.id!=this.label.id){
		this.selectTextBox(label)
	}
	this.isLabelEditable=!this.label.isDisable();
},

ComplexVertice.prototype.isLabelSectedEditable = function(){
	return this.label.isDisable();
},

ComplexVertice.prototype.selectTextBox= function(label) {
	this.label.selectLabel=false;
	this.label.showBar=false;
	this.label=label;
	this.label.selected();
}

ComplexVertice.prototype.deleteBoxContainer= function() {
	if(this.containerSelected!=-1){
		this.label.selectLabel=false;
		this.label.showBar=false;
		this.containers[this.containerSelected].removeTextBox(this.label.id);
		this.label=this.containerName.getTextBox(0);
		this.containerSelected=-1;
		this.label.showBar=true;
		this.label.selectLabel=true;
	}
}


ComplexVertice.prototype.removeTextBoxContainer= function(idContainer, idTextBox) {
	this.containers[idContainer].removeTextBox(idTextBox);
}

ComplexVertice.prototype.addTextBoxContainer= function(idContainer) {
	if(this.containers[idContainer].addTextBoxes)
		this.containers[idContainer].addTextBox(this.IdGenerete++);}





ComplexVertice.prototype.adjustLabel = function(ctx) {
	var label=this.getWidth(ctx);
	var width; //=label.width;
	(label)?width=label.getMaxWidthLine(ctx):width=1;
	
	if ( this.isAutoresize() || !this.isAutoresize()&& width>this.width ) {
		
		if(label.alignment=="left"){	
			this.width = width + 20;
		}
		else
			this.width = width+ width/2;
		if (this.width <= this.widthDefault) {
			this.width = this.widthDefault;
//			this.height = this.heightDefault;
		}
		
	}
	if(this.width<this.widthDefault)
		this.width<this.widthDefault;
	
}


ComplexVertice.prototype.getWidth = function(ctx) {

	var maxLabel=this.containerName.getWidth(ctx);
	for(var i in this.containers ){
		var labelAux=this.containers[i].getWidth(ctx);
		if(labelAux && maxLabel.getMaxWidthLine(ctx)<labelAux.getMaxWidthLine(ctx))
			maxLabel = labelAux;
	}
	if(this.stereotype && this.stereotype.getMaxWidthLine(ctx)>maxLabel.getMaxWidthLine(ctx))
		return this.stereotype;
	return maxLabel;
	
}

ComplexVertice.prototype.createContainer = function(containers){
	for(var i in containers ){
		var container= new ContainerTextBox(this.x,this.y,containers[i].name,containers[i].defaultText);
//		container.alignment = containers[i].alignment;
//		container.underlined = containers[i].underlined;
		container.setConfig(containers[i]);
		var size=containers[i].textboxDefaultNumber;
		for (var k=0; k<size;k++)  {
			container.addTextBox(this.IdGenerete++);
			
		}
		this.containers.push(container);
	}
}

ComplexVertice.prototype.resetContainer = function(){
	for(var i in this.containers ){
		this.containers[i].reset();
	}
}

ComplexVertice.prototype.getTextlabel = function(){
	if(!this.nodeType.isConfigurable){
		return  this.containerName.getTextBox(0).getText();
		
	}
	return this.label.getText();

}	

ComplexVertice.prototype.roundRect = function (ctx, x, y, width, height, radius, fill, stroke) {
	 
	  if (typeof stroke == 'undefined') {
	    stroke = true;
	  }
	  if (typeof radius === 'undefined') {
	    radius = 5;
	  }
	  if (typeof radius === 'number') {
	    radius = {tl: radius, tr: radius, br: radius, bl: radius};
	  } else {
	    var defaultRadius = {tl: 0, tr: 0, br: 0, bl: 0};
	    for (var side in defaultRadius) {
	      radius[side] = radius[side] || defaultRadius[side];
	    }
	  }
	  ctx.beginPath();
	  ctx.moveTo(x + radius.tl, y);
	  ctx.lineTo(x + width - radius.tr, y);
	  ctx.quadraticCurveTo(x + width, y, x + width, y + radius.tr);
	  ctx.lineTo(x + width, y + height - radius.br);
	  ctx.quadraticCurveTo(x + width, y + height, x + width - radius.br, y + height);
	  ctx.lineTo(x + radius.bl, y + height);
	  ctx.quadraticCurveTo(x, y + height, x, y + height - radius.bl);
	  ctx.lineTo(x, y + radius.tl);
	  ctx.quadraticCurveTo(x, y, x + radius.tl, y);
	  ctx.closePath();
	  
	   if (fill) {
	    ctx.fill();
	  }
	  if (stroke) {
	    ctx.stroke();
	  }
	
//	  ComplexVertice.prototype.getName = function () {
////			if(this.existLabel())
////		  alert(this.containerName.getTextBox(0).getText())
//				return this.containerName.getTextBox(0).getText(); //  JSON.stringify(this.containerName.getTextBox(0).label)
//		}

	  ComplexVertice.prototype.setName = function (text) {
		  if(!this.label.isDisable()){
			this.containerName.getTextBox(0).setText(text); //  JSON.stringify(this.containerName.getTextBox(0).label)
			
		  }
	}
	  
	  ComplexVertice.prototype.setNameContainer= function (text,idContainer, idTextBox) {
		  this.containers[idContainer].setTextBox(idTextBox,text);
	  }
	  
		
}

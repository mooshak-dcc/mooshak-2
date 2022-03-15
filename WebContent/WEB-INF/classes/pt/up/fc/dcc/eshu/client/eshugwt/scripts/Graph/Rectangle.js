Rectangle.prototype = Object.create(Vertices.prototype);

function Rectangle(x, y, id) {
	Vertices.call(this, x, y, id);
	this.type = "Rectangle";
	this.pathIconTollBarSVG="./image/actorIcon.svg";
	this.pathimgSVG="./image/actor8.svg";
	//this.pathIconTollBarSVG="/home/hppc/workspace/Eshu2.0Beta/WebContent/image/actorIcon.svg";
	//this.loadImage();
	this.listTypeCanBeconnected=["useCase","actor"];
	this.edgesConnected=[];
	//this.label= new TextBox(this.x+this.width/2,this.y+this.height+10,this.width,this.type);
	
}

Rectangle.prototype.draw = function(ctx) {
	this.updateHandles();
	ctx.save();
	if(this.modify!="")
		 ctx.globalAlpha  = 0.3;
	ctx.beginPath();
	ctx.strokeStyle = this.lineColor;
	ctx.rect(this.x+this.middle, this.y+this.middle, this.width, this.height);
//	ctx.fillStyle = this.backgroundColor;
	ctx.lineWidth=1;
	//ctx.fill();
	ctx.stroke();
	ctx.restore();
	
	 if(this.existLabel()){
//		 	this.adjustLabel();
		 	this.label.updatePosition(this.x,this.y+this.label.getHeight(),this.width);
			this.label.draw(ctx);
	 }
	
	if(this.select)
		this.drawBorder(ctx);
	
}


Rectangle.prototype.setNodeType = function (nodeType){
	this.nodeType=nodeType;
	this.type=nodeType.type;
	this.imageIconTollBarSVG = nodeType.getImageIconTollBarSVG();
	this.listTypeCanBeconnected=["actor","usecase"];
	this.variant=nodeType.variant;
	this.edgesConnected=[];
	this.createLabel(nodeType.labelConf);
	this.updateHandles();
	this.createAnchor(nodeType.listAnchors);
	this.nodeType=nodeType;
	this.createFeatures(nodeType.features);
}


//Rectangle.prototype.adjustLabel= function(ctx) {
////	if(this.labelPosition=="center"){
//
//		var width =this.label.getMaxWidthLine(ctx);
//	    if(this.select && this.isAutoresize() ){
//	    	this.width=width+30;
//	    	if(this.width<=70){this.width=this.widthDefault;this.height=this.heightDefault;}
//	    }
//	    alert(34)
////	    if(this.variant=="Specialization" || this.variant=="Relationship")
////	    	this.height=this.heightDefault+this.label.getHeight();
////	    else
////	    	this.height=this.heightDefault+this.label.getHeight()-10;
////	}
//}

//Rectangle.prototype.updateLabelPosition = function  (){
//		this.label.updatePosition(x,y-5);
//		
////	}
//	
//	
//}

Association.prototype = Object.create(Edge.prototype);

function Association(id,source, target, listHandler) {
	Edge.call(this,id,source, target, listHandler);
	this.type = "Association";
	this.pathIconTollBarSVG="./image/associationIcon.svg";
	//this.pathIconTollBarSVG="/home/hppc/workspace/Eshu2.0Beta/WebContent/image/associationIcon.svg";
	this.loadImage();
	//this.listTypeCanBeconnected=["useCase","actor"];
}

Association.prototype.draw = function(ctx) {
	
	/*
	if(this.select)
		this.drawBorder(ctx);

	*/
	
	this.conectNodes();
	ctx.save();
	ctx.beginPath();
	ctx.lineCap="round";
	
	if( this.listHandler[0]!=undefined)
		ctx.moveTo(this.listHandler[0].x, this.listHandler[0].y);
	
	for(var i=1; i< this.listHandler.length;i++){
		ctx.lineTo(this.listHandler[i].x, this.listHandler[i].y);
	}
	//ctx.lineTo(this.pathEdge[0].x, this.pathEdge[0].y);
	ctx.strokeStyle=this.lineColor; 
	ctx.lineWidth=1;
	ctx.stroke();
	ctx.restore();
	
	if(this.select)
		for(var i in this.listHandler){
			this.listHandler[i].draw(ctx);
	}
	
	
}


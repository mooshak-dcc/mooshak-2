UseCase.prototype = Object.create(Vertices.prototype);

function UseCase(x, y, id) {
	Vertices.call(this, x, y, id);
	this.type ="UseCase";
	this.pathIconTollBarSVG="./image/useCaseIcon.svg";
	this.pathimgSVG="./image/usecase.svg";
	this.loadImage();
	this.listTypeCanBeconnected=["actor","usecase"];
	this.edgesConnected=[];
	this.label=new TextBox(this.x+this.width/2, this.y+this.height/2,this.width,this.type);

}

UseCase.prototype.draw = function(ctx) {
	var height = this.height;
	var width = this.width;
	var x = this.x;
	var y = this.y;
	
	this.label.updatePosition(this.x+this.width/2, this.y+this.height/2);
	 ctx.drawImage(this.imageSVG, x,y,width,height); 
	 if(this.select)
			this.drawBorder(ctx);
	 
	 this.label.draw(ctx);
	 
	
}


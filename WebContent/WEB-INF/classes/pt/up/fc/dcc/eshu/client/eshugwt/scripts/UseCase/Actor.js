Actor.prototype = Object.create(Vertices.prototype);

function Actor(x, y, id) {
	Vertices.call(this, x, y, id);
	this.type = "Actor";
	this.pathIconTollBarSVG="./image/actorIcon.svg";
	this.pathimgSVG="./image/actor8.svg";
	//this.pathIconTollBarSVG="/home/hppc/workspace/Eshu2.0Beta/WebContent/image/actorIcon.svg";
	
	this.loadImage();
	this.listTypeCanBeconnected=["useCase","actor"];
	this.edgesConnected=[];
	this.label= new TextBox(this.x+this.width/2,this.y+this.height+10,this.width,this.type);
	
}

Actor.prototype.draw = function(ctx) {
	
	//var img = new Image();
	var height = this.height;
	var width = this.width;
	var x = this.x;
	var y = this.y;
	var type=this.type;
	var label=this.label;

	this.label.updatePosition(this.x+this.width/2,this.y+this.height+10);
/*
	var imageObj = new Image();
    imageObj.onload = function(){
		 ctx.drawImage(imageObj,x,y,width,height); 
       //  context.drawImage(imageObj, 10, 10);
		 
		 label.draw(ctx);
     };	
     imageObj.src = this.pathIconTollBarSVG ;
*/
    
	 ctx.drawImage(this.imageSVG, x,y,width,height); 
	 this.label.draw(ctx);
	 
	if(this.select)
		this.drawBorder(ctx);
	

	
}


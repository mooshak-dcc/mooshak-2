/**
 * 
 */
Box.prototype.x = 0;
Box.prototype.y = 0;
Box.prototype.boxSize= 6;
Box.prototype.selected = false;
Box.prototype.fill; 
Box.prototype.text="";
Box.prototype.fillstyle='#FFD700';

function Box(x, y, id){
	this.x=x;
	this.y=y;
	this.fill = '#ff0028';
	this.precision=10;
	this.id=id
}

Box.prototype.updatePosition=function(x,y) {
	this.x=x;
	this.y=y;
}
Box.prototype.insertText=function(text) {
	this.text=text;
}
Box.prototype.getText=function() {
	return this.text;
}
Box.prototype.draw=function(ctx) {

	ctx.beginPath();
	ctx.save();
	ctx.strokeStyle =this.fill;
	ctx.rect(this.x+0.5, this.y+0.5, this.boxSize, this.boxSize);
	ctx.fillStyle = this.fillstyle;// "lightGrey"; 
	ctx.lineWidth = 1;
	ctx.fill();
	ctx.stroke();
	//alert(this.x + " "+ this.y)
	
//	ctx.font = "bold 10px Arial";
//	ctx.fillStyle = "black";
//	ctx.textAlign = 'center';
//	ctx.textBaseline = 'middle';
//	ctx.fillText(this.text, this.x + this.boxSize / 2 ,
//			this.y + this.boxSize/ 2);
	ctx.restore();
	//alert(this.text)
	
}


Box.prototype.contains = function(mx, my) {
		return (this.x-this.precision<= mx) && (this.x + this.boxSize+this.precision >= mx) && (this.y-this.precision <= my)
				&& (this.y + this.boxSize+this.precision >= my);
		
}

Box.prototype.distance = function(box) {
	  var x = Math.pow( this.x -  box.x, 2);
	  var y = Math.pow( this.y - box.y, 2);
	  return Math.round(Math.sqrt(x + y, 2));
}

Box.prototype.getId = function() {
	return this.id;
}

//Box.prototype.contains = function(mx, my, precision) {
//	return (this.x-precision <= mx) && (this.x + this.boxSize+precision >= mx) && (this.y-precision <= my)
//	&& (this.y + this.boxSize+ precision >= my);
//	
//}



function RectangleSelection(){
	this.coordinate=[0,0,0,0]; //
	this.xStart=0;
	this.yStart=0;
	this.xEnd=0;
	this.yEnd=0;
	this.width=0;
	this.height=0;
	this.listOfSelected=[];
} 

RectangleSelection.prototype = {
	
	setCoordinates : function(x1,y1,x2,y2){
			this.xStart=x1;
			this.yStart=y1;
			this.xEnd=x2;
			this.yEnd=y2;
	},	
	
	setCoordinateStart : function(x1,y1){
		this.xStart=x1;
		this.yStart=y1;
	},	
	
	setCoordinateEnd : function(x2,y2){
		this.xEnd=x2;
		this.yEnd=y2;
	},

	getCoordinates: function(){
		return{
			xStart:this.xStart,
			yStart:this.yStart=y1,
			xEnd:this.xEnd,
			yEnd:this.yEnd,
		};
	},	
	
	sizeMin: function(){
		return (Math.abs(this.xStart-this.xEnd) <10 && Math.abs(this.yStart-this.yEnd)<10);
	},

	sizeListOfSelected: function(){
		return this.listOfSelected.length;
	},
	
	length: function(){
		return this.listOfSelected.length;
	},
	
	getFirstElementListOfSelected: function(){
		return this.listOfSelected[0];
	},

	removelastElement: function(){
		return this.listOfSelected.pop();
	},
	
	getListOfSelected: function(){
		return this.listOfSelected;
	},
	
	isEmpty: function(){
		return this.listOfSelected.length==0;
		
	},
	
	addElementListOfSelected: function(node){
		this.listOfSelected.push(node);
		
	},
	
	clearListOfSelected: function() {
		this.listOfSelected=[];
	},
	
	reset: function(){
		this.setCoordinates(0,0,0,0);
		this.listOfSelected=[];
		this.xStart=0;
		this.yStart=0;
		this.xEnd=0;
		this.yEnd=0;
		this.width=0;
		this.height=0;
		this.listOfSelected=[];
	},
	
	isInsideRectangleSelection: function(node) {
		if(this.sizeMin()) return false;
		
		var xMin=Math.min(this.xStart,this.xEnd);
		var yMin=Math.min(this.yStart,this.yEnd);
		var xMax=Math.max(this.xStart,this.xEnd);
		var yMax=Math.max(this.yStart,this.yEnd);
		
		var x=Math.floor(node.x);
		var y=Math.floor(node.y);
		var xEnd = x+Math.floor(node.width);
		var yEnd = y+Math.floor(node.height);
		 return (x < xMax && xMin < xEnd && y < yMax && yMin < yEnd)

	},
		
	adjustRectangleToElements : function(){
			
		var xStart=100000;
		var yStart=100000;
		var xEnd=0;
		var yEnd=0;
		var space=5;
		for(var i in this.listOfSelected){
			var node=this.listOfSelected[i];
			
			if(node.x<xStart)
				xStart=node.x-space;
			if(node.y<yStart)
				yStart=node.y-space;
			if(node.x+node.width>xEnd)
				xEnd=node.x+node.width+space;
			if(node.y+node.height>yEnd)
				yEnd=node.y+node.height+space;
		}

		this.xStart=xStart;
		this.yStart=yStart;
		this.xEnd=xEnd;
		this.yEnd=yEnd;
		
		this.width=xEnd-xStart;
		this.height=yEnd-yStart;
	},	
		
	contains : function(mx, my) {
		return ( mx>=this.xStart && mx<this.xEnd && my>=this.yStart && my<=this.yEnd );

	},
		
	move : function(x, y) {
		this.xStart+=x;
		//(this.xStart<0)?this.xStart= 3:this.xStart;
		this.yStart+=y;
		//(this.yStart<0)?this.yStart= 3:this.yStart;
		this.xEnd=this.xStart+this.width;
		this.yEnd=this.yStart+this.height;
		
//		for(var i in this.listOfSelected){
//			this.listOfSelected[i].x+=x;
//			this.listOfSelected[i].y+=y;
//		}

	},
	
	updatePosition : function(x, y) {
		this.xStart=x;
		this.yStart=y;
		this.xEnd=this.xStart+this.width;
		this.yEnd=this.yStart+this.height;
		
	},
	
	draw : function(ctx) {
		
		if(this.xStart== this.xEnd &&
				this.yStar==this.yEnd  || this.xStart==0) return;
		var xStart=Math.min(this.xStart,this.xEnd);
		var yStart=Math.min(this.yStart,this.yEnd);
		var xEnd=Math.max(this.xStart,this.xEnd);
		var yEnd=Math.max(this.yStart,this.yEnd);
		ctx.save();
		ctx.beginPath();
		ctx.globalAlpha=0.2;
		ctx.strokeStyle = "AAAAAA";
		ctx.rect(xStart, yStart, (xEnd-xStart), (yEnd-yStart));
		ctx.fillStyle = "blue";
		ctx.lineWidth=1;
		ctx.fill();
		ctx.stroke();
		ctx.restore();
		

	},
	
	clone : function(){
	var nodes= [];
	for(var i in this.listOfSelected){
		
		var info={	infoNode:this.listOfSelected[i].clone(),
					dx:this.listOfSelected[i].x- Math.min(this.xStart,this.xEnd),
					dy:this.listOfSelected[i].y- Math.min(this.yStart,this.yEnd),
		}
		nodes[i]=info;
	}
	
	return{
		xStart:this.xStart,
		yStart:this.yStart,
		width:this.width,
		height:this.height,
		nodes:nodes,
	}
},

	getXMax: function(){
		return Math.max(this.xStart,this.xEnd);
	},

	getYMax: function(){
		return Math.max(this.yStart,this.yEnd);
	},
	
	getXMin: function(){
		return Math.min(this.xStart,this.xEnd);
	},

	getYMin: function(){
		return Math.min(this.yStart,this.yEnd);
	},
	
	
	copy: function(){
		var rs=new RectangleSelection();
		rs.setCoordinates(this.xStart,this.yStart,this.xEnd,this.yEnd)
		rs.width=this.width;
		rs.height=this.height;
		rs.listOfSelected=this.listOfSelected;
		return rs;
	
	},
	
	equalize: function (min){
		var size;
		if(min)
			size=this.getSizeSmallNode(); 
		else
			size=this.getSizeBigNode();
		
		for(var i in this.listOfSelected){
			console.log(size.width +" "+ size.height)
			this.listOfSelected[i].updateSize(size.width, size.height);
		}
		//return console.log(this.getSizeBigNode());
		this.adjustRectangleToElements();
	},
	
	getSizeBigNode(){
		var width=0;
		var height=0;
		for(var i in this.listOfSelected){
			if(this.listOfSelected[i].width>=width && this.listOfSelected[i].height>=height ){
				height=this.listOfSelected[i].height;
				width=this.listOfSelected[i].width;
			}
		}
		return {
			'width': width,
			'height' : height
			}
	},
	
	getSizeSmallNode(){
		var width=Number.MAX_VALUE ;
		var height=Number.MAX_VALUE;
		for(var i in this.listOfSelected){
			
			if(this.listOfSelected[i].width<=width && this.listOfSelected[i].height<=height ){
				height=this.listOfSelected[i].height;
				width=this.listOfSelected[i].width;
			}
		}
		return {
			'width': width,
			'height' : height
			}
	},
	
	alignHorizontally(value){ //align horizontal left
		if(value=='left'){ //0
			for(var i in this.listOfSelected){
				this.listOfSelected[i].updateX(this.xStart);
			}
		}
		
		else if(value=='center'){ //align horizontal center 1
			var middle= (this.xEnd+this.xStart)/2;
			for(var i in this.listOfSelected){
				this.listOfSelected[i].updateX(middle-this.listOfSelected[i].width/2);
			}
		}
		
		else if(value='right') { // align horizontal right 2
			for(var i in this.listOfSelected){
				this.listOfSelected[i].updateX(this.xEnd-this.listOfSelected[i].width);
			}
		}
		else
			throw new Error("Align Horizontally Position"+ value +" doesn't exists ");
		this.adjustRectangleToElements();
	},
	
	alignVertically(value){ //align horizontal top
		if(value=='top'){ //0
			for(var i in this.listOfSelected){
				this.listOfSelected[i].updateY(this.yStart);
			}
		}
		
		else if(value=='midle'){ //align horizontal center 1
			var middle= (this.yEnd+this.yStart)/2;
			for(var i in this.listOfSelected){
				this.listOfSelected[i].updateY(middle-this.listOfSelected[i].height/2);
			}
		}
		
		else if(value=='bottom') { // align horizontal bottom 2
			
			for(var i in this.listOfSelected){
				this.listOfSelected[i].updateY(this.yEnd-this.listOfSelected[i].height);
			}
		}
		this.adjustRectangleToElements();
	},
}

/*
function clone(obj) {
    if (obj === null || typeof(obj) !== 'object' || 'isActiveClone' in obj)
      return obj;

    if (obj instanceof Date)
      var temp = new obj.constructor(); //or new Date(obj);
    else
      var temp = obj.constructor();

    for (var key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        obj['isActiveClone'] = null;
        temp[key] = clone(obj[key]);
        delete obj['isActiveClone'];
      }
    }

    return temp;
  }
*/

Edge.prototype.source = undefined;
Edge.prototype.target = undefined;
Edge.prototype.AnchorStart = undefined;
Edge.prototype.AnchorEnd = undefined;
Edge.prototype.NM = undefined;
Edge.prototype.x = undefined;
Edge.prototype.y = undefined;
Edge.prototype.group ="EDGE";
Edge.prototype.simpleLine = true;
Edge.prototype.id ;
Edge.prototype.handlesIniFin=false;
Edge.prototype.link=undefined;
Edge.prototype.selec=false;
Edge.prototype.xM = undefined;
Edge.prototype.yM = undefined;
Edge.prototype.xMVet1 = undefined;
Edge.prototype.yMVet1 = undefined;
Edge.prototype.xMVet2 = undefined;
Edge.prototype.yMVet2 = undefined;
Edge.prototype.d1 =0;
Edge.prototype.d2 = 0;
Edge.prototype.inic=true;
Edge.prototype.xI = 0;
Edge.prototype.yI = 0;
Edge.prototype.xF = 0;
Edge.prototype.yF = 0;
Edge.prototype.width=0;
Edge.prototype.select = false;
Edge.prototype.total= undefined;
Edge.prototype.cardinality= undefined;
Edge.prototype.color ="#484848";
Edge.prototype.handles;
Edge.prototype.NumberHandles = 3;
Edge.prototype.selectionBoxSize = 6;
//Edge.prototype.width;
//Edge.prototype.height;




function Edge(n1,n2,b1,b2,id) {
	this.id=id;
	this.selectLink(n1,n2,b1,b2);
	//alert(n1+ " "+ n2+" "+ b1+" "+ b2);
	this.NM =  (this.source > this.target ? this.source : this.target) / 2;
	this.xM= (this.source.handles[this.AnchorStart].x + this.target.handles[this.AnchorEnd].x) / 2;
	this.yM= (this.source.handles[this.AnchorStart].y + this.target.handles[this.AnchorEnd].y) / 2;
	
	this.xI=this.source.handles[this.AnchorStart].x;
	this.yI=this.source.handles[this.AnchorStart].y;
	this.xF=this.target.handles[this.AnchorEnd].x;
	this.yF=this.target.handles[this.AnchorEnd].y;
	
	this.x=Math.min(this.xI,this.xF);
	this.y=Math.min(this.yI,this.yF);
	this.width= Math.max(this.xI,this.xF)-this.x;
	this.height=Math.max(this.yI,this.yF)-this.y;
	//console.log(this.x+" "+ this.y+" "+this.width+" "+this.height)
//	var x1=Math.min(xMin,xMax);
//	var x2=Math.max(xMin,xMax);
//	var y1=Math.min(yMin,yMax);
//	var y2=Math.max(yMin,yMax);
//	

	switch (this.AnchorStart) {
	case 3:
		this.d1=-10;
		break;
	case 1:
		this.d1=10;
		break;	
	}
	
	switch (this.AnchorEnd) {
	case 3:
		this.d2=-10;
		break;
	case 1:
		this.d2=10;
		break;	
	}
	
	
//	switch (this.AnchorStart) {
//	case 3:
//		this.d1=-10;
//		if(this.AnchorEnd==3)
//			this.d2=-10
//		else if (this.AnchorEnd==1)
//			this.d2=10;
//		
//		else if(this.AnchorEnd==2){
//			//if(this.yI<this.yF && this.yM<this.yF){
//				this.yM=Math.max(this.yI,this.yF)+Math.max(this.source.height,this.target.height)*0.60;
//				alert(1)
//		//}
//		//	else
//			//	this.yM=this.yF+10;
//		}
//		
//		else if(this.AnchorEnd==0){
//			//if(this.yI<this.yF){
//				this.yM=Math.min(this.yF,this.yI)-Math.max(this.source.height,this.target.height)*0.60;
//		///	}
//		
//		}
//		break;
//		
//		
//	case 1:
//		this.d1=10;
//		if(this.AnchorEnd==3)
//			this.d2=-10
//		else if (this.AnchorEnd==1)
//			this.d2=10;
//		break;
//		
//	}

	
	
	this.xMVet1 = this.source.handles[this.AnchorStart].x+this.d1;
	this.yMVet1 = (this.yM+this.source.handles[this.AnchorStart].y)/2;
	
	this.xMVet2 = this.target.handles[this.AnchorEnd].x+this.d2;
	this.yMVet2 = (this.target.handles[this.AnchorEnd].y+this.yM)/2;
	
	
//	this.xMVet1 = this.xI+this.d1;
//	this.yMVet1 = (this.yM+this.yI)/2;
//	
//	this.xMVet2 = this.xF+this.d2;
//	this.yMVet2 = (this.yF+this.yM)/2;
	
	this.borderfillStyle = this.color;
	
}	

Edge.prototype.selectLink=function(a,b,anchorStart,anchorEnd){
	for(var l in this.links){
		var link=this.links[l];
		if(link.canBeTarget(a) && link.canBeSource(b) ){ 
			this.source = a;
			this.target = b;
			this.AnchorStart = anchorStart;
			this.AnchorEnd = anchorEnd;
			this.link=link.type;
			if(link.hasOwnProperty('total'))this.total=link.total;
			if(link.hasOwnProperty('cardinality')) this.cardinality="";
			break;
		}
		else if(link.canBeTarget(b) && link.canBeSource(a) ){
			this.source = b;
			this.target = a;
			this.AnchorStart = anchorEnd;
			this.AnchorEnd = anchorStart;
			this.link=link.type;
			if(link.hasOwnProperty('total'))this.total=link.total;
			if(link.hasOwnProperty('cardinality')) this.cardinality="";
			break;	
		}
	}
	
}
Edge.prototype.getProprety=function(name, defaultValue ){
	if(this.link[name]==undefined)
		return defaultValue;
	else 
		return this.link[name];		
}

Edge.prototype.updateHansles = function() {

	this.handles = new Array();
	var half = this.selectionBoxSize / 2;
	(this.simpleLine)?this.NumberHandles=3:this.NumberHandles=5;
	for (var i = 0; i < this.NumberHandles; i++) {
		switch (i) {
		case 0:
			this.handles.push(new Box(this.source.handles[this.AnchorStart].x , this.source.handles[this.AnchorStart].y ));
			break;
		case 1:
			this.handles.push(new Box(this.xM, this.yM ));
			break;
		case 2:
			this.handles.push(new Box(this.target.handles[this.AnchorEnd].x , this.target.handles[this.AnchorEnd].y ));
			break;
			
		case 3:
			this.handles.push(new Box(this.xMVet1, (this.yI+this.yM)/2 ));
			break;	
			
		case 4:
			this.handles.push(new Box(this.xMVet2, (this.yF+this.yM)/2 ));
			break;	
		}
	}
}

Edge.prototype.contains = function(mx, my) {
	this.updateSize();
	var xI=this.xI;
	var yI=this.yI;
	var xF=this.xF;
	var yF=this.yF;

	var xM=this.xM;
	var vertX=xI+xF-mx*2;
	//var vertX1=this.xMVet1+xF-mx*2;
	
	//var vertX=
	var precision = 10;
	
	if(this.simpleLine){
		
		var a = (yF - yI) / (xF - xI);
		var b = yI - a * xI;
		var ray=Math.abs(xM-xI);
		if (Math.abs((my - (a * mx + b))) < precision && mx<ray+xM && mx>xM-ray){ 
			return true;
		}
		else if(vertX<=10 && vertX>=-10 && my<Math.max(yI,yF)&& my>Math.min(yI,yF)  ){
			return true;
		}
		return false;
	}
	else{
		//for two line
		//console.log(this.yMVet1+" "+this.yMVet1*2+" "+this.yM)
		var a = (this.yM - this.yM) / (this.xMVet2 - this.xMVet1);
		var b = this.yM - a * this.xMVet2; 
//		var a = (this.yM - this.yM) / (this.xF - this.xI);
//		var b = this.yM - a * this.xF; 
//		
		if (Math.abs((my - (a * mx + b))) < precision && mx<Math.max(this.xMVet1,this.xMVet2)&& mx>Math.min(this.xMVet1,this.xMVet2) )
			return true;
		else if(Math.abs(this.xMVet2-mx)<=10 && my<Math.max(this.yM,yF)&& my>Math.min(this.yM,yF)  )
			return true;
		else if(Math.abs(this.xMVet1-mx)<=10 && my<Math.max(this.yM,yI)&& my>Math.min(this.yM,yI)  )
			return true;
		else if(Math.abs(yI-my)<=10 && mx<Math.max(xI,this.xMVet1)&& mx>Math.min(xI,this.xMVet1)  )
			return true;
		else if(Math.abs(yF-my)<=10 && mx<Math.max(xF,this.xMVet2)&& mx>Math.min(xF,this.xMVet2)  )
			return true;
		return false;
		
	}
	
}

Edge.prototype.selected = function() {
	
	this.color=selectionColor;
	this.select=true;

}

Edge.prototype.unSelected = function() {
	this.color="#484848";
	this.select=false;
}


Edge.prototype.insideHandle = function(mx, my) {

	for (var i = 0; i < this.NumberHandles; i++) {

		if (this.handles[i].contains(mx, my,5) ) {
			this.handleSelected = i;
			return true;
		}
	}
	return false;
}

Edge.prototype.insideHandleExtreme= function(mx, my) {
	if (this.handles[2].contains(mx, my,5) ) {
		this.handleSelected = 2;
		return true;
	}
	if (this.handles[0].contains(mx, my,5) ) {
		this.handleSelected = 0;
		return true;
	}
}

Edge.prototype.insideHandleMiddle= function(mx, my) {
	if (this.handles[1].contains(mx, my,5) ) {
		this.handleSelected = 1;
		return true;
	}
	
	if(!this.simpleLine){
	if (this.handles[3].contains(mx, my,5) ) {
		this.handleSelected = 3;
		return true;
	}
	if (this.handles[4].contains(mx, my,5) ) {
		this.handleSelected = 4;
		return true;
	}
	}
return false;
}

Edge.prototype.updatePosition = function() {
	//this.width= this.source.handles[this.AnchorStart].x > this.target.handles[this.AnchorEnd].x ? this.source.handles[this.AnchorStart].x- this.target.handles[this.AnchorEnd].x : this.target.handles[this.AnchorEnd].x-this.source.handles[this.AnchorStart].x
	//this.height= this.source.handles[this.AnchorStart].y	 >	this.target.handles[this.AnchorEnd].x ? this.source.handles[this.AnchorStart].y- this.target.handles[this.AnchorEnd].y: this.target.handles[this.AnchorEnd].y-this.source.handles[this.AnchorStart].y;

	this.xI=this.source.handles[this.AnchorStart].x;
	this.yI=this.source.handles[this.AnchorStart].y
	
	this.xF=this.target.handles[this.AnchorEnd].x;
	this.yF=this.target.handles[this.AnchorEnd].y;
	
//	this.x=Math.min(this.xI,this.xF);
//	this.y=Math.min(this.yI,this.yF);
//	this.width= Math.max(this.xI,this.xF)-this.x;
//	this.height=Math.max(this.yI,this.yF)-this.y;
//	
//	console.log(this.x+" "+ this.y+" "+this.width+" "+this.height)
	
	this.xM= (this.xMVet1+ this.xMVet2) / 2;
	if(this.simpleLine){
	this.yM= (this.source.handles[this.AnchorStart].y + this.target.handles[this.AnchorEnd].y) / 2;
	this.xM= (this.source.handles[this.AnchorStart].x + this.target.handles[this.AnchorEnd].x) / 2;
	
	this.yMVet1=(this.yI+this.yM)/2;
	this.yMVet2=(this.yF+this.yM)/2;
	
	}
//	else
//	{
//	
//	
//	if(this.inic){
//	
//		
//		switch (this.AnchorStart) {
//		case 3:
//			this.d1=-10;
//			if(this.AnchorEnd==3)
//				this.d2=-10
//			else if (this.AnchorEnd==1)
//				this.d2=10;
//			
//			else if(this.AnchorEnd==2){
////				if(this.yI<this.yF && this.yM<this.yF){
////					this.yM=this.yF;
////			}
//			}
//			
//			else if(this.AnchorEnd==0){
//				//if(this.yI<this.yF){
//				//	this.yM=this.yF-10;
//			///	}
//			
//			}
//			break;
//			
//			
//		case 1:
//			this.d1=10;
//			if(this.AnchorEnd==3)
//				this.d2=-10
//			else if (this.AnchorEnd==1)
//				this.d2=10;
//			break;
//			
//		}
//	
//	
//	this.xMVet1 = this.xI+this.d1;
//	this.yMVet1 = (this.yM+this.yI)/2;
//	
//	this.xMVet2 = this.xF+this.d2;
//	this.yMVet2 = (this.yF+this.yM)/2;
//	this.inic=false;
//	}
//	}
	
	
}

Edge.prototype.updateSize = function() {
	this.updatePosition();
	if(this.simpleLine){
		this.x=Math.min(this.xI,this.xF);
		this.y=Math.min(this.yI,this.yF);
		this.width= Math.max(this.xI,this.xF)-this.x;
		this.height=Math.max(this.yI,this.yF)-this.y;
	}
	else{
		this.x=Math.min(this.xI,this.xF,this.xMVet1,this.xMVet2);
		this.y=Math.min(this.yI,this.yF,this.yM);
		this.width= Math.max(this.xI,this.xF,this.xMVet1,this.xMVet2)-this.x+5;
		this.height=Math.max(this.yI,this.yF,this.yM)-this.y;
	}
	
	
	
	
}	
Edge.prototype.showHandlesIniFin = function() {
	this.handlesIniFin=true;
}
Edge.prototype.updateCardinality = function(value) {
	this.cardinality=value;
	if(this.source.type=="relationship"){
		this.source.insertCardinality(this.AnchorStart,value);
	}
	else{
		this.target.insertCardinality(this.AnchorEnd,value);
	}
}

Edge.prototype.getAlphaNodes = function(ctx) {
	if (this.source.temporary != "") {
		this.color=this.source.backgroundColor;
		return this.source.alpha;
	} else if (this.target.temporary != "") {
		this.color=this.target.backgroundColor;
		return this.target.alpha;
	}
	
	
}
Edge.prototype.risize = function(value) {
if (this.handleSelected == 1) {
	this.yM =value;
	this.yMVet1=(this.yI+this.yM)/2;
	this.yMVet2=(this.yF+this.yM)/2;
}
else if (this.handleSelected == 3){
	
		this.xMVet1= value;
}
else if (this.handleSelected == 4){
	
	this.xMVet2= value;
}
}

Edge.prototype.risize2 = function(x,y) {
	
	if(this.handleSelected==0){
		this.source=new SimpleNode(x, y);
		this.AnchorStart=4;
	}
		
	else if(this.handleSelected==2){
		this.target=new SimpleNode(x, y);
		this.AnchorEnd=4;
	}
}

Edge.prototype.setAnchorTarget = function(value) {
	this.AnchorEnd=value;	
}

Edge.prototype.setAnchorSource = function(value) {
	this.AnchorStart=value;	
}

Eshu.prototype.widthElementToobar=45;
Eshu.prototype.heightElementToobar=35;
Eshu.prototype.languageOptionCanvas=[];
Eshu.prototype.groupShow;

Eshu.prototype.drawToolbar=function ()  {
	var languageOpt = new Array();
	var opt, figure;
//	var nodes=this.language.getNodes();
//	var edges=this.language.getEdges();
	
}

Eshu.prototype.createNodeTypeArrow= function(){
	var nodeType={type:"Arrow",group:"Arrow"};
	var imgSVG=this.imagesSVG.get("arrow.svg");
	var imgElement=this.imagesSVGObjects.get("arrow.svg");
	var opt = this.loadCanvas(this.toolbar,nodeType, "Arrow");
	
	var ctx =opt.getContext("2d"); 
	
	if(this.editorHTML)
		drawInCanvas2(ctx, this.widthElementToobar, this.heightElementToobar, imgElement);	
	else
		drawInCanvas(ctx, this.widthElementToobar, this.heightElementToobar, imgElement, imgSVG);	
	
	this.languageOptionCanvas.push(opt);
}

Eshu.prototype.createTypeNodeToolbar= function(typeNode) {
	var imgSVG=typeNode.getImageIconTollBarSVG();
//	alert(typeNode.imgSVGPath.replace(/\s/g, ''));
	var imgElement=this.imagesSVGObjects.get(typeNode.iconTollbarSVGPath.replace(/\s/g, ''));
//	alert(typeNode.imgSVGPath.replace(/\s/g, '')+"  "+ this.imagesSVGObjects.get(typeNode.imgSVGPath.replace(/\s/g, '')) )
	var opt = this.loadCanvas(this.toolbar, typeNode, "NODE");
	this.languageOptionCanvas.push(opt);
	
	var ctx =opt.getContext("2d");
	
//	drawInCanvas(ctx, this.widthElementToobar, this.heightElementToobar, imgElement, imgSVG);	
	if(this.editorHTML)
		drawInCanvas2(ctx, this.widthElementToobar, this.heightElementToobar, imgElement);	
	else
		drawInCanvas(ctx, this.widthElementToobar, this.heightElementToobar, imgElement, imgSVG);
//	
}

Eshu.prototype.createTypeEdgeToolbar= function(typeEdge) {
	var imgSVG=typeEdge.getImageIconTollBarSVG();
	var imgElement=this.imagesSVGObjects.get(typeEdge.iconTollbarSVGPath.replace(/\s/g, ''));
	var opt = this.loadCanvas(this.toolbar, typeEdge, "Edge");
	this.languageOptionCanvas.push(opt);
	
	//this.unSelectOperation();
	var ctx =opt.getContext("2d");
//	drawInCanvas(ctx,this.widthElementToobar,this.heightElementToobar,imgElement, imgSVG);
	if(this.editorHTML)
		drawInCanvas2(ctx, this.widthElementToobar, this.heightElementToobar, imgElement);	
	else
		drawInCanvas(ctx, this.widthElementToobar, this.heightElementToobar, imgElement, imgSVG);
}



 Eshu.prototype.unSelectOperation = function() {
	 
		
	if(this.elementDraw==undefined ) return;
	var canvas=this.languageOptionCanvas;
	
	for (var i = 0; i < canvas.length; i++) {
		//var canvasTmp = canvas[i];
		
		canvas[i].style.color = "#A0A0A0";
		canvas[i].style.backgroundColor = "#fff";
		
//		if(canvas[i].elementType.group == this.elementDraw.elementType.group){
//				if(canvas[i].id!= this.elementDraw.elementType.type	){
//						canvas[i].style.display = "none";
//						canvas[i].parentNode.style.border= "0px";
//					
//					}
//		}
		
		this.hideGroup2(this.elementDraw.elementType.variant)
		
	}
	this.elementDraw = undefined;

//	}
//	else{
//		this.unSelectedEdge();
//		 this.unSelectedNode();
//		 (this.elementDraw.name == "line" || this.elementDraw.name == "lineEGC") ? this.setDrawEdges(true)
//					: this.setDrawEdges(false);
//	}
	
	document.body.style.cursor = "auto";
	
	if(canvas[0]!=undefined ){
		canvas[0].style.color = "blue";
		canvas[0].style.backgroundColor = "#C0C0C0 ";
	}
	
	
}
 


Eshu.prototype.selectOperation=function (id) {
	//if(id!=Arrow)
		//this.unSelectOperation()
	
	var groupPrevion="";
	if(this.elementDraw!=undefined) groupPrevion=this.elementDraw;
	if(id==undefined) return;
	//this.unSelectOperation();
	var canvas=this.languageOptionCanvas;
	//this.unselectElementESC();
	var parent = this.toolbar;
	for (var i = 0; i < canvas.length; i++) {
		var idtmp = canvas[i].getAttribute("id");
		
		if (idtmp == id) {
			canvas[i].style.color = "blue";
			canvas[i].style.backgroundColor = "#C0C0C0 ";
			//canvas[i].style.position = "absolute";
			this.elementDraw ={type:canvas[i].type, elementType:canvas[i].elementType, group:canvas[i].parentNode.id};
			if(canvas[i].type=="EDGE"){
				this.graphState=4; //draw path
				this.pathEdge=[];
				//this.pathEdge.push({x:0, y:0});
				
			}
		}
		else {
			canvas[i].style.color = '#A0A0A0';
			canvas[i].style.backgroundColor = "#fff";
		}
	}
//	if(groupPrevion.group!=this.elementDraw.group)
//	console.log(groupPrevion);
//		this.hideGroup(groupPrevion);
	
	if(id!="Arrow"){
		document.body.style.cursor = "crosshair";
	}
	else {
		this.elementDraw = undefined;
		document.body.style.cursor = "auto";
	}
	this.groupShow==undefined;
}


Eshu.prototype.loadDiv=function (parent,elementType,type,imgPath) {
	var div = document.createElement('div');
	div.id = elementType.type;
	div.type=type;
	div.elementType=elementType;
	div.style.width = '50px';
	div.style.height = '50px';
	
	//div.setAttribute("style", "width:" +this.widthElementToobar+"px" );
	div.setAttribute("style", "height:" +this.heightElementToobar+"px" );
	//var width=" width:" +this.widthElementToobar+"px; ";
	//var height=" height:" +this.heightElementToobar+"px; ";
	//console.log(width)
	div.setAttribute('style', 'border: 1px solid; zIndex: -1; display:inline; margin : 1px;' );
	//div.className = 'elementToolbar';
	
	
	div.onclick=this.selectOperation();
	div.addEventListener('click', function(event) {
		this.selectOperation(div.id);
	}.bind(this), false);
	
	div.addEventListener('onmouseenter', function(event) {
		//this.selectOperation(canvas.id);
	}.bind(this), false);

	
	parent.appendChild(div);
	div.offsetHeight=30;
//	console.log(div.offsetWidth+" "+div.offsetHeight)
//	console.log("tb "+parent.offsetWidth+" "+parent.offsetHeight)
	return div;
}



Eshu.prototype.loadCanvas=function (parent,elementType,type) { 
	
	var canvas = document.createElement('canvas');
	canvas.id = elementType.type;
	canvas.type=type;
	canvas.elementType=elementType
	canvas.style.backgroundColor = "#fff";
	canvas.width = this.widthElementToobar;
	canvas.height = this.heightElementToobar;
	canvas.style.zIndex = 2;
	canvas.style.border = "1px solid";
	canvas.style.marginLeft = "5px";
	//canvas.style.display="inline"; 
	canvas.style.color = "#A0A0A0";
	
	
	canvas.addEventListener('click', function(event) {
		this.selectOperation(canvas.id);
	}.bind(this), false);
	
	canvas.addEventListener('dblclick', function(event) {
		this.showGroup(elementType.variant);
	}.bind(this), false);
	
	canvas.addEventListener('mouseenter', function(evt) {
		this.removecolor();
		this.showGroup(elementType.variant);
		canvas.style.color = "blue";
		this.createInfoCanvas(canvas,parent,evt);
	}.bind(this), false);
	
//	var ctx =canvas.getContext("2d");
//	drawInCanvas(ctx,this.widthElementToobar,this.heightElementToobar,img);	
	var divGroup= document.getElementById(elementType.variant);
	
	if(divGroup!=undefined)
		{
		canvas.style.display = "none";
		divGroup.appendChild(canvas);
		//divGroup.length=2;
		//divGroup.setAttribute(title, "Test");
		
		divGroup.length=divGroup.length+1;
		//console.log(divGroup.length+ "  p");
		
		}
	else{
		var group1 = document.createElement("div");
		group1.setAttribute("id", elementType.variant);
		group1.style.display= "block";
		group1.style.cssFloat= "left";
		group1.length=1;
		//console.log(group1.l+ "  er")
		//group1.setAttribute(title, "Test");
		canvas.style.display="inline";
		group1.appendChild(canvas);
		this.toolbar.appendChild(group1);
	}
	
	return canvas;
}
 

Eshu.prototype.showGroup= function (group){

	if(this.groupShow!=undefined){
		this.hideGroup2(this.groupShow);
	}
	var divGroup= document.getElementById(group);
	if(divGroup.length==1)
		return;

//	if(this.elementDraw!=undefined )
//		console.log(34);
//	console.log(45);
	//this.unSelectOperation();
	//this.hideGroup2();
	var canvas=this.languageOptionCanvas;
	
	
	for (var i = 0; i < canvas.length; i++) {
		//var canvasTmp = canvas[i];
		if(canvas[i].elementType.variant == group){
			//console.log(canvas[i].title);	
			//if(canvas.length==1) return;
			
			canvas[i].style.display="block";
			//group1.style.border= 1+"px solid";
			//canvas[i].style.borderColor="blue";
			canvas[i].style.marginBottom = "3px";
			//canvas[i].style.marginLeft = "3px";
			canvas[i].style.marginRight = "3px";
			canvas[i].parentNode.style.border= 1+"px solid";
			canvas[i].style.zIndex = 3;
			
		}
	}
	this.toolbar.style.height=this.toolbarHeight +"px"; 
		//(this.heightElementToobar*1.5)+"px"; 
	this.groupShow=group;
	
}


Eshu.prototype.hideGroup= function (elemDraw){
var canvas=this.languageOptionCanvas;

for (var i = 0; i < canvas.length; i++) {
	if(canvas[i].elementType.variant == elemDraw.group){
		
			if(canvas[i].id!= elemDraw.elementType.type	){
					canvas[i].style.display = "none";
					canvas[i].parentNode.style.border= "0px";
				
				}
	}
}

}


Eshu.prototype.hideGroup2= function (type){
	var canvas=this.languageOptionCanvas;
	
	for (var i = 0; i < canvas.length; i++) {
		//var canvasTmp = canvas[i];
		
//		canvas[i].style.color = "#A0A0A0";
//		canvas[i].style.backgroundColor = "#fff";
		if(canvas[i].elementType.variant == type){
				if(canvas[i].id!= type	){
						canvas[i].style.display = "none";
						canvas[i].parentNode.style.border= "0px";
					
					}
		}
	}
	if(document.getElementById("info"))
		document.getElementById("info").remove();
}


Eshu.prototype.removecolor= function (){
	if(document.getElementById("info"))
		document.getElementById("info").remove();
	var canvas=this.languageOptionCanvas;
	
	for (var i = 0; i < canvas.length; i++) {
		//if(this.elementDraw==undefined && this.elementDraw.type==canvas[i].type)
		//	continue;
		canvas[i].style.color = "#A0A0A0";
	//	canvas[i].style.backgroundColor = "#fff";
		
	}
}

Eshu.prototype.createInfoCanvas= function (canvas,parent,evt){
	if(canvas.id=="Arrow")return; 
	
	var div = document.createElement('div');
	div.id ="info";
	div.style.width = '20px';
	div.style.height = '20px';
	div.innerHTML=canvas.id;
	var offset=getPositionDiv(parent);
	div.style.position = "absolute";
	div.style.left = (evt.pageX-offset.divsOffSetwidth+15)+'px';
	div.style.top = (evt.pageY-offset.divsOffSetheight+15)+'px';
	div.style.color="DarkSlateGray";
	parent.appendChild(div);
}

function drawInCanvas(ctx,width,height,imageElement,imageSVG){

//	drawInCanvas(ctx,this.widthElementToobar,this.heightElementToobar,imageElement);	
	
	var loadToCanvas = function(){
		 ctx.drawImage(imageElement,0,0,width,height); 
		 
    };	
	imageElement.onload = loadToCanvas;
	
	if (imageElement.complete)
		loadToCanvas.call();
}


function drawInCanvas2(ctx,width,height,imageSVG){
//	drawInCanvas(ctx,this.widthElementToobar,this.heightElementToobar,img);	
	imageSVG.onload = function(){
		 ctx.drawImage(imageSVG,0,0,width,height); 
    };	
}
function getPositionDiv(div1){
	
	var divsOffSetheight=0;
	var divsOffSetwidth=0;
	while(div1!=null){
		if(divsOffSetheight<div1.offsetTop)
			divsOffSetheight=div1.offsetTop;
		if(divsOffSetwidth<div1.offsetLeft)
			divsOffSetwidth=div1.offsetLeft;
		div1=div1.parentNode;
	}
	return {divsOffSetheight:divsOffSetheight, divsOffSetwidth:divsOffSetwidth};
}
 

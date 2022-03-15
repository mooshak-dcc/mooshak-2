
function Edge(id, source, target, listHandler) {
	this.x;
	this.y;
	this.group ="EDGE";
	this.link=undefined;
	this.select =false;
	this.width;
	this.height;
	this.lineColor ="black";
	this.pathIconTollBarSVG;
	this.id=id;
	this.source=source; 
	this.target=target;
	this.handlerSelected;
	this.edgeType;
	this.type;
	this.islineDuple;
	this.modify;
	this.sourceType=source.type;
	this.targetType=target.type;
	this.listHandler=[];
	this.source.incremenDegreeOut();
	this.target.incremenDegreeIn();
	this.labelPosition;
	this.drawLabel;
	this.total="no";
	for(var i in listHandler){
		this.listHandler.push(new Box(listHandler[i].x,listHandler[i].y));
	}
	
	this.typeHeadSource="none";
	this.typeHeadTarget="none";
	
	this.fillHeadSource=false;
	this.fillHeadTarget=false;
	
	this.headSourceDash=false;
	this.headTargetDash=false;
	
	this.lineDash=false;
	
	this.isText=false;
	this.text="<include>";
	this.label;
	this.editable=false;
	this.stereotype;
	this.stereotypeMargemHeight;
	this.labelDisabled;
	
	this.isCardinality=true;
	this.cardinalitySource="";
	this.cardinalityTarget="";
	this.order=0;
	this.sourceIsAnchorFixe=source.isAnchorFixed();
	this.targetIsAnchorFixe=target.isAnchorFixed();
	
	this.labelCardinalitySource;
	this.labelCardinalityTarget;
	this.labelSelected;
	
//	console.log("listHandler "+ listHandler)
}	

Edge.prototype = {	
		
		setAnchorTarget : function(value) {
			this.AnchorEnd=value;	
		},

		setAnchorSource : function(value) {
			this.AnchorStart=value;	
		},
		
		conectNodes : function() {
			var boxsSource,boxTarget;
			if(this.sourceIsAnchorFixe){
				 boxsSource=this.source.anchorsDistanceMin(this.getFirstAnchor());
			}
			else{
				boxsSource=this.source.anchorsDistanceMin(this.target);
				
			}
			
			if(this.targetIsAnchorFixe){
				boxTarget=this.target.anchorsDistanceMin( this.listHandler[1]);
			}
			else{
				boxTarget=this.target.anchorsDistanceMin( this.source);
				
			}
			this.setFirstAnchor(boxsSource);
			this.setLastAnchor(boxTarget);
			
			
		},
		drawIcon : function(ctx) {
			
			//var img = new Image();
			var height=this.height;
			var width=this.width;
			//img.addEventListener("load", function() {
			 ctx.drawImage(this.imageIconTollBarSVG, 0,0,width,height); 
			//}, false);

			//img.src = this.pathIconTollBarSVG ;
			ctx.beginPath();

		},
		
		contains : function(mx,my) {
			
			if(this.isCardinality){
				if(this.labelCardinalityTarget.contains2(mx,my))
					console.log("this.cardinalityTarget  "+this.cardinalityTarget )
				if(this.labelCardinalitySource.contains2(mx,my))
					console.log("this.cardinalitySource  "+this.cardinalitySource )
			}
			
			if (this.insideLabel(mx,my)) 
				return true;
			var point ={x:mx,y:my};
			for(var i=0; i< this.listHandlerSize()-1;i++){
				if(this.pointInLine(this.listHandler[i],this.listHandler[i+1],point))
					return true;
			}
			
//			if(this.label.contains(mx,my)){
//				this.editable=true;
//				return true;
//			}
				
			return false;

		},
		
		insideLabel: function(mx, my){
			if(this.label.contains(mx,my) && !this.labelDisabled){
				this.selectLabel();
				return true;
			}
			
		},
		
		pointInLine : function(A,B, C) {
			 var precision = 10;
			   
			   if (Math.abs(A.x - C.x)<precision) return true;
			   // if AC is vertical.
			   if (Math.abs(A.y - C.y)<precision) return true;
			   
				var a = (B.y - A.y) / (B.x - A.x);
				var b = A.y - a * A.x;
				if (Math.abs((C.y - (a * C.x + b))) < precision )
					 return true;
				return false;
				},
			
			
		updateBorderSize : function() {
//			console.log("this.listHandler ---->"+ JSON.stringify(this.listHandler ));
			this.x=this.listHandler[0].x;
			this.y=this.listHandler[0].y;
			this.width= this.x;
			this.height=this.y;
			
			for(var i=1; i< this.listHandlerSize();i++){
				this.x=Math.min(this.x,this.listHandler[i].x);
				this.y=Math.min(this.y,this.listHandler[i].y);
				this.width= Math.max(this.width,this.listHandler[i].x);
				this.height=Math.max(this.height,this.listHandler[i].y);
			}
			
			this.width-=this.x;
			this.height-=this.y;
			
			
			if(this.width<this.getTextWidth()/2)
				this.width=this.getTextWidth()/2;
//			console.log(this.id +" "+this.x + " "+this.y)
		},
		getTextWidth: function (){
			if(this.label && this.label.width)
				return this.label.width;
			return 10;
		},
		
		insideHandle : function(mx, my) {
			for (var i = 0; i < this.listHandlerSize(); i++) {
				if (this.listHandler[i].contains(mx, my) ) {
					this.handlerSelected = this.listHandler[i];
					return true;
				}
			}
			return false;
		},
		
		insideHandlerSource : function(mx, my) {
				if (this.listHandler[0].contains(mx, my) ) {
					return true;
				}
				return false;
		},
		
		insideHandlerTarget : function(mx, my) {
			if (this.listHandler[this.listHandlerSize()-1].contains(mx, my) ) {
				return true;
			}
			return false;
		},
		adjustLabel : function (ctx){},
		
		 /**
		 * creare the image Icon
		 * @return {null}- 
		 */
		loadImage : function() {
			
//			this.imageIconTollBarSVG = new Image();
//			this.imageIconTollBarSVG.src = this.pathIconTollBarSVG ;
//			
//			this.arrow=new Image();
//			this.arrow.src = "./image/arrow2.svg" ;
		},
		selected :function (){
			this.select = true;
			this.lineColor ="blue";//"#484848";
			
		},
		unSelected :function (){
			this.select = false;
			this.lineColor="black";
			this.handlerSelected=undefined;
			this.unSelectedLabel();
//			if(this.lineColor=="black")
			
//			else
				
		},
		
		selectLabel: function (){
			if(this.label && !this.labelDisabled){
				this.editable=true;
				this.label.selectLabel=true;
			}	
		},
		
		unSelectedLabel : function (){
			if(this.label){
				this.editable=false;
				this.label.selectLabel=false;
				this.label.showBar=false;
			}
		},
		
		listHandlerSize : function () {
			return this.listHandler.length;
		},
		
		getFirstAnchor : function () {
			return this.listHandler[0];
		},
		
		getLastAnchor : function () {
			return this.listHandler[this.listHandler.length-1];
		},
		setFirstAnchor : function (box) {
			 this.listHandler[0]=box;
		},
		
		setLastAnchor : function (box) {
			this.listHandler[this.listHandler.length-1]=box;
		},
		getHandlerSelected: function(){
			return this.handlerSelected;
		},
		setCorrdinateHandlerSelected: function (x,y){
			this.handlerSelected.updatePosition(x,y);
		},
		getListHandler: function () {
			return this.listHandler;
		},
		
		setEdgeType : function (edgeType,list){
			this.edgeType=edgeType;
			this.type=edgeType.type;
			this.pathIconTollBarSVG=edgeType.iconTollbarSVGPath;
			this.imageIconTollBarSVG = edgeType.getImageIconTollBarSVG();
			this.variant=edgeType.variant;
			this.islineDuple=convertStringBoolen(edgeType.lineDuple);
			this.lineDash=convertStringBoolen(edgeType.lineDash);
			this.isCardinality=convertStringBoolen(edgeType.cardinality); 
			this.createFeatures(edgeType.features);
			this.typeHeadSource=edgeType.headSource.type;
			this.typeHeadTarget=edgeType.headTarget.type;
			
			this.fillHeadSource=convertStringBoolen(edgeType.headSource.fill);
			this.fillHeadTarget=convertStringBoolen(edgeType.headTarget.fill);
			
			this.headSourceDash=convertStringBoolen(edgeType.headSource.dash);
			this.headTargetDash=convertStringBoolen(edgeType.headTarget.fill);
//			console.log(edgeType.stereotype);
			
			//this.pathimgSVG=edgeType.imgSVGPath;
		//	this.loadImage(); 
//			this.arrow=edgeType.arrow;
			this.listTypeCanBeconnected=[];
			//this.createLabel(edgeType.labelPosition);
			//this.updateHandles();
			
			this.updateBorderSize(); 
			
			this.createLabel(edgeType.labelConf);
			
			if(edgeType.stereotype && edgeType.stereotype!="null" ){
			 this.stereotype=edgeType.stereotype.name;
			 this.stereotypeMargemHeight=this.getStereotypeMargemHeight(edgeType.stereotype.marginHeight);
			}
			if(this.isCardinality){
				var args={defaultValue:this.cardinalitySource,underlined:0,alignment:"center",position:"center"};
				this.labelCardinalitySource=new TextBox(0, 10,20,args);
				this.labelCardinalitySource.selectLabel=false;
				
				var args={defaultValue:this.cardinalityTarget,underlined:0,alignment:"center",position:"center"};
				this.labelCardinalityTarget=new TextBox(0, 2,20,args);
				this.labelCardinalityTarget.selectLabel=false;
				
			}
			//this.createAnchor(edgeType.listAnchors);
		},
		
		createLabel : function (labelConf){
			var position =labelConf.position.toLowerCase();
			this.configLabelPosition(position);
			//alert(position)
//			if(position=="center" || position==this.source.type.toLowerCase()
//					||position==this.target.type.toLowerCase()){
				var x=this.x+this.width/2;
				var args={defaultValue:labelConf.defaultValue,underlined:parseInt(labelConf.underlined),alignment:labelConf.alignment, position:position};
				this.label=new TextBox(x, this.y+this.height/2,this.width,args);
//				this.labelPosition=position;
//				console.log(this.labelPosition);
				this.labelDisabled=labelConf.disabled;
				if(this.labelDisabled)
					this.label.selectLabel=false;
				else
					this.label.selectLabel=true;
				this.drawLabel=true;
		},
		
		configLabelPosition: function (position){
			if(position=="top"){
				this.labelPosition=-15;
			}
			else if(position=="bottom"){
				this.labelPosition=15;
			}
			else {
				this.labelPosition=0;
			}
		},
		
		getStereotypeMargemHeight: function (marginHeight){
			if(!marginHeight)return 0;
			if(typeof(marginHeight)=="number")
				return marginHeight;
			if(typeof(marginHeight)=="string")
				return parseInt(marginHeight);
			return 0;
			
		},
		
		createFeatures  (features){
			this.features=features;
			for (var i in features ){
					if(features[i].name=="total"){
						this.total=features[i].value;
						continue;
						
					}
				this[features[i].name]= convertType(features[i].type, features[i].value);
				}
		},
		
		setFeatures(features){
			for (var i in features ){
				if(features[i].name=="total")
					this.total=features[i];
				this[features[i].name]= convertType(this.getFeature(features[i].name).type, features[i].value);
			
			}
			},
		
		setFeaturesSource(features){
			for (var i in features ){
				if(features[i].name=="cardinality"){
					this.setCardinalitySource(features[i].value);
				}
			}
			},
			
		setFeaturesTarget(features){
			for (var i in features ){
				
				if(features[i].name=="cardinality"){
					this.setCardinalityTarget(features[i].value);
				}
			}
			
			},
			
		getFeature(name){
			for (var i in this.features )
				if(this.features[i].name==name)
					return this.features[i];
		},
		updateLabelPosition : function (){
//			var x=this.x+this.width/2;
//			var y=this.y;
//			var height= this.height;
//			
//			if(this.labelPosition==this.source.type.toLowerCase())
//				this.label.updatePosition(this.listHandler[0].x,this.listHandler[0].y);
//			
//			else if(this.labelPosition=="center")
//				this.label.updatePosition(x,y+height/2);
//			
//			else if (this.labelPosition==this.target.type.toLowerCase())
//				this.label.updatePosition(this.getLastAnchor().x,this.getLastAnchor().y);
		},
		
		draw :function(ctx) {
			
			this.conectNodes();
			
			this.updateBorderSize();
//			if(this.drawLabel)this.updateLabelPosition();
			ctx.save();
			if(this.modify)
				 ctx.globalAlpha  = 0.3;
			ctx.beginPath();
			ctx.lineCap="round";
			if(this.lineDash)
				ctx.setLineDash([5]);
//			if (this.islineDuple){
			if (this.total=="yes"){
				this.drawline(ctx,-1);
				this.drawline(ctx,+1);
			}
			else {	
				this.drawline(ctx,0);
			}
			
			ctx.strokeStyle=this.lineColor; 
			ctx.lineWidth=1;
			ctx.stroke();
			ctx.restore();
			
			this.drawArrow(ctx);
			
			if(this.select)
				for(var i in this.listHandler){
					this.listHandler[i].draw(ctx);
			}
			
			//if(this.drawLabel)this.label.draw(ctx);
			
		},
		
		drawline : function (ctx,dist){
			var handerlHalhpSize=3;
			if( this.listHandler[0]!=undefined)
				ctx.moveTo(this.listHandler[0].x+handerlHalhpSize, this.listHandler[0].y+dist+handerlHalhpSize);
			
			for(var i=1; i< this.listHandler.length;i++){
				ctx.lineTo(this.listHandler[i].x+handerlHalhpSize, this.listHandler[i].y+dist+handerlHalhpSize);
			}
		},

		
		drawLineDuple : function (ctx){
			if( this.listHandler[0]!=undefined)
				ctx.moveTo(this.listHandler[0].x, this.listHandler[0].y);
			
			for(var i=1; i< this.listHandler.length;i++){
				ctx.lineTo(this.listHandler[i].x, this.listHandler[i].y);
			}
		
		},
		
		findAngle : function(sx, sy, ex, ey) {
		    // make sx and sy at the zero point
		    return Math.atan2((ey - sy), (ex - sx));
		},
		
		drawCurvedArrow : function(locx, locy, angle, size,ctx) {
			    ctx.save();
			    //ctx.moveTo(locx, locy);
			    ctx.beginPath();
//			    var a=0*Math.PI/180;
			    ctx.rotate(-Math.PI/180)
//			    ctx.arc(locx,locy,size,1.5*Math.PI+angle,Math.PI/2+angle);
			    ctx.arc(locx,locy,size,1.5*Math.PI+angle,Math.PI/2+angle);
			    ctx.strokeStyle=this.lineColor; 
			    ctx.stroke();
			    ctx.restore();
			}, 
			
		drawRectangle : function (x, y, angle, size,ctx){
//			ctx.save();
//		    ctx.rotate( angle);
//		    ctx.translate(x,y);
////		    this.arrow=new Image();
////			this.arrow.src = "./image/arrow2.svg" ;
//		    ctx.drawImage(this.arrow, x,y,20,20);
//			
//			ctx.restore();
		},	
			
		calculeInfo:function(hander1, hander2){
			var x= Math.min(hander1.x, hander2.x);
			var y= Math.min(hander1.y, hander2.y);
			var width= Math.max(hander1.x, hander2.x)- x;
			var height= Math.max(hander1.y, hander2.y)- y;
			
			var startRadians=Math.atan((hander2.y-hander1.y)/(hander2.x-hander1.x));
			startRadians+=((hander2.x>=hander1.x)?-90:90)*Math.PI/180;
			
			var endRadians=Math.atan((hander2.y-hander1.y)/(hander2.x-hander1.x));
	        endRadians+=((hander2.x>hander1.x)?90:-90)*Math.PI/180;
	        
			return{x:x, y:y, width:width, height:height, startRadians:startRadians, endRadians:endRadians};
		},
		
		
		drawArrow : function(ctx){
			 var handerSource1=this.listHandler[0];
			 var handerSource2=this.listHandler[1]; 
			 var parametsSource=this.calculeInfo(handerSource1,handerSource2);
			 
			 var size=this.listHandlerSize()-1;
			 var handerTarget1=this.listHandler[size-1];
			 var handerTarget2=this.listHandler[size]; 
			 var parametsTarget=this.calculeInfo(handerTarget1,handerTarget2);
			 var half=3
	   switch (this.typeHeadSource) {
	       	
			case "none":
				;
				break;
			case "arrow-complete":
				this.drawArrowhead(ctx,handerSource1.x+half, handerSource1.y+half,parametsSource.startRadians,this.fillHeadSource,this.headSourceDash);
				break;
			case "rhombus":
				this.drawRhombusHead(ctx,handerSource1.x+half, handerSource1.y+half,parametsSource.startRadians,this.fillHeadSource,this.headSourceDash);
				break;	
			case "arrow-open":
				this.drawArrowOpen(ctx,handerSource1.x+half, handerSource1.y+half,parametsSource.startRadians,this.headSourceDash);
//				this.drawArrowhead(ctx,handerSource1.x+half, handerSource1.y+half,parametsSource.startRadians,this.fillHeadSource,this.headSourceDash);
				break;
			case "sime-arrown":
				this.drawSimeArrowhead(ctx,handerSource1.x+half, handerSource1.y+half,parametsSource.startRadians,this.headSourceDash);
//				this.drawArrowhead(ctx,handerSource1.x+half, handerSource1.y+half,parametsSource.startRadians,this.fillHeadSource,this.headSourceDash);
				break;
				
	   }
	        
	        
	   switch (this.typeHeadTarget) {
			case "none":
				;
				break;
			case "arrow-complete":
				this.drawArrowhead(ctx,handerTarget2.x+half, handerTarget2.y+half, parametsTarget.endRadians,this.fillHeadTarget,this.headTargetDash);
				break;
			case "rhombus":
				this.drawRhombusHead(ctx,handerTarget2.x+half, handerTarget2.y+half, parametsTarget.endRadians,this.fillHeadTarget,this.headTargetDash);
				break;	
			case "arrow-open":
				this.drawArrowOpen(ctx,handerTarget2.x+half, handerTarget2.y+half, parametsTarget.endRadians,this.headTargetDash);
//				this.drawArrowhead(ctx,handerTarget2.x+half, handerTarget2.y+half, parametsTarget.endRadians,this.fillHeadTarget,this.headTargetDash);
				break;	
			case "sime-arrown":
				this.drawSimeArrowhead(ctx,handerTarget2.x+half, handerTarget2.y+half, parametsTarget.endRadians,this.headTargetDash);
				break;	

//			default:
//				break;
		}
	        
	        if(this.isCardinality){
	        	
	        	var size=this.listHandlerSize()-1;
				var hander1=this.listHandler[0];
				var hander2=this.listHandler[size]; //this.getFirstAnchor()
				
		        this.drawCardinality(ctx, handerSource1, handerSource2, parametsSource.startRadians);
		        this.drawCardinality2(ctx, handerTarget1 ,handerTarget2, parametsTarget.endRadians);
		        
		        
		        
	        }
//	        if(this.stereotype)
//	        	this.drawText(ctx);
	        this.drawText(ctx);
		
		},
		
		
       
        

	drawArrowhead : function(ctx, x, y, radians,fillHead,headDash) {
		
		ctx.save();
		ctx.beginPath();
		ctx.translate(x, y);
		ctx.rotate(radians);
		ctx.moveTo(0, 0);
		ctx.lineTo(8, 15);
		// ctx.moveTo(0,0);
		ctx.lineTo(-8, 15);
		ctx.lineTo(0, 0);
		if(fillHead)
			ctx.fillStyle = this.lineColor;
		if(headDash)
			ctx.setLineDash([3]);
		ctx.strokeStyle = this.lineColor;
		ctx.stroke();
		ctx.closePath();
		ctx.fill();
		ctx.restore();
	},
      
		
	drawArrowOpen : function(ctx, x, y, radians,headDash) {
		ctx.save();
		ctx.beginPath();
		ctx.translate(x, y);
		ctx.rotate(radians);
		ctx.moveTo(0, 0);
		ctx.lineTo(8, 15);
		 ctx.moveTo(0,0);
		ctx.lineTo(-8, 15);
//		ctx.lineTo(0, 0);
//			ctx.setLineDash([5]);
		if(this.fillHead)
			ctx.fillStyle = this.lineColor;
		if(headDash)
			ctx.setLineDash([3]);
		ctx.strokeStyle = this.lineColor;
		ctx.stroke();
//			ctx.closePath();
		ctx.fill();
		ctx.restore();
		
		},	
		
		
	drawSimeArrowhead : function(ctx, x, y, radians,headDash,headSource) {
		ctx.save();
		ctx.beginPath();
		ctx.translate(x, y);
		ctx.rotate(radians);
		if(radians<0){
			ctx.moveTo(0, 0);
			ctx.lineTo(8, 15);
		 }
		else{
			ctx.moveTo(0,0);
			ctx.lineTo(-8, 15);
		}
		if(this.fillHead)
			ctx.fillStyle = this.lineColor;
		if(headDash)
			ctx.setLineDash([3]);
		ctx.strokeStyle = this.lineColor;
		ctx.stroke();
		ctx.fill();
		ctx.restore();
		
		},		
        
		
		
	drawRhombusHead:function(ctx,x,y,radians,fillHead,headDash){
		  ctx.save();
		  ctx.beginPath();
		  ctx.translate(x,y);
		  var a=45*Math.PI/180;
		  ctx.rotate(radians+a);
		  if(fillHead)
			  ctx.fillStyle = this.lineColor;
		  if(headDash)
				ctx.setLineDash([5]);
		  //ctx.fillRect(0,0,15,15);
		  ctx.strokeStyle=this.lineColor;
		  ctx.rect(0,0,12,12);
		  ctx.stroke();
		  ctx.closePath(); 	
		  ctx.fill();
	      ctx.restore();
      
  },
  

//	drawArrowhead : function(ctx, x, y, radians) {
//		ctx.save();
//		ctx.beginPath();
//		ctx.translate(x, y);
//		ctx.rotate(radians);
//		ctx.fillStyle = this.lineColor;
//		var x=20;
//		var args={text:"<include>",underlined:0};
//		
//		var label=new TextBox(0, 10,20,args);
//		label.selectLabel=false;
//		//ctx.translate(x,y);
//		var a=0*Math.PI/180;
//		ctx.rotate(-radians)
//		label.draw(ctx);;
//	
//		ctx.strokeStyle = 'black';
//		ctx.stroke();
//
//		ctx.restore();
//
//	},

  /**
   * get the angle between source and target 
   * não está a ser usado
   */
  findAngle2 : function(){
		var size=Math.round(((this.listHandlerSize()-1)/2)+0.5);
		var hander1=this.listHandler[size-1];
		var hander2=this.listHandler[size];
		return this.findAngle(hander1.x,hander1.y, hander2.x, hander2.y);
  },
		
  drawText : function(ctx) {
	  	var size=Math.round(((this.listHandlerSize()-1)/2)+0.5);
		var hander1=this.listHandler[size-1];
		var hander2=this.listHandler[size];
		var paramets=this.calculeInfo(hander1,hander2);
		
		var x=paramets.x+paramets.width/2;
		var y=paramets.y+paramets.height/2;
		var radians=paramets.endRadians;
		var angle=this.findAngle(hander1.x,hander1.y, hander2.x, hander2.y)
		
		 if(this.stereotype){
			this.drawStereotype(ctx, x,y,angle,radians);
		 }
		 else{
		
		
//			 	ctx.save();
//				ctx.beginPath();
//				ctx.translate(x, y);
//				ctx.rotate(radians);
//				ctx.fillStyle = this.lineColor;
//				var args={defaultValue:"text",underlined:0,alignment:"center"};
//				
//				var label=new TextBox(0, 10,20,args);
//				label.selectLabel=false;
//		//		ctx.translate(x,y);
//				var a=0*Math.PI/180;
//				ctx.rotate(-radians)
//				label.draw(ctx);
//				ctx.strokeStyle = 'black';
//				ctx.stroke();
//				ctx.restore();
				
				this.label.updatePosition(x,y+this.labelPosition,1);
//				if(this.labelDisabled)
//					this.label.selectLabel=false;
//				else
//					this.label.selectLabel=true;
				this.label.draw(ctx);
//				this.label.selectLabel=true;
				
			 
			 
			 
		 }

	},
	
	drawStereotype: function(ctx, x,y,angle,radians){
		if(this.stereotype=="CurvedArrow"){
			this.drawCurvedArrow(x, y+5, angle, 12,ctx)
			return;
		}
		
		ctx.save();
		ctx.beginPath();
		ctx.translate(x, y-20);
		ctx.rotate(radians);
		ctx.fillStyle = this.lineColor;
		var args={defaultValue:"<< "+this.stereotype+" >>",underlined:0,alignment:"center"};
		
		var label=new TextBox(0, 10+this.stereotypeMargemHeight,20,args);
		label.selectLabel=false;
//		ctx.translate(x,y);
		var a=0*Math.PI/180;
		ctx.rotate(-radians)
		label.draw(ctx);
		ctx.strokeStyle = this.lineColor;
		ctx.stroke();
		ctx.restore();
		
		
	},
  
	  drawCardinality : function(ctx,hander1,hander2, startRadians) {
//		  	var size=this.listHandlerSize()-1;
//		    var hander1=this.listHandler[size-1];
//			var hander2=this.listHandler[size];
			var valueAdd={x:15, y:-5};
			
			if(hander1.x>hander2.x)
				valueAdd.x=-10;
			//if(hander1.y<hander2.y)
			this.labelCardinalitySource.updatePosition(hander1.x+valueAdd.x, hander1.y+valueAdd.y,1)
			this.labelCardinalitySource.draw(ctx);
//			this.labelCardinalitySource.drawBorder(ctx);
//			ctx.save();
//			ctx.beginPath();
//			ctx.translate(hander1.x+valueAdd.x, hander1.y+valueAdd.y);
//			ctx.rotate(startRadians);
//			
//			var args={defaultValue:this.cardinalitySource,underlined:0,alignment:"center"};
//			var label=new TextBox(0, 10,20,args);
//			label.selectLabel=false;
//			ctx.rotate(-startRadians)
//			label.draw(ctx);
//			
//			
//			
//			ctx.strokeStyle = this.lineColor;
//			ctx.stroke();
//			ctx.restore();
		},
  
		
		 drawCardinality2 : function(ctx,hander1,hander2, endRadians) {
//			  	var size=this.listHandlerSize()-1;
//			  	var hander1=this.listHandler[size-1];
//				var hander2=this.listHandler[size];
				var valueAdd={x:15, y:-5};
				if(hander1.x<hander2.x)
					valueAdd.x=-10;

				
				this.labelCardinalityTarget.updatePosition(hander2.x+valueAdd.x, hander2.y+valueAdd.y,1)
				this.labelCardinalityTarget.draw(ctx,hander2.x+valueAdd.x, hander2.y+valueAdd.y,1,endRadians);
				
//				this.labelCardinalityTarget.drawBorder(ctx);
				
//				console.log("Aqui "+this.cardinalityTarget)
			  
//				ctx.save();
//				ctx.beginPath();
//				ctx.translate(hander2.x+valueAdd.x, hander2.y+valueAdd.y);
//				ctx.rotate(endRadians);
//				
//				var args={defaultValue:this.cardinalityTarget,underlined:0,alignment:"center"};
//				var label=new TextBox(0, 10,20,args);
//				label.selectLabel=false;
////				var a=0*Math.PI/180;
//				ctx.rotate(-endRadians)
//				label.draw(ctx);
//				ctx.strokeStyle = 'black';
//				ctx.stroke();
//				ctx.restore();
			},
        
		getJson : function (){
			
			var edgeAux = {};
			var properties=this.edgeType.properties;
			
			for (var i in properties)
				if(convertStringBoolen(properties[i].impExp)){
					if(properties[i].name=="label")
						edgeAux.label=this.label.getText();
					else
						edgeAux[properties[i].name]=this[properties[i].name];
					}
			edgeAux.source= this.source.id;
			edgeAux.target= this.target.id;
			edgeAux.listHandler=this.getJsonListHandler();
			edgeAux.features=[];
			edgeAux.featuresSource=[];
			edgeAux.featuresTarget=[];
			if(this.isCardinality){
//				if(this.cardinalitySource!="")
//					edgeAux.featuresSource.push({name: "cardinality", value:this.cardinalitySource});
				if(this.cardinalityTarget!="")
					edgeAux.featuresTarget.push({name: "cardinality", value:this.cardinalityTarget});
//				if(this.total)
//					edgeAux.features.push({name: "total", value:this.total});
			}
				
			
			for (var i in this.features)
				if(convertStringBoolen(this.features[i].impExp)){
//					if(this.features[i].name== "cardinalitySource" || this.features[i].name== "cardinalityTarget" || this.features[i].name== "total" )
//						continue;
					if(this.features[i].source=='true')
						edgeAux.featuresSource.push({name:this.features[i].name, value:this[this.features[i].name]});
					else if(this.features[i].target=='true')
						edgeAux.featuresTarget.push({name:this.features[i].name, value:this[this.features[i].name]});
					else
						edgeAux.features.push({name:this.features[i].name, value:this[this.features[i].name]});
				}
	//				edgeAux[this.features[i].name]=this[this.features[i].name];
//				//console.log("tesss=this[this.features[i].name];
			return edgeAux;
		},
		
		
		getJsonListHandler : function(){
			var list=[]
			for (var i=0; i< this.listHandler.length;i++)
				list.push({x:parseInt(this.listHandler[i].x),y:parseInt(this.listHandler[i].y)});
			return list;
			
		},
		
		showBarLabel(ctx) {
			
			if(!this.label.showBar)
				this.label.showBar=true;
			else
				this.label.showBar=false;
		},
		
		isNode(){
			return false;
		},
		
		isEdge(){
			return true;
		},
		
		increaseOrder(){
			this.order++;
		},
		
		decreaseOrder(){
			if(this.order>0)
				this.order--;
		},
		
		islabelEditable(){
			return this.existLabel() && this.isLabelEditable;
		},
		
		existLabel(){
			return this.label!=undefined;
		},
		
		islabelEditable(){
			return (this.existLabel() && this.editable)
		},
		getName(){
			if(!this.islabelEditable())
				return this.label.getText();
			return "";
		},
		
		setName(text){
			if(!this.islabelEditable())
				this.label.setText(text);
		},
		
		getType(){
			return this.edgeType.type;
		},
		
		getCardinalitySource(){
			return this.cardinalitySource;
		},
		
		setCardinalitySource(value){
			this.cardinalitySource=value;
			this.setLabelCardinalitySource(value);
		},
		

		getCardinalityTarget(){
			return this.cardinalityTarget;
		},
		
		setCardinalityTarget(value){
			this.cardinalityTarget=value;
			this.setLabelCardinalityTarget(value);
		},
		
		setLabelCardinalitySource(text){
			this.labelCardinalitySource.setText(text);
		},
		
		setLabelCardinalityTarget(text){
			this.labelCardinalityTarget.setText(text);
		},
		getUrl(){
			return this.edgeType.infoUrl;
		},
		setTotal(value){
			this.total=value;
		}
}






/*
 *  inLine( A,  B,  C) {
   // if AC is horizontal
   if (A.x == C.x) return B.x == C.x;
   // if AC is vertical.
   if (A.y == C.y) return B.y == C.y;
   // match the gradients
   return (A.x - C.x)*(A.y - C.y) == (C.x - B.x)*(C.y - B.y);
}

pointInLine : function(A,B, C) {
		   var precision = 15;
		   // if AC is horizontal
		   if (Math.abs(A.x - C.x)<precision) return true;//(C.y < Math.max(A.y,B.y)) && (C.y > Math.min(A.y,B.y));
		   // if AC is vertical.
		   if (Math.abs(A.y - C.y)<precision) return true;// (C.x < Math.max(A.x,B.x)) && (C.x > Math.min(A.x,B.x));
		   // match the gradients
			var a = (B.y - A.y) / (B.x - A.x);
			var b = A.y - a * A.x;
			if (Math.abs((C.y - (a * C.x + b))) < precision )//&& B.x>C.x && A.x<C.x );
				 return true;//(C.y < Math.max(A.y,B.y)) && (C.y > Math.min(A.y,B.y)) && (C.x < Math.max(A.x,B.x)) && (C.x > Math.min(A.x,B.x));
			return false;
			},
 
*/
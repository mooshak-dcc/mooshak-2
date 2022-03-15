function TextBox(x, y,width,args) {
	
	this.x=x;
	this.y=y;
	this.width=width;
	this.height=12;
	this.label = args.defaultValue;
	this.showBar=false;
	this.labelShiftStart=0;
	this.select=true;
	this.selectLabel=true;
	this.textFont="11px Arial";
	this.URLinfo="";
	this.LabelWithoutBar=false;
	this.cursorPosition={column:args.defaultValue.length, line:0}
	if(args.underlined.value)
		this.underlined=santiationUnderlined(args.underlined.value);
	else	
		this.underlined=santiationUnderlined(args.underlined);
	this.alignment=args.alignment.toLowerCase();
	this.id=args.id;
	this.alpha=false;
	this.disabled=false;//args.disabled;
//	console.log("args.position "+ JSON.stringify(args));
	this.position=args.position.toLowerCase();
	//alert("this.position "+ this.position)	
	this.marginWidth=toInteger(args.marginWidth);
	this.marginHeight=toInteger(args.marginHeight);
	this.ctx;
	
	
}


TextBox.prototype = {
		
		/**
		 * 	Deletes the character in the current position
		 *  @return {null}
		 */
		deleteLastCharName : function() {
			if(this.selectLabel){
				this.cursorPosition.column=0;
				this.label= "";
				this.selectLabel=false;
				return;
			}
			
			var s="";
			if(this.cursorPosition.column>=0){
				for(var i in this.label){
					if(i==this.cursorPosition.column-1) continue;
						s+=this.label.charAt(i);
				}
				this.decreaseColumn();
			}
			this.label= s;
		
		},
		
		
		/**
		 * 	Get the current position
		 *  @return {Number} -current position
		 */
		getCursorPosition : function() {
			return this.cursorPosition.column;
		},
		
		
		/**
		 * 	set the current position
		 *  @param {Number} position
		 *  @return {null}
		 */
		setCursorPosition : function(value) {
			 this.cursorPosition.column=value;
		},
		
		
		/**
		 * 	Resets the current position, updating to the end position
		 *  @return {null}
		 */
		restartCursorPosition :function(){
			this.cursorPosition.column=this.label.length;
		},
		
		
		/**
		 * 	marked the label with selected
		 *  @param {Number} position
		 *  @return {null}
		 */
		selected : function (){
			if(!this.isDisable()){
				
				this.selectLabel=true;
				this.restartCursorPosition();
			}
			else{
				this.selectLabel=false;
				this.label.showBar=false;
			}
			
		},
		
		/**
		 * 	Increases the current position of the cursor, 
		 * 	in case is already in the final position, keep the position
		 *  @return {null}
		 */
		increaseColumn  : function() {
			if(this.selectLabel)
				this.selectLabel=false;
			if(this.cursorPosition.column==this.label.length){
				return this.cursorPosition.column
			};
			this.cursorPosition.column++;
			
			this.showBar=true; //show bar in label immediate 

		},
		
		
		/**
		 * 	Decreases the current position of the cursor, 
		 * if it is already in the starting position, maintains the position
		 *  @return {null}
		 */
		decreaseColumn  : function() {
			
			if(this.cursorPosition.column==0){
				return this.cursorPosition.column
			}
			this.cursorPosition.column--;
			if(this.selectLabel)
				this.selectLabel=false;
			
			this.showBar=true; //show bar in label immediate 	
		},
		
		
		/**
		 * 	Increases the line of the cursor, 
		 * 	in case is already in the final line, maintains the line
		 *  @return {null}
		 */
		increaseLine  : function() {
			if(this.selectLabel)
				this.selectLabel=false;
			this.showBar=true; //show bar in label immediate 
			
			    for(var i=this.cursorPosition.column; i<this.label.length;i++){
			    	this.cursorPosition.column++;
			    	if(this.label[i]=='\n'){
			    		break;
			    	}
			    }
			    
			    var posF=this.cursorPosition.column+this.getCoordPosition().column-1;
			    if(this.label[posF])
			    	this.cursorPosition.column= posF;

		},
		
		
		/**
		 * 	Decreases the line in position of the cursor, 
		 * if it is already in the starting line, maintains the line
		 *  @return {null}
		 */
		decreaseLine  : function() {
			
			if(this.selectLabel)
				this.selectLabel=false;
			this.showBar=true; //show bar in label immediate 
			    for(var i=this.cursorPosition.column; i>=0;i--){
			    	if(this.label[i-1]=='\n')
			    		break;
			    }
			    this.cursorPosition.column=i-1;
		},
		
		/**
		 * 	get the position of the current row and column of the course
		 *  @return {Object} -{line:line, column:column } 
		 */
		getCoordPosition(){
			var column=0;
			var line=0
			    for(var i=0; i<this.cursorPosition.column;i++){
			    	if(this.label[i]=='\n'){
			    		line++;
			    		column=0;
			    	}
			    	column++;
			    }
			 return {line:line, column:column }
			
		},
		
		
		/**
		 * add the character in the label text 
		 * 
		 * @param {char} char the character
		 *            
		 * @return {null}
		 */
		addCharLabel : function(char) {
		
			if(this.isDisable()) return ;
			this.label = this.insertcharLabel(char);
			this.cursorPosition.column++;
			this.showBar=true;
		},
		


		/**
		 * Draw the bar of the current cursor position
		 * 
		 * @param {Context} ctx - getContext('2d')
		 *            
		 * @return {null}
		 */
		label1 : function(ctx) {
			
			var coord=this.getCoordPosition();
			var line= coord.line;
			var posX=coord.column;
			if(line>0 )	  
				posX--;
			var arraytext = this.label.split("\n");
			var text  = arraytext[line];
		    
		    var widthLabel=ctx.measureText(text).width;
		    var text2=text.substring(0,posX);
		    ctx.font = this.textFont;      
			var width =ctx.measureText(text2).width
			var x = this.x-(widthLabel/2);
		    var p =  this.x+width+3; 
		    
		    if(this.alignment=="center"){
		    	var xAux=this.x + this.width/2 - widthLabel/2 -1; // I add -1 because to around division 
		    	p = xAux+width;
			}
		    else if (this.alignment=="right"){
		    	var xAux = this.x+this.width-widthLabel-4; 
				 p = xAux+width;
			 }
		    
		    var height =parseInt(this.textFont);
		    var y= this.y+ line*height;
		    
		    ctx.save();
			ctx.beginPath();
			ctx.textBaseline = 'middle';
			ctx.moveTo(p,(y + height/2+2 ));
			ctx.lineTo(p,(y - height/2+2 ));
			ctx.lineWidth=1;
			ctx.strokeStyle ='Blue';
			ctx.stroke();
		 ctx.restore();	
			
		},
		
		/**
		 * text of Label until the current cursor position  
		 * 
		 * @return {string}
		 */
		getLabe2 : function() { 
			return this.label.substring(0,this.cursorPosition.column);
			
		},
		
		/**
		 * insert the character in the text of label
		 * 
		 * @param {char} char- the character to be insert
		 *            
		 * @return {null}
		 */
		insertcharLabel : function(char) {
			if(this.selectLabel){
				
				this.cursorPosition.column=1;
				this.label= char;
				if(char=='\n'){
					this.cursorPosition.column=0;
					this.label="";
					}
				this.selectLabel=false;
				return this.label;
			}
			var s="";
			var subString1=this.label.substring(0,this.cursorPosition.column);
			var subString2=this.label.substring(this.cursorPosition.column, this.label.length);
			
			return subString1+char+subString2;
		},
		
		
		/**
		 * Add the first letter of label to upper case
		 * 
		 * @return {null}
		 */
		labelToUpperCaseFirstLetter : function() {
			if(this.label.length>1){
				var subString1=this.label.substring(0,1);
				var subString2=this.label.substring(1, this.label.length);
				this.label=subString1.toUpperCase()+subString2.toLowerCase();
			}
			else{
				this.label=this.label.toLowerCase();
			}
		},
		
		
		/**
		 * convert the text of label to upper case
		 * 
		 * @return {null}
		 */
		labelToUpperCase : function() {
			this.label=this.label.toUpperCase();
		},
		
		
		/**
		 * convert the text of label to lower case
		 * 
		 * @return {null}
		 */
		labelToLowerCase : function() {
			this.label=this.label.toLowerCase();
		},
		
		
		/**
		 * verify if the label contain the coordinate
		 * 
		 * @param {number} mx - coordinate x
		 * 
		 * @param {number} my - coordinate y
		 *            
		 * @return {boolean} true if contain and false if no
		 */
		contains : function(mx, my){
//			console.log(mx + " " + my +" "+this.y + " "+this.getHeight() + " "+this.x + " "+this.width );
//			if((this.x <= mx) && (this.x + this.width  >= mx)) return true;
			return   (this.y <= my) && (this.y + this.getHeight()  >= my);
			
			
		},
		
		contains2 : function(mx, my){
//			console.log(mx + " " + my +" "+this.y + " "+this.getHeight() + " "+this.x + " "+this.width );
//			if((this.x <= mx) && (this.x + this.width  >= mx)) return true;
//			return  ;
//			console.log(mx + " "+my)
			var ctx=this.ctx;
			var arraytext = this.label.split("\n");
			var height =parseInt(this.textFont);
		    var x=this.x;
		       
			for(var i in arraytext){
		    	var width =ctx.measureText(arraytext[i]).width;
			    var y=this.y+height+height*i-2.5;
			    x=this.x;
				 if(this.alignment=="center"){
					 x=this.x+this.width/2-width/2; 
				 }
				 else if (this.alignment=="right"){
					 x=this.x+this.width-width; 
				 }
//				 console.log(mx +" "+ my  + " " + x + " " + (x + width) + " "+ y  +" "+(y + height))
			   if((x-5 <= mx) && (x + width+5  >= mx) )
			   return true
			 }
		
			return  false;
//			return ((x <= mx) && (x + width  >= mx) &&  (y <= my) && (y + height  >= my))
			
		},
		
		/**
		 * Draw the blue rectangle over the text,
		 * it is used to mark that all the text is selected
		 * 
		 * @param {Context} ctx - getContext('2d')
		 *            
		 * @return {null}
		 */
		drawTextBG:function(ctx) {
			var valueAdd=3; //value a adicionar 
			ctx.save()/// lets save current state as we make a lot of changes        
		    ctx.globalAlpha=0.4;
		    ctx.font = this.textFont; /// set font
		    ctx.fillStyle = '#0066FF'; /// color for background
		    var arraytext = this.label.split("\n");
		    var x=this.x;
		    for(var i in arraytext){
		    	var width =ctx.measureText(arraytext[i]).width+valueAdd;
		    	var height =parseInt(this.textFont);
		    	x=this.x;
		    	
		    	if(this.alignment=="center"){
					x=this.x+this.width/2-width/2;
		    	}
		    	else if (this.alignment=="right"){
					 x=this.x+this.width-width; 
				 }
		    	ctx.fillRect(x, this.y+2+ height*i-valueAdd*2, width, height); /// draw background rect assuming height of font
				
		    }
		    ctx.restore();
		    this.width =ctx.measureText(this.label).width; /// get width of text
		    this.height =parseInt(this.textFont);
		    this.x=x;
		},
		
		
		
		/**
		 * Draw the border of the Label
		 * 
		 * @param {Context} ctx - getContext('2d')
		 *            
		 * @return {null}
		 */
		drawBorder:function(ctx) {
			var valueAdd=2; //value a adicionar 
			ctx.save()/// lets save current state as we make a lot of changes        
		    ctx.globalAlpha=0.4;
		    ctx.font = this.textFont; /// set font
		    var arraytext = this.label.split("\n");
		    var x=this.x;
//		    for(var i in arraytext){
//		    	var width =ctx.measureText(arraytext[i]).width+valueAdd;
//		    	var height =parseInt(this.textFont);
//		    	if(this.alignment=="center")
//					x=this.x-width/2;
//		    	ctx.rect(x, this.y+ height*i-valueAdd*2, width, height); /// draw background rect assuming height of font
//				
//		    }
		    ctx.rect(this.x, this.y, this.width, this.height);
		    ctx.stroke();
		    ctx.restore();
		    
		    
//		    this.width =ctx.measureText(this.label).width; /// get width of text
//		    this.height =parseInt(this.textFont);
//		    this.x=x;
		},
		
		
		/**
		 * Writes the label according to the defined alignment
		 * 
		 * @param {Context} ctx - getContext('2d')
		 *            
		 * @return {null}
		 */
		writeLabel(ctx){
			
			var arraytext = this.label.split("\n");
			for(var i in arraytext){
				 line=i*parseInt(this.textFont);
				var width=ctx.measureText(arraytext[i]).width;
				if(this.alignment=="left"){
					ctx.fillText(arraytext[i],this.x+2,this.y+5+line);
				}
				else if(this.alignment=="center"){
					var x=this.x+this.width/2; 
					ctx.fillText(arraytext[i],x-width/2 ,this.y+this.height/2+line);
				}
				else if (this.alignment=="right"){
					var x=this.x+this.width; 
					ctx.fillText(arraytext[i],x-width ,this.y+this.height/2+line);
				}
				else
					throw new Error("Invalid alignment to label, plaease choose: left, center or right:"+this.alignment);
			}
			
		},
		
		
		/**
		 * draw the label
		 * 
		 * @param {Context} ctx - getContext('2d')
		 *            
		 * @return {null}
		 */
		draw: function(ctx){
			var width=ctx.measureText(this.label).width;
			this.ctx=ctx;
			ctx.save();
			if(this.alpha){
				ctx.globalAlpha=0.3;
			}
			ctx.font = this.textFont;
			ctx.fillStyle = "black";
			this.writeLabel(ctx);
			ctx.restore();
			
			if(this.underlined>0)
				this.drawUnderlined(ctx);
			
			if(this.isDisable()) 
				return;
			
			if(this.showBar)
				this.label1(ctx);
			if(this.selectLabel)
				this.drawTextBG(ctx);
			
		},
		
		
		/**
		 * get the measureText of the line in text with max length
		 * 
		 * @param {Context} ctx - getContext('2d')
		 *            
		 * @return {number} -  max width
		 */
		getMaxWidthLine(ctx){
			var arraytext = this.label.split("\n");
			var maxWidth=0;
			for(var i in arraytext){
				if(maxWidth<ctx.measureText(arraytext[i]).width )
					maxWidth=ctx.measureText(arraytext[i]).width;
			}
			return maxWidth;
		},
		
		
		/**
		 * get height of the label
		 * 
		 * @param {Context} ctx - getContext('2d')
		 *            
		 * @return {number} 
		 */
		getHeight(){
			var numberLine=this.label.split("\n").length;
			var height=parseInt(this.textFont);
			return (numberLine*height); 
		},
		
		/**
		 * get the number of the line in label
		 *            
		 * @return {number} 
		 */
		getNumberLine(){
			return this.label.split("\n").length;
		},
		

		drawSelected: function(ctx){	
			ctx.save();
			ctx.font = this.textFont;
			ctx.fillStyle = "red";
			ctx.textAlign = "center";
			ctx.fillText(this.label,this.x,this.y);
			this.drawTextBG(ctx);
			ctx.restore();
		},
		
		
		/**
		 * update the position of the label
		 * 
		 * @param {number} x - the coordinate x
		 * 
		 * @param {number} x - the coordinate x
		 *            
		 * @param {number} width - the width of the node            
		 *            
		 * @return {number} -  max width
		 */
		updatePosition:function(x,y,width){
			this.x=x;
			this.y=y;
			this.width=width;
//			
			if(this.alignment=="center")
				this.y=y-this.getHeight()/2;
		},
		
		
		/**
		 * get the label text 
		 * 
		 * @return {string} -  the label text 
		 */
		getText : function (){
			return this.label;
		},
		
		
		/**
		 * set the label text 
		 * 
		 *  @param  {string} -  the label text 
		 * 
		 * @return {null}
		 */
		setText : function (text){
			this.label=text;
			this.restartCursorPosition();
		},
		
		
		/**
		 * draw the line (underlined) of text 
		 * 
		 * @param {Context} ctx - getContext('2d')
		 *            
		 * @return {number} 
		 */
		drawUnderlined :function(ctx){
			
			var arraytext = this.label.split("\n");
			var height =parseInt(this.textFont);
		    var x=this.x;
		    
		    ctx.save();    
			ctx.beginPath();
			if(this.underlined==2)
				ctx.setLineDash([3]);
			ctx.font = this.textFont;    
			for(var i in arraytext){
		    	var width =ctx.measureText(arraytext[i]).width;
			    var y=this.y+height+height*i-2.5;
			    x=this.x;
				 if(this.alignment=="center"){
					 x=this.x+this.width/2-width/2; 
				 }
				 else if (this.alignment=="right"){
					 x=this.x+this.width-width; 
				 }
			    ctx.moveTo(x,y);
				ctx.lineTo((x+width),y);
			 }
			  ctx.font = this.textFont; 
			  ctx.fillStyle = 'black';
			  ctx.stroke();
			  ctx.restore();
		},
		
		/**
		 * return if the label is disabled
		 *            
		 * @return {boolean} 
		 */
		isDisable(){
			return this.disabled;
		},
		
		
		/**
		 * return the id of label
		 *            
		 * @return {number} 
		 */
		getId(){
			return this.id;
		}

}

function santiationUnderlined(value){
	value+=""; 
	value =value.replace(/\s/g, '');
	
	if(value == 1 || value == "1")
		return 1;
	else if(value == 2 || value == "2")
		return 2;
	
	return 0;
	
}

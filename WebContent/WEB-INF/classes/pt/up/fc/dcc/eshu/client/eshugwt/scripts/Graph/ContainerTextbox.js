/**
 * 
 */
ContainerTextBox.prototype.selected = false;
ContainerTextBox.prototype.fill; 
ContainerTextBox.prototype.text="";
ContainerTextBox.prototype.fillstyle='#FFD700';
function ContainerTextBox(x, y,name,text){
	this.x=x;
	this.y=y;
	this.fill = '#ff0028';
	this.precision=10;
	this.listTextBox=[];
	this.width=0;
	this.height=0;
	this.defaultText=text;
	this.underlined=0;
	this.alignment="left";
	this.name=name;
	this.positionStart=0;
	this.alpha=false;
	this.lineDash=false;
	this.addTextBoxes=false;
	this.disabled=false;
}

/*
 *   btn.style.backgroundImage = iconFormatPanel;
//    btn.style.backgroundRepeat = "no-repeat";
//    btn.style.backgroundSize = "30px";
//    btn.className='material-icons';
    
 */

ContainerTextBox.prototype ={
		
	contains : function(mx, my,width){
		var x1 = this.x;
		var y1 = this.y;
		return  (x1 <= mx) && (x1 + width  >= mx) && (y1 <= my) && (y1 + this.getHeight()  >= my);
		
	},
	
	addTextBox: function (id,text){
		if(text==undefined)
			text=this.defaultText;
		var args={defaultValue:text,underlined:this.underlined,alignment:this.alignment,id:id,disabled:this.disabled,position:"center"};
		
		console.log("ERRORRRRRRRRR "+ this.defaultText)
		var textBox=new TextBox(this.x,this.y,30,args);
		textBox.selectLabel=false;
		
		this.listTextBox.push(textBox);
			
		this.height+=13;
//		this.height+=textBox.getHeight()+2 ;
		return textBox;
	},
	removeTextBox:function(index){
//		this.listTextBox.splice(index, 1);
		for(var i in this.listTextBox){
			if(this.listTextBox[i].id==index){
				this.listTextBox[i].selectLabel=false;
				this.listTextBox[i].showBar=false;
				this.listTextBox.splice(i, 1);
				if(this.listTextBox[i])
				this.height-=this.listTextBox[i].getHeight();
			}
		}
		

	},
	getTextBox(index){
		return this.listTextBox[index];
	},
	
	setTextBox(id,text){
		for(var i in this.listTextBox){
			if(this.listTextBox[i].id==id)
				this.listTextBox[i].setText(text);
		}
	},
	
	draw:function(ctx) {
		ctx.save();
		var h=0;
		for(var i in this.listTextBox){
			this.listTextBox[i].updatePosition(this.x+3, this.y+h+3, this.width);
			h+=this.listTextBox[i].getHeight();
			this.listTextBox[i].alpha=this.alpha;
			this.listTextBox[i].draw(ctx);
	
		}
		
		
	},
	
	getHeight(){
				
		//return this.height;
		this.height=0;
		for(var i in this.listTextBox){
			this.height +=this.listTextBox[i].getHeight()+2;
		}
		if(this.height<20)
			return 20;

		return this.height;
	},

	updatePosition(x,y,width){
		this.x=x;
//		alert(x+ " container");
		if(width!=undefined)
			this.x+=width/2;
		
		this.y=y+5;
	},
	
	
	
	insideListTextBox(x,y){
		for(var i in this.listTextBox)
			if(this.listTextBox[i].contains(x,y)){
				this.listTextBox[i].selectLabel=true;
				this.listTextBox[i].showBar=true;
				return this.listTextBox[i];
			}
	},
	
	getWidth : function(ctx) {
		var max=0;
		var label=undefined;
		for(var i in this.listTextBox){
			//var width=ctx.measureText(this.listTextBox[i].label).width;
			var width =this.listTextBox[i].getMaxWidthLine(ctx);
			this.listTextBox[i].width=width;
			 if(width>max){
				 max=width;
				 label=this.listTextBox[i];
			 }
		}
		return label;
	},
	
	reset(){
		for(var i in this.listTextBox){
			this.removeTextBox(this.listTextBox[i].id)
		}
	},

	isEmpty(){
		return this.listTextBox.length==0;
	},
	
	
	setConfig(config){
		this.alignment = config.alignment;
		this.underlined = config.underlined;
		this.lineDash = config.lineDash;
		this.addTextBoxes = config.addTextBoxes;
		this.disabled = config.disabled;
		
	},
	isDisable(){
		if(this.disabled) 
			return true;
		return false;
	},
	
	numberTextBox(){
		if(this.listTextBox.length==0)
			return 1;
		return this.listTextBox.length;
	},
}


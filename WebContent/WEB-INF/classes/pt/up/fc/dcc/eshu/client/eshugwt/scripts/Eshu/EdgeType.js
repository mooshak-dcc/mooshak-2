
function EdgeType(args){
	this.typeElement="EDGE";
	this.type=args.type;
	this.iconTollbarSVGPath=args.style.iconTollbarSVGPath;
	//this.imgSVGPath=args.imgSVGPath;
	this.labelConf=args.labelConf;
	this.lineDuple=args.lineDuple;
	this.lineDash=args.lineDash;
	this.variant=args.variant;
	this.isconfigurable=args.isconfigurable;
	this.infoUrl=args.infoUrl;
	this.properties=args.propertiesView;
	this.features=args.features;
	this.cardinality=args.cardinality;
	this.imageIconTollBarSVG = new Image();
	this.headSource=args.style.headSource; 
	this.headTarget=args.style.headTarget;
	this.text=args.text;
	this.arrow=new Image();
	this.stereotype=args.style.stereotype;

	this.stereotypeMargemHeight=args.stereotypeMargemHeight;
} 

EdgeType.prototype = {

	getType : function() {
		return this.type;
	},

	getIconTollbarSVGPath : function() {
		return this.iconTollbarSVGPath;
	},
	
	getImageIconTollBarSVG : function (){
    	return this.imageIconTollBarSVG;
	  },

//	getImgSVGPath : function() {
//		return this.imgSVGPath;
//	},
//	
	getLabelPosition : function() {
		return this.labelPosition;
	},
	
	getVariant : function() {
		return this.variant;
	},
	
	getImageIconTollBarSVG : function (){
	    	return this.imageIconTollBarSVG;
		},
	setImageIconTollBarSVG : function (image){
		    	 this.imageIconTollBarSVG=image;
		},
	
}

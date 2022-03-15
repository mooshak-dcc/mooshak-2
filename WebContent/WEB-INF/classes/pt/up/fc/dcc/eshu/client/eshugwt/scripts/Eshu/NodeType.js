
/**
 * 
 * Stores the general node type information configured in the configuration file
 */
function NodeType(args){
	
	
	this.type=args.type;
	this.iconTollbarSVGPath=args.style.iconTollbarSVGPath;
	this.imgSVGPath=args.style.imgSVGPath;

	this.labelConf=args.labelConf;
	this.variant=args.variant;
	this.listAnchors=args.listAnchors;
	this.listHandlers=args.listHandlers;
	this.isConfigurable=convertStringBoolen(args.isConfigurable);
	this.infoUrl=args.infoUrl;
	this.properties=args.propertiesView;
	this.propertiesImpExp=args.propertiesImpExp;
	this.features=args.features;
	this.connects=args.connects;
    this.typeElement="NODE";
    this.degreeIn=args.degreeIn;
    this.degreeOut=args.degreeOut;
    this.style=args.style;
    this.containers=args.containers;
    this.imageIconTollBarSVG = new Image();
	this.stereotype=args.stereotype;
	this.includeElement=args.includeElement;
	this.imageSVG = new Image();
	this.height=toInteger(args.style.height);
	this.width=toInteger(args.style.width);
	this.autoresize=args.style.autoresize;
	this.stereotype=args.stereotype;
	this.allAnchorConnected=args.nodeValidation.allAnchorConnected;
	this.rotation=args.rotation;
	this.roundCorner =args.roundCorner;
	this.anchorFixed=args.anchorFixed;
	this.multiElement=args.style.multiElement;
} 

NodeType.prototype = {

	getType : function() {
		return this.type;
	},

	getIconTollbarSVGPath : function() {
		return this.iconTollbarSVGPath;
	},

	getImgSVGPath : function() {
		return this.imgSVGPath;
	},
	
	getLabelPosition : function() {
		return this.labelPosition;
	},
	
	getVariant : function() {
		return this.variant;
	},

	getNodesEdges : function() {
		var aux = new Array();
		aux = this.nodes;
		
		for (var i = 0; i < this.edges.length; i++) {
			
			aux.push(this.edges[i]);
		}
		return aux;
	},
	 isNodeConnect:function (nodeType,edgeType){
	       // if(elementType.type=="all") return true;
		
	        for (var i in this.connects){
	        	if(this.connects[i].with_== edgeType.type &&
	        			this.connects[i].to == nodeType.type )
	                return true;
	        }
	        return false;
	    },
	     
	  isEdgeConnect:function (elementType){
		  
	        for (var i in this.connects)
	            if(this.connects[i].with_== elementType.type)
	                return true;
	        return false;
	    },
	    
	  getImageIconTollBarSVG : function (){
    	return this.imageIconTollBarSVG;
	  },
	   
	  getImageSVG : function (){
		  return this.imageSVG;
	  },
	  
	  setImageIconTollBarSVG : function (image){
	    	 this.imageIconTollBarSVG=image;
	  },
		  
	  setImageSVG : function (image){
		  this.imageSVG=image;
			  
	    },
	    
	  isAnchorFixed : function(){
	    	if(this.anchorFixed=='true')
	    		return true;
	    	return false;
	    },
	    
	  
//	    loadImage : function() {
//			alert(23);
//			var b64Start = 'data:image/svg+xml;base64,';
//
//			this.imageIconTollBarSVG = new Image();
//			this.imageIconTollBarSVG.src = this.pathIconTollBarSVG ;
//			
//			this.imageSVG = new Image();
//			this.imageSVG.src = this.pathimgSVG;
//			
//			// alert(b64Start + this.pathimgSVG);
//	    }	
		
}

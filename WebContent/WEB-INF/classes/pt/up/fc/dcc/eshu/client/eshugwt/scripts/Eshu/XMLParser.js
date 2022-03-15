/**
* Define the parsefor the configuration file. 
* @method  XMLParser
* @param {string} xmlFile - eshu configuration file xml
* @return null;
*/
function XMLParser(xmlFile){
	this.xmlDoc = new DOMParser().parseFromString(xmlFile,"text/xml");
} 

XMLParser.prototype = {
		
	/**
	 * Get the root element defined in configuration file xml
	 * @return {Object}
	 */
	getEshu : function() {
		return this.xmlDoc.getElementsByTagName("eshu");
	},
	
	/**
	 * Get the id_div element defined in configuration file xml
	 * @return {string}
	 */
	getIdDiv : function() {
		return this.xmlDoc.getElementsByTagName("id_div")[0].childNodes[0].nodeValue ;
	},

	/**
	 * Get the configuration/style for Editor defined in configuration file xml
	 * @return {Object}
	 */	
	getEditorStyle : function() {
		var editorStyle=this.xmlDoc.getElementsByTagName("editorStyle")[0];
		return{
				width:Number(editorStyle.getAttribute("width")),
				height:Number(editorStyle.getAttribute("height")),
				border_width:editorStyle.getAttribute("borderWidth"),
				border_color:editorStyle.getAttribute("borderColor"),
				background:editorStyle.getAttribute("background"),
				grid_visible:convertStringBoolen(editorStyle.getAttribute("gridVisible")),		
				grid_line_color:editorStyle.getAttribute("gridColorLine"),
		}	 
	},
	
	/**
	 * Get the configuration/style for Toolbar defined in configuration file xml
	 * @return {Object}
	 */	
	getToolbarStyle : function() {
		var toolbarStyle=this.xmlDoc.getElementsByTagName("toolbarStyle")[0];
		return{
				width:toolbarStyle.getAttribute("width"),
				height:toolbarStyle.getAttribute("height"),
				border_width:toolbarStyle.getAttribute("borderWidth"),
				border_color:toolbarStyle.getAttribute("borderColor"),
				background:toolbarStyle.getAttribute("background"),
				position: toolbarStyle.getAttribute("position"),		
		}
		 
	},
	
	getWindowsPropertiesStyle : function() {
		var windowsProperties =this.xmlDoc.getElementsByTagName("windowsProperties")[0];
		return{
			disabled:windowsProperties.getAttribute("disabled"),
			startVisible:windowsProperties.getAttribute("startVisible"),	
		}
		 
	},
	
	
//	/**
//	 * Get the configuration/style for Vertice defined in configuration file xml
//	 * @return {Object}
//	 */	
//	getVerticeStyle : function() {
//		var vertice =this.xmlDoc.getElementsByTagName("vertice")[0]
//		return{
//			width:vertice.getAttribute("width"),
//			height:vertice.getAttribute("height"),
//			border_width:vertice.getAttribute("borderWidth"),
//			border_color:vertice.getAttribute("borderColor"),
//			background:vertice.getAttribute("background"),
//			background_selected:vertice.getAttribute("backgroundSelected"),
//		}	
//	},
//	
//	/**
//	 * Get the configuration/style for TextBox defined in configuration file xml
//	 * @return {Object}
//	 */	
//	getTextBoxStyle : function() {
//		var textBox=this.xmlDoc.getElementsByTagName("text_box")[0];
//		return{
//				text_font: textBox.getAttribute("font"),
//				text_color:textBox.getAttribute("color"),
//				text_color_selected:textBox.getAttribute("colorSelected"),
//				text_align:textBox.getAttribute("align"),
//				text_spacing:textBox.getAttribute("spacing"),
//				background_selected:textBox.getAttribute("backgroundSelected"),
//		}	
//	},
	
	/*
	 * Get the configuration/style for TextBox defined in configuration file xml
	 * @return {Object}
	 */	
	getValidationConfig : function() {
		var rules=this.xmlDoc.getElementsByTagName("syntaxeValidation")[0];
		return{
			sysntaxeValidationIn: rules.getAttribute("sysntaxeValidationIn"),
			numberMaxComponent:rules.getAttribute("numberMaxComponent"),
			numberMinComponent:rules.getAttribute("numberMinComponent"),
			componentsValidation:rules.getAttribute("componentsValidation"),
			edgesValidation:rules.getAttribute("edgesValidation"),
			nodesValidation:rules.getAttribute("nodesValidation"),
			reducibles: this.getReducibles(rules.getElementsByTagName("reducibles")[0].childNodes)
		}	
	},
	
	getReducibles : function(rules){
//		var result="";
//		for(var i in rules ){
//			
//			if(rules[i].nodeType==1){
//				if(result=="")
//					result+=rules[i].childNodes[0].nodeValue;
//				else
//					result+=";"+rules[i].childNodes[0].nodeValue;
//			}
//		}
//		return result;
		
		var result=[];
		for(var i in rules ){
			
			if(rules[i].nodeType==1){
				result.push(rules[i].childNodes[0].nodeValue);
			}
		}
		return result;
		
		
	},
	
	
	/**
	 * Get the nodes types configuration of a diagram in configuration file xml
	 * @return {Object}
	 */	
	getNodeTypes :function() {
		var nodesTypes = this.xmlDoc.getElementsByTagName("nodeTypes");
		var listNodeTypes=[];
		for(var i=0;i<nodesTypes.length;i++){
			listNodeTypes.push(this.getNodeTypeConfig(nodesTypes[i],i));
		}
//		var nodesConfigs = this.xmlDoc.getElementsByTagName("nodeConfig");
//		//var nameD=this.xmlDoc.getElementsByTagName("diagram")[k].getElementsByTagName("name")[0].childNodes[0].nodeValue;
//		console.log(3434);
//		var nodeTypes=[], nodeConfig, config=[];
//		for(var i=0;i<nodesConfigs.length;i++){
//		config.push(this.getNodeTypeConfig(nodesConfigs[i],i));
		return listNodeTypes;
	},
	
	/**
	 * Get the configuration of a node in diagram configuration file xml
	 * @return {Object}
	 */	
	getNodeTypeConfig:function(nodeConfig,index){
		
		var propertiesView =this.xmlDoc.getElementsByTagName("propertiesNode")[index].childNodes;
		var infourl =this.xmlDoc.getElementsByTagName("infoUrlNode")[index].childNodes;
		var feature =this.xmlDoc.getElementsByTagName("featuresNode")[index].childNodes;
		//var anchors=this.getAnchors(nodeConfig.getElementsByTagName("anchors")[0].childNodes);
		var handlers=this.getHandlers(nodeConfig.getElementsByTagName("handlers")[0].childNodes);
		var nodeStyles=nodeConfig.getElementsByTagName("nodeStyle")[0];
		var validation=nodeConfig.getElementsByTagName("nodeValidation")[0];
		var connects= validation.getElementsByTagName("connects")[0].childNodes; 
		var anchors=nodeConfig.getElementsByTagName("anchors")[0].childNodes;
		var containers=nodeConfig.getElementsByTagName("containers")[0];
		return{
			type : nodeConfig.getElementsByTagName("type")[0].childNodes[0].nodeValue,
//			iconTollbarSVGPath : nodeConfig.getElementsByTagName("iconTollbarSVGPath")[0].childNodes[0].nodeValue,
//			imgSVGPath : nodeConfig.getElementsByTagName("imgSVGPath")[0].childNodes[0].nodeValue,
			iconTollbarSVGPath : nodeStyles.getAttribute("iconTollbarSVGPath"),
			imgSVGPath : nodeStyles.getAttribute("imgSVGPath"),
			labelConf : this.getLabelConf(nodeConfig.getElementsByTagName("label")[0],index),
			variant : nodeConfig.getElementsByTagName("variant")[0].childNodes[0].nodeValue,
			isConfigurable : convertStringBoolen(nodeConfig.getElementsByTagName("isConfigurable")[0].childNodes[0].nodeValue),
			rotation: convertStringBoolen(nodeConfig.getElementsByTagName("rotation")[0].childNodes[0].nodeValue),
			listAnchors : this.getAnchors(anchors),
			listHandlers : handlers,
			anchorFixed : nodeConfig.getElementsByTagName("anchors")[0].getAttribute("fixed"),
			infoUrl:this.getInfoUrl(infourl),
			propertiesView:this.getPropertiesView(propertiesView),
			features:this.getFeatures(feature),
			autoresize : nodeStyles.getAttribute("autoresize"),
			height : nodeStyles.getAttribute("height"),
			width : nodeStyles.getAttribute("width"),
			stereotype : this.getStereotypeConfig(nodeConfig.getElementsByTagName("stereotype")),//nodeStyles.getAttribute("stereotype"),
			connects:this.getElementConnected(connects),
			degreeIn:validation.getAttribute("degreeIn"),
			degreeOut:validation.getAttribute("degreeOut"),
			roundCorner: this.getCornerRounded(nodeStyles),
			multiElement: this.getMultiElement(nodeStyles),
			style: this.getNodeStyle(nodeConfig.getElementsByTagName("nodeStyle")[0]),
			nodeValidation: this.getNodeValidation(validation),
			allAnchorConnected: validation.getAttribute("allAnchorsConnect"),
			includeElement : convertStringBoolen(nodeConfig.getElementsByTagName("includeElement")[0].childNodes[0].nodeValue),
			containers:this.getContainer(containers),
			
	}	
		
	},
	
	getStereotype:function(stereotype){
		if (stereotype == undefined)
			return "";
		else
			return stereotype.childNodes[0].nodeValue;
	},
	
	getCornerRounded:function(validation){
		if (!validation.getAttribute("roundCorner"))
			return 0;
		else
			return parseInt(validation.getAttribute("roundCorner"));
	},
	
	getMultiElement:function(validation){
		if (!validation.getAttribute("multiElement"))
			return false;
		else
			return true;
	},
	
	
	
	
	getElementConnected : function (elementConnect){
        
        var connects=[];
       for(var i in elementConnect )
           if(elementConnect[i].nodeType==1){
        	   connects.push({
        		   max:elementConnect[i].getAttribute("max"),
        		   min:elementConnect[i].getAttribute("min"),
        		   to:elementConnect[i].getAttribute("to"),
        		   with_:elementConnect[i].getAttribute("with"),
        	   });
           
            }
        return connects;
         
    },
	
    
    
	getHeadConfig : function (headConfig){
      return {
    	  
	  		  dash:headConfig.getAttribute("dash"),
    		  fill:headConfig.getAttribute("fill"),
    		  type:headConfig.getAttribute("type"),
        	   };
    },
    

	getStereotypeConfig : function (stereotypeConfig){
	
		var stereotype;
		if(!(stereotype = stereotypeConfig[0])){
			return undefined;
		}
      return {
    	  
	  		  name:stereotype.getAttribute("name"),
	  		  marginHeight:stereotype.getAttribute("marginHeight"),
	  		  marginWidth:stereotype.getAttribute("marginWidth"),
	  		  position:stereotype.getAttribute("position"),
        };
    },
    
    
    
    
    getTextConfig : function (textConfig){
        
        return {
        	isText:textConfig.getAttribute("isText"),
        	text:textConfig.getAttribute("value"),
          	   };
      },
      
      getNodeValidation : function (nodeValidation){
    	  
    	  return { 
	    	  notAvailable: convertStringBoolen(nodeValidation.getAttribute("notAvailable")),
	    	  degreeIn: nodeValidation.getAttribute("degreeIn"),
	    	  degreeOut: nodeValidation.getAttribute("degreeOut"), 
	    	  allAnchorsConnect: nodeValidation.getAttribute("allAnchorsConnect"),
	    	  connects: nodeValidation.getElementsByTagName("connects")[0].childNodes,
    	  }
      },
      
      getNodeStyle : function (nodeStyle){
    	  
//    	  console.log(nodeStyle)
    	  return {
    		  autoresize: convertStringBoolen(nodeStyle.getAttribute("autoresize")),
    		  multiElement: nodeStyle.getAttribute("multiElement"),
    		  width: nodeStyle.getAttribute("width"),
    		  height: nodeStyle.getAttribute("height"),
    		  iconTollbarSVGPath: nodeStyle.getAttribute("iconTollbarSVGPath"),
    		  imgSVGPath: nodeStyle.getAttribute("imgSVGPath"),
    		  roundCorner: nodeStyle.getAttribute("roundCorner")
    	  };
      },
    
      
      

      getEdgeStyle : function (edgeStyle){
    	  
//    	  console.log(nodeStyle)
    	  
    	  return {
    		  headSource: edgeStyle.getElementsByTagName("headSource")[0],
    		  headTarget: edgeStyle.getElementsByTagName("headTarget")[0],
//    		  stereotype: edgeStyle.getElementsByTagName("stereotype")[0],
    		  stereotype: this.getStereotypeConfig(edgeStyle.getElementsByTagName("stereotype")),
    		  iconTollbarSVGPath: edgeStyle.getAttribute("iconTollbarSVGPath"),
    		  lineDuple: edgeStyle.getAttribute("lineDuple"),
    		  lineDash: edgeStyle.getAttribute("lineDash"),
    	  };
      },
    
	/**
	 * Get the configuration of properties view of a determined element 
	 * @return {Object}
	 */
	getPropertiesView:function(propertiesView){
		var properties=[];
		for(var i in propertiesView )
			if(propertiesView[i].nodeType==1){
			properties.push({
						name : propertiesView[i].getAttribute("name"),
						typeShow : propertiesView[i].getAttribute("typeShow"),
						type : propertiesView[i].getAttribute("type"),
						disabled : propertiesView[i].getAttribute("disabled"),
						view: propertiesView[i].getAttribute("view"),
						impExp: propertiesView[i].getAttribute("impExp"),
				});
			}
		return properties;
		
	},
	
	
	/**
	 * Get the configuration of properties to Import and Export graph 
	 * @return {Object}
	 */
	getpropertiesImpExp:function(propertiesIE){
		var properties=[];
		for(var i in propertiesIE )
			if(propertiesIE[i].nodeType==1){
			properties.push({
						name : propertiesIE[i].getAttribute("name"),
						type : propertiesIE[i].getAttribute("type"),
				});
			}
		return properties;
		
		
	},
	
	/**
	 * Get the configuration of InfoUrl of a determined element 
	 * @return {Object}
	 */
	getInfoUrl:function(infoUrl){
		var info=[];
		for(var i in infoUrl )
			if(infoUrl[i].nodeType==1){
				info.push({
						url : infoUrl[i].childNodes[0].nodeValue,
				});
			}
		return info;
		
	},
	
	
	getAnchors :function (anchors){
		var anch=[];
		for(var i in anchors )
			if(anchors[i].nodeType==1){
				anch.push(anchors[i].getAttribute("position")) ;
			}
		return {
				anchor: anch,
//				fixe: anchors.getAttribute("fixe"),
			
			};
	},
	
	getHandlers :function (handlers){
		var hands=[];
		for(var i in handlers )
			if(handlers[i].nodeType==1){
				hands.push(handlers[i].getAttribute("position"));
			}
		return hands;
	},
	
	
	/**
	 * Get the nodes types configuration of a diagram in configuration file xml
	 * @return {Object}
	 */	
	getEdgeTypes :function() {
		var edgesConfigs = this.xmlDoc.getElementsByTagName("edgeTypes")
		
		var listEdgeTypes=[];
		for(var i=0;i<edgesConfigs.length;i++){
			listEdgeTypes.push(this.getEdgeTypeConfig(edgesConfigs[i],i));
		};
		return listEdgeTypes;
		
		
	},
	
	/**
	 * Get the configuration of a node in diagram configuration file xml
	 * @return {Object}
	 */	
	getEdgeTypeConfig:function(edgeConfig,index){
		
		var propertiesView =this.xmlDoc.getElementsByTagName("propertiesEdge")[index].childNodes;
		var infourl =this.xmlDoc.getElementsByTagName("infoUrlEdge")[index].childNodes;
		var feature =this.xmlDoc.getElementsByTagName("featuresEdge")[index].childNodes;
		var edgeStyle =edgeConfig.getElementsByTagName("edgeStyle")[0];
		var stereoMH=0;
		if (edgeStyle.getAttribute("stereotypeMargemHeight")) stereoMH=edgeStyle.getAttribute("stereotypeMargemHeight");
		
	//	alert(edgeConfig.getElementsByTagName("text")[0] + " "+ edgeConfig.getElementsByTagName("type")[0].childNodes[0].nodeValue);
//		console.log(edgeConfig)
//		getStereotypeConfig
		return{
			
			type : edgeConfig.getElementsByTagName("type")[0].childNodes[0].nodeValue,
			iconTollbarSVGPath : edgeStyle.getAttribute("iconTollbarSVGPath"),
			labelConf : this.getLabelConf(edgeConfig.getElementsByTagName("label")[0],index),
			lineDuple : edgeStyle.getAttribute("lineDuple"),  
			cardinality :edgeConfig.getElementsByTagName("cardinality")[0].childNodes[0].nodeValue,
			lineDash : edgeStyle.getAttribute("lineDash"), 
			variant : edgeConfig.getElementsByTagName("variant")[0].childNodes[0].nodeValue,
			infoUrl:this.getInfoUrl(infourl),
			propertiesView:this.getPropertiesView(propertiesView),
			features:this.getFeatures(feature),
			headSource:this.getHeadConfig(edgeStyle.getElementsByTagName("headSource")[0]), //headTarget
			headTarget:this.getHeadConfig(edgeStyle.getElementsByTagName("headTarget")[0]),
//			headTarget:this.getHeadConfig(edgeConfig.getElementsByTagName("headTarget")[0]),
			//stereotype:edgeStyle.getAttribute("stereotype"),
			style : this.getEdgeStyle(edgeStyle),  
//			style.stereotype:this.getStereotypeConfig(edgeConfig.getElementsByTagName("stereotype")),
			//stereotypeMargemHeight: stereoMH,
		};	
		
	},
	
	getEdgeStereotypes(stereotypes){
		if(stereotypes){
			return stereotypes.childNodes[0].nodeValue;
			
		}
			
	},
	
	getFeatures : function (features){
		
		var featuresAux=[], imgP;
		for(var i in features )
			if(features[i].nodeType==1){
				if(features[i].hasOwnProperty("imgPath"))
					 imgP=propertiesView[i].getAttribute("imgPath");
				featuresAux.push({
						name : features[i].getAttribute("name"),
						type : features[i].getAttribute("type"),
						value: features[i].getAttribute("value"),
						view: features[i].getAttribute("view"),
						impExp: features[i].getAttribute("impExp"),
						source: features[i].getAttribute("source"),
						target: features[i].getAttribute("target"),
						nodetype: features[i].getAttribute("nodetype"),
						imgPath: imgP,
							
				});
			}
		return featuresAux;
	},
	
	/**
	 * Get the configuration of the label of a determined element 
	 * @return {Object}
	 */
	getLabelConf:function(labelConf,index){
//		var underlined=this.xmlDoc.getElementsByTagName("underlined")[index];
		return {
					defaultValue : labelConf.getAttribute("defaultValue"),
					position : labelConf.getAttribute("position"),
					latterCase : labelConf.getAttribute("letterCase"),
					firstLatterCase : labelConf.getAttribute("firstLetterCase"),
					underlined:labelConf.getAttribute("value"),
					disabled : convertStringBoolen(labelConf.getAttribute("disabled")),
					alignment: labelConf.getAttribute("alignment"),
					margin: labelConf.getAttribute("margin"),
					pattern: labelConf.getAttribute("pattern"),
					underlined: labelConf.getAttribute("underlined"),
					textColor : labelConf.getAttribute("textColor"),
					textFont : labelConf.getAttribute("textFont"),
					marginHeight : labelConf.getAttribute("marginHeight"),
					marginWidth: labelConf.getAttribute("marginWidth"),
					field:  this.getFilds(labelConf.getElementsByTagName("field")),	
			}
		
		
		
		
	},
	
	
	getContainer:function(container){
		
		var listContainer=[];
		if(!container)return listContainer;
		
		var containers=container.childNodes;
		for(var i in containers){
			if(containers[i].nodeType==1){
			listContainer.push({
				name:containers[i].getAttribute("name"),
				fields:this.getFilds(containers[i].getElementsByTagName("field")),	
				pattern:containers[i].getAttribute("pattern"),
				alignment:containers[i].getAttribute("alignment"),
				underlined:containers[i].getAttribute("underlined"),
				defaultText: containers[i].getAttribute("defaultText"),
				textboxDefaultNumber: containers[i].getAttribute("numberDefaultTextbox"),
				addTextBoxes: convertStringBoolen(containers[i].getAttribute("addTextBoxes")),
				lineDash: convertStringBoolen(containers[i].getAttribute("lineDash")),
				disabled: convertStringBoolen(containers[i].getAttribute("disabled")),
			});
		}
		
		}
		
		return listContainer;
		
	},
	
	getFilds: function(fields){
		var field=[];
		for(var i in fields){
			if(fields[i].nodeType==1)
			field.push({
				name:fields[i].getAttribute("name"),
				value:fields[i].getAttribute("value"),
			});
		}
		return field;
	}
}




function convertDirection (direc){
	var d=direc.toLowerCase();
	switch (d) {
	case "north" :
		return 0 ;
		break;
	case "northEast" :
		return 1 ;
		break;	
	case "east" :
		return 2 ;
		break;		
	case "southEastt" :
		return 3 ;
		break;
	case "south" :
		return 4 ;
		break;
	case "southWest" :
		return 5 ;
		break;
	case "west" :
		return 6 ;
		break;
	case "corthWest" :
		return 7 ;
		break;
	case "center" :
		return 8 ;
		break;	
	default:
		throw new Error("Direction invalid:"+type);
		break;
	}	
	        
	
	
}

function parseToIntDegree(degree){
	if(degree=="unbounded")
		return "unbounded";
	else 
		return	parseInt(degree);
}

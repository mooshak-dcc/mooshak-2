FormatPanel.prototype.div;
FormatPanel.prototype.state;


/**
 * 
 * @param eshu
 * @returns
 */
function FormatPanel(eshu){
	this.eshu=eshu;
	this.graph=eshu.graph;
	this.width=210;
	this.visibly=false
	this.state=0;
	this.divsContainer; 
	this.divsContainerHeight=0;
	this.div;
	this.containerBackground;
	this.containerType;
	this.containerName;
	this.containerCardinality;
	this.containerTotal;
	this.containerSize;
	this.containerPosition;
//	this.containerRotation;
	this.containerOrder;
	this.containerAlignVertically;
	this.containerAlignHorizontally;
	this.containerEqualize;
	this.containerCopyCutPaste;
	this.containerUndoRedo;
	this.containerDelete;
	this.containerClear;
	this.containerInfo;
}


FormatPanel.prototype = {
	
		init(){
			
		},
		refresh(){
		},
		
		getWidth(){
			return this.width;
		},
		setWidth(width){
			this.width=width;
		},
		
		setVisibly(visibly){
			this.visibly=visibly;
		},
		
		getVisibly(){
			return this.visibly;
		},
		show(){
			this.div.style.display='inline';
			
		},
		hide(){
			this.div.style.display='none';
		},
		
		setState(state){
			this.state=state;
		},
		
		getstate(){
			return this.state;
		},
		
		createFormatPanel(){
			
			var idDiv = "formatPanel";
			if (document.contains(document.getElementById(idDiv))) 
		        document.getElementById(idDiv).remove();
		    var div = document.createElement('div');    
		    div.id     = idDiv;
//		    div.style.position = "fixed";
//		    div.style.width = 200+"px";
//		    div.style.height = 100+"px";
//		    div.style.background ='#C0C0C0';
//		    div.style.color =  "#505050";
//		    div.style.border="ridge";
//		    div.style.zIndex = "100";
//		    div.style.opacity="0.9";
		    
		    div.setAttribute("id", "formatPanel");
		    div.style.padding= "5px";
		    div.style.marginTop="8px";
//		    div.style.display= "inline";
		    div.style.float= "left";
		    div.style.border= 1+"px solid";
		    div.style.borderColor="black";
		    div.style.backgroundColor ="#DCDCDC";
		    div.style.width = this.width+"px";
//		    alert(this.eshu.getheightGraphEditor())
		    div.style.height = this.eshu.getheightGraphEditor(); //-5 +'px'; // depois eliminar 200
//		    alert(this.eshu.getheightGraphEditor());
//		    alert("this.eshu.getheightGraphEditor() "+ this.eshu.getheightGraphEditor())
		    div.style.overflow="auto"
		    this.eshu.graphEditor.style.float= "left";
//		    div.style.position = "relative";
		    this.eshu.wrapper.appendChild(div); 
//		    console.log(this.eshu.wrapper.id)
		    div.style.display = 'block';
		    this.div=div;
		    
		    
		    
			
		    var header = document.createElement('div');
		    header.style.border = '1px solid #c0c0c0';
		    header.style.borderWidth = '0px 0px 1px 0px';
//		    header.style.textAlign = 'center';
		  //  header.style.fontWeight = 'bold';
		    header.style.overflow = 'hidden';
		    header.style.display = 'inline';
		    header.style.paddingTop = '8px';
		    header.style.height = '34px' ;
		    header.style.width = this.width +'px';
		    header.style.clear= 'both';
//		    header.style.position= 'relative';
		    
		    var headerText = document.createElement('div');
		    headerText.style.textAlign = 'center';
		    headerText.style.fontWeight = 'bold';
		    headerText.style.overflow = 'hidden';
		    headerText.style.display = (true) ? 'inline' : 'inline-block';
//		    header.style.paddingTop = '8px';
//		    headerText.style.height = '100%';
		    headerText.style.width = '198px';
		    headerText.style.clear= 'both';
		    headerText.innerHTML = "Diagram";
		    headerText.style.display= 'table-cell'; 
		    headerText.style.verticalAlign= 'middle';
		    headerText.style.textAlign= 'center';
		   
//		    headerText.style.float= "left";
		    
//		    var btn = document.createElement("BUTTON");        // Create a <button> element
////		    var t = document.createTextNode("X");
////		    btn.appendChild(t); 
//		    btn.style.width = '10%';
//		    btn.style.cursor='pointer';
////		    btn.style.height = '100%';
//		    btn.style.float= "right";
////		    btn.style.border = '1px solid #c0c0c0';
////		    btn.style.borderWidth = '0px 0px 1px 0px';
//		    btn.addEventListener('click', function(event) {
//				this.setWidth(0);
////				alert(this.width);
//				this.eshu.formatPanelVisibly=false;
//				this.eshu.setPositionHorizontal();
//				this.div.style.display='none';
//				document.getElementById("btnformatpanel").display='block';
//			}.bind(this), false);
		   
//		    var icon = document.createElement("i");
//		    icon.className ="material-icons";
//		    icon.innerHTML='clear';
//		    btn.appendChild(icon);
		    
		    
		    header.appendChild(headerText);
//		    header.appendChild(btn);
			this.div.appendChild(header);
			
			this.createContainerGrid();
			this.createContainerBackground();
			this.createContainerType();
			this.createContainerName();
			this.createContainerCardinality();
			this.createContainerTotal();
			this.createContainerSize();
			this.createContainerPosition();
//			this.createContainerRotation();
			this.createContainerOrder();
			this.createContainerAlignVertically();
			this.createContainerAlignHorizontally();
			this.createContainerEqualize();
			this.createContainerCopyCutPaste();
			this.createContainerUndoRedo();
			this.createContainerDelete();
			this.createContainerClear();
			this.createContainerInfo();
			this.div.style.display='none';
			this.updateMain();
//			
		} ,
	
		updatePosition(){
			this.$input('inputX').update(this.eshu);
			this.$input('inputY').update(this.eshu);
		},
		updateSize(){
			this.$input('inputwidth').update(this.eshu);
			this.$input('inputHeight').update(this.eshu);
		},
		
		updateNode(){
			
			this.$input('inputX').update(this.eshu);
			this.$input('inputY').update(this.eshu);
			this.$input('inputwidth').update(this.eshu);
			this.$input('inputHeight').update(this.eshu);
			this.$input('inputNamee').update(this.eshu);
			this.$input('inputType').update(this.eshu);
			this.createContainerInfo();
			
			
			this.$('ContainerOrder').style.display='block';
			this.$('Containersize').style.display='block';
			this.$('ContainerPosition').style.display='block';
			this.$('ContainerName').style.display='block';
			this.$('ContainerType').style.display='block';
//			this.$('ContainerRotation').style.display='block';
			this.$('ContainerDelete').style.display='block';
			
			this.$('containerGrid').style.display='none';
			this.$('containerBackground').style.display='none';
			this.$('ContainerAlignV').style.display='none';
			this.$('ContainerAlignH').style.display='none';
			this.$('ContainerEqualize').style.display='none';
			this.$('undoRedo').style.display='none';
			this.$('ContainerClear').style.display='none';
			this.$('containerCardinality').style.display='none';
			this.$('containerTotal').style.display='none';
			this.$('ContainerInfo').style.display='block';
			
			this.clearDivsContainer(); 
			this.createContainerMenu();
			
//			 alert(this.eshu.getWidthElement());
		},
		
		
		updateEdge(){
			
			this.$input('inputNamee').update(this.eshu);
			this.$input('inputType').update(this.eshu);
//			this.$input('inputCardinalitySource').update(this.eshu);
			this.$input('inputCardinalityTarget').update(this.eshu);
			this.$input('inputTotal').update(this.eshu);
			this.createContainerInfo();
			
			
			
			this.$('ContainerName').style.display='block';
			this.$('ContainerType').style.display='block';
			this.$('ContainerOrder').style.display='block';
			this.$('ContainerDelete').style.display='block';
			if(this.eshu.graph.showCardinality())
				this.$('containerCardinality').style.display='block';
			if(this.eshu.graph.showTotal())
				this.$('containerTotal').style.display='block';
			this.$('ContainerInfo').style.display='block';
			
			
			this.$('ContainerEdit').style.display='none';
//			this.$('ContainerRotation').style.display='none';
			this.$('Containersize').style.display='none';
			this.$('ContainerPosition').style.display='none';
			this.$('containerGrid').style.display='none';
			this.$('containerBackground').style.display='none';
			this.$('ContainerAlignV').style.display='none';
			this.$('ContainerAlignH').style.display='none';
			this.$('ContainerEqualize').style.display='none';
			this.$('undoRedo').style.display='none';
			this.$('ContainerClear').style.display='none';
			
//			alert(this.$('inputNamee'));
			this.clearDivsContainer(); 
			
//			 alert(this.eshu.getWidthElement());
		},
		
		updateWindowsHeight () {
			if(this.eshu.getHeigth()>0)
				this.div.style.height = this.eshu.getheightGraphEditor(); //-5 +'px'; // depois eliminar 200
		},
		
		
		createContainerMenu(){
			
			var containers =this.eshu.graph.selection.getPropertyContainers();
			this.divsContainerHeight=this.getSizeContainers(containers);
			if(!containers) return;	
			if(this.divsContainer)
				this.divsContainer.parentNode.removeChild(this.divsContainer)
			
			this.divsContainer = this.createContainer('divsContainer',null,null);
//			this.divsContainer = this.createContainer('divsContainer',null,this.getSizeContainers(containers));
			this.div.insertBefore(this.divsContainer,this.$('ContainerName').nextSibling);
//			alert("first "+this.getSizeContainers(containers))
			for(var i in containers){
				this.createContainerContainerTextBox(containers[i]);
			}
			this.divsContainer.style.height=this.divsContainerHeight+'px';
		},
		getSizeContainers(containers){
//			alert(jsoncontainers);
		var sizeHeight=0;
		
			if(containers){
//				sizeHeight=+50;
				for(var i in containers){
					sizeHeight+=containers[i].value.length*20;
				}
			}
		return sizeHeight;
		},
		
		updateMain(){
			
			if(this.$('containerGrid')){
			this.$('containerGrid').style.display='block';
			this.$('containerBackground').style.display='block';
//			this.$('ContainerAlignV').style.display='block';
//			this.$('ContainerAlignH').style.display='block';
//			this.$('ContainerEqualize').style.display='block';
			this.$('undoRedo').style.display='block';
			this.$('ContainerClear').style.display='block';
			this.$('ContainerEdit').style.display='block';
			this.$('ContainerInfo').style.display='block';
			
			this.$('ContainerOrder').style.display='none';
			this.$('Containersize').style.display='none';
			this.$('ContainerPosition').style.display='none';
			this.$('ContainerName').style.display='none';
			this.$('ContainerType').style.display='none';
//			this.$('ContainerRotation').style.display='none';
			this.$('ContainerDelete').style.display='none';
			this.$('ContainerAlignV').style.display='none';
			this.$('ContainerAlignH').style.display='none';
			this.$('ContainerEqualize').style.display='none';
			this.$('containerCardinality').style.display='none';
			this.$('containerTotal').style.display='none';
			
			this.createContainerInfo();
			this.clearDivsContainer();  
			}
		},
		
		updateRectangleSelection(){
			
			this.$('ContainerAlignV').style.display='block';
			this.$('ContainerAlignH').style.display='block';
			this.$('ContainerEqualize').style.display='block';
			this.$('undoRedo').style.display='block';
			this.$('ContainerClear').style.display='block';
			this.$('ContainerDelete').style.display='none';
			
			this.$('containerGrid').style.display='none';
			this.$('containerBackground').style.display='none';
			this.$('ContainerOrder').style.display='none';
			this.$('Containersize').style.display='none';
			this.$('ContainerPosition').style.display='none';
			this.$('ContainerName').style.display='none';
			this.$('ContainerType').style.display='none';
//			this.$('ContainerRotation').style.display='none';
			this.$('containerCardinality').style.display='none';
			this.$('containerTotal').style.display='none';
			this.$('ContainerInfo').style.display='none';
			this.clearDivsContainer(); 
		},
		clearDivsContainer(){
			if(this.divsContainer){
				this.divsContainer.parentNode.removeChild(this.divsContainer)
				this.divsContainer=null;	
			}
		},
		createContainer(id,width,height,withouBorder){
			 var div = document.createElement('div');    
			 div.id = id;
			 
			 (height) ? div.style.height=height+'px': div.style.height = '36px' ;
			 (width) ? div.style.width = width+'px' : div.style.width = '200px';
//			 div.style.display = 'inline';
//			 div.style.marginBottom = '18px';
			 if(!withouBorder) //By default border is true
			 {	div.style.border = '1px solid #c0c0c0';
			 	div.style.borderWidth = '0px 0px 1px 0px';
			 }
			 div.style.margin = '10px 0px 10px 0px';
//			 div.innerHTML = "div 1";
			 div.style.display = 'block';
			 return div;
		},
		
		createContainerGrid(){
			
			
			 var  div=this.createContainer('containerGrid');
			 
			 
			 let optionGrid=this.createOption('Grid', this.eshu.getGridVisible())
			 var ckbGrid=optionGrid.firstChild;
			 
			 ckbGrid.addEventListener('click', function(event) {
				 	
				 this.eshu.setGridVisible(ckbGrid.checked);
				 this.eshu.draw();
				 (ckbGrid.checked) ? this.showGrid(): this.hideGrid();
				 
				 
				}.bind(this), false);
			 
			 div.appendChild(optionGrid);
			 
			 let gridwidth= this.addUnitInput(div, 'gridwidth','number',this.eshu.getGridSpacing(),'width','inline',40);
			 gridwidth.addEventListener('click', function(event) {
					this.eshu.setGridSpacing(gridwidth.value);
					this.eshu.draw();
			 	}.bind(this), false);
			 
//			 blur  -- evento desfocar
			 gridwidth.addEventListener('change', function(event) {
					this.eshu.setGridSpacing(gridwidth.value);
					this.eshu.draw();
			 	}.bind(this), false);
			 
			div.appendChild(gridwidth); 
			 
			let gridcolor = this.addUnitInput(div, 'gridcolor', 'color',this.eshu.getGridLineColor());
			gridcolor.addEventListener('change', function(event) {
				
				this.eshu.setGridLineColor(gridcolor.value);
				this.eshu.draw();
		 	}.bind(this), false);
			
			 div.appendChild(gridcolor);
			 this.div.appendChild(div);
			 
			 if(!ckbGrid.checked)
				 this.hideGrid();
			 this.grid=div;
				
			 
			
		},
		
		hideGrid(){
			if( this.$('gridwidth') ){
			this.$('gridwidth').style.display='none';
			this.$('gridcolor').style.display='none';
			}
		},
		showGrid(){
			if( this.$('gridwidth')){
			 this.$('gridwidth').style.display='inline';
			 this.$('gridcolor').style.display='inline';
			}
		},
		
		createContainerBackground(){
			//console.log(this)
			
			 var  div=this.createContainer('containerBackground');
			 let optionBackground = this.createOption('Background', this.eshu.getBackgroundVisible())
			 var ckb=optionBackground.firstChild;
			 ckb.addEventListener('click', function(event) {
				 	this.eshu.setBackgroundVisible(ckb.checked);
				 	
				 	if(this.eshu.getBackgroundVisible()){
						this.eshu.setBackgroundColor(backgroundColor.value);
					}
				 	
				 	if(!ckb.checked){
				 		this.$('backgroundColor').style.display='none';
				 		this.eshu.setBackgroundColor('000');
				 		this.eshu.resetBackgroundColor();
				 	}
				 	else{
				 		backgroundColor.style.display='inline';
				 	}
				 	this.eshu.setPositionHorizontal(); // verificar aqui, e eliminar
				 	
				}.bind(this), false);
			 
			 div.appendChild( optionBackground);
			 
			 var backgroundColor = this.addUnitInput(this.div, 'backgroundColor', 'color',this.eshu.getBackgroundColor());
			 backgroundColor.addEventListener('change', function(event) {
				if(this.eshu.getBackgroundVisible()){
					this.eshu.setBackgroundColor(backgroundColor.value);
					this.eshu.setPositionHorizontal(); // verificar aqui, e eliminar
//					console.log(this.eshu.getBackgroundColor())
					this.eshu.draw();
				}
		 	}.bind(this), false);
			
			 if(!ckb.checked)
			 		div.style.display='none';
			 
			div.appendChild(backgroundColor);
			
			 this.div.appendChild(div);
			 
			this.containerBackground=div;
			 
		},
		
		createContainerOrder(){
			var  div=this.createContainer('ContainerOrder',200,60);
			 div.appendChild(this.creatLabel ('Order',false, 'block')); 
			 div.appendChild(this.createButton ('btnToFront','flip_to_front',32,60,'inline',function(event) {
				 this.eshu.increaseOrder();
				 }.bind(this), false)); 
			 div.appendChild(this.createButton ('btnToBack','flip_to_back',32,20,'inline',function(event) {
				 this.eshu.decreaseOrder();
			 }.bind(this), false)); 
			 
			 
//			var div2= this.createContainer('div2',null,null,true);
//			 div2.appendChild(this.createButton ('btnToFront2','To Front2',90,10,'inline',function(event) {
//				 this.eshu.increaseOrder();
//				 }.bind(this), false)); 
//			 div2.appendChild(this.createButton ('btnToBack2','To Back2',90,5,'inline',function(event) {
//				 this.eshu.decreaseOrder();
//			 }.bind(this), false)); 
//			 
//			 div.appendChild(div2);
			 
			 this.div.appendChild(div);
			
		},
		
		createContainerSize(){
			var  div=this.createContainer('Containersize',null,60);
			 div.appendChild(this.creatLabel ('Size',false, 'block')); 
			 
			 
			 let inputwidth = this.addUnitInput(div, 'inputwidth','number',10,'width','inline',45);
			 inputwidth.disabled = true;
			 
			 inputwidth.addEventListener('click', function(event) {
					this.eshu.setWidthElement(parseInt(inputwidth.value));
			 	}.bind(this), false);
			 
			 inputwidth.addEventListener('blur', function(event) {
					this.eshu.setWidthElement(parseInt(inputwidth.value));
			 	}.bind(this), false);
			 
			 inputwidth.update =function(eshu) { 
				 inputwidth.value=eshu.getWidthElement();
			 };
			 
			 div.appendChild(inputwidth);
			 
			 
			 // create inputHeigth
			 let inputHeight=this.addUnitInput(div, 'inputHeight','number',10,'height','inline',45);
			 inputHeight.disabled = true;
			 inputHeight.addEventListener('click', function(event) {
					this.eshu.setHeightElement(parseInt(inputHeight.value));
			 	}.bind(this), false);
			 
			 inputHeight.addEventListener('blur', function(event) {
					this.eshu.setHeightElement(parseInt(inputHeight.value));
			 	}.bind(this), false);
			 
			 inputHeight.update =function(eshu) { 
				 inputHeight.value=eshu.getHeightElement();
			 };
			 
			 div.appendChild(inputHeight);
			// div.appendChild(this.addUnitInput(div, 'inpHeight','number',10)); 
			
			 
			 this.div.appendChild(div);
			
		},
		
		
		createContainerPosition(){
			var  div=this.createContainer('ContainerPosition',null,60);
			 div.appendChild(this.creatLabel ('Position',false, 'block')); 
			 let inputX = this.addUnitInput(div, 'inputX','number',10,'x','inline',45);

			 inputX.addEventListener('click', function(event) {
					this.eshu.setXElement(parseInt(inputX.value));
			 	}.bind(this), false);
			 
			 inputX.addEventListener('blur', function(event) {
					this.eshu.setXElement(parseInt(inputX.value));
			 	}.bind(this), false);
			 
			 inputX.update =function(eshu) { 
				 inputX.value=eshu.getXElement();
			 };
			 
			 div.appendChild(inputX);
			 
			 
			
			 let inputY = this.addUnitInput(div, 'inputY','number',10,'y','inline',45);
			 
			 inputY.addEventListener('click', function(event) {
					this.eshu.setYElement(parseInt(inputY.value));
			 	}.bind(this), false);
			 
			 inputY.addEventListener('blur', function(event) {
					this.eshu.setYElement(parseInt(inputY.value));
			 	}.bind(this), false);
			 
			 inputY.update =function(eshu) { 
				 inputY.value=eshu.getYElement();
			 };
			 	
			 div.appendChild(inputY);
			 this.div.appendChild(div);
			
		},
		removeAllDiv(myNode){
			while (myNode.firstChild) {
			    myNode.removeChild(myNode.firstChild);
			}
		},
		
		createContainerContainerTextBox(container){
			
			this.divsContainerHeight+=container.value.length*20+20;
			var div = this.createContainer('ContainerNameContainer'+container.name,null,(40+container.value.length*20));
			
			var  divAux=this.createContainer('ContainerName2',null,null,true);
			var label =this.creatLabel (container.name, false, 'inline-table');
			label.style.width='172px';
			divAux.appendChild( label);
			if(container.addTextBoxes && !container.disabled){
				var btnadd = this.createButton ('btn'+container.name,'add',0, 5,'inline-table', function() {});
				btnadd.index=container.index;
				
				var addTextBox = function(index){
					this.eshu.addTextBoxContainer(index);
					this.createContainerMenu();
				}.bind(this);
				
				btnadd.addEventListener('click', function( event) {
					addTextBox(this.index);
				});
				divAux.appendChild(btnadd);
			}
			divAux.style.margin ='0px';
			divAux.style.display='inline'
			div.appendChild(divAux);
			
			
			for (var i in container.value){
				var  div2=this.createContainer('ContainerName2',null,null, true);
				 let inputName2 = this.addUnitInput(div, 'inputName2','text',container.value[i].text,'','inline',160);
				 inputName2.index=container.index;
				 inputName2.idContainer=container.value[i].id;
				 var functionInputName = function(text,idContainer, idTextBox){
					this.graph.setNameContainer(text,idContainer, idTextBox, this.eshu.ctx);
				}.bind(this);
					
					
				inputName2.addEventListener('blur', function(event) {
					 functionInputName(this.value,this.index,this.idContainer);
			 	});
				 
				 inputName2.disabled = container.disabled;
				 
				div2.appendChild(inputName2);
				if(container.addTextBoxes && !container.disabled){
					var butonRemove=this.createButton (container.value[i].id,'remove',0, 5,'inline', function(event) {});
					
					butonRemove.index=container.index;
					butonRemove.idValue=container.value[i].id;
					var someVar = function(index, idValue){
						this.eshu.removeTextBoxContainer(index, idValue);
						this.createContainerMenu();
					}.bind(this);
					
					butonRemove.addEventListener('click', function( event) {
						someVar(this.index, this.idValue);
					});
						
					div2.appendChild(butonRemove);
				}
				div2.style.display='inline';
				div.appendChild(div2);
//				div.style.margin = '0px 0px 50px 0px';
			}
			 this.divsContainer.appendChild(div);
			 
		},
		
		createContainerName(){
			var  div=this.createContainer('ContainerName',null,50);
			 div.appendChild(this.creatLabel ('Name',false, 'block')); 
			 let inputNamee = this.addUnitInput(div, 'inputNamee','text',10,'','inline',170);

			 inputNamee.addEventListener('click', function(event) {
				 this.eshu.graph.setName(inputNamee.value);
			 }.bind(this), false);
			 
			 inputNamee.addEventListener('blur', function(event) {
				 this.eshu.graph.setName(inputNamee.value,this.eshu.ctx);
				 this.eshu.draw();
			 }.bind(this), false);
			 
			 inputNamee.update =function(eshu) { 
				 inputNamee.value=eshu.getNameNodeSeleted();
			 };
			 
			 div.appendChild(inputNamee);
			 
			 this.div.appendChild(div);
		},
		
		
		createContainerType(){
			var  div=this.createContainer('ContainerType',null,50);
			 div.appendChild(this.creatLabel ('Type',false, 'block')); 
			 let inputType = this.addUnitInput(div, 'inputType','text',10,'','inline',170);
			 inputType.disabled = true;

			 inputType.update =function(eshu) { 
				 inputType.value=eshu.graph.getTypeSeleted();
			 };
			 
			 div.appendChild(inputType);
			 
			 this.div.appendChild(div);
			 this.containerType=div;
		},
		
		
		createContainerCardinality(){
			
			var  div=this.createContainer('containerCardinality',null,50);
			 div.appendChild(this.creatLabel ('Cardinality',false, 'block')); 
			
			 
//			 let cardinalitySource = this.addUnitInput(div, 'inputCardinalitySource','text',this.graph.getCardinalitySource(),this.graph.getEdgeSourceName(),'inline',40);
//			 cardinalitySource.addEventListener('blur', function(event) {
//				 this.graph.setCardinalitySource(cardinalitySource.value);
//				 this.eshu.draw();
//			 }.bind(this), false);
//			 cardinalitySource.update =function(eshu) { 
//				 cardinalitySource.value=eshu.graph.getCardinalitySource();
//			 };
//			 div.appendChild(cardinalitySource);
			 
			 let cardinalityTarget = this.addUnitInput(div, 'inputCardinalityTarget','text',this.graph.getCardinalityTarget(),this.graph.getEdgeTargetName(),'inline',160);
			 
			 cardinalityTarget.addEventListener('blur', function(event) {
				 this.eshu.graph.setCardinalityTarget(cardinalityTarget.value);
				 this.eshu.draw();
			 }.bind(this), false);
			 
			 cardinalityTarget.update =function(eshu) { 
				 cardinalityTarget.value=eshu.graph.getCardinalityTarget();
			 };
			 div.appendChild(cardinalityTarget);
			 
			 this.div.appendChild(div);
		},
		
		
		createContainerTotal(){
			
			var  div=this.createContainer('containerTotal',null,50);
			 div.appendChild(this.creatLabel ('Total',false, 'block')); 
			
			 
			 
			 let total = this.addUnitInput(div, 'inputTotal','button',this.graph.getTotal(),'','inline',60);
			 
			 total.addEventListener('click', function(event) {
				 if(total.value=="no"){
					 total.value="yes";
					 this.eshu.graph.setTotal("yes");
				 }
				 else{
					 total.value="no";
					 this.eshu.graph.setTotal("no");
				 }
				 this.eshu.draw();
			 }.bind(this), false);
			 
			 total.update =function(eshu) { 
				 total.value=eshu.graph.getTotal();
			 };
			 div.appendChild(total);
			 
			 this.div.appendChild(div);
		},
		
		
		
		createContainerRotation(){
			var  div=this.createContainer('ContainerRotation',null,60);
			 div.appendChild(this.creatLabel ('Angle',false, 'inline')); 
			 var angle=this.addUnitInput(div, 'idAngle','number',0,null,'inline',40);
			 angle.style.float= "right";
			 div.appendChild(angle);
			 div.appendChild(this.createButton ('btnRotate','crop_rotate',32,80,null));
			// div.appendChild(this.addUnitInput(div, 'inpHeight','number',10)); 
			
			 this.div.appendChild(div);
			
		},
		
		createContainerAlignVertically(){
			var  div=this.createContainer('ContainerAlignV',null,60);
//			div.style.display = 'block';
//			createButton (id,butText,width,marginLeft,display,callback){
			 div.appendChild(this.creatLabel ('Align Vertically',false, 'block')); 
			 
			 div.appendChild(this.createButton ('btntop','vertical_align_top',32,30,'inline', function(event) {
				 this.eshu.alignVertically('top');
			 }.bind(this), false)); 
			 
			 div.appendChild(this.createButton ('btnmidle','vertical_align_center',32,20,'inline',function(event) {
				 this.eshu.alignVertically('midle');
			 }.bind(this), false)); 
			 
			 div.appendChild(this.createButton ('btnmidle','vertical_align_bottom',32,20,'inline' , function(event) {
				 this.eshu.alignVertically('bottom');
			 }.bind(this), false)); 
			// div.appendChild(this.addUnitInput(div, 'inpHeight','number',10)); 
			
			 
			 this.div.appendChild(div);
			
		},
		
		
		
		createContainerAlignHorizontally(){
			var  div=this.createContainer('ContainerAlignH',null,60);
			
			 div.appendChild(this.creatLabel ('Align Horizontally',false, 'block')); 
//			 div.appendChild(this.createButton ('btnleft','<img src="image/btn_l.png" />',32, 30,'inline', function(event) {
			 div.appendChild(this.createButton ('btnleft','<img src='+leftIcon+ ' />',32, 20,'inline', function(event) {
			// div.appendChild(this.createButton ('btnleft','L',32, 30,'inline', function(event) {
//			 div.appendChild(this.createButton ('btnleft','',32, 30,'inline', function(event) {
				 this.eshu.alignHorizontally('left');
			 }.bind(this), false)); 
			 
//			 div.appendChild(this.createButton ('btnmidle','<img src="image/btn_c.png" />',32, 20, 'inline', function(event) {
			 
//			 div.appendChild(this.createButton ('btnmidle','C',32, 20, 'inline', function(event) {
			 div.appendChild(this.createButton ('btnleft','<img src='+centerIcon+ ' />',32, 20,'inline', function(event) {
				 this.eshu.alignHorizontally('center');
			 }.bind(this), false)); 
			 
//			 div.appendChild(this.createButton ('btnright','<img src="image/btn_r.png" />',32, 20, 'inline', function(event) {
//			 div.appendChild(this.createButton ('btnright','R',32, 20, 'inline', function(event) {
			 div.appendChild(this.createButton ('btnleft','<img src='+rightIcon+ ' />',32, 20,'inline', function(event) {
				 this.eshu.alignHorizontally('right');
			 }.bind(this), false)); 
			
			 
			 this.div.appendChild(div);
			
		},
		 
		
		createContainerEqualize(){
			var  div=this.createContainer('ContainerEqualize',null,60);
			 div.appendChild(this.creatLabel ('Equlize',false, 'block')); 
			 div.appendChild(this.createButton ('btnminimum','view_module',32,60,'inline',function(event) {
				 this.eshu.equalizeMin();
				 
			 }.bind(this), false)); 
			 
			 div.appendChild(this.createButton ('btnmaximum','view_stream',33,20,'inline', function(event) {
				 this.eshu.equalizeMax();
			 }.bind(this), false)); 
			
			 
			 this.div.appendChild(div);
			
		},
		
		
		createContainerCopyCutPaste(){
			var  div=this.createContainer('ContainerEdit',null,60);
			
			 div.appendChild(this.creatLabel ('Edit',false, 'block')); 
			 div.appendChild(this.createButton ('btnleft','content_copy',32, 30,'inline', function(event) {
				 this.eshu.copy();
			 }.bind(this), false)); 
			 
			 div.appendChild(this.createButton ('btnmidle','content_cut',32, 20, 'inline', function(event) {
				 this.eshu.cut();
			 }.bind(this), false)); 
			 
			 div.appendChild(this.createButton ('btnright','content_paste',32, 20, 'inline', function(event) {
				 this.eshu.paste();
			 }.bind(this), false)); 
			
			 
			 this.div.appendChild(div);
			
		},
		
		
//		createContainerCopyCutPaste(){
//			var  div=this.createContainer('ContainerDelete',null,50);
//			
//			 div.appendChild(this.creatLabel ('Delete',false, 'block')); 
//			 div.appendChild(this.createButton ('btnleft','Delete',100, 5,'inline', function(event) {
//				 this.eshu.Delete();
//			 }.bind(this), false)); 
//			 
//			 
//			 this.div.appendChild(div);
//			
//		},
		
		
		createContainerDelete(){
			var  div=this.createContainer('ContainerDelete',null,60);
			
			 div.appendChild(this.creatLabel ('Delete',false, 'block')); 
			 
			 div.appendChild(this.createButton ('btndelete','delete',32, 80,'inline', function(event) {
				 this.eshu.deleteElement();
				 
			 }.bind(this), false)); 
			 
			 
			 this.div.appendChild(div);
			
		},
		
		createContainerClear(){
			var  div=this.createContainer('ContainerClear',null,60);
			 div.appendChild(this.creatLabel ('Clear',false, 'block'));
			 div.appendChild(this.createButton ('btnclear','clear',32, 80,'inline', function(event) {
				 this.eshu.clear();
			 }.bind(this), false)); 
			 
			 this.div.appendChild(div);
		},
		
		
		createContainerUndoRedo(){
			var  div=this.createContainer('undoRedo',null,60);
			
			 div.appendChild(this.creatLabel ('Undo Redo',false, 'block')); 
			 div.appendChild(this.createButton ('btnleft','undo',32, 60,'inline', function(event) {
				 this.eshu.undo();
				 
			 }.bind(this), false)); 
			 
			 
			 div.appendChild(this.createButton ('btnleft','redo',32, 20,'inline', function(event) {
				 this.eshu.redo();
			 }.bind(this), false)); 
			 this.div.appendChild(div);
			
		},
		

		createContainerInfo(){
			
			var element = this.$('ContainerInfo');
			if (element) {
				this.removeAllDiv(element);
				element.remove();


				
			}

			var div_info= this.createContainer('ContainerInfo',null,60);
			
			var links=this.eshu.getUrlElementSeleted();
			if(links){
				div_info.appendChild(this.creatLabel ('Info',false, 'block'));
				for(var i in links){
					var a = document.createElement('a');
					a.href = links[i].url;
					a.target="_blank";
					a.appendChild(this.createButton ('link','info',32, 20,'inline', function(event) {
					}.bind(this), false)); 
					div_info.appendChild(a);
					
				}
			}
			else
			{
				
				div_info.appendChild(this.creatLabel ('Help',false, 'block'));
				var a = document.createElement('a');
				a.href = "https://mooshak2.dcc.fc.up.pt/mooshak/kora-diagram-assessment";
				a.target="_blank";
				a.appendChild(this.createButton ('link','help',32, 80,'inline', function(event) {
				}.bind(this), false)); 
				div_info.appendChild(a);
				
			}
			
//			
//			div.appendChild(this.createButton ('link','info',32, 80,'inline', function(event) {
//				
//				
//			}.bind(this), false)); 
			 this.div.appendChild(div_info);
		},
		
		createOption(name, value, listener) {
			
			var div = document.createElement('div');
			div.style.padding = '6px 0px 1px 0px';
			div.style.whiteSpace = 'nowrap';
			div.style.overflow = 'hidden';
			div.style.width = '100px';
			div.style.height ='27px';
			div.style.margin = '0px 6px 0px 6px';
			div.style.display = 'inline';
			
			var cb = document.createElement('input');
			cb.setAttribute('type', 'checkbox');
			cb.style.margin = '0px 6px 0px 0px';
			cb.checked=value;
			cb.id=name;
			div.appendChild(cb);
			

			var span = document.createElement('span');
			span.innerHTML = name;
			div.appendChild(span);
			
//			this.div.appendChild( div);
			return div;
		},
		
		
		addUnitInput (container, id, type, value,labelName,display,width,listener){ 
			
//			marginTop = (marginTop != null) ? marginTop : 0;
			var div = document.createElement('div');
			
			
			var input = document.createElement('input');
			input.type = type;
			input.id=id;
//			input.style.position = 'absolute';
//			input.style.textAlign = 'right';
			input.style.margin = '0px 4px 0px 4px';
//			input.style.right = (right + 12) + 'px';
			(width)?input.style.width = width + 'px':input.style.width = 30 + 'px';
			
			input.style.display = 'inline';
			div.style.display = 'inline';
//			input.style.float= "left";
			
//			if(type=='number'){
				input.value=value;
//				input.addEventListener('blur', function(event) {
//					this.eshu.setGridSpacing(input.value);
//					this.eshu.draw();
//				}.bind(this), false); 
				
//			}
			
			div.appendChild(input);
			
//			input.addEventListener('click', function(event) {
////				listener(cb.checked);
////				this.eshu.draw();
////				this.eshu.setGridVisible(cb.checked);
////				alert(input.value);
//				this.eshu.setGridSpacing(input.value);
//				this.eshu.draw();
//			}.bind(this), false); 
			
			
			
			
			if(labelName)
				div.appendChild(this.creatLabel (labelName,false, display,13)); 
			
			container.appendChild(div);
			
			
//			labelName,display
			
//			var input = document.createElement("input");
//		
//			container.appendChild(input);
			
			
//			var stepper = this.createStepper(input, update, step, null, disableFocus);
//			stepper.style.marginTop = (marginTop - 2) + 'px';
//			stepper.style.right = right + 'px';
//			container.appendChild(stepper);

			return input;
		}, 
		
		
		createStepper (input, update, step, height, disableFocus, defaultValue){
//			step = (step != null) ? step : 1;
//			height = (height != null) ? height : 8;
//			
//			if (mxClient.IS_QUIRKS)
//			{
//				height = height - 2;
//			}
//			else if (mxClient.IS_MT || document.documentMode >= 8)
//			{
//				height = height + 1;
//			} 
//			
			var stepper = document.createElement('div');
//			mxUtils.setPrefixedStyle(stepper.style, 'borderRadius', '3px');
			stepper.style.border = '1px solid rgb(192, 192, 192)';
			stepper.style.position = 'absolute';
			
			var up = document.createElement('div');
			up.style.borderBottom = '1px solid rgb(192, 192, 192)';
			up.style.position = 'relative';
			up.style.height = height + 'px';
			up.style.width = '10px';
			up.className = 'geBtnUp';
			stepper.appendChild(up);
			
			var down = up.cloneNode(false);
			down.style.border = 'none';
			down.style.height = height + 'px';
			down.className = 'geBtnDown';
			stepper.appendChild(down);

//			mxEvent.addListener(down, 'click', function(evt)
//			{
//				if (input.value == '')
//				{
//					input.value = defaultValue || '2';
//				}
//				
//				var val = parseInt(input.value);
//				
//				if (!isNaN(val))
//				{
//					input.value = val - step;
//					
//					if (update != null)
//					{
//						update(evt);
//					}
//				}
//				
//				mxEvent.consume(evt);
//			});
//			
//			mxEvent.addListener(up, 'click', function(evt)
//			{
//				if (input.value == '')
//				{
//					input.value = defaultValue || '0';
//				}
//				
//				var val = parseInt(input.value);
//				
//				if (!isNaN(val))
//				{
//					input.value = val + step;
//					
//					if (update != null)
//					{
//						update(evt);
//					}
//				}
//				
//				mxEvent.consume(evt);
//			});
//		
			
			return stepper;
		},
		
		
		createButton (id,butText,width,marginLeft,display,callback){
		    var td2 = document.createElement('td');
		    var butKey =document.createElement("BUTTON");
		    butKey.id=id;
		    butKey.value=butText;
//		    if(disabled)butKey.disabled = true;
		   // var butText = document.createTextNode(butText); 
		    butKey.style.width=width+"px";
		    butKey.style.height='32px';
		    butKey.style.padding='0px';
		    butKey.style.cursor='pointer';
//		    butKey.appendChild(butText);
		    butKey.addEventListener('click', function(event) {
		    	callback();
//				this.changeValueButton2(butKey.id);}.bind(this), false);
		    }.bind(this), false);
		    butKey.style.marginLeft=marginLeft+'px';
		    butKey.style.display = 'block';
		    
		    var icon = document.createElement("i");
		    icon.className ="material-icons";
		    icon.innerHTML=butText;
		    butKey.appendChild(icon);
		    
		    
		    td2.appendChild(butKey);
		    
		   return td2;
		},
		
		
		createButtonImg (id,img,width,marginLeft,callback){
		    var td2 = document.createElement('td');
		    var butKey =document.createElement("BUTTON");
		    butKey.id=id;
		    butKey.style.width=width+"px";
		    butKey.style.height='32px';
		    butKey.style.padding='0px';
		    butKey.style.cursor='pointer';
		    
		    butKey.addEventListener('click', function(event) {
		    	callback();
		    }.bind(this), false);
		    butKey.style.marginLeft=marginLeft+'px';
		    butKey.style.display = 'block';
		    butKey.style.display.background= "url("+img+") no-repeat 3px center";
		    butKey.innerHTML = '<img src="image/btn_l.png" />';
		    td2.appendChild(butKey);
		    
		   return td2;
		},
		
		
		
		creatLabel(labelText,center,display,fontSize){
//			var label = document.createElement('Label');
//			var labelText =document.createTextNode(labelText);
//			label.appendChild(labelText);
//			label.style.background="#A0A0A0";
//			label.style.width=30+"px";
//			label.style.height='20px';
//			label.style.display = display;
////			label.style.clear = 'left';
//			(fontSize)?label.style.fontSize=fontSize+'px':label.style.fontSize=16;
//			if(center){
//				label.setAttribute("align", "center");
//				label.setAttribute("colspan", "3"); 
//			}
			
			var label = document.createElement('div');
//			var labelText =document.createTextNode(labelText);
			label.innerHTML = labelText;
//			label.style.background="#A0A0A0";
//			label.style.width=30+"px";
			label.style.height='20px';
			label.style.display = display;
//			label.style.clear = 'left';
			(fontSize)?label.style.fontSize=fontSize+'px':label.style.fontSize=16;
			if(center){
				label.setAttribute("align", "center");
				label.setAttribute("colspan", "3"); 
			}
			
			return label;

		},
		
		$(id){
			 var childDivs =this.div.getElementsByTagName('div');
					 for( i=0; i< childDivs.length; i++ )
				{
					var childDiv = childDivs[i];
					if(childDiv.id==id)
						return  childDiv
				}
			
			return null;		 
			//return document.getElementById(id);
		},
		
		
		$input(id){
			 var childDivs =this.div.getElementsByTagName('input');
					 for( i=0; i< childDivs.length; i++ )
				{
					var childDiv = childDivs[i];
					if(childDiv.id==id)
						return  childDiv
				}
			
			return null;		 
			//return document.getElementById(id);
		},
		
		
}


//function $(id){
//	console.log(123456)
//	
//	 var childDivs =this.div.getElementsByTagName('div');
//			 for( i=0; i< childDivs.length; i++ )
//		{
//			var childDiv = childDivs[i];
//				 //alert(childDiv.id)
//			if(childDiv.id==id)
//				return  childDiv
//		}
//	
//	return null;		 
//	//return document.getElementById(id);
//}
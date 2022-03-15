var menuPropDiv;
Eshu.prototype.valueNodeSelected=new Array();
var table;
var menu ;

Eshu.prototype.contextMenu = function(e) {
//	if(this.graphState == 8) return;
//     cursorX = e.pageX;
//     cursorY = e.pageY;
//     if(this.graph.selection!=undefined ){
//    	 menuPropDiv=this.createDiv("mygraph","menuProperties",1,1);
//		this.showContextMenu(cursorX , cursorY+this.graph.selection.height/2, 2)
////		this.showProperties();
//		this.contextMenuProperties();
//		this.graphState = 8;
//	 }
//    
//     if(this.graphState == 7 || this.graphState == 3  ){
//    	 menuPropDiv=this.createDiv("mygraph","menuProperties",1,1);
//    	 this.showContextMenu(cursorX , cursorY  +this.graph.rectangleSelection.height/2, 2);
//    	 this.contextMenuRectangleSelected();
//    	 this.graphState = 8;
//     }
//     
//     
//    	    event.returnValue = false;
//	 event.preventDefault();
	this.showHideFormatPanel();
}


Eshu.prototype.showProperties= function(){
	
//	this.contextMenuProperties();
////	menuPropDiv.style.display = "block";
//
//	var propHeight=document.getElementById("menuProperties").offsetHeight;
//	var graphHeight=this.div.offsetHeight;
//	
//	var  graphWidth=this.div.offsetWidth;
//	var propWidth=document.getElementById("menuProperties").offsetWidth;
//	var div=this.div;
//	var divsOffSetheight=0;
//	var divsOffSetwidth=0;
//	while(div!=null){
//		if(divsOffSetheight<div.offsetTop)
//			divsOffSetheight=div.offsetTop;
//		if(divsOffSetwidth<div.offsetLeft)
//			divsOffSetwidth=div.offsetLeft;
//		div=div.parentNode;
//		
//	}
//	this.showContextMenu(graphWidth,propHeight);
//	if(propHeight+this.graph.selection.height>graphHeight+divsOffSetheight){
//		menuPropDiv.style.top= (graphHeight+divsOffSetheight)-propHeight+-this.graph.selection.height+"px"; 
//		alert("IN")
//	}
//	if(propWidth+this.graph.selection.width> graphWidth+divsOffSetwidth)
//		menuPropDiv.style.left = ( graphWidth+divsOffSetwidth)- propWidth-this.graph.selection.width/2+"px";
}


Eshu.prototype.setNodeSelectedValue= function(){
	var elements=this.valueNodeSelected;
	
	for(var i in elements){
		var value=document.getElementById(elements[i].id).value;
		this.graph.setPropertyElement(elements[i].id,value,elements[i].type);
	}
	this.graph.adjustLabelSelection(this.ctx);
	//this.draw();
}
Eshu.prototype.createFieldType= function(property){
	
	var variant=this.graph.selection.variant;
	var type=this.graph.selection.type;
	var list=[];
	list.push(type);
	for(var i=0; i< this.elementsTypes.size;i++){
		var key=this.elementsTypes.current.key
		
		if(this.elementsTypes.get(key).variant==variant &&this.elementsTypes.get(key).type!=type)
			list.push(key );
		this.elementsTypes.next();
	}
	if(list.length>1)
		this.trCreateListSelectType(type,list);
	else
		this.createFieldsProperty(property);
}

Eshu.prototype.createFieldsProperty =  function (property){
	
	var value=this.graph.getPropertyElement(property.name);
//	alert(property.name)
	this.valueNodeSelected.push({id:property.name,type:property.type});
	var type= property.typeShow.toLowerCase();
	var disabled = property.disabled.toLowerCase();
	
	var tr = document.createElement('tr'); 
	var name=property.name;
	if(name=="cardinalitySource")
		name="cardinalityTo "+this.graph.selection.source.type + "_Id:"+this.graph.selection.source.id;
	if(name=="cardinalityTarget")
		name="cardinalityTo "+this.graph.selection.target.type + "_Id:"+this.graph.selection.target.id;
	tr.appendChild(this.tdCreatLabel(name));
	var name=property.name;
	if(name=="total"){
		tr.appendChild(this.tdCreateButton(name, this.graph.selection.total,"button",convertStringBoolen(disabled)));
		table.appendChild(tr);
		return;
	}
	if(type=="input"){
		
		tr.appendChild(this.tdCreateInput(name, value, "input", convertStringBoolen(disabled)));
	}
	else if(type=="button")
		tr.appendChild(this.tdCreateButton(name, "false","button",convertStringBoolen(disabled)));
	else
		throw new Error("Invalid property: "+type+". Please choose input or button");
	
	table.appendChild(tr);
}

Eshu.prototype.contextMenuProperties= function(){
	
	table =document.createElement("TABLE");
	table.style.background ='#C0C0C0';
	table.style.color =  "#505050";
	
	var properties =this.graph.getPropertiesViewSelected();
	for(var i in properties){
		if(!convertStringBoolen(properties[i].view) )
			continue;
		if(properties[i].name=="type"){
			this.createFieldType(properties[i]);
			continue;
		}
		this.createFieldsProperty(properties[i]);
			
	}
	menuPropDiv.appendChild(table);
	table.appendChild(this.tdCreateAnchor(this.graph.getInfoUrl()));
	this.createOptionButtons();
	    
}



Eshu.prototype.contextMenuRectangleSelected= function(){
	
	table =document.createElement("TABLE");
	table.style.background ='#C0C0C0';
	table.style.color ="#505050";
	
//	var properties =this.graph.getPropertiesViewSelected();
//	for(var i in properties){
//		if(!convertStringBoolen(properties[i].view) )
//			continue;
//		if(properties[i].name=="type"){
//			this.createFieldType(properties[i]);
//			continue;
//		}
//		this.createFieldsProperty(properties[i]);
//			
//	}
//	menuPropDiv.appendChild(table);
//	table.appendChild(this.tdCreateAnchor(this.graph.getInfoUrl()));
	//this.createOptionButtons();
	var align=document.createElement('tr');
	align.appendChild(this.tdCreatLabel('Alinhar Horizontalmente'));
	align.appendChild(this.tdCreatLabel('Alinhar Verticalmente'));
	table.appendChild(align);
	 
	
	 var right = document.createElement('tr'); 
	 right.appendChild(this.tdCreateButton2("alinharVLeft", "A_Esquerda","Button"));
	 right.appendChild(this.tdCreateButton2("alinharHTop", "Superior","Button"));
	 table.appendChild(right);
	 
	 var center = document.createElement('tr');  
	 center.appendChild(this.tdCreateButton2("alinharVCenter", "Centrar","Button"));
	 center.appendChild(this.tdCreateButton2("alinharHCenter", "Meio","Button"));
	 table.appendChild(center);
			
	 var left = document.createElement('tr');
	 left.appendChild(this.tdCreateButton2("alinharVRigth", "A_Direita","Button"));
	 left.appendChild(this.tdCreateButton2("alinharHBottom", "Inferior","Button"));
	 table.appendChild(left);
		
	 
	 var equalizeText=document.createElement('tr');
	
	 equalizeText.appendChild(this.tdCreatLabel('Equalizar', true));
	 
	 	 table.appendChild(equalizeText);
		 var equalize=document.createElement('tr');
		 	equalize.appendChild(this.tdCreateButton2("equalizeMin", "Equalizar Minimo","Button"));
			equalize.appendChild(this.tdCreateButton2("equalizeMax", "Equalizar Maximo","Button"));
		table.appendChild(equalize);
	 
	 menuPropDiv.appendChild(table);
	 this.eventsbuttonRect();
}


Eshu.prototype.createOptionButtons=function(){
	 var ordering = document.createElement('tr');   
	 	ordering.appendChild( this.tdCreateButton2("forward", "forward","Button"));
	 	ordering.appendChild( this.tdCreateButton2("backward", "backward","Button"));
	    table.appendChild(ordering);
	
	var buttonOpcao = document.createElement('tr');   
	    buttonOpcao.appendChild(this.tdCreateButton("OKProperties", "OK","Button"));
	    buttonOpcao.appendChild( this.tdCreateButton("cancelProperties", "Cancel","Button"));
	    table.appendChild(buttonOpcao);
	    
	this.eventsbuttons();    
}

Eshu.prototype.changeType=function(type){
	
	if(this.graph.selection.isNode()){
		var nodeSel=this.graph.selection;
		var args={nodeType:this.elementsTypes.get(type),id:nodeSel.id,isNode:true};
//		currentCoordinate.x=nodeSel.x+nodeSel.width/2;
//		currentCoordinate.y=nodeSel.y+nodeSel.height/2;
//		
//		var remove=new Remove();
//		remove.execute(this.graph);
//		this.commandStack.push(remove); 
//		
//		var insert=new Insert();
//		insert.execute(this.graph,args);
//		insert.element.importNodeVariant(remove.element);
//		insert.element.adjustLabel(this.ctx);
//		this.commandStack.push(insert);
//		this.unSelectOperation();
		
		var changeType=new ChangeType();
		changeType.execute(this.graph,args,this.ctx);
		this.commandStack.push(changeType);
		
	}
	else {
		
		
		var edge=this.graph.selection;
//		var cardinality={cs:"",ct:""};
		var args={nodeType:this.elementsTypes.get(type),id:edge.id,isNode:false,
				source:edge.source,target:edge.target,pointsEdge:edge.listHandler};//,pointsEdge:ep
		
		var changeType=new ChangeType();
		changeType.execute(this.graph,args,this.ctx);
		this.commandStack.push(changeType);
		
//		if(edge.isCardinality)
//			 cardinality={cs:edge.cardinalitySource,ct:edge.cardinalityTarget};
//		var remove=new Remove();
//		remove.execute(this.graph);
////		this.commandStack.push(remove); 
//		var insert=new Insert();
//		
//		insert.execute(this.graph,args);
//		this.commandStack.push(insert);
//		edge=this.graph.selection;
//		if(edge.isCardinality){
//			if(edge.cardinalitySource)
//				edge.cardinalitySource=cardinality.cs;
//			if(edge.cardinalityTarget)
//				edge.cardinalityTarget=cardinality.ct;
//		}
//		this.unSelectOperation();
	}
	this.draw();
	
}

Eshu.prototype.eventsbuttonRect= function(){
	document.getElementById("equalizeMax").addEventListener('click',
			function(event) {
			this.graph.equalize();
			this.hideContextMenu();
			this.draw();
			
	}.bind(this), false);
	
	document.getElementById("equalizeMin").addEventListener('click',
			function(event) {
			this.graph.equalize(true);
			this.hideContextMenu();
			this.draw();
	}.bind(this), false);
	
	document.getElementById("alinharVLeft").addEventListener('click',
			function(event) {
			this.graph.alignHorizontally('left');
			this.hideContextMenu();
			this.draw();
	}.bind(this), false);
	
	document.getElementById("alinharVRigth").addEventListener('click',
			function(event) {
			this.graph.alignHorizontally('rigth');
			this.hideContextMenu();
			this.draw();
	}.bind(this), false);
	
	document.getElementById("alinharVCenter").addEventListener('click',
			function(event) {
			this.graph.alignHorizontally('center');
			this.hideContextMenu();
			this.draw();
	}.bind(this), false);
	
	
	document.getElementById("alinharHTop").addEventListener('click',
			function(event) {
			this.graph.alignVertically('top');
			this.hideContextMenu();
			this.draw();
	}.bind(this), false);
	
	document.getElementById("alinharHCenter").addEventListener('click',
			function(event) {
			this.graph.alignVertically('center');
			this.hideContextMenu();
			this.draw();
	}.bind(this), false);
	
	document.getElementById("alinharHBottom").addEventListener('click',
			function(event) {
			this.graph.alignVertically('bottom');
			this.hideContextMenu();
			this.draw();
	}.bind(this), false);
	
	
	
}
Eshu.prototype.eventsbuttons= function(){

//	document.getElementById("deleteProp").addEventListener('click',this.delet.bind(this), false);
	
	
	document.getElementById("OKProperties").addEventListener('click',
			function(event) {
			this.okEvents();
	}.bind(this), false);
	
	document.getElementById("cancelProperties").addEventListener('click', this.deletMenuProperties.bind(this), false);

	document.getElementById("forward").addEventListener('click', function(event) {
		this.increaseOrder();
	}.bind(this), false);
	
	document.getElementById("backward").addEventListener('click', function(event) {
		this.decreaseOrder();
	}.bind(this), false);
	
//	if(this.graph.selection.type=="Line")
//		document.getElementById("total").addEventListener('click', function(event) {
//			this.decreaseOrder();
//		}.bind(this), false);:

}

Eshu.prototype.okEvents = function (){
	this.setNodeSelectedValue();
	var selectedType=document.getElementById("selectedType");
	this.deletMenuProperties();
	if(selectedType && selectedType.value!=this.graph.selection.type){
		this.changeType(selectedType.value);
	}
	this.draw();
}


Eshu.prototype.exitEvents= function(keyId){
	if(keyId== 13)
		this.okEvents();
	else if(keyId==27)
		this.deletMenuProperties();
}



Eshu.prototype.changeValueButton= function(id){
	 var change = document.getElementById(id);
     if (change.innerHTML == "false") { change.innerHTML = true; }
     else {change.innerHTML = false;}
}

Eshu.prototype.changeValueButton= function(id){
	 var change = document.getElementById(id);
    if (change.innerHTML == "false") { change.innerHTML = true; }
    else {change.innerHTML = false;}
}
Eshu.prototype.changeValueButton2= function(id){
	 var change = document.getElementById(id);
   if (change.innerHTML == "no") {
	   change.innerHTML = "yes";
	   change.value="yes"
   }
   else {change.innerHTML = "no";
   		change.value="no"
   }
}

Eshu.prototype.showContextMenu= function(x, y){
	
	var divsOffSetheight=0;
	var divsOffSetwidth=0;
	var div=this.div;
	while(div!=null){
		if(divsOffSetheight<div.offsetTop)
			divsOffSetheight=div.offsetTop;
		if(divsOffSetwidth<div.offsetLeft)
			divsOffSetwidth=div.offsetLeft;
		div=div.parentNode;
		
	}
	
	menuPropDiv.style.top = (y-divsOffSetheight)  + "px";
	menuPropDiv.style.display = "block";
	menuPropDiv.style.left = (x-divsOffSetwidth) + "px";
	
	//this.menuAtive = 1;
}

Eshu.prototype.hideContextMenu= function(){

	console.log(2323)
//	this.graph.unselect();
	if(this.graphState!=8) return;
	if(this.graph.isSelectionRectangleSelection){
		this.graphState = 3;
		menuPropDiv.style.display = "none";
		return;
	}
	if (menuPropDiv !=undefined) {
		
		menuPropDiv.style.display = "none";
		this.deletMenuProperties();
		this.graphState = 0;
		this.graph.unselect();
		
	}
	

}

Eshu.prototype.deletMenuProperties= function(){
	if(this.graphState!=8) return;
	if (menuPropDiv !=undefined) {
		menuPropDiv.parentNode.removeChild(menuPropDiv);
		this.graphState = 0;
		this.valueNodeSelected=new Array();
	}
}

Eshu.prototype.tdCreateButton= function(id,butText,classNam,disabled){
    var td2 = document.createElement('td');
    var butKey =document.createElement("BUTTON");
    butKey.id=id;
    butKey.value=butText;
    butKey.className = classNam;
    if(disabled)butKey.disabled = true;
    var butText = document.createTextNode(butText); 
    butKey.style.width="100%";
    butKey.appendChild(butText);
    butKey.addEventListener('click', function(event) {
		this.changeValueButton2(butKey.id);}.bind(this), false);
    td2.appendChild(butKey);
    
   return td2;
}


/**
 * create button without events
 */
Eshu.prototype.tdCreateButton2= function(id,butText,classNam,disabled){
    var td2 = document.createElement('td');
    var butKey =document.createElement("BUTTON");
    butKey.id=id;
    butKey.className = classNam;
    if(disabled)butKey.disabled = true;
    var butText = document.createTextNode(butText); 
    butKey.style.width="100%";
    butKey.appendChild(butText);
//    butKey.addEventListener('click', function(event) {
//		this.changeValueButton(butKey.id);}.bind(this), false);
    td2.appendChild(butKey);
    
   return td2;
}

Eshu.prototype.tdCreatLabel= function(labelText,center){
	var td1 = document.createElement('td');
	var label =document.createTextNode(labelText);
	td1.appendChild(label);
	td1.style.background="#A0A0A0";
	if(center){
		td1.setAttribute("align", "center");
		td1.setAttribute("colspan", "3"); 
	}
	return td1;

}



Eshu.prototype.tdCreateInput= function(id,value,classNam,disabled){
    var td2 = document.createElement('td');
    var input = document.createElement("INPUT");
    input.id=id;
    if(disabled)input.disabled = true;
    input.value= value;
    input.style.width  ="100%";
	input.style.height ="100%";
    td2.appendChild(input)
   return td2;
}

Eshu.prototype.tdCreateAnchor= function(listUrls){
//console.log(this.graph.selection.nodeType.infoUrl[0]);
	
	var tr = document.createElement('tr'); 
	tr.appendChild(this.tdCreatLabel("Info"));
	
	
	var div = document.createElement("div");    
	div.id  = "infos";
	var index=1;
	for(var i in listUrls ){
		//console.log(listUrls[i].url)
		div.appendChild(createAnchor(1+"Info",index++,listUrls[i].url));
	}
	tr.appendChild(div);
	
	return tr;
	
}

function createAnchor(id,text,url){
	//console.log(link)
	var link = document.createElement("a");
	link.setAttribute("id", id);
	link.href=url;
	//link.setAttribute("href", link);
	
	link.setAttribute("target","_blank");
	link.appendChild(document.createTextNode(text));
	link.style.marginLeft = "5px";
	
	return link;

}
Eshu.prototype.trCreateListSelect= function(value){
	
	var td2 = document.createElement('td');
	var tr = document.createElement('tr'); 
	
	tr.appendChild(this.tdCreatLabel("name"));

	td2 = document.createElement('tr'); 
	var select = document.createElement("SELECT");
	select.id="selectedEspGenCat";
	
	select.appendChild(createOptionSelect("d","D"));

	select.appendChild(createOptionSelect("o","O"));
	
	select.appendChild(createOptionSelect("U","U"));
	select.value=value
	
	
	td2.appendChild(select);
	tr.appendChild(td2);
	table.appendChild(tr);
	    	
}


Eshu.prototype.trCreateListSelectType= function(value,list){
	
	
	var td2 = document.createElement('td');
	var tr = document.createElement('tr'); 
	//td2.appendChild(this.tdCreatLabel("type"));
	tr.appendChild(this.tdCreatLabel("type"));

	td2 = document.createElement('tr'); 
	var select = document.createElement("SELECT");
	select.id="selectedType";
	
//	select.appendChild(createOptionSelect("attribute","Attribute"));
//	select.appendChild(createOptionSelect("entity","Entity"));
//	select.appendChild(createOptionSelect("relationship","Relationship"));
//	select.appendChild(createOptionSelect("espGenCat","EspGenCat"));
	for(var i in list){
		select.appendChild(createOptionSelect(list[i],list[i]));
	}
	
//	select.addEventListener('change', function(event) {
//	
//	}.bind(this), false);
	 
	select.value=value;
	td2.appendChild(select);
	tr.appendChild(td2);
	table.appendChild(tr);
	    	
}

Eshu.prototype.createDiv= function(idParent, idDiv,width,height){ 
	if (document.contains(document.getElementById(idDiv))) 
        document.getElementById(idDiv).remove();
    var div = document.createElement("div");    
    div.id     = idDiv;
    div.style.position = "fixed";
    div.style.width = width+"px";
    div.style.height = height+"px";
    div.style.background ='#C0C0C0';
    div.style.color =  "#505050";
    div.style.border="ridge";
    div.style.zIndex = "100";
    div.style.opacity="0.9";
    this.div.appendChild(div); 
    return div;
}

Eshu.prototype.clearMenuProperty=function(){
	if (document.contains(document.getElementById("menuProperties"))) {
        document.getElementById("menuProperties").remove();
		this.graphState = 0;
		this.valueNodeSelected=new Array();
	}
	
	
}

function createOptionSelect (value, nameText){
	
	var option = document.createElement("option");
	option.setAttribute("value", value);
	var t = document.createTextNode(nameText);
	option.appendChild(t);
	return option;
	
	
}

function convertStringBoolen(string) {
	if(typeof(string)=="boolean") 
		return string;
	if(!string) return false
	if (string.toLowerCase()=="true") {
		return true;
	} else if (string.toLowerCase()=="false") {
		return false;
	} else
		throw new Error("Invalid booeam type: " + string
				+ ". Please choose true or false");

}

function convertType(type,value) {
	var t=type.toLowerCase();
	if(t=="number" || t=="integer" || t=="int" )
		 return parseInt(value);
	else if(t.type=="booleam" || t.type=="bool")
		return convertStringBoolen(value);
	else{
		return value;
	}
}
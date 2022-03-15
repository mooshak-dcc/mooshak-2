


window.onload = function(){
	
	createConfiguration();
}

function createConfiguration(){
	
	var divConfig=createElement("DIV","config","configuration");
	
	var inputNameConfig = {type:"text",placeholder:"name",textlabel:"Name", parent:divConfig};
	createElementInputLabel(inputNameConfig);
	divConfig.appendChild(createElement("BR"));
	
	var inputDurationConfig = {type:"text",placeholder:"minutes",textlabel:"Duration",parent:divConfig};
	createElementInputLabel(inputDurationConfig);
	divConfig.appendChild(createElement("BR"));
	
	var inputRevision = {type:"checkbox",textlabel:"Revision",parent:divConfig};
	createElementInputLabel(inputRevision); 
	
	var inputRevision = {type:"checkbox",textlabel:"Shuffle",parent:divConfig};
	createElementInputLabel(inputRevision); 
	
	 $("editor").appendChild(divConfig);
	
}

function createElementGroup(){
	
	
}


function createElementDiv(id, class_){
	
	var div = document.createElement("DIV");
	div.setAttribute('id', id);
	div.setAttribute('class', class_);
	
	return div;
}

//id,type, parent,class_,text
function createElementInputLabel(config){
	var label=createElementLabel(config.textlabel);
	config.parent.appendChild(label);
	var input =createElementInput(config);
	config.parent.appendChild(input);
	console.log()
}

function createElementInput(config){
	
	var input = document.createElement("INPUT");
	if(config.id) input.setAttribute('id', config.id);
	input.setAttribute('type', config.type);
	if(config.inputClass)  input.setAttribute('class', config.inputClass);
	if(config.placeholder) input.setAttribute('placeholder', config.placeholder);
	return input;
	
}

function createElementLabel(text, class_){
	var label=createElement("LABEL");
	if(class_) label.setAttribute('class', class_);
	var text = document.createTextNode(text);
	label.appendChild(text);
	return label;
}







function createElement(elementType, id, class_){
	var element = document.createElement(elementType);
	if(id)element.setAttribute('id', id);
	if(class_) element.setAttribute('class', class_);
	return element;
}


function $(id){
	return document.getElementById(id);
}

function geneteID(){
	
}



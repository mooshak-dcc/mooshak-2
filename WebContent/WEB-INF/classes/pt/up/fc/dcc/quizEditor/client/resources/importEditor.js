


function ImportEditor(editor,xmlParser) {
	
this.editor = editor;	
this.xmlParser=xmlParser;
console.log('psy')
console.log(xmlParser)
}


ImportEditor.prototype.div;
ImportEditor.prototype = {
		
		
		
	init(){
		console.log(this.xmlParser.getConfig())
		alert('init')
		this.editor.removeALLGroups();
		alert('init')
		this.editor.importConfigXML(this.xmlParser.getConfig());
		alert('init')
		
		var groups = this.xmlParser.getGroups();
		for (var i=0; i<groups.length;i++){
			this.editor.importGroup(groups[i]);
		}
	},
	
	

		
	createConfiguration(){
	
		var divConfig= this.createElement("DIV","config","configuration");
		
		var inputNameConfig = {type:"text",placeholder:"name",textlabel:"Name", parent:divConfig};
		this.createElementInputLabel(inputNameConfig);
		divConfig.appendChild(this.createElement("BR"));
		
		var inputDurationConfig = {type:"text",placeholder:"minutes",textlabel:"Duration",parent:divConfig};
		this.createElementInputLabel(inputDurationConfig);
		divConfig.appendChild(this.createElement("BR"));
		
		var inputRevision = {type:"checkbox",textlabel:"Revision",parent:divConfig};
		this.createElementInputLabel(inputRevision); 
		
		var inputShuffle = {type:"checkbox",textlabel:"Shuffle",parent:divConfig};
		this.createElementInputLabel(inputShuffle); 
		
		
		var newGroup=this.createButton("newgroup","New Group","new",false);
		newGroup.addEventListener('click', function(event) {
			this.createElementGroup();
	    }.bind(this), false);
		divConfig.appendChild(newGroup);
		this.div.appendChild(divConfig);

		var btnExport=this.createButton("btnexport","Export","export",false);
		btnExport.addEventListener('click', function(event) {
	    }.bind(this), false);
		divConfig.appendChild(btnExport);
		
//		var btnImport=this.createButton("btninport","Import","inport",false);
//		btnImport.addEventListener('click', function(event) {
//			this.readSingleFile(event);
//	    }.bind(this), false);
		divConfig.appendChild(this.createButtonImport());
		
		this.div.appendChild(divConfig);
		
		this.createElementGroup();
	
},


 importGroup(){
	
	var groups=this.editor.divGroups;
	var divgroup= this.createElement("DIV",undefined ,"group");
	groups.appendChild(divgroup);
	
	
	var configGroup= this.createElement("DIV",undefined ,"config-group");
	
	var inputName = {type:"text",placeholder:"name",textlabel:"Group Name", parent:configGroup};
	this.createElementInputLabel(inputName);
	
	var inputScore = {type:"number",textlabel:"Score", inputClass:"numeric-input", parent:configGroup};
	this.createElementInputLabel(inputScore);
	
	var inputNQ= {type:"number",textlabel:"Number Question", inputClass:"numeric-input", parent:configGroup};
	this.createElementInputLabel(inputNQ);
	
	
	var inputShuffle = {type:"checkbox",textlabel:"Shuffle", parent:configGroup};
	this.createElementInputLabel(inputShuffle); 
	
	
	
	var newQA=this.createButton("newqa","New QA","new",false);
	newQA.addEventListener('click', function(event) {
		this.addQAButton(event);
    }.bind(this), false);
	configGroup.appendChild(newQA)
	
	
	var removeGroup = this.createButton(undefined,"X","btn-display",false);
//	removeGroup.style.display = "none";
	
	removeGroup.addEventListener('click', function(event) {
		event.target.parentElement.parentElement.remove();
    }.bind(this), false);
	
	configGroup.appendChild(removeGroup);
	
	
	
	
	divgroup.appendChild(configGroup);
	divgroup.appendChild(this.createElementQA());
	
},


createElementQA(event){
	
	
	var divQA= this.createElement("DIV",undefined ,"qa");
	
	var inputName = {type:"text",placeholder:"Question 1",textlabel:"Question Name", parent:divQA};
	this.createElementInputLabel(inputName);
	this.createSelectTypeQuestion(divQA);
	var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input", parent:divQA};
	this.createElementInputLabel(inputPTS);
	
	
	var qaButtonRemove = this.createButton("newgroup","X","btn-display",false);
	qaButtonRemove.addEventListener('click', function(event) {
		this.removeQA(event);
    }.bind(this), false);
	divQA.appendChild(qaButtonRemove);
	
	
	var qaButtonV = this.createButton("newgroup","-","btn-display",false);
	qaButtonV.addEventListener('click', function(event) {
		this.showHideQA(event);
    }.bind(this), false);
	divQA.appendChild(qaButtonV);
	
	
	
	this.CreateDivQuestion(divQA);
	
	this.createDivAnswer(divQA);
	
	if(event){
		var divGroup= event.target.parentElement.parentElement;
//		console.log("test "+divGroup )
//		divGroup.appendChild(divQA);
		
//		divGroup.insertBefore(divQA, divGroup.lastElementChild);
		console.log(divGroup.lastElementChild);
		
//		this.insertAfter(divQA,);
	}
//	else{
//		this.div.appendChild(divQA);
//	}
	return divQA;
},


addQAButton(event){
	if(event){
		var divGroup= event.target.parentElement.parentElement;
//		console.log("test "+divGroup )
		divGroup.appendChild(this.createElementQA());
		
//		divGroup.insertBefore(divQA, divGroup.lastElementChild);
//		console.log(divGroup.lastElementChild);
		
//		this.insertAfter(divQA,);
	}
	
	
},
 showHideQA(event){
	var btn=event.target;
//	console.log(" test  "+ btn.firstChild.nodeValue);
	var qa= event.target.parentElement;
	
	var question= qa.getElementsByClassName("question")[0];
	
	var answer= qa.getElementsByClassName("answer");
	
	if(btn.firstChild.nodeValue=="-"){
		
		if(question)
			question.style.display = "none";
		
		if(answer)
			for (var i = 0; i < answer.length; ++i)
				answer[i].style.display = "none";
		btn.firstChild.nodeValue='+';
		}
	else{
		
		if(question)
			question.style.display = "block";
		
		if(answer)
			for (var i = 0; i < answer.length; ++i)
				answer[i].style.display = "block";
		btn.firstChild.nodeValue='-';
		}
		
},

 insertAfter(newNode, referenceNode) {
	console.log("referenceNode");
	console.log(referenceNode)
    referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
},


removeQA(event){
	event.target.parentElement.remove();
},
//addmorePossibleAnswer(type,event){
//	
//	
//},


addmorePossibleAnswerSingle(event){
	var btn=event.target;
	var answer= event.target.parentElement;
	var qa=event.target.parentElement.parentElement;
	
	
var type=this.getValueSelect(qa);
	
	switch (type) {
	case 'Single':
		answer.insertBefore(this.createOptionSingle("Possible Answer"), btn);
		break;
	case 'Multiple':
		answer.insertBefore(this.createOptionSingle("Possible Answer"), btn);
		break;
	case 'Numeric':
		answer.insertBefore(this.createOptionNumeric(), btn);
		break;
	case 'ShortAnswer':
		answer.insertBefore(this.createOptionSingle("Correct Answer"), btn);
		break;
	case 'Matching':
		answer.insertBefore(this.createOptionMatching(), btn);
		break;
		
	default:
		alert("Type error");
	}
	
	
},


addmorePossibleAnswerFTB(event){
	var btn=event.target;
	var answer= event.target.parentElement;
	
	var option = this.createOptionSingle("Possible Answer");
//	divQuestion.appendChild(option);
	answer.insertBefore(option,btn);
	
//	 this.insertAfter(divQuestion, last)
	
},

CreateDivQuestion(parent){
	var divQuestion= this.createElement("DIV",undefined ,"question");
	divQuestion.appendChild(this.createElement("BR"));
	
	var label=this.createElementLabel('Question');
	divQuestion.appendChild(label);
	divQuestion.appendChild(this.createElement("BR"));
	
	var questionConf= {placeholder:"Write your question here ...", maxLength:1000, cols:60,rows:4 };
	var textArea=this.createTextArea(questionConf);
	
	textArea.addEventListener('blur', function(event) {
		parent=event.target.parentElement.parentElement;
		
//		var select=parent.getElementsByTagName("select")[0];
		
		
		
		var type=this.getValueSelect(parent);
		if(type=="FillintheBlank"){
			var array = this.getKeyFTB(event.target.value);
			console.log("Value "+array);
			
//			this.createElementSelectFTB(array,parent);
//			
//			if(array.length>0)
			this.createSelectFTB(array,parent);
			
			
//			
		}
		
    	}.bind(this), false);
	
	divQuestion.appendChild(textArea);
	parent.appendChild(divQuestion);
	
},

createSelectFTB(array,parent){
	
	var divAnswer= parent.getElementsByClassName("answer")[0];
	this.removeAll(divAnswer,array);
//	console.log("AQUI")
//	console.log(divAnswer)
	
	for(var i in array){
		
		var div = this.getById(array[i]);
		if(!div) {
			var divQuestion= this.createElement("DIV",array[i] ,"select-options");
//			divQuestion.style.display = "none";
			
			var label=this.createElementLabel(array[i]);
			divQuestion.appendChild(label);
			
			var option = this.createOptionSingle("Possible Answer");
			divQuestion.appendChild(option);
			
//			 this.insertAfter(divQuestion, last)
			divAnswer.appendChild(divQuestion);
			
			
			var addmoreAnswer = this.createButton(undefined,"Add more Answer","add-answer",false);
			addmoreAnswer.addEventListener('click', function(event) {
				this.addmorePossibleAnswerFTB(event)
		    }.bind(this), false);
			
			divQuestion.appendChild(addmoreAnswer);
			
//			console.log("log")
//			console.log(this.getLastDiv(divAnswer));
//			this.getLastDiv(divAnswer);
		}
	}
	

//	var optionSelectSelected=this.getValueSelect(divAnswer);
//	this.$(optionSelectSelected).style.display = "block";
	
},

getLastDiv(divAnswer){
	var children = divAnswer.getElementsByClassName("select-options");
	
	var lastChild=divAnswer.getElementsByTagName("select");
	if(children.length>0)
		lastChild = children[(children.length-1)]
	return lastChild;
},

removeAll(div,listName){
	var d =div.firstChild;
	while (d) {
		console.log( )
//		console.log(this.divExits(d,listName));
		
		if((!this.divExits(d,listName)) && d.classList.contains('select-options')){ //d.tagName !='SELECT'){
			var dAux=d;
			d=d.nextSibling;
			dAux.remove();
			
		}
		else
			d=d.nextSibling;
		
	}
	
	
},

divExits(div ,array){
	for(var i in array){
		if(div.id==array[i])
			return true;
	}
	return false;
},

removeAllOption(div){
	var d =div.firstChild;
	while (d) {
		console.log( )
//		console.log(this.divExits(d,listName));
		if(d.tagName !='option'){
			var dAux=d;
			d=d.nextSibling;
			dAux.remove();
			
		}
		else
			d=d.nextSibling;
		
	}
	
	
},

//removeAllChildren(myNode){
//	while (myNode.firstChild) {
//	    myNode.removeChild(myNode.firstChild);
//	}
//},

createSelectTypeQuestion(parent){

	//Create array of options to be added
	var array = ["Multiple","Single","Numeric","Boolean","FillintheBlank","ShortAnswer","Matching","EssayQuestion"];

	//Create and append select list
	var selectList = document.createElement("select");
//	selectList.id = "mySelect";
	parent.appendChild(selectList);

	//Create and append the options
	for (var i = 0; i < array.length; i++) {
	    var option = document.createElement("option");
	    option.value = array[i];
	    option.text = array[i];
	    selectList.appendChild(option);
	}
	
	selectList.addEventListener('change', function(event) {
//		event.path[0].getElementsByClassName("remove-choice")[0].style.display = "inline";
		var parent=event.target.parentElement;
		var answer=parent.getElementsByClassName("answer")[0];
		answer.remove();
		
		
//		console.log();
		
		var select=event.target;
		var index=select.selectedIndex
		var value=select.options[index].value;
		
		
		this.createDivAnswer(event.target.parentElement);
		
    }.bind(this), false);
	
},




createSelectTypeNumeric(parent){


	var array = ["Exact Answer","Answer in the Range","Answer with Precision"];

	var selectList = document.createElement("select");
//	selectList.id = "mySelect";
	parent.appendChild(selectList);

	for (var i = 0; i < array.length; i++) {
	    var option = document.createElement("option");
	    option.value = array[i];
	    option.text = array[i];
	    selectList.appendChild(option);
	}
	
	selectList.addEventListener('change', function(event) {
		
		var select=event.target;
		var index=select.selectedIndex
		var value=select.options[index].value;
		
		if(value=="Exact Answer"){
			selectList.nextSibling.innerHTML="";
			selectList.nextSibling.nextSibling.nextSibling.innerHTML="with error margin";
		}
		else if(value=="Answer in the Range"){
			selectList.nextSibling.innerHTML="between";
			selectList.nextSibling.nextSibling.nextSibling.innerHTML="end";
		}
		else if(value=="Answer with Precision"){
			selectList.nextSibling.innerHTML="";
			selectList.nextSibling.nextSibling.nextSibling.innerHTML="with Precisio";
		}
		
		

		
//		var select=event.path[0];
//		var index=select.selectedIndex
//		var value=select.options[index].value;
		
		
    }.bind(this), false);
	
},


createElementSelectFTB(array,parent){
	
	var divAnswer= parent.getElementsByClassName("answer")[0];
	
	var selectList=divAnswer.getElementsByTagName("select")[0];
//	if(select) select.remove();
	
	
	
	if(selectList)
		this.removeAllOption(selectList);
	else{
		selectList = document.createElement("select");
		divAnswer.appendChild(selectList);
	}
	
//	var selectList= document.createElement("select");
	
	if(array.length>0){
	
		for (var i = 0; i < array.length; i++) {
		    var option = document.createElement("option");
		    option.value = array[i];
		    option.text = array[i];
		    selectList.appendChild(option);
		}
	}
	else{
		  var option = document.createElement("option");
		    option.value = 0;
		    option.text = "[Enter Answer variables Above]";
		    selectList.appendChild(option);
		
	}
	
	divAnswer.appendChild(selectList);
	
	
//		select.remove();
	
	selectList.addEventListener('change', function(event) {
////		event.path[0].getElementsByClassName("remove-choice")[0].style.display = "inline";
		var parent=event.target.parentElement;
//		var answer=parent.getElementsByClassName("answer")[0];
//		answer.remove();
//		
//		
//		console.log(parent)
//		console.log(parent);
		
		var nodes = parent.parentElement.getElementsByClassName("select-options");
		for(var i in nodes){
			if(nodes[i].tagName=="DIV")
			nodes[i].style.display = "none";
//			console.log("display")
//			console.log(nodes[i].tagname);
		}
		
//		var child = parent.firstChild;
//
//		while(child && child.nodeType !== 1) {
//			console.log(child)
//		    child = child.nextSibling;
//		}
//		
		var select=event.target;
		var index=select.selectedIndex
		var value=select.options[index].value;
		this.$(value).style.display = "block";
		
//		this.createDivAnswer(event.target.parentElement);
		
    }.bind(this), false);
},


createOptionSingle(text){
	
	var divOption= this.createElement("DIV",undefined ,"choice");
	
//	divOption.onmouseenter = function(){myScript};

	
	divOption.addEventListener('mouseenter', function(event) {
		event.target.getElementsByClassName("remove-choice")[0].style.display = "inline";
		
    }.bind(this), false);

	
	divOption.addEventListener('mouseleave', function(event) {
		event.target.getElementsByClassName("remove-choice")[0].style.display = "none";
		
    }.bind(this), false);
	
	
	
	
	var label=this.createElementLabel(text);
	divOption.appendChild(label);
	
	var optionConf= {placeholder:"text answer", maxLength:1000, cols:50,rows:1 };
	divOption.appendChild(this.createTextArea(optionConf));
	
	var feedbackConfig= {placeholder:"Write your feedback here ...", maxLength:1000, cols:50,rows:1 };
	divOption.appendChild(this.createTextArea(feedbackConfig));
	
	var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input", parent:divOption};
	this.createElementInputLabel(inputPTS);
	
	var addmoreAnswer = this.createButton(undefined,"X","remove-choice",false);
	addmoreAnswer.style.display = "none";
	
	addmoreAnswer.addEventListener('click', function(event) {
		event.target.parentElement.remove();
    }.bind(this), false);
	
	divOption.appendChild(addmoreAnswer);
	
	divOption.appendChild(this.createElement("BR"));
	
	
	return divOption;
	
},




createOptionMatching(){
	
	var divOption= this.createElement("DIV",undefined ,"choice");
	
//	divOption.onmouseenter = function(){myScript};
	
	divOption.addEventListener('mouseenter', function(event) {
		event.target.getElementsByClassName("remove-choice")[0].style.display = "inline";
		
    }.bind(this), false);
	
	divOption.addEventListener('mouseleave', function(event) {
		event.target.getElementsByClassName("remove-choice")[0].style.display = "none";
    }.bind(this), false);
	
	
	
	
	var optionConf1= {placeholder:"Matching left side", maxLength:1000, cols:50,rows:1 };
	divOption.appendChild(this.createTextArea(optionConf1));
	
	var optionConf2= {placeholder:"Matching left Side", maxLength:1000, cols:50,rows:1 };
	divOption.appendChild(this.createTextArea(optionConf2));
	
	var feedbackConfig= {placeholder:"Write your feedback here ...", maxLength:1000, cols:20,rows:1 };
	divOption.appendChild(this.createTextArea(feedbackConfig));
	
	var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input", parent:divOption};
	this.createElementInputLabel(inputPTS);
	
	var addmoreAnswer = this.createButton(undefined,"X","remove-choice",false);
	addmoreAnswer.style.display = "none";
	
	addmoreAnswer.addEventListener('click', function(event) {
		event.target.parentElement.remove();
    }.bind(this), false);
	
	divOption.appendChild(addmoreAnswer);
	
	divOption.appendChild(this.createElement("BR"));
	
	
	return divOption;
	
},


createOptionNumeric(){
	
	var divOption= this.createElement("DIV",undefined ,"choice");
	
//	divOption.onmouseenter = function(){myScript};
	
	divOption.addEventListener('mouseenter', function(event) {
		event.target.getElementsByClassName("remove-choice")[0].style.display = "inline";
		
    }.bind(this), false);
	
	divOption.addEventListener('mouseleave', function(event) {
		event.target.getElementsByClassName("remove-choice")[0].style.display = "none";
    }.bind(this), false);
	
	
	this.createSelectTypeNumeric(divOption);
	
	this.createOptionNumericAux(divOption,"","with error margin");
	
	return divOption;
	
},



createOptionNumericAux(divOption,text1,text2){
	

	var inputValue= {type:"number",textlabel:text1, inputClass:"numeric-input", parent:divOption};
	this.createElementInputLabel(inputValue);
	
	var erroMargin= {type:"number",textlabel:text2, inputClass:"With Precisio", parent:divOption};
	this.createElementInputLabel(erroMargin);
	
	
//	var btnRemove
	divOption.appendChild(this.createButtonRemoveChoice());
	
	divOption.appendChild(this.createElement("BR"));
	
},


createButtonRemoveChoice(){
	var btnRemove = this.createButton(undefined,"X","remove-choice",false);
	btnRemove.style.display = "none";
	
	btnRemove.addEventListener('click', function(event) {
		event.target.parentElement.remove();
    }.bind(this), false);
	return btnRemove;
},


//createOptionNumericAux(divOption){
//	
//	
//	var inputValue= {type:"number",textlabel:"Between", inputClass:"numeric-input", parent:divOption};
//	this.createElementInputLabel(inputValue);
//	
//	var erroMargin= {type:"number",textlabel:"end", inputClass:"numeric-input", parent:divOption};
//	this.createElementInputLabel(erroMargin);
//	
//	
//	divOption.appendChild(createButtonRemoveChoice());
//	
//	divOption.appendChild(this.createElement("BR"));
//	
//},

//createOptionNumericAux(divOption){
//	
//	
//	var inputValue= {type:"number",textlabel:"", inputClass:"numeric-input", parent:divOption};
//	this.createElementInputLabel(inputValue);
//	
//	var erroMargin= {type:"number",textlabel:"with error margin", inputClass:"numeric-input", parent:divOption};
//	this.createElementInputLabel(erroMargin);
//	
//	var addmoreAnswer = this.createButton(undefined,"X","remove-choice",false);
//	addmoreAnswer.style.display = "none";
//	
//	addmoreAnswer.addEventListener('click', function(event) {
//		event.path[1].remove();
//    }.bind(this), false);
//	
//	divOption.appendChild(addmoreAnswer);
//	
//	divOption.appendChild(this.createElement("BR"));
//	
//},

createOptionBoolean(text){
	
	var divOption= this.createElement("DIV",undefined ,"choice");
	
	
	var label=this.createElementLabel(text);
	divOption.appendChild(label);
	
	var feedbackConfig= {placeholder:"Write your feedback here ...", maxLength:1000, cols:50,rows:1 };
	divOption.appendChild(this.createTextArea(feedbackConfig));
	
	var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input", parent:divOption};
	this.createElementInputLabel(inputPTS);
	
	divOption.appendChild(this.createElement("BR"));
	
	
	return divOption;
	
	
},

createOptionEssay(){
	
	var divOption= this.createElement("DIV",undefined ,"choice");
	
	
	
	var feedbackConfig= {placeholder:"Write your feedback here ...", maxLength:1000, cols:50,rows:1 };
	divOption.appendChild(this.createTextArea(feedbackConfig));
	
	var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input", parent:divOption};
	this.createElementInputLabel(inputPTS);
	
	divOption.appendChild(this.createElement("BR"));
	
	
	return divOption;
	
	
},



getValueSelect(parent){
	var select=parent.getElementsByTagName("select")[0];
	var index=select.selectedIndex;
	return select.options[index].value;
	
},

createDivAnswer(parent){
	
	
	var divAnswer= this.createElement("DIV",undefined ,"answer");
	parent.appendChild(divAnswer);
	var type=this.getValueSelect(parent);
	
	switch (type) {
	case 'Single':
		this.createChoiceSingle(divAnswer);
		divAnswer.appendChild(this.addMoreOption());
		break;
	case 'Boolean':
		this.createChoiceBoolean(divAnswer);
		break;
	case 'Multiple':
		this.createChoiceSingle(divAnswer);
		divAnswer.appendChild(this.addMoreOption());
		break;
	case 'EssayQuestion':
		this.createChoiceEssay(divAnswer);
		break;
	case 'Numeric':
		this.createChoiceNumeric(divAnswer);
		divAnswer.appendChild(this.addMoreOption());
		break;
	case 'ShortAnswer':
		this.createChoiceShortAnswer(divAnswer);
		divAnswer.appendChild(this.addMoreOption());
		break;
	case 'Matching':
		this.createChoiceMatching(divAnswer);
		divAnswer.appendChild(this.addMoreOption());
		break;
	case 'FillintheBlank':
		this.createChoiceFillintheBlank(parent,divAnswer);
//		divAnswer.appendChild(this.addMoreOption());
		break;
		
	default:
//		alert("Type error");
	}
	
	
	
	
	
	
},


addMoreOption(){
	
	var addmoreAnswer = this.createButton(undefined,"Add more Answer","add-answer",false);
	addmoreAnswer.addEventListener('click', function(event) {
		this.addmorePossibleAnswerSingle(event)
    }.bind(this), false);
	return addmoreAnswer;
	
},


cretelabelAnswer(divAnswer){
	var label=this.createElementLabel('Answer');
	divAnswer.appendChild(label);
	divAnswer.appendChild(this.createElement("BR"));
},

createChoiceSingle(divAnswer){
	this.cretelabelAnswer(divAnswer);
	
	divAnswer.appendChild(this.createOptionSingle("Correct Answer"));
	divAnswer.appendChild(this.createOptionSingle("Possible Answer"));
},


createChoiceBoolean(divAnswer){
	this.cretelabelAnswer(divAnswer);
	
	divAnswer.appendChild(this.createOptionBoolean("True"));
	divAnswer.appendChild(this.createOptionBoolean("False"));
},


createChoiceNumeric(divAnswer){
	divAnswer.appendChild(this.createOptionNumeric());
},


createChoiceShortAnswer(divAnswer){
	this.cretelabelAnswer(divAnswer);
	
	divAnswer.appendChild(this.createOptionSingle("Correct Answer"));
},

createChoiceMatching(divAnswer){
	this.cretelabelAnswer(divAnswer);
	
	divAnswer.appendChild(this.createOptionMatching());
	divAnswer.appendChild(this.createOptionMatching());
	divAnswer.appendChild(this.createOptionMatching());
},


createChoiceFillintheBlank(parent,divAnswer){
	
	var label=this.createElementLabel('[Enter Answer variables Above]');
	divAnswer.appendChild(label);
	divAnswer.appendChild(this.createElement("BR"));
	
//	  option.text = "[Enter Answer variables Above]";
	var divQuestion = parent.querySelector(".question");
	var text = (divQuestion.getElementsByTagName("textarea")[0]).value;
	
	var array = this.getKeyFTB(text);
		this.createSelectFTB(array,parent);
	
	
	
//	this.createSelectFTB();
},

createChoiceEssay(divAnswer){
	divAnswer.appendChild(this.createOptionEssay());
},



 createElementDiv(id, class_){
	
	var div = document.createElement("DIV");
	div.setAttribute('id', id);
	div.setAttribute('class', class_);
	
	return div;
},


createElementInputLabel(config){
	var label=this.createElementLabel(config.textlabel);
	config.parent.appendChild(label);
	var input =this.createElementInput(config);
	config.parent.appendChild(input);
},

addElementInputLabel(config){
	var label=this.createElementLabel(config.textlabel);
	config.parent.insertBefore(label,config.referenceNode);
	var input =this.createElementInput(config);
//	config.parent.appendChild(input);
	config.parent.insertBefore(input, config.referenceNode)
},


 createElementInput(config){
	
	var input = document.createElement("INPUT");
	if(config.id) input.setAttribute('id', config.id);
	input.setAttribute('type', config.type);
	if(config.inputClass)  input.setAttribute('class', config.inputClass);
	if(config.placeholder) input.setAttribute('placeholder', config.placeholder);
	return input;
	
},

 createElementLabel(text, class_){
	var label=this.createElement("LABEL");
	if(class_) label.setAttribute('class', class_);
	var text = document.createTextNode(text);
	label.appendChild(text);
	return label;
},



 createElement(elementType, id, class_){
	var element = document.createElement(elementType);
	if(id)element.setAttribute('id', id);
	if(class_) element.setAttribute('class', class_);
	return element;
},


 createButton(id,butText,classNam,disabled){
	    var butKey =document.createElement("BUTTON");
	    if(id)butKey.id=id;
	    butKey.value=butText;
	    if(classNam)butKey.className = classNam;
	    if(disabled)butKey.disabled = true;
	    if(butText){	    var butText = document.createTextNode(butText); 
	    				butKey.appendChild(butText);}
//	    butKey.addEventListener('click', function(event) {
////			this.changeValueButton2(butKey.id);
//			alert(123);
//	    }.bind(this), false);
    
   return butKey;
},

createTextAreaLabel(config){
	
	var label=this.createElementLabel(config.textlabel);
	config.parent.appendChild(label);
	
	var textA =this.createTextArea(config);
	config.parent.appendChild(textA);
	
},


createTextArea(config){
	
	var textArea = this.createElement("textarea", config.id, config.textAreaClass)
	if(config.placeholder)textArea.setAttribute('placeholder', config.placeholder);
	if(config.maxLength)textArea.maxLength = config.maxLength;
	if(config.cols)textArea.cols = config.cols;
	if(config.rows)textArea.rows = config.rows;
	return textArea
	
},

getKeyFTB(txt){
//	var txt = "I expect five hundred dollars ($500). and new brackets ($600)";
	var list=[];
	var newTxt = txt.split('[');
	for (var i = 1; i < newTxt.length; i++) {
//	    console.log(newTxt[i].split(')')[0]);
		list.push(newTxt[i].split(']')[0]);
	}
	return list;
	
},

 $(id){
	return document.getElementById(id);
},

 geneteID(){},
 
 
 
 exportXML(){
	 var question= this.div.getElementsByClassName("group")[0];
	 console.log(question);

},


createButtonImport(){
	 var butKey =document.createElement("INPUT");
   butKey.id='btninput';
   butKey.value=butText;
   butKey.type='file';
    var butText = document.createTextNode("Inport"); 
   butKey.appendChild(butText);
   
   butKey.addEventListener('change', function(event) {
		this.readSingleFile(event);
   }.bind(this), false);
	return butKey;
	
},


readSingleFile(evt) {
   //Retrieve the first (and only!) File from the FileList object
   var f = evt.target.files[0]; 
   console.log(f)
   if (f) {
     var r = new FileReader();
     r.onload = function(e) { 
	      var contents = e.target.result;
//	      createEditor(contents); 
	      this.XMLParser= new XMLParser(contents);
	      console.log(this.XMLParser.$("d2e1"));
	      return contents;
     }
     r.readAsText(f);
   } else { 
     alert("Failed to load file");
   }
 },

}



	
/**
 * animation in button remove e adicionar css
 */
//window.onload = function() {
//	var editor = new Editor(document.getElementById("editor"));
//	editor.init();
////	nicEditors.allTextAreas();
//	
//	???
//}


//var Editor = function(div) {

function Editor(div) {
	
this.div = div;	
this.XMLParser;
this.divgroups;

}

Editor.init = function(el) {
	el.editor = new Editor(el);
	el.editor.createConfiguration();
	
}


Editor.prototype = {
		
		onChange(json) {},
			
		createConfiguration(){
		
			var divConfig= this.createElement("DIV","config","configuration");
			
			var inputNameConfig = {type:"text",placeholder:"name", inputClass:"text-input", inputClass:"input-name-cong", textlabel:"Name", parent:divConfig};
			this.createElementInputLabel(inputNameConfig);
			divConfig.appendChild(this.createElement("BR"));
			
			var inputDurationConfig = {type:"text",placeholder:"minutes", inputClass:"text-input", inputClass:"input-name-duration", textlabel:"Duration",parent:divConfig};
			this.createElementInputLabel(inputDurationConfig);
			divConfig.appendChild(this.createElement("BR"));
			
			var inputRevision = {type:"checkbox",textlabel:"Revision",inputClass:"input-revision",parent:divConfig};
			this.createElementInputLabel(inputRevision); 
			
			var inputShuffle = {type:"checkbox",textlabel:"Shuffle",inputClass:"input-shuffle",parent:divConfig};
			this.createElementInputLabel(inputShuffle); 
			
			var inputScore = {type:"number",textlabel:"Score", inputClass:"numeric-input-total-score", disabled:'true', parent:divConfig};
			this.createElementInputLabel(inputScore);
			
			
			var newGroup=this.createButton("newgroup","New Group","new",false);
			newGroup.addEventListener('click', function(event) {
				this.createElementGroup();
		    }.bind(this), false);
			divConfig.appendChild(newGroup);
			this.div.appendChild(divConfig);

			var btnExport=this.createButton("btnexport","Export","export",false);
			btnExport.addEventListener('click', function(event) {
				this.exportJSON();
		    }.bind(this), false);
			divConfig.appendChild(btnExport);
			
//			var btnImport=this.createButton("btninport","Import","inport",false);
//			btnImport.addEventListener('click', function(event) {
//				this.readSingleFile(event);
//		    }.bind(this), false);
			divConfig.appendChild(this.createButtonImport());
			
			this.div.appendChild(divConfig);
			
			this.createElementGroups();
		
	},

	createElementGroups(){
		this.divgroups= this.createElement("DIV",undefined ,"groups");
		this.div.appendChild(this.divgroups);
		this.createElementGroup();
	},



	 createElementGroup(){
		
		
		
		
		var divgroup= this.createElement("DIV",undefined ,"group");
		this.divgroups.appendChild(divgroup);
		
		
		var configGroup= this.createElement("DIV",undefined ,"config-group");
		
		var inputName = {type:"text",placeholder:"name",textlabel:"Group Name",inputClass:"group-name", parent:configGroup};
		this.createElementInputLabel(inputName);
		
		var inputScore = {type:"number",textlabel:"Score", inputClass:"numeric-input-pts-group", disabled:'true', parent:configGroup};
		this.createElementInputLabel(inputScore);
		
		var inputNQ= {type:"number",textlabel:"Number Question", inputClass:"numeric-input group-nq", parent:configGroup};
		this.createElementInputLabel(inputNQ);
		
		
		var inputShuffle = {type:"checkbox",textlabel:"Shuffle",inputClass:"group-shuffe", parent:configGroup};
		this.createElementInputLabel(inputShuffle); 
		
		
		
		var newQA=this.createButton("newqa","New QA","new",false);
		newQA.addEventListener('click', function(event) {
			this.addQAButton(event);
	    }.bind(this), false);
		configGroup.appendChild(newQA)
		
		
		var removeGroup = this.createButton(undefined,"X","btn-display",false);
//		removeGroup.style.display = "none";
		
		removeGroup.addEventListener('click', function(event) {
			event.target.parentElement.parentElement.remove();
	    }.bind(this), false);
		
		configGroup.appendChild(removeGroup);
		
		
		
		
		divgroup.appendChild(configGroup);
		divgroup.appendChild(this.createElementQA());
		
	},


	createElementQA(event){
		
		
		var divQA= this.createElement("DIV",undefined ,"qa");
		
		var inputName = {type:"text",placeholder:"Question 1",textlabel:"Question Name", inputClass:"text-input", parent:divQA};
		this.createElementInputLabel(inputName);
		this.createSelectTypeQuestion(divQA);
		var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts-qa", disabled:'true', classlabel:'pts', parent:divQA};
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
//			divGroup.appendChild(divQA);
			
//			divGroup.insertBefore(divQA, divGroup.lastElementChild);
			
//			this.insertAfter(divQA,);
		}
//		else{
//			this.div.appendChild(divQA);
//		}
		return divQA;
	},


	addQAButton(event){
		if(event){
			var divGroup= event.target.parentElement.parentElement;
			divGroup.appendChild(this.createElementQA());
			
//			divGroup.insertBefore(divQA, divGroup.lastElementChild);
			
//			this.insertAfter(divQA,);
		}
		
		
	},
	 showHideQA(event){
		var btn=event.target;
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
//		divQuestion.appendChild(option);
		answer.insertBefore(option,btn);
		
//		 this.insertAfter(divQuestion, last)
		
	},

	CreateDivQuestion(parent){
		var divQuestion= this.createElement("DIV",undefined ,"question");
		divQuestion.appendChild(this.createElement("BR"));
		
		var label=this.createElementLabel('Question');
		divQuestion.appendChild(label);
		divQuestion.appendChild(this.createElement("BR"));
		
		var questionConf= {placeholder:"Write your question here ...", className:'input-question', maxLength:1000, cols:60,rows:4 };
		var textArea=this.createTextArea(questionConf);
		
		textArea.addEventListener('blur', function(event) {
			parent=event.target.parentElement.parentElement;
			
			var type=this.getValueSelect(parent);
			if(type=="FillintheBlank"){
				var array = this.getKeyFTB(event.target.value);
				this.createSelectFTB(array,parent);
			}
			
	    	}.bind(this), false);
		
		divQuestion.appendChild(textArea);
		parent.appendChild(divQuestion);
		
	},

	createSelectFTB(array,parent){
		
		var divAnswer= parent.getElementsByClassName("answer")[0];
		this.removeAll(divAnswer,array);
		for(var i in array){
			
			var div = this.getById(array[i]);
			if(!div) {
				var divQuestion= this.createElement("DIV",array[i] ,"select-options");
//				divQuestion.style.display = "none";
				
				var label=this.createElementLabel(array[i]);
				divQuestion.appendChild(label);
				
				var option = this.createOptionSingle("Possible Answer");
				divQuestion.appendChild(option);
				
//				 this.insertAfter(divQuestion, last)
				divAnswer.appendChild(divQuestion);
				
				
				var addmoreAnswer = this.createButton(undefined,"Add more Answer","add-answer",false);
				addmoreAnswer.addEventListener('click', function(event) {
					this.addmorePossibleAnswerFTB(event)
			    }.bind(this), false);
				
				divQuestion.appendChild(addmoreAnswer);
			}
		}
		

//		var optionSelectSelected=this.getValueSelect(divAnswer);
//		this.getById(optionSelectSelected).style.display = "block";
		
	},


	importSelectFTB(array,parent,xmlQA){
		
		var divAnswer= parent.getElementsByClassName("answer");
//		this.removeAll(divAnswer,array);
		
		for(var i in array){
			
			var div = this.getById(array[i]);
			if(!div) {
				var divQuestion= this.createElement("DIV",array[i] ,"select-options");
				
				var label=this.createElementLabel(array[i]);
				divQuestion.appendChild(label);
				var answer= xmlQA.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","answer");
				
//				var choices = answer[0];
				for(j=0;j<answer.length;j++)
					if(answer[j].id == array[i]){
						var choices =  answer[j].getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","choice");
						for(var k = 0; k<choices.length; k++){
							divQuestion.appendChild(this.importOptionSingle(choices[k]));
						}
					}
				
				parent.appendChild(divQuestion);
				
					
//				var option = this.createOptionSingle("Possible Answer");
//				divQuestion.appendChild(option);
//				divAnswer.appendChild(divQuestion);
				
				
				var addmoreAnswer = this.createButton(undefined,"Add more Answer","add-answer",false);
				addmoreAnswer.addEventListener('click', function(event) {
					this.addmorePossibleAnswerFTB(event)
			    }.bind(this), false);
				
				divQuestion.appendChild(addmoreAnswer);
			}
		}
		
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
			if(d.tagName !='option'){
				var dAux=d;
				d=d.nextSibling;
				dAux.remove();
				
			}
			else
				d=d.nextSibling;
			
		}
		
		
	},


	// 201604674 Fnaque 
	removeAllChildren(myNode){
		while (myNode.firstChild) {
		    myNode.removeChild(myNode.firstChild);
		}
	},

	createSelectTypeQuestion(parent,type){

		//Create array of options to be added
		var array = ["Multiple","Single","Numeric","Boolean","FillintheBlank","ShortAnswer","Matching","EssayQuestion"];

		//Create and append select list
		var selectList = document.createElement("select");
//		selectList.id = "mySelect";
		parent.appendChild(selectList);

		//Create and append the options
		for (var i = 0; i < array.length; i++) {
		    var option = document.createElement("option");
		    option.value = array[i];
		    option.text = array[i];
		    selectList.appendChild(option);
		}
		
		selectList.addEventListener('change', function(event) {
//			event.path[0].getElementsByClassName("remove-choice")[0].style.display = "inline";
			var parent=event.target.parentElement;
			var answer=parent.getElementsByClassName("answer")[0];
			answer.remove();
			
			var select=event.target;
			var index=select.selectedIndex
			var value=select.options[index].value;
			this.createDivAnswer(event.target.parentElement);
			

			
			 var ptsChoices =event.target.parentElement.getElementsByClassName("numeric-input-pts");
			   var ptsQA = event.target.parentElement.getElementsByClassName("numeric-input-pts-qa"); //[0].value
			   
			    var total=0;
			   for(var i =0;i<ptsChoices.length;i++){
				   
					total+=parseInt(ptsChoices[i].value);
				}
			   ptsQA[0].value=total;
			   
			 
			   
			   var ptsGroup = event.target.parentElement.parentElement.getElementsByClassName("numeric-input-pts-group");//.getElementsByClassName("numeric-input-pts-qa"); //[0].value
			   var ptsQAs =   event.target.parentElement.parentElement.getElementsByClassName("numeric-input-pts-qa"); //[0].value
			   
			   total=0;
			   for(var i =0;i<ptsQAs.length;i++){
				   
					total+=parseInt(ptsQAs[i].value);
				}
			   ptsGroup[0].value=total; 
			   
			
			
			
			
			
			
			
			
			
	    }.bind(this), false);
		
		
		if(type){
		    for(var i = 0, j = selectList.options.length; i < j; ++i) {
		    	var text=selectList.options[i].value.toLowerCase()
		        if( text== type.toLowerCase()) {
		        	selectList.selectedIndex = i;
		           break;
		        }
		    }
		}
		
	},




	createSelectTypeNumeric(parent,subtype){


		var array = ["Exact Answer","Answer in the Range","Answer with Precision"];

		var selectList = document.createElement("select");
//		selectList.id = "mySelect";
		parent.appendChild(selectList);

		for (var i = 0; i < array.length; i++) {
		    var option = document.createElement("option");
		    option.value = array[i];
		    option.text = array[i];
		    selectList.appendChild(option);
		}
		
		
		if(subtype=='A'){
			selectList.selectedIndex = 0;
		}
		else if (subtype=='B'){
			selectList.selectedIndex = 1
		}
		else if(subtype=='C'){
			selectList.selectedIndex = 2
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
				selectList.nextSibling.nextSibling.nextSibling.innerHTML="with Precision";
			}
			
			
	    }.bind(this), false);
		
	},


	createElementSelectFTB(array,parent){
		
		var divAnswer= parent.getElementsByClassName("answer")[0];
		
		var selectList=divAnswer.getElementsByTagName("select")[0];
//		if(select) select.remove();
		
		
		
		if(selectList)
			this.removeAllOption(selectList);
		else{
			selectList = document.createElement("select");
			divAnswer.appendChild(selectList);
		}
		
//		var selectList= document.createElement("select");
		
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
		
		
//			select.remove();
		
		selectList.addEventListener('change', function(event) {
////			event.path[0].getElementsByClassName("remove-choice")[0].style.display = "inline";
			var parent=event.target.parentElement;
//			var answer=parent.getElementsByClassName("answer")[0];
//			answer.remove();
//			
//			
			
			var nodes = parent.parentElement.getElementsByClassName("select-options");
			for(var i in nodes){
				if(nodes[i].tagName=="DIV")
				nodes[i].style.display = "none";
			}
			
//			var child = parent.firstChild;
	//
//			while(child && child.nodeType !== 1) {
//			    child = child.nextSibling;
//			}
//			
			var select=event.target;
			var index=select.selectedIndex
			var value=select.options[index].value;
			this.getById(value).style.display = "block";
			
//			this.createDivAnswer(event.target.parentElement);
			
	    }.bind(this), false);
	},


	createOptionSingle(text){
		
		var divOption= this.createElement("DIV",undefined ,"choice");
		this.addEventRemoveQA(divOption);
		
		var label=this.createElementLabel(text);
		divOption.appendChild(label);
		
		var optionConf= {placeholder:"text answer", className:'textarea-choice', maxLength:1000, cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(optionConf));
		
		var feedbackConfig= {placeholder:"Write your feedback here ...",className:'textarea-feedback', maxLength:1000, cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(feedbackConfig));
		
		var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts", classlabel:'pts', parent:divOption};
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

	importOptionSingle(xmlChoice){
			
			var score= xmlChoice.getAttribute("score");
			var text= this.getTextSingle(xmlChoice);
			var feedbackNode=xmlChoice.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","feedback");
			var feedbackText;
			if(feedbackNode && feedbackNode.length>0 )
				feedbackText=feedbackNode[0].childNodes[0].nodeValue;
		
		
		var divOption= this.createElement("DIV",undefined ,"choice");
		this.addEventRemoveQA(divOption);
		
		var label=this.createElementLabel("Possible Answer");
		divOption.appendChild(label);
		var optionConf= {placeholder:"text answer", maxLength:1000, value:text,className:'textarea-choice', cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(optionConf));
		
		var feedbackConfig= {placeholder:"Write your feedback here ...", value:feedbackText, className:'textarea-feedback', maxLength:1000, cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(feedbackConfig));
		
		var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts", classlabel:'pts', value:score, parent:divOption};
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


	getTextSingle(xmlChoice){
		var children=xmlChoice.childNodes;
		var text="";
		for(var i =0;i<children.length;i++){
			if(children[i].nodeType == 3)
				text+=(children[i].nodeValue);
//			else
//				text+=children[i].innerHTML;
		}
//		[0].nodeValue;
//		tmp.innerHTML
		return text.trim();
	},



	createOptionMatching(){
		
		var divOption= this.createElement("DIV",undefined ,"choice");
		
		this.addEventRemoveQA(divOption);
		
		var optionConf1= {placeholder:"Matching left side", maxLength:1000, className:'textarea-matching-left', cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(optionConf1));
		
		var optionConf2= {placeholder:"Matching left Side", maxLength:1000,className:'textarea-matching-right', cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(optionConf2));
		
		var feedbackConfig= {placeholder:"Write your feedback here ...", maxLength:1000, className:'textarea-feedback',cols:20,rows:1 };
		divOption.appendChild(this.createTextArea(feedbackConfig));
		
		var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input", classlabel:'pts', parent:divOption};
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



	importOptionMatching(xmlChoice){
		
		var score= xmlChoice.getAttribute("score");
		var textLeft= xmlChoice.getAttribute("mapKey");
		var textRight= xmlChoice.getAttribute("mappedValue");
		
		var feedbackNode=xmlChoice.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","feedback");
		var feedbackText;
		if(feedbackNode && feedbackNode.lenght>0)
			feedbackText=feedbackNode[0].childNodes[0].nodeValue;
		
		
		var divOption= this.createElement("DIV",undefined ,"choice");
		this.addEventRemoveQA(divOption);
		
		
		var optionConf1= {placeholder:"Matching left side", maxLength:1000, value:textLeft, className:'textarea-matching-left',cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(optionConf1));
		
		var optionConf2= {placeholder:"Matching Right Side", maxLength:1000, value:textRight,className:'textarea-matching-right', cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(optionConf2));
		
		var feedbackConfig= {placeholder:"Write your feedback here ...", value: feedbackText, className:'textarea-feedback', maxLength:1000, cols:20,rows:1 };
		divOption.appendChild(this.createTextArea(feedbackConfig));
		
		var inputPTS= {type:"number",textlabel:"pts", value:score, inputClass:"numeric-input-pts", classlabel:"pts", parent:divOption};
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

	addEventRemoveQA(divOption){

		divOption.addEventListener('mouseenter', function(event) {
			event.target.getElementsByClassName("remove-choice")[0].style.display = "inline";
			
	    }.bind(this), false);
		
		divOption.addEventListener('mouseleave', function(event) {
			event.target.getElementsByClassName("remove-choice")[0].style.display = "none";
	    }.bind(this), false);
	},


	createOptionNumeric(){
		
		var divOption= this.createElement("DIV",undefined ,"choice");

		this.addEventRemoveQA(divOption);
		
		this.createSelectTypeNumeric(divOption);
		this.createOptionNumericAux(divOption,"","with error margin");
		return divOption;
		
	},


	importOptionNumeric(xmlchoice,subtype){
		
		var divOption= this.createElement("DIV",undefined ,"choice");

		this.addEventRemoveQA(divOption);
		
		this.createSelectTypeNumeric(divOption ,subtype);
		
		if(subtype=='A'){
			this.createOptionNumericAux(divOption,"","with error margin",xmlchoice.getAttribute("minimumValue"),xmlchoice.getAttribute("maximumValue"));
		}
		else if (subtype=='B'){
			this.createOptionNumericAux(divOption,"between","end",xmlchoice.getAttribute("low"),xmlchoice.getAttribute("high"));
		}
		else if(subtype=='C'){
			this.createOptionNumericAux(divOption,"","with Precision",xmlchoice.getAttribute("minimumValue"),xmlchoice.getAttribute("maximumValue"));
		}
		
		
		return divOption;
		
	},



	createOptionNumericAux(divOption,text1,text2,value,value2){
		

		var inputValue= {type:"number",textlabel:text1,value:value, inputClass:"numeric-input1", parent:divOption};
		this.createElementInputLabel(inputValue);
		
		var erroMargin= {type:"number",textlabel:text2,value:value2, inputClass:"numeric-input2", parent:divOption};
		this.createElementInputLabel(erroMargin);
		
		var feedbackConfig= {placeholder:"Write your feedback here ...", className:'textarea-feedback', maxLength:1000, cols:30,rows:1 };
		divOption.appendChild(this.createTextArea(feedbackConfig));
		
		var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts", classlabel: "pts", parent:divOption};
		this.createElementInputLabel(inputPTS);
		

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
//		var inputValue= {type:"number",textlabel:"Between", inputClass:"numeric-input", parent:divOption};
//		this.createElementInputLabel(inputValue);
	//	
//		var erroMargin= {type:"number",textlabel:"end", inputClass:"numeric-input", parent:divOption};
//		this.createElementInputLabel(erroMargin);
	//	
	//	
//		divOption.appendChild(createButtonRemoveChoice());
	//	
//		divOption.appendChild(this.createElement("BR"));
	//	
	//},

	//createOptionNumericAux(divOption){
	//	
	//	
//		var inputValue= {type:"number",textlabel:"", inputClass:"numeric-input", parent:divOption};
//		this.createElementInputLabel(inputValue);
	//	
//		var erroMargin= {type:"number",textlabel:"with error margin", inputClass:"numeric-input", parent:divOption};
//		this.createElementInputLabel(erroMargin);
	//	
//		var addmoreAnswer = this.createButton(undefined,"X","remove-choice",false);
//		addmoreAnswer.style.display = "none";
	//	
//		addmoreAnswer.addEventListener('click', function(event) {
//			event.path[1].remove();
//	    }.bind(this), false);
	//	
//		divOption.appendChild(addmoreAnswer);
	//	
//		divOption.appendChild(this.createElement("BR"));
	//	
	//},

	createOptionBoolean(text){
		
		var divOption= this.createElement("DIV",undefined ,"choice");
		
		
		var label=this.createElementLabel(text,"bool");
		divOption.appendChild(label);
		
		var feedbackConfig= {placeholder:"Write your feedback here ...", className:'textarea-feedback', maxLength:1000, cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(feedbackConfig));
		
		var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts", classlabel: "pts", parent:divOption};
		this.createElementInputLabel(inputPTS);
		
		divOption.appendChild(this.createElement("BR"));
		
		
		return divOption;
		
		
	},


	importOptionBoolean(xmlChoice){
		
		var score= xmlChoice.getAttribute("score");
		var text= xmlChoice.childNodes[0].nodeValue;
		var divOption= this.createElement("DIV",undefined ,"choice");
		
		var label=this.createElementLabel(text,"bool");
		divOption.appendChild(label);
		
		var feedbackNode=xmlChoice.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","feedback");
		var feedbackText;
		if(feedbackNode)
			feedbackText=feedbackNode[0].childNodes[0].nodeValue;
		
		var feedbackConfig= {placeholder:"Write your feedback here ...", className:'textarea-feedback', maxLength:1000, value:feedbackText, cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(feedbackConfig));
		
		var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts", value:score, classlabel:"pts", parent:divOption};
		this.createElementInputLabel(inputPTS);
		
		divOption.appendChild(this.createElement("BR"));
		
		return divOption;
		
		
	},

	createOptionEssay(){
		
		var divOption= this.createElement("DIV",undefined ,"choice");
		
		
		
		var feedbackConfig= {placeholder:"Write your feedback here ...", className:'input-feedback', maxLength:1000, cols:50,rows:1 };
		divOption.appendChild(this.createTextArea(feedbackConfig));
		
		var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts", classlabel:"pts", parent:divOption};
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
//			divAnswer.appendChild(this.addMoreOption());
			break;
			
		default:
//			alert("Type error");
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

	importChoiceSingle(xmlAnswer, divAnswer){
		this.cretelabelAnswer(divAnswer);
		
//		divAnswer.appendChild(this.createOptionSingle("Correct Answer"));
//		divAnswer.appendChild(this.createOptionSingle("Possible Answer"));
		
		var choices = xmlAnswer.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","choice");
		for(var i = 0; i<choices.length; i++)
			divAnswer.appendChild(this.importOptionSingle(choices[i]));
	},


	createChoiceBoolean(divAnswer){
		this.cretelabelAnswer(divAnswer);
		
		divAnswer.appendChild(this.createOptionBoolean("True"));
		divAnswer.appendChild(this.createOptionBoolean("False"));
	},

	importChoiceBoolean(xmlAnswer,divAnswer){
		this.cretelabelAnswer(divAnswer);
		var choices = xmlAnswer.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","choice");
		for(var i = 0; i<choices.length; i++)
			divAnswer.appendChild(this.importOptionBoolean(choices[i]));
	},


	createChoiceNumeric(divAnswer){
		divAnswer.appendChild(this.createOptionNumeric());
	},

	importChoiceNumeric(xmlAnswer,divAnswer){
		var choices = xmlAnswer.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","choice");
		var subtype=xmlAnswer.getAttribute("subtype");
		for(var i = 0; i<choices.length; i++)
			divAnswer.appendChild(this.importOptionNumeric(choices[i],subtype));
		
//		divAnswer.appendChild(this.importOptionNumeric());
	},




	createChoiceShortAnswer(divAnswer){
		this.cretelabelAnswer(divAnswer);
		
		divAnswer.appendChild(this.createOptionSingle("Correct Answer"));
	},


	importChoiceShortAnswer(xmlAnswer,divAnswer){
		this.cretelabelAnswer(divAnswer);
		var choices = xmlAnswer.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","choice");
		for(var i = 0; i<choices.length; i++)
			divAnswer.appendChild(this.importOptionSingle(choices[i]));
	},

	createChoiceMatching(divAnswer){
		this.cretelabelAnswer(divAnswer);
		
		divAnswer.appendChild(this.createOptionMatching());
		divAnswer.appendChild(this.createOptionMatching());
		divAnswer.appendChild(this.createOptionMatching());
	},


	importChoiceMatching(xmlAnswer,divAnswer){
		this.cretelabelAnswer(divAnswer);
		
		var choices = xmlAnswer.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","choice");
		for(var i = 0; i<choices.length; i++)
			divAnswer.appendChild(this.importOptionMatching(choices[i]));
		
		
		
	},


	importChoiceFillintheBlank(xmlQA, parent,divAnswer){
		
		var label=this.createElementLabel('[Enter Answer variables Above]');
		divAnswer.appendChild(label);
		divAnswer.appendChild(this.createElement("BR"));
		
//		  option.text = "[Enter Answer variables Above]";
		var divQuestion = parent.querySelector(".question");
		var text = (divQuestion.getElementsByTagName("textarea")[0]).value;
		
//		var divAnswer = parent.querySelector(".answer");
		var array = this.getKeyFTB(text);
		this.importSelectFTB(array,divAnswer,xmlQA);
		
	},



	createChoiceFillintheBlank(parent,divAnswer){
		
		var label=this.createElementLabel('[Enter Answer variables Above]');
		divAnswer.appendChild(label);
		divAnswer.appendChild(this.createElement("BR"));
		
//		  option.text = "[Enter Answer variables Above]";
		var divQuestion = parent.querySelector(".question");
		var text = (divQuestion.getElementsByTagName("textarea")[0]).value;
		
		var array = this.getKeyFTB(text);
		this.createSelectFTB(array,parent);
		
//		this.createSelectFTB();
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
		var label=this.createElementLabel(config.textlabel, config.classlabel);
		config.parent.appendChild(label);
		var input =this.createElementInput(config);
		config.parent.appendChild(input);
	},

	importElementInputLabel(config){
		var label=this.createElementLabel(config.textlabel);
		config.parent.appendChild(label);
		var input =this.createElementInput(config);
		config.parent.appendChild(input);
	},

	addElementInputLabel(config){
		var label=this.createElementLabel(config.textlabel);
		config.parent.insertBefore(label,config.referenceNode);
		var input =this.createElementInput(config);
//		config.parent.appendChild(input);
		config.parent.insertBefore(input, config.referenceNode)
	},


	 createElementInput(config){
		
		var input = document.createElement("INPUT");
		if(config.id) input.setAttribute('id', config.id);
		input.setAttribute('type', config.type);
		if(config.inputClass)  input.setAttribute('class', config.inputClass);
		if(config.value) input.setAttribute('value', config.value);
		if(config.placeholder) input.setAttribute('placeholder', config.placeholder);
		if(config.disabled) input.disabled = true;
		if(config.type=='number') input.value =0;
//		if(config.inputClass =='numeric-input-pts-qa' || config.inputClass =="numeric-input-pts-group" ) input.disabled = true;
		
		if( config.inputClass=="numeric-input-pts" ||
			config.inputClass=="numeric-input-pts-qa" ||
			config.inputClass=="numeric-input-pts-group" ||
			config.inputClass=="numeric-input-total-score"){
		
			var that = this;
			input.addEventListener("blur", function(event){
			   
			   var ptsChoices =event.target.parentElement.parentElement.parentElement.getElementsByClassName("numeric-input-pts");
			   var ptsQA = event.target.parentElement.parentElement.parentElement.getElementsByClassName("numeric-input-pts-qa"); //[0].value
			   
			    var total=0;
			   for(var i =0;i<ptsChoices.length;i++){
				   
					total+=parseInt(ptsChoices[i].value);
				}
			   ptsQA[0].value=total;
			   
			 
			   
			   var ptsGroup = event.target.parentElement.parentElement.parentElement.parentElement.getElementsByClassName("numeric-input-pts-group");//.getElementsByClassName("numeric-input-pts-qa"); //[0].value
			   var ptsQAs =   event.target.parentElement.parentElement.parentElement.parentElement.getElementsByClassName("numeric-input-pts-qa"); //[0].value
			   
			   total=0;
			   for(var i =0;i<ptsQAs.length;i++){
				   
					total+=parseInt(ptsQAs[i].value);
				}
			   ptsGroup[0].value=total; 
			   
			   
			   
			   var ptsTotal = event.target.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement.getElementsByClassName("numeric-input-total-score");//.getElementsByClassName("numeric-input-pts-qa"); //[0].value
			   var ptsGroup =   event.target.parentElement.parentElement.parentElement.parentElement.parentElement.getElementsByClassName("numeric-input-pts-group"); //[0].value
			   
			   total=0;
			   console.log(ptsGroup);
			   for(var i =0;i<ptsGroup.length;i++){
				   
					total+=parseInt(ptsGroup[i].value);
				}
			   ptsTotal[0].value=total; 
			   
			   that.onChange(that.exportJSON());
			});
		}	
		
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
//		    butKey.addEventListener('click', function(event) {
////				this.changeValueButton2(butKey.id);
//				alert(123);
//		    }.bind(this), false);
	    
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
		if(config.value)textArea.value = config.value;
		if(config.className)textArea.className = config.className;
		
		return textArea
		
	},

	getKeyFTB(txt){
//		var txt = "I expect five hundred dollars ($500). and new brackets ($600)";
		var list=[];
		var newTxt = txt.split('[');
		for (var i = 1; i < newTxt.length; i++) {
			list.push(newTxt[i].split(']')[0]);
		}
		return list;
		
	},

	getById(id){
		return document.getElementById(id);
	},

	 geneteID(){},
	 
	 
	 
	removeALLGroups(){
		this.removeAllChildren(this.divgroups);
	},


	createButtonImport(){
		 var butKey =document.createElement("INPUT");
	   butKey.id='btninput';
	   butKey.value=butText;
	   butKey.type='file';
	    var butText = document.createTextNode("Inport"); 
	   butKey.appendChild(butText);
	   
	   butKey.addEventListener('change', function(event) {
			this.readSingleFile(event,this);
	   }.bind(this), false);
		return butKey;
		
	},


	readSingleFile(evt,editor) {
	   //Retrieve the first (and only!) File from the FileList object
	   var f = evt.target.files[0]; 
	   if (f) {
	     var r = new FileReader();
	     r.onload = function(e) { 
		      var contents = e.target.result;
//		      createEditor(contents); 
		      this.XMLParser= new XMLParser(contents);
		      
		      var importEditor= new ImportEditor(editor, this.XMLParser);
		      importEditor.init();
		      return contents;
	     }
	     r.readAsText(f);
	   } else { 
	     alert("Failed to load file");
	   }
	 },
	 
	 
	 
	 
	 
	 importGroup(xmlGroup){
			
		
			var divgroup= this.createElement("DIV",undefined ,"group");
			this.divgroups.appendChild(divgroup);
			
			var configGroup= this.createElement("DIV",undefined ,"config-group");
			
			var inputName = {type:"text",placeholder:"name",textlabel:"Group Name", inputClass:"group-name",value:xmlGroup.getAttribute("name"), parent:configGroup};
			this.createElementInputLabel(inputName);
			
			var inputScore = {type:"number",textlabel:"Score", inputClass:"numeric-input-pts-group", disabled:'true', value:xmlGroup.getAttribute("maxScore"), parent:configGroup};
			this.createElementInputLabel(inputScore);
			
			var inputNQ= {type:"number",textlabel:"Number Question", inputClass:"numeric-input group-nq", value:xmlGroup.getAttribute("numberQuestion"), parent:configGroup};
			this.createElementInputLabel(inputNQ);
			
			
			var inputShuffle = {type:"checkbox",textlabel:"Shuffle", inputClass:"group-shuffe", value:xmlGroup.getAttribute("maxScore"), parent:configGroup};
			this.createElementInputLabel(inputShuffle); 		
			
			
			var newQA=this.createButton("newqa","New QA","new",false);
			newQA.addEventListener('click', function(event) {
				this.addQAButton(event);
		    }.bind(this), false);
			configGroup.appendChild(newQA);
			
			
			var removeGroup = this.createButton(undefined,"X","btn-display",false);
//			removeGroup.style.display = "none";
			
			removeGroup.addEventListener('click', function(event) {
				event.target.parentElement.parentElement.remove();
		    }.bind(this), false);
			
			configGroup.appendChild(removeGroup);
			
			divgroup.appendChild(configGroup);
			
			
			
			
			var qas=xmlGroup.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","QA");
			for(var i =0;i<qas.length;i++){
				this.importElementQA(qas[i],divgroup)
			}
			
			
			
//			divgroup.appendChild(this.createElementQA());
		},
		
		
		
		
		importElementQA(xmlQA,parent){
			
			var type = xmlQA.getAttribute("type");
			var divQA= this.createElement("DIV",undefined ,"qa");
			
//			xmlGroup.getAttribute("name")
			var inputName = {type:"text",placeholder:"Question 1",textlabel:"Question Name", inputClass:"text-input", parent:divQA};
			this.createElementInputLabel(inputName);
			this.createSelectTypeQuestion(divQA,type);
			var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts-qa",disabled:'true', classlabel:"pts", parent:divQA};
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
			
			
//			
			this.importDivQuestion(divQA,xmlQA);
//			
//			this.createDivAnswer(divQA);
			
			var answer = xmlQA.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","question")[0];
			this.importDivAnswer(xmlQA.getAttribute("type"),xmlQA,divQA)

			parent.appendChild(divQA);
		},
	 
		

		importDivQuestion(parent,xmlQA){
			var type=this.getValueSelect(parent);
			
			var divQuestion= this.createElement("DIV",undefined ,"question");
			divQuestion.appendChild(this.createElement("BR"));
			
			var label=this.createElementLabel('Question');
			divQuestion.appendChild(label);
			divQuestion.appendChild(this.createElement("BR"));
			
			var e=xmlQA.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","question")[0];
			
			var cln = e.outerHTML;;
//			var s = "<q:question xmlns:q=\"http://mooshak.dcc.fc.up.pt/quiz\">";
//			var s1 = "<q:question xmlns:q=\"http://mooshak.dcc.fc.up.pt/quiz\"\/>";
//			var s2 = "</q:question>";
//			str = cln.replace(s, "").replace(s2, "").replace(s1, "").replace('\\t', "")
			
			var  valueQuestion="";
			if(xmlQA.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","question")[0].childNodes.length>0)
				valueQuestion=xmlQA.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","question")[0].childNodes[0].nodeValue;
			var questionNode= xmlQA.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","question");
			
			var valueText= valueQuestion.replace('\t','');
			
			if(type=="FillintheBlank"){
				valueText=this.getTextFTB(questionNode);
			}
			var questionConf= {placeholder:"Write your question here ...", value:valueText.trim(),className:'input-question', maxLength:1000, cols:60,rows:4 };
			var textArea=this.createTextArea(questionConf);
			
			
			
			textArea.addEventListener('blur', function(event) {
				parent=event.target.parentElement.parentElement;
				
				if(type=="FillintheBlank"){
					var array = this.getKeyFTB(event.target.value);
					this.importSelectFTB(array,parent,xmlQA);
				}
				
		    	}.bind(this), false);
			
			divQuestion.appendChild(textArea);
			parent.appendChild(divQuestion);
			
		},

		getTextFTB(xmlChoice){
				var children=xmlChoice[0].childNodes;
				var text="";
				for(var i =0;i<children.length;i++){
					if(children[i].nodeType == 3)
						text+=(children[i].nodeValue);
					else if(children[i].nodeType == 1){
						if(children[i].nodeName=='q:missingWord')
							text+='['+children[i].innerHTML+']';
				}
				}
//				[0].nodeValue;
//				tmp.innerHTML
				return text.trim();
			
			
		},
		importDivAnswer(type,xmlAnswer,parent){
			
			
			var divAnswer= this.createElement("DIV",undefined ,"answer");
			parent.appendChild(divAnswer);
			
			
			switch (type.toLowerCase()) {
			case 'single':
				this.importChoiceSingle(xmlAnswer,divAnswer);
				divAnswer.appendChild(this.addMoreOption());
				break;
			case 'boolean':
				this.importChoiceBoolean(xmlAnswer, divAnswer);
				break;
			case 'multiple':
				this.importChoiceSingle(xmlAnswer,divAnswer);
				divAnswer.appendChild(this.addMoreOption());
				break;
			case 'essayquestion':
				this.createChoiceEssay(divAnswer);
				break;
			case 'numeric':
				this.importChoiceNumeric(xmlAnswer,divAnswer);
				divAnswer.appendChild(this.addMoreOption());
				break;
			case 'shortanswer':
				this.importChoiceShortAnswer(xmlAnswer,divAnswer);
				divAnswer.appendChild(this.addMoreOption());
				break;
			case 'matching':
				this.importChoiceMatching(xmlAnswer,divAnswer);
				divAnswer.appendChild(this.addMoreOption());
				break;
			case 'fillintheblank':
				this.importChoiceFillintheBlank(xmlAnswer,parent,divAnswer);
				break;
				
			default:
			}
			
		},
		
		 exportJSON(){
			 var groups= this.div.getElementsByClassName("group");
			 var listGroups=[];
			 for(var i=0;i<groups.length;i++){
				 listGroups.push(this.getGroup( groups[i]));
			 }
			 
			 var result = {config:this.getQuizConfig(), groups:listGroups};
			 
			 console.log(result);
			 return result;
		},
		
		getQuizConfig(){
			
			var divConfig = this.div.getElementsByClassName("configuration")[0]; 
			return {
				name: divConfig.getElementsByClassName("input-name-cong")[0].value,
				duration: divConfig.getElementsByClassName("input-name-duration")[0].value,
				revision: divConfig.getElementsByClassName("input-revision")[0].checked,
				shuffle: divConfig.getElementsByClassName("input-shuffle")[0].checked,
				
			}
		},
		
		
		importConfigXML(config){
			
			var divConfig = this.div.getElementsByClassName("configuration")[0]; 
			
			divConfig.getElementsByClassName("input-name-cong")[0].value = config.getAttribute("name");
			divConfig.getElementsByClassName("input-name-duration")[0].value = config.getAttribute("duration");
			divConfig.getElementsByClassName("input-revision")[0].value = config.getAttribute("revision");
			divConfig.getElementsByClassName("input-shuffle")[0].value = config.getAttribute("shuffle"); 
		},
		
		getGroup(group){
			
			var qas = group.getElementsByClassName("qa");
			var divConfig=group.getElementsByClassName("config-group")[0];
			var groupName = divConfig.getElementsByClassName("group-name")[0].value;
			var groupScore = divConfig.getElementsByClassName("numeric-input-pts-group")[0].value;
			var groupNQ = divConfig.getElementsByClassName("group-nq")[0].value;
			var groupShuffe = divConfig.getElementsByClassName("group-shuffe")[0].checked;
			
			var listQA=[];
			for(var i=0;i<qas.length;i++){
				listQA.push(this.getQA(qas[i]));
			}
			
			return {
				'id' : 0,
				'name' : divConfig.getElementsByClassName("group-name")[0].value,
				'numberQuestion' : divConfig.getElementsByClassName("group-nq")[0].value,
				'score' : divConfig.getElementsByClassName("numeric-input-pts-group")[0].value,
				'shuffle' : divConfig.getElementsByClassName("group-shuffe")[0].checked,
				'listQA':listQA,
			}
		},
//		id="d2e1" name="superquiz" numberQuestion="6" score="0" shuffle="true"
//		var xml  = parser.parseFromString('<?xml version="1.0" encoding="utf-8"?><root></root>', "application/xml"
		
		getQA(qa){
			var question = qa.getElementsByClassName("question");
			var answer = qa.getElementsByClassName("answer");
			
//			console.log(this.getQuestion(question));
//			var xmlDoc = document.implementation.createDocument(null, "books");
//			var select=event.target;
			
			
			var type = this.getValueSelect(qa);
			
			this.getAnswer(answer,type);
			
			return {'id':0,
				'type' : type,
				'question':this.getQuestion(question).question,
				'answer':this.getAnswer(answer,type).answer,
				

			}
			
		},
		
		getQuestion(question){
			var questionArea = question[0].getElementsByTagName("textarea")[0];
			var txt = (questionArea.value).replace('/\r?t|\r/','');
			return {'question': txt };
		},
		
		getAnswer(answer,type){
			
			switch (type.toLowerCase()) {
			case 'single':
				return {answer : this.exportChoiceSingle(answer)};
				
			case 'boolean':
				return {answer : this.exportChoiceBooleam(answer)};
				
			case 'multiple':
				return {answer : this.exportChoiceSingle(answer)};
				
			case 'essayquestion':
				return {answer : this.exportChoiceEssayQuestion(answer)};
				
			case 'numeric':
//				var obj={answer : this.exportChoiceNumeric(answer)};
//				console.log(obj)
				return  {'answer' : this.exportChoiceNumeric(answer)};
				
			case 'shortanswer':
				return {answer : this.exportChoiceSingle(answer)};
				
			case 'matching':
				return {'answer' : this.exportChoiceMatching(answer)};
				
			case 'fillintheblank':
				return  {'answer' : this.exportChoiceFillinTheBlank(answer)};
				
			default:
			}
		},
		
	 
		exportChoiceSingle(answer){
			var choices = answer[0].getElementsByClassName("choice");
			var result=[];
			for(var i =0;i<choices.length;i++){
				var choice= choices[i];
//				console.log(choice.getElementsByClassName("numeric-input-pts")[0].value);
				result.push({
					id:0,
					text:(choice.getElementsByClassName("textarea-choice")[0].value),
					score:choice.getElementsByClassName("numeric-input-pts")[0].value,
					feedback:choice.getElementsByClassName("textarea-feedback")[0].value,
				});
			}
			
			return result;
			
		},

		
		exportChoiceBooleam(answer){
			var choices = answer[0].getElementsByClassName("choice");
			var result=[];
			for(var i =0;i<choices.length;i++){
				var choice= choices[i];
				result.push({
					id:0,
					'bool':(choice.getElementsByClassName("bool")[0].innerHTML).trim(),
					'score':choice.getElementsByClassName("numeric-input-pts")[0].value,
					'feedback':choice.getElementsByClassName("textarea-feedback")[0].value,
				});
			}
			
			return result;
			
		},
		
		exportChoiceEssayQuestion(answer){
			var choices = answer[0].getElementsByClassName("choice");
			var result=[];
			for(var i =0;i<choices.length;i++){
				var choice= choices[i];
				result.push({
					'id':0,
					'feedback':choice.getElementsByClassName("textarea-feedback")[0].value,
					'score':choice.getElementsByClassName("numeric-input-pts")[0].value,
				});
			}
			return result;
		},
		
		
		exportChoiceMatching(answer){
			var choices = answer[0].getElementsByClassName("choice");
			var result=[];
			for(var i =0;i<choices.length;i++){
				var choice= choices[i];
				result.push({
					'id':0,
					'key':choice.getElementsByClassName("textarea-matching-left")[0].value,
					'mappedValue':choice.getElementsByClassName("textarea-matching-right")[0].value,
					'score':choice.getElementsByClassName("numeric-input-pts")[0].value,
					'feedback':choice.getElementsByClassName("textarea-feedback")[0].value,
				});
			}
			
			return result;
		},
		
		
		exportChoiceFillinTheBlank(answer){
			
			var divs=answer[0].getElementsByClassName("select-options");
			
			var result=[];
			for(var i =0;i<divs.length;i++){
				var div= divs[i];
			
				result.push(
					this.exportChoiceFillinTheBlankAux(div)
				);
			}
			
//			console.log(result);
			return result;
		},
		
		exportChoiceFillinTheBlankAux(answer){
//			console.log(answer.id);
			var choices = answer.getElementsByClassName("choice");
			
			var result=[];
			for(var i =0;i<choices.length;i++){
				var choice= choices[i];
				result.push({
					id:0,
					text:(choice.getElementsByClassName("textarea-choice")[0].value),
					score:choice.getElementsByClassName("numeric-input-pts")[0].value,
					feedback:choice.getElementsByClassName("textarea-feedback")[0].value,
					
					
				});
			}
			//var res = {answer_: result};
			
			return { 'id': answer.id, 'answer_': result}
		},
		
		
	//exportChoiceFillinTheBlankAux(answer){
//			
//			var choices = answer.getElementsByClassName("choice");
//			
//			var result=[];
//			for(var i =0;i<choices.length;i++){
//				var choice= choices[i];
//				result.push({
//					id:0,
//					text:(choice.getElementsByClassName("textarea-choice")[0].value),
//					score:choice.getElementsByClassName("numeric-input-pts")[0].value,
//					feedback:choice.getElementsByClassName("textarea-feedback")[0].value,
//					
//					
//				});
//			}
//			return result;
//		},
		
		
		
		exportChoiceNumeric(answer){
			var choices = answer[0].getElementsByClassName("choice");
			var result=[];
			for(var i =0;i<choices.length;i++){
				var choice= choices[i];
				
				var selectValue= this.getValueSelect(choice);
				
				if(selectValue == 'Exact Answer'){
					result.push({
						id:0,
						minimumValue:(choice.getElementsByClassName("numeric-input1")[0].value),
						maximumValue:(choice.getElementsByClassName("numeric-input2")[0].value),
						score:choice.getElementsByClassName("numeric-input-pts")[0].value,
						feedback:choice.getElementsByClassName("textarea-feedback")[0].value,
					});
				}
				else if( selectValue == 'Answer in the Range'){
					result.push({
						id:0,
						low:(choice.getElementsByClassName("numeric-input1")[0].value),
						high:(choice.getElementsByClassName("numeric-input2")[0].value),
						score:choice.getElementsByClassName("numeric-input-pts")[0].value,
						feedback:choice.getElementsByClassName("textarea-feedback")[0].value,
						
					});
				}
				
				else if( selectValue == 'Answer with Precision'){
					
					result.push({
						id:0,
						minimumValue:(choice.getElementsByClassName("numeric-input1")[0].value),
						maximumValue:(choice.getElementsByClassName("numeric-input2")[0].value),
						score:choice.getElementsByClassName("numeric-input-pts")[0].value,
						feedback:choice.getElementsByClassName("textarea-feedback")[0].value,
						
					});
				}
				
			}
			
			return result;
			
		},
		
		exportNumericExactAnswer(answer){
			var choices = answer[0].getElementsByClassName("choice");
			var result=[];
			for(var i =0;i<choices.length;i++){
				var choice= choices[i];
				result.push({
					id:0,
					text:(choice.getElementsByClassName("textarea-choice")[0].value),
					score:choice.getElementsByClassName("numeric-input-pts")[0].value,
					feedback:choice.getElementsByClassName("textarea-feedback")[0].value,
				});
			}
			
			return result;
			
		},
		
		 /***************************IMPORT XML******************************/

		 importXML(xml){
			 this.xmlParser= new XMLParser(xml);
			 console.log('importXML')
			 console.log(this.xmlParser.getConfig())
		   
		 		this.removeALLGroups();
		 		this.importConfigXML(this.xmlParser.getConfig());
		 		
		 		var groups = this.xmlParser.getGroups();
		 		for (var i=0; i<groups.length;i++){
		 			this.importGroup(groups[i]);
		 		}
		     
		 },
		 
		 
		 
		 /***************************IMPORT JSON******************************/
		 
		 exportNumericExactAnswer(answer){
				var choices = answer[0].getElementsByClassName("choice");
				var result=[];
				for(var i =0;i<choices.length;i++){
					var choice= choices[i];
					result.push({
						id:0,
						text:(choice.getElementsByClassName("textarea-choice")[0].value),
						score:choice.getElementsByClassName("numeric-input-pts")[0].value,
						feedback:choice.getElementsByClassName("textarea-feedback")[0].value,
					});
				}
				
				return result;
				
			},
			
			
			
			
			
			importJSON(){
				this.removeALLGroups();
				var json = JSON.parse(text);
				var config = json.config;
				var groups = json.groups;
				
				var divConfig = this.div.getElementsByClassName("configuration")[0]; 
				
				divConfig.getElementsByClassName("input-name-cong")[0].value = config.name;
				divConfig.getElementsByClassName("input-name-duration")[0].value =config.duration;
				divConfig.getElementsByClassName("input-revision")[0].value =config.revision;
				divConfig.getElementsByClassName("input-shuffle")[0].value =config.shuffle;
				
				
				for(var i in groups){
					this.importJSONGroup(groups[i]);
				}
				
//				alert(json);
			},
			
			importJSONGroup(group){
				
					var id = group.id;
					var name = group.name;
					var numberQuestion = group.numberQuestion;
					var score = group.score;
					var shuffle = group.shuffle;
					var listQA = group.listQA;
				
					var divgroup= this.createElement("DIV",undefined ,"group");
					this.divgroups.appendChild(divgroup);
					
					var configGroup= this.createElement("DIV",undefined ,"config-group");
					
					var inputName = {type:"text",placeholder:"name",textlabel:"Group Name", inputClass:"group-name",value:name, parent:configGroup};
					this.createElementInputLabel(inputName);
					
					var inputScore = {type:"number",textlabel:"Score", inputClass:"numeric-input-pts-group", disabled:'true', value:score, parent:configGroup};
					this.createElementInputLabel(inputScore);
					
					var inputNQ= {type:"number",textlabel:"Number Question", inputClass:"numeric-input group-nq", value:numberQuestion, parent:configGroup};
					this.createElementInputLabel(inputNQ);
					
					
					var inputShuffle = {type:"checkbox",textlabel:"Shuffle", inputClass:"group-shuffe", value:shuffle, parent:configGroup};
					this.createElementInputLabel(inputShuffle); 		
					
					
					var newQA=this.createButton("newqa","New QA","new",false);
					newQA.addEventListener('click', function(event) {
						this.addQAButton(event);
				    }.bind(this), false);
					configGroup.appendChild(newQA)
					
					
					var removeGroup = this.createButton(undefined,"X","btn-display",false);
					
					removeGroup.addEventListener('click', function(event) {
						event.target.parentElement.parentElement.remove();
				    }.bind(this), false);
					
					configGroup.appendChild(removeGroup);
					
					divgroup.appendChild(configGroup);
					
					
					
					
//					var qas=xmlGroup.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","QA");
//					for(var i =0;i<qas.length;i++){
//						this.importElementQA(qas[i],divgroup)
//					}
					
					
					for(var i in listQA){
						this.importJSONQA(listQA[i],divgroup)
					}
					
//				
//				alert(json);
			},
			
			importJSONQA(qa, parent){
				
				var id = qa.id;
				var type = qa.type;
				var question = qa.question;
				var answer = qa.answer;
				var score = qa.score;
			

				var divQA= this.createElement("DIV",undefined ,"qa");
				
//				xmlGroup.getAttribute("name")
				var inputName = {type:"text",placeholder:"Question 1",textlabel:"Question Name",value:name, inputClass:"text-input", parent:divQA};
				this.createElementInputLabel(inputName);
				this.createSelectTypeQuestion(divQA,type);
				var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts-qa",value:score,disabled:'true', classlabel:"pts", parent:divQA};
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
				
				
				this.importJSONQuestion(divQA,qa);
				
				this.importJSONAnswer(type,answer,divQA)
				parent.appendChild(divQA);

				
				
				
			},
			
			
			importJSONQuestion(parent,qa){
				var type=qa.type;
				
				var divQuestion= this.createElement("DIV",undefined ,"question");
				divQuestion.appendChild(this.createElement("BR"));
				
				var label=this.createElementLabel('Question');
				divQuestion.appendChild(label);
				divQuestion.appendChild(this.createElement("BR"));
				
				var valueText= qa.question;//valueQuestion.replace('\t','');
				
				var questionConf= {placeholder:"Write your question here ...", value:valueText.trim(),className:'input-question', maxLength:1000, cols:100,rows:4 };
				var textArea=this.createTextArea(questionConf);
				
				
				textArea.addEventListener('blur', function(event) {
					parent=event.target.parentElement.parentElement;
					
					var type=this.getValueSelect(parent);
					if(type=="FillintheBlank"){
						var array = this.getKeyFTB(event.target.value);
						this.createSelectFTB(array,parent);
					}
			    	}.bind(this), false);
				
				divQuestion.appendChild(textArea);
				parent.appendChild(divQuestion);
				
			},
			
			importJSONAnswer(type, answer,parent){
				var divAnswer= this.createElement("DIV",undefined ,"answer");
				parent.appendChild(divAnswer);
				
				switch (type.toLowerCase()) {
				case 'single':
					this.importJSONAnswerSingle(answer,divAnswer);
					divAnswer.appendChild(this.addMoreOption());
					break;
				case 'multiple':
					this.importJSONAnswerSingle(answer,divAnswer);
					divAnswer.appendChild(this.addMoreOption());
					break;
				case 'boolean':
					this.importJSONAnswerBoolean(answer, divAnswer);
					divAnswer.appendChild(this.addMoreOption());
					break;
				case 'numeric':
					this.importJSONAnswerNumeric(answer,divAnswer);
					divAnswer.appendChild(this.addMoreOption());
					break;
				case 'shortanswer':
					this.importJSONAnswerSingle(answer,divAnswer);
					divAnswer.appendChild(this.addMoreOption());
					break;
				case 'matching':
					this.importJSONAnswerMatching(answer,divAnswer);
					divAnswer.appendChild(this.addMoreOption());
					break;
				case 'fillintheblank':
					this.importJSONChoiceFillInTheBlank(answer, parent);
					break;
				case 'essayquestion':
					divAnswer.appendChild(this.importJSONChoiceEssay(answer[0]));
					break;
					
				default:
					
					break;
				}
			},
			
			importJSONAnswerSingle(answers, divAnswer){
				this.cretelabelAnswer(divAnswer);
					
				
				for(var i in answers){
					divAnswer.appendChild(this.importJSONChoiceSingle(answers[i]));
				}
			},
			
			importJSONAnswerMatching(answers, divAnswer){
				this.cretelabelAnswer(divAnswer);
					
				
				for(var i in answers){
					divAnswer.appendChild(this.importJSONChoiceMatching(answers[i]));
				}
			},
			
			importJSONAnswerBoolean(answers, divAnswer){
				this.cretelabelAnswer(divAnswer);
					
				
				for(var i in answers){
					divAnswer.appendChild(this.importJSONChoiceBoolean(answers[i]));
				}
			},
			
			
			importJSONAnswerNumeric(answers, divAnswer){
//				var choices = xmlAnswer.getElementsByTagNameNS("http://mooshak.dcc.fc.up.pt/quiz","choice");
//				var subtype=xmlAnswer.getAttribute("subtype");

				for(var i in answers){
					divAnswer.appendChild(this.importJSONChoiceNumeric(answers[i]));
				}
				
//				divAnswer.appendChild(this.importOptionNumeric());
			},
			
//			importJSONAnswerEssay(answer){
//				this.importJSONChoiceEssay()
//			},
			
			
			importJSONChoiceSingle(option){
				
				var id = option.id;
				var text = option.text;
				var score = option.score;
				var feedback = option.feedback;

			var divOption= this.createElement("DIV",undefined ,"choice");
			this.addEventRemoveQA(divOption);
			
			var label=this.createElementLabel("Possible Answer");
			divOption.appendChild(label);
			var optionConf= {placeholder:"text answer", maxLength:1000, value:text,className:'textarea-choice', cols:50,rows:1 };
			divOption.appendChild(this.createTextArea(optionConf));
			
			var feedbackConfig= {placeholder:"Write your feedback here ...", value:feedback, className:'textarea-feedback', maxLength:1000, cols:50,rows:1 };
			divOption.appendChild(this.createTextArea(feedbackConfig));
			
			var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts", classlabel:'pts', value:score, parent:divOption};
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
			
			
			importJSONChoiceBoolean(option){
				
				var id = option.id;
				var text = option.bool;
				var score = option.score;
				var feedbackText = option.feedback;
				
				
				var divOption= this.createElement("DIV",undefined ,"choice");
				
				var label=this.createElementLabel(text,"bool");
				divOption.appendChild(label);
				
				var feedbackConfig= {placeholder:"Write your feedback here ...", className:'textarea-feedback', maxLength:1000, value:feedbackText, cols:50,rows:1 };
				divOption.appendChild(this.createTextArea(feedbackConfig));
				
				var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts", value:score, classlabel:"pts", parent:divOption};
				this.createElementInputLabel(inputPTS);
				
				divOption.appendChild(this.createElement("BR"));
				
				return divOption;
				
				
			},
			
			
			
			importJSONChoiceMatching(option){
				
				var id = option.id;
				var textLeft = option.key;
				var textRight = option.mappedValue;
				var score = option.score;
				var feedbackText = option.feedback;
				
					
					var divOption= this.createElement("DIV",undefined ,"choice");
					this.addEventRemoveQA(divOption);
					
					
					var optionConf1= {placeholder:"Matching left side", maxLength:1000, value:textLeft, className:'textarea-matching-left',cols:50,rows:1 };
					divOption.appendChild(this.createTextArea(optionConf1));
					
					var optionConf2= {placeholder:"Matching Right Side", maxLength:1000, value:textRight,className:'textarea-matching-right', cols:50,rows:1 };
					divOption.appendChild(this.createTextArea(optionConf2));
					
					var feedbackConfig= {placeholder:"Write your feedback here ...", value: feedbackText, className:'textarea-feedback', maxLength:1000, cols:20,rows:1 };
					divOption.appendChild(this.createTextArea(feedbackConfig));
					
					var inputPTS= {type:"number",textlabel:"pts", value:score, inputClass:"numeric-input-pts", classlabel:"pts", parent:divOption};
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
			
			importJSONChoiceNumeric(choice){
			
				  
				var divOption= this.createElement("DIV",undefined ,"choice");
			
				this.addEventRemoveQA(divOption);
				
				this.createSelectTypeNumeric(divOption ,choice.subtype);
				
				
				
				if(choice.subtype=='exact'){ 
					this.createOptionNumericAux(divOption,"","with error margin",choice.minimumValue,choice.maximumValue,choice.score, choice.feedback);
				}
				else if (choice.subtype=='range'){
					this.createOptionNumericAux(divOption,"between","end",choice.low,choice.high,choice.score, choice.feedback);
				}
				else if(choice.subtype=='precision'){
					this.createOptionNumericAux(divOption,"","with Precision",choice.minimumValue,choice.maximumValue,choice.score, choice.feedback);
				}
			
			
			return divOption;
			},
			
			
			importJSONChoiceEssay(choice){
				
				var divOption= this.createElement("DIV",undefined ,"choice");
				
				var feedbackConfig= {placeholder:"Write your feedback here ...", className:'input-feedback',value:choice.feedback, maxLength:1000, cols:50,rows:1 };
				divOption.appendChild(this.createTextArea(feedbackConfig));
				
				var inputPTS= {type:"number",textlabel:"pts", inputClass:"numeric-input-pts", value:choice.score, classlabel:"pts", parent:divOption};
				this.createElementInputLabel(inputPTS);
				
				divOption.appendChild(this.createElement("BR"));
				
				return divOption;
				
			},
			
			importJSONChoiceFillInTheBlank(answers, parent){
				
				var question=parent.getElementsByClassName("question")[0].getElementsByClassName("input-question")[0].value; //.value
				
				var divAnswer= parent.getElementsByClassName("answer")[0]
				divAnswer.appendChild(this.createElementLabel("[Enter Answer variables Above]"));
				
				
				for(var i in answers){
					this.importJSONSelectFTB(answers[i],divAnswer)
				}
			},
			
			
			importJSONSelectFTB(answers,parent){
				
				
				var id= answers.id;
				var _answers=answers.answer_;
				
				var divQuestion= this.createElement("DIV",id ,"select-options");
					
					var label=this.createElementLabel(id);
					divQuestion.appendChild(label);
					parent.appendChild(divQuestion);
				
				for(var i in _answers){
					
					divQuestion.appendChild(this.importJSONChoiceSingle(_answers[i]));
					
					var addmoreAnswer = this.createButton(undefined,"Add more Answer","add-answer",false);
					addmoreAnswer.addEventListener('click', function(event) {
						this.addmorePossibleAnswerFTB(event)
				    }.bind(this), false);
					
					divQuestion.appendChild(addmoreAnswer);
			
				}
				
			},
			

	}

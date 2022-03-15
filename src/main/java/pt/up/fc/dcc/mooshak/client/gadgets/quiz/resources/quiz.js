var centesimas = 5;
var segundos = 5;
var minutos = 5;
var horas = 5;
var distance=100000;
var progress;
var qaLine;
function inicio () {
	control = setInterval(cronometro,10);
}

function cronometro () {
	
	// var days = Math.floor(distance / (1000 * 60 * 60 * 24));
	  var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
	  var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
	  var seconds = Math.floor((distance % (1000 * 60)) / 1000);
	  var centesimas = Math.floor((distance % (1000 * 60)) / 100);
	  
		if (seconds < 10) { seconds = "0"+seconds }
		Segundos.innerHTML = ":"+seconds;

		if (minutes < 10) { minutes = "0"+minutes }
		Minutos.innerHTML = ":"+minutes;

		if (hours < 10) { hours = "0"+hours }
		Horas.innerHTML = hours;
		
		distance-=10;
		progress.value=distance;
		if(distance==0)
			stop();
}

function onload (){
//	progress = document.getElementById("_progress");
//	progress.max=distance;
//	progress.value=distance;
	qaLine=$("question-line");
//	inicio();
	getGroup();
}

function stop () {
	clearInterval(control);
}

function submit () {
	getQuizzes();
}


function getParent(element,className){
	while (element) {
		if(element.classList.contains(className))
			return element;
		element=element.parentElement;
		
	}
}
function getGroup(){
	var x = document.querySelectorAll('.group'); //document.getElementsByClassName("group");
	var i;
	var text="";
//	while (qaLine.firstChild) {
//		qaLine.removeChild(qaLine.firstChild);
//	}
	for (var i = 0; i < x.length; i++) {
		createAncora("Group"+(i+1), x[i].id);
		
		var qa =x[i].querySelectorAll('.qa');
		
		for (var k = 0; k < qa.length; k++) {
			createAncora("Q"+ (k+1), qa[k].id);
		}

	}
	
//	qaLine.innerHTML = text;
	
}



function createAncora(text, id){
	 var createA = document.createElement('a');
     var createAText = document.createTextNode(text);
     createA.setAttribute('href', "#"+id)
      createA.setAttribute('name', id)
     createA.setAttribute('id', "link_"+id);
//     createA.setAttribute('name', "link_"+id);
     if(text.startsWith("Group"))
    	 createA.setAttribute('class', "ancora-group");
     else
    	 createA.setAttribute('class', "ancora-question");
     createA.appendChild(createAText);
     if(checkDisplayQLine())
    	 createA.style.display="inline-block"
//    	 console.log(qaLine.childNodes.length)
	 createA.onclick=(function(event){
		 event.preventDefault();
		 $(this.name).scrollIntoView(true);
		 $("quiz").scrollTop -=  $("quiz-head").offsetHeight+20;
	    
	 }); 
     qaLine.appendChild(createA);
     
}

function $(id){
	return document.getElementById(id);
}

function addClass(id,className){
	$(id).classList.add(className); 
}

function removeClass(id,className){
	$(id).classList.remove(className); 
}



function inputSingle(e){
	
	addClass("link_"+e.name,"animate"); 
	addClass(e.name,"ckeked"); 
	checkGroupComplete(getParent(e,"group"));
//	console.log("e.value "+e.checked)
	
//	console.log(getWidthQuestionLine());
}

function inputBoolean(e){
	
	addClass("link_"+e.name,"animate"); 
	addClass(e.name,"ckeked"); 
	checkGroupComplete(getParent(e,"group"));
}



function inputMultiple(e){
	if(isRadioSeletected(e.name)){
		addClass("link_"+e.name,"animate");
		addClass(e.name,"ckeked"); 
	}
	else
		{
		removeClass(e.name, "ckeked"); 
		removeClass("link_"+e.name, "animate");
		}
	
	checkGroupComplete(getParent(e,"group"));
}

function inputShort(e){
	
	var id = $(e.name ).parentElement.parentElement.id;
		if (e.value != ""){
			addClass("link_"+id,"animate");
			addClass(id, "ckeked");
			
		}
		else {
			removeClass("link_"+id, "animate");
			removeClass(id,"ckeked");
		}
		
		checkGroupComplete(getParent(e,"group"));
}

function inputNumeric(e){
	
	var id = $(e.name ).id;
	
		if (e.value != ""){
			addClass("link_"+id, "animate");
			addClass(id, "ckeked");
		}
		else {
			removeClass("link_"+id, "animate");
			removeClass(id, "ckeked");
		}
		
		checkGroupComplete(getParent(e,"group"));
}

function inputMatching(e){

	var id = e.parentElement.parentElement.parentElement.parentElement.parentElement.id;
	var qa=e.parentElement.parentElement.parentElement.parentElement.parentElement;
	
	var inputs =qa.getElementsByTagName("INPUT");
	
	for (var i = 0; i < inputs.length; i++) {
		if(inputs[i].value==""){
			removeClass("link_"+id, "animate");
			removeClass(id, "ckeked");
			return;
		}
	}
	
	addClass("link_"+id, "animate");
	addClass(id, "ckeked");
	checkGroupComplete(getParent(e,"group"));
}


function inputfillInTheBlank(e){
	
	var id = e.parentElement.parentElement.id;
	addClass("link_"+id,"animate");
	addClass(id, "ckeked");
	checkGroupComplete(getParent(e,"group"));
}

function inputEssayChoice(e) {

	var id = e.parentElement.id;
	if (e.value != "") {
		addClass("link_" + id, "animate");
		addClass(id, "ckeked");
	} else {
		removeClass("link_" + id, "animate");
		removeClass(id, "ckeked");
	}
	
	checkGroupComplete(getParent(e,"group"));
}


function checkGroupComplete(group){
	
	var qa =group.querySelectorAll('.qa');
	
	for (var k = 0; k < qa.length; k++) {

		if(!qa[k].classList.contains('ckeked')){
			removeClass("link_" + group.id, "animate");
			return
			}
	}
	addClass("link_" + group.id, "animate");
	
	
}


function isRadioSeletected(name){
	var radios = document.getElementsByName(name);

	for (var i = 0, length = radios.length; i < length; i++)
	{
	 if (radios[i].checked)
	 {
	 return true;
	 }
	}
}


function getWidthQuestionLine(){
	
	var l = $("question-line").childNodes;
	var w=0;
	for (var i = 0; i < l.length; i++){
		
		if(l[i].classList.contains('ancora-group')){
			w+=l[i].offsetWidth+20;
		}
		else
			w+=l[i].offsetWidth+10;
	}
	return w;
}

function checkDisplayQLine(){
	var width=$("question-line").offsetWidth;
	var widthQuestion = getWidthQuestionLine();
	
	if(width>widthQuestion)
		return true;
	return false;
}

function getQuizzes(){
	
	var groups = document.querySelectorAll('.group');
	var json=[]
	var groupJSON;
	for (var i = 0; i < groups.length; i++) {
		groupJSON={groupID:groups[i].id};
//		groupJSON.groupID=
		var qa =groups[i].querySelectorAll('.qa');
		
		var questionAnswers=[];
//		var qaJSON={};
		for (var k = 0; k < qa.length; k++) {
//			fillInTheBlank
			if(qa[k].classList.contains("single") )
				questionAnswers.push({qaId:qa[k].id,type:"single", value:getJsonSingleMultipleBoolean(qa[k])});
			else if( qa[k].classList.contains("boolean"))
				questionAnswers.push({qaId:qa[k].id,type:"boolean", value:getJsonSingleMultipleBoolean(qa[k])});
			else if( qa[k].classList.contains("multiple"))
				questionAnswers.push({qaId:qa[k].id,type:"multiple", value:getJsonSingleMultipleBoolean(qa[k])});
			else if(qa[k].classList.contains("numeric")  )
				questionAnswers.push({qaId:qa[k].id,type:"numeric", value:getJsonInputText(qa[k])});
			else if( qa[k].classList.contains("shortAnswer") )
				questionAnswers.push({qaId:qa[k].id,type:"shortAnswer", value:getJsonInputText(qa[k])});
			else if(qa[k].classList.contains("essay") )
				questionAnswers.push({qaId:qa[k].id,type:"essay", value:getJsonTextArea(qa[k])});
			else if(qa[k].classList.contains("fillInTheBlank") ){
//				alert(qa[k].getElementsByTagName('select').length)
				
				var selects=qa[k].getElementsByTagName('select');
				for(var s=0; s<selects.length;s++)
					questionAnswers.push({qaId:qa[k].id,type:"fillInTheBlank", value:getJsonFillInTheBlank(selects[s])});
				
			}
			else if(qa[k].classList.contains("matching") )
				questionAnswers.push({qaId:qa[k].id,type:"matching", value:getJsonMatching(qa[k])});
			else
				questionAnswers.push({qaId:qa[k].id,type:qa[k].className});
			
		}
		groupJSON.qa=questionAnswers;
		json.push(groupJSON);

	}
	return JSON.stringify(json);
//	console.log(json);
}




function getJsonSingleMultipleBoolean(qa){
	
	var inputList=qa.getElementsByTagName('INPUT');
	
	var json=[];
	for (var i = 0; i < inputList.length; i++) {
//		console.log(inputList[i])
		json.push({inputId:inputList[i].id, value: inputList[i].checked});
	}
	return json;
}



function getJsonInputText(qa){
	

	var inputList=qa.getElementsByTagName('INPUT');
	var json=[];
		if(inputList.length>0)
			json.push({inputId:inputList[0].name, value: inputList[0].value});
	return json;
}


function getJsonTextArea(qa){
	
	var inputList=qa.getElementsByTagName('TEXTAREA');
	var json=[];
		if(inputList.length>0)
			json.push( {inputId:inputList[0].id, value: inputList[0].value});
	return json;
}



function getJsonInputText(qa){
	
	var inputList=qa.getElementsByTagName('INPUT');
	var json=[];
		if(inputList.length>0)
			json.push({inputId:inputList[0].id, value: inputList[0].value});
	return json;
}

//function getJsonMultiple(qa){
//	
//	var inputList=qa.getElementsByTagName('INPUT');
//	
//	var json=[];
//	for (var i = 0; i < inputList.length; i++) {
//		console.log(inputList[i].checked)
//		json.push({inputId:inputList[i].id, value: inputList[i].checked});
//	}
//	return json;
//}


function getJsonFillInTheBlank(sel){
	
//	var sel=qa.getElementsByTagName('select');
	var json=[];
	if(sel.length>0)
			json.push( {inputId:sel.id, value: sel.options[sel.selectedIndex].id}); 
	return json;
}

function getJsonMatching(qa){
	
var inputList=qa.getElementsByTagName('INPUT');
	
	var json=[];
	for (var i = 0; i < inputList.length; i++) {
		json.push({inputId:inputList[i].id, value: inputList[i].value});
	}
	return json;
	
}







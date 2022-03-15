var eshu;
var fileConfig;
window.onload = function() {	
	readXml();

	//document.getElementById('fileinput').addEventListener('change', readSingleFile, false);
	//document.getElementById('fileinput').click();
	
	//alert(fileconf);
	
	//var element =document.getElementById('fileinput');
	//var event = new Event('change');
	//element.dispatchEvent(event);
	
	
	
	
	var loc = window.location.pathname;
	  var dir = loc.substring(0, loc.lastIndexOf('/'));
	  //"C:\\Users\\User\\workspace\\Eshu2.0Beta\\WebContent\\EshuConfig.xml"
	
}
function windowToCanvas(canvas, event) {
	
	var x = event.x == undefined ? event.clientX : event.x;
	var y = event.y == undefined ? event.clientY : event.y;	
	var bbox = canvas.getBoundingClientRect();

	return {
		x : x - bbox.left * (canvas.width / bbox.width),
		y : y - bbox.top * (canvas.height / bbox.height)
	};
}



function disableContextMenu(){ 
   	return false 
}

function getNegative(value){ 
   	if (value <=0 )return value;
   	return -value;
}



/**
 * Request a new frame with a FPS adjusted to the browser
 */
window.requestAnimFrame = (function(callback) {
    return  window.requestAnimationFrame || 
    	window.webkitRequestAnimationFrame || 
    	window.mozRequestAnimationFrame || 
    	window.oRequestAnimationFrame || 
    	window.msRequestAnimationFrame ||
    	function(callback) { window.setTimeout(callback, 1000 / 60); };
  })();


function readXml(){
	document.getElementById('fileinput').addEventListener('change', readSingleFile, false);
		//xmlData=()parseFromString(xml.responseText,)
		
	
//	var tagObj=xmlDoc.getElementsByTagName("vertice");
//	var typeValue = tagObj[0].getElementsByTagName("toolbar")[0].childNodes[0].nodeValue;
//	var titleValue = tagObj[0].getElementsByTagName("layout")[0].childNodes[0].nodeValue;

}


function readSingleFile(evt) {
    //Retrieve the first (and only!) File from the FileList object
    var f = evt.target.files[0]; 
    if (f) {
      var r = new FileReader();
      r.onload = function(e) { 
	      var contents = e.target.result;
	      createEditor(contents);     
	      return contents;
      }
      r.readAsText(f);
    } else { 
      alert("Failed to load file");
    }
  }

function createEditor(fileconf){
	fileConfig=fileconf;
	
	var eshuConfig= new XMLParser(fileconf);
	var editorStyle = eshuConfig.getEditorStyle();
	var toobarStyle = eshuConfig.getToolbarStyle();
	var editorConf = {
		editorStyle : editorStyle,
		toobarStyle : toobarStyle,
	}
	
	var div = document.getElementById("graphDiv");
	eshu = new Eshu(div);
	
	
	setEditorStyle(eshu,eshuConfig);
	eshu.setImage("arrow.svg","./image/arrow.svg");
	eshu.createNodeTypeArrow();
	
	var nodeTypes=eshuConfig.getNodeTypes();
	for(var i in nodeTypes){
		
		var iconPath=removeSpaceString(nodeTypes[i].iconTollbarSVGPath);
		var imagePath=removeSpaceString(nodeTypes[i].imgSVGPath);
		eshu.setImage(iconPath,iconPath);
		eshu.setImage(imagePath,imagePath);
		eshu.createNodeType(JSON.stringify(nodeTypes[i]));
	}
	
	
	var edgeTypes=eshuConfig.getEdgeTypes();
	for(var i in edgeTypes){
		var iconPath=removeSpaceString(edgeTypes[i].iconTollbarSVGPath);
		eshu.setImage(iconPath,iconPath);
		eshu.createEdgeType(JSON.stringify(edgeTypes[i]));
	}
	eshu.setReducibles(eshuConfig.getValidationConfig().reducibles);
	
	eshu.setPositionHorizontal();
	eshu.syntaxValidation=+eshuConfig.getValidationConfig();
	HideShowDivs();
	
	document.getElementById('editor').style.display = 'block';
	eshu.showToolbar(true);
	//eshu.createBtnMenu();
	eshu.init();
	
	
	
//	var s ="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+CjwhLS0gQ3JlYXRlZCB3aXRoIElua3NjYXBlIChodHRwOi8vd3d3Lmlua3NjYXBlLm9yZy8pIC0tPgoKPHN2ZwogICB4bWxuczpvc2I9Imh0dHA6Ly93d3cub3BlbnN3YXRjaGJvb2sub3JnL3VyaS8yMDA5L29zYiIKICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICB4bWxuczpjYz0iaHR0cDovL2NyZWF0aXZlY29tbW9ucy5vcmcvbnMjIgogICB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiCiAgIHhtbG5zOnN2Zz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciCiAgIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIKICAgeG1sbnM6c29kaXBvZGk9Imh0dHA6Ly9zb2RpcG9kaS5zb3VyY2Vmb3JnZS5uZXQvRFREL3NvZGlwb2RpLTAuZHRkIgogICB4bWxuczppbmtzY2FwZT0iaHR0cDovL3d3dy5pbmtzY2FwZS5vcmcvbmFtZXNwYWNlcy9pbmtzY2FwZSIKICAgd2lkdGg9IjEwNW1tIgogICBoZWlnaHQ9IjgwbW0iCiAgIHZpZXdCb3g9IjAgMCAzNzIuMDQ3MjQgMjgzLjQ2NDU4IgogICBpZD0ic3ZnMiIKICAgdmVyc2lvbj0iMS4xIgogICBpbmtzY2FwZTp2ZXJzaW9uPSIwLjkxIHIxMzcyNSIKICAgc29kaXBvZGk6ZG9jbmFtZT0iYWN0b3JJY29uLnN2ZyI+CiAgPGRlZnMKICAgICBpZD0iZGVmczQiPgogICAgPGxpbmVhckdyYWRpZW50CiAgICAgICBpZD0ibGluZWFyR3JhZGllbnQ1NTgyIgogICAgICAgb3NiOnBhaW50PSJzb2xpZCI+CiAgICAgIDxzdG9wCiAgICAgICAgIHN0eWxlPSJzdG9wLWNvbG9yOiMwMDAwMDA7c3RvcC1vcGFjaXR5OjE7IgogICAgICAgICBvZmZzZXQ9IjAiCiAgICAgICAgIGlkPSJzdG9wNTU4NCIgLz4KICAgIDwvbGluZWFyR3JhZGllbnQ+CiAgPC9kZWZzPgogIDxzb2RpcG9kaTpuYW1lZHZpZXcKICAgICBpZD0iYmFzZSIKICAgICBwYWdlY29sb3I9IiNmZmZmZmYiCiAgICAgYm9yZGVyY29sb3I9IiM2NjY2NjYiCiAgICAgYm9yZGVyb3BhY2l0eT0iMS4wIgogICAgIGlua3NjYXBlOnBhZ2VvcGFjaXR5PSIwLjAiCiAgICAgaW5rc2NhcGU6cGFnZXNoYWRvdz0iMiIKICAgICBpbmtzY2FwZTp6b29tPSIwLjk4OTk0OTQ5IgogICAgIGlua3NjYXBlOmN4PSIxMTAuOTk4ODQiCiAgICAgaW5rc2NhcGU6Y3k9IjIwOS4zMTQ1OCIKICAgICBpbmtzY2FwZTpkb2N1bWVudC11bml0cz0icHgiCiAgICAgaW5rc2NhcGU6Y3VycmVudC1sYXllcj0ibGF5ZXIxIgogICAgIHNob3dncmlkPSJmYWxzZSIKICAgICBoZWlnaHQ9IjEwMG1tIgogICAgIGlua3NjYXBlOndpbmRvdy13aWR0aD0iMTM2NiIKICAgICBpbmtzY2FwZTp3aW5kb3ctaGVpZ2h0PSI3MDUiCiAgICAgaW5rc2NhcGU6d2luZG93LXg9Ii04IgogICAgIGlua3NjYXBlOndpbmRvdy15PSItOCIKICAgICBpbmtzY2FwZTp3aW5kb3ctbWF4aW1pemVkPSIxIiAvPgogIDxtZXRhZGF0YQogICAgIGlkPSJtZXRhZGF0YTciPgogICAgPHJkZjpSREY+CiAgICAgIDxjYzpXb3JrCiAgICAgICAgIHJkZjphYm91dD0iIj4KICAgICAgICA8ZGM6Zm9ybWF0PmltYWdlL3N2Zyt4bWw8L2RjOmZvcm1hdD4KICAgICAgICA8ZGM6dHlwZQogICAgICAgICAgIHJkZjpyZXNvdXJjZT0iaHR0cDovL3B1cmwub3JnL2RjL2RjbWl0eXBlL1N0aWxsSW1hZ2UiIC8+CiAgICAgICAgPGRjOnRpdGxlPjwvZGM6dGl0bGU+CiAgICAgIDwvY2M6V29yaz4KICAgIDwvcmRmOlJERj4KICA8L21ldGFkYXRhPgogIDxnCiAgICAgaW5rc2NhcGU6bGFiZWw9IkNhbWFkYSAxIgogICAgIGlua3NjYXBlOmdyb3VwbW9kZT0ibGF5ZXIiCiAgICAgaWQ9ImxheWVyMSIKICAgICB0cmFuc2Zvcm09InRyYW5zbGF0ZSgwLC03NjguODk3NikiPgogICAgPGcKICAgICAgIGlkPSJnNTYyNSIKICAgICAgIHN0eWxlPSJzdHJva2Utd2lkdGg6NTtzdHJva2UtbWl0ZXJsaW1pdDo0O3N0cm9rZS1kYXNoYXJyYXk6bm9uZSI+CiAgICAgIDxlbGxpcHNlCiAgICAgICAgIGN4PSIxODYuNzI0NDQiCiAgICAgICAgIGN5PSI4MTYuMzExMSIKICAgICAgICAgcng9IjQ3LjAyNTYzMSIKICAgICAgICAgcnk9IjM5LjAwNjQ0MyIKICAgICAgICAgaWQ9InBhdGg0MTM2IgogICAgICAgICBzdHlsZT0ib3BhY2l0eToxO2ZpbGw6I2ZmZmZmZjtmaWxsLW9wYWNpdHk6MTtzdHJva2U6IzAwMDAwMDtzdHJva2Utd2lkdGg6NTtzdHJva2UtbWl0ZXJsaW1pdDo0O3N0cm9rZS1kYXNoYXJyYXk6bm9uZTtzdHJva2UtZGFzaG9mZnNldDowO3N0cm9rZS1vcGFjaXR5OjEiIC8+CiAgICAgIDxwYXRoCiAgICAgICAgIGlua3NjYXBlOmNvbm5lY3Rvci1jdXJ2YXR1cmU9IjAiCiAgICAgICAgIGlkPSJwYXRoNTYwNCIKICAgICAgICAgZD0ibSAxODMuODQ3NzYsODU0LjM3MjI3IDAsOTcuOTg0OCIKICAgICAgICAgc3R5bGU9ImZpbGw6bm9uZTtmaWxsLXJ1bGU6ZXZlbm9kZDtzdHJva2U6IzAwMDAwMDtzdHJva2Utd2lkdGg6NTtzdHJva2UtbGluZWNhcDpidXR0O3N0cm9rZS1saW5lam9pbjptaXRlcjtzdHJva2Utb3BhY2l0eToxO3N0cm9rZS1taXRlcmxpbWl0OjQ7c3Ryb2tlLWRhc2hhcnJheTpub25lIiAvPgogICAgICA8cGF0aAogICAgICAgICBpbmtzY2FwZTpjb25uZWN0b3ItY3VydmF0dXJlPSIwIgogICAgICAgICBpZD0icGF0aDU2MDYiCiAgICAgICAgIGQ9Im0gMTg0LjE4NDEyLDk1MC42NTIzNyA3NC4wNzg1Nyw3NC4xMjAxMyIKICAgICAgICAgc3R5bGU9ImZpbGw6bm9uZTtmaWxsLXJ1bGU6ZXZlbm9kZDtzdHJva2U6IzAwMDAwMDtzdHJva2Utd2lkdGg6NTtzdHJva2UtbGluZWNhcDpidXR0O3N0cm9rZS1saW5lam9pbjptaXRlcjtzdHJva2Utb3BhY2l0eToxO3N0cm9rZS1taXRlcmxpbWl0OjQ7c3Ryb2tlLWRhc2hhcnJheTpub25lIiAvPgogICAgICA8cGF0aAogICAgICAgICBpbmtzY2FwZTpjb25uZWN0b3ItY3VydmF0dXJlPSIwIgogICAgICAgICBpZD0icGF0aDU2MDYtNiIKICAgICAgICAgZD0ibSAxODMuNjc5MjEsOTUwLjY2NzAyIC03Ny40NDQ2NSw3NC4wOTA3OCIKICAgICAgICAgc3R5bGU9ImZpbGw6bm9uZTtmaWxsLXJ1bGU6ZXZlbm9kZDtzdHJva2U6IzAwMDAwMDtzdHJva2Utd2lkdGg6NTtzdHJva2UtbGluZWNhcDpidXR0O3N0cm9rZS1saW5lam9pbjptaXRlcjtzdHJva2Utb3BhY2l0eToxO3N0cm9rZS1taXRlcmxpbWl0OjQ7c3Ryb2tlLWRhc2hhcnJheTpub25lIiAvPgogICAgICA8cGF0aAogICAgICAgICBpbmtzY2FwZTpjb25uZWN0b3ItY3VydmF0dXJlPSIwIgogICAgICAgICBpZD0icGF0aDU2MjMiCiAgICAgICAgIGQ9Im0gMTA1LjA1NTg2LDg3NS41ODU0NyAxNjIuNjM0NTYsMCIKICAgICAgICAgc3R5bGU9ImZpbGw6bm9uZTtmaWxsLXJ1bGU6ZXZlbm9kZDtzdHJva2U6IzAwMDAwMDtzdHJva2Utd2lkdGg6NTtzdHJva2UtbGluZWNhcDpidXR0O3N0cm9rZS1saW5lam9pbjptaXRlcjtzdHJva2UtbWl0ZXJsaW1pdDo0O3N0cm9rZS1kYXNoYXJyYXk6bm9uZTtzdHJva2Utb3BhY2l0eToxIiAvPgogICAgPC9nPgogIDwvZz4KPC9zdmc+Cg==";
//	alert(atob(s));
	
}

function setEditorStyle(eshu,eshuConfig ){
	//eshu.BACKGROUNDCOLOR=editorStyle.background;
	var editorStyle = eshuConfig.getEditorStyle();
	var toobarStyle = eshuConfig.getToolbarStyle();
	
	//eshuConfig.getWindowsPropertiesStyle();
	
	eshu.resize(editorStyle.width,editorStyle.height);
	eshu.setGridVisible(editorStyle.grid_visible);
	eshu.setGridLineColor(editorStyle.grid_line_color);
	eshu.setToolbarBorderWidth(toobarStyle.border_width);
	eshu.setToolbarBorderColor(toobarStyle.border_color);
	eshu.setToolbarBackgroundColor(toobarStyle.background);
	    
//	eshu.setValidationSyntaxe( eshuConfig.getValidationSyntaxe());
	
	
	
	
	
	
}

function HideShowDivs( ){
	//document.getElementById('inputTextToSave').value=fileconf;
	//document.getElementById('inputbox').style.display = 'none';
	//document.getElementById('editor').style.display = 'block';
	document.getElementById('menuButton').style.display = 'block';
}


function eshuCopy( ){
	eshu.copy();
}

function eshuPaste( ){
	eshu.paste();
}

function eshuCut( ){
	eshu.cut();
}

function eshuDelete( ){
	eshu.deleteElement();
}

function eshuClear( ){
	eshu.clear();
}

function eshuUndo( ){
	eshu.undo();
}

function eshuRedo( ){
	eshu.redo();
}

function exportGraph(){
	document.getElementById('inputTextToSave').value=eshu.exportGraph();
	
}

function importGraph(){
	eshu.importGraph(document.getElementById('inputTextToSave').value);
}
function importGraphDiff(){
	eshu.importGraphDiff(document.getElementById('inputTextToSave').value);
}

function clearFileAsText(){
	document.getElementById('inputTextToSave').value="";
}

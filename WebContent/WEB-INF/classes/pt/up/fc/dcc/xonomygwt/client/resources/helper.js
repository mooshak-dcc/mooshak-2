function onReady(method) {
	if (document.readyState === 'complete') { // or also compare to
		// 'interactive'
		setTimeout(method, 1); // schedule to run immediately
	} else {
		readyStateCheckInterval = setInterval(function() {
			if (document.readyState === 'complete') { // or also compare to
				// 'interactive'
				clearInterval(readyStateCheckInterval);
				method();
			}
		}, 10);
	}
}

/* Overrides to Xonomy */

Xonomy.verifyDocSpec = function() { // make sure the docSpec object has
	// everything it needs
	if (!Xonomy.docSpec || typeof (Xonomy.docSpec) != "object")
		Xonomy.docSpec = {};
	if (!Xonomy.docSpec.elements
			|| typeof (Xonomy.docSpec.elements) != "object")
		Xonomy.docSpec.elements = {};
	console.log(Xonomy.docSpec.namespaces)
	if (!Xonomy.docSpec.namespaces
			|| typeof (Xonomy.docSpec.namespaces) != "object")
		Xonomy.namespaces = {};
	else
		Xonomy.namespaces = Xonomy.docSpec.namespaces;		
	if (Xonomy.docSpec.onchange && typeof (Xonomy.docSpec.onchange) == "string"
			&& typeof (eval(Xonomy.docSpec.onchange)) == "function")
		Xonomy.docSpec.onchange = eval(Xonomy.docSpec.onchange)
	else if (!Xonomy.docSpec.onchange
			|| typeof (Xonomy.docSpec.onchange) != "function")
		Xonomy.docSpec.onchange = function() {
		};
	if (Xonomy.docSpec.validate && typeof (Xonomy.docSpec.validate) == "string"
			&& typeof (eval(Xonomy.docSpec.validate)) == "function")
		Xonomy.docSpec.validate = eval(Xonomy.docSpec.validate)
	else if (!Xonomy.docSpec.validate
			|| typeof (Xonomy.docSpec.validate) != "function")
		Xonomy.docSpec.validate = function() {
		};
};
Xonomy.asFunction = function(specProperty, defaultValue) {
	if (typeof (specProperty) == "function")
		return specProperty;
	if (typeof (specProperty) == "string") {
		try {
			if (typeof (eval(specProperty)) == "function")
				return eval(specProperty);
		} catch (err) {
		}
	}
	if (typeof (specProperty) == typeof (defaultValue))
		return function() {
			return specProperty;
		}
	return function() {
		return defaultValue
	};
}
Xonomy.verifyDocSpecAttribute = function(elementName, attributeName) { // make
	// sure
	// the
	// DocSpec
	// object
	// has
	// such
	// an
	// attribute,
	// that
	// the
	// attribute
	// has
	// everything
	// it
	// needs
	var elSpec = Xonomy.docSpec.elements[elementName];
	if (!elSpec.attributes[attributeName]
			|| typeof (elSpec.attributes[attributeName]) != "object") {
		if (Xonomy.docSpec.unknownAttribute) {
			elSpec.attributes[attributeName] = (typeof (Xonomy.docSpec.unknownAttribute) === "function") ? Xonomy.docSpec
					.unknownAttribute(elementName, attributeName)
					: Xonomy.docSpec.unknownAttribute;
		} else
			elSpec.attributes[attributeName] = {};
	}
	var spec = elSpec.attributes[attributeName];
	if (!spec.asker
			|| (typeof (spec.asker) != "function" && typeof (spec.asker) != "string"))
		spec.asker = function() {
			return ""
		};
	else if (typeof (spec.asker) == "string") {
		try {
			if (typeof (eval(spec.asker)) == "function")
				spec.asker = eval(spec.asker);
			else
				spec.asker = function() {
					return ""
				};
		} catch (err) {
			spec.asker = function() {
				return ""
			};
		}
	}
	if (!spec.menu || typeof (spec.menu) != "object")
		spec.menu = [];
	spec.isReadOnly = Xonomy.asFunction(spec.isReadOnly, false);
	spec.isInvisible = Xonomy.asFunction(spec.isInvisible, false);
	if (spec.validate && typeof (spec.validate) == "string") {
		try {
			if (typeof (eval(spec.validate)) == "function")
				spec.validate = eval(spec.validate);
		} catch (err) {
		}
	}
	spec.shy = Xonomy.asFunction(spec.shy, false);
	if (spec.displayName)
		spec.displayName = Xonomy.asFunction(spec.displayName, "");
	if (spec.title)
		spec.title = Xonomy.asFunction(spec.title, "");
	for (var i = 0; i < spec.menu.length; i++)
		Xonomy.verifyDocSpecMenuItem(spec.menu[i]);
};
Xonomy.verifyDocSpecMenuItem = function(menuItem) { // make sure the menu item
	// has all it needs
	menuItem.caption = Xonomy.asFunction(menuItem.caption, "?");
	if (!menuItem.action
			|| (typeof (menuItem.action) != "function" && typeof (menuItem.action) != "string"))
		menuItem.action = function() {
		};
	else if (typeof (menuItem.action) == "string") {
		try {
			if (typeof (eval(menuItem.action)) == "function")
				menuItem.action = eval(menuItem.action);
			else
				menuItem.action = function() {
					return ""
				};
		} catch (err) {
			menuItem.action = function() {
				return ""
			};
		}
	}
	if (!menuItem.hideIf)
		menuItem.hideIf = function() {
			return false;
		};
	else if (typeof (menuItem.hideIf) == "string") {
		try {
			if (typeof (eval(menuItem.hideIf)) == "function")
				menuItem.hideIf = eval(menuItem.hideIf);
		} catch (err) {
		}
	}
};

Xonomy.changed = function(jsElement) { // called when the document changes
	Xonomy.harvestCache = {};
	Xonomy.refresh();
	Xonomy.validate();
	Xonomy.docSpec.onchange(jsElement); // report that the document has changed
	document.querySelector('div.xonomy').dispatchEvent(new Event('change', {
		'value' : Xonomy.harvest()
	}))
};

Xonomy.askTags = function(defaultString, askerParameter, jsMe) {
	var html = "<div id='tagsList' style='padding:10px;'>" 
		+ Xonomy.getTagsHTML(Xonomy.xmlEscape(defaultString)
		.split(',').filter(item => item)) + "</div>";
	html += "<form onsubmit='Xonomy.answer(this.val.value); return false'" +
			" style='margin: 0 -5px -5px;'>";
	html += "<input id='val' name='val' type='hidden' value='"
			+ Xonomy.xmlEscape(defaultString) + "'/>";
	html += "<input id='tag' name='tag' class='textbox focusme'/>";
	html += " <input type='button' value='Add' onclick='Xonomy.addTag()'>";
	html += " <input type='submit' value='OK'>";
	html += "</form>";
	return html;
};
Xonomy.addTag = function() {
	var tags = document.getElementById('val').value.split(',').filter(item => item);
	var tag = document.getElementById('tag').value.trim().split(',').filter(item => item);
	for (var i = 0; i < tag.length; i++)
		tags.push(tag[i]);
	Xonomy.showTags(tags);
	return false;
};
Xonomy.removeTag = function() {
	var tags = document.getElementById('val').value.split(',').filter(item => item);
	var id = this.getAttribute('id');
	tags.splice(id, 1);
	Xonomy.showTags(tags);
	return false;
};
Xonomy.getTagsHTML = function(tags) {
	if (!tags)
		tags = document.getElementById('val').value.split(',').filter(item => item);
	var html = '<div>';
	for (var i = 0; i < tags.length; i++) {
		html += '<div style="line-height:30px;vertical-align:middle;">' + tags[i] + '<button class="remove" id="' + i
				+ '" style="float:right;" onclick="Xonomy.removeTag.bind(this)()">x</button></div>';
	}
	html += '</div>';
	return html;
};
Xonomy.showTags = function(tags) {
	html = Xonomy.getTagsHTML(tags);
	document.getElementById('tagsList').innerHTML = html;
	var buttons = document.getElementsByClassName('remove');
	document.getElementById('val').value = tags.join(',');
	return html;
};

Xonomy.askDatetime = function(defaultString, askerParameter, jsMe) {
	html = "";
	html += "<form onsubmit='Xonomy.answer(new Date(this.date.value+\" \"+this.time.value).toLocalISOString()); return false'>";
	var d = new Date( Date.parse(Xonomy.xmlEscape(defaultString)) );
	var dd = d.getDate();
	var mm = d.getMonth()+1; 
	var yyyy = d.getFullYear();
	if(dd<10) { dd='0'+dd; } 
	if(mm<10) { mm='0'+mm; } 
	var dateStr = yyyy+'-'+mm+'-'+dd;
	var hh = d.getHours();
	var mm = d.getMinutes(); 
	var ss = d.getSeconds();
	if(hh<10) { hh='0'+hh; } 
	if(mm<10) { mm='0'+mm; } 
	if(ss<10) { ss='0'+ss; } 
	var timeStr = hh+':'+mm+':'+ss;
	html += '<div><input id="date" name="date" type="date" value="' + dateStr
			+ '" style="width:100%;box-sizing: border-box;margin-bottom:5px;"></div>' +
			'<div><input id="time" name="time" type="time" value="' + timeStr
			+ '" style="width:100%;box-sizing: border-box;margin-bottom:5px;"></div>';
	html += " <input type='submit' style='display:block;margin-left:auto;margin-right:0;' value='OK'>";
	html += "</form>";
	return html;
};
Date.prototype.toLocalISOString = function() {

    // If not called on a Date instance, or timevalue is NaN, return undefined
    if (isNaN(this) || Object.prototype.toString.call(this) != '[object Date]') return;

    // Copy date so don't modify original
    var d = new Date(+this);
    var offset = d.getTimezoneOffset();
    var offSign = offset > 0? '-' : '+';
    offset = Math.abs(offset);
    var tz = offSign + ('0' + (offset/60|0)).slice(-2) + ':' + ('0' + offset%60).slice(-2)
    return new Date(d.setMinutes(d.getMinutes() - d.getTimezoneOffset())).toISOString().slice(0,-1) + tz; 
};
Date.prototype.addHours = function(h) {    
	this.setTime(this.getTime() + (h*60*60*1000)); 
	return this;   
};
Xonomy.askTime = function(defaultString, askerParameter, jsMe) {
	html = "";
	html += "<form onsubmit=\"Xonomy.answer(new Date((this.hour.value*3600 + this.min.value*60 + this.sec.value*1)*1000).toISOString()); return false\">";
	var t = new Date( Date.parse(Xonomy.xmlEscape(defaultString)) ).getTime();
	var hh = Math.floor(Math.floor(t/3600)/1000);
	var mm = Math.floor(Math.floor((t-hh*3600*1000)/60)/1000); 
	var ss = Math.floor((t-hh*3600*1000 - mm*60*1000)/1000);
	/*if(hh<10) { hh='00'+hh; } 
	else if(hh<100) { hh='0'+hh; } 
	if(mm<10) { mm='0'+mm; }
	if(ss<10) { ss='0'+ss; }*/
	html += '<input id="hour" name="hour" type="number" min="0" max="999" value="' + hh + '"> : ';
	html += '<input id="min" name="min" type="number" min="0" max="59" value="' + mm + '"> : ';
	html += '<input id="sec" name="sec" type="number" min="0" max="59" value="' + ss + '">';
	html += '<input type="submit" style="display:block;margin-left:auto;margin-right:0;" value="OK">';
	html += '</form>';
	return html;
};

Xonomy.askNumber = function(defaultString, askerParameter, jsMe) {
	if(askerParameter.min!=undefined) askerParameter.min='min="' + askerParameter.min + '"';
	if(askerParameter.max!=undefined) askerParameter.max='max="' + askerParameter.max + '"';
	html = "";
	html += "<form onsubmit=\"Xonomy.answer(this.val.value); return false\">";
	html += '<input id="val" name="hour" type="number" ' + askerParameter.min + ' ' + askerParameter.max + ' value="' 
		+ Xonomy.xmlEscape(defaultString) + '">';
	html += '<input type="submit" value="OK">';
	html += '</form>';
	return html;
};







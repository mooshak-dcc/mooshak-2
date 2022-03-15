
function InformationBox(context){
	this.context=context;
	this.height;
	this.width;
	this.x;
	this.y;
}

InformationBox.prototype.drawInfo = function(x,y,features) {
	var sep = 5;
	var line = 10;
	var width = 0;
	var height = line;
	var labels = new Array();
	
	for(feature in features) {
		var label = feature+": "+features[feature];
		var labelWidth = this.measureText(this.context,label).width;
		width = labelWidth + line > width ? labelWidth + line : width;
		height += line;
		
		labels.push(label);	
	}
	
	this.context.save();
	this.context.beginPath();
	this.context.fillStyle = "#FFFFCC";
	this.context.rect(x-width,y,width,height);

	this.context.fill();
	// this.context.stroke();
	this.context.fillStyle = "#686868 "//'#B8B8B8';
	y += line;
	for(l in labels) {
		this.context.fillText(labels[l],x-width/2+sep,y);
		y += line;
	}
	this.context.restore();
	
this.height=height;
this.width=width;
this.x=x;
this.y=y;
}
var textMeasures = new Object();

/**
 * Hack to deal with the fact that SVG context does not handle measureText
 * @param context
 * @param label
 * @returns
 */
InformationBox.prototype.measureText=function(context,label) {
	
	if(textMeasures[label] == undefined)
		textMeasures[label] = context.measureText(label);
	
	return textMeasures[label];
}

InformationBox.prototype.contains =function(mx, my) {
			var x1 = this.x;
			var y1 = this.y;
			return (x1 <= mx) && (x1 + this.width + 10 >= mx) && (y1 <= my)
					&& (y1 + this.height + 10 >= my);

		}




/******************
 *  QuadTree for objects with a location and dimensions,
 *  that is, with properties x, y, width and height.
 *  Location (x,y) is the upper left corner.
 */

QuadTree.bucketSize = 5; 	// number of nodes in bucket before overflowing
QuadTree.minSize = 5;		// minimum size of a bucket
/**
 * Create a QuadTree for rectangular region with
 * upper left corner xMin,yMin and lower left corner xMax,yMax 
 */
function QuadTree(xMin,yMin,xMax,yMax) {
	this.root = new LeafNode(xMin,yMin,xMax,yMax);
	
	this.maxWidth = 0;
	this.maxHeight = 0;
}

/**
 * Insert a node (object with location and dimensions) in this QuadTree
 */
QuadTree.prototype.insert =  function(node) {
	this.root = this.root.insert(node);
	if(node.width > this.maxWidth)
		this.maxWidth = node.width;
	if(node.height > this.maxHeight)
		this.maxHeight = node.height;
};

/**
 * Return a node containing position (x,y) or undefined if none
 */
QuadTree.prototype.search =  function(x,y) {
// ***************************	
	var nodes=new Array();
	var coorX,coorY;
	(x-this.maxWidth)<0?coorX=0:coorX=x-this.maxWidth;
	(x-y-this.maxHeight)<0?coorY=0:coorY=y-this.maxHeight;
	
//	var candidates = this.locate(x-this.maxWidth,y-this.maxHeight,x,y);
	var candidates = this.locate(x-this.maxWidth,y-this.maxHeight,x,y);
	
	for(var c in candidates) {
		var node = candidates[c];
		if(contains(node.x,node.y,node.x+node.width,node.y+node.height,x,y))
			//return node;
			nodes.push(node);
	}
	return nodes;
};

/**
 * Return an array of nodes overlapping the rectangular region with 
 * upper left corner xMin,yMin and lower left corner xMax,yMax 
 */
QuadTree.prototype.locate =  function(xMin,yMin,xMax,yMax) {
	var nodes = [];

	this.root.locate(nodes,xMin,yMin,xMax,yMax);
	
	return nodes;
};

/**
 * Return all nodes in the QuadTree 
 */
QuadTree.prototype.getAll = function() {
	var nodes = [];
	
	this.root.getAll(nodes);
	
	return nodes;
}

/**
 * Remove given node from the QuadTree. If node is not in QuadTree
 * then this is silently ignored
 */
QuadTree.prototype.remove = function(node) {
	this.root.remove(node);
//	var nodes=this.root.getAll(node);
//	alert("ALL "+nodes.legth)
	
	
}

//--------- Auxiliary classes used by QuadTree

function LeafNode(xMin,yMin,xMax,yMax) {
	this.nodes = [];
	this.xMin = xMin;
	this.yMin = yMin;
	this.xMax = xMax;
	this.yMax = yMax;
};

LeafNode.prototype.insert = function(node) {
	
	if(
			(this.nodes.length < QuadTree.bucketSize) 	|| 
			(this.xMax-this.xMin < QuadTree.minSize) 	||
			(this.yMax-this.yMin < QuadTree.minSize)
	) {
		this.nodes.push(node);
		
		return this;
	} else {
		var quad = new QuadNode(this.xMin,this.yMin,this.xMax,this.yMax);
		
		for(var i in this.nodes) 
			quad.insert(this.nodes[i]);
		
		quad.insert(node);
		
		return quad;
	}
}

LeafNode.prototype.locate =  function(nodes,xMin,yMin,xMax,yMax) {
	var x1=Math.min(xMin,xMax);
	var x2=Math.max(xMin,xMax);
	var y1=Math.min(yMin,yMax);
	var y2=Math.max(yMin,yMax);
	

	for(var i in this.nodes) {
		var node = this.nodes[i];
		
//		if(contains(xMin,yMin,xMax,yMax,node.x,node.y) ||
//		   contains(xMin,yMin,xMax,yMax,node.x + node.width,node.y)  ||
//	       contains(xMin,yMin,xMax,yMax,node.x,node.y + node.height) ||
//	       contains(xMin,yMin,xMax,yMax,node.x + node.width,node.y + node.height)
//		)
			if(contains2(x1,y1,x2,y2,node.x,node.y) ||
					contains2(x1,y1,x2,y2,node.x + node.width,node.y)  ||
					contains2(x1,y1,x2,y2,node.x,node.y + node.height) ||
					contains2(x1,y1,x2,y2,node.x + node.width,node.y + node.height)
			)
			 nodes.push(node);
	}
}

LeafNode.prototype.getAll = function(nodes) {

	for(var i in this.nodes)
		nodes.push(this.nodes[i]);
}

	
LeafNode.prototype.remove  = function(dead) {
	var nodes = [];
//	alert("AAA "+dead.id)
	for(var i in this.nodes) {
		var node = this.nodes[i];
//		alert("PPP "+node.id)
		if(node.id != dead.id ) {
			nodes.push(node);
			
		}
	}
	
	
	this.nodes = nodes;
}

function QuadNode(xMin,yMin,xMax,yMax) {
	var xMed = (xMin + xMax) / 2;
	var yMed = (yMin + yMax) / 2;
	
	this.xMed = xMed;
	this.yMed = yMed;
	
	this.xMin = xMin;
	this.yMin = yMin;
	this.xMax = xMax;
	this.yMax = yMax;
	
	this.northWest = new LeafNode(xMin,yMin,xMed,yMed);
	this.northEast = new LeafNode(xMed,yMin,xMax,yMed);
	this.southWest = new LeafNode(xMin,yMed,xMed,yMax);
	this.southEast = new LeafNode(xMed,yMed,xMax,yMax);	
};

QuadNode.prototype.quadrant = function(node) {
//	alert(node);
	
	if(node.x < this.xMed) {
		if(node.y < this.yMed)
			return "northWest" ;
		else
			return "southWest";
	} else { // node.x >= this.xMed 
		if(node.y < this.yMed)
			return "northEast";
		else
			return "southEast";
	}
};

QuadNode.prototype.insert = function(node) {
	
	var quadrant = this.quadrant(node);
	
    this[quadrant] = this[quadrant].insert(node);
    
    return this;
}

QuadNode.prototype.locate =  function(nodes,xMin,yMin,xMax,yMax) {
	var quadrants = new Object();
	
	if(contains2(this.xMin,this.yMin,this.xMax,this.yMax,xMin,yMin))
		quadrants[this.quadrant({ x: xMin, y: yMin})] = true;
	if(contains2(this.xMin,this.yMin,this.xMax,this.yMax,xMin,yMax))
		quadrants[this.quadrant({ x: xMin, y: yMax})] = true;
	if(contains2(this.xMin,this.yMin,this.xMax,this.yMax,xMax,yMin))
		quadrants[this.quadrant({ x: xMax, y: yMin})] = true;
	if(contains2(this.xMin,this.yMin,this.xMax,this.yMax,xMin,yMax))
		quadrants[this.quadrant({ x: xMax, y: yMax})] = true;
	
	for(var q in quadrants) {
	//	this[q].locate(nodes,xMin,yMin,xMax,yMax);
		var quadrant = this[q];
		quadrant.locate(nodes,xMin,yMin,xMax,yMax);
	}
}
	
QuadNode.prototype.remove = function(node) {
	var quadrant = this.quadrant(node);
//	alert(this[quadrant].getAll(node))
	
//	for(var i in quadrant) {
//		var node1 = this.northWest[i];
//		alert("edx "+node1.id)
//	}
//	this.northWest.getAll(nodes);
//	this.northEast.getAll(nodes);
//	this.southWest.getAll(nodes);
//	this.southEast.getAll(nodes);	
	
	var el=this[quadrant].remove(node);
//	alert("this "+quadrant)
	return el;

}

QuadNode.prototype.getAll = function(nodes) {
	
	this.northWest.getAll(nodes);
	this.northEast.getAll(nodes);
	this.southWest.getAll(nodes);
	this.southEast.getAll(nodes);	
}

// Utils

function contains(xMin,yMin,xMax,yMax,x,y) {
	return x >= xMin-5 && x <= xMax+5 && y >= yMin-5 && y <= yMax+5;
} 

function contains2(xMin,yMin,xMax,yMax,x,y) {
	return x >= xMin && x <= xMax || y >= yMin && y <= yMax;
}
//function contains2(xMin,yMin,xMax,yMax,x,y) {
//	return (x >= xMin && x <= xMax || x <=xMin && x >= xMax) && (y >= yMin && y <= yMax || y <= yMin && y >= yMax);
//}

//function contains2(xMin,yMin,xMax,yMax,x,y) {
//	var width=xMax-xMin;
//	var height=yMax-yMin;
//	return (((x >= xMin && x <= xMax) || (x+width >= xMin && x+width <= xMax)  ||  
//			(x+width <= xMin && x+width >= xMax) || (x <= xMin && x >= xMax)   )    &&  
//			((y >= yMin && y <= yMax) ||(y +height>= yMin && y+height <= yMax) ||
//			(y +height<= yMin && y+height >= yMax)|| (y<= yMin && y>= yMax))) ;
//}




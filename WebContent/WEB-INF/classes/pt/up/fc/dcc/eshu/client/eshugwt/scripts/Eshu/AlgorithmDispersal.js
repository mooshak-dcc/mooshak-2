
// Eshu.updateRate		= 10;	// Deprecated: using requestAnimFrame
Eshu.convergenceForce	= 0.001;

Eshu.repulsion			= 50000;
Eshu.springLength 		= 50;
Eshu.springForce 		= 0.01;
Eshu.slack				= 0.5;
Eshu.margin			= 10;	// 


/**
 * force redrawing even if already converged or not doing layout
 */
Eshu.prototype.redraw = function() {
	if(! this.converging)
		this.update(this.context);
};

/**
 * Update positions of given nodes and show them until they converge
 * @param nodes
 * @param arcs
 * @param speeds
 */
Eshu.prototype.update = function() {
	this.converging = this.refine();

	this.draw();

	if(this.converging) {
		requestAnimFrame( function(){this.update()}.bind(this) );
		// setTimeout(function(){this.update()}.bind(this),Eshu.updateRate);
	} else {

	//	console.log("converged!");
	}
};

/**
 * Refine the position of a set of nodes, given the
 * arcs than connect them and their current positions
 * @returns {Boolean} is the process still converging
 */
Eshu.prototype.refine = function() {

	var forces = new Object();
	var nodes=this.graph.nodes.getAll();
	var edges=this.graph.edges.getAll();
	for(var n in nodes) 
		forces[nodes[n]] = new Arrow();


	for(var e in edges) {
		var edge = edges[e];
		var source = edge.source;
		var target = edge.target;

		var vec = new Arrow(source,	target);

		var displacement = vec.norm() - Eshu.springLength;
		var atraction = Eshu.springForce*displacement/2;

		vec.normalise();
		vec.mult(atraction);
		forces[source].add(vec);
		forces[target].sub(vec); 
	}

	var converging = false;
	var width = this.canvas.width;
	var height = this.canvas.height;
	for(var i in nodes) {
		var node = nodes[i];

		if(node.temporary === "insert") {
			// only inser modifications are changed
			var force = forces[node];

			for(var j in nodes)
				if(i != j) {
					var other =  nodes[j];
					var rep = this.repulse(node,other);
					force.sub(rep);
				}

			this.confine(node,force,width,height);

			if(force.norm2() > Eshu.convergenceForce)
				converging = true;

			var pos = new Arrow(node).add(force);

			node.x = pos.x;
			node.y = pos.y;
		}
	}

	return converging;
};

/**
 * Apply forces to node to keep it confined to the canvas bounding box
 */
Eshu.prototype.confine = function(node,force,width,height) {
	if(node.x > 0)
		force.sub(this.repulse(node,{x: 0, y: node.y}));
	else
		force.sub(this.repulse(node,{x: node.x-Eshu.margin, y: node.y}));
	if(node.x < width)
		force.sub(this.repulse(node,{x: width, y: node.y}));
	else
		force.sub(this.repulse(node,{x: node.x + Eshu.margin, y: node.y}));					
	if(node.y > 0)
		force.sub(this.repulse(node,{x: node.x, y: 0}));
	else
		force.sub(this.repulse(node,{x: node.x, y: node.y-Eshu.margin}));
	if(node.y < height)
		force.sub(this.repulse(node,{x: node.x, y: height}));
	else
		force.sub(this.repulse(node,{x: node.x, y: node.y+Eshu.margin}));

};


/**
 * Compute repulsion force on node induced by other node
 */
Eshu.prototype.repulse = function(node,other) {
	var vec = new Arrow(node,other);

	var distance = vec.norm2() + Eshu.slack; 

	vec.normalise();

	return vec.mult(Eshu.repulsion/distance);
};






/**
 * 
 *  Arrows are math vectors (or points)
 *
 * Create an arrow according to number of arguments:
 * 1) if no arguments is given is the null arrow;
 * 2) if 1 argument is given it must be a arrow/points
 * 			and a copy is created;
 * 3) if 2 arguments are given they must be points
 * 			and and arrow with connecting them is created
 *
 *   José Paulo Leal <zp@dcc.fc.up.pt>	
 * 	 November 2015  
 * 	
 */
function Arrow() {

	switch(arguments.length) {
	case 0:
		this.x = 0;
		this.y = 0;
		break;
	case 1:
		for(var property in arguments[0])
			this[property] = arguments[0][property];
		break;
	case 2:
		this.x = arguments[1].x - arguments[0].x;
		this.y = arguments[1].y - arguments[0].y;
		break;
	}
}

Arrow.delta = 1e-5;
Arrow.prototype = {
		/**
		 * Multiply an arrow by a scalar k
		 * @param k
		 */
		mult : function(k) {
			this.x *= k;
			this.y *= k;
			return this;
		},
		
		/**
		 * Inner product of this arrow with another arrow
		 * @param other arrow
		 */
		inner : function(other) {
			return this.x * other.x + this.y * other.y; 
		},

		/**
		 * Add this arrow to another
		 * @param arrow to add
		 * @return this arrow
		 */
		add : function(arrow) {
			this.x += arrow.x;
			this.y += arrow.y;
			return this;
		},

		/**
		 * Sub this arrow to another
		 * @param arrow to subtract
		 * @return this arrow
		 */
		sub : function(arrow) {
			this.x -= arrow.x;
			this.y -= arrow.y;
			return this;
		},

		normalise : function() {
			var norm = this.norm();

			if(norm > Arrow.delta) {
				this.x = this.x / norm;
				this.y = this.y / norm;
			}
		},

		/**
		 * The norm of this arrow
		 */
		norm : function() {
			return Math.sqrt(this.norm2());
		},

		/**
		 * The square norm of this arrow 
		 * @returns {Number}
		 */
		norm2 : function() {
			return this.x*this.x + this.y*this.y; 
		},
		
		/**
		 * Transform this arrow in an orthogonal arrow with same norm 
		 */
		orth : function() {
			var x = this.x;
			this.x = -this.y
			this.y = x;
		},

		/**
		 * Show this arrow as a string for debugging
		 */
		toString : function() {
			var label = "{";
			var first = true;
			for(var p in this) {
				if(typeof this[p] === "function")
					continue;
				if(first)
					first = false;
				else
					label += ",";
				label += p+":"+ this[p];
			}
			return label + "}";

		},
}
Links.prototype.type = undefined;

function Links(){
	
	Links.prototype={
		canBeTarget:function(target){alert("Function canbeTarget Not defined")},
		canBeSource:function(source){alert("canBeSource")}
	}
	
}

EntityAtribuite.prototype=Object.create(Links.prototype);
function EntityAtribuite(){
	
	this.type="entityAtribuite";
	
	
}

EntityAtribuite.prototype={	
		canBeTarget:function(target){if(target.type=="attribute")return true; return false;},
		canBeSource:function(source){if(source.type=="entity")return true; return false;}
}

EntityEntity.prototype=Object.create(Links.prototype);
function EntityEntity(){
	this.type="entityEntity";
	
}
EntityEntity.prototype={	
		canBeTarget:function(target){if(target.type=="entity")return true; return false;},
		canBeSource:function(source){if(source.type=="entity")return true; return false;}
}
EntityRelationship.prototype=Object.create(Links.prototype);
function EntityRelationship(){
	
	this.type="entityRelationship";
	this.total=false;
	this.cardinality="";
}

EntityRelationship.prototype={	
		
		canBeTarget:function(target){if(target.type=="entity")return true; return false;},
		canBeSource:function(source){if(source.type=="relationship")return true; return false;}
}

AtribuiteAtribuite.prototype=Object.create(Links.prototype);
function AtribuiteAtribuite(){
	this.type="atribuiteAtribuite";

}
AtribuiteAtribuite.prototype={	
		canBeTarget:function(target){if(target.type=="attribute")return true; return false;},
		canBeSource:function(source){if(source.type=="attribute")return true; return false;}
}

AtribuiteRelationship.prototype=Object.create(Links.prototype);
function AtribuiteRelationship(){
	
	this.type="atribuiteRelationship";

}
AtribuiteRelationship.prototype={	
		canBeTarget:function(target){if(target.type=="attribute")return true; return false;},
		canBeSource:function(source){if(source.type=="relationship")return true; return false;}
}

RelationshipRelationship.prototype=Object.create(Links.prototype);
function RelationshipRelationship(){
	this.type="relationshipRelationship";

}
RelationshipRelationship.prototype={	
		canBeTarget:function(target){if(target.type=="relationship")return true; return false;},
		canBeSource:function(source){if(source.type=="relationship")return true; return false;}
}

EntityEspGenCat.prototype=Object.create(Links.prototype);
function EntityEspGenCat(){
	this.type="entityEspGenCat";
	this.total=false;
		
}
EntityEspGenCat.prototype={	
		canBeTarget:function(target){if(target.type=="espGenCat")return true; return false;},
		canBeSource:function(source){if(source.type=="entity")return true; return false;}
}

SimpleNodeOther.prototype=Object.create(Links.prototype);
function SimpleNodeOther(){
	this.type="simpleNodeOther";
}
SimpleNodeOther.prototype={	
		canBeTarget:function(target){if(target.type=="entity" ||target.type=="attribute" ||target.type=="relationship" ||target.type=="espGenCat")return true; return false;},
		canBeSource:function(source){if(source.type=="simpleNode")return true; return false;}

}


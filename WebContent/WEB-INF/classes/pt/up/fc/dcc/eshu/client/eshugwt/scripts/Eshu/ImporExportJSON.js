

Eshu.prototype.exportGraph = function() {	
//	console.log(this.graph.getGraph());
	var graph = this.graph.getGraph();
	graph.reducibles=this.reducibles;
	return JSON.stringify(graph,null,1);
//	return this.graph.getGraph();
}

Eshu.prototype.importGraph = function(graph) {	
	//this.graph.setGraph();
	this.graph.clearGraph(); 
	var objt = JSON.parse(graph);

	var nodes=objt.nodes;
	var edges=objt.edges;
//	console.log(edges)

	var width=0;
	var height=0;
	
	var maxID=0; 
	for (var i = 0; i < nodes.length; i++) {
		var type = this.elementsTypes.get(nodes[i].type);
		if(!type)continue;
		var node = this.graph.createNodeImport(type,nodes[i] );
		if (node.id>maxID) maxID=node.id;
		node.adjustLabel(this.ctx);
		this.graph.addNode(node);

	}
		
//	this.updateCanvasSize();
	this.updateCanvasSizeImport();
	
		
	for (var i = 0; i < edges.length; i++) {
		
		var edgeJ=edges[i]
		var type = this.elementsTypes.get(edgeJ.type);
		if(!type)continue;
		var edge=this.graph.createEdge(type,edgeJ.id,this.getNodeById(edgeJ.source),
				this.getNodeById(edgeJ.target),edgeJ.listHandler);
		if (edge.id>maxID) maxID=edge.id;
//		console.log("Teste "+edgeJ.featuresSource);
//		var fs =edgeJ.featuresSource
//		for(k in fs )
//			console.log(fs[k].name + " "+ fs[k].value)
			
		edge.setFeatures(edgeJ.features);
//		edge.setFeaturesSource(edgeJ.featuresSource);
		edge.setFeaturesTarget(edgeJ.featuresTarget);
//		console.log("edge "+ "id "+edge.id+ "x "+edge.x + "y "+edge.y+ "W "+edge.width+ "H "+edge.height)
		this.graph.addEdge(edge);
		
	}
	
	this.generateID=maxID+1;
	this.clearMenuProperty();
	this.draw();
}


Eshu.prototype.importGraphDiff = function(graph) {	
//	alert(graph);
	if(graph == undefined ||graph =="{}" || graph =="")
		return;
	//this.graph.setGraph();
	
	var nodesE=this.graph.nodes.getAll();
	var edgesE=this.graph.edges.getAll();

	
	var objt = JSON.parse(graph);
	var nodes=objt.nodes;
	var edges=objt.edges;
	var note=objt.note;
//	console.log("Test "+JSON.stringify(nodes))
	if(nodes){
		for (var i = 0; i < nodes.length; i++) {
			if(!nodes[i])
				continue;
			
			if(!nodes[i].type)
				continue;
			
			var type = this.elementsTypes.get(nodes[i].type);
			if(!type)continue;
			if(nodes[i].temporary!="insert"){
				this.graph.setGraph();
				for(var n=0; n<nodesE.length;n++)
					if(nodes[i].modify!="insert" && nodesE[n].id==nodes[i].id)
						nodesE[n].modify=nodes[i].temporary;
			}		
			else{
				this.clear();
				nodes[i].id=this.generateID++;
				var node = this.graph.createNodeImportDiff(type,nodes[i]);
//				node.modify="insert";
				node.adjustLabel(this.ctx);
//				this.redraw();
			}
			
		}
	}
	if(edges){
		for (var i = 0; i < edges.length; i++) {
			var type = this.elementsTypes.get(edges[i].type);
			if(!type)continue;
			
			if(edges[i].temporary!="insert"){
				for(var e=0; e<edgesE.length;e++){
					if(edgesE[i].id==edges[i].id){
						edgesE[e].modify=edges[i].temporary;
					}
				}
			}
				
			else {
				
				this.graph.createEdgeImport(type,edges[i]);
//				var sourceId=this.getNodeById(edges[i].source)
//				var targetId=this.getNodeById(edges[i].target)
//				if(sourceId && targetId){
//					
//					var edge=this.graph.createEdge(type,edges[i].id,sourceId,
//							targetId,edges[i].listHandler);
//					edge.setFeatures(edges[i].features);
//					edge.modify="insert";
//					this.graph.addEdge(edge);
//					}
				 }
		  }
	
	 }
	this.clearMenuProperty();
	this.draw();
}

Eshu.prototype.getNodeById= function(id){
	var nodes = this.graph.getAllNodes();
	for(var i in nodes)
		if(nodes[i].id==id)
			return nodes[i];
}






//<?xml version="1.0" encoding="UTF-8" standalone="no"?>
//<course xmlns="http://www.example.org/Seqins" author="Jose Paulo Leal" creationDate="2017-06-13T00:00:00.000Z" endDate="2017-12-31T00:00:00.000Z" id="kora_test" mode="RELAX" name="Kora Course Prototype" recommender="pt.up.fc.dcc.seqins.server.recommendation.impl.SequentialRecommender" startDate="2017-06-13T00:00:00.000Z">
//     
//       
//            <coursepart id="m4" minimumWeight="0" name="ER Diagram" start="EER">
//                <resource href="http://www.alunos.dcc.fc.up.pt/~up201108850/eerEN/er.pdf" id="EER" tags="pdf" title="ER Models" type="PDF"/>
//                 <resource href="https://www.youtube.com/watch?v=7SCsKsfQ8EA&t=212" id="example" tags="video" title="Editing example" type="VIDEO"/>
//                <resource href="https://mooshak.dcc.fc.up.pt/v2/proto_diagram/A" id="A" title="A - Student's Profile" type="PROBLEM" onSuccess="B" onFail="EER"/>
//                 <resource href="https://mooshak.dcc.fc.up.pt/v2/proto_diagram/B" id="B" title="B - Classes" type="PROBLEM" onSuccess="C" onFail="EER"/>
//                 <resource href="https://mooshak.dcc.fc.up.pt/v2/proto_diagram/C" id="C" title="C - Music" type="PROBLEM" onSuccess="D" onFail="EER"/>
//                 <resource href="https://mooshak.dcc.fc.up.pt/v2/KoraValidation/D" id="D" title="D - Company" type="PROBLEM" onSuccess="D" onFail="EER"/>
//            </coursepart>
//        
//          
//       
//      
//
//</course>

package ModelExample;

import java.util.LinkedList;

import be.fnord.jbpt.extend.altJSON2Process;
import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.GraphChecker;
import be.fnord.util.processModel.GraphLoader;
import be.fnord.util.processModel.PGraph;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.io.BMIJSON2Graph;


public class ModelLoadingExample {
	
	
	
	public static void main(String[] args) {
		/////////////////////////////////////////
		/* Initialize core app. */ new a.e();  // 
		/////////////////////////////////////////
		//// Real start of program below	/////
		/////////////////////////////////////////
		
		Graph<Vertex,Edge> g1 = GraphLoader.loadModel("models/proc1.bpmn20.xml");
		//System.out.println("G1-" + g1);
		
		Graph<Vertex,Edge> g2 = GraphLoader.loadModel("models/proc2.bpmn20.xml");
		//System.out.println("G2-" + g2);
		
		// Outputs True if Strongly connected
		// and other syntactic checks are cleared
		GraphChecker g1Checker = new GraphChecker();
		GraphChecker g2Checker = new GraphChecker();
		System.out.println("G1 Test: " + g1Checker.CheckGraph(g1));
		System.out.println("G2 Test: " + g2Checker.CheckGraph(g2));
		
		// The jbpt algorithms appear mangled for their DirectedGraphAlgorithms, to use these functions 
		// we need to recreate all edges with a new interface. Something to do later
		
		System.out.println("Trying to load a json file attached to the jbptTests");
		Graph<Vertex,Edge> g3 = altJSON2Process.convert("988654311_rev1.json", altJSON2Process.readFile("models/a.s00000112__s00003260.tpn_0.json"));
		System.out.println("Displaying JSON file form jbptTests:" + g3.toString());
		
		// Loading the BMI json files: 
		Graph<Vertex,Edge> g4 = BMIJSON2Graph.convert("1582472422_rev18.json", altJSON2Process.readFile("models/1582472422_rev18.json"));
		System.out.println("Displaying JSON based model:" + g4.toString());
		System.out.println("G4 Test: " + g2Checker.CheckGraph(g4));
		
		//PGraph<Vertex,Edge> seqPGraph = seqComp(g1, g2);
		//System.out.println("PG-" + seqPGraph);
		
		return ;
		
	}
	
	public static LinkedList<PGraph<Vertex, Edge>> makeDecisionFree(Graph<Vertex,Edge> g){
		LinkedList<PGraph<Vertex, Edge>> result = new LinkedList<PGraph<Vertex, Edge>>();
		boolean continuer = false;
		// First Check for XOR's
		for(Vertex v: g.vertexSet()){
			if(v.isXOR) continuer = true;
		}
		if(!continuer) { // No XOR's found 
			PGraph<Vertex, Edge> pg = new PGraph<Vertex,Edge>(Edge.class);
			pg.copyInGraph(g);
			result.add(pg);
			return result;
		}
		// XOR's found, lets remove all joins
		// while searching lets save start/end points
//		TreeMap<String, String> startEnds = new TreeMap<String,String>();
//		LinkedList<String> gates = new LinkedList<String>();
//		for(Edge e: g.edgeSet()){
//			
//		}
		
		
		return result;
	}
	
	
	
	public static PGraph<Vertex,Edge> seqComp(Graph<Vertex,Edge> lhs, Graph<Vertex,Edge> rhs ){
		PGraph<Vertex,Edge> result = new PGraph<Vertex,Edge>(Edge.class);
		
		
		return result;
	}
	

}

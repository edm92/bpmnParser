package be.fnord.util.processModel.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Vertex;

/**
 * Transformation functions for graphs
 * 
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 * Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */
public class GraphTransformer {
	public static boolean __DEBUG = a.e.__DEBUG;
	public static boolean __INFO  = a.e.__INFO;
	
	/**
	 * Remove duplicate decision free graphs.
	 * @param duped
	 * @return
	 */
	public static LinkedList<Graph<Vertex,Edge>> removeDupes(LinkedList<Graph<Vertex,Edge>> duped){
		if(a.e.DEDUPING_LEVEL == a.e.NO_DEDUPING) return duped;		
		
		LinkedList<Graph<Vertex, Edge>> deDuped = new LinkedList<Graph<Vertex, Edge>>();
		LinkedList<String> hashedDecisionFreeGraphs = new LinkedList<String>();
		
		for(Graph<Vertex,Edge> g: duped){
			String key = hashDecisionFreeGraph(g);
			
			if(!hashedDecisionFreeGraphs.contains(key)) { hashedDecisionFreeGraphs.add(key); deDuped.add(g);}
			//else a.e.println("Found duped graph " + key);
		}
		return deDuped;
	}

	
	/**
	 * Create a hash for a graph. This is not a really safe function as edges have been removed from the dedupe process
	 * @param in
	 * @return String representing a hash of a particular graph based on the edges and nodes.
	 */
	public static String hashDecisionFreeGraph(Graph<Vertex, Edge> in){
		String result = "";
		TreeSet<String> vertices = new TreeSet<String>();
		TreeSet<String> edges = new TreeSet<String>();
		for(Vertex v: in.vertexSet()) {
			vertices.add((v.toString().length() < 4 ? v.toString().substring(0, v.toString().length()) : v.toString().substring(0,4) ).trim());
		}
		for(Edge e: in.edgeSet()) {
			edges.add((e.id.toString().length() < 4 ? e.id.toString().substring(0, e.id.toString().length()) :  e.id.toString().substring(0,4)).trim());
		}
		
		for(String s: vertices.descendingSet())
			result += s;
		
		if(a.e.DEDUPING_LEVEL == a.e.SIMPLE_DEDUPING)
		for(String s: edges.descendingSet())
			result += s;
		
		
		return result;
	}
	
	
	/**
	 * Create a set of decision free process model from an input model. This will split models to have only one start event etc.
	 *  
	 * @param g Input graph
	 * @return LinkedList<PGraph<Vertex,Edge>> 
	 * 
	 */
	public static LinkedList<Graph<Vertex, Edge>> makeDecisionFree(Graph<Vertex,Edge> g){
		LinkedList<Graph<Vertex, Edge>> result = new LinkedList<Graph<Vertex, Edge>>();
		LinkedList<String> startEvents = new LinkedList<String>();
		LinkedList<String> joinGates = new LinkedList<String>();
		LinkedList<String> splitGates = new LinkedList<String>();

		boolean continuer = false;
		int startCounter = 0;
		int boundaryCounter = 0;
		// First Check for XOR's, boundaries and multiple starts
		for(Vertex v: g.vertexSet()){
			if(v.isXOR) { 
							if(g.inDegreeOf(v) > 1)	// Only add in joins
								joinGates.add(v.id);
							if(g.outDegreeOf(v) > 1){	// Only add in splits
								splitGates.add(v.id);
								continuer = true;
							}
						}
								
			if(v.type == GraphLoader.StartEvent) {
				
				if(!startEvents.contains(v.id)){
					startCounter++; startEvents.add(v.id);}
			}
		}

		if(startCounter > 1) continuer = true;
		if(boundaryCounter > 1) continuer = true;
		
		if(!continuer) { // No XOR's or many starts found, nothing to do so lets add the graph to the return 
			Graph<Vertex, Edge> pg = new Graph<Vertex,Edge>(Edge.class);
			pg.copyInGraph(g);
			result.add(pg);
			return result;
		}
		if(__DEBUG){
			a.e.print( " Joins: ");
			for(String gateID : joinGates){
				a.e.print(g.vertexRef.get(g.vertexIDRef.get(gateID)).toString() + ", ");
			}
			a.e.println("");
			a.e.print( " Splits: ");
			for(String gateID : splitGates){
				a.e.print(g.vertexRef.get(g.vertexIDRef.get(gateID)).toString() + ", ");
			}
			a.e.println("");
		}	
		// Handle Multiple Starts
		if(startCounter > 1){
			LinkedList<Graph<Vertex, Edge>> corrected = new LinkedList<Graph<Vertex, Edge>>();
			for(String startID : startEvents){
				Vertex startEvent = g.vertexRef.get(g.vertexIDRef.get(startID));

				// Make a new graph
				if(startEvent != null){
					Graph<Vertex, Edge> pg = new Graph<Vertex,Edge>(Edge.class);
					pg.copyInGraph(g, startEvent);
//					a.e.println("Processing " + startEvent.toString());
//					a.e.println("Starting with " + pg.toString());
					LinkedList<Graph<Vertex, Edge>> revised = makeDecisionFree(pg);
					corrected.addAll(revised);
//					a.e.println("Got: " + revised.toString());
				}
			}
			// Send each of the new subgraphs to the function again to deal with any and all XOR's			
			for(Graph<Vertex, Edge> current : corrected){
				LinkedList<Graph<Vertex, Edge>> revised = makeDecisionFree(current);
				
				//corrected.addAll(revised);
				result.addAll(revised); 
			}
		}
		
		// XOR FOUND 
		// Look for paths from each start event to our gate
		
		for(String startID : startEvents){
		
			Vertex startEvent = g.vertexRef.get(g.vertexIDRef.get(startID));
			if(__DEBUG) a.e.println("Processing " + startEvent.toString());
			for(String gateID : splitGates){
				// Small amount of error checking
				if(g.vertexIDRef.get(gateID) == null) { 
					if(__DEBUG) a.e.println("Can't find gateway " + gateID + " skipping fragmenting");
					continue;
				}
				
				Vertex gateway = g.vertexRef.get(g.vertexIDRef.get(gateID));
				if(__DEBUG) a.e.println("Gateway: " + gateway.toString());
				
				FloydWarshallShortestPaths<Vertex, Edge> pather = new FloydWarshallShortestPaths<Vertex, Edge>(g);
				GraphPath<Vertex,Edge> gp = null;
				try{
					gp = pather.getShortestPath(startEvent, gateway);
				}catch(Exception e){
					a.e.println("Error computing shortest path", a.e.FATAL);
				}
				if(gp != null){
					// Get vertex before the gate and list after the gate
					Vertex predGate = null;
					for(Edge pe : g.incomingEdgesOf(gateway)){
						predGate = pe.getSource();
					}
					LinkedList<Vertex> succGates = new LinkedList<Vertex>();
					for(Edge pe : g.outgoingEdgesOf(gateway)){
						succGates.add(g.getEdgeTarget(pe));
					}
					// Remove the gateway
					
					for(Vertex successor : succGates){
						// Now we have a path lets build a fragment
						Graph<Vertex, Edge> pg = new Graph<Vertex,Edge>(Edge.class);
						pg.copyInGraph(g, startEvent, predGate);
						LinkedList<Graph<Vertex, Edge>> startFragment = makeDecisionFree(pg);
//						a.e.println("222Made graph " + pg.toString());

						Graph<Vertex, Edge> pg2 = new Graph<Vertex,Edge>(Edge.class);
						pg2.copyInGraph(g, successor);
//						a.e.println("Now working on " + pg2.toString());
						LinkedList<Graph<Vertex, Edge>> endFragment = makeDecisionFree(pg2);
						// Join the fragments together
						// Replaced predGate with gateway to try to include gateway node
						result.addAll(merge(startFragment, endFragment, predGate, gateway, successor));
					}
				}else{
					if(__DEBUG) a.e.println("GP was null for " + " from " + startEvent + " to " + gateway ); 
				}
			}
		}
		
//		// Remove XOR joins
//		// XOR's found, lets first remove all joins
//		// 
//		for(String gateID : joinGates){
//			Vertex gateway = g.vertexRef.get(gateID);
//			if(gateway == null) {
//				if(__DEBUG) a.e.println("Can't find " + gateID + " skipping");
//				continue;
//			}
//			if(gateway.type == GraphLoader.ExclusiveGateway && gateway.isJoin){
//				// Remove
//				LinkedList<Edge> inEdges = new LinkedList<Edge>();
//				for(Edge e : g.incomingEdgesOf(gateway))
//					inEdges.add(e);
//				Edge outEdge = null ;
//				for(Edge e: g.outgoingEdgesOf(gateway))
//					outEdge = e;
//				if(outEdge == null){ a.e.println("Error here!",a.e.FATAL);}; 
//				for(Edge e: inEdges){
//					g.removeE(e);
//					e.setTarget(outEdge.target);
//					g.addE(e);
//				}
//				g.removeE(outEdge);
//			}
//		}
//			
		
		
		return result;
	}
	
	public static LinkedList<Graph<Vertex, Edge>> merge(LinkedList<Graph<Vertex, Edge>> startFrags, LinkedList<Graph<Vertex, Edge>> endFrags, Vertex endOfPred, Vertex gateway, Vertex startOfSucc){
		LinkedList<Graph<Vertex, Edge>> results = new LinkedList<Graph<Vertex, Edge>>();
		for(Graph<Vertex, Edge> predFrag : startFrags){
			for(Graph<Vertex, Edge> succFrag : endFrags){
				Graph<Vertex, Edge> pg = new Graph<Vertex,Edge>(Edge.class);
				pg.copyInGraph(predFrag);
				pg.copyInGraph(succFrag);
				pg.addV(endOfPred);
				pg.addV(gateway);
				pg.addV(startOfSucc);
				pg.addE(new Edge(endOfPred, gateway));
				pg.addE(new Edge(gateway, startOfSucc));
				results.add(pg);
//				a.e.println("Made a graph : " + pg.toString());
			}
		}		
		return results;
	}
	
	
	public static Graph<Vertex,Edge> seqComp(Graph<Vertex,Edge> lhs, Graph<Vertex,Edge> rhs ){
		Graph<Vertex,Edge> result = new Graph<Vertex,Edge>(Edge.class);
		
		
		return result;
	}
	

}

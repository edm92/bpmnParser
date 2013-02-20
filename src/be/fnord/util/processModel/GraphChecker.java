package be.fnord.util.processModel;

import java.util.LinkedList;
import java.util.TreeMap;
import java.util.UUID;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.alg.StrongConnectivityInspector;

public class GraphChecker {
	public static boolean __DEBUG = a.e.__DEBUG;
	public static boolean __INFO  = a.e.__INFO;

	public static final int MAX_TIMES_TRY_FIX = 1;	// Cut off point for fixing gateway issues
	public static final int MAX_PATH_LENGTH = 100;	// Cut off point for processing gateway fixes

	
	private int NumberFixed = 0;	
	LinkedList<String> startNodes = new LinkedList<String>();
	LinkedList<String> endNodes = new LinkedList<String>();
	

	
	/**
	 * Test if gateways in a process model appear to be processable.
	 * @param g
	 * @return
	 */
	private boolean testGateways(Graph<Vertex,Edge> g){
		int openAND = 0;
		int closeAND = 0;
		for(Vertex v: g.vertexSet()){
			// Count number of gateways first
			if(v.type.compareToIgnoreCase("gateway") == 0){
				if(v.getCorresponding() == null && !v.isXOR) {	// Check if we labeled a corresponding gateway
					if(__DEBUG) a.e.println("Didn't find a corresponding gate for " + v.toString());
					return false;
				}else{
					if(!v.isXOR) // ignore corresponding XOR's, we'll throw them out anyway.
					if(v.getCorresponding().getCorresponding().id.toString().compareTo(v.id.toString()) != 0){
						if(__DEBUG) a.e.println("Corresponding gate has been associated to something else " + v.toString() + "; " + v.getCorresponding().toString());
						if(__DEBUG) a.e.println("Corresponding gate has been associated to something else " + v.getCorresponding().toString() + "; " + v.getCorresponding().getCorresponding().toString());
						return false;
					}
				}
				if(g.inDegreeOf(v) > 1) closeAND++;
				if(g.outDegreeOf(v) > 1) openAND++;
				
			}
		}
		if(openAND == closeAND){
			return true;
		}
		return false;
	}
	
	
	/**
	 * Check the graph for good structures, fix some if possible and remove the rest
	 * This function will add a substructural start and end node to the process which means that the process will only end 
	 * up with a single start and a single end. 
	 * @return True if good structured model, false if badly structured model beyond repair
	 */
	public boolean CheckGraph(Graph<Vertex,Edge> g){
		// Find the start and end nodes, then check if strongly connected.
		for(Vertex v: g.vertexSet() ){
			// Check if start node
			if(g.inDegreeOf(v) == 0){
				startNodes.add(v.toString());
			}
			// Check if end node
			if(g.outDegreeOf(v) == 0){
				endNodes.add(v.toString());
			}
		}
		
		// Go through list looking for elements in both
		LinkedList<String> unconnectedNodes = new LinkedList<String>();
		for(String s: startNodes){
			for(String e: endNodes){
				if(s.compareTo(e) == 0){
					unconnectedNodes.add(s);
				}
			}
		}
		// Remove these elements
		for(String u: unconnectedNodes){
			startNodes.remove(u);
			endNodes.remove(u);
			g.removeVertex(g.vertexRef.get(u));
			g.vertexRef.remove(u);
			g.existingVertices.remove(u);
			if(__DEBUG) a.e.println("Removed unconnected node: " + u);
		}
		
		// Check if we have at least one start and end
		if(!(startNodes.size() > 0 && endNodes.size() > 0)) return false;
		
		// Create copy of graph and connect the ends to the starts
		Graph <Vertex,Edge> copy = g.copyGraph(g);
		for(String s: startNodes){
			for(String e: endNodes){
				Edge newEdge = new Edge(g.vertexRef.get(e), g.vertexRef.get(s));
				copy.addE(newEdge);
			}
		}
		// Check if each node is reachable
		StrongConnectivityInspector<Vertex, Edge> sci =
	            new StrongConnectivityInspector<Vertex,Edge>(copy);
		if(!sci.isStronglyConnected()) return false;
		
		// Finally add structural nodes for multi-starts/multi-ends
		// This includes a parallel gateway for splitting multiplestarts
		if(startNodes.size() > 1){
			Vertex newStart = new Vertex("newStart-" + UUID.randomUUID(), "gateway");
			newStart.isAND = true; 
			newStart.isSplit = true;
			newStart.isSubstructural = true;
			newStart.corresponding = null;
			g.addV(newStart);
			g.trueStart = newStart;
			for(String s: startNodes){
				// Create edge to each node
				Edge newEdge = new Edge(newStart, g.vertexRef.get(s));
				g.addE(newEdge);
			}
		}else g.trueStart = g.vertexRef.get(startNodes.get(0));
		
		if(endNodes.size() > 1){
			Vertex newEnd = new Vertex("newEnd-" + UUID.randomUUID(), "gateway");
			newEnd.isAND = true;
			newEnd.isJoin = true;
			newEnd.isSubstructural = true;
			g.addV(newEnd);
			g.trueEnd = newEnd;
			
			if(g.trueStart.type.compareTo("gateway") == 0) g.trueStart.setCorresponding(newEnd);
			for(String s: startNodes){
				// Create edge to each node
				Edge newEdge = new Edge(g.vertexRef.get(s), newEnd );
				g.addE(newEdge);
			}
		}else g.trueEnd = g.vertexRef.get(endNodes.get(0));
		// Do cleanup (add in missing gateway to start of process)
		if(g.trueEnd.type.compareTo("gateway") == 0 && g.trueEnd.corresponding == null){
			Vertex newStartGate = new Vertex("newStartGate-" + UUID.randomUUID(), "gateway");
			newStartGate.isAND = true; 
			newStartGate.isSplit = true;
			newStartGate.isSubstructural = true;
			newStartGate.setCorresponding(g.trueEnd);
			g.addV(newStartGate);
			// Replace edges from the start node to vertices and recreate from the new start node gateway
			LinkedList<Edge> removeList = new LinkedList<Edge>();
			for(Edge e: g.outgoingEdgesOf(g.trueStart)){
				Edge newEdge = new Edge(newStartGate, e.getTarget());
				g.addE(newEdge);
				removeList.add(e);
			}
			for(Edge e: removeList){
				g.removeE(e);
			}
			// Add and edge from the start to the new Gateway
			Edge newEdge = new Edge(g.trueStart, newStartGate);
			g.addE(newEdge);
		}
		// Add in missing gateway to the end of process
		if(g.trueStart.type.compareTo("gateway") == 0 && g.trueStart.corresponding == null){
			Vertex newEndGate = new Vertex("newEndGate-" + UUID.randomUUID(), "gateway");
			newEndGate.isAND = true; 
			newEndGate.isSplit = true;
			newEndGate.isSubstructural = true;
			newEndGate.setCorresponding(g.trueStart);
			g.addV(newEndGate);
			// Replace edges from the start node to vertices and recreate from the new start node gateway
			LinkedList<Edge> removeList = new LinkedList<Edge>();
			for(Edge e: g.incomingEdgesOf(g.trueEnd)){
				Edge newEdge = new Edge(e.getSource(), newEndGate);
				g.addE(newEdge);
				removeList.add(e);
			}
			for(Edge e: removeList){
				g.removeE(e);
			}
			// Add and edge from the start to the new Gateway
			Edge newEdge = new Edge(newEndGate, g.trueEnd);
			g.addE(newEdge);
		}

		
		
		// Test all non-gateway nodes for only one entry and one exit
		for(Vertex v: g.vertexSet()){
			if(!(v.isAND || v.isXOR || v.isOR)){
				if(g.inDegreeOf(v) > 1 || g.outDegreeOf(v) > 1){
					if(__DEBUG) a.e.println("Vertex " + v.name + " has a large in/out degree for a non-gateway");
					return false;
				}
			}else{	// Ensure that gateways are either a split or a join
				if(g.inDegreeOf(v) > 1 && g.outDegreeOf(v) > 1){
					if(__DEBUG) a.e.println("Gateway " + v.name + " is used as both a split and a join.");
					return false;
				}
			}
		}
		
		// Check if there are correct gateways
		fixGateways(g); // See if we can fix the gateways first then test them
		if(!testGateways(g)) return false;
		
		return true;
	        
	}
	
	/**
	 * Search graph for corresponding gateways and attempt to fix small structural issues
	 * @param g input Graph
	 * @return return true if no errors needed fixing and false if there were some errors found
	 */
	@SuppressWarnings("unused")
	private boolean fixGateways(Graph<Vertex,Edge> g) {
		int openAND = 0;
		int closeAND = 0;
		boolean result = true;
		for(Vertex v: g.vertexSet()){
			// Count number of gateways first
			if(v.type.compareToIgnoreCase("gateway") == 0){
				if(g.inDegreeOf(v) > 1) closeAND++;
				if(g.outDegreeOf(v) > 1) openAND++;
			}
		}
		String msg = "";
		if(openAND == closeAND){
			msg = "gateway count matches";
			// Add corresponding gates
			fixCorrespondingGates(g);
		}else{
			msg = "gateway count mismatch";

			if(closeAND < openAND)
				msg += " ; less closing AND gates than opening AND gates";
			else
				msg += " ; more closing AND gates than opening AND gates";
			fixLessCloseGates(g);
		}
		if(__DEBUG && false)	a.e.println("msg:" + msg);	// This is for detailed debugging only
		if(++NumberFixed < MAX_TIMES_TRY_FIX) fixGateways(g);
		return result;
	}
	
	
	private void fixLessCloseGates(Graph<Vertex,Edge> g) {
		TreeMap<String, Integer> scoreTallyOut = new TreeMap<String, Integer>();
		TreeMap<String, Integer> scoreTallyIn = new TreeMap<String, Integer>();
		LinkedList<String> gateways = new LinkedList<String>();
		// Make paths through the graph
		KShortestPaths<Vertex, Edge> sp = new KShortestPaths<Vertex, Edge>(g, g.trueStart, MAX_PATH_LENGTH);
		for(GraphPath<Vertex, Edge> gp : sp.getPaths(g.trueEnd)){
			// Walk along paths keeping score of open and close gateways
			Vertex start = gp.getStartVertex();
			Vertex end = gp.getEndVertex();
			Vertex current = start;
			//System.err.println("Viewing current path " + gp);
			//System.err.println("Starting with " + start);
			do{		
				if(current == null) break;
				if(current.type.compareToIgnoreCase("gateway") == 0){
					if(g.inDegreeOf(current) > 1){
						scoreTallyIn.put(current.name, g.inDegreeOf(current));
						gateways.add(current.name);
					}else if(g.outDegreeOf(current) > 1){
						scoreTallyOut.put(current.name, g.outDegreeOf(current));
						gateways.add(current.name);
					}
				}				
				current = next(current, gp, g);
			}while(current != null || current != end);
			
			
			// Now check along path
			String last = "";
			boolean first = true;
			for(String currentGate: gateways){
				if(first){ last = currentGate; first = false; continue;}; // Skip first gateway
				
				if(!(scoreTallyOut.get(last) == null || scoreTallyIn.get(currentGate) == null)){
				if(scoreTallyOut.get(last) > scoreTallyIn.get(currentGate)){
					if(__INFO) a.e.println("Found point of interest: " + last + ":" + scoreTallyOut.get(last) + " vs. " + currentGate + ":" + scoreTallyIn.get(currentGate));
				}//else System.err.println("No point of interest: " + last + ":" + scoreTallyOut.get(last) + " vs. " + currentGate + ":" + scoreTallyIn.get(currentGate));
				}
				last = currentGate;
			}
		}
	}	
	
	private void fixCorrespondingGates(Graph<Vertex,Edge> g) {
		TreeMap<String, Integer> scoreTallyOut = new TreeMap<String, Integer>();
		TreeMap<String, Integer> scoreTallyIn = new TreeMap<String, Integer>();
		LinkedList<String> gateways = new LinkedList<String>();
		
		KShortestPaths<Vertex, Edge> sp = new KShortestPaths<Vertex, Edge>(g, g.trueStart, MAX_PATH_LENGTH);
		for(GraphPath<Vertex, Edge> gp : sp.getPaths(g.trueEnd)){
			// Walk along paths keeping score of open and close gateways
			Vertex start = gp.getStartVertex();
			Vertex end = gp.getEndVertex();
			Vertex current = start;
			//System.err.println("Viewing current path " + gp);
			//System.err.println("Starting with " + start);
			do{		
				if(current == null) break;
				if(current.type.compareToIgnoreCase("gateway") == 0){
					//scoreTallyIn.put(current.name, 0);
					if(g.inDegreeOf(current) > 1){
						// gatway join
						scoreTallyIn.put(current.name, g.inDegreeOf(current));
						gateways.add(current.name);
					}else if(g.outDegreeOf(current) > 1){
						scoreTallyOut.put(current.name, g.outDegreeOf(current));
						gateways.add(current.name);
					}
				}				
				current = next(current, gp,g);
			}while(current != null || current != end);
		}
		
		loopyfix(gateways, scoreTallyIn, scoreTallyOut,g);
		
	}

	private void loopyfix(LinkedList<String> gateways,
			TreeMap<String, Integer> scoreTallyIn,
			TreeMap<String, Integer> scoreTallyOut, 
			Graph<Vertex,Edge> g) {
		if(gateways.size() < 1) return;
		String last = gateways.get(0);
		boolean starter = true;
		LinkedList<String> rmList = new LinkedList<String>();
		LinkedList<String> contList = new LinkedList<String>();
		for(String s: gateways){
			if(starter) {starter = false; continue;};
			if(scoreTallyOut.get(last) != null && scoreTallyIn.get(s) != null)
			if(scoreTallyOut.get(last) > 1 && scoreTallyIn.get(s) > 1){
				Vertex pntSt = null; Vertex pntEd = null; 
				for(Vertex v: g.vertexSet()){
					if(v.name.compareTo(last) == 0)
						pntSt = v;
				}
				for(Vertex v: g.vertexSet()){
					if(v.name.compareTo(s) == 0)
						pntEd = v;
				}
				if(pntSt != null && pntEd != null){
					//System.err.println("Setting correspoinding" + pntSt + "," + pntEd);
					pntSt.setCorresponding(pntEd);
					rmList.add(last); rmList.add(s);
				}
			}
			last = s;
		}
		for(String ss: gateways){
			if(!rmList.contains(ss))
				contList.add(ss);
		}
		if(contList.size() > 1) loopyfix(contList, scoreTallyIn, scoreTallyOut,g );
		
	}
	
	private Vertex next(Vertex current, GraphPath<Vertex, Edge> gp, Graph<Vertex,Edge> g) {
		for(Edge e: g.outgoingEdgesOf(current)){
			if(gp.getEdgeList().contains(e)){
				return gp.getGraph().getEdgeTarget(e);
			}
		}
		return null;
	}

}

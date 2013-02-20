package be.fnord.util.processModel;

import java.util.LinkedList;
import java.util.TreeMap;

public class PGraph<v extends Vertex, e extends Edge> extends Graph<Vertex, Edge> {
	private static final long serialVersionUID = 1L;
	// List of all graphs
	public static TreeMap<String, Graph<Vertex,Edge>> allGraphs = new TreeMap<String, Graph<Vertex,Edge>>();	
	
	public LinkedList<String> existingGraph = new LinkedList<String>();
	public TreeMap<String, Graph<Vertex,Edge>> graphRef = new TreeMap<String,Graph<Vertex,Edge>>();
	
	public LinkedList<String> existingTrace = new LinkedList<String>();
	public TreeMap<String, Trace<Vertex,Edge>> traceRef = new TreeMap<String,Trace<Vertex,Edge>>();
	
	public PGraph(Class<Edge> arg0) {
		super(arg0);
	}
	
	/**
	 * Copy a graph into the PGraph structure
	 * @param g
	 */
	public void copyInGraph(Graph<Vertex, Edge> g){
		if(this.existingGraph.contains(g.name)) return;
		
		for(Vertex v: g.vertexSet())
			this.addV(v);
		for(Edge e: g.edgeSet())
			this.addE(e);
		this.existingGraph.add(g.name);
		this.graphRef.put(g.name, g);
	}

}

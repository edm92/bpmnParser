package be.fnord.util.processModel;

import java.util.LinkedList;

/**
 * 
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 * Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 *
 * @param <v>
 * @param <e>
 */

public class Trace extends Vertex{
	public boolean INCLUDE_EDGE = false;
	private static final long serialVersionUID = 1L;
	public Trace(String _name) {	super(_name);	isTrace = true;}
	public Trace() { super() ; isTrace = true;}

	LinkedList<Vertex> nodes = new LinkedList<Vertex>();
	LinkedList<Edge> edges = new LinkedList<Edge>();
	public boolean parallel = false;
	
	public void addTraceNode(Vertex vert){
		nodes.add(vert);
	}
	
	public void addTraceEdge(Edge edge){
		edges.add(edge);
	}
	
	public void removeTraceNode(Vertex vert){
		if(nodes.contains(vert))
			nodes.remove(vert);
	}
	
	public void removeTraceEdge(Edge edge){
		if(edges.contains(edge))
			edges.remove(edge);
	}
	
	public Trace copy(){
		Trace result = new Trace();
		result.isTrace = true;
		for(Vertex vert : nodes){
			result.addTraceNode(vert);
		}
		for(Edge edge : edges){
			result.addTraceEdge(edge);
		}
		return result;
	}
	
	public String toString(){
		String result = "Trace<";
		int i = 0;
		for(Vertex v : nodes){			
//			if(v.isTrace) { 
//				Trace t = (Trace)v;
//				result += t.toString() + ",";
//			} else{
				//System.err.println("v:" + v.name + " " + v.isTrace);
				result += v.toString() + ",";
//			}
			if(INCLUDE_EDGE){
				if(i < edges.size())
					result = result.substring(0, result.length() - 1) + "->(" + edges.get(i++) + ")->"; 
			}

		}
		if(nodes.size() > 1 ) result = result.substring(0, result.length() - 1);
		result += ">";
		
		return result;
	}
}

package be.fnord.util.processModel;

import java.util.LinkedList;

import be.fnord.util.functions.OCP.OrderConstrainedPartitionList;
import be.fnord.util.functions.OCP.PartitionList;
import be.fnord.util.functions.OCP.PartitionListElement;
import be.fnord.util.functions.OCP.PartitionListItem;

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
	public static int ten = 10;
	public LinkedList<LinkedList<Vertex>> toVertexArray(){
		// For the main store of vertices
		LinkedList<LinkedList<Vertex>> mainResult = new LinkedList<LinkedList<Vertex>>();
		LinkedList<Vertex> __result = new LinkedList<Vertex>();
		mainResult.add(__result);
		
		// For sub traces
		LinkedList<LinkedList<LinkedList<Vertex>>> subtraces = null;
		
		boolean tripped = false;
		boolean finished = true;
		int i = ten + 0;
		ten += 10; int _ten = ten; 
		for(Vertex v : nodes){	
			if(v.isTrace  && v != nodes.getFirst()) {
				if(!tripped){ tripped = true;
				// Start with a fresh list of lists of lists. 
					subtraces = new LinkedList<LinkedList<LinkedList<Vertex>>>();
				}
				
				// Treat the vertex like a trace (technically it's a trace trapped inside a verticies body. 
				Trace t = (Trace)v;
				
				// Create subtraces
				LinkedList<LinkedList<Vertex>> subtrace = t.toVertexArray();
				
				if(subtrace.toString().trim().compareTo("[[]]") != 0 && subtrace.toString().trim().compareTo("[]") != 0){
					subtraces.add(subtrace);
					finished = false;
				}						
				
				// We do the merge once we stop collecting subtraces. 
			}else if(!v.isTrace){
				if(tripped){
					// Let merge everything together and add it to the main results before adding the new element. 
					LinkedList<LinkedList<Vertex>> merged = mergeListOListOList(subtraces);
					a.e.println("Running on " + subtraces);
					a.e.println("Merge result = " + merged);
					mainResult = mergeListOList(mainResult, merged);
//					a.e.println("Main result = " + mainResult);
					tripped = false; 
					finished = true;
				}
				
				// Process the sequential elements here
				for(LinkedList<Vertex> _result : mainResult){
//					a.e.println("Adding " + v);
					_result.add(v);
				}
			}
			
			if(v == nodes.getLast() && !finished){
				if(tripped){
					// Let merge everything together and add it to the main results before adding the new element. 
					LinkedList<LinkedList<Vertex>> merged = mergeListOListOList(subtraces);
					mainResult =  mergeListOList(mainResult, merged);
					a.e.println("Main result = " + mainResult);
					tripped = false; 
					finished = true;
				}
			}
			
		}
			
		return mainResult;
	}
	
	/**
	 * Function for merging the results of the merged LoLoL into LoL. See below
	 * @param mainResult
	 * @param merged
	 */
	private LinkedList<LinkedList<Vertex>> mergeListOList(LinkedList<LinkedList<Vertex>> mainResult,
			LinkedList<LinkedList<Vertex>> merged) {
		
		LinkedList<LinkedList<Vertex>> newResult = new LinkedList<LinkedList<Vertex>>();
		a.e.println("Looking at " + mainResult + a.e.endl + a.e.tab + " and " + merged);
		// Iterate through main results and for each trace, copy and append elements of merged
		for(LinkedList<Vertex> mResult : mainResult){
			
			for(LinkedList<Vertex> mMerge : merged){
				// Copy in for the main 
				LinkedList<Vertex> cpmResult = new LinkedList<Vertex>();
				for(Vertex v : mResult){
					cpmResult.add(v);
				}
				// add in the merged
//				a.e.println("Considering " + mMerge);
				for(Vertex v : mMerge){
					cpmResult.add(v);
				}
				newResult.add(cpmResult);				
			}
		}
		// Overright
		mainResult = newResult;
		a.e.println("Main result = " + mainResult);
		return newResult;
	}
	
	
	/**
	 * Function for mergingsubtraces 
	 * @param subtraces
	 * @return
	 */
	private LinkedList<LinkedList<Vertex>> mergeListOListOList(
			LinkedList<LinkedList<LinkedList<Vertex>>> subtraces) {
		LinkedList<PartitionList<Vertex>> _partitions = new LinkedList<>();
		
		if(subtraces.size() < 1  || subtraces.get(0).size() < 1 || 
				subtraces.get(0) == null || subtraces.get(0).get(0) == null) {
			LinkedList<LinkedList<Vertex>> result = new LinkedList<LinkedList<Vertex>>();
			result.add(new LinkedList<Vertex>());
			return result;
		}

		Vertex startNode = subtraces.get(0).get(0).getFirst();
		Vertex endNode = subtraces.get(0).get(0).getLast();
		
		
		
		if(__DEBUG) a.e.println("Checking " + subtraces);
		LinkedList<PartitionList<Vertex>> _traces = new LinkedList<PartitionList<Vertex>>();
		for(LinkedList<LinkedList<Vertex>> subtrace : subtraces){			
			for(LinkedList<Vertex> trace : subtrace){
				
				PartitionListElement<Vertex> list = new PartitionListElement<Vertex>();
				for(Vertex v: trace){
					if(v == startNode || v == endNode) continue;
					list.add(v);
				}
				PartitionList<Vertex> __part = OrderConstrainedPartitionList.makePartitions(list) ; 
				_traces.add( __part );
				if(__DEBUG) a.e.println("Get list: " + list);
			}
						
		}
		
		// OCP these togeather
		boolean start = false;
		boolean _start = false;
		PartitionList<Vertex> mergeResult = null ;  
		PartitionList<Vertex> lastList=null;
		if(_traces.size() <= 1 ){
			PartitionListElement<Vertex> newTrace = new PartitionListElement<Vertex>();
			PartitionListItem<Vertex> newTraceItem = new PartitionListItem<Vertex>();
			PartitionList<Vertex> emptyList = new PartitionList<Vertex>();
			newTraceItem.add(newTrace);
			emptyList.add(newTraceItem);
			PartitionList<Vertex> thisList = _traces.get(0);
			
			mergeResult = OrderConstrainedPartitionList.joinSets( thisList, emptyList );
			_partitions.add(mergeResult);
		}else			
		for(PartitionList<Vertex> thisList : _traces){
			if(!_start) { _start = true; lastList = thisList ; }else{
				if(!start) { start = true; 
					mergeResult = OrderConstrainedPartitionList.joinSets(lastList, thisList);
					lastList = thisList ;
				} else{
					mergeResult = OrderConstrainedPartitionList.joinPartitionedSets(lastList, thisList);
					lastList = thisList ;
					
				}
			}
		}
		_partitions.add(mergeResult);
		for(PartitionListItem<Vertex> _newPartition: mergeResult){
			for(PartitionListElement<Vertex> __newPartition: _newPartition){
				if(__DEBUG) a.e.println("Partition: " + __newPartition + "\n");
			}
			
		}
		
		
		LinkedList<LinkedList<Vertex>> results = new LinkedList<LinkedList<Vertex>>();
		for(PartitionList<Vertex> newPartition: _partitions){
			for(PartitionListItem<Vertex> _newPartition: newPartition){
				for(PartitionListElement<Vertex> __newPartition: _newPartition){
					LinkedList<Vertex> myResult = new LinkedList<Vertex>();
//					myResult.add(startNode); 
					for(Vertex myV : __newPartition){
						myResult.add(myV);
					}
//					myResult.add(endNode); 
					if(__DEBUG) a.e.println("Partition: " + _newPartition + "\n");
					results.add((LinkedList<Vertex>)myResult);
				}
				
			}
		}

		return results;
	}
	public String toString(){
		String result = "Trace<";
		int i = 0;
		
		// Changed for now, replace with functin below later TODO~!
		for(Vertex v: nodes){
			a.e.println(v.name + ",");
		}
//		for(Vertex v : this.toVertexArray()){			
//			result += v.name + ",";
//			if(INCLUDE_EDGE){
//				if(i < edges.size())
//					result = result.substring(0, result.length() - 1) + "->(" + edges.get(i++) + ")->"; 
//			}
//
//		}
		if(nodes.size() > 1 ) result = result.substring(0, result.length() - 1);
		result += ">";
		
		return result;
	}
}

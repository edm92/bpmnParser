package be.fnord.util.processModel;

import java.util.LinkedList;
import java.util.TreeMap;
import java.util.UUID;

import org.jbpt.pm.AndGateway;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.bpmn.Task;
import org.jgrapht.graph.DefaultDirectedGraph;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Vertex;

public class Graph<v extends Vertex , e extends Edge> extends DefaultDirectedGraph<Vertex, Edge>{
	public static boolean __DEBUG = a.e.__DEBUG;
	
	ProcessModel jbptProcess = new ProcessModel();
	
	public TreeMap<String, Graph<v,e> > processPools = new TreeMap<String, Graph<v,e>>();
	public TreeMap<String, FlowNode> jbptNodeElements = new TreeMap<String, FlowNode >();
	public TreeMap<String, FlowNode> jbptFlowElements = new TreeMap<String, FlowNode >();
	public LinkedList<String> existingVertices = new LinkedList<String>(); 
	public TreeMap<String, Vertex> vertexRef = new TreeMap<String, Vertex>();
	public TreeMap<String, String> vertexIDRef = new TreeMap<String, String>();
	public LinkedList<String> existingEdges = new LinkedList<String>();
	public TreeMap<String, Edge> edgeRef = new TreeMap<String, Edge>();
	public boolean isEmpty = false;
	public String id = UUID.randomUUID().toString();
	public String name = id.toString();
	
	public Vertex trueStart = null;	// Start Node 
	public Vertex trueEnd = null;	// End Node
	
	public Graph<v,e> copyGraph(Graph<v,e> in){
		Graph<v,e> copy = new Graph<v,e>(Edge.class);
		for(Vertex v: in.vertexSet()){
			copy.addV(v);
		}
		for(Edge e: in.edgeSet()){
			copy.addE(e);
		}
		return copy;
	}
	
	public String toString(){
		String result = a.e.dent() + 
				"g[{" + "NODES:" + a.e.endl;
		a.e.incIndent();
		for(Vertex v : this.vertexSet()){
			result += a.e.dent() + v.toString() + a.e.endl;
		}		
		result += "}{EDGES: " +a.e.endl;
		
		for(Edge e: this.edgeSet()){
			result += a.e.dent() + e.toString() + a.e.endl;
		}
		a.e.decIndent();
		if(processPools.size() > 0){
			result += a.e.dent() + "Pools:(" + a.e.endl;
			a.e.incIndent();
			for(String key: processPools.keySet()){
				result += processPools.get(key).toString();
			}
			a.e.decIndent();
			result += ")[End of Pools]" + a.e.endl;			
		}
		
		return result + "}]" + a.e.endl;
	}
	
	@SuppressWarnings("deprecation")
	public boolean addV(Vertex myV){
		if(!existingVertices.contains(myV.toString())){
			
			
			if(myV.isAND){
				Gateway newGate = new AndGateway(myV.getName());
				jbptProcess.addGateway(newGate);
				jbptNodeElements.put(myV.getName(), newGate);
			}else if(myV.isXOR){
				Gateway newGate = new XorGateway(myV.getName());
				jbptProcess.addGateway(newGate);
				jbptNodeElements.put(myV.getName(), newGate);
			}else if(myV.isOR){
				Gateway newGate = new OrGateway(myV.getName());
				jbptProcess.addGateway(newGate);
				jbptNodeElements.put(myV.getName(), newGate);
			}else{
				Task newTask = new Task(myV.getName());
				jbptProcess.addTask(newTask);
				jbptNodeElements.put(myV.getName(), newTask);
			}
			
			
			existingVertices.add(myV.toString());
			vertexRef.put(myV.toString(), myV);
			vertexIDRef.put(myV.id, myV.toString());
			return this.addVertex((Vertex) myV);
		}
		return false;
	}
	
	public boolean addP(Graph<v,e> pool){
		processPools.put(pool.name, pool);
		
		return true;
	}
	
	public boolean removeV(Vertex myV){
		existingVertices.remove(myV.toString());
		vertexRef.remove(myV.toString());
		
		// Remove associated edges
		LinkedList<Edge> removeList = new LinkedList<Edge>();
		for(Edge e: this.edgesOf(myV))
			removeList.add(e);
		for(Edge e: removeList)
			this.removeEdge(e);
		
		this.removeVertex(myV);
		return true;
	}
	
	
	public boolean addE(Edge myE){
		if(!existingEdges.contains(myE.toString())){
			existingEdges.add(myE.toString());
			edgeRef.put(myE.toString(),myE);
			Vertex src = (Vertex)((be.fnord.util.processModel.Edge)myE).getSource();
			Vertex trg = (Vertex)((be.fnord.util.processModel.Edge)myE).getTarget();
			
			
			if(src == null ){
				if(__DEBUG) a.e.println("src node not found " );
				return false;
			}
			if(trg == null){
				if(__DEBUG) a.e.println("trg node not found " );
				return false;
			}

			FlowNode jbptSrc = jbptNodeElements.get(src.getName());
			FlowNode jbptTrg = jbptNodeElements.get(trg.getName());
			jbptProcess.addControlFlow(jbptSrc, jbptTrg);

			
			if(__DEBUG &&  a.e.__HIGHDETAILS) a.e.println(src.name + " " + this.inDegreeOf(src));
			if(__DEBUG &&  a.e.__HIGHDETAILS) a.e.println(trg.toString() + " ");
			return this.addEdge(src, trg, myE);
		}
		return false;
	}
	
	public boolean removeE(Edge myE){
		existingEdges.remove(myE.toString());
		edgeRef.remove(myE.toString());
		this.removeEdge(myE);
		return true;
	}
		
	public Graph(Class<Edge> arg0) {
		super((Class<? extends Edge>) arg0);		
	}
	
	public Graph(){
		super(Edge.class);
	}
	
	private static final long serialVersionUID = 1L;


}

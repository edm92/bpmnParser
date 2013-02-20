package be.fnord.util.processModel;

import java.util.UUID;

import org.jgrapht.graph.DefaultWeightedEdge;


public class Edge extends DefaultWeightedEdge{
	private static final long serialVersionUID = 1L;
	public String name = "";
	public String type = "";
	public UUID id = UUID.randomUUID();
	public Vertex source = null;
	public Vertex target = null;

	public String getName() {	return name;}
	public void setName(String name) {this.name = name;	}
	public String getType() {return type;}
	public void setType(String type) {this.type = type;	}
	public Vertex getSource() {	return source;}
	public void setSource(Vertex source) {	this.source = source;}
	public Vertex getTarget() {	return target;}
	public void setTarget(Vertex target) {	this.target = target;}

	public Edge(String _name){	this(_name, "");}
	public Edge(String _name, String _type){this.name = _name; this.type = _type;	}
	public Edge(Vertex _source, Vertex _target){this("","", _source, _target);}
	
	public Edge(String _name, String _type, Vertex _source, Vertex _target){
		this.name = _name; this.type = _type; this.source = _source; this.target = _target;	}
	
	public String toString()
	{	return "[E](" + this.source + "->" + this.target +")" + ((this.name.length() > 0) ? "{"+name+"}" : ""); }
}

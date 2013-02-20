package be.fnord.util.processModel;


public class Vertex extends Graph<Vertex, Edge>{
	private static final long serialVersionUID = 1L;
	public String name = "";
	public String type = "";
	public String id = "";
	public boolean isSplit = false;
	public boolean isJoin = false;
	public boolean isSubstructural = false;
	public boolean isAND = false;
	public boolean isXOR = false;
	public boolean isOR = false;
	public boolean isSubprocess = false;
	
	public Vertex corresponding = null;
	
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getType() {return type;}
	public void setType(String type) {this.type = type;	}
	public Vertex(String _name){this(_name, "task");}
	public Vertex(String _name, String _type){this.name=_name; this.type = _type;}
	public Vertex getCorresponding() {	return corresponding;	}
	public void setCorresponding(Vertex corresponding) {if(corresponding.corresponding != null || corresponding.isXOR) return ; 
														this.corresponding = corresponding; this.corresponding.corresponding = this;}
	public String toString(){return this.name+"("+type+")";	}
	public boolean isSubprocess() {	return isSubprocess;}
	public void setSubprocess(boolean isSubprocess) {	this.isSubprocess = isSubprocess;	}
}


package be.fnord.util.processModel;

import java.util.LinkedList;

public class Trace<v extends Vertex, e extends Edge> {
	LinkedList<v> nodes = new LinkedList<v>();
	LinkedList<e> edges = new LinkedList<e>();
}

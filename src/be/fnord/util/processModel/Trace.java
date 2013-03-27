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

public class Trace<v extends Vertex, e extends Edge> {
	LinkedList<v> nodes = new LinkedList<v>();
	LinkedList<e> edges = new LinkedList<e>();
}

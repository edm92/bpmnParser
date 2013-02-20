package be.fnord.jbpt.extend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.throwable.SerializationException;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Vertex;


/**
 * This class loads an inputted JSON string using JSON2Process in jbpt and then converts the processModel into a graph structure.
 * @author edm92
 *
 */
public class altJSON2Process {

	public static Graph<Vertex,Edge> convert(String name, String json){
		Graph<Vertex,Edge> result = new Graph<Vertex,Edge>(Edge.class);
		result.name = name;
		ProcessModel jbptProcess = null;
		try {
			jbptProcess = JSON2Process.convert(json);
		} catch (SerializationException e) {
			e.printStackTrace();
		}
		if(jbptProcess == null) return null;
		
		// Lets copy the process to a graph
		for(ControlFlow<?> e: jbptProcess.getEdges()){
			FlowNode src = (FlowNode) e.getSource();
			
			System.out.println("Looking at " + src.getName() + " = " + src.toString());
		}
		
		
		return result;
	}
	
	@SuppressWarnings({ "resource" })
	public static String readFile( String file ){
	    BufferedReader reader = null;
		try {
			reader = new BufferedReader( new FileReader (file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if(reader == null) return "";
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    try {
			while( ( line = reader.readLine() ) != null ) {
			    stringBuilder.append( line );
			    stringBuilder.append( ls );
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	    return stringBuilder.toString();
	}
	
	
}

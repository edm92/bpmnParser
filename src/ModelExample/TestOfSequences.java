package ModelExample;

import java.util.LinkedList;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.util.GraphChecker;
import be.fnord.util.processModel.util.GraphLoader;
import be.fnord.util.processModel.util.GraphTransformer;

/**
 * 
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 * Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 *
 */
public class TestOfSequences {
	
	public static void main(String[] args) {
		/////////////////////////////////////////
		/* Initialize core app. */ new a.e();  // 
		/////////////////////////////////////////
		//// Real start of program below	/////
		/////////////////////////////////////////
		
		Graph<Vertex,Edge> g1 = GraphLoader.loadModel("models/Model1.bpmn20.xml", a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
//		System.out.println("G1-" + g1);
		GraphChecker gc = new GraphChecker();
		if(!gc.CheckEventsAndGateways(g1)) a.e.println("Issue checking events and gateways"); 
		
		LinkedList<Graph<Vertex, Edge>> decisionless = GraphTransformer.makeDecisionFree(g1);
		for(Graph<Vertex,Edge> g : decisionless){
			System.out.println("G!!!!" + g);
		}
		return ;
		
	}
	


}

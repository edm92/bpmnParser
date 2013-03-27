package General;

import static org.junit.Assert.*;

import org.junit.Test;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.util.GraphChecker;
import be.fnord.util.processModel.util.GraphLoader;

public class ModelLoading {

	@Test
	public void testModelLoading() {		
	
		/////////////////////////////////////////
		/* Initialize core app. */ new a.e();  // 
		/////////////////////////////////////////
		//// Real start of program below	/////
		/////////////////////////////////////////
		
		Graph<Vertex,Edge> g1 = GraphLoader.loadModel("tests/models/proc1.bpmn20.xml");
		assertNotNull("Model models/proc1.bpmn20.xml not loaded successfully", g1);
		System.out.println("G1-" + g1);
		
		Graph<Vertex,Edge> g2 = GraphLoader.loadModel("tests/models/proc2.bpmn20.xml");
		assertNotNull("Model models/proc2.bpmn20.xml not loaded successfully", g2);
		System.out.println("G2-" + g2);
		
		// Outputs True if Strongly connected
		// and other syntactic checks are cleared
		GraphChecker g1Checker = new GraphChecker();
		GraphChecker g2Checker = new GraphChecker();
		boolean g1result = g1Checker.CheckGraph(g1);
		boolean g2result = g2Checker.CheckGraph(g2);
		assertTrue("g1 model checking completed unsuccessfully",g1result);
		assertFalse("g2 model checking completed unsuccessfully",g2result);
		System.out.println("G1 Test: " + g1result);
		System.out.println("G2 Test: " + g2result);
		
		
		//fail("Not yet implemented");
	}

}

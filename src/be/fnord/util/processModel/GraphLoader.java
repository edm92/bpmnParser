package be.fnord.util.processModel;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;


import org.w3c.dom.Document;
import org.yaoqiang.bpmn.model.BPMNModelCodec;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.BPMNModelParsingErrors.ErrorMessage;
import org.yaoqiang.bpmn.model.elements.core.common.SequenceFlow;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;

import be.fnord.yaoqiang.extend.eDefinitions;

public class GraphLoader {
	
	
	public static Graph<Vertex,Edge> loadModel(String filename){
		Graph<Vertex,Edge> myModel = new Graph<Vertex,Edge>(Edge.class);
		Document document = null;
		document = BPMNModelUtils.parseDocument(filename, true, new LinkedList<ErrorMessage>());
		
		if(document != null) {
			BPMNModelCodec myCodec = new BPMNModelCodec();
			eDefinitions myDef = new eDefinitions(); 
			myCodec.decode(document.getDocumentElement(), myDef);
			
			
			BPMNProcess bpmn = BPMNModelUtils.getDefaultProcess(myDef);
			myModel.isEmpty = bpmn.isEmptyProcess() ;
			if(myModel.isEmpty) return null;
			
			List<SequenceFlow> mySeq = bpmn.getSequenceFlows();
			TreeMap<String, Vertex> allNodes = new TreeMap<String, Vertex>();
			
			for(SequenceFlow seq : mySeq) {
				String activity1 = seq.getSourceFlowNode().getId();
				String activity2 = seq.getTargetFlowNode().getId();
				Vertex myActivity = convertNode(seq.getSourceFlowNode());
				Vertex myActivity2 = convertNode(seq.getTargetFlowNode());
				
				if(!allNodes.containsKey(activity1)){
					allNodes.put(activity1, myActivity);
				}
				if(!allNodes.containsKey(activity2)){
					allNodes.put(activity2, myActivity2);
				}
				myActivity = allNodes.get(activity1);
				myActivity2 = allNodes.get(activity2);
				String sequenceFlow = seq.getName();
				Edge sequence = new Edge(sequenceFlow, 
						seq.getClass().getCanonicalName().substring(44), 
						myActivity, 
						myActivity2);
				
				myModel.addV(myActivity);
				myModel.addV(myActivity2);
				myModel.addE(sequence);
				
				
				// Add to repository
				myModel.name = filename;
				PGraph.allGraphs.put(filename, myModel);
			}
		}else {
			System.err.println("Can't load file.");
		}
		return myModel;
	}
	
	
	/**
	 * Convert a yaoqiang bpmn node to a simple Vertex
	 * Try to use actual mapping but if can't figure it out try to use basic element names
	 * @param inputNode
	 * @return
	 */
	public static Vertex convertNode(org.yaoqiang.bpmn.model.elements.core.common.FlowNode inputNode){
		Vertex outputNode = null;
		String type = inputNode.getClass().getCanonicalName();
		if(outputNode == null){
			if(type.toLowerCase().contains("gateway")){
				//a.e.println(type + " " + inputNode.getName());
				outputNode = new Vertex("" + inputNode.getName(), "gateway");
				if(type.toLowerCase().contains("exclusivegateway")){outputNode.isXOR = true;}
				if(type.toLowerCase().contains("parallelgateway")){outputNode.isAND = true;}
				if(type.toLowerCase().contains("inclusivegateway")){outputNode.isOR = true;}
				if(inputNode.getOutgoingSequenceFlows().size() > 1)
					outputNode.isSplit = true;
				else
					outputNode.isJoin = true;
				
			}else if(type.toLowerCase().contains("event")){
					outputNode = new Vertex("" + inputNode.getName(), "event");
			}else{
				outputNode = new Vertex("" + inputNode.getName(), "task");
			}
		}	
		outputNode.id = UUID.randomUUID().toString();
		return outputNode;
	}

}

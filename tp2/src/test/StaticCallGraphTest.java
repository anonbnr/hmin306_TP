package test;

import java.io.IOException;

import org.junit.Test;

import callgraph.StaticCallGraph;

/**
 * A test class to test the creation of the call graph (system test)
 * @author anonbnr
 * @author Amandine Paillard
 */
public class StaticCallGraphTest {
	
	private static StaticCallGraph graph;
	
	@Test
	public void testCreateCallGraph() {
		try {
			
			graph = StaticCallGraph.createCallGraph("/home/anonbnr/eclipse-workspace/design_patterns/src");
			System.out.println(graph);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

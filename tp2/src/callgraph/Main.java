package callgraph;

import java.io.IOException;

/**
 * Entry point for using the Static Call Graph (main method)
 * @author anonbnr
 * @author Amandine Paillard
 */
public class Main {

	public static void main(String[] args) throws IOException {
		String pathToSource = args[0];
		System.out.println("Here's the call graph of the " + pathToSource + " project.");
		
		StaticCallGraph graph = StaticCallGraph.createCallGraph(pathToSource);
		System.out.println(graph);
	}
}

package callgraph;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		String pathToSource = args[0];
		System.out.println("Here is the dependency graph of "+ pathToSource + "'s project.");
		CallGraph graph = CallGraph.constructGraph(pathToSource);
		System.out.println(graph);
	}
}

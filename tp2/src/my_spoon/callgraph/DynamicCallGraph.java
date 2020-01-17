package my_spoon.callgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A dynamic call graph with static vertices and arrows.<br>
 * The vertices of the call graph designate methods that 
 * are sources or destinations of invocations. 
 * The arrows of the call graph designate a map between
 * a source method and its destination. 
 * Every destination is also mapped to the number 
 * of invocations source -> destination.<br>
 * In short, the arrows are represented as  
 * source -> map -> destination -> map -> inv, such that:
 * <dl>
 * <dt>destination</dt>
 * <dd>The fully-qualified name of the destination method</dd>
 * <dt>inv</dt>
 * <dd>The number of invocations between
 * the source and destination methods</dd>
 * </dl>
 * 
 * @author anonbnr
 * @author Amandine Paillard
 */
public class DynamicCallGraph {
	
	/* ATTRIBUTES */
	private static Map<String, Map<String, Integer>> invocations = new HashMap<>();
	
	/* METHODS */
	// business logic
	
	/**
	 * Adds an invocation source -> destination to the call graph.<br>
	 * If the invocation already exists, its number of invocations
	 * is increased. Otherwise an invocation is created and its number
	 * set to 1.
	 * @param source the source of the invocation.
	 * @param destination the destination of the invocation.
	 * @return the previous number of the invocation if it already exists,
	 * or null if it's a new invocation.
	 */
	public static void addInvocation(String source, String destination) {
		
		if (invocations.containsKey(source)) {
			
			if (invocations.get(source).containsKey(destination)) {
				int numberOfArrows = invocations.get(source).get(destination);
				invocations.get(source).put(destination, numberOfArrows + 1);
			}
			
			else {
				invocations.get(source).put(destination, 1);
			}
		}
		
		else {
			invocations.put(source, new HashMap<String, Integer>());
			invocations.get(source).put(destination, 1);
		}
	}
	
	/**
	 * Adds an invocation source -> destination -> inv to the call graph.<br>
	 * If the invocation already exists, its number of occurrences
	 * is overwritten.
	 * @param source the source of the invocation.
	 * @param destination the destination of the invocation.
	 * @param occurrences the number of invocations source -> destination
	 */
	public static void addInvocation(String source, String destination, int occurrences) {
		if (!invocations.containsKey(source))
			invocations.put(source, new HashMap<String, Integer>());
		
		invocations.get(source).put(destination, occurrences);
	}
	
	/**
	 * Adds the source -> destination -> inv map 
	 * to the invocations of the call graph.
	 * @param map the map of the source -> destination -> inv invocations
	 */
	public static void addInvocations(Map<String, Map<String, Integer>> map) {
		for (String source: map.keySet())
			for (String destination: map.get(source).keySet())
				DynamicCallGraph.addInvocation(source, destination, map.get(source).get(destination));
	}
	
	/**
	 * Checks if the provided strings designate the fully qualified names
	 * of source and destination candidate methods of at least one invocation
	 * in the call graph.
	 * @param source the source method candidate.
	 * @param destination the destination method candidate.
	 * @return true if a source -> destination invocation exists in the call graph.
	 */
	public static boolean containsInvocations(String source, String destination) {
		return  invocations.containsKey(source) && 
				invocations.get(source).containsKey(destination);
	}
	
	public static void display() {
		StringBuffer buf = new StringBuffer();
		
		for (String source: invocations.keySet()) {
			buf.append(source + ":\n");
			
			for (String destination: invocations.get(source).keySet())
				buf.append("  ---> " + destination + 
						" (" + invocations.get(source).get(destination) + " fois)\n");
		}
		
		System.out.println(buf.toString());
	}
}

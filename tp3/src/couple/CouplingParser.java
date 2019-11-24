package couple;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import visitors.MethodInvocationVisitor;
import visitors.TypeDeclarationVisitor;

public class CouplingParser {
	/*attributes*/
	private String projectPath;
	//*** Bachar's jrePath ***//
	// public static final String jrePath = "/usr/lib/jvm/java-11-oracle";
	//*** Amandine's jrePath ***//
	public static final String jrePath = "/usr/lib/jvm/java-8-openjdk-amd64/";
	
	/*constructors*/
	public CouplingParser(String projectPath) {
		this.projectPath = projectPath;
	}
	
	/*methods*/
	/**
	 * The project path getter
	 * @return the project path
	 */
	public String getProjectPath() {
		return this.projectPath;
	}
	
	/**
	 * Recursively returns the list of java files for a given folder
	 * @param folder the folder whose files to list
	 * @return the list of java files for the folder
	 */
	public static ArrayList<File> listJavaFilesForProject(File folder) {
		ArrayList<File> javaFiles = new ArrayList<>();
		String fileName = "";
		
		for (File fileEntry: folder.listFiles()) {
			fileName = fileEntry.getName();
			
			if (fileEntry.isDirectory())
				javaFiles.addAll(listJavaFilesForProject(fileEntry));
			else if (fileName.endsWith(".java"))
				javaFiles.add(fileEntry);
		}

		return javaFiles;
	}
	
	/**
	 * The compilation unit for the root node of the source's AST
	 * @param source array of characters designating the source code to parse
	 * @return the compilation unit for the root node of source's AST
	 */
	public CompilationUnit parse(char[] source) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
 
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
 
		parser.setUnitName("");
 
		String[] sources = { projectPath }; 
		String[] classpath = {jrePath};
 
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
		parser.setSource(source);
		
		return (CompilationUnit) parser.createAST(null); // create and parse
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////// Coupling related functions  //////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the number of call between class A and B.
	 */
	public int getCouplingBetween(String callingClassName, String calledClassName) throws IOException { // Exo3
		ArrayList<File> javaFiles = CouplingParser.listJavaFilesForProject(new File(projectPath));
		
		int cpt =0;
		if(callingClassName.equals(calledClassName)) { // check A != B
			System.out.println("Can not compute coupling in same class");
			return 0; 
		}
		for (File fileEntry : javaFiles) { 
			String content = FileUtils.readFileToString(fileEntry);
			CompilationUnit parse = parse(content.toCharArray());
			TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
			parse.accept(visitor);
			
			for (TypeDeclaration td : visitor.getTypes()) { // for each class
				if(callingClassName.equals(td.getName().toString())) {
					for(MethodDeclaration method : td.getMethods()){ // in each methods from td class
						MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
						method.accept(visitor2);
						for (MethodInvocation methodInvocation : visitor2.getMethods()) { // look for a call in that method
							if(methodInvocation.resolveMethodBinding()!= null) {
								if (calledClassName.equals(methodInvocation.resolveMethodBinding().getDeclaringClass().getName().toString()) ) {
									cpt++;
								}
							} 
						}
					}
				}
			}
		}
		return cpt;
	}
	
	/**
	 * Returns the total number of relation (call from a class to another).
	 * Does not check if A <--> A.
	 */
	public int countAllCouplesInProject() throws IOException {
		ArrayList<File> javaFiles = CouplingParser.listJavaFilesForProject(new File(projectPath));
		
		int numberOfCallingMethods = 0;
		for (File fileEntry : javaFiles) { // for each class A
			String content = FileUtils.readFileToString(fileEntry);
			CompilationUnit parse = parse(content.toCharArray());
			TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
			parse.accept(visitor);
			
			for (TypeDeclaration td : visitor.getTypes()) { // in each method M from A
				for(MethodDeclaration method : td.getMethods()){
					MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
					method.accept(visitor2);
					for (MethodInvocation methodInvocation : visitor2.getMethods()) { // in each method invocation MI in M.
						if(methodInvocation.resolveMethodBinding()!= null) { // if it is attached to a class, if there is a call.
							numberOfCallingMethods++;
						} 
					}
				}
			}
		}
		return numberOfCallingMethods;
	}

	/**
	 * Draws a sort of "weighted-coupling graphs". Used in exo 3 TP3.
	 */
	public ArrayList<Couple> makeCoupledWeightedGraph() throws IOException {
		ArrayList<File> javaFiles = CouplingParser.listJavaFilesForProject(new File(projectPath));
		
		int numberOfMethods = 0;
		ArrayList<Couple> couples = new ArrayList<Couple>();
		String source, target; 
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			CompilationUnit parse = parse(content.toCharArray());
			TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
			parse.accept(visitor);
			
			for (TypeDeclaration td : visitor.getTypes()) { // for each class A
				source =  td.getName().toString();
				for(MethodDeclaration method : td.getMethods()){ // for each methods M in A
					MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
					method.accept(visitor2);
					for (MethodInvocation methodInvocation : visitor2.getMethods()) { // for each method's invocation Mi from M
						if(methodInvocation.resolveMethodBinding()!= null) {
							numberOfMethods++;
							target = methodInvocation.resolveMethodBinding().getDeclaringClass().getName().toString();
							if (!source.equals(target) ) { /* A != B */
								if(Couple.isCoupleAlreadyInArray(couples, source, target)) { // we already know the couple
									Couple.incrementCoupleCounter(couples, source, target);
								} else { // we add this new couple in the array
									Couple c = new Couple(td.getName().toString(), target, 1, 0);
									couples.add(c);
								}
							}
						} 
					}
				}
			}
		}
		for(Couple c : couples) {
			c.setTotalNumberOfRelations(numberOfMethods);
			c.computeScore();
		}
		return couples;
	}
	
	/**
	 * Used in the creation of the hierarchical cluster.
	 * 
	 * @return an ArrayList of Clusters where a cluster contains a class.
	 * @throws IOException
	 */
	public ArrayList<Cluster> initializeClusters() throws IOException{
		ArrayList<File> javaFiles = CouplingParser.listJavaFilesForProject(new File(projectPath));
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		
		// read all java files to fetch classes.
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			CompilationUnit parse = parse(content.toCharArray());
			TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
			parse.accept(visitor);
			
			for (TypeDeclaration td : visitor.getTypes()) {
				boolean isAlreadyInCluster = false; // check that td is not already in clusters
				for(Cluster c : clusters) {
					if(c.getClasses().contains(td.getName().toString())) {
						isAlreadyInCluster = true;
					}
				}
				if(!isAlreadyInCluster) {
					Cluster cls = new Cluster(td.getName().toString(), 0);
					clusters.add(cls);
				}
			}	
		}
		return clusters;
	}
	
	/**
	 * Returns the call metric between two clusters.
	 * 
	 * @param source : source Cluster
	 * @param target : target Cluster
	 * @return the number of calls among all the classes from two clusters
	 * @throws IOException
	 */
	public int getScoreBetweenClusters(ArrayList<Couple> couples, Cluster source, Cluster target) throws IOException {
		int score = 0;
		for(String sourceCls : source.getClasses()) {
			for(String targetCls : target.getClasses()) {
				if(!sourceCls.equals(targetCls)) {
					for(Couple c : couples) {
						if(c.getSource().equals(sourceCls) && c.getTarget().equals(targetCls)) {
							score += c.getCpt();
						}
					}
				}
			}
		}
		return score;
	}
	
	/**
	 * Create the hierarchical cluster of a Java program based on coupling metric.
	 * Can also be used as preprocessing to count all calls.
	 * Used for question 2a from TP3.
	 * 
	 * @return a Stack<Cluster> representing the hierarchical cluster.
	 * @throws IOException
	 */
	public Stack<Cluster> makeHierarchicalCluster() throws IOException {
		// variables
		ArrayList<Cluster> clusters = initializeClusters();
		Stack<Cluster> hierarchicalCluster = new Stack<Cluster>();
		ArrayList<Couple> couples = makeCoupledWeightedGraph();
		Cluster sourceCluster, targetCluster, firstPart, secondPart, newCluster;
		int bestScore, firstIndex, secondIndex;
		
		// ouputs
		System.out.println("Original classes are put in clusters: ");
		clusters.forEach(c -> System.out.println(c));
		System.out.println("\nCreation of hierarchical clusters:");
		
		// while we don't have one final cluster
		while(clusters.size()>1) { 
			bestScore = 0;
			firstIndex = 0;
			secondIndex = 0;
			
			// look for the best clusters to fusionned
			for(int i = 0 ; i < clusters.size() ; i++) {
				sourceCluster = clusters.get(i);
				for(int j = 0 ; j < clusters.size() ; j++) {
					targetCluster = clusters.get(j);
					if(i != j) {
						int coupleScore = getScoreBetweenClusters(couples, sourceCluster, targetCluster);
						if(bestScore < coupleScore){
							bestScore = coupleScore;
							firstIndex = i;
							secondIndex = j;
						}
					}					
				}
			}
			
			// break condition if there is no more call
			if(bestScore == 0)
				break;
			
			// fusion best clusters
			firstPart = clusters.get(firstIndex);
			secondPart = clusters.get(secondIndex);
			newCluster = new Cluster(firstPart.getClasses(),
									bestScore + firstPart.getCouplingScore() + secondPart.getCouplingScore());
			newCluster.addClasses(secondPart.getClasses());
			clusters.remove(firstPart);				// remove the
			clusters.remove(secondPart);			// composed clusters
			clusters.add(newCluster);
			hierarchicalCluster.push(newCluster);	// and push fusion
			
			// ouputs
			System.out.println("\nFusion of: "+firstPart + " and " + secondPart
								+ ", they have " + bestScore + " call(s).");
			System.out.println("Clusters: ");
			clusters.forEach(cluster -> System.out.println(cluster.toString()));
		}
		// outputs
		System.out.println("Process done.\n Final clusters:");
		clusters.forEach(cluster -> System.out.println(cluster.toString()));
		
		return hierarchicalCluster;
	}
	
	/**
	 * Silent version of makeHierarchicalCluster(), used for question 2b from TP3.
	 * Also store extra details in the stack to facilitate our work.
	 */
	public Stack<Cluster> silentHierarchicalClusterMaker() throws IOException {
		// variables
		ArrayList<Cluster> clusters = initializeClusters();
		Stack<Cluster> hierarchicalCluster = new Stack<Cluster>();
		ArrayList<Couple> couples = makeCoupledWeightedGraph();
		Cluster sourceCluster, targetCluster, firstPart, secondPart, newCluster;
		int bestScore, firstIndex, secondIndex;
		
		// while we don't have one final cluster
		while(clusters.size()>1) { 
			bestScore = 0;
			firstIndex = 0;
			secondIndex = 0;
			
			// look for the best clusters to fusionned
			for(int i = 0 ; i < clusters.size() ; i++) {
				sourceCluster = clusters.get(i);
				for(int j = 0 ; j < clusters.size() ; j++) {
					targetCluster = clusters.get(j);
					if(i != j) {
						int coupleScore = getScoreBetweenClusters(couples, sourceCluster, targetCluster);
						if(bestScore < coupleScore){
							bestScore = coupleScore;
							firstIndex = i;
							secondIndex = j;
						}
					}					
				}
			}
			
			// break condition if there is no more call
			if(bestScore == 0)
				break;
			
			// fusion best clusters
			firstPart = clusters.get(firstIndex);
			secondPart = clusters.get(secondIndex);
			newCluster = new Cluster(firstPart.getClasses(),
									bestScore + firstPart.getCouplingScore() + secondPart.getCouplingScore());
			newCluster.addClasses(secondPart.getClasses());
			clusters.remove(firstPart);				// remove the
			clusters.remove(secondPart);			// composed clusters
			clusters.add(newCluster);
			hierarchicalCluster.push(firstPart);	// and push fusion
			hierarchicalCluster.push(secondPart);	// and push fusion
			hierarchicalCluster.push(newCluster);	// and push fusion
		}		
		return hierarchicalCluster;
	}
	
	/**
	 * Used in question 2b from TP3
	 * Computes the partition of a program based on its hierarchical cluster.
	 */
	public ArrayList<Cluster> makePartition() throws IOException {
		System.out.println("\nPartition construction");
		Stack<Cluster> stackOfCluster = silentHierarchicalClusterMaker();
		ArrayList<Cluster> partition = new ArrayList<Cluster>();
		while(!stackOfCluster.isEmpty()) {
			
			Cluster father = stackOfCluster.pop();
			Cluster secondSon = stackOfCluster.pop();
			Cluster firstSon = stackOfCluster.pop();
			System.out.println("\nFather weight: " + father.getCouplingScore());
			System.out.println("Average of sons weight: " + Math.ceil((firstSon.getCouplingScore()+secondSon.getCouplingScore())/2));
			if(father.getCouplingScore() > Math.ceil((firstSon.getCouplingScore()+secondSon.getCouplingScore())/2)){
				partition.add(father);
				System.out.println("We add father to partition.");
			} else {
				System.out.println("Weight inferior or equal, his sons are not part of the partition.");
			}
			System.out.println("Partition(s):");
			partition.forEach(p -> System.out.println(p));
		}
		return partition;
	}
}

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
	
	
	
	/*
	 * Returns the number of call between class A and B.
	 */
	public int getCouplingBetween(String callingClassName, String calledClassName) throws IOException { // Exo3
		ArrayList<File> javaFiles = CouplingParser.listJavaFilesForProject(new File(projectPath));
		
		int cpt =0;
		if(callingClassName.equals(calledClassName)) { // check A != B
			System.out.println("0");
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
	
	/*
	 * Returns the total number of relation (call from a class to another).
	 * Does not check if A <--> A.
	 */
	public int countAllRelationsInProject() throws IOException {
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

	/*
	 * Draws a sort of "weighted-coupling graphs". Used in exo 3 TP3.
	 */
	public ArrayList<Couple> getAllCouplingMetrics() throws IOException {
		ArrayList<File> javaFiles = CouplingParser.listJavaFilesForProject(new File(projectPath));
		
		int numberOfMethods = 0;
		ArrayList<Couple> couples = new ArrayList<Couple>();
		String callingClass, calledClass; 
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			CompilationUnit parse = parse(content.toCharArray());
			TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
			parse.accept(visitor);
			
			for (TypeDeclaration td : visitor.getTypes()) { // for each class A
				callingClass =  td.getName().toString();
				for(MethodDeclaration method : td.getMethods()){ // for each methods M in A
					MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
					method.accept(visitor2);
					for (MethodInvocation methodInvocation : visitor2.getMethods()) { // for each method's invocation Mi from M
						if(methodInvocation.resolveMethodBinding()!= null) {
							numberOfMethods++;
							calledClass = methodInvocation.resolveMethodBinding().getDeclaringClass().getName().toString();
							if (!callingClass.equals(calledClass) ) { /* A != B */
								if(Couple.isCoupleAlreadyInArray(couples, callingClass, calledClass)) { // we already know the couple
									Couple.incrementCoupleCounter(couples, callingClass, calledClass);
								} else { // we add this new couple in the array
									Couple c = new Couple(td.getName().toString(), calledClass, 1, 0);
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
	
	/*
	 * Used in question 2a from TP3.
	 * Make a Hierarchical Cluster from code (analyzed in getAllCouplingMetrics()).
	 * Store all the steps in a stack.
	 * TODO Optimize it with only one loop for and save variables in if.
	 */
	public Stack<Cluster> makeHierarchicalCluster() throws IOException {		
		ArrayList<Couple> couplesWithMetric = getAllCouplingMetrics();
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		Stack<Cluster> stack = new Stack<Cluster>();
		// initialize the clusters
		for(Couple couple : couplesWithMetric) {
			ArrayList<String> classNames = new ArrayList<String>();
			classNames.add(couple.getClassCalling());
			classNames.add(couple.getClassCalled());
			Cluster c = new Cluster(classNames, couple.getCpt());
			clusters.add(c);
		}
		System.out.println("Original clusters: ");
		clusters.forEach(cluster -> System.out.println(cluster.toString()));
		// make clusters until there is no class left
		int firstClusterIndex = 0;
		int secondClusterIndex = 0;
		while(clusters.size()>1){
			int bestUnionScore = 0;
			// look for best union to do
			for(int i = 0 ; i < clusters.size() ; i++) {
				Cluster currentCluster = clusters.get(i);
				for(int j = 0 ; j < clusters.size() ; j++) {
					Cluster comparativeCluster = clusters.get(j);
					if(currentCluster!=comparativeCluster && currentCluster.isCoupledWith(comparativeCluster)) {
						if((currentCluster.getCouplingValue() + comparativeCluster.getCouplingValue()) > bestUnionScore) {
							bestUnionScore = currentCluster.getCouplingValue() + comparativeCluster.getCouplingValue();
							firstClusterIndex= i;
							secondClusterIndex= j;
						}
					}
				}
			}
			Cluster firstPartCluster = clusters.get(firstClusterIndex);
			Cluster secondPartCluster = clusters.get(secondClusterIndex);
			Cluster newCluster = new Cluster(firstPartCluster.getClasses(), bestUnionScore);
			newCluster.addClasses(secondPartCluster.getClasses());
			clusters.add(newCluster);
			clusters.remove(firstClusterIndex);
			clusters.remove(secondPartCluster);
			stack.push(newCluster);
			System.out.println("\nFusion of: "+firstPartCluster + " and " + secondPartCluster);
			System.out.println("Clusters: ");
			clusters.forEach(cluster -> System.out.println(cluster.toString()));
		}
		return stack;
	}
	
	/*
	 * Silent version of makeHierarchicalCluster(), used for question 2b from TP3.
	 * Also store extra details in the stack to facilitate our work.
	 */
	public Stack<Cluster> silentHierarchicalClusterMaker() throws IOException {
		ArrayList<Couple> couplesWithMetric = getAllCouplingMetrics();
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		Stack<Cluster> stack = new Stack<Cluster>();
		// initialize the clusters
		for(Couple couple : couplesWithMetric) {
			ArrayList<String> classNames = new ArrayList<String>();
			classNames.add(couple.getClassCalling());
			classNames.add(couple.getClassCalled());
			Cluster c = new Cluster(classNames, couple.getCpt());
			clusters.add(c);
		}
		// make clusters until there is no class left
		int firstClusterIndex = 0;
		int secondClusterIndex = 0;
		while(clusters.size()>1){
			int bestUnionScore = 0;
			// look for best union to do
			for(int i = 0 ; i < clusters.size() ; i++) {
				Cluster currentCluster = clusters.get(i);
				for(int j = 0 ; j < clusters.size() ; j++) {
					Cluster comparativeCluster = clusters.get(j);
					if(currentCluster!=comparativeCluster && currentCluster.isCoupledWith(comparativeCluster)) {
						if((currentCluster.getCouplingValue() + comparativeCluster.getCouplingValue()) > bestUnionScore) {
							bestUnionScore = currentCluster.getCouplingValue() + comparativeCluster.getCouplingValue();
							firstClusterIndex= i;
							secondClusterIndex= j;
						}
					}
				}
			}
			Cluster firstPartCluster = clusters.get(firstClusterIndex);
			Cluster secondPartCluster = clusters.get(secondClusterIndex);
			Cluster newCluster = new Cluster(firstPartCluster.getClasses(), bestUnionScore);
			newCluster.addClasses(secondPartCluster.getClasses());
			clusters.add(newCluster);
			clusters.remove(firstClusterIndex);
			clusters.remove(secondPartCluster);
			stack.push(firstPartCluster);
			stack.push(secondPartCluster);
			stack.push(newCluster);
		}
		return stack;
	}
	
	/*
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
			System.out.println("\nFather weight: " + father.getCouplingValue());
			System.out.println("Average of sons weight: " + Math.ceil((firstSon.getCouplingValue()+secondSon.getCouplingValue())/2));
			if(father.getCouplingValue() > Math.ceil((firstSon.getCouplingValue()+secondSon.getCouplingValue())/2)){
				partition.add(father);
				System.out.println("We add father to partition.");
			}
			System.out.println("Partition(s):");
			partition.forEach(p -> System.out.println(p));
		}
		return partition;
	}
}

package callgraph;

import java.io.IOException;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import processors.BaseProcessor;
import visitors.ClassDeclarationVisitor;
import visitors.MethodDeclarationVisitor;
import visitors.MethodInvocationVisitor;
import visitors.PackageDeclarationVisitor;

/**
 * A static call graph using a BaseProcessor to parse
 * a project.
 * @author anonbnr
 * @author Amandine Paillard
 * @see BaseProcessor
 */
public class StaticCallGraph extends AbstractCallGraph {
	
	/* ATTRIBUTES */
	private BaseProcessor processor;

	/* CONSTRUCTOR */
	public StaticCallGraph(String projectPath) {
		this.processor = new BaseProcessor(projectPath);
	}
	
	/* METHODS */
	/**
	 * creates a static call graph for compilation unit in the project identified
	 * by its path.
	 * @param projectPath the project path.
	 * @param cUnit the compilation unit of the project.
	 * @return the static call graph of the compilation unit.
	 */
	public static StaticCallGraph createCallGraph(String projectPath, CompilationUnit cUnit) {
		StaticCallGraph graph = new StaticCallGraph(projectPath);
		ClassDeclarationVisitor classVisitor = new ClassDeclarationVisitor();
		
		for(TypeDeclaration cls: classVisitor.getClasses(cUnit)){
			MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
			
			for(MethodDeclaration method: methodVisitor.getMethods(cls))
				graph.addMethodAndInvocations(cls, method);
		}
		
		return graph;
	}
	
	/**
	 * creates a static call graph for the whole project identified by its path.
	 * @param projectPath the project path.
	 * @return the static call graph of the whole project.
	 * @throws IOException if the content of a source file in the project cannot be read.
	 */
	public static StaticCallGraph createCallGraph(String projectPath) 
			throws IOException {
		StaticCallGraph graph = new StaticCallGraph(projectPath);
		
		for(CompilationUnit cUnit: graph.processor.getParser().parseProject()) {
			StaticCallGraph partial = StaticCallGraph.createCallGraph(projectPath, cUnit);
			graph.addMethods(partial.getMethods());
			graph.addInvocations(partial.getInvocations());
		}
		
		return graph;
	}
	
	/**
	 * Adds the method declared in the class and the methods invoked therein 
	 * to the methods and invocations of the static call graph respectively.
	 * @param cls the class in which the method is declared
	 * @param method the method to add along with the methods invoked within to the static call graph
	 * @return true if the method and its invocations were added to the static call graph
	 */
	private boolean addMethodAndInvocations(TypeDeclaration cls, MethodDeclaration method) {
		if(method.getBody() != null) {
			String methodName = PackageDeclarationVisitor.getFullName(cls) + "::" + method.getName().toString();
			this.addMethod(methodName);
			
			MethodInvocationVisitor invocationVisitor = new MethodInvocationVisitor();
			this.addInvocations(cls, method, methodName, invocationVisitor);
			this.addSuperInvocations(methodName, invocationVisitor);
		}
		
		return method.getBody() != null;
	}
	
	/**
	 * Adds the invoked methods within a method to the static call graph.
	 * @param cls the class declaring the method whose methods invoked therein 
	 * will be added to the methods and invocations of the static call graph.
	 * @param method the method whose methods invoked therein will be added to the
	 * methods and invocations of the static call graph.
	 * @param methodName the method name.
	 * @param invocationVisitor the invocations nodes visitor.
	 */
	private void addInvocations(TypeDeclaration cls, MethodDeclaration method, String methodName, MethodInvocationVisitor invocationVisitor) {
		method.accept(invocationVisitor);
		
		for (MethodInvocation invocation: invocationVisitor.getMethods()) {
			Expression expr = invocation.getExpression();
			String invocationName = "";
			
			if (expr != null) {
				ITypeBinding type = expr.resolveTypeBinding();
				
				if (type != null) 
					invocationName = type.getQualifiedName() + "::" + invocation.getName().toString();
				else
					invocationName = expr + "::" + invocation.getName().toString();
			}
			
			else
				invocationName = PackageDeclarationVisitor.getFullName(cls) + "::" + invocation.getName().toString();
			
			this.addMethod(invocationName);
			this.addInvocation(methodName, invocationName);
		}
	}
	
	/**
	 * Adds the invoked super methods within a method to the static call graph.
	 * @param methodName the name of the method whose super methods invoked therein
	 * will be added to the static call graph
	 * @param invocationVisitor the super methods invocations nodes visitor.
	 */
	private void addSuperInvocations(String methodName, MethodInvocationVisitor invocationVisitor) {
		for (SuperMethodInvocation superInvocation: invocationVisitor.getSuperMethods()) {
			
			String superInvocationName = superInvocation.getName().getFullyQualifiedName();
			this.addMethod(superInvocationName);
			this.addInvocation(methodName, superInvocationName);
		}
	}
}
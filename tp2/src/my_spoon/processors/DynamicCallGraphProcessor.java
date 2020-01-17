package my_spoon.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my_spoon.callgraph.DynamicCallGraph;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtBlockImpl;

/**
 * A Dynamic Call Graph processor that instruments classes containing main methods.
 * The processor instruments the main method and the methods invoked in it. The
 * sensor instructions consists in using methods of the DynamicCallGraph
 * to construct the dynamic call graph of a given execution scenario.<br>
 * The instrumentation consists of adding DynamicCallGraph.addInvocation()
 * before every method invoked in the main method and methods invoked within it,
 * to construct the invocations of the dynamic call graph.
 * The main method is also instrumented by an ending DynamicCallGraph.display()
 * that displays the call graph.
 * @author anonbnr
 * @author Amandine Paillard
 * @see DynamicCallGraph
 */
public class DynamicCallGraphProcessor extends AbstractProcessor<CtClass> {
	
	/* ATTRIBUTES */
	/**
	 * a map between an invocation and its positions (line number, column number) 
	 * of invocation in the original source code.
	 */
	private Map<CtInvocation, List<SourcePosition>> map = new HashMap<>();
	
	/* METHODS */
	/**
	 * Processes only classes that contain a main method.<br> 
	 */
	@Override
	public boolean isToBeProcessed(CtClass candidate) {
		return !candidate.getMethodsByName("main").isEmpty();
	}
	
	/**
	 * processes the class containing a main method, by instrumenting
	 * all methods <em>m</em> invoked in the main method and methods invoked
	 * in the declarations of <em>m</em>.
	 */
	@Override
	public void process(CtClass cls) {
		
		CtMethod main = (CtMethod) cls.getMethodsByName("main").get(0);
		String mainName = cls.getQualifiedName() + "::main";
		
		for (CtInvocation invokedInMain: this.getMainInvokedMethods(cls)) {
			if (instrumentMethodInMain(mainName, invokedInMain)) {
				
				CtMethod methodInMain = this.getDeclaration(invokedInMain);
				String methodInMainName = methodInMain.getDeclaringType().getQualifiedName() +
						"::" + methodInMain.getSimpleName();
				
				if (methodInMain.getBody() != null)
					for (CtInvocation invokedInMethod: this.getInvokedMethods(methodInMain))
						instrumentMethodInMethod(methodInMainName, invokedInMethod);
			}
		}
		
		main.getBody().insertEnd(createGraphDisplaySensor());
	}
	
	/**
	 * Gets method invocations in a class' main method
	 * @param cls the target class
	 * @return the list of method invocations in the class' main method
	 */
	private List<CtInvocation> getMainInvokedMethods(CtClass cls) {
		CtMethod main = (CtMethod) cls.getMethodsByName("main").get(0);
		
		return this.getInvokedMethods(main);
	}
	
	/**
	 * Gets method invocations in a method.
	 * @param method the target method.
	 * @return the list of method invocations in the method.
	 */
	private List<CtInvocation> getInvokedMethods(CtMethod method) {
		return method.getElements(
				new TypeFilter<CtInvocation>(CtInvocation.class)
		);
	}
	
	/**
	 * The declaration of a method from its invocation.
	 * @param invocation the invocation of a method
	 * @return the declaration of the invoked method.
	 */
	private CtMethod getDeclaration(CtInvocation invocation) {
		return (CtMethod) invocation.getExecutable().getExecutableDeclaration();		
	}
	
	/**
	 * Tags an invocation by adding it to the Invocation -> positions map
	 * @param invocation the invocation to tag
	 */
	private void tagInvocation(CtInvocation invocation) {
		if (!map.containsKey(invocation))
			map.put(invocation, new ArrayList<>());
		
		map.get(invocation).add(invocation.getPosition());
	}
	
	/**
	 * Checks whether an invocation is tagged
	 * @param invocation the invocation to tag
	 * @return true if the invocation is tagged
	 */
	private boolean isATaggedMethodInvocation(CtInvocation invocation) {
		return map.containsKey(invocation) && 
				map.get(invocation).contains(invocation.getPosition());
	}
	
	/**
	 * Checks whether the invocation is an instrumentation sensor instruction
	 * @param invocation the invocation to check
	 * @return true if the invocation is an instrumentation sensor instruction
	 */
	private boolean isAGraphMethodInvocation(CtInvocation invocation) {
		return invocation.toString().contains("DynamicCallGraph");
	}
	
	/**
	 * creates a dynamic call graph instrumentation sensor instruction
	 * consisting of an invocation of DynamicCallGraph.addInvocation(source, destination)
	 * @param source the name of the invocation's source method
	 * @param destination the name of the invocation's destination method
	 * @return an invocation of DynamicCallGraph.addInvocation(source, destination)
	 */
	private CtInvocation createGraphAddInvocationSensor(String source, String destination) {
		CtTypeAccess accessToGraph = 
				getFactory().createTypeAccess(
						getFactory().createCtTypeReference(DynamicCallGraph.class));
		
		CtExecutableReference refGraphAddInvocation = 
				getFactory()
				.Type()
				.get(DynamicCallGraph.class)
				.getMethodsByName("addInvocation")
				.get(0)
				.getReference();
		
		CtExpression sourceArg = getFactory()
				.createLiteral(source);
		CtExpression destinationArg = getFactory()
				.createLiteral(destination);
		
		List<CtExpression<?>> arguments = Arrays.asList(sourceArg, destinationArg);
		
		return getFactory().createInvocation(
				accessToGraph, 
				refGraphAddInvocation,
				arguments
		);
	}
	
	/**
	 * creates a dynamic call graph instrumentation sensor instruction
	 * consisting of an invocation of DynamicCallGraph.display()
	 * @return an invocation of DynamicCallGraph.display()
	 */
	private CtInvocation createGraphDisplaySensor() {
		CtTypeAccess accessToGraph = 
				getFactory().createTypeAccess(
						getFactory().createCtTypeReference(DynamicCallGraph.class));
		
		CtExecutableReference refGraphAddInvocation = 
				getFactory()
				.Type()
				.get(DynamicCallGraph.class)
				.getMethodsByName("display")
				.get(0)
				.getReference();
		
		return getFactory().createInvocation(
				accessToGraph, 
				refGraphAddInvocation
		);
	}
	
	/**
	 * Inserts the graph instrumentation sensor instruction in the proper position,
	 * according to the position of the invocation to instrument in its parent.<br>
	 *  
	 * @param invocation the method invocation to instrument
	 * @param graphSensor the dynamic call graph instrumentation sensor instruction
	 */
	private void insertSensor(CtInvocation invocation, CtInvocation graphSensor) {
		
		CtElement parentElement = invocation.getParent();
		while (parentElement != null && 
				!(parentElement instanceof CtBlock) 
				&& !(parentElement.getParent() instanceof CtBlock)) {
			parentElement = parentElement.getParent();
		}
		
		/*
		 * if the invocation is a direct statement of a block
		 * insert the sensor before it
		 */
		if (parentElement.getClass() == CtBlockImpl.class)
			invocation.insertBefore(graphSensor);
		
		/*
		 * else get the nearest enclosing statement
		 * and insert the sensor before it
		 */
		else {
			CtStatement parent = (CtStatement) parentElement;
			parent.insertBefore(graphSensor);
		}
	}
	
	/**
	 * Instruments a method invocation in the main method of a class
	 * with a DynamicCallGraph.addInvocation(source, destination)
	 * sensor instruction. The source designates the fully-qualified
	 * name of the main method, while the destination designates
	 * the fully qualified name of the invoked method in the main.
	 * @param mainName the fully-qualified name of the main method
	 * @param invokedInMain the invocation of a method in the main method
	 * @return true if the instrumentation has been added to the main method.
	 */
	private boolean instrumentMethodInMain(String mainName, CtInvocation invokedInMain) {
		if (!this.isAGraphMethodInvocation(invokedInMain)) {
			
			CtMethod methodInMain = this.getDeclaration(invokedInMain);
			String methodInMainName = methodInMain.getDeclaringType().getQualifiedName() +
					"::" + methodInMain.getSimpleName();
			
			CtInvocation addInvocationSensor = createGraphAddInvocationSensor(mainName, methodInMainName);
			
			this.insertSensor(invokedInMain, addInvocationSensor);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Instruments a method invocation in a method invoked in the main method of a class,
	 * with a DynamicCallGraph.addInvocation(source, destination)
	 * sensor instruction. The source designates the fully-qualified
	 * name of the method invoked in the main method, while the destination designates
	 * the fully qualified name of the invoked method in the declaration of the 
	 * one invoked in the main method.
	 * @param methodInMainName the fully-qualified name of the method invoked in the main method
	 * @param invokedInMethod the invocation of a method in the invoked method's declaration
	 * @return true if the instrumentation has been added to the invoked method.
	 */
	private boolean instrumentMethodInMethod(String methodInMainName, CtInvocation invokedInMethod) {
		if (!this.isAGraphMethodInvocation(invokedInMethod)
				&& !this.isATaggedMethodInvocation(invokedInMethod)) {
			CtMethod methodInMethod = this.getDeclaration(invokedInMethod);
			String methodInMethodName = methodInMethod.getDeclaringType().getQualifiedName() +
					"::" + methodInMethod.getSimpleName();
			
			CtInvocation addInvocationSensor = createGraphAddInvocationSensor(methodInMainName, methodInMethodName);
			this.insertSensor(invokedInMethod, addInvocationSensor);
			this.tagInvocation(invokedInMethod);
			
			return true;
		}
		
		return false;
	}
}

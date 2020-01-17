package visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import comparators.MethodParamNumberComparator;
import comparators.MethodStatementsNumberReverseComparator;

/**
 * A MethodDeclaration and ClassDeclaration nodes visitor.
 * It's used for method-level information extraction for projects or individual classes.
 * @author anonbnr
 * @author Amandine Paillard
 *
 */
public class MethodDeclarationVisitor extends ASTVisitor {
	
	private ArrayList<MethodDeclaration> methods = new ArrayList<>();
	private Map<TypeDeclaration, List<MethodDeclaration>> map = new HashMap<>();
	
	/**
	 * Stores the list of all visited methods
	 */
	@Override
	public boolean visit(MethodDeclaration method) {
		if (!method.isConstructor())
			methods.add(method);
		
		return super.visit(method);
	}
	
	/**
	 * Stores the lists of visited methods per class in a map
	 */
	@Override
	public boolean visit(TypeDeclaration type) {
		if(!type.isInterface() && !map.containsKey(type))
			map.put(type, Arrays.asList(type.getMethods()));
		
		return super.visit(type);
	}
	
	/**
	 * Returns all visited methods
	 * @return all visited methods
	 */
	public ArrayList<MethodDeclaration> getMethods(){return methods;}
	
	/**
	 * Returns the total number of visited methods for the class.
	 * @param cls the class whose number of visited methods is what we're looking for.
	 * @return the number of methods for the visited class
	 */
	public List<MethodDeclaration> getMethods(TypeDeclaration cls) {
		cls.accept(this);
		return map.get(cls);
	}
	
	/**
	 * @return the total number of visited methods.
	 */
	public long getNbMethods() {
		return this.methods.size();
	}
	
	/**
	 * return the total number of methods declared in the visited class cls
	 * @param cls the class whose methods' number we want to know
	 * @return the number of declared methods in the class
	 */
	public long getNbMethods(TypeDeclaration cls) {
		cls.accept(this);
		return map.get(cls).size();
	}
	
	/**
	 * The maximum number of parameters that a method has.
	 * @return the maximum number of parameters that a method has.
	 */
	public long getMaxNbParamsPerMethod() {
		return this.getMethods()
				.stream()
				.max(new MethodParamNumberComparator())
				.get().parameters().size();
	}
	
	/**
	 * The percentage of methods with the highest number of statements per class visited.
	 * @param percentage the threshold percentage of methods.
	 * @return the percentage of methods with the highest number of statements per class 
	 * visited.
	 */
	public Map<TypeDeclaration, List<MethodDeclaration>> methodsWithHighestStatements(double percentage) {
		
		Map<TypeDeclaration, List<MethodDeclaration>> result = new HashMap<>();
		
		for (TypeDeclaration cls: map.keySet()) {
			result.put(cls, map.get(cls)
					.stream()
					.filter(method -> method.getBody() != null)
					.sorted(new MethodStatementsNumberReverseComparator())
					.limit((long) Math.ceil(percentage * this.getNbMethods(cls)))
					.collect(Collectors.toList())
			);
		}
		
		return result;
	}
}
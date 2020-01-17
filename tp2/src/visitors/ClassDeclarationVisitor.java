package visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import comparators.ClassAttributeNumberReverseComparator;
import comparators.ClassMethodNumberReverseComparator;

/**
 * A TypeDeclaration and CompilationUnit node Visitor that only visits classes.
 * It's used for class-level information extraction.
 * @author anonbnr
 * @author Amandine Paillard
 */
public class ClassDeclarationVisitor extends ASTVisitor {
	private ArrayList<TypeDeclaration> classes = new ArrayList<>();
	
	@Override
	public boolean visit(TypeDeclaration node) {
		if(!node.isInterface())
			classes.add(node);
		return super.visit(node);
	}
	
	public ArrayList<TypeDeclaration> getClasses() {return classes;}
	
	/**
	 * @return the total number of classes visited in the type declarations.
	 */
	public long getNbClasses() {
		return this.classes.size();
	}
	
	/**
	 * The percentage of classes with the highest number of methods.
	 * @param percentage the threshold percentage of classes between 0 (0%) and 1 (100%).
	 * @return the percentage of classes with the highest number of methods.
	 */
	public List<TypeDeclaration> getClassesWithHighestMethods(double percentage) {
		return this.getClasses()
				.stream()
				.sorted(new ClassMethodNumberReverseComparator())
				.limit((long) Math.floor(percentage * this.getNbClasses()))
				.collect(Collectors.toList());
	}
	
	/**
	 * The percentage of classes with the highest number of attributes.
	 * @param percentage the threshold percentage of classes between 0 (0%) and 1 (100%).
	 * @return the percentage of classes with the highest number of attributes.
	 */
	public List<TypeDeclaration> getClassesWithHighestAttributes(double percentage) {
		return this.getClasses()
				.stream()
				.sorted(new ClassAttributeNumberReverseComparator())
				.limit((long) Math.floor(percentage * this.getNbClasses()))
				.collect(Collectors.toList());
	}
	
	/**
	 * The classes having methods more than a specified threshold.
	 * @param nbMethods the number of methods threshold.
	 * @return the classes with more than "nbMethods" methods. 
	 */
	public List<TypeDeclaration> getClassesWithMoreMethodsThan(int nbMethods) {
		return this.getClasses()
				.stream()
				.filter(cls -> cls.getMethods().length > nbMethods)
				.collect(Collectors.toList());
	}
	
	/**
	 * The classes in a CompilationUnit
	 * @param cUnit the compilation unit whose clases we want to extract
	 * @return the classes of the compilation unit 
	 */
	public List<TypeDeclaration> getClasses(CompilationUnit cUnit) {
		cUnit.accept(this);
		return this.getClasses();
	}
}

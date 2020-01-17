package processors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import comparators.ClassAttributeNumberReverseComparator;
import comparators.MethodStatementsNumberReverseComparator;
import visitors.ClassDeclarationVisitor;
import visitors.FieldDeclarationVisitor;
import visitors.MethodDeclarationVisitor;
import visitors.PackageDeclarationVisitor;

/**
 * The processor of statistics about a project
 * (TP2 - partie 2)
 * @author anonbnr
 * @author Amandine Paillard
 *
 */
public class StatsProcessor extends BaseProcessor {
	
	/* CONSTRUCTOR */
	public StatsProcessor(String projectPath) {
		super(projectPath);
	}
	
	/* METHODS */
	/**
	 * The total number of classes per project using visitors and streams
	 * @return the total number of classes for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long nbClasses() throws IOException {
		
		ClassDeclarationVisitor classVisitor = new ClassDeclarationVisitor();
		
		for (CompilationUnit cUnit: this.parser.parseProject())
			cUnit.accept(classVisitor);
		
		return classVisitor.getNbClasses();
	}
	
	/**
	 * The total number of lines per project
	 * @return the total number of lines for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long nbLines() throws IOException {
		
		String content = "";
		long lines = 0;
		
		for (File file: this.parser.listJavaFilesForProject()) {
			content = FileUtils.readFileToString(file);
			lines += content.lines().count();
		}
		
		return lines;
	}
	
	/**
	 * The total number of attributes per project using visitors and streams
	 * @return the total number of attributes for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long nbAttributes() throws IOException {
		FieldDeclarationVisitor fieldVisitor = new FieldDeclarationVisitor();
		
		for (CompilationUnit cUnit: this.parser.parseProject())
			cUnit.accept(fieldVisitor);
		
		return fieldVisitor.getNbFields();
	}
	
	/**
	 * The total number of methods per project using visitors and streams
	 * @return the total number of methods for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long nbMethods() throws IOException {
		
		MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
		
		for (CompilationUnit cUnit: this.parser.parseProject())
			cUnit.accept(methodVisitor);
		
		return methodVisitor.getNbMethods();
	}
	
	/**
	 * The total number of packages per project using visitors and streams
	 * @return the total number of packages for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long nbPackages() throws IOException {
		
		PackageDeclarationVisitor packageVisitor = new PackageDeclarationVisitor();
		
		for (CompilationUnit cUnit: this.parser.parseProject())
			cUnit.accept(packageVisitor);
		
		return packageVisitor.getNbPackages();
	}
	
	/**
	 * The average number of methods per class for the project
	 * @return the average number of methods for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public double averageMethodsPerClass() throws IOException {
		return (double) this.nbMethods() / (double) this.nbClasses();
	}
	
	/**
	 * The average number of lines per method
	 * @return the average number of lines per method for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public double averageLinesPerMethod() throws IOException {
		return (double) this.nbLines() / (double) this.nbMethods();
	}

	/**
	 * The average number of attributes per class
	 * @return the average number of attributes per class for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public double averageAttributesPerClass() throws IOException {
		return (double) this.nbAttributes() / (double) this.nbClasses();
	}
	
	/**
	 * The percentage of classes with the highest number of methods
	 * @param percentage the threshold percentage of classes between 0 (0%) and 1 (100%).
	 * @return the percentage of classes with the highest number of methods for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public List<TypeDeclaration> classesWithHighestMethods(double percentage) 
			throws IOException {
		
		ClassDeclarationVisitor classVisitor = new ClassDeclarationVisitor();
		
		for (CompilationUnit cUnit: this.parser.parseProject())
			cUnit.accept(classVisitor);
		
		return classVisitor.getClassesWithHighestMethods(percentage);
	}
	
	/**
	 * The percentage of classes with the highest number of attributes
	 * @param percentage the threshold percentage of classes between 0 (0%) and 1 (100%).
	 * @return the percentage of classes with the highest number of attributes for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public List<TypeDeclaration> classesWithHighestAttributes(double percentage) 
			throws IOException{
		
		ClassDeclarationVisitor classVisitor = new ClassDeclarationVisitor();
		
		for (CompilationUnit cUnit: this.parser.parseProject())
			cUnit.accept(classVisitor);
		
		return classVisitor.getClassesWithHighestAttributes(percentage);
	}
	
	/**
	 * The percentage of classes with the highest number of methods and attributes
	 * @param percentage the threshold percentage of classes between 0 (0%) and 1 (100%).
	 * @return the percentage of classes with the highest number of methods and attributes for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public List<TypeDeclaration> classesWithHighestAttributesAndMethods(double percentage) 
			throws IOException {
		
		List<TypeDeclaration> clsHighestMethods = this.classesWithHighestMethods(percentage);
		List<TypeDeclaration> clsHighestAttributes = this.classesWithHighestAttributes(percentage);
		List<TypeDeclaration> target = new ArrayList<>();
		
		for (TypeDeclaration clsHighestMethod: clsHighestMethods) {
			for (TypeDeclaration clsHighestAttribute: clsHighestAttributes) {
				if (clsHighestMethod.getName().getFullyQualifiedName()
						.equals(clsHighestAttribute.getName().getFullyQualifiedName()))
					target.add(clsHighestAttribute);
			}
		}
		
		// if no class has highest number of methods and attributes
		// return the class with the highest number of attributes
		if (target.isEmpty()) {
			return Arrays.asList(clsHighestAttributes
					.stream()
					.max(new ClassAttributeNumberReverseComparator())
					.get());
		}
		
		return target;
	}
	
	/**
	 * The classes having methods more than a specified threshold
	 * @param nbMethods the number of methods threshold
	 * @return the classes with more than "nbMethods" methods for the project identified by its path 
	 * @throws IOException if the project path is invalid
	 */
	public List<TypeDeclaration> classesWithMoreMethodsThan(int nbMethods)
			throws IOException {
		
		ClassDeclarationVisitor classVisitor = new ClassDeclarationVisitor();
		
		for (CompilationUnit cUnit: this.parser.parseProject())
			cUnit.accept(classVisitor);
		
		return classVisitor.getClassesWithMoreMethodsThan(nbMethods);
	}
	
	/**
	 * The percentage of methods with the highest number of statements per class
	 * @param percentage the threshold percentage of methods
	 * @return the percentage of methods with the highest number of statements per class 
	 * for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public Map<TypeDeclaration, List<MethodDeclaration>> methodsWithHighestStatements(double percentage) 
			throws IOException {
		
		MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
		
		for (CompilationUnit cUnit: this.parser.parseProject())
			cUnit.accept(methodVisitor);
		
		return methodVisitor.methodsWithHighestStatements(percentage);
	}
	
	/**
	 * The maximum number of parameters that a method has
	 * @return the maximum number of parameters that a method has in the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long maxNbParamsPerMethod() throws IOException {
		
		MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
		
		for (CompilationUnit cUnit: this.parser.parseProject())
			cUnit.accept(methodVisitor);
		
		return methodVisitor.getMaxNbParamsPerMethod();
	}
	
	// display methods
	/**
	 * Display list of classes sequentially on a new line each
	 * @param classes list of classes to display
	 * @return <ul><li>class1</li><li>class2</li><li>...</li></ul>
	 * 
	 */
	private static String newlineClassesFormat(List<TypeDeclaration> classes) {
		StringBuffer buf = new StringBuffer();
		
		for (TypeDeclaration cls: classes)
			buf.append("  " + PackageDeclarationVisitor.getFullName(cls) + "\n");
		
		return buf.toString();
	}
	
	/**
	 * Display list of methods inline, separated by commas
	 * @param methods list of methods to display
	 * @return method1, method2, ...
	 * 
	 */
	public static String inlineMethodsFormat(List<MethodDeclaration> methods) {
		StringBuffer buf = new StringBuffer();
		
		if (!methods.isEmpty()) {
			for (MethodDeclaration method: methods)
				buf.append(method.getName() + ", ");
			
			buf.delete(buf.toString().length() - 2, buf.toString().length());
			buf.append("\n");
		}
		
		return buf.toString();
	}
	
	/**
	 * Display project stats, covering:
	 * <ol>
	 * <li>Number of packages</li>
	 * <li>Number of classes</li>
	 * <li>Number of methods</li>
	 * <li>Lines of code</li>
	 * <li>Average methods/class</li>
	 * <li>Average attributes/class</li>
	 * <li>Average lines/method</li>
	 * <li>Maximum method parameter number</li>
	 * <li>10% of classes with highest number of methods</li>
	 * <li>10% of classes with highest number of attributes</li>
	 * <li>10% of classes with highest number of methods and attributes</li>
	 * <li>classes with more than 3 methods</li>
	 * <li>10% of methods with highest number of statements per class</li>
	 * </ol>
	 * @return the stats display
	 * @throws IOException
	 */
	private String projectStats() throws IOException {
		StringBuffer buf = new StringBuffer();
		
		buf.append("Nombre de packages : " + this.nbPackages() + "\n");
		buf.append("Nombre de classes : " + this.nbClasses() + "\n");
		buf.append("Nombre de méthodes : " + this.nbMethods() + "\n");
		buf.append("Lignes de code : " + this.nbLines() + "\n");
		buf.append("Moyenne méthodes/classe : " + this.averageMethodsPerClass() + "\n");
		buf.append("Moyenne attributs/classe :" + this.averageAttributesPerClass() + "\n");
		buf.append("Moyenne lignes/méthode :" + this.averageLinesPerMethod() + "\n");
		buf.append("nombre maximal de paramètres par rapport à toutes les méthodes : ");
		buf.append(this.maxNbParamsPerMethod() + "\n\n");
		
		buf.append("10% classes avec plus grand nombre de méthodes : \n");
		buf.append(StatsProcessor.newlineClassesFormat(this.classesWithHighestMethods(0.1)) + "\n");
		
		buf.append("10% classes avec plus grand nombre d'attributs : \n");
		buf.append(StatsProcessor.newlineClassesFormat(this.classesWithHighestAttributes(0.1)) + "\n");
		
		buf.append("10% classes avec plus grand nombre de méthodes et d'attributs : \n");
		buf.append(StatsProcessor.newlineClassesFormat(this.classesWithHighestAttributesAndMethods(0.1)) + "\n");
		
		buf.append("classes avec plus que 3 méthodes : \n");
		buf.append(StatsProcessor.newlineClassesFormat(this.classesWithMoreMethodsThan(3)) + "\n");
		
		buf.append("10% des méthodes qui possèdent le plus grand nombre de statements par classe : \n");
		
		Map<TypeDeclaration, List<MethodDeclaration>> map = this.methodsWithHighestStatements(0.1);
		
		for (TypeDeclaration cls: map.keySet()) {
			buf.append(PackageDeclarationVisitor.getFullName(cls) + " : ");
			
			if (!map.get(cls).isEmpty())
				buf.append(StatsProcessor.inlineMethodsFormat(map.get(cls)));
			else buf.append("\n");
		}
		
		return buf.toString();
	}
	
	/**
	 * Display project stats
	 * @throws IOException
	 */
	public void displayProjectStats() throws IOException {
		System.out.println(this.projectStats());
	}
}

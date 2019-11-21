package stats;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import comparators.ClassAttributeNumberReverseComparator;
import comparators.ClassMethodNumberReverseComparator;
import comparators.MethodBodyLineNumberReverseComparator;
import comparators.MethodParamNumberComparator;
import parser.Parser;
import visitors.FieldDeclarationVisitor;
import visitors.MethodDeclarationVisitor;
import visitors.PackageDeclarationVisitor;
import visitors.TypeDeclarationVisitor;

public class StatsParser {
	/*attributes*/
	private String projectPath;
	//*** Bachar's jrePath ***//
	// public static final String jrePath = "/usr/lib/jvm/java-11-oracle";
	//*** Amandine's jrePath ***//
	public static final String jrePath = "/usr/lib/jvm/java-8-openjdk-amd64/";
	
	/*constructors*/
	public StatsParser(String projectPath) {
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
	
	/**
	 * The total number of classes per project using visitors and streams
	 * @return the total number of classes for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long nbClasses() throws IOException {
		ArrayList<File> javaFiles = StatsParser.listJavaFilesForProject(new File(projectPath));
	
		TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
		String content = "";
		CompilationUnit parse = null;
		
		for (File fileEntry: javaFiles) {
			content = FileUtils.readFileToString(fileEntry);
			parse = parse(content.toCharArray());
			
			parse.accept(visitor);
		}
		
		return visitor.getTypes()
				.stream()
				.filter(type -> !type.isInterface())
				.count();
	}
	
	/**
	 * The total number of lines per project
	 * @return the total number of lines for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long nbLines() throws IOException {
		ArrayList<File> javaFiles = StatsParser.listJavaFilesForProject(new File(projectPath));
		String content = "";
		long lines = 0;
		
		for (File fileEntry: javaFiles) {
			content = FileUtils.readFileToString(fileEntry);
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
		ArrayList<File> javaFiles = StatsParser.listJavaFilesForProject(new File(projectPath));
		
		FieldDeclarationVisitor visitor = new FieldDeclarationVisitor();
		String content = "";
		CompilationUnit parse = null;
		
		for (File fileEntry: javaFiles) {
			content = FileUtils.readFileToString(fileEntry);
			parse = parse(content.toCharArray());
			parse.accept(visitor);
		}
		
		return visitor.getFields()
				.stream()
				.count();
	}
	
	/**
	 * The total number of methods per project using visitors and streams
	 * @return the total number of methods for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long nbMethods() throws IOException {
		ArrayList<File> javaFiles = StatsParser.listJavaFilesForProject(new File(projectPath));
		
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		String content = "";
		CompilationUnit parse = null;
		
		for (File fileEntry: javaFiles) {
			content = FileUtils.readFileToString(fileEntry);
			parse = parse(content.toCharArray());
			parse.accept(visitor);
		}
		
		return visitor.getMethods()
				.stream()
				.count();
	}
	
	/**
	 * The total number of packages per project using visitors and streams
	 * @return the total number of packages for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long nbPackages() throws IOException {
		ArrayList<File> javaFiles = StatsParser.listJavaFilesForProject(new File(projectPath));
		
		PackageDeclarationVisitor visitor = new PackageDeclarationVisitor();
		String content = "";
		CompilationUnit parse = null;
		
		for (File fileEntry: javaFiles) {
			content = FileUtils.readFileToString(fileEntry);
			parse = parse(content.toCharArray());
			parse.accept(visitor);
		}
		
		return visitor.getNames()
				.stream()
				.count();
	}
	
	/**
	 * The average number of methods per project
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
	 * @param percentage the threshold percentage of classes
	 * @return the percentage of classes with the highest number of methods for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public List<TypeDeclaration> classesWithHighestMethods(double percentage) 
			throws IOException {
		ArrayList<File> javaFiles = Parser.listJavaFilesForFolder(new File(projectPath));
		
		TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
		String content = "";
		CompilationUnit parse = null;
		
		for (File fileEntry: javaFiles) {
			content = FileUtils.readFileToString(fileEntry);
			parse = parse(content.toCharArray());
			parse.accept(visitor);
		}
		
		return visitor.getTypes()
				.stream()
				.filter(type -> !type.isInterface())
				.sorted(new ClassMethodNumberReverseComparator())
				.limit((long) Math.ceil(percentage * visitor.getTypes().size()))
				.collect(Collectors.toList());
	}
	
	/**
	 * The percentage of classes with the highest number of attributes
	 * @param percentage the threshold percentage of classes
	 * @return the percentage of classes with the highest number of attributes for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public List<TypeDeclaration> classesWithHighestAttributes(double percentage) 
			throws IOException{
		ArrayList<File> javaFiles = Parser.listJavaFilesForFolder(new File(projectPath));
		
		TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
		String content = "";
		CompilationUnit parse = null;
		
		for (File fileEntry: javaFiles) {
			content = FileUtils.readFileToString(fileEntry);
			parse = parse(content.toCharArray());
			parse.accept(visitor);
		}
		
		return visitor.getTypes()
				.stream()
				.filter(type -> !type.isInterface())
				.sorted(new ClassAttributeNumberReverseComparator())
				.limit((long) Math.ceil(percentage * visitor.getTypes().size()))
				.collect(Collectors.toList());
	}
	
	/**
	 * The percentage of classes with the highest number of methods and attributes
	 * @param percentage the threshold percentage of classes
	 * @return the percentage of classes with the highest number of methods and attributes for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public List<TypeDeclaration> classesWithHighestAttributesAndMethods(double percentage) 
			throws IOException {
		List<TypeDeclaration> highestMethods = this.classesWithHighestMethods(percentage);
		List<TypeDeclaration> highestAttributes = this.classesWithHighestAttributes(percentage);
		List<TypeDeclaration> target = new ArrayList<>();
		
		for (TypeDeclaration highestMethod: highestMethods) {
			for (TypeDeclaration highestAttribute: highestAttributes) {
				if (highestMethod.getName().getFullyQualifiedName()
						.equals(highestAttribute.getName().getFullyQualifiedName()))
					target.add(highestAttribute);
			}
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
		ArrayList<File> javaFiles = StatsParser.listJavaFilesForProject(new File(projectPath));
		
		TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
		String content = "";
		CompilationUnit parse = null;
		
		for (File fileEntry: javaFiles) {
			content = FileUtils.readFileToString(fileEntry);
			parse = parse(content.toCharArray());
			parse.accept(visitor);
		}
		
		return visitor.getTypes()
				.stream()
				.filter(type -> !type.isInterface() && type.getMethods().length > nbMethods)
				.collect(Collectors.toList());
	}
	
	/**
	 * The percentage of methods with the highest number of statements
	 * @param percentage the threshold percentage of methods
	 * @return the percentage of methods with the highest number of statements for the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public List<MethodDeclaration> methodsWithHighestLines(double percentage) 
			throws IOException {
		ArrayList<File> javaFiles = StatsParser.listJavaFilesForProject(new File(projectPath));
		
		TypeDeclarationVisitor classVisitor = new TypeDeclarationVisitor();
		MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
		
		String content = "";
		CompilationUnit parse = null;
		
		for (File fileEntry: javaFiles) {
			content = FileUtils.readFileToString(fileEntry);
			parse = parse(content.toCharArray());
			parse.accept(classVisitor);
		}
		
		classVisitor.getTypes()
				.stream()
				.filter(type -> !type.isInterface())
				.forEach(cls -> cls.accept(methodVisitor));
		
		return methodVisitor.getMethods()
				.stream()
				.filter(method -> method.getBody() != null)
				.sorted(new MethodBodyLineNumberReverseComparator())
				.limit((long) Math.ceil(percentage * methodVisitor.getMethods().size()))
				.collect(Collectors.toList());
	}
	
	/**
	 * The maximum number of parameters that a method has
	 * @return the maximum number of parameters that a method has in the project identified by its path
	 * @throws IOException if the project path is invalid
	 */
	public long maxNbMethodParams() throws IOException {
		ArrayList<File> javaFiles = StatsParser.listJavaFilesForProject(new File(projectPath));
		
		TypeDeclarationVisitor classVisitor = new TypeDeclarationVisitor();
		MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
		
		String content = "";
		CompilationUnit parse = null;
		
		for (File fileEntry: javaFiles) {
			content = FileUtils.readFileToString(fileEntry);
			parse = parse(content.toCharArray());
			parse.accept(classVisitor);
		}
		
		classVisitor.getTypes()
		.stream()
		.filter(type -> !type.isInterface())
		.forEach(cls -> cls.accept(methodVisitor));
		
		return methodVisitor.getMethods()
				.stream()
				.max(new MethodParamNumberComparator())
				.get().parameters().size();
	}
}

package parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A wrapper class for an ASTParser.
 * @author anonbnr
 * @author Amandine Paillard
 */
public class Parser {
	/* ATTRIBUTES */
	private String projectPath; // the project's root folder path
	private String jrePath; // the JRE path
	private ASTParser parser; // the wrapped ASTParser
	
	/* CONSTRUCTOR */
	public Parser(String projectPath) {
		this.setProjectPath(projectPath);
		this.setJREPath(System.getProperty("java.home"));
		this.setParser();
	}
	
	/* METHODS */
	// getters & setters
	/**
	 * getter of the parser's project path.
	 * @return the parser's project path.
	 */
	public String getProjectPath() {return this.projectPath;}
	
	/**
	 * setter of the parser's project path.
	 * @param projectPath the parser's new project path.
	 */
	public void setProjectPath(String projectPath) {this.projectPath = projectPath;}
	
	/**
	 * getter of the parser's JRE path.
	 * @return the parser's JRE path.
	 */
	public String getJREPath() {return this.jrePath;}
	
	/**
	 * setter of the parser's JRE path.
	 * @param jrePath the parser's new JRE path.
	 */
	public void setJREPath(String jrePath) {this.jrePath = jrePath;}
	
	/**
	 * getter of the parser's wrapped AST parser.
	 * @return the parser's wrapped AST parser.
	 */
	public ASTParser getParser() {return this.parser;}
	
	/**
	 * Provides a default configuration of the wrapped AST parser.
	 */
	protected void setParser() {
		this.parser = ASTParser.newParser(AST.JLS4); // java +1.6
		this.parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		this.parser.setResolveBindings(true);
		this.parser.setBindingsRecovery(true);
 
		Map options = JavaCore.getOptions();
		this.parser.setCompilerOptions(options);
 
		this.parser.setUnitName("");
 
		String[] sources = {this.getProjectPath()}; 
		String[] classpath = {this.getJREPath()};
 
		this.parser.setEnvironment(classpath, sources, new String[] {"UTF-8"}, true);
	}
	
	// business logic methods
	/**
	 * Lists recursively the java source files in the parsers's project folder.
	 * @return the list of java files in the parsers's project folder.
	 */
	public ArrayList<File> listJavaFilesForProject() {
		return listJavaFiles(this.getProjectPath());
	}
	
	/**
	 * If filePath designates the path of a file, then extract the file,
	 * else (if it designates the path of a folder), then invoke the algorithm
	 * recursively on the folder's content.
	 * @param filePath the path of the file/folder.
	 * @return the list of java files associated with the provided path.
	 */
	protected ArrayList<File> listJavaFiles(String filePath) {
		File folder = new File(filePath);
		ArrayList<File> javaFiles = new ArrayList<File>();
		String fileName = "";
		
		for (File fileEntry : folder.listFiles()) {
			fileName = fileEntry.getName();
			
			if (fileEntry.isDirectory())
				javaFiles.addAll(listJavaFiles(fileEntry.getAbsolutePath()));
			
			else if (fileName.endsWith(".java"))
				javaFiles.add(fileEntry);
		}

		return javaFiles;
	}
	
	/**
	 * Parses the source files of the parsers's project.
	 * @return the list of compilation units 
	 * obtained from parsing the parsers's project source files.
	 * @throws IOException if the content of a source file in the parsers's project cannot be read.
	 */
	public ArrayList<CompilationUnit> parseProject() throws IOException {
		ArrayList<CompilationUnit> cUnits = new ArrayList<>();
		
		for(File sourceFile: this.listJavaFilesForProject())
			cUnits.add(this.parse(sourceFile));
		
		return cUnits;
	}
	
	/**
	 * Parses the provided source file.
	 * @param sourceFile the source file to parse.
	 * @return the compilation unit obtained from parsing the source file.
	 * @throws IOException if the content of the source file cannot be read.
	 */
	public CompilationUnit parse(File sourceFile) throws IOException {
		this.parser.setSource(FileUtils.readFileToString(sourceFile).toCharArray());
		
		return (CompilationUnit) parser.createAST(null);
	}
}
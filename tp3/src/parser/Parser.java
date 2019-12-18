package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import visitors.FieldDeclarationVisitor;
import visitors.MethodDeclarationVisitor;
import visitors.MethodInvocationVisitor;
import visitors.TypeDeclarationVisitor;

public class Parser {

	//*** Bachar's projectPath ***//
	// public static final String projectPath = "/home/anonbnr/eclipse-workspace/design_patterns";
	//*** Amandine's projectPath ***//
	public static final String projectPath = "/home/amapai/workspaces/eclipse-workspace/step2/";
	public static final String projectSourcePath = projectPath + "/src";
	
	//*** Bachar's jrePath ***//
	// public static final String jrePath = "/usr/lib/jvm/java-11-oracle";
	//*** Amandine's jrePath ***//
	public static final String jrePath = "/usr/lib/jvm/java-8-openjdk-amd64/";
	

	public static void main(String[] args) throws IOException {

		// read java files
		final File folder = new File(projectSourcePath);
		ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);

			CompilationUnit parse = parse(content.toCharArray());
			
//			printClassInfo(parse);
//			printMethodInfo(parse);
//			printMethodInvocationInfo(parse);
			
		}
	}

	// read all java files from specific folder
	public static ArrayList<File> listJavaFilesForFolder(final File folder) {
		ArrayList<File> javaFiles = new ArrayList<File>();
		String fileName = "";
		for (File fileEntry : folder.listFiles()) {
			fileName = fileEntry.getName();
			if (fileEntry.isDirectory()) {
				javaFiles.addAll(listJavaFilesForFolder(fileEntry));
			} else if (fileName.endsWith(".java")) {
				 //System.out.println(fileName);
				javaFiles.add(fileEntry);
			}
		}

		return javaFiles;
	}
	
	//create AST
	public static CompilationUnit parse(char[] source) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
 
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
 
		parser.setUnitName("");
 
		String[] sources = { projectSourcePath }; 
		String[] classpath = {jrePath};
 
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
		parser.setSource(source);
		
		return (CompilationUnit) parser.createAST(null); // create and parse
	}
	
	//navigate class information
	public static void printClassInfo(CompilationUnit parse) {
		TypeDeclarationVisitor classVisitor = new TypeDeclarationVisitor();
		FieldDeclarationVisitor fieldVisitor = null;
		
		parse.accept(classVisitor);
		
		for (TypeDeclaration type: classVisitor.getTypes()) {
			if(!type.isInterface()) {
				System.out.println("Class: " + type.getName());
				System.out.println("Parent Class: " + type.getSuperclassType());
				
				fieldVisitor = new FieldDeclarationVisitor();
				type.accept(fieldVisitor);
				
				System.out.println("Attributes:");
				
				for (FieldDeclaration field: fieldVisitor.getFields())
					System.out.println(field);
			}
		}
	}
	
	//navigate method information
	public static void printMethodInfo(CompilationUnit parse) {
		MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
		parse.accept(methodVisitor);
		
		System.out.println("Methods");
		
		for (MethodDeclaration method: methodVisitor.getMethods()) 
			System.out.println(method.getReturnType2() + " " + method.getName());
		
		System.out.println();
	}
	
	//navigate method invocations within a method
	public static void printMethodInvocationInfo(CompilationUnit parse) {
		MethodDeclarationVisitor methodDeclarationVisitor = new MethodDeclarationVisitor();
		MethodInvocationVisitor methodInvocationVisitor = null;
		
		parse.accept(methodDeclarationVisitor);
		
		for (MethodDeclaration methodDeclaration: methodDeclarationVisitor.getMethods()) {
			methodInvocationVisitor = new MethodInvocationVisitor();
			
			methodDeclaration.accept(methodInvocationVisitor);
			System.out.println(methodDeclaration.getName() + " invokes :");
			
			for (MethodInvocation methodInvocation: methodInvocationVisitor.getMethods()) {
				Expression expr = methodInvocation.getExpression();
				
				if (expr != null) {
					ITypeBinding type = expr.resolveTypeBinding();
					
					if (type != null) 
						System.out.println(type.getName() + "::" + methodInvocation.getName());
				} else
					System.out.println(expr + "::" + methodInvocation.getName());
			}
			
			System.out.println();
		}
	}
}

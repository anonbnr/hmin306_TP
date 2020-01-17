package processors;

import java.io.IOException;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import visitors.ClassDeclarationVisitor;
import visitors.FieldDeclarationVisitor;
import visitors.MethodDeclarationVisitor;
import visitors.MethodInvocationVisitor;
import visitors.PackageDeclarationVisitor;

/**
 * The processor of basic and method-oriented class informations.<br>
 * (TP2 - partie 1)
 * @author anonbnr
 * @author Amandine Paillard
 *
 */
public class InfoProcessor extends BaseProcessor {
	
	/* CONSTRUCTOR */
	public InfoProcessor(String projectPath) {
		super(projectPath);
	}
	
	/* METHODS */
	// business logic
	/**
	 * Provides a formated description for a class declaration,
	 * including its attributes (owned), methods (owned and overridden)
	 * and/or invoked methods within each method.
	 * @param type the class declaration to describe.
	 * @param includeAttributes flag for displaying attributes.
	 * @param includeMethods flag for displaying methods.
	 * @param includeInvokedMethods flag for displaying method
	 * invocations in the class's methods.
	 * @return the description of the class declaration. 
	 */
	private String classInfo(TypeDeclaration type, 
			boolean includeAttributes, 
			boolean includeMethods, 
			boolean includeInvokedMethods) {
		StringBuffer buf = new StringBuffer();
		
		if (!type.isInterface()) {
			
			buf.append("Class: " + PackageDeclarationVisitor.getFullName(type) + "\n");
			
			buf.append("Superclass: ");
			Type superCls = type.getSuperclassType();
			
			if (superCls != null) {
				
				ITypeBinding binding = superCls.resolveBinding();
				
				if (binding != null && binding.isClass())
					buf.append(binding.getName() + "\n");
				
				else buf.append(superCls + "\n");
			}
			else
				buf.append("N/A\n");
			
			if (includeAttributes) {
				
				buf.append("Attributes:\n");
				FieldDeclarationVisitor fieldVisitor = new FieldDeclarationVisitor();
				type.accept(fieldVisitor);
				
				for(FieldDeclaration field: fieldVisitor.getFieldDeclarations())
					buf.append("  " + fieldInfo(field));
			}
			
			if (includeMethods) {
				
				buf.append("Methods:\n");
				MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
				type.accept(methodVisitor);
				
				for(MethodDeclaration method: methodVisitor.getMethods()) {
					buf.append("  " + methodInfo(method));
					
					if (includeInvokedMethods) {
						
						MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor();	
						method.accept(methodInvocationVisitor);
						
						if (methodInvocationVisitor.getMethods().isEmpty() && 
								methodInvocationVisitor.getSuperMethods().isEmpty()) {
							
							buf.append("\n");
							continue;
						}
						
						buf.append(" invokes:\n");
						
						for (MethodInvocation methodInvocation: methodInvocationVisitor.getMethods())
							buf.append("\t" + methodInvocationInfo(method, methodInvocation) + "\n");
						
						for (SuperMethodInvocation methodInvocation: methodInvocationVisitor.getSuperMethods())
							buf.append("\t" + superMethodInvocationInfo(methodInvocation) + "\n");
					}
					
					else buf.append("\n");
				}
			}
		}
		
		return buf.toString();
	}
	
	/**
	 * Provides a formated description for declared fields.
	 * @param fieldDeclaration the declared field(s) to describe.
	 * @return the description of the declared fields.
	 */
	private String fieldInfo(FieldDeclaration fieldDeclaration) {
		StringBuffer buf = new StringBuffer();
		 
		for(Object obj: fieldDeclaration.fragments()) {
			
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
			
			// field modifiers: [visibility[, static[, final]]]
			fieldDeclaration.modifiers()
			 .stream()
			 .forEach(modifier -> buf.append(modifier + " "));
			
			buf.append(fieldDeclaration.getType() + " "); // field type
			buf.append(fragment.getName() + "\n"); // field name
		}
		
		return buf.toString();
	}
	
	/**
	 * Provides a formated description for a method declaration.
	 * @param method the method declaration to describe.
	 * @return the description of the field declaration.
	 */
	private String methodInfo(MethodDeclaration method) {
		StringBuffer buf = new StringBuffer();
		
		// method modifiers: [annotations[, visibility[, abstract[, static[, final]]]]]
		method.modifiers()
			  .stream()
			  .forEach(modifier -> buf.append(modifier + " "));
		
		if (method.getReturnType2() != null) // method return type
			buf.append(method.getReturnType2() + " ");
		
		buf.append(method.getParent().getStructuralProperty(TypeDeclaration.NAME_PROPERTY) + 
				"::" + method.getName() + "("); // method declaring parent
		
		for (Object obj: method.parameters()) { // method parameters
			SingleVariableDeclaration parameter = (SingleVariableDeclaration) obj;
			buf.append(parameter + ", ");
		}
		
		if (!method.parameters().isEmpty()) // removing trailing parameter-separating comma
			buf.delete(buf.toString().length() - 2, buf.toString().length());
		
		buf.append(")");
		
		return buf.toString();
	}
	
	/**
	 * Provides a formated description for a method invocation in a method declaration.
	 * @param method the method declaration containing the method invocation.
	 * @param methodInvocation the method invocation to describe.
	 * @return the description of the method invocation in the method declaration.
	 */
	private String methodInvocationInfo(MethodDeclaration method, MethodInvocation methodInvocation) {
		StringBuffer buf = new StringBuffer();
		Expression expr = methodInvocation.getExpression();
		
		if (expr != null) { // the expression invoking the method
			ITypeBinding type = expr.resolveTypeBinding();
			
			if (type != null)
				buf.append(type.getName()); // display invoking instance/class
			else
				buf.append(expr); // display the expression otherwise
			
			buf.append("::" + methodInvocation.getName() + "("); // invoked method name
		}
		
		else // the invoked method is an instance method of the containing method's parent
			buf.append(method.getParent().getStructuralProperty(TypeDeclaration.NAME_PROPERTY) + 
					"::" + methodInvocation.getName() + "(");
	
		for (Object obj: methodInvocation.arguments()) { // invoked method arguments
			Expression argument = (Expression) obj;
			ITypeBinding type = argument.resolveTypeBinding();
			
			if (type != null)
				buf.append(argument + ": " + type.getName()); // display argument + type
			else
				buf.append(argument); // display argument alone otherwise
			
			buf.append(", ");
		}
		
		if (!methodInvocation.arguments().isEmpty()) // removing trailing argument-separating comma
			buf.delete(buf.toString().length() - 2, buf.toString().length());
		
		buf.append(")");
		
		return buf.toString();
	}
	
	/**
	 * Provides a formated description for a super method invocation in a method declaration.
	 * @param superMethodInvocation the super method invocation to describe.
	 * @return the description of the super method invocation.
	 */
	private String superMethodInvocationInfo(SuperMethodInvocation superMethodInvocation) {
		StringBuffer buf = new StringBuffer();
		IMethodBinding binding = superMethodInvocation.resolveMethodBinding();
		
		if (binding != null) { // the invoked super method
			ITypeBinding type = binding.getDeclaringClass();
			
			if (type != null)
				buf.append(type.getName()); // display declaring class
			else
				buf.append("super"); // display "super" otherwise
			
			buf.append("::" + superMethodInvocation.getName() + "("); // invoked super method name
		}
	
		for (Object obj: superMethodInvocation.arguments()) { // invoked super method arguments
			Expression argument = (Expression) obj;
			ITypeBinding type = argument.resolveTypeBinding();
			
			if (type != null)
				buf.append(argument + ": " + type.getName()); // display argument + type
			else
				buf.append(argument); // display argument alone otherwise
			
			buf.append(", ");
		}
		
		if (!superMethodInvocation.arguments().isEmpty()) // removing trailing argument-separating comma
			buf.delete(buf.toString().length() - 2, buf.toString().length());
		
		buf.append(")");
		
		return buf.toString();
	}
	
	/**
	 * Displays the <strong>basic formated class description</strong> in a compilation unit.
	 * The <strong>basic formated class description</strong> include the descriptions of its attributes and methods.
	 * @param cUnit the compilation unit whose class declarations will be displayed.
	 */
	public void displayBasicClassInfo(CompilationUnit cUnit) {
		ClassDeclarationVisitor classVisitor = new ClassDeclarationVisitor();
		cUnit.accept(classVisitor);
		
		for(TypeDeclaration type: classVisitor.getClasses())
			System.out.println(classInfo(type, true, true, false));
	}
	
	/**
	 * Displays the <strong>method-oriented formated class description</strong> in a compilation unit.
	 * The <strong>method-oriented formated class description</strong> include the descriptions of its methods
	 * and methods invoked therein.
	 * @param cUnit the compilation unit whose class declarations will be displayed.
	 */
	public void displayMethodOrientedClassInfo(CompilationUnit cUnit) {
		ClassDeclarationVisitor classVisitor = new ClassDeclarationVisitor();
		cUnit.accept(classVisitor);
		
		for(TypeDeclaration type: classVisitor.getClasses())
			System.out.println(classInfo(type, false, true, true));
	}
	
	/**
	 * Displays the <strong>basic formated class description</strong> in the compilation units 
	 * of the project parsed by the processor's parser.
	 * @throws IOException if the content of a source file in the project cannot be read.
	 */
	public void displayProjectBasicClassInfo() throws IOException {
		
		for(CompilationUnit cUnit: this.parser.parseProject())
			displayBasicClassInfo(cUnit);
	}
	
	/**
	 * Displays <strong>the method-oriented formated class description</strong>
	 * in the compilation units of the project parsed by the processor's parser.
	 * @throws IOException if the content of a source file in the project cannot be read.
	 */
	public void displayProjectMethodOrientedClassInfo() throws IOException {
		
		for(CompilationUnit cUnit: this.parser.parseProject())
			displayMethodOrientedClassInfo(cUnit);
	}
}

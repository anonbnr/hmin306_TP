package visitors;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDeclarationVisitor extends ASTVisitor {
	
	private ArrayList<MethodDeclaration> methods = new ArrayList<>();
	
	@Override
	public boolean visit(MethodDeclaration node) {
		methods.add(node);
		return super.visit(node);
	}
	
	public ArrayList<MethodDeclaration> getMethods(){return methods;}
}

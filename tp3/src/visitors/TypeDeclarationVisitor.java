package visitors;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeDeclarationVisitor extends ASTVisitor {
	private ArrayList<TypeDeclaration> types = new ArrayList<>();
	
	@Override
	public boolean visit(TypeDeclaration node) {
		types.add(node);
		return super.visit(node);
	}
	
	public ArrayList<TypeDeclaration> getTypes() {return types;}
}

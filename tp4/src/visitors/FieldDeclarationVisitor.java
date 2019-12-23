package visitors;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

public class FieldDeclarationVisitor extends ASTVisitor {
	private ArrayList<FieldDeclaration> fields = new ArrayList<>();
	private ArrayList<SimpleName> names = new ArrayList<>();
	
	@Override
	public boolean visit(FieldDeclaration node) {
		fields.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SimpleName node) {
		names.add(node);
		return super.visit(node);
	}
	
	public ArrayList<FieldDeclaration> getFields() {return fields;}
	public ArrayList<SimpleName> getNames() {return names;}
}

package visitors;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.PackageDeclaration;

public class PackageDeclarationVisitor extends ASTVisitor {
	private ArrayList<PackageDeclaration> packages = new ArrayList<>();
	private HashSet<String> names = new HashSet<>();
	
	@Override
	public boolean visit(PackageDeclaration node) {
		packages.add(node);
		String name = node.getName().toString();
		
		if (!names.contains(name))
			names.add(name);
		
		return super.visit(node);
	}
	
	public ArrayList<PackageDeclaration> getPackages() {return packages;}
	public HashSet<String> getNames() {return names;}
}

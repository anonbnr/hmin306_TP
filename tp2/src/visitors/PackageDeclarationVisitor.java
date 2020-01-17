package visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * A PackageDeclaration node visitor. It is used for package-level information extraction.
 * @author anonbnr
 * @author Amandine Paillard
 *
 */
public class PackageDeclarationVisitor extends ASTVisitor {
	private List<PackageDeclaration> packages = new ArrayList<>();
	private Set<String> names = new HashSet<>();
	
	
	@Override
	public boolean visit(PackageDeclaration node) {
		packages.add(node);
		names.add(node.getName().toString());
		
		return super.visit(node);
	}
	
	public List<PackageDeclaration> getPackages() {return packages;}
	public Set<String> getNames() {return names;}
	
	/**
	 * @return the total number of visited packages
	 */
	public long getNbPackages() {
		return names.size();
	}
	
	/**
	 * Resolve the fully qualified name of the provided type declaration 
	 * @param type the type declaration whose fully qualified name should be resolved
	 * @return the full qualified name of the type declaration
	 */
	public static String getFullName(TypeDeclaration type) {
		String name = type.getName().getIdentifier();
		ASTNode parent = type.getParent();
		
		while(parent != null && parent.getClass() == TypeDeclaration.class) {
			name = ((TypeDeclaration) parent).getName().getIdentifier() + "." + name;
			parent = parent.getParent();
		}
		
		if (type.getRoot().getClass() == CompilationUnit.class) {
			CompilationUnit root = (CompilationUnit) type.getRoot();
			
			if(root.getPackage() != null) {
				PackageDeclaration pack = root.getPackage();
				name = pack.getName().getFullyQualifiedName() + "." + name;
			}
		}
		
		return name;
	}
}

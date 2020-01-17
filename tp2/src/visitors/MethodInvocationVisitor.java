package visitors;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

/**
 * A MethodInvocation and SuperMethodInvocation node visitor. It is used for method-level information extraction,
 * particularly for invocations.
 * @author anonbnr
 * @author Amandine Paillard
 *
 */
public class MethodInvocationVisitor extends ASTVisitor {
	private ArrayList<MethodInvocation> methods = new ArrayList<>();
	private ArrayList<SuperMethodInvocation> superMethods = new ArrayList<>();
	
	@Override
	public boolean visit(MethodInvocation node) {
		methods.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SuperMethodInvocation node) {
		superMethods.add(node);
		return super.visit(node);
	}
	
	public ArrayList<MethodInvocation> getMethods(){return methods;}
	public ArrayList<SuperMethodInvocation> getSuperMethods(){return superMethods;}
}

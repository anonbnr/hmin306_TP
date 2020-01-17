package comparators;

import java.util.Comparator;

import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * Comparator of MethodDeclarations by the order on their number of parameters
 * @author anonbnr
 * @author Amandine Paillard
 */
public class MethodParamNumberComparator implements Comparator<MethodDeclaration> {

	@Override
	public int compare(MethodDeclaration o1, MethodDeclaration o2) {
		return o1.parameters().size() - o2.parameters().size();
	}
	
}

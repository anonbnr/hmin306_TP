package comparators;

import java.util.Comparator;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodParamNumberComparator implements Comparator<MethodDeclaration> {

	@Override
	public int compare(MethodDeclaration o1, MethodDeclaration o2) {
		return o1.parameters().size() - o2.parameters().size();
	}
	
}

package comparators;

import java.util.Comparator;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodBodyLineNumberReverseComparator implements Comparator<MethodDeclaration> {
	@Override
	public int compare(MethodDeclaration o1, MethodDeclaration o2) {
		return o2.getBody().statements().size() - o1.getBody().statements().size();
	}
}

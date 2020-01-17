package comparators;

import java.util.Comparator;

import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Comparator of TypeDeclarations by the reverse order on their number of methods
 * @author anonbnr
 * @author Amandine Paillard
 */
public class ClassMethodNumberReverseComparator implements Comparator<TypeDeclaration> {

	@Override
	public int compare(TypeDeclaration o1, TypeDeclaration o2) {
		return o2.getMethods().length - o1.getMethods().length;
	}
}

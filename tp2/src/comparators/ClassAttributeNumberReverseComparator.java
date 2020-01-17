package comparators;

import java.util.Comparator;

import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Comparator of TypeDeclarations by the reverse order on their number of attributes
 * @author anonbnr
 * @ author Amandine Paillard
 *
 */
public class ClassAttributeNumberReverseComparator implements Comparator<TypeDeclaration> {

	@Override
	public int compare(TypeDeclaration o1, TypeDeclaration o2) {
		return o2.getFields().length - o1.getFields().length;
	}
}

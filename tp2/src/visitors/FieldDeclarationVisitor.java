package visitors;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;

/**
 * A FieldDeclaration node visitor. It's used for field-level information extraction.
 * @author anonbnr
 * @author Amandine Paillard
 *
 */
public class FieldDeclarationVisitor extends ASTVisitor {
	private ArrayList<FieldDeclaration> fields = new ArrayList<>();
	
	@Override
	public boolean visit(FieldDeclaration node) {
		fields.add(node);
		return super.visit(node);
	}
	
	public ArrayList<FieldDeclaration> getFieldDeclarations() {return fields;}
	
	/**
	 * @return the total number of fields visited in the field declarations
	 */
	public long getNbFields() {
		return this.getFieldDeclarations()
				.stream()
				.map(fieldDeclaration -> fieldDeclaration.fragments().size())
				.reduce(0, Integer::sum);
	}
}

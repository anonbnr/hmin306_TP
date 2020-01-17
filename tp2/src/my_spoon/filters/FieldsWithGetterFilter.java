package my_spoon.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import my_spoon.utility.Utility;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.AbstractFilter;

/**
 * A spoon filter extracting fields that have getters 
 * within a parent class, along with their getters.
 * @author anonbnr
 * @author Amandine Paillard
 *
 */
public class FieldsWithGetterFilter extends AbstractFilter<CtField>{
	
	/* ATTRIBUTES */
	/**
	 * The parent class of the examined field
	 */
	private CtClass target;
	
	/**
	 * The fields getters in the parent class of the examined field
	 */
	private List<CtMethod> getters = new ArrayList<CtMethod>();
	
	/* CONSTRUCTOR */
	/**
	 * constructs a field getter filter for the provided class, 
	 * parent of the examined field.
	 * @param target the parent class of the examined field.
	 */
	public FieldsWithGetterFilter(CtClass target) {
		this.setTarget(target);
	}
	
	/* METHODS */
	// getters & setters
	public CtClass getTarget() {return this.target;}
	public void setTarget(CtClass target) {this.target = target;}
	
	public List<CtMethod> getGetters() { return getters; }
	
	/**
	 * if the field has a getter "getField()" within its parent class,
	 * then filter it out along with its getter.
	 */
	@Override
	public boolean matches(CtField field) {
		Set<CtMethod> methods = target.getMethods();
		
		String fieldName = field.getSimpleName();
		boolean matches = false;
		for(CtMethod method: methods) {
			
			// check if the method is a getter and get the matched field name portion
			String matchedFieldName = Utility.matchedFieldName(
					method.getSimpleName()
			);
			
			// if the method is indeed a getter, the matched field name will not be empty
			if(!matchedFieldName.isEmpty()) {
				String extractedFieldName = 
						Utility.lowerFirst(matchedFieldName);
				
				matches = matches || fieldName.equals(extractedFieldName);
				
				/*
				 * if the field name and the matched field name are equal
				 * then the method is a getter and both will be filtered
				 */
				if(matches) {
					getters.add(method);
					break;
				}
			}
		}
		
		return matches;
	}
}

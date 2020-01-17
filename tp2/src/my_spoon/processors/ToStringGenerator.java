package my_spoon.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import my_spoon.filters.FieldsWithGetterFilter;
import my_spoon.logger.SpoonLogger;
import my_spoon.utility.Utility;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;

/**
 * A toString() generator for a given class.<br>
 * If the class doesn't already have a toString() overriding implementation,
 * then it will generate one using the class' fields having getters declared 
 * in the class.<br>
 * Uses a FieldsWithGetterFilter spoon Filter to filter out fields 
 * of the class having getters.<br>
 * Methods in the class invoking any of the filtered out fields' getters 
 * will be instrumented by sensor instructions using a SpoonLogger.
 * @author anonbnr
 * @author Amandine Paillard
 * 
 * @see FieldsWithGetterFilter
 * @see SpoonLogger
 */
public class ToStringGenerator extends AbstractProcessor<CtClass> {
	/* ATTRIBUTES */
	/**
	 * A map between a class and the list of field getters
	 */
	private Map<CtClass, List<CtMethod>> map = new HashMap<CtClass, List<CtMethod>>();
	
	/* METHODS */
	public Map<CtClass, List<CtMethod>> getMap(){ return map; }
	
	/**
	 * Processes only classes not having a toString() implementation
	 * and fields with corresponding getters.
	 */
	@Override
	public boolean isToBeProcessed(CtClass candidate) {
		return !analyze(candidate).isEmpty();
	}
	
	/**
	 * Instruments the class' methods containing invocations
	 * of field getters, using the SpoonLogger and generates
	 * the corresponding the toString() implementation of the class. 
	 */
	@Override
	public void process(CtClass cls) {
		
		for (CtMethod getter: map.get(cls))
			instrument(cls, getter);
		
		generate(cls);
	}

	/**
	 * Analyzes if the provided class element requires processing by the processor.<br>
	 * If the class already contains a toString() method than it will be discarded.
	 * Otherwise, it will filter out fields with corresponding getters, 
	 * using the FieldsWithGetterFilter spoon Filter and add <class, getters> to its map.
	 * @param cls the class element to examine.
	 * @return the list of the class' fields with corresponding getters.
	 */
	private List<CtField> analyze(CtClass cls) {
		
		List<CtField> fieldsWithGetters = new ArrayList<>();
		
		if(!cls.getMethodsByName("toString").isEmpty())
			return fieldsWithGetters;
		
		FieldsWithGetterFilter filter = new FieldsWithGetterFilter(cls);
		fieldsWithGetters = cls.getElements(filter);
		
		if (!fieldsWithGetters.isEmpty())
			map.put(cls, filter.getGetters());
		
		return fieldsWithGetters;
	}
	
	/**
	 * Instruments methods of the class element containing 
	 * invocations of the provided getter method with sensor
	 * instructions, using the SpoonLogger.<br>
	 * The sensors instructions correspond to "info" level message logging.
	 * @param cls the class whose methods to instrument.
	 * @param getter the field getter whose invocations to 
	 * look for in the class' methods.
	 */
	private void instrument(CtClass cls, CtMethod getter) {
		
		Map<CtStatement, CtInvocation> statementToInvocationMap = null;
		
		for(Object objMethod: cls.getMethods()) {
			CtMethod method = (CtMethod) objMethod;
			statementToInvocationMap = new HashMap<>();
			
			for (CtStatement statement: method.getBody().getStatements()) {
				// if the method statements contain an invocation to the getter
				if (statement.toString().contains(getter.getSimpleName()+"()")) {
					// create the sensor instruction
					CtInvocation loggerInvocation = 
							logGetterInvocationInMethod(
									SpoonLogger.class, 
									"info", 
									cls, 
									method, 
									getter
							);
					statementToInvocationMap.put(statement, loggerInvocation);
				}
			}
			
			for (Entry<CtStatement, CtInvocation> entry: statementToInvocationMap.entrySet())
				entry.getKey().insertBefore(entry.getValue());
		}
	}
	
	/**
	 * Creates a sensor instruction using the logger class and logging method
	 * in the provided method of the class element for the invoked getter
	 * @param loggerClass the Logger class (e.g. SpoonLogger)
	 * @param loggerMethodName the logging method of the Logger class (e.g. SpoonLogger.info())
	 * @param cls the class declaring the method to instrument
	 * @param method the method to instrument
	 * @param getter the getter invoked in the method to instrument
	 * @return an invocation sensor instruction of the logger logging method
	 */
	private CtInvocation logGetterInvocationInMethod(
			Class<?> loggerClass, 
			String loggerMethodName, 
			CtClass cls, 
			CtMethod method, 
			CtMethod getter) {
		CtTypeAccess accessToLogger = 
				getFactory().createTypeAccess(
						getFactory().createCtTypeReference(loggerClass));
		
		CtExecutableReference refLoggerMethod = 
				getFactory()
				.Type()
				.get(loggerClass)
				.getMethodsByName(loggerMethodName)
				.get(0)
				.getReference();
		
		CtExpression argument = getFactory().createLiteral(
				cls.getQualifiedName()+"::"+getter.getSimpleName()+"()" + 
		" invoked from " + cls.getQualifiedName()+"::"+method.getSimpleName()+"()"
		);
		
		return getFactory().createInvocation(
				accessToLogger,
				refLoggerMethod,
				argument
		);
	}
	
	/**
	 * Creates the toString() implementation of the provided class element.
	 * @param cls the class whose toString() implementation will be provided.
	 * @return the toString() implementation of the provided class element.
	 */
	private String createToString(CtClass cls) {
		StringBuffer buf = new StringBuffer();
		String attributeName = "";
		
		buf.append("java.lang.StringBuffer buf = new java.lang.StringBuffer();\n");
		
		for (CtMethod getter: map.get(cls)) {
			attributeName = Utility.lowerFirst(
					Utility.matchedFieldName(getter.getSimpleName()));
			
			buf.append("buf.append(\"" + attributeName + ":\\n\");\n");
			buf.append("buf.append(\"");
			
			for(int i=0; i<=attributeName.length(); i++)
				buf.append("=");
			
			buf.append("\\n\");\n");
			buf.append("buf.append(" + getter.getSimpleName() + "()+\"\\n\\n\");\n\n");
		}
		
		buf.append("return buf.toString()");
		
		return buf.toString();
	}
	
	/**
	 * Generates a toString() method for the provided class element.
	 * @param cls the class element whose toString() method will be generated.
	 */
	private void generate(CtClass cls) {
		Factory factory = getFactory();
		
		CtMethod toString = factory.createMethod();
		toString.setVisibility(ModifierKind.PUBLIC); // visibility = public
		toString.setSimpleName("toString"); // name = toString
		toString.setType(factory.Type().stringType()); // type = java.lang.String
		
		CtAnnotation override = factory.createAnnotation();
		override.setAnnotationType(factory.Annotation().createReference(Override.class));
		toString.addAnnotation(override); // annotation = @Override
		
		CtComment autoGenerate = factory.createComment();
		autoGenerate.setContent("Automatically generated by Spoon");
		toString.addComment(autoGenerate); // prefixing comment = "Automatically generated by Spoon"
		
		String toStringResult = createToString(cls);
		CtCodeSnippetStatement toStringStatement = factory
				.Code()
				.createCodeSnippetStatement(toStringResult);
		toString.setBody(toStringStatement); // implementation = createToString(cls)
		
		System.out.println("generated toString() for class " + cls.getQualifiedName() + ":");
		System.out.println(toString);
		
		cls.addMethod(toString);
	}
}

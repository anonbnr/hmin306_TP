package processors;

import parsers.Parser;

/**
 * The base class for all project processors using 
 * a wrapper of the AST Parser of the AST JDT package.
 * @author anonbnr
 * @author Amandine Paillard
 *
 */
public class BaseProcessor {
	/* ATTRIBUTE */
	protected Parser parser;
	
	/* CONSTRUCTOR */
	public BaseProcessor(String projectPath) {
		this.parser = new Parser(projectPath);
	}
	
	/* METHODS */
	/**
	 * getter of the processor's parser's wrapper.
	 * @return the wrapper of the processor's parser. 
	 */
	public Parser getParser() { return this.parser;}
	
	/**
	 * setter of the processor's parser's wrapper.
	 * @param parser the new wrapper of the processor's parser.
	 */
	public void setParser(Parser parser) {this.parser = parser;}
}

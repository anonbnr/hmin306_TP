package my_spoon.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import my_spoon.formatters.HTMLFormatter;

/**
 * A logger intended for usage with Spoon. It wraps a java.util.logging.Logger
 * and defines configurations of the logger for the ToStringGenerator
 * and the NonFactoredDynamicCallGraph Spoon processors.<br>
 * It formats traces in both text and HTML formats using a java.util.logging.SimpleFormatter
 * and a custom-made HTMLFormatter respectively.
 * @author anonbnr
 * @author Amandine Paillard
 * 
 * @see Logger
 * @see SimpleFormatter
 * @see HTMLFormatter
 * @see ToStringGenerator
 * @see NonFactoredDynamicCallGraph
 */
public class SpoonLogger {
	/* ATTRIBUTES */
	/**
	 * The wrapped java.util.logging.Logger
	 */
	private static Logger LOGGER;
	
	/**
	 * The text file log handler
	 */
	private static FileHandler fileTxt;
	
	/**
	 * The text file formatter
	 */
	private static SimpleFormatter formatterTxt;
	
	/**
	 * The HTML file log handler
	 */
	private static FileHandler fileHTML;
	
	/**
	 * The HTML file formatter
	 */
	private static HTMLFormatter formatterHTML;
	
	/* METHODS */
	/**
	 * Configures the logger for usage with the Spoon ToStringGenerator processor.
	 * It removes the console handler (no traces are displayed in the console)
	 * and provides FileHandlers for text and HTML files, using
	 * a SimpleFormatter and an HTMLFormatter respectively.<br>
	 * It traces messages of Level INFO (informational messages).
	 * @param logDirectoryPath the root directory for log files.
	 * @param txtFilePath the text file log.
	 * @param htmlFilePath the HTML file log.
	 * @throws IOException if an error occurs during the writing of the log files
	 */
	public static void toStringGeneratorSetup(String logDirectoryPath, 
			String txtFilePath, String htmlFilePath) 
					throws IOException {
		LOGGER = Logger.getGlobal();
		
		Logger rootLogger = Logger.getLogger("");
		Handler[] rootLoggerHandlers = rootLogger.getHandlers();
		
		if(rootLoggerHandlers[0] instanceof ConsoleHandler)
			rootLogger.removeHandler(rootLoggerHandlers[0]);
		
		LOGGER.setLevel(Level.INFO);
		
		if(!Files.isDirectory(Paths.get(logDirectoryPath)))
			Files.createDirectory(Paths.get(logDirectoryPath));
		
		setTxtFormatter(txtFilePath);
		setHTMLFormatter(htmlFilePath);
	}
	
	/**
	 * Adds a text file handler to the logger using a SimpleFormatter.
	 * @param txtFilePath the text file log path
	 * @throws IOException if an error occurs during the opening of the log file.
	 */
	private static void setTxtFormatter(String txtFilePath)
			throws IOException {
		fileTxt = new FileHandler(txtFilePath);
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		LOGGER.addHandler(fileTxt);
	}
	
	/**
	 * Adds an HTML file handler to the logger using an HTMLFormatter.
	 * @param htmlFilePath the HTML file log path
	 * @throws IOException if an error occurs during the opening of the log file.
	 */
	private static void setHTMLFormatter(String htmlFilePath)
			throws IOException {
		fileHTML = new FileHandler(htmlFilePath);
		formatterHTML = new HTMLFormatter();
		fileHTML.setFormatter(formatterHTML);
		LOGGER.addHandler(fileHTML);
	}
	
	/**
	 * wrapper for the java.util.logging.Logger.severe() method
	 * @param msg the message to log.
	 */
	public static void severe(String msg) {
		LOGGER.severe(msg);
	}
	
	/**
	 * wrapper for the java.util.logging.Logger.warning() method
	 * @param msg the message to log.
	 */
	public static void warning(String msg) {
		LOGGER.warning(msg);
	}
	
	/**
	 * wrapper for the java.util.logging.Logger.info() method
	 * @param msg the message to log.
	 */
	public static void info(String msg) {
		LOGGER.info(msg);
	}
	
	/**
	 * wrapper for the java.util.logging.Logger.fine() method
	 * @param msg the message to log.
	 */
	public static void fine(String msg) {
		LOGGER.fine(msg);
	}
	
	/**
	 * wrapper for the java.util.logging.Logger.finer() method
	 * @param msg the message to log.
	 */
	public static void finer(String msg) {
		LOGGER.finer(msg);
	}
	
	/**
	 * wrapper for the java.util.logging.Logger.finest() method
	 * @param msg the message to log.
	 */
	public static void finest(String msg) {
		LOGGER.finest(msg);
	}
}
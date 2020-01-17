package my_spoon.formatters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A java.util.logging.Formatter that formats log messages into HTML documents.
 * @author anonbnr
 * @author Amandine Paillard
 */
public class HTMLFormatter extends Formatter {
	
	/* METHODS */
	// inherited abstract method to be implemented to define formatting behavior
	/**
	 * Format log records into an HTML table. It colors in red log records
	 * with severity level of warning or higher.
	 */
	@Override
	public String format(LogRecord record) {
		StringBuffer buf = new StringBuffer(1000);
		buf.append("<tr>/n");
		
		// color any levels >= WARNING in red
		if(record.getLevel().intValue() >= Level.WARNING.intValue()) {
			buf.append("\t<td style=\"color:red\">");
			buf.append("<strong>");
			buf.append(record.getLevel());
			buf.append("</strong>");
		}
		else {
			buf.append("\t<td>");
			buf.append(record.getLevel());
		}
		
		buf.append("</td>\n");
		buf.append("\t<td>");
		buf.append(calcDate(record.getMillis()));
		buf.append("</td>\n");
		
		buf.append("\t<td>");
		buf.append(formatMessage(record));
		buf.append("</td>\n");
		
		buf.append("</tr>\n");
		
		return buf.toString();
	}
	
	/**
	 * Returns a date formatted as "'Month' 'day of the week', 'year' 'hours':'minutes'"
	 * from the provided milliseconds.
	 * @param millis the milliseconds to convert to a date.
	 * @return the formated date obtained from milliseconds.
	 */
	private String calcDate(long millis) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultDate = new Date(millis);
		return dateFormat.format(resultDate);
	}
	
	// method invoked just after the formatter is created
	/**
	 * Creates the HTML log document, provides styles for the log table, 
	 * and create its columns.
	 */
	@Override
	public String getHead(Handler h) {
		StringBuffer buf = new StringBuffer(10000);
		buf.append("<!DOCTYPE html>\n");
		
		buf.append("\t<head>\n");
		buf.append("\t\t<style>\n");
		buf.append("\t\ttable { width: 100% }\n");
		buf.append("\t\tth { font: bold 10pt Tahoma; }\n");
		buf.append("\t\ttd { font: normal 10pt Tahoma; }\n");
		buf.append("\t\th1 { font: normal 11pt Tahoma; }\n");
		buf.append("\t\t</style>\n");
		buf.append("\t</head>\n");
		
		buf.append("\t<body>\n");
		buf.append("\t\t<h1>" + (new Date()) + "\n");
		buf.append("\t\t<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n");
		buf.append("\t\t\t<tr align=\"left\">\n");
		buf.append("\t\t\t\t<th style=\"width:10%\">LogLevel</th>\n");
		buf.append("\t\t\t\t<th style=\"width:15%\">Time</th>\n");
		buf.append("\t\t\t\t<th style=\"width:75%\">LogMessage</th>\n");
		buf.append("\t\t\t</tr>\n");
		
		return buf.toString();
	}
	
	// method invoked just after the formatter is closed
	/**
	 * Finishes the creation of the HTML log document.
	 */
	@Override
	public String getTail(Handler h) {
		StringBuffer buf = new StringBuffer(100);
		buf.append("\t\t</table>\n");
		buf.append("\t</body>\n");
		buf.append("</html>");
		
		return buf.toString();
	}
}

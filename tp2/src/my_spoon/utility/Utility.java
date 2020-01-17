package my_spoon.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
	
	/**
	 * if the method's simple name matches that of a getter
	 * (i.e. getAttributeName()), then return the matched field portion
	 * (i.e. AttributeName)
	 * @param methodSimpleName the target method's simple name
	 * @return the matched field name portion of the simple name, or ""
	 */
	public static String matchedFieldName(String methodSimpleName) {
		Pattern p = Pattern.compile("get(([A-Z][a-z0-9]*)+)");
		Matcher matcher = p.matcher(methodSimpleName);
		if (matcher.matches())
			return matcher.group(1);
		return "";
	}
	
	/**
	 * returns the same string but starting with a lower case
	 * @param string the target string
	 * @return the same string but starting with a lower case
	 */
	public static String lowerFirst(String string) {
		return string.substring(0,1).toLowerCase() + 
		string.substring(1, string.length());
	}
}

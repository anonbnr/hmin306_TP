package to_string_generator.data;

import java.io.IOException;

import my_spoon.logger.SpoonLogger;

public class Main {

	public static void main(String[] args) {
		try {
			SpoonLogger.toStringGeneratorSetup("log/", "log/toStringGenerator.txt", "log/toStringGenerator.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Empty empty = new Empty();
		empty.display();
		
		Closed closed = new Closed("closed", "sealed");
		closed.callClosedAttr1();
		closed.callClosedAttr2();
		closed.callAllClosedAttr();
		
		AlreadyHasToString already = new AlreadyHasToString("already_attr1", "already_attr2");
		already.callGetterAttr1();
		already.callGetterAttr2();
		already.callAllGetters();
		System.out.println(already);
		
		HasGettersButNoToString allGetters = new HasGettersButNoToString("allGetters_attr1", "allGetters_attr2");
		allGetters.callAllGetters();
		System.out.println(allGetters);
		
		HasSomeGettersButNoToString someGetters = new HasSomeGettersButNoToString("someGetters_attr1", "someGetters_attr2", "someGetters_closed");
		someGetters.callSomeGetters();
		someGetters.callClosedAttr();
		System.out.println(someGetters);
	}
}

package test;

import java.io.IOException;

import org.junit.Test;

import processors.InfoProcessor;

/**
 * A test class to test the basic (attributes and methods) and method-oriented (basic + invocations) display
 * formats of the info processor (system test)
 * @author anonbnr
 * @author Amandine Paillard
 */
public class InfoProcessorTest {
	
	private static InfoProcessor info = new InfoProcessor("/home/anonbnr/eclipse-workspace/design_patterns/src/");
	
	@Test
	public void testDisplayProjectBasicClassInfo() {
		System.out.println("TESTING THE BASIC DISPLAY FORMAT");
		try {
			info.displayProjectBasicClassInfo();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDisplayProjectMethodOrientedClassInfo() {
		System.out.println("TESTING THE METHOD-ORIENTED DISPLAY FORMAT");
		try {
			info.displayProjectMethodOrientedClassInfo();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

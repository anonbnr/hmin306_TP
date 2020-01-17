package test;

import java.io.IOException;

import org.junit.Test;

import processors.StatsProcessor;

/**
 * A test class to test the statistics display of the stats processor (system test)
 * @author anonbnr
 * @author Amandine Paillard
 */
public class StatsProcessorTest {
	private static StatsProcessor stats;
	
	@Test
	public void testDisplayProjectStats() {
		stats = new StatsProcessor("/home/anonbnr/eclipse-workspace/design_patterns/src/");
		
		try {
			
			stats.displayProjectStats();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

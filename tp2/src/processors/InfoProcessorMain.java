package processors;

import java.io.IOException;
import java.util.Scanner;

/**
 * Entry point for using the AST-based class information processor (main method)
 * @author anonbnr
 * @author Amandine Paillard
 */
public class InfoProcessorMain {

	public static void main(String[] args) throws IOException {
		String pathToSource = args[0];
		InfoProcessor infoProcessor = new InfoProcessor(pathToSource);

		Scanner sc = new Scanner(System.in);
		int choice = 1;
		
		help();
		while(choice != 0) {
			if (sc.hasNextInt()) {
				choice = sc.nextInt();
				sc.nextLine();
			}
			else
				choice = 0;
			
			switch(choice) {
			case 1:
				infoProcessor.displayProjectBasicClassInfo();
				break;
			case 2:
				infoProcessor.displayProjectMethodOrientedClassInfo();
				break;
			case 3:
				help();
				break;
			case 0:
				System.out.println("Bye...");
				return;
			default:
				System.out.println("Sorry, wrong input. Please try again.");
				help();
				break;
			}
		}
	}
	
	private static void help() {
		System.out.println("With which grain of precision do you want to display class information?"+
				"\n1. Basic information format (attributes (owned) and methods (owned and overriden)." +
				"\n2. Method-oriented information format (Basic information format + invoked methods + super invoked methods)." +
				"\n3. Help menu." +
				"\n0 To quit.");
	}
}

package processors;

import java.io.IOException;
import java.util.Scanner;

/**
 * Entry point for using the AST-based Processors (main method)
 * @author anonbnr
 * @author Amandine Paillard
 */
public class Main {

	public static void main(String[] args) throws IOException {

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
				InfoProcessorMain.main(args);
				help();
				break;
			case 2:
				StatsProcessorMain.main(args);
				help();
				break;
			case 3:
				help();
				break;
			case 0:
				System.out.println("Bye...");
				break;
			default:
				System.out.println("Sorry, wrong input. Please try again.");
				help();
				break;
			}
		}
		
		sc.close();
		return;
	}
	
	private static void help() {
		System.out.println("What kind of information would you like to see about your project?"+
				"\n1. Class information" +
				"\n2. General statistics" +
				"\n3. Help menu." +
				"\n0 To quit.");
	}
}

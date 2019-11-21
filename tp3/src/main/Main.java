package main;

import java.io.IOException;
import java.util.Scanner;

/**
 * Main entry point of all TP's.
 * User can choose what information he wants about a program 
 * whose path is hard-coded in this file only.
 * He can also compare two programs with option 6 or 7.
 * 
 * When he made his decision, main method of corresponding
 * exercise is called.
 * 
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		
		//*** Choose a Java project ***//
		String defaultPath = "/home/amapai/workspaces/eclipse-workspace/step2"; 		// Ami's path
		// String defaultPath = "/home/anonbnr/eclipse-workspace/design_patterns/src"; 	// Bachar's path
		System.out.println("Analyze will be on project " + defaultPath + "\n");
		//* Used for TP4 *//
		// String defaultPathSecondProject = "anotherVersionOfProgram'sPath"; 			// Ami's path
		// String defaultPathSecondProject = "anotherVersionOfProgram'sPath"; 			// Bachar's path
		
		//*** Storing paths ***//
		String[] paths = new String[2];
		paths[0]= defaultPath;
		// paths[1] = defaultPathSecondProject;
		
		//*** Analyse's choice ***//
		System.out.println("Which information do you want?\n"
				+ "Enter 1 for information on analysed code.\n"
				+ "Enter 2 for the dependency graph.\n"
				+ "Enter 3 for the coupling weighted classes' graph ;\n"
				+ "            the hierarchical coupling cluster ;\n"
				+ "            the partition.\n"
				+ "Enter 4 for TP4.1\n"
				+ "Enter 5 for TP4.2\n");
		Scanner sc = new Scanner(System.in);
	    int choice = sc.nextInt();
	    switch(choice) {
	    // TP 2
	    case 1:
	    	stats.Main.main(paths);;
	    	break;
	    case 2:
	    	callgraph.Main.main(paths);
	    	break;
	    // TP 3
	    case 3:
	    	couple.Main.main(paths);
	    	break;
	    // TP4
	    case 4:
	    	System.out.println("TODO");
	    	break;
	    case 5:
	    	System.out.println("TODO");
	    	break;
	    default:
	    	System.out.println("Sorry, wrong input. Please try again.");
	    	break;
	    }
	    sc.close();
	}
}

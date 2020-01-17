package processors;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import visitors.PackageDeclarationVisitor;

/**
 * Entry point for using the AST-based statistics processor (main method)
 * @author anonbnr
 * @author Amandine Paillard
 */
public class StatsProcessorMain {
	
	/* ATTRIBUTES */
	// stats parameters
	private static double percentage = 0.1;
	private static int x = 3;

	public static void main(String[] args) throws IOException {
		
		String pathToSource = args[0];
		StatsProcessor statsProcessor = new StatsProcessor(pathToSource);

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
				System.out.println("> " + statsProcessor.nbClasses() + " classes.");
				break;
			case 2:
				System.out.println("> " + statsProcessor.nbLines() + " line(s) of code.");
				break;
			case 3:
				System.out.println("> " + statsProcessor.nbMethods() + " method(s).");
				break;
			case 4:
				System.out.println("> " + statsProcessor.nbPackages() + " package(s).");
				break;
			case 5:
				System.out.println("> " + statsProcessor.averageMethodsPerClass() + " method(s) per class in average.");
				break;
			case 6:
				System.out.println("> " + statsProcessor.averageLinesPerMethod() + " line(s) per methods in average.");
				break;
			case 7:
				System.out.println("> " + statsProcessor.averageAttributesPerClass() + " attributes per class in average.");
				break;
			case 8:
				System.out.println(" > " + percentage*100 + " percent class(es) that have the highest number of methods:");
				statsProcessor.classesWithHighestMethods(percentage)
					.forEach(type -> System.out.println(PackageDeclarationVisitor.getFullName(type)));
				break;
			case 9:
				System.out.println(" > " + percentage*100 + " percent class(es) that have the highest number of attributes:");
				statsProcessor.classesWithHighestAttributes(percentage)
					.forEach(type -> System.out.println(PackageDeclarationVisitor.getFullName(type)));
				break;
			case 10:
				System.out.println(" > Class(es) that are in the " + percentage*100 + " percent of classes having the highest number of methods and " 
						+ percentage*100 + " percent of classes having the highest number of fields:");
				statsProcessor.classesWithHighestAttributesAndMethods(percentage)
					.forEach(type -> System.out.println(PackageDeclarationVisitor.getFullName(type)));
				break;
			case 11:
				System.out.println("> X is set to " + x + ".");
				System.out.println(" > Class(es) that have more than "+x+" methods:");
				statsProcessor.classesWithMoreMethodsThan(x)
					.forEach(type -> System.out.println(PackageDeclarationVisitor.getFullName(type)));
				break;
			case 12:
				System.out.println(" > " + percentage*100 + "% methods that have the highest number of statements for each class are:");
				statsProcessor.methodsWithHighestStatements(percentage)
					.forEach((TypeDeclaration type, List<MethodDeclaration> methods) -> {
						System.out.print(PackageDeclarationVisitor.getFullName(type) + " : ");
						if (!methods.isEmpty())
							System.out.print(StatsProcessor.inlineMethodsFormat(methods));
						else System.out.println();
					});
				break;
			case 13:
				System.out.println("> " + statsProcessor.maxNbParamsPerMethod() + " maximum number of methods' parameters.");
				break;
			case 14:
				help();
				break;
			// Coupling metric from TP 3.1 
			/* case 14 :
				System.out.println("Coupling metrics between A<-->B are:\nWith A = Parser.java\nWith B = MethodInvocationVisitor.java");
				String classAName = "Parser";
				String classBName = "MethodInvocationVisitor";
				int totalCouples = TP3.getCouplingBetween(javaFiles, classAName, classBName) + TP3.getCouplingBetween(javaFiles, classBName, classAName);
				// we do not display score because is rounded to 0 :)
				System.out.println(classAName+"<-["+totalCouples+"/"+TP3.countAllRelationsInProject(javaFiles)+"]->"+classBName);;
				break;*/
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
		System.out.println("Which stat do you want?"+
				"\n1. Number of classes." +
				"\n2. Number of line of code." +
				"\n3. Number of methods." +
				"\n4. Number of packages." +
				"\n5. Average number of methods per class." +
				"\n6. Average number of line of code per methods." +
				"\n7. Average number of attributes per class." +
				"\n8. " + percentage*100 + "% classes with the the highest number of methods." +
				"\n9. " + percentage*100 + "% classes with the highest number of attributes." +
				"\n10. Classes that are both part of the " + percentage*100 + "% classes with the highest number of methods and " 
					+ percentage*100 + "% classes with the highest number of attributes." +
				"\n11. Classes that have more than " + x +" methods." +
				"\n12. " + percentage*100 + "% methods that have the highest number of statements." +
				"\n13. Maximum number of parameters for a method." +
				"\n14. Help menu." +
				"\n0 To quit.");
	}
}

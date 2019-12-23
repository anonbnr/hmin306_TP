package stats;

import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {
		String pathToSource = args[0];
		StatsParser parser = new StatsParser(pathToSource);

		//* stats parameters *//
		double percentage = 0.1;
		int x = 3;
		
		System.out.println("Which stat do you want?"+
				"\n1. Number of classes." +
				"\n2. Number of line of code." +
				"\n3. Number of methods." +
				"\n4. Number of packages." +
				"\n5. Average number of methods per class." +
				"\n6. Average number of line of code per methods." +
				"\n7. Average number of attributes per class." +
				"\n8. " + percentage*100 + "% classes with the more methods." +
				"\n9. " + percentage*100 + "% classes with the more attributes." +
				"\n10. Classes that are both part of the " + percentage*100 + "% classes with more methods and " + percentage*100 + "% classes with more attributes." +
				"\n11. Classes that have more than " + x +" methods." +
				"\n12. " + percentage*100 + "% methods that have the more line of code." +
				"\n13. Maximum of parameter for a method." +
				//"\n14. Coupling metrics for all classes." +
				"\n0 To quit.");
		Scanner sc = new Scanner(System.in);
		int choice = sc.nextInt();
		sc.close();
		switch(choice) {
		case 1:
			System.out.println("> " + parser.nbClasses() + " classes.");
			break;
		case 2:
			System.out.println("> " + parser.nbLines() + " line(s) of code.");
			break;
		case 3:
			System.out.println("> " + parser.nbMethods() + " method(s).");
			break;
		case 4:
			System.out.println("> " + parser.nbPackages() + " package(s).");
			break;
		case 5:
			System.out.println("> " + parser.averageMethodsPerClass() + " method(s) per class in average.");
			break;
		case 6:
			System.out.println("> " + parser.averageLinesPerMethod() + " line(s) per methods in average.");
			break;
		case 7:
			System.out.println("> " + parser.averageAttributesPerClass() + " attributes per class in average.");
			break;
		case 8:
			System.out.println(" > " + percentage*100 + " percents class(es) that have more methods:");
			parser.classesWithHighestMethods(percentage)
				.forEach(type -> System.out.println(type.getName().toString()));
			break;
		case 9:
			System.out.println(" > " + percentage*100 + " percents class(es) that have more attributes:");
			parser.classesWithHighestAttributes(percentage)
				.forEach(type -> System.out.println(type.getName().toString()));
			break;
		case 10:
			System.out.println(" > Class(es) that are in both " + percentage*100 + " percents more methods and " + percentage*100 + " percents more fields:");
			parser.classesWithHighestAttributesAndMethods(percentage)
				.forEach(type -> System.out.println(type.getName().toString()));
			break;
		case 11:
			System.out.println("> X is set to " + x + ".");
			System.out.println(" > Class(es) that have more than "+x+" methods:");
			parser.classesWithMoreMethodsThan(x)
				.forEach(type -> System.out.println(type.getName().toString()));
			break;
		case 12:
			System.out.println(" > " + percentage*100 + "% methods that have the more line of code are:");
			parser.methodsWithHighestLines(percentage)
				.forEach(type -> System.out.println(type.getName().toString()));
			break;
		case 13:
			System.out.println("> " + parser.maxNbMethodParams() + " maximum number of methods' parameters.");
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
			return;
		default:
			System.out.println("Sorry, wrong input. Please try again.");
			break;
		}
	}
}

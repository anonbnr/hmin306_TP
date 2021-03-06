package couple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Used for TP3 : coupling metrics, cluster and partitions. 
 */
public class Main {

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String pathToSource = args[0];
		CouplingParser parser = new CouplingParser(pathToSource);

		System.out.println("Which information do you want?"+
				"\n1. Coupling Weighted Graph between all classes." +
				"\n2. Hierarchical coupling cluster algorithm output." +
				"\n3. Partition algorithm output." +
				"\n4. To have all those informations with Spoon." +
				"\n0 To quit.");
		Scanner sc = new Scanner(System.in);
		int choice = sc.nextInt();
		sc.close();
		switch(choice) {
			case 1:
				System.out.println("Here is the coupling weighted graph between all classes from "+pathToSource);
				parser.makeCoupledWeightedGraph()
						.forEach(c -> System.out.println(c.getSource()+"<-["+c.getCpt()+"/"+c.getTotalNumberOfRelations()+"]->"+c.getTarget()));
				break;
			case 2:
				System.out.println("Here is the hierarchical coupling cluster process:");
				parser.makeHierarchicalCluster(parser.initializeClusters(), parser.makeCoupledWeightedGraph());
						//.forEach(c -> System.out.println("Final cluster: "+c));
				break;
			case 3:
				System.out.println("Here is the partitionnement process:");
				ArrayList<Cluster> partition = parser.makePartition();
				System.out.println("\nFinal partition:");
				partition.forEach(cluster -> System.out.println(cluster));
				break;
			case 4:
			    Spoon.main(args);
			case 0:
				return;
			default:
				System.out.println("Sorry, wrong input. Please try again.");
				break;		
		}
	}
}

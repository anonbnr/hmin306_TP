package couple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class Spoon {
    /*attributes*/
    private String projectPath;
    //*** Bachar's jrePath ***//
    // public static final String jrePath = "/usr/lib/jvm/java-11-oracle";
    //*** Amandine's jrePath ***//
    public static final String jrePath = "/usr/lib/jvm/java-8-openjdk-amd64/";

    /*constructors*/
    public Spoon(String projectPath) {
        this.projectPath = projectPath;
    }

    public CtModel getModel() {
        Launcher launcher = new Launcher();
        launcher.addInputResource(projectPath);
        launcher.buildModel();
        return launcher.getModel();
    }

    /**
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String pathToSource = "/home/amapai/workspaces/eclipse-workspace/HMIN306_Seriai/src";//args[0];
        Spoon spoon = new Spoon(pathToSource);

        // Q1
        System.out.println("Here is the coupling weighted graph between all classes from "+pathToSource);
        spoon.makeCoupledWeightedGraph()
        .forEach(c -> System.out.println(c.getSource()+"<-["+c.getCpt()+"/"+c.getTotalNumberOfRelations()+"]->"+c.getTarget()));

        // Q2
        System.out.println("Here is the hierarchical coupling cluster process:");
        spoon.makeHierarchicalCluster(spoon.initializeClusters(), spoon.makeCoupledWeightedGraph());
          //.forEach(c -> System.out.println("Final cluster: "+c));

        // Q3
        System.out.println("Here is the partitionnement process:");
        ArrayList<Cluster> partition = spoon.makePartition();

        System.out.println("\nFinal partition:");
        partition.forEach(cluster -> System.out.println(cluster));
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// Coupling related functions WITH SPOON ////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the number of call between class A and B.
     */
    public int getCouplingBetween(String callingClassName, String calledClassName) throws IOException { // Exo3
        CtModel model = getModel();
        
        int cpt =0;
        if(callingClassName.equals(calledClassName)) { // check A != B
            System.out.println("Can not compute coupling in same class");
            return 0; 
        }
        for (CtType<?> type : model.getAllTypes()) { // for each class
            if(callingClassName.equals(type.getQualifiedName())) { // A -> B
                for(CtMethod<?> method : type.getAllMethods()){ // in each methods from td class
                    for (CtInvocation<?> methodInvocation : Query.getElements(method, new TypeFilter<CtInvocation<?>>(CtInvocation.class))) { // look for a call in that method
                        if(methodInvocation.getTarget().getType() != null) {
                            if (calledClassName.equals(methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName())) {
                                cpt++;
                            }
                        }
                    }
                }
            } else if(calledClassName.equals(type.getQualifiedName())) { // B -> A
                for(CtMethod<?> method : type.getAllMethods()){ // in each methods from td class
                    for (CtInvocation<?> methodInvocation : Query.getElements(method, new TypeFilter<CtInvocation<?>>(CtInvocation.class))) { // look for a call in that method
                        if(methodInvocation.getTarget().getType() != null) {
                            if (callingClassName.equals(methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName())) {
                                cpt++;
                            }
                        }
                    }
                }
            }
        }
        return cpt;
    }

    /**
     * Draws a sort of "weighted-coupling graphs". Used in exo 3 TP3.
     */
    public ArrayList<Couple> makeCoupledWeightedGraph() throws IOException {
        CtModel model = getModel();
        int numberOfMethods = 0;
        ArrayList<Couple> couples = new ArrayList<Couple>();
        String source, target; 
        for (CtType<?> type : model.getAllTypes()) { // for each class A
            source = type.getQualifiedName();
            for(CtMethod<?> method : type.getAllMethods()){ // for each methods M in A
                for (CtInvocation<?> methodInvocation : Query.getElements(method, new TypeFilter<CtInvocation<?>>(CtInvocation.class))) { // for each method's invocation Mi from M
                    if(methodInvocation.getTarget().getType() != null) { ;
                        target = methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName();
                        if (!source.equals(target) ) { /* A != B */
                            numberOfMethods++;
                            if(Couple.isCoupleAlreadyInArray(couples, source, target)) { // we already know the couple
                                Couple.incrementCoupleCounter(couples, source, target);
                            } else { // we add this new couple in the array
                                Couple c = new Couple(source, target, 1, 0);
                                couples.add(c);
                            }
                        }
                    }
                }
            }
        }

        for(Couple c : couples) {
            c.setTotalNumberOfRelations(numberOfMethods);
            c.computeScore();
        }
        return couples;
    }

    /**
     * Used in the creation of the hierarchical cluster.
     * 
     * @return an ArrayList of Clusters where a cluster contains a class.
     * @throws IOException
     */
    public ArrayList<Cluster> initializeClusters() throws IOException{
        CtModel model = getModel();
        ArrayList<Cluster> clusters = new ArrayList<Cluster>();

        // read all java files to fetch classes.
        for (CtType<?> type : model.getAllTypes()) {
            boolean isAlreadyInCluster = false; // check that td is not already in clusters
            for(Cluster c : clusters) {
                if(c.getClasses().contains(type.getQualifiedName())) {
                    isAlreadyInCluster = true;
                }
            }
            if(!isAlreadyInCluster) {
                Cluster cls = new Cluster(type.getQualifiedName(), 0);
                clusters.add(cls);
            }
              
        }
        return clusters;
    }
    
    /**
     * Create the hierarchical cluster of a Java program based on coupling metric.
     * Can also be used as preprocessing to count all calls.
     * Used for question 2a from TP3.
     * 
     * @return a Stack @Cluster representing the hierarchical cluster.
     * @throws IOException
     */
    public Stack<Cluster> makeHierarchicalCluster(ArrayList<Cluster> clusters,
            ArrayList<Couple> couples) throws IOException {
        // variables
        Stack<Cluster> hierarchicalCluster = new Stack<Cluster>();
        Cluster sourceCluster, targetCluster, firstPart, secondPart, newCluster;
        int bestScore, firstIndex, secondIndex;

        // ouputs
        System.out.println("Original classes are put in clusters: ");
        clusters.forEach(c -> System.out.println(c));
        System.out.println("\nCreation of hierarchical clusters:");

        // while we don't have one final cluster
        while(clusters.size()>1) { 
            bestScore = 0;
            firstIndex = 0;
            secondIndex = 0;

            // look for the best clusters to fusionned
            for(int i = 0 ; i < clusters.size() ; i++) {
                sourceCluster = clusters.get(i);
                for(int j = 0 ; j < clusters.size() ; j++) {
                    targetCluster = clusters.get(j);
                    if(i != j) {
                        int coupleScore = sourceCluster.getScoreBetweenClusters(couples, targetCluster);
                        if(bestScore < coupleScore){
                            bestScore = coupleScore;
                            firstIndex = i;
                            secondIndex = j;
                        }
                    }                   
                }
            }

            // break condition if there is no more call
            if(bestScore == 0)
                break;

            // fusion best clusters
            firstPart = clusters.get(firstIndex);
            secondPart = clusters.get(secondIndex);
            newCluster = new Cluster(firstPart.getClasses(),
                    bestScore + firstPart.getCouplingScore() + secondPart.getCouplingScore());
            newCluster.addClasses(secondPart.getClasses());
            clusters.remove(firstPart);             // remove the
            clusters.remove(secondPart);            // composed clusters
            clusters.add(newCluster);
            hierarchicalCluster.push(newCluster);   // and push fusion

            // ouputs
            System.out.println("\nFusion of: "+firstPart + " and " + secondPart
                    + ", they have " + bestScore + " call(s).");
            System.out.println("Clusters: ");
            clusters.forEach(cluster -> System.out.println(cluster.toString()));
        }
        // outputs
        System.out.println("Process done.\n Final clusters:");
        clusters.forEach(cluster -> System.out.println(cluster.toString()));

        return hierarchicalCluster;
    }

    /**
     * Silent version of makeHierarchicalCluster(), used for question 2b from TP3.
     * Also store extra details in the stack to facilitate our work.
     */
    public Stack<Cluster> silentHierarchicalClusterMaker(ArrayList<Cluster> clusters, 
            ArrayList<Couple> couples) throws IOException {
        // variables
        Stack<Cluster> hierarchicalCluster = new Stack<Cluster>();
        Cluster sourceCluster, targetCluster, firstPart, secondPart, newCluster;
        int bestScore, firstIndex, secondIndex;

        // while we don't have one final cluster
        while(clusters.size()>1) { 
            bestScore = 0;
            firstIndex = 0;
            secondIndex = 0;

            // look for the best clusters to fusionned
            for(int i = 0 ; i < clusters.size() ; i++) {
                sourceCluster = clusters.get(i);
                for(int j = 0 ; j < clusters.size() ; j++) {
                    targetCluster = clusters.get(j);
                    if(i != j) {
                        int coupleScore = sourceCluster.getScoreBetweenClusters(couples, targetCluster);
                        if(bestScore < coupleScore){
                            bestScore = coupleScore;
                            firstIndex = i;
                            secondIndex = j;
                        }
                    }                   
                }
            }

            // break condition if there is no more call
            if(bestScore == 0)
                break;

            // fusion best clusters
            firstPart = clusters.get(firstIndex);
            secondPart = clusters.get(secondIndex);
            newCluster = new Cluster(firstPart.getClasses(),
                    bestScore + firstPart.getCouplingScore() + secondPart.getCouplingScore());
            newCluster.addClasses(secondPart.getClasses());
            clusters.remove(firstPart);             // remove the
            clusters.remove(secondPart);            // composed clusters
            clusters.add(newCluster);
            hierarchicalCluster.push(firstPart);    // and push fusion
            hierarchicalCluster.push(secondPart);   // and push fusion
            hierarchicalCluster.push(newCluster);   // and push fusion
        }       
        return hierarchicalCluster;
    }

    /**
     * Used in question 2b from TP3
     * Computes the partition of a program based on its hierarchical cluster.
     */
    public ArrayList<Cluster> makePartition() throws IOException {
        System.out.println("\nPartition construction");
        Stack<Cluster> stackOfCluster = silentHierarchicalClusterMaker(initializeClusters(), makeCoupledWeightedGraph());
        ArrayList<Cluster> partition = new ArrayList<Cluster>();
        while(!stackOfCluster.isEmpty()) {

            Cluster father = stackOfCluster.pop();
            Cluster secondSon = stackOfCluster.pop();
            Cluster firstSon = stackOfCluster.pop();
            System.out.println("\nFather weight: " + father.getCouplingScore());
            System.out.println("Average of sons weight: " + Math.ceil((firstSon.getCouplingScore()+secondSon.getCouplingScore())/2));
            if(father.getCouplingScore() > Math.ceil((firstSon.getCouplingScore()+secondSon.getCouplingScore())/2)){
                partition.add(father);
                System.out.println("We add father to partition.");
            } else {
                System.out.println("Weight inferior or equal, his sons are not part of the partition.");
            }
            System.out.println("Partition(s):");
            partition.forEach(p -> System.out.println(p));
        }
        return partition;
    }
  
}

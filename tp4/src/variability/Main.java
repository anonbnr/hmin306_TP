package variability;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Used for TP4.
 */
public class Main {

    /**
     * Main.
     * 
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        String pathToSourceV1 = args[0];
        String pathToSourceV2 = args[1];
        VariabilityParser parser = new VariabilityParser(pathToSourceV1, pathToSourceV2);

        System.out.println("Comparaisons will be on ArgoUML v0.32.2 and v0.34. " + 
                "Which information do you want?"+
                "\n1. Common and variant classes (same classes name)." +
                "\n2. Common and variant classes (different methods)." +
                "\n3. Common and variant classes (different heritancy)." +
                "\n4. Common and variant classes (different exceptions)." +
                "\n5. Different methods for a given class." +
                "\n0 To quit.");
        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();
        sc.close();

        System.out.println("Collecting information for ArgoUML v0.32.2.");
        ArrayList<ClassInfo> infoV1 = parser.getClassInfoFromFile(pathToSourceV1);
        System.out.println("Done.\nCollecting information for ArgoUML v0.34.");
        ArrayList<ClassInfo> infoV2 = parser.getClassInfoFromFile(pathToSourceV2);
        System.out.println("Done.");
        switch(choice) {
            case 1:
                parser.findDifferentClassesByName(infoV1, infoV2);
                break;
            case 2:
                parser.findDifferentClassesByMethods(infoV1, infoV2);
                break;
            case 3:
                parser.findDifferentClassesByHeritancy(infoV1, infoV2);
                break;
            case 4:
                parser.findDifferentClassesByException(infoV1, infoV2);
                break;
            case 5:
                ClassInfo c1 = selectClass(infoV1);
                ClassInfo c2 = selectClass(infoV2);
                if(c1!=null && c2!=null)
                    parser.findDifferentMethodsInCls(c1, c2);
                break;
            case 0:
                return;
            default:
                System.out.println("Sorry, wrong input. Please try again.");
                break;  
        }
    }
    
    /**
     * Allow user to select a class among others with its name. 
     * For now, compare methods from Main classes. 
     * TODO debug scan functions.
     *
     * @param cls array list of class info : informations on classes
     * @return the wanted class info.
     */
    public static ClassInfo selectClass(ArrayList<ClassInfo> cls) {
        //System.out.println("Select a class among:");
        cls.forEach(c->System.out.println(c.getName()));
        //Scanner sc = new Scanner(System.in);
        String choice = "Main"; //sc.next();
        //sc.close();
        for (ClassInfo c : cls) {
            if(c.getName().toString().equals(choice))
                return c;
        }
        //System.out.println("Sorry input not recognized.");
        return null;
    }
}

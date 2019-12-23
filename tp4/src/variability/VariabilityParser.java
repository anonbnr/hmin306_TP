package variability;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import visitors.TypeDeclarationVisitor;

public class VariabilityParser {

    /*attributes*/
    private String projectPathV1;
    private String projectPathV2;
    //*** Bachar's jrePath ***//
    // public static final String jrePath = "/usr/lib/jvm/java-11-oracle";
    //*** Amandine's jrePath ***//
    public static final String jrePath = "/usr/lib/jvm/java-8-openjdk-amd64/";
    
    /*constructors*/
    public VariabilityParser(String projectPathV1, String projectPathV2) {
        this.projectPathV1 = projectPathV1;
        this.projectPathV2 = projectPathV2;
    }
    
    /*methods*/
    /**
     * The project path getter
     * @return the project path
     */
    public String getProjectPathV1() {
        return this.projectPathV1;
    }
    public String getProjectPathV2() {
        return this.projectPathV2;
    }
    
    /**
     * Recursively returns the list of java files for a given folder
     * @param folder the folder whose files to list
     * @return the list of java files for the folder
     */
    public static ArrayList<File> listJavaFilesForProject(File folder) {
        ArrayList<File> javaFiles = new ArrayList<>();
        String fileName = "";
        
        for (File fileEntry: folder.listFiles()) {
            fileName = fileEntry.getName();
            
            if (fileEntry.isDirectory())
                javaFiles.addAll(listJavaFilesForProject(fileEntry));
            else if (fileName.endsWith(".java"))
                javaFiles.add(fileEntry);
        }

        return javaFiles;
    }
    
    /**
     * The compilation unit for the root node of the source's AST
     * @param source array of characters designating the source code to parse
     * @return the compilation unit for the root node of source's AST
     */
    public CompilationUnit parse(String projectVersion, char[] source) {
        ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
 
        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);
 
        parser.setUnitName("");
 
        String[] sources = { projectVersion }; 
        String[] classpath = {jrePath};
 
        parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
        parser.setSource(source);
        
        return (CompilationUnit) parser.createAST(null); // create and parse
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////// Variability related functions  //////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Collect once and for all information needed in reading a project's files.
     *
     * @return : an array list of ClassInfo, object representing our class data.
     * @throws IOException 
     */
    public ArrayList<ClassInfo> getClassInfoFromFile(String projectPathVersion) throws IOException{
        System.out.println(projectPathVersion);
        ArrayList<File> javaFiles = VariabilityParser.listJavaFilesForProject(new File(projectPathVersion));
        
        // return object
        ArrayList<ClassInfo> clsInfo = new ArrayList<ClassInfo>();
        
        // ClassInfo data we want to gather
        ClassInfo cInfo;
        String name, heritsFrom, methodName, methodContent;
        ArrayList<MethodInfo> methods;
        MethodInfo method;
        ArrayList<String> exceptions;
        
        // parse files
        for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry);
            CompilationUnit parse = parse(projectPathVersion, content.toCharArray());
            TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
            parse.accept(visitor);
            for (TypeDeclaration td : visitor.getTypes()) { // for each class A

                // get name
                name = td.getName().toString();
                
                // get heritancy
                if(td.getSuperclassType()!=null)
                    heritsFrom = td.getSuperclassType().toString();
                else 
                    heritsFrom="";
                
                // get exception(s)
                exceptions = new ArrayList<String>();
                
                // get methods
                methods = new ArrayList<MethodInfo>();
                for(MethodDeclaration m : td.getMethods()){ // for each methods M in A
                    methodName = m.getName().toString();
                    methodContent = m.getBody().toString();
                    method = new MethodInfo(methodName, methodContent);
                    methods.add(method);
                    
                    for (Object e : m.thrownExceptions()) {
                        if(!exceptions.contains(e.toString()))
                            exceptions.add(e.toString());
                    }                    
                }
                cInfo = new ClassInfo(name, methods, heritsFrom, exceptions);
                clsInfo.add(cInfo);
            }         
        }
        return clsInfo;
    }
    
    /**
     * For now, produce the output to question :
     * "Identify common and variant classes (two classes are 
     * different if its names are different) "
     * 
     * @param v1 array with data related to V1' software-to-compare.
     * @param v2 array with data related to V2' software-to-compare.
     */
    public void findDifferentClassesByName(ArrayList<ClassInfo> v1, ArrayList<ClassInfo> v2) {
        ArrayList<String> commonCls = new ArrayList<String>();
        
        for (ClassInfo cls1 : v1) {
            for (ClassInfo cls2 : v2) {
                if(cls1.hasSameNameThan(cls2) && !commonCls.contains(cls1.getName()))
                    commonCls.add(cls1.getName());
            }
        }
        clssPrinter("common", commonCls);
        clssPrinter("different", getDifferentClss(commonCls, v1, v2));
    }
    
    /**
     * For now, produce the output to question :
     * "Identify common and variant classes (two classes are 
     * different if its methods are different) "
     * 
     * @param v1 array with data related to V1' software-to-compare.
     * @param v2 array with data related to V2' software-to-compare.
     */
    public void findDifferentClassesByMethods(ArrayList<ClassInfo> v1, ArrayList<ClassInfo> v2) {
        ArrayList<String> commonCls = new ArrayList<String>();
        
        for (ClassInfo cls1 : v1) {
            for (ClassInfo cls2 : v2) {
                if(cls1.hasSameMethodsThan(cls2) && !commonCls.contains(cls1.getName()))
                    commonCls.add(cls1.getName());
            }
        }
        clssPrinter("common", commonCls);
        clssPrinter("different", getDifferentClss(commonCls, v1, v2));
    }
    
    /**
     * For now, produce the output to question :
     * "Identify common and variant classes (two classes are 
     * different if its heritancy are different) "
     * 
     * @param v1 array with data related to V1' software-to-compare.
     * @param v2 array with data related to V2' software-to-compare.
     */
    public void findDifferentClassesByHeritancy(ArrayList<ClassInfo> v1, ArrayList<ClassInfo> v2) {
        ArrayList<String> commonCls = new ArrayList<String>();
        
        for (ClassInfo cls1 : v1) {
            for (ClassInfo cls2 : v2) {
                if(cls1.hasSameHeritancyNameThan(cls2) && !commonCls.contains(cls1.getName()))
                    commonCls.add(cls1.getName());
            }
        }
        clssPrinter("common", commonCls);
        clssPrinter("different", getDifferentClss(commonCls, v1, v2));
    }
    
    /**
     * For now, produce the output to question :
     * "Identify common and variant classes (two classes are 
     * different if its exception(s) are different) "
     * 
     * @param v1 array with data related to V1' software-to-compare.
     * @param v2 array with data related to V2' software-to-compare.
     */
    public void findDifferentClassesByException(ArrayList<ClassInfo> v1, ArrayList<ClassInfo> v2) {
        ArrayList<String> commonCls = new ArrayList<String>();
        
        for (ClassInfo cls1 : v1) {
            for (ClassInfo cls2 : v2) {
                if(cls1.hasSameExceptionsThan(cls2) && !commonCls.contains(cls1.getName())) 
                    commonCls.add(cls1.getName());
            }
        }
        clssPrinter("common", commonCls);
        clssPrinter("different", getDifferentClss(commonCls, v1, v2));
    }
    
    /**
     * For now, produce the output to question :
     * "Identify different methods within a given class"
     * 
     * @param v1 array with data related to V1' software-to-compare.
     * @param v2 array with data related to V2' software-to-compare.
     */
    public void findDifferentMethodsInCls(ClassInfo cls1, ClassInfo cls2) {
        ArrayList<MethodInfo> differentMethods = new ArrayList<MethodInfo>();
        
        for(MethodInfo m1 : cls1.getMethods()) {
            for(MethodInfo m2 : cls2.getMethods()) {
                if(!m1.getContent().equals(m2.getContent()) && !differentMethods.contains(m1)) {
                    differentMethods.add(m1);
                }
            }  
        }
        methodsPrinter("different", differentMethods);
    }
    
    /**
     * Determines different classes between 2 software's version knowing common classes.
     * 
     * @param commonCls shared classes
     * @param v1 classes of v1' software
     * @param v2 classes of v2' software
     * @return
     */
    public ArrayList<String> getDifferentClss(ArrayList<String> commonCls, ArrayList<ClassInfo> v1,
            ArrayList<ClassInfo> v2) {
        ArrayList<String> differentCls = new ArrayList<String>();
        for(ClassInfo c : v1) {
            if(!commonCls.contains(c.getName()) && !differentCls.contains(c.getName()))
                differentCls.add(c.getName());
        }
        for(ClassInfo c : v2) {
            if(!commonCls.contains(c.getName()) && !differentCls.contains(c.getName()))
                differentCls.add(c.getName());
        }
        return differentCls;
    }
    
    /**
     * Basic function to nicely print our results after each algo.
     * 
     * @param clss : array of element to print
     */
    public void clssPrinter(String subject, ArrayList<String> clss) {
        System.out.println("\n\nFollowing are "+subject + " classes.");
        for (String cls : clss) {
            System.out.println(cls);
        }
        System.out.println();
    }
    
    /**
     * Basic function to nicely print our results after each algo.
     * 
     * @param mI : array of element to print
     */
    public void methodsPrinter(String subject, ArrayList<MethodInfo> mI) {
        System.out.println("\n\nFollowing are "+subject + " methods.");
        for (MethodInfo methodInfo : mI) {
            System.out.println(methodInfo.getName());
        }
        System.out.println();
    }
    
}

package variability;

import java.util.ArrayList;

public class ClassInfo {
    
    private String name;
    private ArrayList<MethodInfo> methods;
    private String heritsFrom;
    private ArrayList<String> exceptions;
    
    public ClassInfo(String name, ArrayList<MethodInfo> methods, String heritsFrom, ArrayList<String> exceptions) {
        super();
        this.name = name;
        this.methods = methods;
        this.heritsFrom = heritsFrom;
        this.exceptions = exceptions;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<MethodInfo> getMethods() {
        return methods;
    }
    public void setMethods(ArrayList<MethodInfo> methods) {
        this.methods = methods;
    }
    public String getHeritsFrom() {
        return heritsFrom;
    }
    public void setHeritsFrom(String heritsFrom) {
        this.heritsFrom = heritsFrom;
    }
    public ArrayList<String> getExceptions() {
        return exceptions;
    }
    public void setExceptions(ArrayList<String> exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    public String toString() {
        return "ClassInfo [name=" + name + ", methods=" + methods + ", heritsFrom=" + heritsFrom + ", exceptions="
                + exceptions + "]";
    }

    public boolean hasSameNameThan(ClassInfo cls2) {
        return this.getName().equals(cls2.getName());
    }

    public boolean hasSameMethodsThan(ClassInfo cls2) {
        ArrayList<String> m1 = new ArrayList<String>();
        ArrayList<String> m2 = new ArrayList<String>();
        for(MethodInfo m : this.getMethods()) {
            m1.add(m.getName());
        }
        for(MethodInfo m : cls2.getMethods()) {
            m2.add(m.getName());
        }
        // Change with equals() ?
        return (m1.containsAll(m2)) && (m2.containsAll(m1));
    }

    public boolean hasSameHeritancyNameThan(ClassInfo cls2) {
        return this.getHeritsFrom().equals(cls2.getHeritsFrom());
    }

    public boolean hasSameExceptionsThan(ClassInfo cls2) {
        // Change with equals() ?
        return (this.getExceptions().containsAll(cls2.getExceptions())) &&
                (cls2.getExceptions().containsAll(this.getExceptions())) ;
    }
}

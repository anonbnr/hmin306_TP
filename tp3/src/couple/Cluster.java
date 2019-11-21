package couple;

import java.util.ArrayList;

/**
 * Self made cluster.
 */
public class Cluster {

	private ArrayList<String> classes;
	private int couplingInCluster;
	
	public Cluster(ArrayList<String> classes, int couplingInCluster) {
		super();
		this.classes = new ArrayList<String>(classes);
		this.couplingInCluster = couplingInCluster;
	}

	public Cluster(Cluster other) {
		this.classes = new ArrayList<String>(other.getClasses());
		this.couplingInCluster = other.getCouplingValue();
	}

	public ArrayList<String> getClasses() {
		return classes;
	}

	public void setClasses(ArrayList<String> classes) {
		this.classes = classes;
	}

	public int getCouplingValue() {
		return couplingInCluster;
	}

	public void setCouplingInCluster(int couplingInCLuster) {
		this.couplingInCluster = couplingInCLuster;
	}
	
	public void addClasses(ArrayList<String> classesToAdd) {
		for(String classToAdd : classesToAdd) {
			if(!getClasses().contains(classToAdd)) {
				this.classes.add(classToAdd);
			}
		}
	}
	
	@Override
	public String toString() {
		return "Cluster [classes=" + classes + ", couplingInCluster=" + couplingInCluster + "]";
	}

	boolean isCoupledWith(Cluster other){
		for(String className : getClasses()) {
			if(other.getClasses().contains(className))
				return true;
		}
		return false;
	}
	
}
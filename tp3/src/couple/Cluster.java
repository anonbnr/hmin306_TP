package couple;

import java.util.ArrayList;

/**
 * Self made cluster.
 */
public class Cluster {

	private ArrayList<String> classes;
	private int couplingScore;
	
	public Cluster(String cls, int couplingInCluster) {
		super();
		ArrayList<String> array = new ArrayList<String>();
		array.add(cls);
		this.classes = array;
		this.couplingScore = couplingInCluster;
	}
	
	public Cluster(ArrayList<String> classes, int couplingInCluster) {
		super();
		this.classes = new ArrayList<String>(classes);
		this.couplingScore = couplingInCluster;
	}

	public Cluster(Cluster other) {
		this.classes = new ArrayList<String>(other.getClasses());
		this.couplingScore = other.getCouplingScore();
	}

	public ArrayList<String> getClasses() {
		return classes;
	}

	public void setClasses(ArrayList<String> classes) {
		this.classes = classes;
	}

	public int getCouplingScore() {
		return couplingScore;
	}

	public void setCouplingInCluster(int couplingInCLuster) {
		this.couplingScore = couplingInCLuster;
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
		return "Cluster [classes=" + classes + ", couplingScore=" + couplingScore + "]";
	}

	boolean isCoupledWith(Cluster other){
		for(String className : getClasses()) {
			if(other.getClasses().contains(className))
				return true;
		}
		return false;
	}

	public void setClass(String cls) {
		ArrayList<String> c = new ArrayList<String>();
		c.add(cls);
		this.classes = c;
	}
	
}
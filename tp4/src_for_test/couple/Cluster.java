package couple;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Self made cluster.
 */
public class Cluster {

	private ArrayList<String> classes;
	private ArrayList<Cluster> clustersInside;
    private int couplingScore;
	
	public Cluster(String cls, int couplingScore) {
		super();
		ArrayList<String> array = new ArrayList<String>();
		array.add(cls);
		this.classes = array;
		this.clustersInside = new ArrayList<Cluster>();
		this.couplingScore = couplingScore;
	}
	
	public Cluster(ArrayList<String> classes, int couplingInCluster) {
		super();
		this.classes = new ArrayList<String>(classes);
		this.couplingScore = couplingInCluster;
        this.clustersInside = new ArrayList<Cluster>();
	}

	public Cluster(Cluster other) {
		this.classes = new ArrayList<String>(other.getClasses());
		this.couplingScore = other.getCouplingScore();
        this.clustersInside = other.getClustersInside();
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

    public ArrayList<Cluster> getClustersInside() {
        return clustersInside;
    }

    public void setClustersInside(ArrayList<Cluster> clustersInside) {
        this.clustersInside = clustersInside;
    }

	public void setCouplingInCluster(int couplingInCLuster) {
		this.couplingScore = couplingInCLuster;
	}

    public void setClass(String cls) {
        ArrayList<String> c = new ArrayList<String>();
        c.add(cls);
        this.classes = c;
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
	
	 /**
     * Returns the call metric between two clusters.
     * 
     * @param target : target Cluster
     * @return the number of calls among all the classes from two clusters
     * @throws IOException
     */
    public int getScoreBetweenClusters(ArrayList<Couple> couples, Cluster target) throws IOException {
        int score = 0;
        for(String sourceCls : this.getClasses()) {
            for(String targetCls : target.getClasses()) {
                if(!sourceCls.equals(targetCls)) {
                    for(Couple c : couples) {
                        if(c.getSource().equals(sourceCls) && c.getTarget().equals(targetCls)) {
                            score += c.getCpt();
                        }
                    }
                }
            }
        }
        return score;
    }

    public void addClusters(Cluster firstPart, Cluster secondPart) {
        this.clustersInside.add(firstPart);
        this.clustersInside.add(secondPart);        
    }
	
}
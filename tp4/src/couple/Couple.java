package couple;

import java.util.ArrayList;

/**
 * Self-made couple. 
 */
public class Couple {

	String source;
	String target;
	int totalNumberOfRelations;
	int cpt;
	float score;
	
	public Couple(String classCalling, String classCalled, int cpt, float score) {
		super();
		this.source = classCalling;
		this.target = classCalled;
		this.cpt = cpt;
		this.score = score;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String classCalling) {
		this.source = classCalling;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String classCalled) {
		this.target = classCalled;
	}
	public int getTotalNumberOfRelations() {
		return totalNumberOfRelations;
	}
	public void setTotalNumberOfRelations(int totalNumberOfRelations) {
		this.totalNumberOfRelations = totalNumberOfRelations;
	}
	public int getCpt() {
		return cpt;
	}
	public void setCpt(int cpt) {
		this.cpt = cpt;
	}
	public double getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	
	public float computeScore() {
		this.score = this.cpt/this.totalNumberOfRelations;
		return this.score;
	}
	
	public void incrementCpt() {
		this.cpt++;
	}
		
	/*
	 * Look for a couple in an array by looking at class' names
	 * Remind : A <--> B is the same as B <-->
	 * Assume that callingClass == A and calledClass == B
	 */
	static boolean isCoupleAlreadyInArray(ArrayList<Couple> couples, String callingClass, String calledClass){
		for(Couple c : couples) {
			if( c.getSource().equals(callingClass)&&c.getTarget().equals(calledClass)  || /* A <--> B */
			  c.getSource().equals(calledClass)&&c.getTarget().equals(callingClass) ) { /* B <--> A */
					return true;
			}
		}
		return false;
	}
	
	/*
	 * increment the cpt of a couple in an Array by looking at class' names
	 * Remind : A <--> B is the same as B <-->
	 * Assume that callingClass == A and calledClass == B
	 */
	static void incrementCoupleCounter(ArrayList<Couple> couples, String callingClass, String calledClass){
		for(Couple c : couples) {
			if((c.getSource().equals(callingClass)&&c.getTarget().equals(calledClass))  || /* A <--> B */
			   (c.getSource().equals(calledClass)&&c.getTarget().equals(callingClass))) { // B <--> A
					c.incrementCpt();
			}
		}
	}
	
}

package to_string_generator.data;

public class Closed {
	private String closedAttr1;
	private String closedAttr2;
	
	public Closed(String closedAttr1, String closedAttr2) {
		this.closedAttr1 = closedAttr1;
		this.closedAttr2 = closedAttr2;
	}
	
	public void callClosedAttr1() {
		System.out.println("calling attribute closedAttr1 without getter:" + closedAttr1);
	}
	
	public void callClosedAttr2() {
		System.out.println("calling attribute closedAttr2 without getter:" + closedAttr2);
	}
	
	public void callAllClosedAttr() {
		System.out.println("calling all closed attributes without getters:");
		callClosedAttr1();
		callClosedAttr2();
	}
}

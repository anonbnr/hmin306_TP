package to_string_generator.data;

public class HasSomeGettersButNoToString {
	private String someAttr1;
	private String someAttr2;
	private String closedAttr;
	
	public HasSomeGettersButNoToString(String someAttr1, String someAttr2, String closedAttr) {
		this.someAttr1 = someAttr1;
		this.someAttr2 = someAttr2;
		this.closedAttr = closedAttr;
	}

	public String getSomeAttr1() {
		return someAttr1;
	}

	public void setSomeAttr1(String someAttr1) {
		this.someAttr1 = someAttr1;
	}
	
	public String getSomeAttr2() {
		return someAttr2;
	}

	public void setSomeAttr2(String someAttr2) {
		this.someAttr2 = someAttr2;
	}
	
	public void callClosedAttr() {
		System.out.println("calling closed attribute without getter:" + closedAttr);
	}
	
	public void callSomeGetters() {
		System.out.println("calling some attributes with getters:");
		System.out.println("calling someAttr1 with getter:" + getSomeAttr1());
		System.out.println("calling someAttr2 with getter:" + getSomeAttr2());
	}
}

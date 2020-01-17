package to_string_generator.data;

public class HasGettersButNoToString {
	private String someAttr1;
	private String someAttr2;
	
	public HasGettersButNoToString(String someAttr1, String someAttr2) {
		this.someAttr1 = someAttr1;
		this.someAttr2 = someAttr2;
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
	
	public void callAllGetters() {
		System.out.println("called all attributes with getters:");
		System.out.println("calling someAttr1 with getter:" + getSomeAttr1());
		System.out.println("calling someAttr2 with getter:" + getSomeAttr2());
	}
}

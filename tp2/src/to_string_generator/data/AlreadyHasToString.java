package to_string_generator.data;

public class AlreadyHasToString {
	private String someAttr1;
	private String someAttr2;
	
	public AlreadyHasToString(String someAttr1, String someAttr2) {
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
	
	public void callGetterAttr1() {
		System.out.println("calling someAttr1 with getter:" + getSomeAttr1());
	}
	
	public void callGetterAttr2() {
		System.out.println("calling someAttr2 with getter:" + getSomeAttr2());
	}
	
	public void callAllGetters() {
		System.out.println("calling all attributes with getters:");
		System.out.println("calling someAttr1 with getter:" + getSomeAttr1());
		System.out.println("calling someAttr2 with getter:" + getSomeAttr2());
	}
	
	@Override
	public String toString() {
		return "["+getSomeAttr1()+", "+getSomeAttr2()+"]";
	}
}

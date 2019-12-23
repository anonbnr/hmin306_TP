package test;

class AA {
	
	public BB atta1;
	public CC atta2;
	public int atta3;
	
	public void ma1() {
		ma2();
	}
	
	public void ma2() {atta2.mc1();}
	public void ma3() {atta1.mb2();}
}

class BB {

	public AA attb1;
	public CC attb2;
	
	public void mb1() {mb2();}
	
	public void mb2() {
		attb2.mc2();
		attb1.ma1();
	}
	
	public void mb3() {attb1.ma3();}
}

class CC {

	public void mc1() {
		BB b = new BB();
		b.mb1();
	}

	public void mc2() {
		System.out.println("fin");
	}
}

public class Test {

}

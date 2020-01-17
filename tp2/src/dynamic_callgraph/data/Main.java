package dynamic_callgraph.data;

import java.util.Random;

public class Main {

	public static void main(String[] args) {
		Random generator = new Random();
		
		Person john = new Person("John", "Doe", generator.nextInt(25) + 10);
		Person scott = new Person("Scott", "Skittles", generator.nextInt(25) + 10);
		Person jane = new Person("Jane", "Doe", generator.nextInt(25) + 10);
		
		House house = new House(120, 300, 5, 
				new Address(3, "Grayscale", "", "New York", 665599));
		House shack = new House(90, 180, 3, 
				new Address(10, "Gloomy", "", "New Jersey", 778855));
		House villa = new House(250, 1000, 12, 
				new Address(55, "Glamor", "", "Miami", 441122));
		House castle = new House(400, 1500, 20, 
				new Address(3, "Bourgeoisie", "", "Vérsailles", 40383));
		
		if (john.canOwnHouse()) {
			john.addHouse(shack);
			john.addHouse(castle);
		}
		
		System.out.println(john);
		
		if (scott.canOwnHouse()) {
			scott.addHouse(house);
			scott.addHouse(villa);
		}
		
		System.out.println(scott);
		
		if (!jane.canOwnHouse()) {
			jane.setAge(21);
			
			if (scott.hasHouse(villa.getAddress()))
				scott.transferOwnership(villa, jane);
			
			else
				jane.addHouse(villa);
			
			if (john.hasHouse(castle.getAddress()))
				john.transferOwnership(castle, jane);
			else
				jane.addHouse(castle);
		}
		
		System.out.println(jane);
		
		jane.transferOwnership(villa, john);
		scott.transferOwnership(house, john);
		john.transferOwnership(shack, scott);
		
		System.out.println(john);
		System.out.println(scott);
		System.out.println(jane);
	}
}

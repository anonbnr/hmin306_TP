package dynamic_callgraph.data;

import java.util.ArrayList;
import java.util.List;

public class Person {
	private String firstName;
	private String lastName;
	private int age;
	private List<House> houses;
	
	public Person(String firstName, String lastName, int age) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.houses = new ArrayList<>();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFullName() {
		return firstName + " " + lastName.toUpperCase();
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public boolean canOwnHouse() {
		return age >= 21;
	}
	
	public boolean hasHouse(House house) {
		return houses
				.stream()
				.anyMatch(ownedHouse -> ownedHouse.equals(house));
	}
	
	public boolean hasHouse(Address address) {
		return houses
				.stream()
				.anyMatch(ownedHouse -> ownedHouse.getAddress().equals(address));
	}
	
	public void addHouse(House house) {
		if (canOwnHouse() && !hasHouse(house))
			this.houses.add(house);
	}
	
	public House removeHouse(House house) {
		if (hasHouse(house)) {
			houses.remove(house);
			return house;
		}
		
		return null;
	}
	
	public House getHouse(Address address) {
		return houses
				.stream()
				.filter(house -> house.getAddress().equals(address))
				.findFirst().get();
	}
	
	public List<House> getHouses() {
		return this.houses;
	}
	
	public boolean transferOwnership(House house, Person p) {
		if (hasHouse(house)) {
			
			 if (p.canOwnHouse()) {
				 p.addHouse(house);
				 this.removeHouse(house);
					
				 System.out.println(this.getFirstName() + " transfered to " 
						 + p.getFullName() + " the following house: \n");
				 System.out.println(house);
					
				 return true;
			 }
			 
			 else {
				 System.out.println(p.getFullName() + " (" + p.getAge() + " years) cannot own houses");
				 return false;
			 }
		}
		
		else
			System.out.println(this.getFullName() + " doesn't own the following house: \n" + house);
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(firstName + " " + lastName.toUpperCase());
		buf.append(" (" + age + " years) ");
		buf.append("owns the following houses:\n");
		
		for (int i=0; i<houses.size(); i++) {
			buf.append("House " + (i + 1));
			buf.append("\n==============\n");
			buf.append(houses.get(i) + "\n\n");
		}
		
		return buf.toString();
	}
}

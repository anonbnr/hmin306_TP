package dynamic_callgraph.data;

public class House {
	/* ATTRIBUTES */
	private double size;
	private double rent;
	private int nbOfRooms;
	private Address address;
	
	/* CONSTRUCTOR */
	public House(double size, double rent, int nbOfRooms, Address address) {
		this.size = size;
		this.rent = rent;
		this.nbOfRooms = nbOfRooms;
		this.address = new Address(address);
	}
	
	/* METHODS */
	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public double getRent() {
		return rent;
	}

	public void setRent(double rent) {
		this.rent = rent;
	}

	public int getNbOfRooms() {
		return nbOfRooms;
	}

	public void setNbOfRooms(int nbOfRooms) {
		this.nbOfRooms = nbOfRooms;
	}
	
	public Address getAddress() {
		return this.address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("Size: " + size + "\n");
		buf.append("Number of Rooms: " + nbOfRooms + "\n");
		buf.append("Rent: " + rent + " euros\n");
		buf.append("Address: " + address);
		
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		House other = (House) obj;
		
		return address.equals(other.address);
	}
}

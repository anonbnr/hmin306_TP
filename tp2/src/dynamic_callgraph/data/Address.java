package dynamic_callgraph.data;

public class Address {
	private int roadNumber;
	private String roadName;
	private String details;
	private String city;
	private int zipCode;
	
	public Address(int roadNumber, String roadName, String details, String city, int zipCode) {
		this.setRoadNumber(roadNumber);
		this.setRoadName(roadName);
		this.setDetails(details);
		this.setCity(city);
		this.setZipCode(zipCode);
	}
	
	public Address(Address address) {
		this.setRoadNumber(address.getRoadNumber());
		this.setRoadName(address.getRoadName());
		this.setDetails(address.getDetails());
		this.setCity(address.getCity());
		this.setZipCode(address.getZipCode());
	}

	public int getRoadNumber() {
		return roadNumber;
	}

	public void setRoadNumber(int roadNumber) {
		this.roadNumber = roadNumber;
	}

	public String getRoadName() {
		return roadName;
	}

	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getZipCode() {
		return zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}

	@Override
	public String toString() {
		return this.getRoadNumber() + " " + this.getRoadName() + 
				" street" + 
				((details != "")? " (" + this.getDetails() + "), " : ", ")
				+ this.getZipCode() + ", " + this.getCity();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Address other = (Address) obj;
		
		if (city == null) {
			if (other.city != null)
				return false;
		} 
		else if (!city.equals(other.city))
			return false;
		
		if (details == null) {
			if (other.details != null)
				return false;
		} 
		else if (!details.equals(other.details))
			return false;
		
		if (roadName == null) {
			if (other.roadName != null)
				return false;
		}
		else if (!roadName.equals(other.roadName))
			return false;
		
		if (roadNumber != other.roadNumber)
			return false;
		
		if (zipCode != other.zipCode)
			return false;
		
		return true;
	}
}

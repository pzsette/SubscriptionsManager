package it.pietro.subscriptionsmanager.model;

import java.util.Objects;

public class Subscription {
	private String id;
	private String name;
	private Float price;
	private String repetition;
	
	public Subscription(String id, String name, Float price, String repetition) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.repetition = repetition;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getRepetition() {
		return repetition;
	}

	public void setRepetition(String repetition) {
		this.repetition = repetition;
	}
	
	@Override
	public String toString() {
		return "Subscription [id= "+id+", name= "+name+", price= "+price+", reptition= "+repetition+"]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		Subscription other = (Subscription) o;
		return Objects.equals(id, other.id) && Objects.equals(name, other.name) && Objects.equals(price, other.price) && Objects.equals(repetition, other.repetition);
	}

}

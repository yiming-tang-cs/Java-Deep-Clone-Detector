package p;

import java.util.ArrayList;
import java.util.List;

public class A {

	public class Order {

		private long number;

		public Order() {
		}

		/**
		 * Copy constructor
		 */
		public Order(Order source) {
			number = source.number;
		}
	}

	private String name;
	private List<Order> orders = new ArrayList<Order>();

	public A() {
	}

	/**
	 * Copy constructor
	 */
	public A(A source) {
		name = source.name;
		for (Order sourceOrder : source.orders) {
			orders.add(new Order(sourceOrder));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
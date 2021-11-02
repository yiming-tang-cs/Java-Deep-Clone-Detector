package p;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class A {

	public class Order implements Serializable {

		private static final long serialVersionUID = 1L;
		private String orderNumber;
		private double orderAmount;
		private String orderStatus;

		public Order(String orderNumber, double orderAmount, String orderStatus) {
			this.orderNumber = orderNumber;
			this.orderAmount = orderAmount;
			this.orderStatus = orderStatus;
		}

		public String getOrderNumber() {
			return this.orderNumber;
		}

		public double getOrderAmount() {
			return this.orderAmount;
		}

		public String getOrderStatus() {
			return this.orderStatus;
		}

		public void setOrderStatus(String orderStatus) {
			this.orderStatus = orderStatus;
		}

	}

	public class Customer implements Serializable {

		private static final long serialVersionUID = 1L;
		private String firstName;
		private String lastName;
		private Order order;

		public Customer(String firstName, String lastName, Order order) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.order = order;
		}

		public String getFirstName() {
			return this.firstName;
		}

		public String getLastName() {
			return this.lastName;
		}

		public Order getOrder() {
			return this.order;
		}
	}

	public A() {

		Order order = new Order("12345", 100.45, "In Progress");
		Customer customer = new Customer("Test", "CUstomer", order);

		Customer cloneCustomer = deepClone(customer);
		order.setOrderStatus("Shipped");
		System.out.println(cloneCustomer.getOrder().getOrderStatus());

	}

	@SuppressWarnings("unchecked")
	public static <T> T deepClone(T object) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			ObjectInputStream objectInputStream = new ObjectInputStream(bais);
			return (T) objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
package p;

import java.io.Serializable;

import org.apache.commons.lang.SerializationUtils;

/**
 * This class requires an external library. It cannot currently be tested
 * automatically.
 * 
 * @author yimingtang
 *
 */
public class A {

	class User implements Serializable {

		private static final long serialVersionUID = 1L;
		private String firstName;
		private String lastName;

		public User(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String getFirstName() {
			return this.firstName;
		}

		public String getLatsName() {
			return this.lastName;
		}

	}

	public A() {
		User pm = new User("Prime", "Minister");
		User deepCopy = (User) SerializationUtils.clone(pm);
		System.out.println("Copied a new user: " + deepCopy.firstName + " " + deepCopy.lastName);
	}

}
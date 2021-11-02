package p;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * This class requires an external library. It cannot currently be tested
 * automatically.
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
	    Gson gson = new Gson();
	    User deepCopy = gson.fromJson(gson.toJson(pm), User.class);
		System.out.println("Copied a new user: " + deepCopy.firstName + " " + deepCopy.lastName);
	}

}
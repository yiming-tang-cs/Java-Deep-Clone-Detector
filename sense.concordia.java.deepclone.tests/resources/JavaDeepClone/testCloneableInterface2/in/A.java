package p;

public class A {

	public class Department {
		private int id;
		private String name;

		public Department(int id, String name) {
			this.id = id;
			this.name = name;
		}

		// Getters and Setters
		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		// Defined clone method in Department class.
		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
	}

	public class Employee implements Cloneable {

		private Department department;

		public Employee(int id, String name, Department dept) {
			this.department = dept;
		}

		// Modified clone() method in Employee class
		@Override
		protected Object clone() throws CloneNotSupportedException {
			Employee cloned = (Employee) super.clone();
			cloned.setDepartment((Department) cloned.getDepartment().clone());
			return cloned;
		}

		public void setDepartment(Department department) {
			this.department = department;
		}

		public Department getDepartment() {
			return this.department;
		}

		// Getters and Setters
	}

	public A() throws CloneNotSupportedException {
		Department hr = new Department(1, "Human Resource");

		Employee original = new Employee(1, "Admin", hr);
		Employee cloned = (Employee) original.clone();

		// Let change the department name in cloned object and we will verify in
		// original object
		cloned.getDepartment().setName("Finance");

		System.out.println(original.getDepartment().getName());
		System.out.println(cloned.getDepartment().getName());
	}

}
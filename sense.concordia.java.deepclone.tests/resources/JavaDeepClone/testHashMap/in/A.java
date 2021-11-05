package p;

import java.util.HashMap;

public class A {

	private HashMap<String, String> students = new HashMap<>();

	public HashMap<String, String> getStudents() {
		return students;
	}

	public void setStudents(HashMap<String, String> students) {
		this.students = students;
	}

	public A() {

		students.put("111", "Alice");
		students.put("222", "Bob");
		HashMap<String, String> graders = new HashMap<>(students);
		if (!graders.isEmpty())
			System.out.println(graders.size());
	}

}
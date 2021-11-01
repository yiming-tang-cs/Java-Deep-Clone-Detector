package p;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class A {

	public class ObjectCloner {
		// so that nobody can accidentally create an ObjectCloner object
		private ObjectCloner() {
		}

		// returns a deep copy of an object
		public Object deepCopy(Object oldObj) throws Exception {
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
				oos = new ObjectOutputStream(bos); // B
				// serialize and pass the object
				oos.writeObject(oldObj); // C
				oos.flush(); // D
				ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray()); // E
				ois = new ObjectInputStream(bin); // F
				// return the new object
				return ois.readObject(); // G
			} catch (Exception e) {
				System.out.println("Exception in ObjectCloner = " + e);
				throw (e);
			} finally {
				oos.close();
				ois.close();
			}
		}

	}

	public A() {

		try {
			// create original object
			Vector<Point> v1 = new Vector<Point>();
			Point p1 = new Point(1, 1);
			v1.addElement(p1);
			// see what it is
			System.out.println("Original = " + v1);
			Vector<?> vNew = null;

			ObjectCloner objectCloner = new ObjectCloner();
			// deep copy
			vNew = (Vector<?>) (objectCloner.deepCopy(v1)); // A

			// verify it is the same
			System.out.println("New      = " + vNew);
			// change the original object's contents
			p1.x = 2;
			p1.y = 2;
			// see what is in each one now
			System.out.println("Original = " + v1);
			System.out.println("New      = " + vNew);
		} catch (Exception e) {
			System.out.println("Exception in main = " + e);
		}
	}

}
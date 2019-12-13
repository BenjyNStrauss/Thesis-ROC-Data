package bio;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import chem.Atom;

/**
 * A (slightly) specialized hashtable for atoms
 * @author Benjy Strauss
 *
 */

public class AtomTable extends Hashtable<String, Atom> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Adds an atom in the table
	 * @param atom: the atom to add
	 */
	public void addAtom(Atom atom) { put(atom.name(), atom); }
	
	/** See addAtom() */
	public void put(Atom atom) { put(atom.name(), atom); }
	
	public Atom get(String key) { return super.get(key); }
	
	public Atom remove(String key) { return super.remove(key); }
	
	/**
	 * Make a deep copy of the AtomTable and everything in it
	 */
	public AtomTable clone() {
		AtomTable myClone = new AtomTable();
		Set<String> keys = keySet();
		
		for(String key: keys) {
			Atom atom = get(key);
			myClone.addAtom(atom.clone());
		}
		
		return myClone;
	}
	
	/**
	 * 'qp' stands for quick-print
	 * @param arg0: the object to print
	 */
	protected static void qp(Object arg0) {
		if(arg0 != null && arg0.getClass().isArray()) {
			Object[] i_arg0 = (Object[]) arg0;
			for(Object o: i_arg0) {
				System.out.println(o);
			}
		} else if(arg0 instanceof List) {
			List<?> l_arg0 = (List<?>) arg0;
			for(Object o: l_arg0) {
				System.out.println(o);
			}
		} else {
			System.out.println(arg0);
		}
	}
}

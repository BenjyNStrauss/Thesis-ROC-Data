package util;

import java.io.Serializable;

/**
 * Represents a coordinate in 3-space
 * (copied from Trigamon Mithril, and then modified)
 * @author Benjy Strauss
 *
 */

public class Coordinate implements Serializable, Deconstructable, Comparable<Coordinate> {
	private static final long serialVersionUID = 1L;
	
	private enum Axis { X, Y, Z };
	
	private static Axis compare_axis = Axis.X;
	
	//it's important for these fields to be public
	public double x = 0;
	public double y = 0;
	public double z = 0;
	
	public static void setCompareX() { compare_axis = Axis.X; }
	public static void setCompareY() { compare_axis = Axis.Y; }
	public static void setCompareZ() { compare_axis = Axis.Z; }
	
	/**
	 * Default Constructor
	 */
	public Coordinate() { }
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public Coordinate(double x, double y) { this.x = x; this.y = y; }
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Coordinate(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
	
	/**
	 * Determines if two coordinates are equal
	 * @param other
	 * @return
	 */
	public boolean equals(Coordinate other) {
		if(x == other.x && y == other.y && z == other.z) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 */
	public Coordinate clone() { return new Coordinate(x,y,z); }
	
	@Override
	public int compareTo(Coordinate c) {
		switch(compare_axis) {
		case X:
			if(x < c.x) {
				return -1;
			} else if(x > c.x) {
				return 1;
			} else {return 0; }
		case Y:
			if(y < c.y) {
				return -1;
			} else if(y > c.y) {
				return 1;
			} else {return 0; }
		case Z:
			if(z < c.z) {
				return -1;
			} else if(z > c.z) {
				return 1;
			} else {return 0; }
		default:
			return 0;
		}
	}
	
	/**
	 * 
	 */
	public String toString() { return "(" + x + "," + y + "," + z +")"; }

	@Override
	public void deconstruct() {
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}	
	}
}


package chem;

import util.Coordinate;

/**
 * Represents an atom(s) that is part of an amino acid
 * @author Benjy Strauss
 *
 */

public class Atom extends ChemObject implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	private String symbol;
	private String name;
	private Coordinate position;
	private double charge = Double.NaN;
	private double occupancy;
	private double tempFactor;
	private int serialNo;
	
	/**
	 * Construct a new Atom
	 * @param symbol: The Atom's atomic symbol (EX: "Se" for "Selenium")
	 * @param name: The Atom's name (this is not necessarily the element name)
	 * 		Example: "Nitrogen" or "Alpha-Carbon"
	 */
	public Atom(String symbol, String name) {
		this(symbol, name, new Coordinate(), 0, 0, 0);
	}
	
	/**
	 * Construct a new Atom
	 * @param symbol: The Atom's atomic symbol (EX: "Se" for "Selenium")
	 * @param name: The Atom's name (this is not necessarily the element name)
	 *  		Example: "Nitrogen" or "Alpha-Carbon"
	 * @param x
	 * @param y
	 * @param z
	 * @param charge
	 * @param occupancy
	 * @param tempFactor
	 */
	public Atom(String symbol, String name, double x, double y, double z, double charge, double occupancy, double tempFactor) {
		this(symbol, name, new Coordinate(x,y,z), charge, occupancy, tempFactor);
	}
	
	/**
	 * Construct a new Atom
	 * @param symbol
	 * @param name
	 * @param position
	 * @param charge
	 * @param occupancy
	 * @param tempFactor
	 */
	public Atom(String symbol, String name, Coordinate position, double charge, double occupancy, double tempFactor) {
		this.symbol = symbol;
		this.name = name;
		this.position = position;
		this.charge = charge;
		this.occupancy = occupancy;
		this.tempFactor = tempFactor;
	}
	
	/** @param name: what to set the atom's name to */
	public void setName(String name) { this.name = name; }
	/** @param symbol: what to set the atom's symbol to */
	public void setSymbol(String symbol) { this.symbol = symbol; }
	/** @param position: what to set the atom's position to */
	public void setPos(Coordinate position) { this.position = position; }
	/** @param charge: what to set the atom's charge to */
	public void setCharge(double charge) { this.charge = charge; }
	public void setOccupancy(double occupancy) { this.occupancy = occupancy; }
	public void setTempFactor(double tempFactor) { this.tempFactor = tempFactor; }
	public void setSerialNo(int serialNo) { this.serialNo = serialNo; }	
	
	public String name() { return name; }
	public String symbol() { return symbol; }
	public Coordinate position() { return position; }
	public double charge() { return charge; }
	public double occupancy() { return occupancy; }
	public double tempFactor() { return tempFactor; }
	public int serialNo() { return serialNo; }
	
	@Override
	public String impliedFileName() {
		return name;
	}
	
	/** @return PDB specified X-coordinate, if known */
	public double getX() {
		if(position == null) {
			return Double.NaN;
		} else {
			return position.x;
		}
	}
	
	/** @return PDB specified Y-coordinate, if known */
	public double getY() {
		if(position == null) {
			return Double.NaN;
		} else {
			return position.y;
		}
	}
	
	/** @return PDB specified Z-coordinate, if known */
	public double getZ() {
		if(position == null) {
			return Double.NaN;
		} else {
			return position.z;
		}
	}
	
	/**
	 * Make a deep copy of the atom
	 * @return
	 */
	public Atom clone() {
		Atom myClone = new Atom(symbol, name);
		myClone.position = position.clone();
		myClone.charge = charge;
		myClone.occupancy = occupancy;
		myClone.tempFactor = tempFactor;
		return myClone;
	}
	
	/**
	 * 
	 */
	public void debugPrintAll() {
		qp("symbol    : "+ symbol);
		qp("name      : "+ name);
		qp("position  : "+ position);
		qp("charge    : "+ charge);
		qp("occupancy : "+ occupancy);
		qp("tempFactor: "+ tempFactor);
		qp("serialNo  : "+ serialNo);
	}
	
	/**
	 * 
	 */
	public String toString() { return ("Atom: " + name + " (" + symbol + ")"); }
}

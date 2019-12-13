package bio.tools.blast;

import java.util.*;

import bio.tools.blast.assist.Logger;
import bio.tools.blast.assist.LoggerFactory;


/**
 *
 * @author Andy Yates
 *
 * @param <C> The compound this set will contain
 */
public abstract class AbstractCompoundSet<C extends Compound> implements CompoundSet<C> {

	private final static Logger logger = LoggerFactory.getLogger(AbstractCompoundSet.class);

	private Map<CharSequence, C> charSeqToCompound = new HashMap<CharSequence, C>();
	private int maxCompoundCharSequenceLength = -1;
	private Boolean compoundStringLengthEqual = null;

	Map<C,Set<C>> equivalentsMap = new HashMap<C, Set<C>>();

	protected void addCompound(C compound, C lowerCasedCompound, Iterable<C> equivalents) {
		addCompound(compound);
		addCompound(lowerCasedCompound);

		addEquivalent(compound, lowerCasedCompound);
		addEquivalent(lowerCasedCompound, compound);

		for(C equivalent: equivalents) {
			addEquivalent(compound, equivalent);
			addEquivalent(equivalent, compound);
			addEquivalent(lowerCasedCompound, equivalent);
			addEquivalent(equivalent, lowerCasedCompound);
		}
	}

	@SuppressWarnings("unchecked")
	protected void addCompound(C compound, C lowerCasedCompound, C... equivalents) {
		List<C> equiv = new ArrayList<C>(equivalents.length);
		equiv.addAll(Arrays.asList(equivalents));
		addCompound(compound, lowerCasedCompound, equiv);
	}

	protected void addEquivalent(C compound, C equivalent) {
	 Set<C> s = equivalentsMap.get(compound);
	 if ( s == null){
		 s = new HashSet<C>();
		 equivalentsMap.put(compound, s);
	 }

		s.add( equivalent);
	}

	protected void addCompound(C compound) {
		charSeqToCompound.put(compound.toString(), compound);
		maxCompoundCharSequenceLength = -1;
		compoundStringLengthEqual = null;
	}

	@Override
public String getStringForCompound(C compound) {
		return compound.toString();
	}

	@Override
public C getCompoundForString(String string) {
		if(string == null) {
			throw new IllegalArgumentException("Given a null CharSequence to process");
		}

		if (string.length()==0) {
			return null;
		}

		if (string.length() > getMaxSingleCompoundStringLength()) {
			throw new IllegalArgumentException("CharSequence supplied is too long.");
		}

		return charSeqToCompound.get(string);
	}

	@Override
public int getMaxSingleCompoundStringLength() {
		if(maxCompoundCharSequenceLength == -1) {
			for(C compound: charSeqToCompound.values()) {
				int size = getStringForCompound(compound).length();
				if(size > maxCompoundCharSequenceLength) {
					maxCompoundCharSequenceLength = size;
				}
			}
		}
		return maxCompoundCharSequenceLength;
	}

		@Override
		public boolean isCompoundStringLengthEqual() {
				if(compoundStringLengthEqual == null) {
						int lastSize = -1;
						compoundStringLengthEqual = Boolean.TRUE;
						for(CharSequence c: charSeqToCompound.keySet()) {
								if(lastSize != c.length()) {
										compoundStringLengthEqual = Boolean.FALSE;
										break;
								}
						}
				}
				return compoundStringLengthEqual;
		}

	@Override
public boolean hasCompound(C compound) {
		C retrievedCompound = getCompoundForString(compound.toString());
		return retrievedCompound != null;
	}

	@Override
public boolean compoundsEquivalent(C compoundOne, C compoundTwo) {
		assertCompound(compoundOne);
		assertCompound(compoundTwo);
		return compoundOne.equals(compoundTwo) || equivalentsMap.get(compoundOne).contains(compoundTwo);
	}

	@Override
public Set<C> getEquivalentCompounds(C compound) {
		return equivalentsMap.get(compound);
	}

	public boolean compoundsEqual(C compoundOne, C compoundTwo) {
		assertCompound(compoundOne);
		assertCompound(compoundTwo);
		return compoundOne.equalsIgnoreCase(compoundTwo);
	}

		@Override
		public boolean isValidSequence(Sequence<C> sequence) {
				for (C compound: sequence) {
						if (!hasCompound(compound)) {
								return false;
						}
				}
				return true;
		}



	@Override
public List<C> getAllCompounds() {
		return new ArrayList<C>(charSeqToCompound.values());
	}

	private void assertCompound(C compound) {
		if (!hasCompound(compound)) {
			// TODO this used to throw an error, now only warning, is this the best solution?
				// dmyersturnbull: I think throwing a CompoundNotFoundException is far better
			logger.warn("The CompoundSet {} knows nothing about the compound {}", getClass().getSimpleName(), compound);
			//throw new CompoundNotFoundError("The CompoundSet "+
			//    getClass().getSimpleName()+" knows nothing about the compound "+
			//    compound);
		}
	}

		@Override
		public boolean isComplementable() {
				return false;
		}

		@Override
		public int hashCode() {
				int s = Hashcoder.SEED;
				s = Hashcoder.hash(s, charSeqToCompound);
				s = Hashcoder.hash(s, equivalentsMap);
				return s;
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
				if (! (o instanceof AbstractCompoundSet)) return false;
				if(Equals.classEqual(this, o)) {
						AbstractCompoundSet<C> that = (AbstractCompoundSet<C>)o;
						return  Equals.equal(charSeqToCompound, that.charSeqToCompound) &&
										Equals.equal(equivalentsMap, that.equivalentsMap);
				}
				return false;
		}


}


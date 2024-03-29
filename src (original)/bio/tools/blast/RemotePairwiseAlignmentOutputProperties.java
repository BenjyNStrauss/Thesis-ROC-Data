package bio.tools.blast;

/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 */

import java.io.Serializable;
import java.util.Set;

/**
 * RemotePairwiseAlignmentOutputProperties: the simplest representation of an object capable of holding
 * output formatting informations to be fed to a RemotePairwiseAlignmentService-implemented object.
 *
 * @author Sylvain Foisy, Diploide BioIT
 * @since Biojava 3
 *
 *
 */
public interface RemotePairwiseAlignmentOutputProperties extends Serializable {
	/**
	 *
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * Method that returns the value associated with the key given in parameter.
	 *
	 * @param key :a String with the required key for this map.
	 * @return a String with the value associated with this key
	 * @throws Exception if key is not in the map of output options.
	 */
	public String getOutputOption(String key) throws Exception;


	/**
	 * Method to set the value for a specific output parameter using a key to store in a map.
	 *
	 * @param key :the key use to designate the value to be stored
	 * @param val :the actual value matched to key
	 */
	public void setOutputOption(String key,String val);

	/**
	 * Method to get all keys to the information stored in this object.
	 *
	 * @return a <code>Set</code> with all keys held in this instance of the object
	 */
	public Set<String> getOutputOptions();
}


package fgmt;

public abstract class Fragment {
	public enum typeEnum {ENTITY_FRAGMENT, RELATION_FRAGMENT, TYPE_FRAGMENT, VAR_FRAGMENT}; 
	
	public typeEnum fragmentType;
	public int fragmentId;
	
	/**
	 * 0: compatible
	 * 1: compatible but narrowed
	 * -2: not compatilbe
	 * -3: error
	 * 4: need check entity and type
	 * 5: should adjust and check again
	 * 
	 * @param neighborFragment
	 * @return
	 */
};

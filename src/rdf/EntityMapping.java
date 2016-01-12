package rdf;

import fgmt.EntityFragment;

public class EntityMapping implements Comparable<EntityMapping> {
	public String entityID = null;
	public String entityName = null;
	public double score = 0;
	
	public EntityFragment entityFragment = null;
	
	public EntityMapping(String eid, String en, double sco) {
		entityID = eid;
		entityName = en;
		score = sco;
		
		//惩罚一下以?开头的entity
		if (entityName.startsWith("?"))
			score *=0.5;
	}
	
	// In descending order: big --> small
	public int compareTo(EntityMapping o) {
		double diff = this.score - o.score;
		if (diff > 0) return -1;
		else if (diff < 0) return 1;
		else return 0;
	}
	
	public int hashCode()
	{
		return entityID.hashCode();
	}
	
	public String toString() 
	{
		StringBuilder res = new StringBuilder(entityName+"("+score+")");
		return res.toString();
	}
}
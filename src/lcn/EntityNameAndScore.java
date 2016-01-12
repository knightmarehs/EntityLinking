package lcn;

public class EntityNameAndScore implements Comparable<EntityNameAndScore> {
	public String entityName;
	public double score;
	
	public EntityNameAndScore(String n, double s) {
		entityName = n;
		score = s;		
	}
	
	@Override
	public String toString() {
		return "<" + entityName + ">\t" + score;
	}

	public int compareTo(EntityNameAndScore o) {
		if(this.score < o.score) {
			return 1;
		}
		else if (this.score > o.score) {
			return -1;
		}
		else {
			return 0;
		}
	}

}

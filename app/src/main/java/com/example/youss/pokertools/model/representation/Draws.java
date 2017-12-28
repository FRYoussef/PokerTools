package model.representation;

public enum Draws {
	OpenEndedStraight,
	GutShotStraight,
	FlushDraw,
	OpenEndedStraightFlush,
	GutShotStraightFlush;
	
	private static final String NAMES [] = {"Open Ended\nStraight Draw","Gutshot Straight\nDraw","Flush Draw", "Open Ended\nStraightflush\nDraw", "Gutshot Straightflush\nDraw"};
	
	public static final int NUM_DRAWS = 5;
	
	@Override
	public String toString() {
		return NAMES[this.ordinal()];
	}
	
}

package model.representation.game;

public enum Play implements Comparable<Play> {
	HighCard,
	Pair,
	TwoPair,
	ThreeOfAKind,
	Straight,
	Flush,
	FullHouse,
	FourOfAKind,
	StraightFlush;

	public static final int NUM_PLAYS = 9;
	public static final int CARDS_PER_PLAY = 5;
	public static int playFormingCards (Play play) {
		int res = 5;
		if (play == HighCard) {
			res = 1;
		}
		else if (play == Pair) {
			res = 2;
		}
		else if (play == ThreeOfAKind) {
			res = 3;
		}
		else if (play == TwoPair || play == FourOfAKind) {
			res = 4;
		}
		return res;
	}

	@Override
	public String toString() {
		return this.name();
	}
}

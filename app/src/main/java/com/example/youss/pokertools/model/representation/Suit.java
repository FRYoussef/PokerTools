package model.representation;

public enum Suit {
	Spades,
	Hearts,
	Clubs,
	Diamonds,
	None;

	public static final int NUM_SUIT = 4;

	public static Suit getFromChar (char c) {
		c = Character.toLowerCase(c);
		switch (c) {
		case 's':
			return Suit.Spades;
		case 'h':
			return Suit.Hearts;
		case 'c':
			return Suit.Clubs;
		case 'd':
			return Diamonds;
		default:
			return null;
		}
	}

	public static Suit getFromInt(int value){
        return Suit.values()[value];
    }

	@Override
	public String toString() {
		return "" + Character.toLowerCase(this.name().charAt(0));
	}


	
	public String toLongString() {
		return this.name();
	}
}

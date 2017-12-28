package model.representation.range;


public enum PairType {
	WeakPair,
	MiddlePair,
	BelowPair,
	TopPair,
	OverPair;
	
	public static int NUM_PAIRS = 5;
	
	public static PairType getFromInt(int value){
        switch (value) {
            case 0:
                return WeakPair;
            case 1:
                return MiddlePair;
            case 2:
                return BelowPair;
            case 3:
                return TopPair;
            case 4:
                return OverPair;
        }
        return null;
    }
	
	@Override
	public String toString() {
		String res = "";
		if (this == WeakPair) {
			res = "Weak Pair";
		}
		else if (this == MiddlePair) {
			res = "Middle Pair";
		}
		else if (this == BelowPair) {
			res = "Below Pair";
		}
		else if (this == TopPair) {
			res = "Top Pair";
		}
		else if (this == OverPair) {
			res = "Over Pair";
		}		
		return res;
	}
}



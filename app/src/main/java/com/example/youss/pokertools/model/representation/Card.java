package com.example.youss.pokertools.model.representation;

public class Card implements Comparable<Card>{

//public:
	//fields:
	public final static char [] lut = {'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
	public static final int NUM_CARDS = 13;
	 
	
	//ctor:
	public Card (byte value, Suit suit) throws Exception {
		if (value < 0 || value > 12) {
			throw new Exception("Card ctor.: Card value must be within [0,12]");
		}		
		this.value = value;
		this.suit = suit;
	}
	
	public Card (int value, Suit suit) throws Exception {
		if (value < 0 || value > 12) {
			throw new Exception("Card ctor.: Card value must be within [0,12]");
		}	
		this.value = (byte)value;
		this.suit = suit;
	}
	
	
	//getters&setters:
	public byte getValue() {
		return value;
	}
	
	public Suit getSuit () {
		return suit;
	}
	
	
	//methods:
	public static int charToValue(char c) {
		if (c >= '2' && c <= '9') {
			return c - _CONVERSION;
		}
		else {
			int res = -1;
			
			switch (c) {
			case 'T':
				res = 8;
				break;
			case 'J':
				res = 9;
				break;
			case 'Q':
				res = 10;
				break;
			case 'K':
				res = 11;
				break;
			case 'A':
				res = 12;
				break;
			}
			
			return res;
		}
	}
	
	//inherited:
	@Override
	public String toString() {
		return lut[value] + suit.toString();
	}

	@Override
	public int compareTo(Card o) {
		return value - o.getValue();
	}

	@Override
	public boolean equals(Object o) {
	    Card c = (Card) o;
		return value == c.getValue() && suit == c.getSuit();
	}

    @Override
    public int hashCode() {
        return value + suit.ordinal()*13;
    }

    //private:
	//fields:
	private byte value;
	private Suit suit;
	private static char _CONVERSION = '2';  
	
		
}

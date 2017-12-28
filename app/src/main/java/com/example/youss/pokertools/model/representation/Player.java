package model.representation;


public class Player {
	public final int NUM_CARDS;
	private int ID;
	private Card cards [] = null;
	
	
	public Player(int ID, int n, Card ...cards) {
		this.ID = ID;
		this.cards = cards;
		NUM_CARDS = n;
	}

	public Player(int ID, int n) {
		this.ID = ID;
		NUM_CARDS = n;
	}

	public int getID() {
		return ID;
	}
	
	public void setId(int id){
		this.ID = id; 
	}
	public int getNCards () {
		return cards.length;
	}

	public Card[] getCards() {
		return cards;
	}

	public void setCards(Card[] cards) {
		this.cards = cards;
	}

	public Card getCard (int i) {
		return cards[i];
	}

	public String toString(){
		String res = "ID: " + this.getID() + " Cartas: ";
		if(cards != null){
			for(int i = 0; i < this.cards.length; i++)
				res += this.cards[i].toString();
			res += "\n";
		}
		return res;
	}
}

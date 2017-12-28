package control.ObserverPatron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

import model.representation.Card;
import model.representation.game.Deck;

public class OPlayerCards {
	
	private ArrayList<Card> cards;
	private int numPlayer;

	public OPlayerCards(ArrayList<Card> cards, int numPlayer){
		this.cards = cards;
		this.numPlayer = numPlayer;
		Collections.sort(this.cards);
    }

	public ArrayList<Card> getCards(){
		return this.cards;
	}
	public int getNumPlayer(){
		return this.numPlayer;
	}
}

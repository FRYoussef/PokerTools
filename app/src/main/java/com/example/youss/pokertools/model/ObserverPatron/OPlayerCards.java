package com.example.youss.pokertools.model.ObserverPatron;

import com.example.youss.pokertools.model.representation.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

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

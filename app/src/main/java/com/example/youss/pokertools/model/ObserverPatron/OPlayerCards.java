package com.example.youss.pokertools.model.ObserverPatron;

import com.example.youss.pokertools.model.representation.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

public class OPlayerCards {
	
	private ArrayList<Card> cards;
	private int numPlayer;
	private Card[] outs = null;
	private Card[] ins = null;

	public OPlayerCards(ArrayList<Card> cards, int numPlayer){
		this.cards = cards;
		this.numPlayer = numPlayer;
		Collections.sort(this.cards);
    }

	public OPlayerCards(ArrayList<Card> cards, int numPlayer, Card[] outs, Card[] ins) {
		this.cards = cards;
		this.numPlayer = numPlayer;
		this.outs = outs;
		this.ins = ins;
		Collections.sort(this.cards);
	}

	public Card[] getOuts() {
		return outs;
	}

	public Card[] getIns() {
		return ins;
	}

	public ArrayList<Card> getCards(){
		return this.cards;
	}
	public int getNumPlayer(){
		return this.numPlayer;
	}
}

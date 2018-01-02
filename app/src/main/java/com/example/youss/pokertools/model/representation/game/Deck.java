package com.example.youss.pokertools.model.representation.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.youss.pokertools.model.representation.Card;
import com.example.youss.pokertools.model.representation.Suit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Deck implements Parcelable{
	private HashSet<Card> cards;
	private Random random;
	
	public Deck() {
		init();
		fill();
	}
	
	private Deck (boolean fill) {
		init();
		if (fill)
			fill();
	}

	public Deck(Parcel in){
		readFromParcel(in);
		random = new Random(System.currentTimeMillis());
	}
	
	private void init () {
		cards = new HashSet<Card>(52);
		random = new Random(System.currentTimeMillis());
	}
	
	private void fill () {
		for (int i = 0; i < Suit.NUM_SUIT; i++) {
			Suit s = Suit.getFromInt(i);
			for (int j = 0; j < Card.NUM_CARDS; j++) {
				try {
					Card c = new Card(j, s);
					cards.add(c);
				} catch (Exception e) {}
			}
		}
	}
	
	
	public Card drawCard () {
		try {
			int cardVal;
			Card card = null;
			do {
				cardVal = random.nextInt(52);
				card = new Card(cardVal%13, Suit.getFromInt(cardVal/13));
			}while (!cards.remove(card));
			
			return card;
		}catch (Exception e) {}
		return null;
	}
	
	public void insertCards (HashSet<Card> cards) {
		this.cards.addAll(cards);
	}
	
	public void removeCard(Card card){
		if(cards.contains(card))
			cards.remove(card);
	}
	
	public void replaceCard(Card card){
		if(!cards.contains(card))
			cards.add(card);
	}
	
	public boolean contains(Card card){
		return cards.contains(card);
	}
	
	@Override
	public Object clone() {
		Deck d = new Deck (false);
		
		for (Card card : cards) {
			try {
				//This absolutely makes a new card with no reference to the original
				d.cards.add(new Card(card.getValue(), Suit.getFromInt(card.getSuit().ordinal())));
			} catch (Exception e) {}
		}
		
		return d;
	}
	//------
	public boolean takeOutCard(Card c){
		return this.cards.remove(c);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeParcelableArray((Card[])cards.toArray(), 1);
	}

	private void readFromParcel(Parcel in) {
		cards = new HashSet<>(Arrays.asList((Card[])in.readParcelableArray(getClass().getClassLoader())));
	}

	public static final Creator<Deck> CREATOR = new Creator<Deck>() {
		@Override
		public Deck createFromParcel(Parcel in) {
			return new Deck(in);
		}

		@Override
		public Deck[] newArray(int size) {
			return new Deck[size];
		}
	};
}

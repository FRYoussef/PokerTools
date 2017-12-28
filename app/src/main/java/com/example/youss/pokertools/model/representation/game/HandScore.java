package model.representation.game;

import model.representation.Card;
import model.representation.Player;

import java.util.ArrayList;
import java.util.List;

public class HandScore implements Comparable<HandScore> {

    public static final int NUM_HAND_PLAY = 5;
	private Play handValue = Play.HighCard;
	private List<Card> handPlay = null;
	private String playerId;
	private Player player = null;

	public HandScore(Player player, Play handValue, List<Card> handPlay,
			Card[] playerCards){
		this.player = player;
		this.playerId = "J" + player.getID();
        this.handValue = handValue;
        this.handPlay = handPlay;
	}
    public HandScore(Play handValue, List<Card> handPlay, Card[] playerCards) {
    	this.player = new Player(-1, handPlay.size(), playerCards);
        this.handValue = handValue;
        this.handPlay = handPlay;
    }

    public HandScore(Card ... playerCards) {
        handPlay = new ArrayList<Card>(NUM_HAND_PLAY);
        this.player = new Player(-1, handPlay.size(), playerCards);
    }

    public HandScore() {
        handPlay = new ArrayList<Card>(NUM_HAND_PLAY);
    }

    public void setHandValue(Play handValue) {
        this.handValue = handValue;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean contains (Card c) {
    	int cardsToCheck = Play.playFormingCards(handValue);
    	boolean res = false;
    	for (int i = 0; i < cardsToCheck && !res; i++) {
    		res = handPlay.get(i).equals(c);
    	}
    	return res;
    }

    //---
    public void setPlayerId(String id){
    	this.playerId = id;
    }

    public String getPlayerId(){
    	return this.playerId;
    }
    
    public int getPlayer () {
    	return player.getID();
    }
    
    public Play getHandValue() {
        return handValue;
    }

    public Card[] getPlayerCards() {
    	Card cards [] = new Card [player.getNCards()];
    	for (int i = 0; i < player.getNCards(); i++) {
    		cards [i] = player.getCard(i);
    	}
    	return cards;
    }

    public String getPlayValue(){
        StringBuilder stringBuilder = new StringBuilder("( ");
        for (Card card : handPlay)
            stringBuilder.append(card.toString());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }

    public String toString(){
        String aux = new String(this.handValue.toString() + ": ");
        return aux + getPlayValue();
    }

    /**
     * Compare HandScore
     * @param other
     * @return >0 if the left HandScore is higher, <0 if the rigth HandScore is higher
     *          , and 0 if it is the same HandScore
     */
    @Override
	public int compareTo(HandScore other) {
        //compare the range of the play
        if(handValue.ordinal() == other.getHandValue().ordinal()){
            //compare the play
            int same = 0;

            for(int i = 0; i < Play.CARDS_PER_PLAY; i++){
                same = handPlay.get(i).compareTo(other.handPlay.get(i));
                if(same != 0)
                    return same;
            }
        }

        return handValue.ordinal() - other.getHandValue().ordinal();
	}
}

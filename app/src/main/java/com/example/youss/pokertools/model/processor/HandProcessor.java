package com.example.youss.pokertools.model.processor;

import com.example.youss.pokertools.model.representation.Card;
import com.example.youss.pokertools.model.representation.game.HandScore;
import com.example.youss.pokertools.model.representation.game.Play;

import java.util.ArrayList;

/**
 * This class is to return the higher play with the player cards
 */
public class HandProcessor {

    /** Table cards */
    private CardsProcessor tableCardsProcessor;
    /** Table cards + player cards */
    private CardsProcessor playerCardsProcessor;


    public HandProcessor() {
        tableCardsProcessor = new CardsProcessor();
    }

    public boolean getdrawFlush() {
    	if (playerCardsProcessor != null)
    		return playerCardsProcessor.getdrawFlush();
    	else
    		return tableCardsProcessor.getdrawFlush();
    }
    public boolean getopenEndedStraight() {
    	if (playerCardsProcessor != null)
    		return playerCardsProcessor.getopenEndedStraight();
    	else
    		return tableCardsProcessor.getopenEndedStraight();
    }
    public boolean getgutShotStraight() {
    	if (playerCardsProcessor != null)
    		return playerCardsProcessor.getgutShotStraight();
    	else
    		return tableCardsProcessor.getgutShotStraight();
    }
    public boolean getopenEndedStraightFlush() {
    	if (playerCardsProcessor != null)
    		return playerCardsProcessor.getopenEndedStraightFlush();
    	else
    		return tableCardsProcessor.getopenEndedStraightFlush();
    }
    public boolean getgutShotStraightFlush() {
    	if (playerCardsProcessor != null)
    		return playerCardsProcessor.getgutShotStraightFlush();
    	else
    		return tableCardsProcessor.getgutShotStraightFlush();
    }
    

    public void resetHandProcessor(){
        tableCardsProcessor = new CardsProcessor();
        playerCardsProcessor = null;
    }

    /**
     * Add a card
     * @param card to be added
     */
    public void addCard(Card card) throws Exception {
        if(card == null)
            throw new Exception("Null card");
        tableCardsProcessor.addCard(card);
    }

    /**
     * Add a card
     * @param table true it is add a card to the table, false add player card
     * @param card to be added
     */
    private void addCard(boolean table, Card card) throws Exception {
        if(!table){
            if(playerCardsProcessor == null)
                throw new Exception("playerCardsProcessor is not initialize");
            playerCardsProcessor.addCard(card);
        }
    }

    /**
     * Copy the content of the processor
     */
    private void cloneTableCards(){
        playerCardsProcessor = tableCardsProcessor.clone();
    }

    /**
     * Return the best play with the player cards
     * @param playerCards, or null
     * @return the best play
     */
    public HandScore getBestPlay(Card ...playerCards) throws Exception {
        CardsProcessor cardsProcessor = tableCardsProcessor;  
        
        //add the player cards
        if(playerCards != null){
            cloneTableCards();
            for(Card c : playerCards)
                addCard(false, c);
            cardsProcessor = playerCardsProcessor;
        }

        ArrayList<Card> cards = cardsProcessor.getHighRepeatedsCards();
        ArrayList<Card> cardsInStraights; /*for the 5 cards of a flush, straight or StraightFlush*/

        //straight flush
        if(cardsProcessor.isStraightFlush())
        {
        	cardsInStraights = cardsProcessor.getcardsOfStraights();

        	return new HandScore(Play.StraightFlush, cardsInStraights, playerCards);
        }
        //four of a kind
        if(cardsProcessor.isFourOfAKind())
        {
            return new HandScore(Play.FourOfAKind, cardsProcessor.fillWithHighCards(cards), playerCards);
        }
        //full house
        if(cardsProcessor.isFullHouse())
        {
            return new HandScore(Play.FullHouse, cards, playerCards);
        }
        //flush
        if(cardsProcessor.isFlush())
        {
        	cardsInStraights = cardsProcessor.getcardsOfStraights();
        	return new HandScore(Play.Flush, cardsInStraights, playerCards);
        }
        //straight
        if(cardsProcessor.isStraight())
        {
        	cardsInStraights = cardsProcessor.getcardsOfStraights();
        	return new HandScore(Play.Straight, cardsInStraights, playerCards);
        }
        //ThreeOfAKind
        if(cardsProcessor.isThreeOfAKind())
        {
            return new HandScore(Play.ThreeOfAKind, cardsProcessor.fillWithHighCards(cards), playerCards);
        }
        //two pair
        if(cardsProcessor.isTwoPair())
        {
            return new HandScore(Play.TwoPair, cardsProcessor.fillWithHighCards(cards), playerCards);
        }
        //pair or high card
        Play p = Play.HighCard;
        if(cardsProcessor.isPair())
            p = Play.Pair;

        return new HandScore(p, cardsProcessor.fillWithHighCards(cards), playerCards);
    }
}

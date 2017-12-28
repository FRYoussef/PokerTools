package model.representation.game;

import model.representation.Card;
import model.representation.Suit;

import java.util.ArrayList;

public class RankPerSuit {

    private int rank;
    private ArrayList<Boolean> suits;
    private int suitCounter;
    private Suit firstSuit;

    public RankPerSuit() {
        suits = new ArrayList<>(Suit.NUM_SUIT);
        for(int i = 0; i < Suit.NUM_SUIT; i++)
            suits.add(false);
        rank = -1;
        suitCounter = 0;
        firstSuit = Suit.None;
    }

    public int getRank() {
        return rank;
    }

    public int getSuitCounter() {
        return suitCounter;
    }

    public Suit getFirstSuit() {
        return firstSuit;
    }

    public ArrayList<Boolean> getSuits() {
        return suits;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setSuits(ArrayList<Boolean> suits) {
        this.suits = suits;
    }

    public void setSuitCounter(int suitCounter) {
        this.suitCounter = suitCounter;
    }

    /**
     * @param suit to consult
     * @return true is suited, false is not suited
     */
    public boolean isRankSuited(Suit suit){
        return this.suits.get(suit.ordinal());
    }

    /**
     * Add the card´s suits, and increase the counter of cards
     * @param card to add
     * @return the counter of suits
     */
    public int addCard(Card card){
        if(firstSuit == Suit.None)
            firstSuit = card.getSuit();

        if (rank == -1)
        	rank = card.getValue();

        suits.set(card.getSuit().ordinal(), true);
        return ++suitCounter;
    }

    public RankPerSuit clone(){
        RankPerSuit rankPerSuit = new RankPerSuit();
        rankPerSuit.setSuitCounter(suitCounter);
        rankPerSuit.setRank(rank);
        rankPerSuit.firstSuit = this.firstSuit;
        ArrayList<Boolean> s = new ArrayList<Boolean>();
        for (int i = 0; i < suits.size(); i++) {
        	s.add(i, ((boolean)suits.get(i)));
        }
        rankPerSuit.setSuits(s);
        return rankPerSuit;
    }

}

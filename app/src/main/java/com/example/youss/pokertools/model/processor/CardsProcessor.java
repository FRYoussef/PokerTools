package model.processor;

import model.representation.Card;
import model.representation.game.Play;
import model.representation.game.RankPerSuit;
import model.representation.Suit;

import java.util.ArrayList;

/**
 * It returns the plays(pair, straight, ...) that make up the cards
 */
public class CardsProcessor {

    /** the highest value of card repeated */
    private int highRankRep = -1;
    /** the second highest value of card repeated */
    private int secondRankRep = -1;
    /** counter of suites */
    private ArrayList<Integer> suitCounter;
    /** index to find flush */
    private int maxSuit = -1;
    private ArrayList<RankPerSuit> rankPerSuits;
    
    private Boolean isFlush = null;
    private Boolean isStraight = null;

    /** for the 5 cards of a flush, straight or StraightFlush */
    private ArrayList<Card> cardsOfStraights;
    /** if we need just one more card for flush */
    private boolean drawFlush;
    /** if we need just one more card for straight in one of the ends */
    private boolean openEndedStraight;
    /** if we need just one more card for straight between 2 cards */
    private boolean gutShotStraight;
    private int straightDrawPos[];
    /** if we need just one more card for straight in one of the ends */
    private boolean openEndedStraightFlush;
    /** if we need just one more card for straight between 2 cards */
    private boolean gutShotStraightFlush;
    /** In case we have flush this is the color of it */
    private Suit flushSuit;

    public CardsProcessor() {
        suitCounter = new ArrayList<>(Suit.NUM_SUIT);
        for(int i = 0; i < Suit.NUM_SUIT; i++)
            suitCounter.add(0);

        rankPerSuits = new ArrayList<>(Card.NUM_CARDS);
        for(int i = 0; i < Card.NUM_CARDS; i++)
            rankPerSuits.add(null);
        straightDrawPos = new int [4];
    }
    
    
    
    public ArrayList<Card> getcardsOfStraights() {
        return (ArrayList<Card>) cardsOfStraights.clone();
    }
    public boolean getdrawFlush() {
        return drawFlush;
    }
    public boolean getopenEndedStraight() {
        return openEndedStraight;
    }
    public boolean getgutShotStraight() {
        return gutShotStraight;
    }
    public boolean getopenEndedStraightFlush() {
        return openEndedStraightFlush;
    }
    public boolean getgutShotStraightFlush() {
        return gutShotStraightFlush;
    }

    
    
    public int getHighRankRep() {
        return highRankRep;
    }
    
    public ArrayList<Integer> getSuitCounter() {
        return suitCounter;
    }

    public int getMaxSuit() {
        return maxSuit;
    }

    public ArrayList<RankPerSuit> getRankPerSuits() {
        return rankPerSuits;
    }

    public void setHighRankRep(int highRankRep) {
        this.highRankRep = highRankRep;
    }

    public void setSuitCounter(ArrayList<Integer> suitCounter) {
        this.suitCounter = suitCounter;
    }

    public void setMaxSuit(int maxSuit) {
        this.maxSuit = maxSuit;
    }

    public void setRankPerSuits(ArrayList<RankPerSuit> rankPerSuits) {
        this.rankPerSuits = rankPerSuits;
    }

    public int getSecondRankRep() {
        return secondRankRep;
    }

    public void setSecondRankRep(int secondRankRep) {
        this.secondRankRep = secondRankRep;
    }

    /**
     * Add a card
     * @param card to be added
     */
    public void addCard(Card card) throws Exception {
    	if (rankPerSuits.get(card.getValue()) != null && rankPerSuits.get(card.getValue()).getSuits().get(card.getSuit().ordinal())) {
    		throw new Exception("CardsProcessor.addCard(): Card already inserted");
    	}
        int sC = suitCounter.get(card.getSuit().ordinal());
        suitCounter.set(card.getSuit().ordinal(), ++sC);
        if(sC > maxSuit){
            maxSuit = sC;
            flushSuit = card.getSuit();
        }

        if(rankPerSuits.get(card.getValue()) == null)
            rankPerSuits.set(card.getValue(), new RankPerSuit());

        int cont = rankPerSuits.get(card.getValue()).addCard(card);

        if (highRankRep == -1) {
            highRankRep = card.getValue();
        }
        else {
        	if (cont > rankPerSuits.get(highRankRep).getSuitCounter()) {
        		secondRankRep = highRankRep;
                highRankRep = card.getValue();
        	}
        	else if (cont == rankPerSuits.get(highRankRep).getSuitCounter()) {
        		if (card.getValue() > highRankRep) {
        			secondRankRep = highRankRep;
                    highRankRep = card.getValue();
        		}
        		else if (card.getValue() < highRankRep){
        			if (secondRankRep == -1) {
        				secondRankRep = card.getValue();
        			}
        			if (cont == rankPerSuits.get(secondRankRep).getSuitCounter()) {
	        			if (card.getValue() > secondRankRep) {
	        				secondRankRep = card.getValue();
	        			}
        			}
        			else if (cont > rankPerSuits.get(secondRankRep).getSuitCounter()) {
        				secondRankRep = card.getValue();
        			}
        		}
        	}
        	else {	//cont < rankPerSuits.get(highRankRep).getSuitCounter()
        		if (secondRankRep == -1) {
        			secondRankRep = card.getValue();
        		}
        		else {
	        		if (cont > rankPerSuits.get(secondRankRep).getSuitCounter()) {
	        			secondRankRep = card.getValue();
	        		}
	        		else if (cont == rankPerSuits.get(secondRankRep).getSuitCounter()) {
	        			if (card.getValue() > secondRankRep) {
	        				secondRankRep = card.getValue();
	        			}
	        		}
        		}
        	}
        }
    }

    public CardsProcessor clone(){
        CardsProcessor rP = new CardsProcessor();
        rP.setHighRankRep(highRankRep);
        rP.setSecondRankRep(secondRankRep);
        rP.setMaxSuit(maxSuit);
        rP.flushSuit = flushSuit;
        ArrayList<RankPerSuit> rps = new ArrayList<RankPerSuit>();
        for (int i = 0; i < rankPerSuits.size(); i++) {
        	if (rankPerSuits.get(i) == null) {
        		rps.add(i, null);
        	}
        	else {
        		rps.add(i, rankPerSuits.get(i).clone());
        	}
        }
        rP.setRankPerSuits(rps);
        ArrayList<Integer> sc = new ArrayList<Integer>();
        for (int i = 0; i < suitCounter.size(); i++) {
        	if (suitCounter.get(i) == null) {
        		sc.add(i, null);
        	}
        	else {
        		sc.add(i, ((int)suitCounter.get(i)));
        	}
        }
        rP.setSuitCounter(sc);
        return rP;
    }

    /**
     * @return true if there is four of a kind, false if theres not
     */
    public boolean isFourOfAKind(){
        return rankPerSuits.get(highRankRep).getSuitCounter() == 4;
    }

    /**
     * @return true if there is full house, false if theres not
     */
    public boolean isFullHouse(){
        return secondRankRep != -1 && rankPerSuits.get(highRankRep).getSuitCounter() == 3
                && rankPerSuits.get(secondRankRep).getSuitCounter() >= 2;
    }


    /**
     * @return true if there is three of a kind, false if theres not
     */
    public boolean isThreeOfAKind(){
        return rankPerSuits.get(highRankRep).getSuitCounter() == 3;
    }

    /**
     * @return true if there is two pair, false if theres not
     */
    public boolean isTwoPair() {
        return secondRankRep != -1 && rankPerSuits.get(highRankRep).getSuitCounter() == 2
                && rankPerSuits.get(secondRankRep).getSuitCounter() == 2;
    }

    /**
     * @return true if there is pair, false if theres not
     */
    public boolean isPair() {
        return rankPerSuits.get(highRankRep).getSuitCounter() == 2;
    }

    /**
	 * @return if there is a straight
	 * @throws Exception
	 */
	public boolean isStraight() throws Exception {
		if (isStraight == null) {
			int cont = 0;
			int contGS = 0;
			boolean openEnded = false, gutShot = false, straight = false;
			ArrayList<Card> cards = new ArrayList<Card>(5);

			boolean ace = rankPerSuits.get(Card.NUM_CARDS - 1) != null;
			for (int i = rankPerSuits.size()-1; i >= 0 && !straight; i--) { /* begins on the end to get the highest straight*/
				if (rankPerSuits.get(i) != null) {
					cont++;
					if (!openEnded) {
						openEnded = cont == 4;
						if (openEnded) {
							for (int j = 0; j < 4; j++) {
								straightDrawPos[j] = i+j;
							}
						}
					}

					if (!gutShot) {
						gutShot = contGS + cont == 4 && contGS != 0 && cont != 0;
						if (gutShot) {
							for (int j = 0; j < 4; j++) {
								int aux = 0;
								if (j == cont)
									aux = 1;
								straightDrawPos[j] = i+j+aux;
							}
						}
					}
					if (cont == 5) {
						straight = true;
						for (int j = 4; j >= 0; j--) {
							Card cardS = new Card(i+j, rankPerSuits.get(i+j).getFirstSuit());
							cards.add(4-j, cardS);
						}
					}
				} else {
					if (!gutShot) {
						contGS = cont;
					}
					cont = 0;
				}
				if (ace && i == 0 && cont == 4) {	//5432A straight
					straight = true;
					for (int j = 3; j >= 0; j--) {
						Card cardS = new Card(i+j, rankPerSuits.get(i+j).getFirstSuit());
						cards.add(3-j, cardS);
					}
					Card cardS = new Card(12, rankPerSuits.get(12).getFirstSuit());
					cards.add(4, cardS);
				}//TODO Add check for gutshot with ACE!
			}
			isStraight = straight;
			if (!isStraight) {
				openEndedStraight = openEnded;
				gutShotStraight = gutShot;
			}


			if (straight) {
				cardsOfStraights = cards;
			}
			checkStraightFlushDraw();
		}
		
		return isStraight;
	}
	
	private void checkStraightFlushDraw() {
		if (openEndedStraight || gutShotStraight) {
			boolean straightFlushDraw = false;
			for (int i = 0; i < 4 && !straightFlushDraw; i++) {
				Suit suit = Suit.values()[i];
				int straightFlushCount = 0;
				for (int j = 0; j < 4; j++) {
					if (rankPerSuits.get(straightDrawPos[j]).isRankSuited(suit))
						straightFlushCount++;
				}
				straightFlushDraw = straightFlushCount == 4;
			}
			if (straightFlushDraw) {
				openEndedStraightFlush = openEndedStraight;
				gutShotStraightFlush = gutShotStraight;
			}
		}
	}

	/**
	 * @return if there is a flush
	 * @throws Exception
	 */
	public boolean isFlush() throws Exception {

		if (isFlush == null) {
			int cont = 0;


			boolean flush = suitCounter.get(flushSuit.ordinal()) >= 5;
			if (flush) {
				cardsOfStraights = new ArrayList<Card>();

				for (int i = rankPerSuits.size() - 1; i >= 0 && cont < 5; i--) {
					if (rankPerSuits.get(i) != null) {
						if (rankPerSuits.get(i).getSuits().get(flushSuit.ordinal())) {	//rankPerSuits.contains(maxSuit)
							Card cardS = new Card(i, flushSuit);
							cardsOfStraights.add(cont++, cardS);
						}
					}

				}
			}
			drawFlush = suitCounter.get(flushSuit.ordinal()) == 4;
			isFlush = flush;
		}

		return isFlush;

	}

	/**
	 * @return if there is a straight flush
	 * @throws Exception
	 */
	public boolean isStraightFlush() throws Exception {

		boolean straightFlush = false;

		if (isFlush()) {
			int cont = 0;
			ArrayList<Card> cards = new ArrayList<Card>();

			boolean ace = rankPerSuits.get(Card.NUM_CARDS - 1) != null;
			for (int i = rankPerSuits.size()-1; i >= 0 && !straightFlush; i--) { /* begins on the end to get the highest straight*/
				if (rankPerSuits.get(i) != null) {
					cont++;
					if (cont >= 5) {
						boolean error = false;
						for (int j = 4; j >= 0 && !error; j--) {
							if (rankPerSuits.get(i+j).getSuits().get(flushSuit.ordinal())) {	//if rankPerSuits[i+j].contains(flushSuit)
								Card cardS = new Card(i+j, flushSuit);
								cards.add(4-j, cardS);
							}
							else {
								cards.clear();
								error = true;
							}
						}
						if (cards.size() == 5) {
							straightFlush = true;
							cardsOfStraights = cards;
							if (cardsOfStraights.size() != 5) {
								System.err.println("ERROR");
							}
						}
					}
				}
				else {
					cont = 0;
				}
				if (ace && i == 0 && cont >= 4 && !straightFlush) {	//5432A straight
					if (rankPerSuits.get(12).getSuits().get(flushSuit.ordinal())) {	//If A is of suit flushSuit

						boolean error = false;
						for (int j = 3; j >= 0 && !error; j--) {
							if (rankPerSuits.get(i+j).getSuits().get(flushSuit.ordinal())) {	//if rankPerSuits[i+j].contains(flushSuit)
								Card cardS = new Card(i+j, flushSuit);
								cards.add(3-j, cardS);
							}
							else {
								cards.clear();
								error = true;
							}
						}
						if (!error) {
							Card cardS = new Card(12, flushSuit);
							cards.add(4, cardS);
							straightFlush = true;
						}
						if (cards.size() == 5) {
							straightFlush = true;
							cardsOfStraights = cards;
							if (cardsOfStraights.size() != 5) {
								System.err.println("ERROR");
							}
						}
					}
				}
			}

		}

		return straightFlush;
	}

    /**
     * Get the repeateds cards 4, 3, 2, 1
     * @return an array where is the play cards
     * @throws Exception
     */
    public ArrayList<Card> getHighRepeatedsCards() throws Exception {
        ArrayList<Card> play = new ArrayList<>(Play.CARDS_PER_PLAY);
        RankPerSuit h = rankPerSuits.get(highRankRep);
        RankPerSuit s = null;
        if (secondRankRep != -1)
        	 s = rankPerSuits.get(secondRankRep);
        
        //best hand
        for(int i = 0; i < Suit.NUM_SUIT && play.size() < Play.CARDS_PER_PLAY; i++){
            if(h.getSuits().get(i)){
                play.add(new Card(h.getRank(), Suit.getFromInt(i)));
            }
        }
        //second best hand
        if (s != null) {
	        for(int i = 0; i < Suit.NUM_SUIT && play.size() < Play.CARDS_PER_PLAY; i++){
	            if(s.getSuits().get(i)){
	                play.add(new Card(s.getRank(), Suit.getFromInt(i)));
	            }
	        }
        }
        return play;
    }

    /**
     * Fill an array with 4,3,2,1 cards
     * @param cards
     * @throws Exception
     */
    public ArrayList<Card> fillWithHighCards(ArrayList<Card> cards) throws Exception {
            for(int j = Card.NUM_CARDS - 1; j >= 0 && cards.size() < Play.CARDS_PER_PLAY; j--){
                if(rankPerSuits.get(j) != null && j != highRankRep && j != secondRankRep){
                    cards.add(new Card(rankPerSuits.get(j).getRank(), rankPerSuits.get(j).getFirstSuit()));
                }
        }

        return cards;
    }


}

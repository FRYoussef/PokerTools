package model.processor;

import model.representation.Card;
import model.representation.Draws;
import model.representation.Suit;
import model.representation.game.HandScore;
import model.representation.game.Play;
import model.representation.range.CoupleCards;
import model.representation.range.PairType;
import model.representation.Stat;

import java.util.ArrayList;
import java.util.HashSet;

public class RangeProcessor {
    private HashSet<Card> hsBoard = null;
    private Card highBoardCard = null;
    private Card sndHighBoardCard = null;
    private Card lowBoardCard = null;
    private HashSet<CoupleCards> hsRange = null;
    private HandScore[] plays = null;
    private HandScore[] pairPlays = null;
    private int[] playsCounter = null;
    private int[] pairPlaysCounter = null;
    private int [] drawsCounter = null;
    private HandProcessor handProcessor = null;    
    private int combos = 0;
    private int nDraws = 0;
    private HandScore boardScore = null;

    private ArrayList<Stat> playStats = null;
    private ArrayList<Stat> drawStats = null;
    
    
    public RangeProcessor(HashSet<Card> hsBoard, HashSet<CoupleCards> hsRange) throws Exception {
        this.hsBoard = hsBoard;
        this.hsRange = hsRange;
        handProcessor = new HandProcessor();
        for(Card c : hsBoard) {
            handProcessor.addCard(c);
            if (highBoardCard == null || highBoardCard.compareTo(c) < 0) {
            	sndHighBoardCard = highBoardCard;
            	highBoardCard = c;
            }
            else if (sndHighBoardCard == null || sndHighBoardCard.compareTo(c) < 0)
            	sndHighBoardCard = c;
            if (lowBoardCard == null || lowBoardCard.compareTo(c) > 0) {
            	lowBoardCard = c;
            }
        }
        boardScore = handProcessor.getBestPlay();

        
        plays = new HandScore [Play.NUM_PLAYS];
        pairPlays = new HandScore [PairType.NUM_PAIRS];
        playsCounter = new int [Play.NUM_PLAYS];
        pairPlaysCounter = new int [PairType.NUM_PAIRS];
        drawsCounter = new int [Draws.NUM_DRAWS];
        
        playStats = new ArrayList<>();
        drawStats = new ArrayList<>();
    }

    public ArrayList<Stat> getPlayStats(){
        return playStats;
    }

    public ArrayList<Stat> getDrawStats() {
        return drawStats;
    }

    public void run() throws Exception {
        for(CoupleCards cp : hsRange){
            if(cp.isOffSuited())
            	offSuitedCombination(cp);
            else
            	suitedCombination(cp);   
        }
        //Possibly redundant check, better safe than sorry
        if (combos > 0) {
	        for (int i = plays.length-1; i >= 0; i--) {
	            if(plays[i] != null)
	            	playStats.add(new Stat(plays[i].getHandValue().toString(), plays[i].getPlayValue(), (int)Math.floor((playsCounter[i]*100)/combos)));
	            else if (i == Play.Pair.ordinal()) {
	        		for (int j = pairPlays.length -1; j >= 0; j--) {
	        			if (pairPlays[j] != null) 
	        				playStats.add(new Stat(PairType.getFromInt(j).toString(), pairPlays[j].getPlayValue(), (int)Math.floor((pairPlaysCounter[j]*100)/combos)));
	        		}
	            }
	        }
        }
        if (nDraws > 0) {
	        for (int i = 0; i < Draws.NUM_DRAWS; i++) {
	        	if (drawsCounter[i] > 0) 
	        		drawStats.add(new Stat(Draws.values()[i].toString(), drawsCounter[i], false));
	        }
        }
    }

    private void offSuitedCombination(CoupleCards cp) throws Exception {
        Card c1 = null;
        Card c2 = null;
        for (int i = 0; i < Suit.NUM_SUIT; i++) {
            for (int j = i+1; j < Suit.NUM_SUIT; j++) {
                c1 = new Card(cp.getHigherValue(), Suit.getFromInt(i));
                c2 = new Card(cp.getLowerValue(), Suit.getFromInt(j));
                handScoreProcess(c1, c2);
            }
        }
    }

    private void suitedCombination(CoupleCards cp) throws Exception {
        Card c1 = null;
        Card c2 = null;
        for (int i = 0; i < Suit.NUM_SUIT; i++) {
            c1 = new Card(cp.getHigherValue(), Suit.getFromInt(i));
            c2 = new Card(cp.getLowerValue(), Suit.getFromInt(i));
            handScoreProcess(c1, c2);
        }
    }

    private void handScoreProcess(Card c1, Card c2) throws Exception {

        if(hsBoard.contains(c1) || hsBoard.contains(c2))
            return;

        HandScore handScore = handProcessor.getBestPlay(c1, c2);
        //If the play doesn't include hand cards
        if (allCardsFromBoard(handScore, c1, c2))	
        	return;
        
        combos++;
        if(plays[handScore.getHandValue().ordinal()] == null) {
            plays[handScore.getHandValue().ordinal()] = handScore;
            checkPairType(handScore, c1, c2);
        }
        

        else if(plays[handScore.getHandValue().ordinal()].compareTo(handScore) < 0){
            plays[handScore.getHandValue().ordinal()] = handScore;
            checkPairType(handScore, c1, c2);
        }

        playsCounter[handScore.getHandValue().ordinal()]++;
        if (handProcessor.getopenEndedStraight()) {
        	drawsCounter[Draws.OpenEndedStraight.ordinal()] ++;
        	nDraws++;
        }
        if (handProcessor.getgutShotStraight()) {
        	drawsCounter[Draws.GutShotStraight.ordinal()] ++;
        	nDraws++;
        }
        if (handProcessor.getdrawFlush()) {
        	drawsCounter[Draws.FlushDraw.ordinal()] ++;
        	nDraws++;
        }
        if (handProcessor.getopenEndedStraightFlush()) {
        	drawsCounter[Draws.OpenEndedStraightFlush.ordinal()] ++;
        	nDraws++;
        }
        if (handProcessor.getgutShotStraightFlush()) {
        	drawsCounter[Draws.GutShotStraightFlush.ordinal()] ++;
        	nDraws++;
        }
    }

    private boolean allCardsFromBoard(HandScore handScore, Card c1, Card c2) {
    	boolean res = false;
		if (handScore.getHandValue() == boardScore.getHandValue()){
			res = !(handScore.contains(c1) || handScore.contains(c2));
		}
		return res;
	}
    
    private void checkPairType (HandScore hand, Card c1, Card c2) {
    	if (hand.getHandValue() == Play.Pair) {
    		PairType p = null;
    		if (c1.getValue() == c2.getValue() && c1.getValue() > highBoardCard.getValue()) {
    			 p = PairType.OverPair;
    		}
    		else if (c1.getValue() == c2.getValue() && c1.getValue() < highBoardCard.getValue()) {
    			if (c1.getValue() > lowBoardCard.getValue())	
    				p = PairType.BelowPair;
    			else
    				p = PairType.WeakPair;
    		}
    		else if (c1.getValue() == highBoardCard.getValue() || c2.getValue() == highBoardCard.getValue()) {
    			p = PairType.TopPair;
    		}
    		else if (c1.getValue() == sndHighBoardCard.getValue() || c2.getValue() == sndHighBoardCard.getValue()) {
    			p = PairType.MiddlePair;
    		}
    		else {
    			p = PairType.WeakPair;
    		}
    		if (pairPlays [p.ordinal()] == null || pairPlays [p.ordinal()].compareTo(hand) < 0)
    			pairPlays [p.ordinal()] = hand;
    		pairPlaysCounter[p.ordinal()] ++;
            plays[Play.Pair.ordinal()] = null;
    	}
    }
    
	public String toString(){
        StringBuilder sb = new StringBuilder();
       for (int i = plays.length-1; i >= 0; i--) {
            if(plays[i] != null) {
            	
            		sb.append(plays[i]).append(" -> ").append((int) Math.floor((playsCounter[i] * 100) / combos)).append("%");
            }
            else if (i == Play.Pair.ordinal()) {
        		for (int j = pairPlays.length -1; j >= 0; j--) {
        			if (pairPlays[j] != null) 
        				sb.append(pairPlays[j]).append(" (").append(PairType.getFromInt(j).toString()).append(") -> ").append((int) Math.floor((pairPlaysCounter[j] * 100) / combos)).append("%");
        			
        		}
            }
        }
        return sb.toString();
    }

}

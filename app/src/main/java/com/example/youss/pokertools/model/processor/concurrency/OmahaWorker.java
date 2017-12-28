package model.processor.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import model.processor.HandProcessor;
import model.representation.Card;
import model.representation.Player;
import model.representation.game.Deck;
import model.representation.game.HandScore;

public class OmahaWorker extends HEWorker {

	public OmahaWorker(Shared shared, HashMap<Integer, Player> players, ArrayList<Card> boardCards, Deck deck) {
		super(shared, players, boardCards, deck);
	}

	public OmahaWorker(Shared shared, HashMap<Integer, Player> players, ArrayList<Card> boardCards, Deck deck, int nExecutes) {
		super(shared, players, boardCards, deck, nExecutes);
	}
	
	@Override
	protected void simulate() throws Exception {
		int nBoard = 5 - boardCards.size();
		HandProcessor hp = new HandProcessor();
		for (Card card : boardCards) {
			hp.addCard(card);
		}
	
		HashSet<Card> drawnCards = new HashSet<Card>();
		for (int i = 0; i < nBoard; i++) {
			Card c = deck.drawCard();
			drawnCards.add(c);
			hp.addCard(c);
		}
		
		ArrayList<HandScore> results = new ArrayList<HandScore>();
		for (Player p : players.values()){
			for (int i = 0; i < 3; i++) {
				for (int j = i+1; j < 4; j++) {
					HandScore bp = hp.getBestPlay(p.getCard(i), p.getCard(j));
					bp.setPlayer(p);
					results.add(bp);
				}
			}
		}
		
		HandScore best;
		Collections.sort(results);
		//it keeps track of tied players. 
		ArrayList<Integer> tiedPlayers = new ArrayList<Integer>();
		do {
			best = results.remove(results.size()-1);
			if (!tiedPlayers.contains(best.getPlayer()))
				tiedPlayers.add(best.getPlayer());
		}while(results.size() > 0 && best.compareTo(results.get(results.size()-1)) == 0);
		
		for(Integer i : tiedPlayers)
			shared.increasePlayer(i, 1.d / (double) tiedPlayers.size());
		
		shared.increaseSim();
		deck.insertCards(drawnCards);
	}

}

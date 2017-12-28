package model.processor;

import java.util.*;

import control.ObserverPatron.HandlerObserver;
import control.ObserverPatron.OPlayerCards;
import model.processor.concurrency.HEWorker;
import model.processor.concurrency.OmahaWorker;
import model.processor.concurrency.Shared;
import model.representation.Card;
import model.representation.Player;
import model.representation.game.Deck;

public class EquityProcessor{
	private final int N_THREADS;
	private static final int MAX_BOARD_CARDS = 5;
	public static final int GAME_NLHE = 0;
	public static final int GAME_OMAHA = 1;

	private ArrayList<Thread> threads; 
	private Shared sharedData; 

	private Timer timer;
	private Deck deck;
	private ArrayList<Card> boardCards;
	private HashMap<Integer, Player> hmPlayer;
	
	public EquityProcessor(int dim){
		N_THREADS = Runtime.getRuntime().availableProcessors();
		this.threads = new ArrayList<>(N_THREADS);
		deck = new Deck();
		boardCards = new ArrayList<>(MAX_BOARD_CARDS);
		hmPlayer = new HashMap<>(dim);
	}

	public HashMap<Integer, Player> getHmPlayer() {
		return hmPlayer;
	}

	/**
	 * It adds a player no nullable
	 * @param player
	 * @throws Exception
	 */
	public void addPlayer(Player player) throws Exception {
		if(player == null)
			throw new Exception("You cant add a null player");
		hmPlayer.put(player.getID(), player);
	}

	/**
	 * It removes a player who exist
	 * @param player
	 * @throws Exception
	 */
	public void removePlayer(int player) throws Exception {
		if(!hmPlayer.containsKey(player))
			throw new Exception("This player " + player + " does not exist, you cant remove it");

		hmPlayer.remove(player);
	}

	/**
	 * It restores the player cards to the deck, and clear the players
	 */
	public void removeAllPlayers(){
		if(hmPlayer.size() == 0)
			return;
		//restore the player cards
		for(Integer p : hmPlayer.keySet())
			if(hmPlayer.get(p).getCards() != null)
				deck.insertCards(new HashSet<>(Arrays.asList(hmPlayer.get(p).getCards())));
		hmPlayer.clear();
	}

	public void addPlayerCards(int player, Card ...cards) throws Exception {
		if (cards == null)
			throw new Exception("The player cards should not be null");
		if(!hmPlayer.containsKey(player))
			throw new Exception("The player " + player + " are not in the list");
		hmPlayer.get(player).setCards(cards);
	}

	/**
	 * It adds cards form the controller
	 * @param cards
	 * @throws Exception
	 */
	public void addBoardCards(Card ...cards) throws Exception {
		if(cards == null)
			throw new Exception("The cards to be added in the board must be not null");
		if(MAX_BOARD_CARDS - boardCards.size() - cards.length < 0)
			throw new Exception("The amount of board cards is " + MAX_BOARD_CARDS);
		for(Card c : cards){
			if(boardCards.contains(c))
				throw new Exception("This card is already in the board: " + c);
			boardCards.add(c);
		}
	}

	/**
	 * It adds cards form the controller
	 * @param cards
	 * @throws Exception
	 */
	public void addBoardCards(ArrayList<Card> cards) throws Exception {
		if(cards == null)
			throw new Exception("The cards to be added in the board must be not null");
		if(cards.size() > MAX_BOARD_CARDS)
			throw new Exception("The max board size is " + MAX_BOARD_CARDS);
		boardCards = (ArrayList<Card>) cards.clone();
	}

	public void clearBoard(){
		for(Card c : boardCards)
			deck.replaceCard(c);
		boardCards.clear();
	}

	/**
	 * It returns if all players got cards
	 * @param num
	 * @return
	 */
	public boolean isPlayersGetCards(int num){
		int count = 0;
		for(Integer i : hmPlayer.keySet())
			if(hmPlayer.get(i).getCards() != null)
				count++;
		return count == num;
	}

    /**
     * It add cards to players who dont have cards, and notify it
     */
	public void placeRemainingPlayerCards(){
	    for(Integer i: hmPlayer.keySet()){
	        if(hmPlayer.get(i).getCards() == null){
	            Card cs[] = new Card[hmPlayer.get(i).NUM_CARDS];
                for (int j = 0; j < cs.length; j++)
                    cs[j] = deck.drawCard();
                hmPlayer.get(i).setCards(cs);
                HandlerObserver.getoSolution().notifyPlayerCards(new OPlayerCards(new ArrayList<>(Arrays.asList(cs)),i));
            }
        }
    }

	public int numCardsBoard(){
		return boardCards.size();
	}

	/**
	 * It adds and return a random card from the deck
	 * @return
	 * @throws Exception
	 */
	public Card getRandomBoardCard() throws Exception {
		Card c = deck.drawCard();
		addBoardCards(c);
		HandlerObserver.getoSolution().notifyPlayerCards(new OPlayerCards(boardCards, -1));
		return c;
	}

	public ArrayList<Card> getBoardCards() {
		return boardCards;
	}

	public Deck getDeck() {
		return deck;
	}

	public void calculateEquity(int initialPlayers, int game){
		this.sharedData = new Shared(initialPlayers);
		this.timer = new Timer();
		for(int i = 0; i < N_THREADS; i++){
			Thread t = null;
			if(game == GAME_NLHE)
				t = new Thread(new HEWorker(this.sharedData, hmPlayer, boardCards, deck));
			else if(game == GAME_OMAHA)
				t = new Thread(new OmahaWorker(this.sharedData, hmPlayer, boardCards, deck));
			this.threads.add(t);
			t.setDaemon(true);
			t.start();
		}
		this.timer.scheduleAtFixedRate(new UpdateEquities(this.sharedData), 0, 300);
		this.timer.scheduleAtFixedRate(new UpdateSims(this.sharedData), 0, 300);
	}

	public void calculateFinalEquity(int initialPlayers, int game){
		this.sharedData = new Shared(initialPlayers);
		Thread t = null;
		if(game == GAME_NLHE)
			t = new Thread(new HEWorker(this.sharedData, hmPlayer, boardCards, deck, 1));
		else if(game == GAME_OMAHA)
			t = new Thread(new OmahaWorker(this.sharedData, hmPlayer, boardCards, deck, 1));
		t.setDaemon(true);
		t.start();
	}
	
	public void stopThreads(){
		if(this.sharedData != null)
			this.sharedData.stop(); 
		for(Thread t : this.threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//cancel and removes tasks.
		if(this.timer != null){
			this.timer.cancel();
			this.timer.purge();
		}
	}
	
}

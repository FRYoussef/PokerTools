package control.ObserverPatron;


import java.util.Observable;

public class OSolution extends Observable{
	public static final int NOTIFY_PLAYER_CARDS = 0;
	public static final int NOTIFY_EQUITY = 1;
	public static final int NOTIFY_SIM = 2;
	public static final int NOTIFY_FOLD = 3;
	public static final int NOTIFY_PLAYER_CARDS_STRING = 4;

	private int state = -1;

	public void notifyPlayerCards(OPlayerCards playerCards){
		state = NOTIFY_PLAYER_CARDS;
	    setChanged();
	    notifyObservers(playerCards);
    }

	public void notifyEquity(double[] is){
		state = NOTIFY_EQUITY;
		this.setChanged();
		this.notifyObservers(is);
	}

	public void notifySimulations(int sims){
		state = NOTIFY_SIM;
		this.setChanged();
		this.notifyObservers(sims);
	}

	public void notifyFold(int player){
		state = NOTIFY_FOLD;
		this.setChanged();
		this.notifyObservers(player);
	}

	public void notifyPlayerCards(String playerCards){
		state = NOTIFY_PLAYER_CARDS_STRING;
		setChanged();
		notifyObservers(playerCards);
	}

	public int getState() {
		return state;
	}
}

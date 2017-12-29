package com.example.youss.pokertools.model.ObserverPatron;

import com.example.youss.pokertools.model.utils.EntryParser;

import java.util.Observable;

public class OSolution extends Observable{
	public static final int NOTIFY_EQUITY_PLAYER_CARDS = 0;
	public static final int NOTIFY_EQUITY = 1;
	public static final int NOTIFY_EQUITY_SIM = 2;
	public static final int NOTIFY_EQUITY_FOLD = 3;
	public static final int NOTIFY_PLAYER_CARDS_STRING = 4;
	public static final int NOTIFY_RANGE_CLEAR = 5;
	public static final int NOTIFY_RANGE_SELECT_ALL = 6;
	public static final int NOTIFY_RANGE_SELECT_SUITED = 7;
	public static final int NOTIFY_RANGE_SELECT_BROADWAY = 8;
	public static final int NOTIFY_RANGE_SELECT_PAIR = 9;
	public static final int NOTIFY_RANGE_CHANGE_RANKING = 10;
	public static final int NOTIFY_RANGE_PERSONAL_RANGE_REQUEST = 11;
	public static final int NOTIFY_RANGE_PERSONAL_RANGE_REPONSE = 12;
	public static final int NOTIFY_RANGE_GENERATE = 13;

	private int state = -1;

	public void notifyPlayerCards(OPlayerCards playerCards){
		state = NOTIFY_EQUITY_PLAYER_CARDS;
	    setChanged();
	    notifyObservers(playerCards);
    }

	public void notifyEquity(double[] is){
		state = NOTIFY_EQUITY;
		this.setChanged();
		this.notifyObservers(is);
	}

	public void notifySimulations(int sims){
		state = NOTIFY_EQUITY_SIM;
		this.setChanged();
		this.notifyObservers(sims);
	}

	public void notifyFold(int player){
		state = NOTIFY_EQUITY_FOLD;
		this.setChanged();
		this.notifyObservers(player);
	}

	public void notifyPlayerCards(String playerCards){
		state = NOTIFY_PLAYER_CARDS_STRING;
		setChanged();
		notifyObservers(playerCards);
	}

	public void notifyClear(){
		state = NOTIFY_RANGE_CLEAR;
		setChanged();
		notifyObservers();
	}

	public void notifySelectAll(){
		state = NOTIFY_RANGE_SELECT_ALL;
		setChanged();
		notifyObservers();
	}

	public void notifySelectSuited(){
		state = NOTIFY_RANGE_SELECT_SUITED;
		setChanged();
		notifyObservers();
	}

	public void notifySelectBroadway(){
		state = NOTIFY_RANGE_SELECT_BROADWAY;
		setChanged();
		notifyObservers();
	}

	public void notifySelectPair(){
		state = NOTIFY_RANGE_SELECT_PAIR;
		setChanged();
		notifyObservers();
	}

	public void notifyChangeRanking(boolean ranking){
		state = NOTIFY_RANGE_CHANGE_RANKING;
		setChanged();
		notifyObservers(ranking);
	}

	public void notifyPersonalRangeRequest(){
		state = NOTIFY_RANGE_PERSONAL_RANGE_REQUEST;
		setChanged();
		notifyObservers();
	}

	public void notifyPersonalRangeReponse(EntryParser parser){
		state = NOTIFY_RANGE_PERSONAL_RANGE_REPONSE;
		setChanged();
		notifyObservers(parser);
	}

	public void notifyGenerate(){
		state = NOTIFY_RANGE_GENERATE;
		setChanged();
		notifyObservers();
	}

	public int getState() {
		return state;
	}
}

package model.ioFiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import model.representation.Card;
import model.representation.Suit;

public abstract class FileInputReader {

//public:
	//ctor:
	protected FileInputReader(String path) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(path));
		nBoardCards = 0;
		nPlayers = 0;
	}
	
	protected FileInputReader() {
		nBoardCards = 0;
		nPlayers = 0;
	}
	

	//getters&setters:
	public int getNPlayers () {
		return nPlayers;
	}

	public int getNBoardCards () {
		return nBoardCards;
	}
	
	
	//methods:
	public boolean open (String path) {
		try {
			if (reader != null)
				reader.close();
			reader = new BufferedReader(new FileReader(path));
		} catch (IOException e) {
			return false;
		}
		return true;
	}	
	
	//Reads next line, updating any data necessary. 
	//Returns false if there is an error, and doesn't change any data.
	public boolean readNext () { 
		boolean res = true;
		String aux = null;
		try {
			aux = reader.readLine();
		} catch (IOException e) {
			res = false;
		}
		if (aux == null) 
			res = false;
		else {
			String save [] = currLine;
			//---
			this.currWholeLine = aux;
			//---
			currLine = aux.split(";");
			res = resetLine();
			if (!res) 
				currLine = save;
		}
		return res;
	}

	public String getCurrWholeLine(){
		return this.currWholeLine;
	}


	//abstract:
	public abstract Card getBoardCard();
	
	public abstract Card getPlayerCard (int i);
	/**
	 * Set number of players and board cards.
	 */
	public abstract boolean setN();
	
	
//private:
	//fields:
	private BufferedReader reader;
	
	protected String currLine [];
	protected String currWholeLine;
	protected int nPlayers;
	protected int nBoardCards;
	
	
	//methods:
	//Converts a 2 char String into a Card.
	//Returns the Card if it succeeds or null if there is any error.
	protected Card readCard (char fst, char snd) {
		//Converts a string into a card. Returns null if there is any error.
		
		Card card = null;			
		int value = Card.charToValue(fst);
		if (value != -1) {	
			
			Suit suit = Suit.getFromChar(snd);
			if (suit != null) {
				try {			
					
					card = new Card(value, suit);
				} catch (Exception e) {}
			}
		}
		
		return card;
	}
	
	
	//abstract
	protected abstract boolean resetLine();
}

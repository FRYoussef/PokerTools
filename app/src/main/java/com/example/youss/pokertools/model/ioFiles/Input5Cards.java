package model.ioFiles;

import java.io.FileNotFoundException;

import model.representation.Card;

public class Input5Cards extends FileInputReader{

//public:
	//ctor:
	public Input5Cards(String path) throws FileNotFoundException {
		super(path);
		iCard = 0;
		nBoardCards = 5;
	}
	
	public Input5Cards() {
		super();
		iCard = 0;
		nBoardCards = 5;
	}
	
	
	//methods:
	@Override
	public Card getBoardCard() {
		Card res = null;
		if (iCard < nBoardCards) {	
			int pos = iCard*2;
			res = readCard(currLine[0].charAt(pos), currLine[0].charAt(pos+1));
			iCard++;
		}
		return res;
	}

	@Override
	public Card getPlayerCard(int i) {
		return getBoardCard();
	}

	
	
//private:
	//fields:
	private int iCard;

	
	//methods:
	@Override
	protected boolean resetLine() {
		iCard = 0;
		return true;
	}

	@Override
	public boolean setN() {
		this.nPlayers = 0;
		this.nBoardCards = 5;
		return true;
	}
	
}

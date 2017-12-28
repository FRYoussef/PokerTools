package model.ioFiles;

import java.io.FileNotFoundException;

import model.representation.Card;

public class InputNPlayerHE extends FileInputReader {

//public:
	//ctor:
	public InputNPlayerHE(String path) throws FileNotFoundException {
		super(path);
		nBoardCards = 5;
	}
	
	public InputNPlayerHE() {
		super();
		nBoardCards = 5;
	}
	
	
	//methods:
	@Override
	public Card getBoardCard() {
		Card res = null;
		if (iBoardCard < nBoardCards) {	
			int pos = iBoardCard*2;
			res = readCard(currLine[nPlayers+1].charAt(pos), currLine[nPlayers+1].charAt(pos+1));
			iBoardCard++;
		}
		return res;
	}

	@Override
	public Card getPlayerCard(int i) {
		Card res = null;
		if (iPlayerCard[i] < 2) {
			int pos = iPlayerCard[i]*2 + 2;
			res = readCard(currLine[i+1].charAt(pos), currLine[i+1].charAt(pos+1));
			iPlayerCard[i]++;
		}
		return res;
	}

	
	
//private
	//fields:
	private int iBoardCard;
	private int iPlayerCard [];
	
	
	//methods:
	@Override
	protected boolean resetLine() {
		try {
			nPlayers = Integer.parseInt(currLine[0]);
		}catch (NumberFormatException e) {return false;}
		iBoardCard = 0;
		iPlayerCard = new int [nPlayers];
		return true;

	}

	@Override
	public boolean setN() {
		try{
			this.nPlayers = Integer.parseInt(this.currLine[0]);
		}catch (NumberFormatException e){return false;}
		return true;
	}

}

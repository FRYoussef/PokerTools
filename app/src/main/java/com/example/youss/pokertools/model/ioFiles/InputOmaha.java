package model.ioFiles;

import java.io.FileNotFoundException;

import model.representation.Card;

public class InputOmaha extends FileInputReader {

//public:
	//ctor:
	public InputOmaha(String path) throws FileNotFoundException {
		super(path);
		nPlayers = 1;
	}
	
	public InputOmaha() {
		super();
		nPlayers = 1;
	}
	
	
	//methods:
	@Override
	public Card getBoardCard() {
		Card res = null;
		if (iBoardCard < nBoardCards) {
			int pos = iBoardCard*2;
			res = readCard(currLine[2].charAt(pos), currLine[2].charAt(pos+1));
			iBoardCard++;
		}
		return res;
	}

	@Override
	public Card getPlayerCard(int i) {
		Card res = null;
		if (iPlayerCard < 4) {
			int pos = iPlayerCard*2;
			res = readCard(currLine[0].charAt(pos), currLine[0].charAt(pos+1));
			iPlayerCard++;
		}
		return res;
	}

	
	
//private:
	//fields:
	private int iPlayerCard;
	private int iBoardCard;
	
	
	//methods
	@Override
	protected boolean resetLine() {
		try {
			nBoardCards = Integer.parseInt(currLine[1]);
		}catch (NumberFormatException e) {return false;}
		iBoardCard = 0;
		iPlayerCard = 0;
		return true;
	}

	@Override
	public boolean setN() {
		try{
			this.nBoardCards = Integer.parseInt(currLine[1]);
		}catch (NumberFormatException e){ return false;}

		return true;
	}

}

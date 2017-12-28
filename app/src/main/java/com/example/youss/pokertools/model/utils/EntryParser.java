package model.utils;

import java.util.ArrayList;

import model.representation.Card;
import model.representation.range.CoupleCards;
import model.representation.range.Range;

public class EntryParser {
	private String [] entry; 
	private ArrayList<Range> rangeEntry;
	private static final String REGEX = ",";
	
	public EntryParser(){}
	
	public EntryParser(String s){
		//remove all whitespaces 
		s = s.replaceAll("\\s+","");
		this.entry = s.split(EntryParser.REGEX);
		this.rangeEntry = new ArrayList<>(this.entry.length);
	}
	/**
	 * It parses entry 
	 * @return true if it is correct. Otherwise, it'll return null 
	 */
	public boolean parseEntry(){
		this.rangeEntry.clear();
		String s; 
		for (int i = 0; i < this.entry.length; i++){
			s = this.entry[i]; 
			Range r;
			CoupleCards atomicRank = null; 
			if(s.length() < 2 || s.length() > 7 || s.length() == 5)
				return false;
			atomicRank = this.parseCoupleCards(s);
			if(atomicRank != null)
				this.rangeEntry.add(new Range(atomicRank, null, false, false));
			else{
				r = this.parsePlus(s);
				if(r != null)
					this.rangeEntry.add(r);
				else{
					r = this.parseHyphen(s);
					if(r != null)
						this.rangeEntry.add(r);
					else
						return false;
				}
			}
		}
		return true;
	}
	
	public ArrayList<Range> getRangeEntry(){
		return this.rangeEntry;
	}
	/**
	 * It parses hyphen format entry
	 * @param entry
	 * @return the proper Range or null if entry isn't correct
	 */
	private Range parseHyphen(String entry){
		if(entry.length() != 7)
			return null; 
		if(entry.charAt(3) != '-')
			return null;
		String [] cards = entry.split("-");
		CoupleCards cp1 = this.parseCoupleCards(cards[0]);
		CoupleCards cp2 = this.parseCoupleCards(cards[1]);
		if(cp1 == null || cp2 == null)
			return null; 
		else{
			if(cp1.getHigherValue() != cp2.getHigherValue() || cp1.isOffSuited() != cp2.isOffSuited()
					|| cp1.getLowerValue() < cp2.getLowerValue())
				return null;
			else
				return new Range(cp1, cp2, true, false);
		}
	}
	/**
	 * It parses entries with + operator
	 * @param entry
	 * @return null if there is an error or the proper range if it's correct
	 */
	private Range parsePlus(String entry){
		int l = entry.length();
		if(l != 3 && l != 4)
			return null;
		if(l == 3 && entry.charAt(2) != '+')
			return null;
		else if(l == 4 && entry.charAt(3) != '+')
			return null;
		CoupleCards cp = this.parseCoupleCards(entry.substring(0, l - 1));
		if (cp == null)
			return null; 
		else
			return new Range(cp, null, false, true);
	}
	/**
	 * It parses a single couple of cards
	 * @param entry
	 * @return null if entry isn't correct. Otherwise, it will return the proper CoupleCards
	 * object
	 */
	private CoupleCards parseCoupleCards(String entry){
		int l = entry.length();
		if(l != 2 && l != 3)
			return null;
		Integer c1, c2;
		Boolean suit; 
		c1 = this.correctChar(entry.charAt(0));
		c2 = this.correctChar(entry.charAt(1));
		if(c1 == null || c2 == null)
			return null; 
		if(l == 2 && c1 == c2)
			return new CoupleCards(c1, c2);
		else if(l == 3 && (suit = this.correctSuit(entry.charAt(2))) != null){
			if(c1 > c2)
				return new CoupleCards(c1, c2, suit);
			else
				return new CoupleCards(c2, c1, suit);
		}
		else
			return null;
	}
	/**
	 * It parses a single char.
	 * @param c
	 * @return int value of c or null if c isn't correct.
	 */
	private Integer correctChar(char c){
	   	Integer res = Card.charToValue(c);
	   	if(res == -1)
	  		return null; 
	   	else 
  			return res;
    }
	/**
	 * It parses suit char
	 * @param c char that represents suit
	 * @return true or false if suit char is correct. Otherwise, it will return null
	 */
	private Boolean correctSuit(char c){
		if(c == 'o')
			return true;

		else if(c == 's')
			return false;

		else
			return null;
	}
}

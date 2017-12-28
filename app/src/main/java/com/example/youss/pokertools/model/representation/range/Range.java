package model.representation.range;

import model.representation.Card;

import java.util.ArrayList;
import java.util.Collections;

public class Range {

    private static final ArrayList<Range> SKLANSKY = initSklansky();
    private static final ArrayList<Range> STRENGTH = initStrength();

    private CoupleCards coupleCards1 = null;
    private CoupleCards coupleCards2 = null;

    private boolean range = false; /** like A9s-A7s */
    private boolean highRange = false; /** like JJ+ */

    public Range() { }

    public Range(CoupleCards coupleCards1) {
        this.coupleCards1 = coupleCards1;
    }

    public Range(CoupleCards coupleCards1, CoupleCards coupleCards2, boolean range, boolean highRange) {
        this.coupleCards1 = coupleCards1;
        this.coupleCards2 = coupleCards2;
        this.range = range;
        this.highRange = highRange;
    }

    public CoupleCards getCoupleCards1() {
        return coupleCards1;
    }

    public CoupleCards getCoupleCards2() {
        return coupleCards2;
    }

    public boolean isRange() {
        return range;
    }

    public boolean isHighRange() {
        return highRange;
    }

    public void setCoupleCards1(CoupleCards coupleCards1) {
        this.coupleCards1 = coupleCards1;
    }

    public void setCoupleCards2(CoupleCards coupleCards2) {
        this.coupleCards2 = coupleCards2;
    }

    public void setRange(boolean range) {
        this.range = range;
    }

    public void setHighRange(boolean highRange) {
        this.highRange = highRange;
    }


    /**
     * It tranform a range to couple cards
     * @param ranges
     * @return an array of couple cards
     */
    public static ArrayList<CoupleCards> rangeToCoupleCards(ArrayList<Range> ranges){
        ArrayList<CoupleCards> coupleCards = new ArrayList<>(ranges.size());
        for(Range r: ranges){
            if(r.getCoupleCards2() == null && !r.isHighRange())
                coupleCards.add(r.getCoupleCards1());
            else if(r.isRange()){
                for(int i = r.getCoupleCards2().getLowerValue(); i <= r.getCoupleCards1().getLowerValue(); i++)
                    coupleCards.add(new CoupleCards(r.getCoupleCards2().getHigherValue(), i, r.getCoupleCards2().isOffSuited()));
            }
            else if(r.isHighRange()){
                if(r.getCoupleCards1().isPair()){
                    for(int i = r.getCoupleCards1().getHigherValue(); i <= 12; i++)
                        coupleCards.add(new CoupleCards(i, i));
                }
                else{
                    CoupleCards cp;
                    for(int i = r.getCoupleCards1().getLowerValue(); i < r.getCoupleCards1().getHigherValue(); i++) {
                        if (r.getCoupleCards1().isOffSuited())
                            cp = new CoupleCards(r.getCoupleCards1().getHigherValue(), i);
                        else
                            cp = new CoupleCards(i, r.getCoupleCards1().getHigherValue());
                        coupleCards.add(cp);
                    }
                }
            }
        }
        return coupleCards;
    }

    public static ArrayList getRangeArraySklansky(int rangPercentage) throws Exception{
        ArrayList<Range> range = new ArrayList();
        for(int i = 0; i<((rangPercentage*SKLANSKY.size())/100);i++)
            range.add(SKLANSKY.get(i));
        return range;
    }

    public static ArrayList getRangeArrayStrength(int rangPercentage) throws Exception{
        ArrayList<Range> range = new ArrayList();
        for(int i = 0; i<((rangPercentage*STRENGTH.size())/100);i++)
            range.add(STRENGTH.get(i));
        return range;
    }

    /**
     * It creates a range from two couples of cards.
     * @param c1 first couple of cards
     * @param c2 second couple of cards
     * @return null if there is no union, the range otherwise
     */
    public static Range union(CoupleCards c1, CoupleCards c2){
        CoupleCards cp1 = c1.compareTo(c2) >= 0 ? c1 : c2;
        CoupleCards cp2 = c1.compareTo(c2) < 0 ? c1 : c2;

        if(c1.isPair() || c2.isPair()){
            if(cp1.isPair() && cp2.isPair()){
                if(cp1.getHigherValue() == Card.NUM_CARDS-1 && cp2.getHigherValue() == Card.NUM_CARDS-2)
                    return new Range(cp1, null, false, true);

                return null;
            }
            return null;
        }
        /** 97o, 86s */
        if(c1.getHigherValue() != c2.getHigherValue() || c1.isOffSuited() != c2.isOffSuited())
            return null;

        if(Math.abs(c1.getLowerValue() - c2.getLowerValue()) == 1){
            /** 54o-53o => 53o+  */
            if(Math.abs(cp1.getHigherValue() - cp1.getLowerValue()) == 1)
                return new Range(cp2, null, false, true);

            return new Range(c1, c2, true, false);
        }

        return null;
    }

    /**
     * It creates a range from a couples of cards and from a range.
     * @param r the range
     * @param c first couple of cards
     * @return null if there is no union, the new range otherwise
     */
    public static Range union(Range r, CoupleCards c){
        CoupleCards cp1 = r.getCoupleCards1().compareTo(c) >= 0 ? r.getCoupleCards1() : c;
        CoupleCards cp2 = r.getCoupleCards1().compareTo(c) < 0 ? r.getCoupleCards1() : c;

        if(r.getCoupleCards1().isPair() || c.isPair()){
            if(r.getCoupleCards1().isPair() && c.isPair()){
                if(r.isHighRange() && Math.abs(r.getCoupleCards1().getHigherValue() - c.getHigherValue()) == 1){
                    r.setCoupleCards1(c);
                    return r;
                }
                else if(cp1.getHigherValue() == Card.NUM_CARDS-1
                        && Math.abs(cp1.getHigherValue() - cp2.getHigherValue()) == 1)
                {
                    r.setHighRange(true);
                    r.setCoupleCards1(cp2);
                    return r;
                }
                else
                    return null;
            }
            return null;
        }
        /** 97o, 86s */
        if(r.getCoupleCards1().getHigherValue() != c.getHigherValue()
                || r.getCoupleCards1().isOffSuited() != c.isOffSuited())
            return null;

        if(!r.isHighRange() && !r.isRange() && Math.abs(r.getCoupleCards1().getLowerValue() - c.getLowerValue()) == 1){
            if(Math.abs(cp1.getHigherValue() - cp1.getLowerValue()) == 1){
                r.setCoupleCards1(cp2);
                r.setCoupleCards2(null);
                r.setRange(false);
                r.setHighRange(true);
                return r;
            }

            r.setCoupleCards2(cp2);
            r.setRange(true);
            return r;
        }

        if(r.isRange()){
            /** the couple of cards are above the range */
            if(Math.abs(r.getCoupleCards1().getLowerValue() - c.getLowerValue()) == 1){
                if(Math.abs(c.getHigherValue()-c.getLowerValue()) == 1){
                    r.setCoupleCards1(r.getCoupleCards2());
                    r.setCoupleCards2(null);
                    r.setRange(false);
                    r.setHighRange(true);
                    return r;
                }
                r.setCoupleCards1(c);
                return r;
            }
            /** the couple of cards are below the range */
            else if(Math.abs(r.getCoupleCards2().getLowerValue() - c.getLowerValue()) == 1){
                r.setCoupleCards2(c);
                return r;
            }
            return null;
        }

        if(r.isHighRange()){
            if(Math.abs(r.getCoupleCards1().getLowerValue() - c.getLowerValue()) == 1){
                r.setCoupleCards1(c);
                return r;
            }
        }

        return null;
    }

    /**
     *
     * @param couples
     * @return
     */
    public static String getRanks(String [] couples){
        ArrayList<CoupleCards> cp = new ArrayList<>(10);
        ArrayList<Range> ranges = new ArrayList<>(10);

        for (String c: couples){
            boolean o = false;
            if(c.length() == 3)
                o = c.charAt(2) == 'o' ? true : false;
            CoupleCards cop = new CoupleCards(Card.charToValue(c.charAt(0)), Card.charToValue(c.charAt(1)), o);
            cp.add(cop);
        }

        Collections.sort(cp, Collections.reverseOrder());
        Range r = new Range(cp.get(0));
        for(int i = 1; i < cp.size(); i++){
            Range aux = Range.union(r, cp.get(i));
            if(aux != null)
                r = aux;
            else{
                ranges.add(r);
                r = new Range(cp.get(i));
            }
            aux = null;
        }
        // last value
        if(r != null)
            ranges.add(r);

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < ranges.size()-1; i++){
            sb.append(ranges.get(i) + ", ");
        }
        sb.append(ranges.get(ranges.size()-1));
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(coupleCards1.toString());
        if(range && coupleCards2 != null)
            sb.append("-" + coupleCards2.toString());
        else if(highRange)
            sb.append("+");
        return sb.toString();
    }

    private static ArrayList<Range> initSklansky(){
        ArrayList<Range> s = new ArrayList<>(169);

        s.add(new Range(new CoupleCards(12,12)));
        s.add(new Range(new CoupleCards(11,11)));
        s.add(new Range(new CoupleCards(12,11,false)));
        s.add(new Range(new CoupleCards(10,10)));
        s.add(new Range(new CoupleCards(12,11, true)));
        s.add(new Range(new CoupleCards(9,9)));
        s.add(new Range(new CoupleCards(12,10,false)));
        s.add(new Range(new CoupleCards(8,8)));
        s.add(new Range(new CoupleCards(12,10,true)));
        s.add(new Range(new CoupleCards(7,7)));
        s.add(new Range(new CoupleCards(12,9,false)));
        s.add(new Range(new CoupleCards(6,6)));
        s.add(new Range(new CoupleCards(12,8,false)));
        s.add(new Range(new CoupleCards(12,9,true)));
        s.add(new Range(new CoupleCards(5,5)));
        s.add(new Range(new CoupleCards(4,4)));
        s.add(new Range(new CoupleCards(12,8,true)));
        s.add(new Range(new CoupleCards(12,7, false)));
        s.add(new Range(new CoupleCards(3,3)));
        s.add(new Range(new CoupleCards(11,10,false)));
        s.add(new Range(new CoupleCards(12,6,false)));
        s.add(new Range(new CoupleCards(2,2)));
        s.add(new Range(new CoupleCards(12,7,true)));
        s.add(new Range(new CoupleCards(12,5,false)));
        s.add(new Range(new CoupleCards(12,8,true)));
        s.add(new Range(new CoupleCards(11,9,false)));/*A8o*/
        s.add(new Range(new CoupleCards(12,6,true)));
        s.add(new Range(new CoupleCards(12,3,false)));
        s.add(new Range(new CoupleCards(12,4,false)));
        s.add(new Range(new CoupleCards(12,2,false)));
        s.add(new Range(new CoupleCards(1,1)));
        s.add(new Range(new CoupleCards(11,8,false)));
        s.add(new Range(new CoupleCards(12,5,true)));
        s.add(new Range(new CoupleCards(12,1,false)));
        s.add(new Range(new CoupleCards(11,10,true)));
        s.add(new Range(new CoupleCards(12,0,false)));
        s.add(new Range(new CoupleCards(12,3,true)));
        s.add(new Range(new CoupleCards(12,4,true)));
        s.add(new Range(new CoupleCards(12,2,true)));
        s.add(new Range(new CoupleCards(11,9,true)));
        s.add(new Range(new CoupleCards(10,9,false)));
        s.add(new Range(new CoupleCards(12,1,true)));
        s.add(new Range(new CoupleCards(0,0)));
        s.add(new Range(new CoupleCards(11,7,false)));
        s.add(new Range(new CoupleCards(12,0,true)));
        s.add(new Range(new CoupleCards(11,8,true)));/*QTs*/
        s.add(new Range(new CoupleCards(10,8,false)));
        s.add(new Range(new CoupleCards(10,9,false)));
        s.add(new Range(new CoupleCards(11,6,false)));
        s.add(new Range(new CoupleCards(11,5,false)));
        s.add(new Range(new CoupleCards(9,8,false)));
        s.add(new Range(new CoupleCards(11,7,true)));
        s.add(new Range(new CoupleCards(11,4,false)));
        s.add(new Range(new CoupleCards(10,9,true)));
        s.add(new Range(new CoupleCards(10,7,false)));
        s.add(new Range(new CoupleCards(11,3,false)));
        s.add(new Range(new CoupleCards(11,6,true)));
        s.add(new Range(new CoupleCards(11,2,false)));/*QTo*/
        s.add(new Range(new CoupleCards(10,8,true)));
        s.add(new Range(new CoupleCards(11,5,true)));
        s.add(new Range(new CoupleCards(11,1,false)));
        s.add(new Range(new CoupleCards(11,0,false)));
        s.add(new Range(new CoupleCards(10,6,false)));
        s.add(new Range(new CoupleCards(11,4,true)));
        s.add(new Range(new CoupleCards(9,7,false)));
        s.add(new Range(new CoupleCards(11,3,true)));
        s.add(new Range(new CoupleCards(10,7,true)));
        s.add(new Range(new CoupleCards(9,8,true)));
        s.add(new Range(new CoupleCards(11,2,true)));
        s.add(new Range(new CoupleCards(10,6,false)));/*Q7s*/
        s.add(new Range(new CoupleCards(10,5,false)));
        s.add(new Range(new CoupleCards(8,7,false)));
        s.add(new Range(new CoupleCards(10,4,false)));
        s.add(new Range(new CoupleCards(11,1,true)));
        s.add(new Range(new CoupleCards(9,6,false)));
        s.add(new Range(new CoupleCards(10,3,false)));
        s.add(new Range(new CoupleCards(11,0,true)));
        s.add(new Range(new CoupleCards(10,6,true)));
        s.add(new Range(new CoupleCards(10,2,false)));
        s.add(new Range(new CoupleCards(10,1,false)));
        s.add(new Range(new CoupleCards(9,7,true)));
        s.add(new Range(new CoupleCards(8,6,false)));
        s.add(new Range(new CoupleCards(9,5,false)));/*Q7o*/
        s.add(new Range(new CoupleCards(10,5,true)));
        s.add(new Range(new CoupleCards(10,0,false)));
        s.add(new Range(new CoupleCards(10,4,true)));
        s.add(new Range(new CoupleCards(7,6,false)));
        s.add(new Range(new CoupleCards(10,3,true)));
        s.add(new Range(new CoupleCards(8,7,true)));
        s.add(new Range(new CoupleCards(9,6,true)));
        s.add(new Range(new CoupleCards(9,4,false)));
        s.add(new Range(new CoupleCards(9,3,false)));/*T7s*/
        s.add(new Range(new CoupleCards(8,5,false)));
        s.add(new Range(new CoupleCards(8,5,true)));
        s.add(new Range(new CoupleCards(9,2,true)));/*Q4o*/
        s.add(new Range(new CoupleCards(10,2,true)));
        s.add(new Range(new CoupleCards(9,2,false)));
        s.add(new Range(new CoupleCards(9,5,true)));
        s.add(new Range(new CoupleCards(10,1,true)));
        s.add(new Range(new CoupleCards(7,5,false)));
        s.add(new Range(new CoupleCards(8,6,true)));
        s.add(new Range(new CoupleCards(9,1,false)));
        s.add(new Range(new CoupleCards(8,4,false)));
        s.add(new Range(new CoupleCards(10,0,true)));
        s.add(new Range(new CoupleCards(9,0,false)));
        s.add(new Range(new CoupleCards(6,5,false)));
        s.add(new Range(new CoupleCards(9,4,true)));
        s.add(new Range(new CoupleCards(7,6,true)));/*T7o*/
        s.add(new Range(new CoupleCards(8,5,true)));
        s.add(new Range(new CoupleCards(7,4,false)));
        s.add(new Range(new CoupleCards(9,3,true)));
        s.add(new Range(new CoupleCards(8,3,false)));
        s.add(new Range(new CoupleCards(8,2,false)));
        s.add(new Range(new CoupleCards(6,4,false)));
        s.add(new Range(new CoupleCards(9,2,true)));
        s.add(new Range(new CoupleCards(8,1,false)));
        s.add(new Range(new CoupleCards(7,5,true)));
        s.add(new Range(new CoupleCards(8,4,true)));
        s.add(new Range(new CoupleCards(7,3,false)));
        s.add(new Range(new CoupleCards(5,4,false)));
        s.add(new Range(new CoupleCards(9,1,true)));
        s.add(new Range(new CoupleCards(8,0,false)));
        s.add(new Range(new CoupleCards(6,5,true)));
        s.add(new Range(new CoupleCards(6,3,false)));
        s.add(new Range(new CoupleCards(7,4,true)));
        s.add(new Range(new CoupleCards(9,0,true)));
        s.add(new Range(new CoupleCards(8,3,true)));
        s.add(new Range(new CoupleCards(7,2,false)));
        s.add(new Range(new CoupleCards(5,3,false)));
        s.add(new Range(new CoupleCards(8,2,true)));
        s.add(new Range(new CoupleCards(4,3,false)));
        s.add(new Range(new CoupleCards(7,1,false)));
        s.add(new Range(new CoupleCards(6,4,true)));
        s.add(new Range(new CoupleCards(6,2,false)));
        s.add(new Range(new CoupleCards(7,3,true)));
        s.add(new Range(new CoupleCards(5,4,true)));
        s.add(new Range(new CoupleCards(8,1,true)));
        s.add(new Range(new CoupleCards(7,0,false)));
        s.add(new Range(new CoupleCards(5,2,false)));
        s.add(new Range(new CoupleCards(6,3,true)));
        s.add(new Range(new CoupleCards(8,0,true)));
        s.add(new Range(new CoupleCards(3,2,false)));
        s.add(new Range(new CoupleCards(4,2,false)));
        s.add(new Range(new CoupleCards(6,1,false)));
        s.add(new Range(new CoupleCards(5,3,true)));
        s.add(new Range(new CoupleCards(7,2,true)));
        s.add(new Range(new CoupleCards(6,0,false)));
        s.add(new Range(new CoupleCards(5,1,false)));
        s.add(new Range(new CoupleCards(7,1,true)));
        s.add(new Range(new CoupleCards(4,3,true)));
        s.add(new Range(new CoupleCards(3,1,false)));
        s.add(new Range(new CoupleCards(4,1,false)));
        s.add(new Range(new CoupleCards(6,2,true)));
        s.add(new Range(new CoupleCards(7,0,true)));
        s.add(new Range(new CoupleCards(2,1,false)));
        s.add(new Range(new CoupleCards(5,0,false)));
        s.add(new Range(new CoupleCards(5,2,true)));
        s.add(new Range(new CoupleCards(3,2,true)));
        s.add(new Range(new CoupleCards(4,0,false)));
        s.add(new Range(new CoupleCards(3,0,false)));
        s.add(new Range(new CoupleCards(4,2,true)));
        s.add(new Range(new CoupleCards(6,1,true)));
        s.add(new Range(new CoupleCards(2,0,false)));
        s.add(new Range(new CoupleCards(6,0,true)));
        s.add(new Range(new CoupleCards(5,1,true)));
        s.add(new Range(new CoupleCards(4,1,true)));
        s.add(new Range(new CoupleCards(3,1,true)));
        s.add(new Range(new CoupleCards(1,0,false)));
        s.add(new Range(new CoupleCards(2,1,true)));
        s.add(new Range(new CoupleCards(5,0,true)));
        s.add(new Range(new CoupleCards(4,0,true)));
        s.add(new Range(new CoupleCards(3,0,true)));
        s.add(new Range(new CoupleCards(2,0,true)));
        s.add(new Range(new CoupleCards(1,0,true)));
        return s;
    }

    private static ArrayList<Range> initStrength(){
        ArrayList<Range> strength = new ArrayList<>(169);

        strength.add(new Range(new CoupleCards(12,12)));
        strength.add(new Range(new CoupleCards(11,11)));
        strength.add(new Range(new CoupleCards(10,10)));
        strength.add(new Range(new CoupleCards(12,11, false)));
        strength.add(new Range(new CoupleCards(9,9)));
		strength.add(new Range(new CoupleCards(12,10, false)));
		strength.add(new Range(new CoupleCards(11,10, false)));
		strength.add(new Range(new CoupleCards(12,9,false)));
		strength.add(new Range(new CoupleCards(11,9, false)));
		strength.add(new Range(new CoupleCards(8,8)));
		strength.add(new Range(new CoupleCards(12,11, true)));
		strength.add(new Range(new CoupleCards(12,8,false)));
		strength.add(new Range(new CoupleCards(10,9,false)));
		strength.add(new Range(new CoupleCards(11,8,false)));
		strength.add(new Range(new CoupleCards(10,8,false)));
		strength.add(new Range(new CoupleCards(9,8,false)));
		strength.add(new Range(new CoupleCards(7,7)));
		strength.add(new Range(new CoupleCards(12,10, true)));
		strength.add(new Range(new CoupleCards(12,7, false)));
		strength.add(new Range(new CoupleCards(11,10,true)));
		strength.add(new Range(new CoupleCards(6,6)));
		strength.add(new Range(new CoupleCards(11,7,false)));
		strength.add(new Range(new CoupleCards(8,7,false)));
		strength.add(new Range(new CoupleCards(12,6,false)));
		strength.add(new Range(new CoupleCards(10,7,false)));
		strength.add(new Range(new CoupleCards(9,7,false)));
		strength.add(new Range(new CoupleCards(12,9,true)));
		strength.add(new Range(new CoupleCards(12,3,false)));
		strength.add(new Range(new CoupleCards(5,5)));
		strength.add(new Range(new CoupleCards(12,5, false)));
		strength.add(new Range(new CoupleCards(11,9, true)));
		strength.add(new Range(new CoupleCards(12,2,false)));
		strength.add(new Range(new CoupleCards(12,1,false)));
		strength.add(new Range(new CoupleCards(12,4,false)));
		strength.add(new Range(new CoupleCards(10,9,true)));
		strength.add(new Range(new CoupleCards(4,4)));
		strength.add(new Range(new CoupleCards(11,6,false)));
		strength.add(new Range(new CoupleCards(8,6,false)));
		strength.add(new Range(new CoupleCards(12,0,false)));
		strength.add(new Range(new CoupleCards(7,6,false)));
		strength.add(new Range(new CoupleCards(9,6,false)));
		strength.add(new Range(new CoupleCards(12,8,true)));
		strength.add(new Range(new CoupleCards(10,6,false)));
		strength.add(new Range(new CoupleCards(11,5,false)));
		strength.add(new Range(new CoupleCards(11,8,true)));
		strength.add(new Range(new CoupleCards(3,3)));
		strength.add(new Range(new CoupleCards(9,8,true)));
		strength.add(new Range(new CoupleCards(6,5,false)));
		strength.add(new Range(new CoupleCards(10,8,true)));
		strength.add(new Range(new CoupleCards(2,2)));

		strength.add(new Range(new CoupleCards(0,0)));
		strength.add(new Range(new CoupleCards(1,1)));
		strength.add(new Range(new CoupleCards(11,4,false)));
		strength.add(new Range(new CoupleCards(7,5,false)));
		strength.add(new Range(new CoupleCards(11,3,false)));
		strength.add(new Range(new CoupleCards(5,4,false)));
		strength.add(new Range(new CoupleCards(8,5,false)));
		strength.add(new Range(new CoupleCards(11,2,false)));
		strength.add(new Range(new CoupleCards(11,0,false)));
		strength.add(new Range(new CoupleCards(11,1,false)));
		strength.add(new Range(new CoupleCards(10,5,false)));
		strength.add(new Range(new CoupleCards(6,4,false)));
		strength.add(new Range(new CoupleCards(4,3,false)));
		strength.add(new Range(new CoupleCards(9,5,false)));
		strength.add(new Range(new CoupleCards(3,2,false)));
		strength.add(new Range(new CoupleCards(10,4,false)));
		strength.add(new Range(new CoupleCards(5,3,false)));
		strength.add(new Range(new CoupleCards(7,4,false)));
		strength.add(new Range(new CoupleCards(10,3,false)));
		strength.add(new Range(new CoupleCards(4,2,false)));
		strength.add(new Range(new CoupleCards(10,2,false)));
		strength.add(new Range(new CoupleCards(10,1,false)));
		strength.add(new Range(new CoupleCards(8,7,true)));
		strength.add(new Range(new CoupleCards(8,4,false)));
		strength.add(new Range(new CoupleCards(10,0,false)));
		strength.add(new Range(new CoupleCards(12,7,true)));
		strength.add(new Range(new CoupleCards(3,1,false)));
		strength.add(new Range(new CoupleCards(6,3,false)));
		strength.add(new Range(new CoupleCards(9,4,false)));
		strength.add(new Range(new CoupleCards(9,7,true)));
		strength.add(new Range(new CoupleCards(11,7,true)));
		strength.add(new Range(new CoupleCards(9,3,false)));
		strength.add(new Range(new CoupleCards(10,7,true)));
		strength.add(new Range(new CoupleCards(2,1,false)));
		strength.add(new Range(new CoupleCards(5,2,false)));
		strength.add(new Range(new CoupleCards(9,2,false)));
		strength.add(new Range(new CoupleCards(9,1,false)));
		strength.add(new Range(new CoupleCards(7,3,false)));
		strength.add(new Range(new CoupleCards(9,0,false)));
		strength.add(new Range(new CoupleCards(4,1,false)));
		strength.add(new Range(new CoupleCards(12,6,true)));
		strength.add(new Range(new CoupleCards(3,0,false)));
		strength.add(new Range(new CoupleCards(8,3,false)));
		strength.add(new Range(new CoupleCards(6,2,false)));
		strength.add(new Range(new CoupleCards(8,2,false)));
		strength.add(new Range(new CoupleCards(8,1,false)));
		strength.add(new Range(new CoupleCards(2,0,false)));
		strength.add(new Range(new CoupleCards(8,0,false)));
		strength.add(new Range(new CoupleCards(7,6,true)));
		strength.add(new Range(new CoupleCards(8,6,true)));

		strength.add(new Range(new CoupleCards(12,3,true)));
		strength.add(new Range(new CoupleCards(12,5,true)));
		strength.add(new Range(new CoupleCards(5,1,false)));
		strength.add(new Range(new CoupleCards(12,2,true)));
		strength.add(new Range(new CoupleCards(1,0,false)));
		strength.add(new Range(new CoupleCards(7,2,false)));
		strength.add(new Range(new CoupleCards(7,1,false)));
		strength.add(new Range(new CoupleCards(9,6,true)));
		strength.add(new Range(new CoupleCards(12,1,true)));
		strength.add(new Range(new CoupleCards(4,0,false)));
		strength.add(new Range(new CoupleCards(7,0,false)));
		strength.add(new Range(new CoupleCards(11,6,true)));
		strength.add(new Range(new CoupleCards(12,4,true)));
		strength.add(new Range(new CoupleCards(6,5,true)));
		strength.add(new Range(new CoupleCards(10,6,true)));
		strength.add(new Range(new CoupleCards(6,1,false)));
		strength.add(new Range(new CoupleCards(12,0,true)));
		strength.add(new Range(new CoupleCards(6,0,false)));
		strength.add(new Range(new CoupleCards(7,5,true)));
		strength.add(new Range(new CoupleCards(5,0,false)));
		strength.add(new Range(new CoupleCards(5,4,true)));
		strength.add(new Range(new CoupleCards(11,5,true)));
		strength.add(new Range(new CoupleCards(4,3,true)));
		strength.add(new Range(new CoupleCards(8,5,true)));
		strength.add(new Range(new CoupleCards(11,4,true)));
		strength.add(new Range(new CoupleCards(6,4,true)));
		strength.add(new Range(new CoupleCards(3,2,true)));
		strength.add(new Range(new CoupleCards(11,3,true)));
		strength.add(new Range(new CoupleCards(9,5,true)));
		strength.add(new Range(new CoupleCards(5,3,true)));
		strength.add(new Range(new CoupleCards(10,5,true)));
		strength.add(new Range(new CoupleCards(11,2,true)));
		strength.add(new Range(new CoupleCards(11,1,true)));
		strength.add(new Range(new CoupleCards(7,4,true)));
		strength.add(new Range(new CoupleCards(11,0,true)));
		strength.add(new Range(new CoupleCards(4,2,true)));
		strength.add(new Range(new CoupleCards(10,4,true)));
		strength.add(new Range(new CoupleCards(3,1,true)));
		strength.add(new Range(new CoupleCards(6,3,true)));
		strength.add(new Range(new CoupleCards(8,4,true)));
		strength.add(new Range(new CoupleCards(10,3,true)));
		strength.add(new Range(new CoupleCards(2,1,true)));
		strength.add(new Range(new CoupleCards(10,2,true)));
		strength.add(new Range(new CoupleCards(10,1,true)));
		strength.add(new Range(new CoupleCards(5,2,true)));
		strength.add(new Range(new CoupleCards(10,0,true)));
		strength.add(new Range(new CoupleCards(9,4,true)));
		strength.add(new Range(new CoupleCards(4,1,true)));
		strength.add(new Range(new CoupleCards(9,3,true)));
		strength.add(new Range(new CoupleCards(7,3,true)));

		strength.add(new Range(new CoupleCards(3,0,true)));
		strength.add(new Range(new CoupleCards(9,2,true)));
		strength.add(new Range(new CoupleCards(9,1,true)));
		strength.add(new Range(new CoupleCards(2,0,true)));
		strength.add(new Range(new CoupleCards(9,0,true)));
		strength.add(new Range(new CoupleCards(6,2,true)));
		strength.add(new Range(new CoupleCards(8,3,true)));
		strength.add(new Range(new CoupleCards(8,2,true)));
		strength.add(new Range(new CoupleCards(1,0,true)));
		strength.add(new Range(new CoupleCards(8,1,true)));
		strength.add(new Range(new CoupleCards(5,1,true)));
		strength.add(new Range(new CoupleCards(8,0,true)));
		strength.add(new Range(new CoupleCards(4,0,true)));
		strength.add(new Range(new CoupleCards(7,2,true)));
		strength.add(new Range(new CoupleCards(7,1,true)));
		strength.add(new Range(new CoupleCards(7,0,true)));
		strength.add(new Range(new CoupleCards(6,1,true)));
		strength.add(new Range(new CoupleCards(6,0,true)));
		strength.add(new Range(new CoupleCards(5,0,true)));

        return strength;
    }
}

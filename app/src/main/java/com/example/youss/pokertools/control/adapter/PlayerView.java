package com.example.youss.pokertools.control.adapter;

import android.content.Context;
import com.example.youss.pokertools.R;
import java.util.ArrayList;

/**
 * Created by Youss on 30/12/2017.
 */

public class PlayerView {

    private int player;
    private double equity;
    private boolean enabled;
    private ArrayList<String> cardsSrc;


    public PlayerView(int player) {
        this.player = player;
        equity = 0.000d;
        cardsSrc = new ArrayList<>(2);
        enabled = true;
    }

    public void reset(){
        equity = 0d;
        cardsSrc.clear();
        enabled = true;
    }

    public String getDrawableCard1(Context context){
        if(cardsSrc.size() == 0)
            return context.getString(R.string.back_card_resource);

        return cardsSrc.get(0);
    }

    public String getDrawableCard2(Context context){
        if(cardsSrc.size() == 0)
            return context.getString(R.string.back_card_resource);

        return cardsSrc.get(1);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCardSrc(int index, String cardSrc1) {
        cardsSrc.add(index, cardSrc1);
    }

    public int getPlayer() {
        return player;
    }

    public void setEquity(double equity) {
        this.equity = equity;
    }

    public double getEquity(){
        return equity;
    }
}

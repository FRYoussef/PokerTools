package com.example.youss.pokertools.control;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.youss.pokertools.R;
import com.example.youss.pokertools.control.adapter.PlayerAdapter;
import com.example.youss.pokertools.control.adapter.PlayerView;
import com.example.youss.pokertools.model.ObserverPatron.HandlerObserver;
import com.example.youss.pokertools.model.ObserverPatron.OPlayerCards;
import com.example.youss.pokertools.model.ObserverPatron.OSolution;
import com.example.youss.pokertools.model.processor.EquityProcessor;
import com.example.youss.pokertools.model.representation.Card;
import com.example.youss.pokertools.model.representation.Player;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EquityFragment extends Fragment implements Observer{

    private static final int PHASE_PREFLOP = 0;
    private static final int PHASE_FLOP = 1;
    private static final int PHASE_TURN = 2;
    private static final int PHASE_RIVER = 3;
    private static final int MAX_PLAYERS_HE = 9;
    private static final int DEFAULT_STOP_LIMIT = 2000000;
    private static final int DEFAULT_NUM_PLAYERS = 6;
    private static final int HE_NUM_CARDS = 2;
    private String PHASES[];


    private Unbinder unbinder;
    @BindView(R.id._rvPlayers) RecyclerView _rvPlayers;
    private RecyclerView.LayoutManager layoutManager;
    private PlayerAdapter playerAdapter;
    @BindView(R.id._llBoardCards) LinearLayout _llBoardCards;
    @BindView(R.id._tvPhase) TextView _tvPhase;
    @BindView(R.id._btCalculate) Button _btCalculate;
    @BindView(R.id._btStop) Button _btStop;
    @BindView(R.id._btNextPhase) Button _btNextPhase;
    @BindView(R.id._btReset) Button _btReset;
    @BindView(R.id._tvCrono) TextView _tvCrono;
    @BindView(R.id._tvSimu) TextView _tvSimu;

    private int numPlayers = DEFAULT_NUM_PLAYERS;
    private int remainPlayers = DEFAULT_NUM_PLAYERS;
    private int phase = PHASE_PREFLOP;
    private EquityProcessor equityProcessor;
    private int stopLimit = DEFAULT_STOP_LIMIT;
    private ArrayList<PlayerView> alPlayers;
    private boolean allowChangePlayers = true;
    private long initTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_equity, container, false);
        unbinder = ButterKnife.bind(this, v);

        equityProcessor = new EquityProcessor(MAX_PLAYERS_HE);
        alPlayers = new ArrayList<>(numPlayers);
        addPlayers();

        playerAdapter = new PlayerAdapter(getContext(), alPlayers);
        _rvPlayers.setAdapter(playerAdapter);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        _rvPlayers.setLayoutManager(layoutManager);

        PHASES = new String[]{getString(R.string.preflop), getString(R.string.flop), getString(R.string.turn), getString(R.string.river)};

        HandlerObserver.addObserver(this);
        return v;
    }

    /**
     * Adds players to the view, and the logic
     */
    private void addPlayers(){
        try{
            alPlayers.clear();
            equityProcessor.removeAllPlayers();
            for (int i = 0; i < numPlayers; i++) {
                alPlayers.add(new PlayerView(i+1));
                equityProcessor.addPlayer(new Player(i, HE_NUM_CARDS));
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Add player error", e.getMessage());
        }
    }

    @OnClick(R.id._btCalculate)
    public void onClickCalculate(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(remainPlayers == 0)
                    return;

                evaluatePhase();
                if(remainPlayers == 1){
                    //only 1 execution
                    for(Integer i : equityProcessor.getHmPlayer().keySet())
                        alPlayers.get(i).setEquity(1d);
                    _tvSimu.setText(getString(R.string.default_simu));
                    return;
                }
                if(phase == PHASE_RIVER){
                    equityProcessor.calculateFinalEquity(numPlayers, EquityProcessor.GAME_NLHE);
                    return;
                }
                enableForSim(false);
                initTime = System.currentTimeMillis();
                equityProcessor.calculateEquity(numPlayers, EquityProcessor.GAME_NLHE);
            }
        });
    }

    @OnClick(R.id._btStop)
    public void onClickStop(){
        equityProcessor.stopThreads();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableForSim(true);
            }
        });
    }

    @OnClick(R.id._btReset)
    public void onClickReset(){
        clear();
    }

    @OnClick(R.id._btNextPhase)
    public void onClickNextPhase(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                evaluatePhase();
                phase = ++phase %PHASES.length;
                _tvPhase.setText(PHASES[phase]);

                if(phase == 0)
                    clear();
                else if(phase == 1)
                    allowChangePlayers = false;
            }
        });
    }

    private void clear(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                equityProcessor.stopThreads();
                enableForSim(true);
                _tvSimu.setText(getString(R.string.default_simu));
                _tvCrono.setText(getString(R.string.default_crono));
                phase = 0;
                _tvPhase.setText(PHASES[phase]);
                for(PlayerView p : alPlayers)
                    p.reset();
                for(int i = 0; i < _llBoardCards.getChildCount(); i++) {
                    ((ImageView) _llBoardCards.getChildAt(i)).setImageResource(getResources()
                            .getIdentifier(getString(R.string.back_card_resource),
                                    "drawable", getActivity().getPackageName()));
                }
                equityProcessor.removeAllPlayers();
                try{
                    for(PlayerView p : alPlayers)
                        equityProcessor.addPlayer(new Player(p.getPlayer()-1, HE_NUM_CARDS));
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("Removing a player", e.getMessage());
                }
                equityProcessor.clearBoard();
                allowChangePlayers = true;
            }
        });
    }

    private void enableForSim(boolean b){
        _btCalculate.setEnabled(b);
        _btNextPhase.setEnabled(b);
        _btStop.setEnabled(!b);
        for(int i = 0; i < _llBoardCards.getChildCount(); i++)
            _llBoardCards.getChildAt(i).setEnabled(b);

        for(PlayerView p : alPlayers)
            p.setOnSim(b);
        playerAdapter.notifyDataSetChanged();
    }

    private void evaluatePhase(){
        int remainCards = 0;
        if(!equityProcessor.isPlayersGetCards(remainPlayers))
            equityProcessor.placeRemainingPlayerCards();

        if(phase == PHASE_FLOP)
            remainCards = 3;

        else if(phase == PHASE_TURN)
            remainCards = 4;

        else if(phase == PHASE_RIVER)
            remainCards = 5;

        try{
            for (int i = equityProcessor.numCardsBoard(); i < remainCards; i++)
                equityProcessor.getRandomBoardCard();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Error Phase", e.getMessage());
        }
    }

    private String getFormatCrono(long ms) {
        int dec = ((int)ms / 100) % 10;
        int sec = ((int)ms / 1000) % 60;
        int min = (int)ms / 1000 / 60;
        return (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec) + ":" + dec;
    }


    @Override
    public void update(final Observable arg0, final Object o) {
        OSolution sol = (OSolution) arg0;

        if(sol.getState() == OSolution.NOTIFY_EQUITY_EQUITY_PLAYERS){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    double [] eq = (double[]) o;
                    for(PlayerView p : alPlayers)
                        p.setEquity(eq[p.getPlayer()-1]);
                    playerAdapter.notifyDataSetChanged();
                }
            });
            return;
        }
        else if(sol.getState() == OSolution.NOTIFY_EQUITY_SIM){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(stopLimit != 0 && stopLimit <= (Integer)o){
                        equityProcessor.stopThreads();
                        enableForSim(true);
                    }
                    String number = getString(R.string.text_simu) + String.format("%,d", o);
                    _tvSimu.setText(number);
                    _tvCrono.setText(getFormatCrono(System.currentTimeMillis() - initTime));
                }
            });
            return;
        }
        else if(sol.getState() == OSolution.NOTIFY_EQUITY_PLAYER_CARDS){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OPlayerCards pc = (OPlayerCards)o;
                        if(pc.getNumPlayer() != -1)
                        {
                            addPlayerscards(pc.getCards(), pc.getNumPlayer());
                            equityProcessor.addPlayerCards(pc.getNumPlayer(), pc.getCards().toArray(new Card[pc.getCards().size()]));
                        }
                        else
                        {
                            equityProcessor.addBoardCards(pc.getCards());
                            addBoardCards(equityProcessor.getBoardCards());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Place cards", e.getMessage());
                    }
                }
            });
            return;
        }
        else if(sol.getState() == OSolution.NOTIFY_EQUITY_FOLD){
            try {
                remainPlayers--;
                equityProcessor.removePlayer((Integer)o);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error, fold player", e.getMessage());
            }
        }
    }

    private void addPlayerscards(final ArrayList<Card> cards, final int player) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(cards == null)
                    return;
                try{

                    for (int i = 0; i < cards.size(); i++){
                        String c = cards.get(i).getSuit().toString()
                                + Card.valueToCharLowerCase(cards.get(i).getValue());
                        alPlayers.get(player).setCardSrc(i, c);
                    }
                    playerAdapter.notifyItemChanged(player);

                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("", e.getMessage());
                }
            }
        });
    }

    private void addBoardCards(final ArrayList<Card> boardCards) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(boardCards == null)
                    return;
                try{

                    for (int i = 0; i < boardCards.size(); i++){
                        String c = boardCards.get(i).getSuit().toString()
                                + Card.valueToCharLowerCase(boardCards.get(i).getValue());
                        ((ImageView)_llBoardCards.getChildAt(i)).setImageResource(getResources()
                                .getIdentifier(c,"drawable", getActivity().getPackageName()));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("", e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HandlerObserver.removeObserver(this);
        unbinder.unbind();
    }
}

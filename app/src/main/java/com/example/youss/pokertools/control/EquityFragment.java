package com.example.youss.pokertools.control;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
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
import com.example.youss.pokertools.model.representation.game.Deck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
    private static final int DEFAULT_STOP_LIMIT = 100000;
    private static final int DEFAULT_NUM_PLAYERS = 6;
    private static final int HE_NUM_CARDS = 2;
    private String PHASES[];
    public static final String KEY_STOP_LIMIT = "key_stop_limit";
    public static final String KEY_NUM_PLAYER = "key_num_player";
    public static final String KEY_NUM_CARDS = "key_num_cards";
    public static final String KEY_DECK = "key_deck";
    public static final String KEY_PLAYER_CARDS = "key_player_cards";


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
    private StopLimitDialog stopLimitDialog;
    private SelectCardDialog selectCardDialog;
    private boolean onSim = false;

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
        stopLimitDialog = new StopLimitDialog();
        selectCardDialog = new SelectCardDialog();
        onClickBoardCard();
        HandlerObserver.addObserver(this);
        return v;
    }

    private void onClickBoardCard() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < _llBoardCards.getChildCount(); i++){
                    TextView tv = (TextView) _llBoardCards.getChildAt(i);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(phase == PHASE_PREFLOP) return;

                            Bundle b = new Bundle();
                            b.putParcelableArrayList(KEY_PLAYER_CARDS, equityProcessor.getBoardCards());
                            b.putParcelable(KEY_DECK, equityProcessor.getDeck());
                            b.putInt(KEY_NUM_PLAYER, -1);

                            int nCards = 3;
                            if(phase == PHASE_TURN) nCards = 4;
                            else if(phase == PHASE_RIVER) nCards = 5;

                            b.putInt(KEY_NUM_CARDS, nCards);
                            selectCardDialog.setArguments(b);
                            selectCardDialog.show(getFragmentManager(), "cards selector");
                        }
                    });
                }
            }
        });
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
                if(remainPlayers == 0) {
                    Snackbar.make(_btCalculate, getString(R.string.error_no_players), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                evaluatePhase();
                if(remainPlayers == 1){
                    for(PlayerView pl : alPlayers) {
                        if(pl.isOnFold())
                            pl.setEquity(1d);
                        else
                            pl.setEquity(0d);
                    }
                    playerAdapter.notifyDataSetChanged();
                    _tvSimu.setText(getString(R.string.default_simu));
                    _tvCrono.setText(getString(R.string.default_crono));
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
                remainPlayers = numPlayers;
                _tvPhase.setText(PHASES[phase]);

                for(int i = 0; i < _llBoardCards.getChildCount(); i++) {
                    ((ImageView) _llBoardCards.getChildAt(i)).setImageResource(getResources()
                            .getIdentifier(getString(R.string.back_card_resource),
                                    "drawable", getActivity().getPackageName()));
                }
                equityProcessor.removeAllPlayers();
                try{
                    for(PlayerView p : alPlayers) {
                        p.reset();
                        equityProcessor.addPlayer(new Player(p.getPlayer() - 1, HE_NUM_CARDS));
                    }
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
        onSim = !b;
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
                    String number = getString(R.string.text_simu) + " " + String.format("%,d", o);
                    _tvSimu.setText(number);
                    long time = System.currentTimeMillis() - initTime;
                    _tvCrono.setText(time <= 0 ? getString(R.string.default_crono) : getFormatCrono(time));
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
                        equityProcessor.removeCardsDeck(pc.getOuts());
                        equityProcessor.replaceCardsDeck(pc.getIns());
                        if (pc.getNumPlayer() != -1) {
                            addPlayerscards(pc.getCards(), pc.getNumPlayer());
                            equityProcessor.addPlayerCards(pc.getNumPlayer(), pc.getCards().toArray(new Card[pc.getCards().size()]));
                        } else {
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
        else if(sol.getState() == OSolution.NOTIFY_EQUITY_STOP_LIMIT){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(onSim){
                        Snackbar.make(_btCalculate, getString(R.string.error_onsim), Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if(o == null) {
                        Bundle b = new Bundle();
                        b.putInt(KEY_STOP_LIMIT, stopLimit);
                        stopLimitDialog.setArguments(b);
                        stopLimitDialog.show(getFragmentManager(), "stopLimit dialog");
                        return;
                    }

                    stopLimit = (Integer) o;
                }
            });
        }
        else if(sol.getState() == OSolution.NOTIFY_EQUITY_EQUITY_NUM_PLAYERS){
            if(o == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(phase != PHASE_PREFLOP){
                        Snackbar.make(_btCalculate, getString(R.string.error_not_onpreflop), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if(onSim){
                        Snackbar.make(_btCalculate, getString(R.string.error_onsim), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    int val = (Integer)o;
                    if(val == numPlayers)
                        return;
                    if(val > numPlayers){
                        for (int i = numPlayers; i < val; i++) {
                            alPlayers.add(new PlayerView(i+1));
                            playerAdapter.notifyItemInserted(i);
                            try {
                                equityProcessor.addPlayer(new Player(i, val));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else{
                        for (int i = numPlayers-1; i > val-1; i--) {
                            alPlayers.remove(i);
                            playerAdapter.notifyItemRemoved(i);
                            try {
                                equityProcessor.removePlayer(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    numPlayers = val;
                    remainPlayers = val;
                }
            });

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

    public static class StopLimitDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.alertdialog_stop_limit, null))
                    .setTitle(R.string.stop_limit)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    }).setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    });

            return builder.create();
        }

        @Override
        public void onResume() {
            super.onResume();
            final AlertDialog d = (AlertDialog)getDialog();
            if(d != null)
            {
                final EditText et = d.findViewById(R.id._etStopLimit);
                et.setText(getArguments().getInt(KEY_STOP_LIMIT)+"");
                Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        try{
                            int val = Integer.parseInt(et.getText().toString());
                            HandlerObserver.getoSolution().notifyStopLimit(val);
                            d.dismiss();
                        }catch (Exception e){
                            et.setTextColor(getActivity().getResources().getColor(R.color.errorColor));
                        }
                    }
                });
            }
        }
    }

    public static class SelectCardDialog extends DialogFragment {
        private GridLayout _glDeckCards;
        private Deck deck;
        private int numCards;
        private int numPlayer;
        private HashSet<Card> hsIn;
        private HashSet<Card> hsOut;
        private HashSet<Card> hsSelect;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            hsSelect = new HashSet<>(5);
            hsIn = new HashSet<>();
            hsOut = new HashSet<>();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.alertdialog_select_cards, null))
                    .setTitle(R.string.title_select_cards)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    }).setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });

            return builder.create();
        }

        @Override
        public void onResume() {
            super.onResume();
            final AlertDialog d = (AlertDialog)getDialog();
            if(d != null)
            {
                _glDeckCards = d.findViewById(R.id._glDeckCards);
                deck = getArguments().getParcelable(KEY_DECK);
                numCards = getArguments().getInt(KEY_NUM_CARDS);
                numPlayer = getArguments().getInt(KEY_NUM_PLAYER);
                hsSelect.clear();
                hsIn.clear();
                hsOut.clear();

                outCards();
                selectPlayerCards();

                for (int i = 0; i < _glDeckCards.getChildCount(); i++) {
                    TextView tv = (TextView) _glDeckCards.getChildAt(i);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onClickCardDeck((TextView)view);
                        }
                    });
                }

                Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                    onClickAccept();
                    }
                });

            }
        }

        private void onClickCardDeck(TextView view){
            try {
                Card c = Card.parseString(view.getText().toString());
                if(hsSelect.contains(c))
                {
                    hsSelect.remove(c);
                    if(hsOut.contains(c)) hsOut.remove(c);
                    hsIn.add(c);
                    view.setEnabled(true);
                    view.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                }
                else if(hsSelect.size() < numCards)
                {
                    hsSelect.add(c);
                    if(hsIn.contains(c)) hsIn.remove(c);
                    hsOut.add(c);
                    view.setEnabled(true);
                    view.setBackgroundColor(getActivity().getResources().getColor(R.color.selected));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void onClickAccept(){
            if(numCards != hsSelect.size())
                return;
            ArrayList<Card> cs = new ArrayList<>(Arrays.asList((Card[])hsSelect.toArray()));
            HandlerObserver.getoSolution().notifyPlayerCards(new OPlayerCards(cs, numPlayer,
                    (Card[]) hsOut.toArray(), (Card[]) hsIn.toArray()));
            dismiss();
        }

        private void outCards(){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < _glDeckCards.getChildCount(); i++) {
                            TextView tv = (TextView) _glDeckCards.getChildAt(i);
                            if (deck.contains(Card.parseString(tv.getText().toString()))){
                                tv.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                                tv.setEnabled(true);
                            }
                            else{
                                tv.setBackgroundColor(getActivity().getResources().getColor(R.color.disable));
                                tv.setEnabled(false);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

        private void selectPlayerCards(){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Card> alCd = getArguments().getParcelableArrayList(KEY_PLAYER_CARDS);
                    if(alCd != null){
                        for(Card c : alCd){
                            TextView tv = (TextView) _glDeckCards.getChildAt(c.getSuit().ordinal()*Card.NUM_CARDS+c.getValue());
                            tv.setEnabled(true);
                            tv.setBackgroundColor(getActivity().getResources().getColor(R.color.selected));
                            hsSelect.add(c);
                            hsOut.add(c);
                        }
                    }
                }
            });
        }
    }
}

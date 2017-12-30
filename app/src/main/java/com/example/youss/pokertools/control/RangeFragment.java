package com.example.youss.pokertools.control;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.youss.pokertools.R;
import com.example.youss.pokertools.model.ObserverPatron.HandlerObserver;
import com.example.youss.pokertools.model.ObserverPatron.OSolution;
import com.example.youss.pokertools.model.processor.RangeProcessor;
import com.example.youss.pokertools.model.representation.Card;
import com.example.youss.pokertools.model.representation.Suit;
import com.example.youss.pokertools.model.representation.range.CoupleCards;
import com.example.youss.pokertools.model.representation.range.Range;
import com.example.youss.pokertools.model.utils.EntryParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.Unbinder;


public class RangeFragment extends Fragment implements Observer{

    private static final int MAX_BOARD_CARDS = 5;
    public static final String KEY_RANGE = "key_range";
    public static final String KEY_PLAY_STATS = "key_play_stats";
    public static final String KEY_DRAW_STATS = "key_draw_stats";

    private Unbinder unbinder;
    @BindView(R.id._glRanking) GridLayout _glRanking;
    @BindView(R.id._etPercentage) EditText _etPercentage;
    @BindView(R.id._sbSlider) SeekBar _sbSlider;
    @BindView(R.id._btStats) Button _btStats;
    @BindView(R.id._glBoardsCards) GridLayout _glBoardsCards;
    private PersonalRangeDialog personalRangeDialog;
    private ShowRangeDialog showRangeDialog;

    private HashSet<String> hsBoardCards = null;
    private HashSet<String> hsCouples = null;
    private int numBoardCards = 0;
    private boolean sklanskyRanking = true;
    private RangeProcessor rP = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_range, container, false);
        unbinder = ButterKnife.bind(this, view);

        hsBoardCards = new HashSet<>(MAX_BOARD_CARDS);
        hsCouples = new HashSet<>();

        addListenerBoardCards();
        addListenerSeekBar();
        drawColorCells();
        HandlerObserver.addObserver(this);
        personalRangeDialog = new PersonalRangeDialog();
        showRangeDialog = new ShowRangeDialog();
        return view;
    }

    private void addListenerSeekBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _sbSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        _etPercentage.setText(seekBar.getProgress()+"%");
                        showRange();
                    }
                });
            }
        });
    }

    private void addListenerBoardCards(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < _glBoardsCards.getChildCount(); i++) {
                    _glBoardsCards.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onClickBoardCard((TextView)view);
                        }
                    });
                }
            }
        });
    }

    private void onClickBoardCard(TextView view){

        char suit = view.getText().charAt(1);
        int selected = getResources().getColor(R.color.selected);
        int nonSelected = 0;
        switch (suit){
            case 'h':
                nonSelected = getResources().getColor(R.color.hearts);
                break;
            case 'c':
                nonSelected = getResources().getColor(R.color.clubs);
                break;
            case 'd':
                nonSelected = getResources().getColor(R.color.diamonds);
                break;
            case 's':
                nonSelected = getResources().getColor(R.color.spades);
                break;
        }
        if(hsBoardCards.contains(view.getText().toString())){
            hsBoardCards.remove(view.getText().toString());
            numBoardCards--;
            view.setBackgroundColor(nonSelected);
        }
        else if(numBoardCards < MAX_BOARD_CARDS){
            hsBoardCards.add(view.getText().toString());
            numBoardCards++;
            view.setBackgroundColor(selected);
        }
    }

    /**
     * It draws the color cells of the ranking
     */
    private void drawColorCells(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Card.NUM_CARDS; i++) {
                    for (int j = 0; j < Card.NUM_CARDS; j++) {
                        int color = 0;
                        if( i == j)
                            color = getResources().getColor(R.color.pairCell);
                        else if( i > j)
                            color = getResources().getColor(R.color.offSuitedCell);
                        else if( i < j)
                            color = getResources().getColor(R.color.suitedCell);

                        _glRanking.getChildAt(i*Card.NUM_CARDS+j).setBackgroundColor(color);
                    }
                }
            }
        });
    }

    @OnClick(R.id._btStats)
    public void onClickStats() {
        if(hsCouples.size() == 0){
            Snackbar.make(_btStats, getString(R.string.error_no_range_selected), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(numBoardCards < 3) {
            Snackbar.make(_btStats, R.string.error_noboards_cards, Snackbar.LENGTH_SHORT).show();
            return;
        }

        try{
            HashSet<Card> hsC = new HashSet<>(hsBoardCards.size());
            for (String st: hsBoardCards)
                hsC.add(new Card(Card.charToValue(st.charAt(0)), Suit.getFromChar(st.charAt(1))));

            rP = new RangeProcessor(hsC, CoupleCards.toCoupleCards(hsCouples));
            rP.run();

            Intent intent = new Intent(getActivity(), StatsActivity.class);
            intent.putParcelableArrayListExtra(KEY_PLAY_STATS, rP.getPlayStats());
            intent.putParcelableArrayListExtra(KEY_DRAW_STATS, rP.getDrawStats());
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity()
                    , R.anim.activity_chart_in, R.anim.activity_out);

            startActivityForResult(intent, 1, options.toBundle());

        }catch (Exception e){
            e.printStackTrace();
            Log.e("Error stats", e.getMessage());
        }
    }

    @OnEditorAction(R.id._etPercentage)
    public boolean onPercentageChange(int i, KeyEvent key){
        if(i== EditorInfo.IME_ACTION_DONE)
            showRange();
        return false;
    }

    private void showRange(){
        try{
            int val = Integer.parseInt(_etPercentage.getText().toString().split("%")[0]);
            if(val < 0 || val > 100)
                throw new NumberFormatException("Not in range");

            _etPercentage.setTextColor(getResources().getColor(R.color.black));
            _etPercentage.setText(val+"%");
            _sbSlider.setProgress(val);

            drawColorCells();
            hsCouples.clear();

            if (sklanskyRanking)
                selectElemsMatrix(CoupleCards.coupleCardsToMatrix(Range.rangeToCoupleCards(Range.getRangeArraySklansky(val))));
            else
                selectElemsMatrix(CoupleCards.coupleCardsToMatrix(Range.rangeToCoupleCards(Range.getRangeArrayStrength(val))));

        }catch (Exception e){
            Log.e("Show range error", e.getMessage());
            _etPercentage.setTextColor(getResources().getColor(R.color.errorColor));
        }
    }

    /**
     * It select the elements in the grid
     * @param pairs
     */
    private void selectElemsMatrix(final ArrayList<Pair<Integer, Integer>> pairs){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Pair<Integer, Integer> p : pairs) {
                    _glRanking.getChildAt(p.first * Card.NUM_CARDS + p.second).setBackgroundColor(getResources().getColor(R.color.selected));
                    hsCouples.add(((TextView) _glRanking.getChildAt(p.first * Card.NUM_CARDS + p.second)).getText().toString());
                }
            }
        });
    }

    private void clear(){
        hsCouples.clear();
        drawColorCells();
        _sbSlider.setProgress(0);
        _etPercentage.setText("0%");
    }

    private void onClickClearAll(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear();
            }
        });
    }

    private void onClickSelectAll(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Card.NUM_CARDS; i++) {
                    for (int j = 0; j < Card.NUM_CARDS; j++) {
                        _glRanking.getChildAt(i*Card.NUM_CARDS+j).setBackgroundColor(getResources().getColor(R.color.selected));
                        hsCouples.add(((TextView)_glRanking.getChildAt(i*Card.NUM_CARDS+j)).getText().toString());
                    }
                }
                updatePercentage(hsCouples.size());
                _sbSlider.setProgress(100);
            }
        });
    }

    private void onClickSuited(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Card.NUM_CARDS; i++) {
                    for (int j = i+1; j < Card.NUM_CARDS; j++) {
                        _glRanking.getChildAt(i*Card.NUM_CARDS+j).setBackgroundColor(getResources().getColor(R.color.selected));
                        hsCouples.add(((TextView)_glRanking.getChildAt(i*Card.NUM_CARDS+j)).getText().toString());
                    }
                }
                updatePercentage(hsCouples.size());
            }
        });
    }

    private void onClickBroadway(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Card.NUM_CARDS; i++) {
                    for (int j = 0; j < i; j++) {
                        _glRanking.getChildAt(i*Card.NUM_CARDS+j).setBackgroundColor(getResources().getColor(R.color.selected));
                        hsCouples.add(((TextView)_glRanking.getChildAt(i*Card.NUM_CARDS+j)).getText().toString());
                    }
                }
                updatePercentage(hsCouples.size());
            }
        });
    }

    private void onClickPairs(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Card.NUM_CARDS; i++) {
                    _glRanking.getChildAt(i*Card.NUM_CARDS+i).setBackgroundColor(getResources().getColor(R.color.selected));
                    hsCouples.add(((TextView)_glRanking.getChildAt(i*Card.NUM_CARDS+i)).getText().toString());
                }
                updatePercentage(hsCouples.size());
            }
        });
    }

    private void onClickRanking(final boolean ranking){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sklanskyRanking = ranking;
                showRange();
            }
        });
    }

    private void onClickPersonalRanking(final EntryParser parser){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear();
                selectElemsMatrix(CoupleCards.coupleCardsToMatrix(Range.rangeToCoupleCards(parser.getRangeEntry())));
                updatePercentage(hsCouples.size());
            }
        });
    }

    private void updatePercentage(int num){
        _etPercentage.setText( (int)Math.floor((num*100) / CoupleCards.NUM_COUPLE_CARDS) + "%");
    }

    private void onClickGenerate(){
        if(hsCouples.size() > 0){
            Bundle b = new Bundle();
            b.putString(KEY_RANGE, Range.getRanks(hsCouples.toArray(new String[hsCouples.size()])));
            showRangeDialog.setArguments(b);
            showRangeDialog.show(getActivity().getSupportFragmentManager(), "generate");
        }
    }

    @Override
    public void update(Observable arg0, Object o) {
        OSolution sol = (OSolution) arg0;

        if(sol.getState() == OSolution.NOTIFY_RANGE_CLEAR)
            onClickClearAll();
        else if(sol.getState() == OSolution.NOTIFY_RANGE_SELECT_ALL)
            onClickSelectAll();
        else if(sol.getState() == OSolution.NOTIFY_RANGE_SELECT_SUITED)
            onClickSuited();
        else if(sol.getState() == OSolution.NOTIFY_RANGE_SELECT_BROADWAY)
            onClickBroadway();
        else if(sol.getState() == OSolution.NOTIFY_RANGE_SELECT_PAIR)
            onClickPairs();
        else if(sol.getState() == OSolution.NOTIFY_RANGE_CHANGE_RANKING)
            onClickRanking((Boolean)o);
        else if(sol.getState() == OSolution.NOTIFY_RANGE_PERSONAL_RANGE_REQUEST)
            personalRangeDialog.show(getActivity().getSupportFragmentManager(), "personalRange");

        else if(sol.getState() == OSolution.NOTIFY_RANGE_PERSONAL_RANGE_REPONSE)
            onClickPersonalRanking((EntryParser) o);
        else if(sol.getState() == OSolution.NOTIFY_RANGE_GENERATE)
            onClickGenerate();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HandlerObserver.removeObserver(this);
        unbinder.unbind();
    }

    public static class PersonalRangeDialog extends DialogFragment{
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.alertdialog_personal_range, null))
            .setTitle(R.string.personal_range_dialog_title)
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            return builder.create();
        }

        @Override
        public void onResume() {
            super.onResume();
            final AlertDialog d = (AlertDialog)getDialog();
            if(d != null)
            {
                Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        TextView tv = d.findViewById(R.id._etPersonalRange);
                        String entry = tv.getText().toString();
                        if(entry.isEmpty())
                            return;

                        EntryParser entryParser = new EntryParser(entry);
                        if(!entryParser.parseEntry()){
                            tv.setTextColor(getResources().getColor(R.color.errorColor));
                            return;
                        }
                        HandlerObserver.getoSolution().notifyPersonalRangeReponse(entryParser);
                        d.dismiss();
                    }
                });
            }
        }
    }

    public static class ShowRangeDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getArguments().getString(KEY_RANGE))
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    });
            return builder.create();
        }
    }
}

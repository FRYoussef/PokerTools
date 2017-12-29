package com.example.youss.pokertools.control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.youss.pokertools.model.representation.Card;
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

    private Unbinder unbinder;
    @BindView(R.id._glRanking) GridLayout _glRanking;
    @BindView(R.id._etPercentage) EditText _etPercentage;
    @BindView(R.id._sbSlider) SeekBar _sbSlider;
    @BindView(R.id._btStats) Button _btStats;
    @BindView(R.id._glBoardsCards) GridLayout _glBoardsCards;
    private PersonalRangeDialog personalRangeDialog;

    private HashSet<String> hsBoardCards = null;
    private HashSet<String> hsCouples = null;
    private int numBoardCards = 0;
    private boolean sklanskyRanking = true;

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
        int selected = 0;
        int nonSelected = 0;
        switch (suit){
            case 'h':
                selected = getResources().getColor(R.color.heartsSelected);
                nonSelected = getResources().getColor(R.color.hearts);
                break;
            case 'c':
                selected = getResources().getColor(R.color.clubsSelected);
                nonSelected = getResources().getColor(R.color.clubs);
                break;
            case 'd':
                selected = getResources().getColor(R.color.diamondsSelected);
                nonSelected = getResources().getColor(R.color.diamonds);
                break;
            case 's':
                selected = getResources().getColor(R.color.spadesSelected);
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
            personalRangeDialog.show(getActivity().getSupportFragmentManager(), null);

        else if(sol.getState() == OSolution.NOTIFY_RANGE_PERSONAL_RANGE_REPONSE)
            onClickPersonalRanking((EntryParser) o);
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
}

package com.example.youss.pokertools.control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youss.pokertools.R;
import com.example.youss.pokertools.model.ObserverPatron.HandlerObserver;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Youss on 30/12/2017.
 */

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder>{

    private static final DecimalFormat DF = new DecimalFormat("#.###");
    private Context context;
    private LayoutInflater inflater;
    private List<PlayerView> lPlayer;

    public PlayerAdapter(Context context, List<PlayerView> lPlayer) {
        this.context = context;
        this.lPlayer = lPlayer;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_player, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PlayerView pl = lPlayer.get(position);
        holder.enableView(pl.isOnFold(), pl.isOnSim());
        String aux = context.getString(R.string.player)+ pl.getPlayer();
        holder._tvPlayer.setText(aux);
        aux = context.getString(R.string.equity) + ": " + DF.format(pl.getEquity()*100.f) + "%";
        holder._tvEquity.setText(aux);
        holder._ivCard1.setImageResource(context.getResources().getIdentifier(pl.getDrawableCard1(context),
                "drawable", context.getPackageName()));
        holder._ivCard2.setImageResource(context.getResources().getIdentifier(pl.getDrawableCard2(context),
                "drawable", context.getPackageName()));
        holder._btFold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.onClickFold(pl);
            }
        });
        holder._ivCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.onClickImg(pl.getPlayer()-1);
            }
        });
        holder._ivCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.onClickImg(pl.getPlayer()-1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lPlayer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView _ivCard1;
        public ImageView _ivCard2;
        public TextView _tvPlayer;
        public TextView _tvEquity;
        public Button _btFold;
        public View _vDisable;

        public ViewHolder(View itemView) {
            super(itemView);
            _ivCard1 = itemView.findViewById(R.id._ivCard1);
            _ivCard2 = itemView.findViewById(R.id._ivCard2);
            _tvPlayer = itemView.findViewById(R.id._tvPlayer);
            _tvEquity = itemView.findViewById(R.id._tvEquity);
            _btFold = itemView.findViewById(R.id._btFold);
            _vDisable = itemView.findViewById(R.id._vDisable);
        }

        public void onClickFold(PlayerView pl){
            pl.setOnFold(false);
            enableView(pl.isOnFold(), pl.isOnSim());
            HandlerObserver.getoSolution().notifyFold(pl.getPlayer()-1);
        }

        public void onClickImg(int player){
            HandlerObserver.getoSolution().notifySelectionPlayerCards(player);
        }

        private void enableView(boolean onFold, boolean onSim){
            if(!onFold){
                _btFold.setEnabled(onFold);
                _ivCard1.setEnabled(onFold);
                _ivCard2.setEnabled(onFold);
                _vDisable.setVisibility(View.VISIBLE);
            }else {
                _btFold.setEnabled(onSim);
                _ivCard1.setEnabled(onSim);
                _ivCard2.setEnabled(onSim);
                _vDisable.setVisibility(View.GONE);
            }
        }
    }
}

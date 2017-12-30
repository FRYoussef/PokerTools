package com.example.youss.pokertools.control;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.youss.pokertools.R;
import com.example.youss.pokertools.model.representation.Stat;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatsActivity extends AppCompatActivity {

    @BindView(R.id._pcPlays) PieChart _pcPlays;
    @BindView(R.id._pcDraws) PieChart _pcDraws;
    @BindView(R.id._tbAppBar) Toolbar _tbAppBar;

    private int colors[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        ButterKnife.bind(this);

        setSupportActionBar(_tbAppBar);
        getSupportActionBar().setTitle(R.string.actionbar_title);

        String cs[] = getResources().getStringArray(R.array.colorsChart);
        colors = new int[cs.length];
        for(int i = 0; i < cs.length; i++)
            colors[i] = Color.parseColor(cs[i]);

        ArrayList<Stat> alPlays = getIntent().getParcelableArrayListExtra(RangeFragment.KEY_PLAY_STATS);
        ArrayList<Stat> alDraws = getIntent().getParcelableArrayListExtra(RangeFragment.KEY_DRAW_STATS);

        getSameChart(_pcPlays, 3000, alPlays);
        _pcPlays.setHoleRadius(45);
        _pcPlays.setTransparentCircleRadius(55);
        _pcPlays.setData(getPieData(alPlays));
        _pcPlays.setCenterText(getString(R.string.title_play_chart));
        _pcPlays.setCenterTextSize(15);

        getSameChart(_pcDraws, 3000, alDraws);
        _pcDraws.setHoleRadius(45);
        _pcDraws.setTransparentCircleRadius(55);
        _pcDraws.setData(getPieData(alDraws));
        _pcDraws.setCenterText(getString(R.string.title_draw_chart));
        _pcDraws.setCenterTextSize(15);
    }

    private Chart getSameChart(Chart chart, int animationDuration, ArrayList<Stat> stats){
        chart.getDescription().setEnabled(false);
        chart.animateX(animationDuration);
        chart.setExtraRightOffset(100);
        legend(chart, stats);
        return chart;
    }

    private Chart legend(Chart chart, ArrayList<Stat> stats){
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setFormSize(10);
        legend.setTextSize(13);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setDrawInside(true);

        ArrayList<LegendEntry> entries = new ArrayList<>(stats.size());
        for(int i = 0; i < stats.size(); i++){
            LegendEntry entry = new LegendEntry();
            entry.label= stats.get(i).toString();
            entry.formColor = colors[i];
            entries.add(entry);
        }
        if(stats.size() == 0){
            LegendEntry entry = new LegendEntry();
            entry.label= getString(R.string.no_draws);
            entry.formColor = colors[0];
            entries.add(entry);
        }
        legend.setCustom(entries);
        return chart;
    }

    private ArrayList<PieEntry> getPieEntries(ArrayList<Stat> stats){
        ArrayList<PieEntry> entries = new ArrayList<>(stats.size());
        for(Stat s : stats)
            entries.add(new PieEntry(s.getValue()));
        if(stats.size() == 0)
            entries.add(new PieEntry(100));
        return entries;
    }

    private PieData getPieData(ArrayList<Stat> stats){
        PieDataSet pieDataSet = new PieDataSet(getPieEntries(stats), "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(18);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        pieDataSet.setSliceSpace(3);
        if(stats.size() > 0 && stats.get(0).isRelativeValue())
            pieDataSet.setValueFormatter(new RelativeFormatter());
        else
            pieDataSet.setValueFormatter(new AbsoluteFormatter());
        return new PieData(pieDataSet);
    }

    public class AbsoluteFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return ((int)value)+"";
        }
    }

    public class RelativeFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return ((int)value)+"%";
        }
    }
}

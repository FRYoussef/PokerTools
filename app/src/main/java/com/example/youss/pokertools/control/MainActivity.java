package com.example.youss.pokertools.control;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.youss.pokertools.R;
import com.example.youss.pokertools.model.ObserverPatron.HandlerObserver;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    private static final int RANGE = 0;
    private static final int EQUITY = 1;

    @BindView(R.id._tbAppBar) Toolbar _tbAppBar;
    @BindView(R.id._tlTabs) TabLayout _tlTabs;
    @BindView(R.id._vpContent) ViewPager _vpContent;

    private MenuInflater menuInflater;
    private RangeFragment rangeFragment;
    private EquityFragment equityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        menuInflater = getMenuInflater();
        setSupportActionBar(_tbAppBar);
        getSupportActionBar().setTitle(R.string.actionbar_title);

        rangeFragment = new RangeFragment();
        equityFragment = new EquityFragment();
        _vpContent.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        _vpContent.setCurrentItem(RANGE, true);
        _tlTabs.setupWithViewPager(_vpContent);
        HandlerObserver.init();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        if(_vpContent.getCurrentItem() == RANGE)
            menuInflater.inflate(R.menu.menu_range, menu);
        else if(_vpContent.getCurrentItem() == EQUITY)
            menuInflater.inflate(R.menu.menu_equity, menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(_vpContent.getCurrentItem() == RANGE)
            return rangeItemMenu(item);
        else
            return equityItemMenu(item);
    }

    private boolean equityItemMenu(MenuItem item) {
        switch (item.getItemId()){
            case R.id.stopLimit:
                HandlerObserver.getoSolution().notifyStopLimit(null);
                return true;
            case R.id.p1:
                HandlerObserver.getoSolution().notifyNumPlayers(1);
                return true;
            case R.id.p2:
                HandlerObserver.getoSolution().notifyNumPlayers(2);
                return true;
            case R.id.p3:
                HandlerObserver.getoSolution().notifyNumPlayers(3);
                return true;
            case R.id.p4:
                HandlerObserver.getoSolution().notifyNumPlayers(4);
                return true;
            case R.id.p5:
                HandlerObserver.getoSolution().notifyNumPlayers(5);
                return true;
            case R.id.p6:
                HandlerObserver.getoSolution().notifyNumPlayers(6);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean rangeItemMenu(MenuItem item){
        switch (item.getItemId()) {
            case R.id.selectSuited:
                HandlerObserver.getoSolution().notifySelectSuited();
                return true;
            case R.id.selectBroadway:
                HandlerObserver.getoSolution().notifySelectBroadway();
                return true;
            case R.id.selectPairs:
                HandlerObserver.getoSolution().notifySelectPair();
                return true;
            case R.id.selectAll:
                HandlerObserver.getoSolution().notifySelectAll();
                return true;
            case R.id.clear:
                HandlerObserver.getoSolution().notifyClear();
                return true;
            case R.id.sklansky:
                HandlerObserver.getoSolution().notifyChangeRanking(true);
                return true;
            case R.id.strength:
                HandlerObserver.getoSolution().notifyChangeRanking(false);
                return true;
            case R.id.selectPersonalRange:
                HandlerObserver.getoSolution().notifyPersonalRangeRequest();
                return true;
            case R.id.generateRange:
                HandlerObserver.getoSolution().notifyGenerate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case RANGE:
                    return rangeFragment;
                case EQUITY:
                    return equityFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case RANGE:
                    return getString(R.string.range);
                case EQUITY:
                    return getString(R.string.equity);
                default:
                    return null;
            }
        }
    }

}

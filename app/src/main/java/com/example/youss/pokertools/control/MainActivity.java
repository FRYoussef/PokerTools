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

import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Observer{

    private static final int RANGE = 0;
    private static final int EQUITY = 1;

    @BindView(R.id._tbAppBar) Toolbar _tbAppBar;
    @BindView(R.id._tlTabs) TabLayout _tlTabs;
    @BindView(R.id._vpContent) ViewPager _vpContent;

    private int currentTab = RANGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(_tbAppBar);
        getSupportActionBar().setTitle(R.string.actionbar_title);

        _vpContent.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        _vpContent.setCurrentItem(currentTab, true);
        _tlTabs.setupWithViewPager(_vpContent);
        HandlerObserver.init();
        HandlerObserver.addObserver(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_range, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    @Override
    public void update(Observable observable, Object o) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HandlerObserver.removeObserver(this);
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case RANGE:
                    return new RangeFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 1;
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

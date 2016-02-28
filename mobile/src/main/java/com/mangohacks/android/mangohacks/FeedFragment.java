package com.mangohacks.android.mangohacks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FeedFragment extends BaseFragment {

    private OnFragmentInteractionListener mListener;

    // View Items
    Toolbar mToolbar;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    AppBarLayout mAppBar;
    CollapsingToolbarLayout mCollasping;
    TextView mCountdownLabel;
    TextView mCountdownTime;

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    // Required empty public constructor
    public FeedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mTabLayout = (TabLayout) v.findViewById(R.id.tabs);
        mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
        mAppBar = (AppBarLayout) v.findViewById(R.id.app_bar);
        mCollasping = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        mCountdownLabel = (TextView) v.findViewById(R.id.tv_countdown_label);
        mCountdownTime = (TextView) v.findViewById(R.id.tv_countdown_time);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Toolbar stuff
        //toolbar.inflateMenu(R.menu.menu_main);
        //setSupportActionBar(mToolbar);
        //mToolbar.setTitle("MangoHacks");
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        mToolbar.inflateMenu(R.menu.menu_main);
        mListener.registerToolbar(mToolbar);

        // Init toolbar icons
        final SharedPreferences sp = getContext().getSharedPreferences(MangoHacks.PREFERENCES, Context.MODE_PRIVATE);
        Menu menu = mToolbar.getMenu();
        MenuItem notifs = menu.findItem(R.id.action_notifications);
        notifs.setIcon(sp.getBoolean(MangoHacks.NOTIFICATIONS, true) ?
                R.drawable.ic_notifications_24dp : R.drawable.ic_notifications_off_24dp);
        MenuItem countdown = menu.findItem(R.id.action_countdown);
        countdown.setIcon(sp.getBoolean(MangoHacks.COUNTDOWN, true) ?
                R.drawable.ic_timer_24dp : R.drawable.ic_timer_off_white_24dp);


        // View Pager setup
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(UpdateFragment.newInstance());
        fragments.add(ScheduleFragment.newInstance());
        PagerAdapter mPagerAdapter = new PagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        if(savedInstanceState != null) {
            mViewPager.setCurrentItem(savedInstanceState.getInt("pos"));
        }


        // Custom toolbar font
        Typeface face;
        face = Typeface.createFromAsset(getContext().getAssets(), getResources().getString(R.string.mangohacks_font));
        mCollasping.setCollapsedTitleTypeface(face);
        mCollasping.setExpandedTitleTypeface(face);
        mCollasping.setTitle("MangoHacks");

        mCountdownLabel.setTypeface(face);
        mCountdownTime.setTypeface(face);

        // Countdown

        initNextTimer();

    }

    public void initNextTimer() {

        Date now = Calendar.getInstance().getTime();

        ParseQuery<CountdownItem> query = new ParseQuery<CountdownItem>(ParseName.COUNTDOWNITEM);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.whereGreaterThan(ParseName.COUNTDOWN_TIME, now);
        query.getFirstInBackground(new GetCallback<CountdownItem>() {
            @Override
            public void done(final CountdownItem object, ParseException e) {

                if(e == null && object != null) {
                    mCountdownLabel.setText(object.getLabel());
                    Log.d("MangoHacks", "Object label: " + object.getLabel());
                    Log.d("MangoHacks", "Object time: " + object.getTime().toString());


                    long until = (object.getTime().getTime() - System.currentTimeMillis());
                    new CountDownTimer(until, 1000) {
                        @Override
                        public void onFinish() {
                            mCountdownTime.setText("MangoHacks");
                            mCountdownLabel.setText("");
                            initNextTimer();
                        }

                        @Override
                        public void onTick(long millis) {
                            String out = String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(millis),
                                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                            mCountdownTime.setText(out);

                        }
                    }.start();
                } else if(object == null) {
                    mCountdownTime.setText("MangoHacks");
                    mCountdownLabel.setText("");
                } else if(e != null) {
                    Log.e("MangoHacks", e.getMessage());
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pos", mViewPager.getCurrentItem());
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @ParseClassName(ParseName.COUNTDOWNITEM)
    public static class CountdownItem extends ParseObject {

        public CountdownItem(){}

        public String getLabel() {
            return getString(ParseName.COUNTDOWN_LABEL);
        }

        public Date getTime() {
            return getDate(ParseName.COUNTDOWN_TIME);
        }


    }

}

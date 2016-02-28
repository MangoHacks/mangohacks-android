package com.mangohacks.android.mangohacks;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class ScheduleFragment extends BaseFragment {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    ScheduleRecyclerAdapter mAdapter;
    SwipeRefreshLayout mSwipeLayout;
    View mEmptyView;

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    // Required empty public constructor
    public ScheduleFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_list_refresh, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);
        mEmptyView = v.findViewById(R.id.empty_view);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).build());

        // specify an adapter (see also next example)
        mAdapter = new ScheduleRecyclerAdapter(new ArrayList<ScheduleItem>());
        mRecyclerView.setAdapter(mAdapter);

        ParseQuery<ScheduleItem> query = ParseQuery.getQuery(ParseName.SCHEDULEITEM);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.orderByAscending("startTime");
        query.findInBackground(new FindCallback<ScheduleItem>() {
            @Override
            public void done(List<ScheduleItem> list, ParseException e) {
                if(e != null) {
                    Log.e("MangoHacks", "Error: " + e.getMessage());
                } else {
                    mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
                    mAdapter.replaceDataset(list);
                    mAdapter.notifyItemRangeInserted(0, mAdapter.getItemCount());
                }
            }
        });


        // Swipe Reload
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ParseQuery<ScheduleItem> query = ParseQuery.getQuery(ParseName.SCHEDULEITEM);
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.orderByAscending("startTime");
                query.findInBackground(new FindCallback<ScheduleItem>() {
                    @Override
                    public void done(List<ScheduleItem> list, ParseException e) {
                        if (e != null) {
                            Log.e("MangoHacks", e.getMessage());
                            Snackbar.make(mRecyclerView, "Could not refresh.", Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
                            mAdapter.replaceDataset(list);
                            mAdapter.notifyItemRangeInserted(0, mAdapter.getItemCount());
                        }
                        mSwipeLayout.setRefreshing(false);
                    }
                });
            }
        });
        mSwipeLayout.setColorSchemeResources(R.color.accent);
    }

    // Adapter used by this fragment
    private class ScheduleRecyclerAdapter extends
            RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder> {

        private List<ScheduleItem> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            public View card;
            public TextView mTitleText;
            public TextView mSubtitleText;
            public TextView mTimeText;
            public ViewHolder(View v) {
                super(v);
                card = v;
                mTitleText = (TextView) v.findViewById(R.id.tv_title);
                mSubtitleText = (TextView) v.findViewById(R.id.tv_subtitle);
                mTimeText = (TextView) v.findViewById(R.id.tv_time);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ScheduleRecyclerAdapter(ArrayList<ScheduleItem> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ScheduleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tile_schedule, parent, false);

            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            // ssshhhh ignore this bit it never got finished
            if(mDataset.get(position) instanceof ScheduleDivider) {

                //holder.mTimeText.setText("Upcoming");
                holder.mTitleText.setText("");
                holder.mSubtitleText.setText("");

            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("EEE hh:mm a", Locale.US);
                formatter.setTimeZone(TimeZone.getTimeZone("EST"));
                String startTime = formatter.format(mDataset.get(position).getStartTime());

                holder.mTimeText.setText(startTime);
                holder.mTitleText.setText(mDataset.get(position).getTitle());
                holder.mSubtitleText.setText(mDataset.get(position).getSubtitle());
            }



        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void replaceDataset(List<ScheduleItem> data) {
            mDataset = data;
            if(data.size() > 0) mEmptyView.setVisibility(View.INVISIBLE);
            else mEmptyView.setVisibility(View.VISIBLE);

            /*GregorianCalendar gc = new GregorianCalendar(Locale.US);
            for(Sponsor u : mDataset) {
                if(u.getStartTime().compareTo(gc.getTime()) < 0) {
                    mDataset.add(mDataset.indexOf(u), new ScheduleDivider());
                }
            }*/
        }
    }

    @ParseClassName("ScheduleItem")
    public static class ScheduleItem extends ParseObject {

        public ScheduleItem(){

        }

        /*public Date getEndTime() {
            return getDate("endTime");
        }*/

        public Date getStartTime() {
            return getDate("startTime");
        }

        public String getSubtitle() {
            return getString("subtitle");
        }

        public String getTitle() {
            return getString("title");
        }
    }

    @ParseClassName("ScheduleDivider")
    public static class ScheduleDivider extends ScheduleItem {

        // This never got used. IGNORE ME.

    }
}

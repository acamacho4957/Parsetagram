package com.example.parsetagram.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.parsetagram.EndlessRecyclerViewScrollListener;
import com.example.parsetagram.ItemClickSupport;
import com.example.parsetagram.PostsAdapter;
import com.example.parsetagram.R;
import com.example.parsetagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedFragment extends Fragment {

    private final String TAG = "FeedFragment";

    @BindView(R.id.rvPosts) RecyclerView rvPosts;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    private PostsAdapter adapter;
    private ArrayList<Post> mPosts;
    private EndlessRecyclerViewScrollListener scrollListener;
    private FragmentManager fragmentManager;

    public FeedFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        fragmentManager = getFragmentManager();

        rvPosts.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        // create the data source
        mPosts = new ArrayList<>();
        //create the adapter
        adapter = new PostsAdapter(getContext(), mPosts, fragmentManager);
        // set adapter on recycler view
        rvPosts.setAdapter(adapter);
        // set layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(linearLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                queryPosts(page, false);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts(0,true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        ItemClickSupport.addTo(rvPosts).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Bundle args = new Bundle();
                        args.putParcelable("post", mPosts.get(position));
                        Fragment fragment = new DetailFragment();
                        fragment.setArguments(args);
                        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, fragment).addToBackStack(null).commit();
                    }
                }
        );

        queryPosts(0,false);

    }

    private void queryPosts(int page, final boolean isRefresh) {
        if (isRefresh) {
            swipeContainer.setRefreshing(true);
        } else {
            pbLoading.setVisibility(ProgressBar.VISIBLE);
        }

        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser().onPage(page);

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    if (isRefresh) {
                        adapter.clear();
                    }
                    mPosts.addAll(objects);
                    adapter.notifyDataSetChanged();

                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    swipeContainer.setRefreshing(false);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}

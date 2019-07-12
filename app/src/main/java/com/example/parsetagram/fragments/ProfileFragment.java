package com.example.parsetagram.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parsetagram.EndlessRecyclerViewScrollListener;
import com.example.parsetagram.ItemClickSupport;
import com.example.parsetagram.PostsAdapterGrid;
import com.example.parsetagram.R;
import com.example.parsetagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.rvPosts) RecyclerView rvPosts;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUsername) TextView tvUsername;

    private PostsAdapterGrid adapter;
    private ArrayList<Post> mPosts;
    private EndlessRecyclerViewScrollListener scrollListener;
    private FragmentManager fragmentManager;
    private ParseUser user;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        user = getArguments().getParcelable("user");
        mPosts = new ArrayList<>();
        adapter = new PostsAdapterGrid(getContext(), mPosts);
        rvPosts.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        rvPosts.setLayoutManager(gridLayoutManager);

        tvUsername.setText(user.getUsername().toString());

        ParseFile profileImage = user.getParseFile("profileImage");
        if (profileImage != null) {
            String preURL = profileImage.getUrl();
            String completeURL = preURL.substring(0, 4) + "s" + preURL.substring(4, preURL.length());
            Glide.with(getContext())
                    .load(completeURL)
                    .into(ivProfileImage);
        } else {
            Log.d("Profile", "no profile image");
        }

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(page, false);
            }
        };
        rvPosts.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts(0,true);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fragmentManager = getFragmentManager();
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
        postsQuery.getTop().withUser().onPage(page).whereEqualTo(Post.KEY_USER, user);

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

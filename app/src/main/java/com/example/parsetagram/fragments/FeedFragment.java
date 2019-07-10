package com.example.parsetagram.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private PostsAdapter adapter;
    private ArrayList<Post> mPosts;

    public FeedFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        rvPosts.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        // create the data source
        mPosts = new ArrayList<>();
        //create the adapter
        adapter = new PostsAdapter(getContext(), mPosts);
        // set adapter on recycler view
        rvPosts.setAdapter(adapter);
        // set layout manager
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPosts();
    }

    private void queryPosts() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    mPosts.addAll(objects);
                    adapter.notifyDataSetChanged();
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i).getImage() != null) {
                        Log.d(TAG, "Post[" + i + "] = " + objects.get(i).getDescription()
                                + "\nurl = " + objects.get(i).getImage().getUrl());}
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}

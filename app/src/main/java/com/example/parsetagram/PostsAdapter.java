package com.example.parsetagram;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parsetagram.fragments.ProfileFragment;
import com.example.parsetagram.model.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{
    private static final String TAG = "PostsAdapter";

    private Context context;
    private ArrayList<Post> posts;
    private FragmentManager fragmentManager;

    public PostsAdapter(Context context, ArrayList<Post> posts, FragmentManager fragmentManager) {
        this.context = context;
        this.posts = posts;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Post post = posts.get(position);
        final ParseUser author = post.getUser();

        viewHolder.tvUsername.setText(author.getUsername());
        viewHolder.tvDescription.setText(post.getDescription());

        ParseFile postedImage = post.getImage();
        if (postedImage != null) {
            String preURL = postedImage.getUrl();
            String completeURL = preURL.substring(0, 4) + "s" + preURL.substring(4, preURL.length());
            Glide.with(context)
                    .load(completeURL)
                    .into(viewHolder.ivPostedImage);
        }

        String rawDate = post.getCreatedAt().toString();
        viewHolder.tvCreatedAt.setText(getRelativeTimeAgo(rawDate));

        ParseFile profileImage = author.getParseFile("profileImage");
        if (profileImage != null) {
            String preURL = profileImage.getUrl();
            String completeURL = preURL.substring(0, 4) + "s" + preURL.substring(4, preURL.length());
            Glide.with(context)
                    .load(completeURL)
                    .into(viewHolder.ivProfileImage);
        }

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (post.isLikedBy(currentUser.getObjectId())) {
            viewHolder.ivLike.setBackground(context.getResources().getDrawable(R.drawable.ufi_heart_active));
        } else {
            viewHolder.ivLike.setBackground(context.getResources().getDrawable(R.drawable.ufi_heart));
        }

        viewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.isLikedBy(currentUser.getObjectId())) {
                    post.removeLike(currentUser.getObjectId());
                } else {
                    post.addLike(currentUser.getObjectId());
                }
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        onBindViewHolder(viewHolder,  position);
                        Log.d(TAG, "sent like");
                    }
                });
            }
        });

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(author);
            }
        });

        viewHolder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(author);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
        @BindView(R.id.ivPostedImage) ImageView ivPostedImage;
        @BindView(R.id.ivLike) ImageView ivLike;
        @BindView(R.id.tvDescription) TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    private String getRelativeTimeAgo(String rawJsonDate) {
        String instagramFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(instagramFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    private void goToProfile(ParseUser user) {
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        Fragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, fragment).addToBackStack(null).commit();
    }
}

package com.example.parsetagram.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parsetagram.R;
import com.example.parsetagram.model.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {

    private final String TAG = "DetailFragment";

    private Post post;
    private FragmentManager fragmentManager;

    @BindView(R.id.btLike) Button btLike;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
    @BindView(R.id.ivPostedImage) ImageView ivPostedImage;
    @BindView(R.id.tvDescription) TextView tvDescription;
    @BindView(R.id.tvLikes) TextView tvLikes;

    public DetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, parent, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        fragmentManager = getFragmentManager();
        post = getArguments().getParcelable("post");
        final ParseUser author = post.getUser();

        tvUsername.setText(author.getUsername());
        tvDescription.setText(post.getDescription());

        Integer likesCount = post.getLikes();
        if (likesCount != null && likesCount > 0) {
            tvLikes.setText(post.getLikes().toString());
        } else {
            tvLikes.setText("");
        }

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (post.isLikedBy(currentUser.getObjectId())) {
            btLike.setBackground(getResources().getDrawable(R.drawable.ufi_heart_active));
        } else {
            btLike.setBackground(getResources().getDrawable(R.drawable.ufi_heart));
        }

        btLike.setOnClickListener(new View.OnClickListener() {
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
                        onViewCreated(view, savedInstanceState);
                        Log.d(TAG, "saved like action");
                    }
                });
            }
        });

        ParseFile postedImage = post.getImage();
        if (postedImage != null) {
            String preURL = postedImage.getUrl();
            String completeURL = preURL.substring(0, 4) + "s" + preURL.substring(4, preURL.length());
            Glide.with(getContext())
                    .load(completeURL)
                    .into(ivPostedImage);
        }

        String rawDate = post.getCreatedAt().toString();
        tvCreatedAt.setText(getRelativeTimeAgo(rawDate));

        ParseFile profileImage = author.getParseFile("profileImage");
        if (profileImage != null) {
            String preURL = profileImage.getUrl();
            String completeURL = preURL.substring(0, 4) + "s" + preURL.substring(4, preURL.length());
            Glide.with(getContext())
                    .load(completeURL)
                    .into(ivProfileImage);
        }

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(author);
            }
        });

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(author);
            }
        });
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

    private void goToProfile(ParseUser user) {
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        Fragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, fragment).addToBackStack(null).commit();
    }
}

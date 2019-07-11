package com.example.parsetagram.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        fragmentManager = getFragmentManager();
        post = getArguments().getParcelable("post");
        final ParseUser user = post.getUser();

        tvUsername.setText(user.getUsername());
        tvDescription.setText(post.getDescription());

        Integer likesCount = post.getLikes();
        if (likesCount != null && likesCount > 0) {
            tvLikes.setText(post.getLikes().toString());
        } else {
            tvLikes.setText("");
        }

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

        ParseFile profileImage = user.getParseFile("profileImage");
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
                goToProfile(user);
            }
        });

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(user);
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
        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, fragment).commit();
    }
}

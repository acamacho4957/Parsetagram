package com.example.parsetagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.parsetagram.MainActivity;
import com.example.parsetagram.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";

    @BindView(R.id.btLogout) Button btLogout;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.d(TAG, currentUser.getUsername());

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        ParseFile profileImage = currentUser.getParseFile("profileImage");
        if (profileImage != null) {
            String preURL = profileImage.getUrl();
            String completeURL = preURL.substring(0, 4) + "s" + preURL.substring(4, preURL.length());
            Log.d(TAG, completeURL);
            Glide.with(getContext())
                    .load(completeURL)
                    .into(ivProfileImage);
        } else {
            Log.d(TAG, "no profile image");
        }
    }
}

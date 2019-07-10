package com.example.parsetagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.parsetagram.fragments.ComposeFragment;
import com.example.parsetagram.fragments.FeedFragment;
import com.example.parsetagram.fragments.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = "HomeActivity";

    @BindView(R.id.bnvTabs) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment feedFragment = new FeedFragment();
        final Fragment composeFragment = new ComposeFragment();
        final Fragment profileFragment = new ProfileFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.homeTab:
                                fragment = feedFragment;
                                break;
                            case R.id.composeTab:
                                fragment = composeFragment;
                                break;
                            case R.id.profileTab:
                                fragment = profileFragment;
                                break;
                            default:
                                fragment = feedFragment;
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, fragment).commit();
                        return true;
                    }
                });

        bottomNavigationView.setSelectedItemId(R.id.homeTab);
    }
}

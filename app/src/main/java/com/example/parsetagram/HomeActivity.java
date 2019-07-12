package com.example.parsetagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.parsetagram.fragments.ComposeFragment;
import com.example.parsetagram.fragments.FeedFragment;
import com.example.parsetagram.fragments.ProfileFragment;
import com.example.parsetagram.fragments.SettingsFragment;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = "HomeActivity";

    @BindView(R.id.bnvTabs) BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();

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
                                Bundle args = new Bundle();
                                args.putParcelable("user", ParseUser.getCurrentUser());
                                fragment = profileFragment;
                                fragment.setArguments(args);
                                break;
                            default:
                                fragment = feedFragment;
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, fragment).addToBackStack(null).commit();
                        return true;
                    }
                });

        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, feedFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onSettingsAction(MenuItem mi) {
        final Fragment profileFragment = new SettingsFragment();
        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, profileFragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    Log.i("MainActivity", "popping backstack");
                    fragmentManager.popBackStack();
                } else {
                    Log.i("MainActivity", "nothing on backstack, calling super");
                    super.onBackPressed();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}

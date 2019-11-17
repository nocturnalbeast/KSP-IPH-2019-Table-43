package com.invictus.reehbayse.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.invictus.reehbayse.adapters.InboxAdapter;
import com.invictus.reehbayse.fragments.InboxPagerFragment;
import com.invictus.reehbayse.R;
import com.invictus.reehbayse.xmpp.XMPP;

public class InboxActivity extends AppCompatActivity
        implements InboxPagerFragment.OnFragmentInteractionListener {

    private ViewPager mViewPager;
    private TextView tvNotifications;
    public static FloatingActionButton fab;
    private int[] iconIntArray = {R.drawable.ic_new_chat, R.drawable.ic_new_group};

    private static InboxActivity instance;

    public static InboxActivity getInstance() {
        return instance;
    }

    @Override
    protected void onStart() {
        super.onStart();
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(XMPP.username);

        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        mViewPager = (ViewPager) findViewById(R.id.viewPagerInbox);
        mViewPager.setAdapter(new InboxAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_inbox);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_groups);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_threads, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_notification);

        View actionView = MenuItemCompat.getActionView(menuItem);
        tvNotifications = (TextView) actionView.findViewById(R.id.tvNotification);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log_out:
                XMPP.disconnect();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(XMPP.AUTH, false).apply();

                startActivity(new Intent(InboxActivity.this, SplashActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void updateBadge() {
        int count = XMPP.roster.getEntryCount();

        if (count > 0) {
            tvNotifications.setText(String.valueOf(count));
            if (tvNotifications.getVisibility() != View.VISIBLE) {
                tvNotifications.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }

        } else {
            if (tvNotifications.getVisibility() != View.GONE) {
                tvNotifications.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }
        }
    }

    protected void animateFab(final int position) {
        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                switch (position) {
                    case 0:
                        fab.setImageResource(R.drawable.ic_new_chat);
                        break;
                    case 1:
                        fab.setImageResource(R.drawable.ic_new_group);
                        break;
                }

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

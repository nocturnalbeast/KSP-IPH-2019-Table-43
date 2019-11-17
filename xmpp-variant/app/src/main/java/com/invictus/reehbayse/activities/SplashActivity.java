package com.invictus.reehbayse.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.invictus.reehbayse.xmpp.XMPP;

public class SplashActivity extends AppCompatActivity {

    private String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean auth = prefs.getBoolean(XMPP.AUTH, false);

        if (auth) {
            Log.d(TAG, "--------------------- old user");

            XMPP.username = prefs.getString(XMPP.USERNAME, "");
            XMPP.password = prefs.getString(XMPP.PASSWORD, "");

            XMPP.getInstance(SplashActivity.this).init();
        } else {
            Log.d(TAG, "--------------------- new user");
            startActivity(new Intent(SplashActivity.this, StartActivity.class));
            finish();
        }
    }
}
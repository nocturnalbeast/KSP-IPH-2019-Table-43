package com.invictus.unichat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.perf.metrics.AddTrace

import com.invictus.unichat.auth.LoginActivity
import com.invictus.unichat.chat.ChatsActivity
import com.invictus.unichat.onboarding.IntroActivity


class SplashActivity : AppCompatActivity() {

    @AddTrace(name = "AppLaunchTrace", enabled = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set up a sharedpref variable to signal non-first-time launches
        val preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        if (preferences.getBoolean("FIRST_TIME_LAUNCH", true)) {
            preferences.edit(commit = true) { putBoolean("FIRST_TIME_LAUNCH", false) }
            val moveToIntro = Intent(this, IntroActivity::class.java)
            startActivity(moveToIntro)
        }

        // move to either authorization or chat window on the basis of whether the user is already logged in or not
        else {
            if (FirebaseAuth.getInstance().currentUser == null) {
                val moveToAuth = Intent(this, LoginActivity::class.java)
                startActivity(moveToAuth)
            } else {
                val moveToChat = Intent(this, ChatsActivity::class.java)
                startActivity(moveToChat)
            }
        }

        // destroy this activity, we don't need it in the activity stack
        finish()

    }
}
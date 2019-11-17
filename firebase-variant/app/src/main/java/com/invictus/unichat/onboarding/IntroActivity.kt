package com.invictus.unichat.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import java.util.*

import com.invictus.unichat.auth.LoginActivity
import com.invictus.unichat.R

import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {

    private var position = 0
    private var screenPager: ViewPager? = null
    private lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    private lateinit var btnAnim: Animation
    private lateinit var btnAnimRev: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // link to the layout
        setContentView(R.layout.activity_intro)

        // init animations for the get started button
        btnAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)
        btnAnimRev = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation_rev)

        // populate a list with screenItems
        val screenList = ArrayList<ScreenItem>()
        screenList.add(ScreenItem(getString(R.string.onboarding_one_title), getString(R.string.onboarding_one_description), R.drawable.onboarding_one))
        screenList.add(ScreenItem(getString(R.string.onboarding_two_title), getString(R.string.onboarding_two_description), R.drawable.onboarding_two))
        screenList.add(ScreenItem(getString(R.string.onboarding_three_title), getString(R.string.onboarding_three_description), R.drawable.onboarding_three))
        screenList.add(ScreenItem(getString(R.string.onboarding_four_title), getString(R.string.onboarding_four_description), R.drawable.onboarding_four))
        screenList.add(ScreenItem(getString(R.string.onboarding_five_title), getString(R.string.onboarding_five_description), R.drawable.onboarding_five))
        screenList.add(ScreenItem(getString(R.string.onboarding_ready_title), getString(R.string.onboarding_ready_description), R.drawable.onboarding_ready))

        // init viewPager
        screenPager = findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(this, screenList)
        screenPager!!.adapter = introViewPagerAdapter

        // init tabLayout with viewPager
        tab_indicator.setupWithViewPager(screenPager)

        // what happens when the next button is clicked
        btn_next.setOnClickListener {
            position = screenPager!!.currentItem
            if (position < screenList.size) {
                position++
                screenPager!!.currentItem = position
            }
            if (position == screenList.size - 1) {
                loadLastScreen()
            }
        }

        // defining the behavior of the tabLayout
        tab_indicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == screenList.size - 1) {
                    loadLastScreen()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                if (tab.position == screenList.size - 1) {
                    unloadLastScreen()
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
                if (tab.position == screenList.size - 1) {
                    loadLastScreen()
                }
            }
        })

        // what happens when the get started button is clicked
        btn_get_started.setOnClickListener {
            val moveToAuth = Intent(this, LoginActivity::class.java)
            startActivity(moveToAuth)
            finish()
        }

        // what happens when the skip button is clicked
        tv_skip.setOnClickListener {
            screenPager!!.currentItem = screenList.size
        }

    }

    // what happens on the last screen - show get started button and hide other controls
    private fun loadLastScreen() {

        btn_next.visibility = View.INVISIBLE
        btn_get_started.visibility = View.INVISIBLE
        tv_skip.visibility = View.INVISIBLE
        tab_indicator.visibility = View.INVISIBLE
        // animate the get started button
        btn_get_started.animation = btnAnim
        btn_get_started.visibility = View.VISIBLE

    }

    // what happens when you go back from the last screen - undoing the changes in the prev function
    private fun unloadLastScreen() {

        btn_next.visibility = View.VISIBLE
        btn_get_started.visibility = View.VISIBLE
        tv_skip.visibility = View.VISIBLE
        tab_indicator.visibility = View.VISIBLE
        // animate the get started button, but in reverse
        btn_get_started.animation = btnAnimRev
        btn_get_started.visibility = View.INVISIBLE

    }

}
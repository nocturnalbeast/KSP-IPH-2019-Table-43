package com.invictus.unichat.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

import com.invictus.unichat.R

import kotlinx.android.synthetic.main.layout_screen.view.*

class IntroViewPagerAdapter(internal var ctx: Context, internal var screenList: List<ScreenItem>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen = inflater.inflate(R.layout.layout_screen, null)

        layoutScreen.intro_title.text = screenList[position].title
        layoutScreen.intro_description.text = screenList[position].description
        layoutScreen.intro_img.setImageResource(screenList[position].screenImg)

        container.addView(layoutScreen)

        return layoutScreen

    }

    override fun getCount(): Int {
        return screenList.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
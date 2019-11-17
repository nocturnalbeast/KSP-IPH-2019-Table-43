package com.invictus.unichat

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView

import com.bumptech.glide.Glide

import com.mikepenz.iconics.IconicsColor.Companion.colorRes
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize.Companion.dp
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerUIUtils

import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.twitter.TwitterEmojiProvider

import com.invictus.unichat.glide.GlideApp


// custom application class to handle loading of custom emoji and implement glide to load images in the sidebar
class CustomApp : Application() {

    override fun onCreate() {

        super.onCreate()

        // install the emoji
        EmojiManager.install(TwitterEmojiProvider())

        // create a glide-based imageloader
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {

            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable, tag: String?) {
                GlideApp.with(imageView.context).load(uri).centerCrop().placeholder(placeholder).into(imageView)
            }

            override fun cancel(imageView: ImageView) {
                Glide.with(imageView.context).clear(imageView)
            }

            override fun placeholder(ctx: Context, tag: String?): Drawable {

                return when (tag) {
                    DrawerImageLoader.Tags.PROFILE.name -> DrawerUIUtils.getPlaceHolder(ctx)
                    DrawerImageLoader.Tags.ACCOUNT_HEADER.name -> IconicsDrawable(ctx).iconText(" ").backgroundColor(colorRes(R.color.colorBackground)).size(dp(56))
                    "customUrlItem" -> IconicsDrawable(ctx).iconText(" ").backgroundColor(colorRes(R.color.colorBackground)).size(dp(56))
                    else -> super.placeholder(ctx, tag)
                }

            }

        })
    }
}
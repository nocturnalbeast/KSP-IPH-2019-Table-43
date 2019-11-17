package com.invictus.unichat.recyclerview.item

import android.content.Context

import com.invictus.unichat.glide.GlideApp
import com.invictus.unichat.model.User
import com.invictus.unichat.R
import com.invictus.unichat.util.StorageUtil

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

import kotlinx.android.synthetic.main.item_person.*

class PersonItem(val person: User, val userID: String, private val context: Context) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text = person.name
        viewHolder.textView_bio.text = person.bio
        if (person.profilePicturePath != null) {
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(person.profilePicturePath))
                .placeholder(R.drawable.user_image_placeholder)
                .into(viewHolder.imageView_profile_picture)
        }
    }

    override fun getLayout() = R.layout.item_person

}
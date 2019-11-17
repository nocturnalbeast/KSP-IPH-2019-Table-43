package com.invictus.unichat.recyclerview.item

import android.content.Context
import java.text.SimpleDateFormat

import com.google.firebase.auth.FirebaseAuth

import com.invictus.unichat.model.ImageMessage
import com.invictus.unichat.R
import com.invictus.unichat.glide.GlideApp
import com.invictus.unichat.util.EncryptionWrapper
import com.invictus.unichat.util.StorageUtil

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

import kotlinx.android.synthetic.main.item_image_message_to.*

class ImageMessageItem(
    val message: ImageMessage,
    val key: String,
    val context: Context)

    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        var islandRef = StorageUtil.pathToReference(message.imagePath)

        val FOUR_MEGABYTE: Long = 1024 * 1024 * 4
        islandRef.getBytes(FOUR_MEGABYTE).addOnSuccessListener { encImageBytes ->
            val decImageBytes = EncryptionWrapper.decryptByteArray(encImageBytes, key)
            GlideApp.with(context)
                .load(decImageBytes)
                .placeholder(android.R.drawable.ic_dialog_alert)
                .into(viewHolder.imageView_message_image)
        }.addOnFailureListener {
            GlideApp.with(context)
                .load(android.R.drawable.ic_dialog_alert)
                .placeholder(android.R.drawable.ic_dialog_alert)
                .into(viewHolder.imageView_message_image)
        }
        setTimeText(viewHolder)
    }

    private fun setTimeText(viewHolder: ViewHolder) {
        val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text = dateFormat.format(message.time)
    }

    override fun getLayout(): Int {
        return if (message.senderID == FirebaseAuth.getInstance().currentUser?.uid) R.layout.item_image_message_from else R.layout.item_image_message_to
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is ImageMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? ImageMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

}
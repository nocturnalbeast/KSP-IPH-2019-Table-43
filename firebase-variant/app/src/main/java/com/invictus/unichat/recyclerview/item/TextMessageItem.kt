package com.invictus.unichat.recyclerview.item

import android.content.Context
import java.text.SimpleDateFormat

import com.google.firebase.auth.FirebaseAuth

import com.invictus.unichat.model.TextMessage
import com.invictus.unichat.R

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

import kotlinx.android.synthetic.main.item_text_message_to.*

class TextMessageItem(
    val message: TextMessage,
    val context: Context)

    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_message_text.text = message.text
        setTimeText(viewHolder)
    }

    private fun setTimeText(viewHolder: ViewHolder) {
        val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text = dateFormat.format(message.time)
    }

    override fun getLayout(): Int {
        return if (message.senderID == FirebaseAuth.getInstance().currentUser?.uid) R.layout.item_text_message_from else R.layout.item_text_message_to
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is TextMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? TextMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

}
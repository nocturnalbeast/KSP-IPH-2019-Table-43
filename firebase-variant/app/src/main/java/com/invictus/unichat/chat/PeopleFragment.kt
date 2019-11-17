package com.invictus.unichat.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.perf.metrics.AddTrace

import com.invictus.unichat.AppConstants
import com.invictus.unichat.R
import com.invictus.unichat.recyclerview.item.PersonItem
import com.invictus.unichat.util.FirestoreUtil

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder

import kotlinx.android.synthetic.main.fragment_people.*


class PeopleFragment : Fragment() {

    private lateinit var userListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var peopleSection: Section

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        userListenerRegistration = FirestoreUtil.addUsersListener(this.activity!!, this::updateRecyclerView)
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    @AddTrace(name = "LoadPeopleTrace", enabled = true)
    private fun updateRecyclerView(items: List<Item>) {

        fun init() {
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if(shouldInitRecyclerView)
            init()
        else
            updateItems()

    }

    private val onItemClick = OnItemClickListener { item, view ->
        if (item is PersonItem) {
            val moveToChat = Intent(activity, ChatLogActivity::class.java)
            moveToChat.putExtra(AppConstants.USER_NAME, item.person.name)
            moveToChat.putExtra(AppConstants.USER_ID, item.userID)
            startActivity(moveToChat)
        }
    }

}

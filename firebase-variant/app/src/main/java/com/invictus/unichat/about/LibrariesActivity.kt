package com.invictus.unichat.about

import android.os.Bundle

import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.ui.LibsActivity

import com.invictus.unichat.R


class LibrariesActivity : LibsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // create the activity using the libraries we use and set the style accordingly
        intent = LibsBuilder().withActivityTheme(R.style.LibrariesTheme).intent(this)
        super.onCreate(savedInstanceState)

    }
}
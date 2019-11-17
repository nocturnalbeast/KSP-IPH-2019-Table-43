package com.invictus.unichat.chat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

import com.google.firebase.auth.FirebaseAuth

import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.mikepenz.materialdrawer.*
import com.mikepenz.materialdrawer.interfaces.ICrossfader
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.util.DrawerUIUtils
import com.mikepenz.materialize.util.UIUtils

import com.invictus.unichat.about.AboutActivity
import com.invictus.unichat.about.LibrariesActivity
import com.invictus.unichat.auth.LoginActivity
import com.invictus.unichat.R
import com.invictus.unichat.util.FirestoreUtil
import com.invictus.unichat.util.StorageUtil

import kotlinx.android.synthetic.main.activity_chats.*


class ChatsActivity : AppCompatActivity() {

    private lateinit var headerResult: AccountHeader
    private lateinit var result: Drawer
    private lateinit var crossfadeDrawerLayout: CrossfadeDrawerLayout

    // init the profiledraweritem with dummy data
    private var dummyProfile = ProfileDrawerItem().withName("").withEmail("").withIcon(R.drawable.user_image_placeholder).withIdentifier(100)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        // handle the appbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.app_name)

        // set the chat window as the default one on launch
        replaceFragment(PeopleFragment())

        // init the drawer header
        headerResult = AccountHeaderBuilder()
            .withActivity(this)
            .withCompactStyle(true)
            .withSelectionListEnabledForSingleProfile(false)
            .addProfiles(dummyProfile)
            .withSavedInstance(savedInstanceState)
            .build()

        // init drawer
        result = DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .withHasStableIds(true)
            .withDrawerLayout(R.layout.cf_drawer)
            .withDrawerWidthDp(72)
            .withGenerateMiniDrawer(true)
            .withActionBarDrawerToggleAnimated(true)
            .withAccountHeader(headerResult)
            .withSelectedItem(-1)
            .addDrawerItems(
                PrimaryDrawerItem().withName("Edit Profile").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(1),
                ExpandableDrawerItem().withName("More").withIcon(GoogleMaterial.Icon.gmd_grade).withIdentifier(2).withSelectable(false).withSubItems(
                    SecondaryDrawerItem().withName("About").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(200),
                    SecondaryDrawerItem().withName("Libraries").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_code).withIdentifier(201)
                ),
                PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_exit_to_app).withIdentifier(3).withSelectable(false),
                PrimaryDrawerItem().withName("Exit").withIcon(GoogleMaterial.Icon.gmd_close).withIdentifier(4).withSelectable(false)
            )
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {

                    var intent: Intent? = null
                    when {
                        drawerItem.identifier == 1L -> replaceFragment(ProfileFragment())
                        drawerItem.identifier == 200L -> intent = Intent(this@ChatsActivity, AboutActivity::class.java)
                        drawerItem.identifier == 201L -> intent = Intent(this@ChatsActivity, LibrariesActivity::class.java)
                        drawerItem.identifier == 3L -> logout()
                        drawerItem.identifier == 4L -> closeApp()
                    }
                    if (intent != null) {
                        this@ChatsActivity.startActivity(intent)
                    }
                    return false

                }
            })
            .withSavedInstance(savedInstanceState)
            .withShowDrawerOnFirstLaunch(false)
            .build()

        crossfadeDrawerLayout = result.drawerLayout as CrossfadeDrawerLayout
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this))
        val miniResult = result.miniDrawer!!
        val view = miniResult.build(this)
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background))

        crossfadeDrawerLayout.smallView.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        miniResult.withCrossFader(object : ICrossfader {

            override val isCrossfaded: Boolean
                get() = crossfadeDrawerLayout.isCrossfaded

            override fun crossfade() {
                val isFaded = isCrossfaded
                crossfadeDrawerLayout.crossfade(400)
                if (isFaded) {
                    result.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
        })

        updateDrawer()
        // reflect the changes in the minidrawer as well
        miniResult.createItems()

    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        // add the values which need to be saved from the drawer to the bundle
        if (::result.isInitialized) {
            outState = result.saveInstanceState(outState)
        }
        // add the values which need to be saved from the accountHeader to the bundle
        if (::headerResult.isInitialized) {
            outState = headerResult.saveInstanceState(outState)
        }
        super.onSaveInstanceState(outState)
    }

    // handler for the back button press
    override fun onBackPressed() {

        // close the drawer first and if the drawer is closed, close the activity
        if (result.isDrawerOpen) {

            result.closeDrawer()

        } else {

            // also handle the case wherein the user is in the profile edit screen and wants to go back
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_layout)
            if (currentFragment is ProfileFragment) {
                replaceFragment(PeopleFragment())
            } else {
                super.onBackPressed()
            }

        }
    }

    // function to update the userprofileitem with the current user's info
    fun updateDrawer() {
        FirestoreUtil.getCurrentUser { currentUser ->
            if (currentUser.profilePicturePath != null) {
                StorageUtil.pathToReference(currentUser.profilePicturePath).downloadUrl
                    .addOnSuccessListener {
                        // update the header for the main drawer
                        headerResult.updateProfile(ProfileDrawerItem()
                            .withName(currentUser.name)
                            .withEmail(currentUser.bio)
                            .withIcon(it)
                            .withIdentifier(100)
                        )
                    }
            }
        }
    }

    // a simple fragment manager thingy
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, fragment)
            .commit()
    }

    // simple function to let the user sign out of the application
    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val moveToLogin = Intent(this, LoginActivity::class.java)
        moveToLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(moveToLogin)
        finish()
    }

    // short function to display a message and close the application
    private fun closeApp() {
        Snackbar.make(findViewById(android.R.id.content), R.string.exit_msg, Snackbar.LENGTH_SHORT).show()
        val handler = Handler()
        handler.postDelayed({
            finishAndRemoveTask()
        }, 1000)
    }

}
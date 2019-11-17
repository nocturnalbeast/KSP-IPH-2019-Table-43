package com.invictus.unichat.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream

import com.invictus.unichat.glide.GlideApp
import com.invictus.unichat.R
import com.invictus.unichat.util.FirestoreUtil
import com.invictus.unichat.util.StorageUtil

import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        view.apply {
            profile_image_select.setOnClickListener {
                val selectImage = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(selectImage, "Select Profile Picture"), RC_SELECT_IMAGE)
            }
            save_button.setOnClickListener {
                showProgressBar()
                if (::selectedImageBytes.isInitialized)
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(username_edit_text.text.toString(), bio_edit_text.text.toString(), imagePath)
                        (activity as ChatsActivity).updateDrawer()
                    }
                else
                    FirestoreUtil.updateCurrentUser(username_edit_text.text.toString(), bio_edit_text.text.toString(), null)
                hideProgressBar()
            }
            cancel_button.setOnClickListener {
                fragmentManager!!.beginTransaction().replace(R.id.fragment_layout, PeopleFragment()).commit()
            }
        }

        return view

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImageBytes)
                .into(profile_image_select)

            pictureJustChanged = true

        }
    }

    override fun onStart() {

        super.onStart()

        FirestoreUtil.getCurrentUser {user ->
            if(this@ProfileFragment.isVisible) {
                username_edit_text.setText(user.name)
                bio_edit_text.setText(user.bio)
                if (!pictureJustChanged && user.profilePicturePath != null) {
                    GlideApp.with(this)
                        .load(StorageUtil.pathToReference(user.profilePicturePath))
                        .placeholder(R.drawable.user_image_placeholder)
                        .into(profile_image_select)
                }
            }
        }

    }

    private fun showProgressBar() {
        button_layout.visibility = View.GONE
        progressbar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressbar.visibility = View.GONE
        button_layout.visibility = View.VISIBLE
    }

}

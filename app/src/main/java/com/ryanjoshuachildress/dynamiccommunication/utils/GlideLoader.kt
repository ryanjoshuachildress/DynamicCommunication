package com.ryanjoshuachildress.dynamiccommunication.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ryanjoshuachildress.dynamiccommunication.R
import java.io.IOException
import java.net.URI

class GlideLoader (val context: Context){

    fun loadUserPicture(imageURI: Uri, imageView: ImageView) {
        try {
            Glide
                .with(context)
                .load(Uri.parse(imageURI.toString()))
                .centerCrop()
                .placeholder(R.drawable.default_user_image)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
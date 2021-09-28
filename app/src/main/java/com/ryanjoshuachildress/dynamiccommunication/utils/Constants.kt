package com.ryanjoshuachildress.dynamiccommunication.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ryanjoshuachildress.dynamiccommunication.activities.UserProfileActivity

object Constants {
    const val READ_STORAGE_PERMISSION_CODE: Int = 100
    const val PICK_IMAGE_REQUEST_CODE: Int = 203

    const val USERS: String = "users"
    const val LOGS: String = "logs"

    const val DYNAMICCOMMUNICATION_PREFERENCES: String = "DynamicCommunicationPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    const val MALE: String = "Male"
    const val FEMALE: String = "Female"
    const val OTHER: String = "Other"

    const val MOBILE: String = "mobile"
    const val IMAGE: String = "image"
    const val USER_PROFILE_IMAGE: String = "user_profile_image"
    const val GENDER: String = "gender"
    const val FIRSTNAME: String = "firstName"
    const val LASTNAME: String = "LastName"



    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}
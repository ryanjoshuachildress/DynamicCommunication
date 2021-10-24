package com.ryanjoshuachildress.dynamiccommunication.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val READ_STORAGE_PERMISSION_CODE: Int = 100
    const val PICK_IMAGE_REQUEST_CODE: Int = 203

    const val USERS: String = "users"
    const val LOGS: String = "logs"
    const val YNMQUESTION = "YNMQuestion"
    const val YNMANSWER = "YNMAnswer"

    const val DYNAMICCOMMUNICATION_PREFERENCES: String = "DynamicCommunicationPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    const val MALE: String = "Male"
    const val FEMALE: String = "Female"
    const val OTHER: String = "Other"

    const val MOBILE: String = "mobile"
    const val IMAGE: String = "image"
    const val USER_PROFILE_IMAGE: String = "user_profile_image/"
    const val GENDER: String = "gender"
    const val FIRSTNAME: String = "firstName"
    const val LASTNAME: String = "lastName"
    const val PROFILE_COMPLETED: String = "profileCompleted"
    const val RATING: String = "rating"

    const val ANSWER_YES: String = "Yes"
    const val ANSWER_NO: String = "No"
    const val ANSWER_MAYBE: String = "Maybe"



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
package com.ryanjoshuachildress.dynamiccommunication.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ryanjoshuachildress.dynamiccommunication.ui.activities.RegisterActivity
import com.ryanjoshuachildress.dynamiccommunication.ui.activities.UserProfileActivity
import com.ryanjoshuachildress.dynamiccommunication.models.LogData
import com.ryanjoshuachildress.dynamiccommunication.models.User
import com.ryanjoshuachildress.dynamiccommunication.models.YNMAnswer
import com.ryanjoshuachildress.dynamiccommunication.models.YNMQuestion
import com.ryanjoshuachildress.dynamiccommunication.ui.activities.LoginActivity
import com.ryanjoshuachildress.dynamiccommunication.ui.activities.SettingsActivity
import com.ryanjoshuachildress.dynamiccommunication.utils.Constants
import kotlinx.android.synthetic.main.activity_view_log.*

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }

    }

    fun addYNMQuestion(question: String)
    {
        var questionInfo = YNMQuestion()
        var timeStamp = System.currentTimeMillis().toString()
        questionInfo.id = timeStamp
        questionInfo.question = question
        mFireStore.collection(Constants.YNMQUESTION)
            .document(timeStamp)
            .set(questionInfo, SetOptions.merge())
            .addOnSuccessListener {
                logToDatabase(LogData(1,"${timeStamp} YNMQuestion Added"))
            }
            .addOnFailureListener{ e ->
                Log.e(
                    "this",
                    "Error while registering the user.",
                    e
                )
            }

    }

    fun answewrYNMQuestion(questionID: String, question:String, answer:String) {
        var questionInfo = YNMAnswer()
        questionInfo.answer = answer
        questionInfo.questionID = questionID
        questionInfo.question = question
        questionInfo.UserID = getCurrentUserID()
        mFireStore.collection(Constants.YNMANSWER)
            .add(questionInfo)
            .addOnSuccessListener {

            }
            .addOnFailureListener{ e ->
                Log.e(
                    "Test",
                    "Error while writing log data.",
                    e
                )
            }
    }

    fun logToDatabase(logData: LogData) {
        mFireStore.collection(Constants.LOGS)
            .add(logData)
            .addOnSuccessListener {

            }
            .addOnFailureListener{ e ->
                Log.e(
                    "Test",
                    "Error while writing log data.",
                    e
                )
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if(currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }



    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.DYNAMICCOMMUNICATION_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when(activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is UserProfileActivity -> {
                        activity.populateUserDataFromFirebase(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailSuccess(user)
                    }
                }

            }
            .addOnFailureListener{
                when(activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity){
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener{e ->
                when (activity){
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }

    }
    fun uploadImageTOCloudStorage(activity: Activity, imageFileURI: Uri?){
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(activity,imageFileURI)
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()

            )
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    when (activity) {
                        is UserProfileActivity ->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
                .addOnFailureListener{exception ->
                    when (activity) {
                        is UserProfileActivity ->{
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(
                        activity.javaClass.simpleName,
                        exception.message,
                        exception
                    )
                }
        }
    }

    fun getTotalUsersCount(): Int {
        return (0..100000).random()

    }
    fun getTotalQuestionsCount(): Int {
        return (0..100000).random()

    }
    fun getTotalAnswersCount(): Int {
        return (0..100000).random()

    } }
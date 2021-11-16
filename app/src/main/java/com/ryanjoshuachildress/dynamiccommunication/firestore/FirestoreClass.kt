package com.ryanjoshuachildress.dynamiccommunication.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ryanjoshuachildress.dynamiccommunication.models.*
import com.ryanjoshuachildress.dynamiccommunication.ui.activities.*
import com.ryanjoshuachildress.dynamiccommunication.utils.Constants

class FirestoreClass {

    /*

    This class writes and reads data from the firestore database

    */

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        /*

    This function writes the user object to firestore on sucessful registration

    might be aniquated and possibly be removed in the future

    */
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }

    }

    fun addYNMQuestion(activity: Activity, question: String){
        /*

    This function writes the YNMQuestion object to firestore

    */
        val questionInfo = YNMQuestion()
        val timeStamp = System.currentTimeMillis().toString()
        questionInfo.id = timeStamp
        questionInfo.question = question
        mFireStore.collection(Constants.YNMQUESTION)
            .document(timeStamp)
            .set(questionInfo, SetOptions.merge())
            .addOnSuccessListener {
                logToDatabase(LogData(1,"$timeStamp YNMQuestion Added"))
            }
            .addOnFailureListener{
                when(activity) {
                    is MainActivity -> {
                        activity.showSnackbar("Could not add question to database",true)
                        logToDatabase(LogData(3,"Could not add question to database"))
                    }
                }

            }

    }

    fun updateYNMQuestion(activity: Activity, question: YNMQuestion){
        /*

    This function writes the YNMQuestion object to firestore

    */

        mFireStore.collection(Constants.YNMQUESTION)
            .document(question.id)
            .set(question, SetOptions.merge())
            .addOnSuccessListener {
                logToDatabase(LogData(1,"$question.id YNMQuestion Updated"))
            }
            .addOnFailureListener{
                when(activity) {
                    is MainActivity -> {
                        activity.showSnackbar("Could not update question",true)
                        logToDatabase(LogData(3,"Could not update question"))
                    }
                }

            }

    }

    fun answerYNMQuestion(questionID: String, question:String, answer:String) {
        /*

    This function writes the YNMAnswer object to firestore

    */
        val questionInfo = YNMAnswer()
        questionInfo.answer = answer
        questionInfo.questionID = questionID
        questionInfo.question = question
        questionInfo.UserID = getCurrentUserID()
        mFireStore.collection(Constants.YNMANSWER)
            .add(questionInfo)
            .addOnSuccessListener {

            }
            .addOnFailureListener{

            }
    }

    fun getAllUnansweredYNMQuestions(activity: Activity) {
        /*

    This function gets all the questions that the user has not answered

    */
        val allQuestions = ArrayList<YNMQuestion>()
        mFireStore.collection(Constants.YNMQUESTION)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val mQuestion = document.toObject(YNMQuestion::class.java)
                  if(mQuestion.approved) {
                        allQuestions.add(mQuestion)
                   }
                }
                val allYNMAnswer = ArrayList<YNMAnswer>()
                mFireStore.collection(Constants.YNMANSWER)
                    .whereEqualTo("userID",getCurrentUserID())
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            allYNMAnswer.add(document.toObject(YNMAnswer::class.java))
                        }
                        when(activity) {
                            is MainActivity -> {
                                activity.validateYNMQuestions(allYNMAnswer,allQuestions)
                            }
                        }
                    }
                    .addOnFailureListener {

                    }
            }
            .addOnFailureListener {

            }
    }

    fun getAllUnapprovedYNMQuestions(activity: Activity) {
        val allQuestions = ArrayList<YNMQuestion>()
        mFireStore.collection(Constants.YNMQUESTION)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val mQuestion = document.toObject(YNMQuestion::class.java)
                    if(!mQuestion.approved)
                    {
                        allQuestions.add(mQuestion)
                    }
                }
                when(activity) {
                    is QuestionActivity -> {
                        activity.loadAllQuestions(allQuestions)
                    }
                }
            }
            .addOnFailureListener {
            }
    }

    fun getAllNotifications(activity: Activity) {
        val allNotifications = ArrayList<NotificationInfo>()
        mFireStore.collection(Constants.NOTIFICATIONS)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val mNotification = document.toObject(NotificationInfo::class.java)
                    if(mNotification.notificationActive)
                    {
                        allNotifications.add(mNotification)
                    }
                }
                when(activity) {
                    is NotificationActivity -> {
                        activity.UpdateUI(allNotifications)
                    }
                }
            }
            .addOnFailureListener {
            }
    }

    fun addNotification(activity: Activity,notificationInfo: NotificationInfo) {
        /*

    This function writes the NotificationInfo object to firestore

    */
        mFireStore.collection(Constants.NOTIFICATIONS)
            .add(notificationInfo)
            .addOnSuccessListener {
                when(activity) {
                    is NotificationActivity -> {
                        activity.notificationWriteSucess()
                    }
                }
            }
            .addOnFailureListener{
                when(activity) {
                    is NotificationActivity -> {
                        activity.notificationWriteFailure(it.message)
                    }
                }
            }
    }

    fun getQuestionDetails(activity: Activity, question: YNMQuestion) {
        val allAnswers = ArrayList<YNMAnswer>()
        var answerCount = 0
        var yesCount = 0
        var noCount = 0
        var maybeCount = 0

        mFireStore.collection(Constants.YNMANSWER)
            .whereEqualTo("questionID",question.id)
            .get()

            .addOnSuccessListener { result ->
                for((index,document) in result.withIndex()) {
                    allAnswers.add(document.toObject(YNMAnswer::class.java))
                    answerCount += 1
                    if (allAnswers[index].answer == Constants.ANSWER_YES) {
                        yesCount += 1
                    }
                    if (allAnswers[index].answer == Constants.ANSWER_NO) {
                        noCount += 1
                    }
                    if (allAnswers[index].answer == Constants.ANSWER_MAYBE) {
                        maybeCount += 1
                    }
                }
                when(activity) {
                    is MainActivity -> {
                        activity.showYNMQuestionDetails(question.question,answerCount,yesCount,noCount,maybeCount)
                    }
                }

            }
            .addOnFailureListener {
            }
    }

    fun logToDatabase(logData: LogData) {
        mFireStore.collection(Constants.LOGS)
            .add(logData)
            .addOnSuccessListener {

            }
            .addOnFailureListener{

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
                    is MainActivity -> {
                        activity.LoadUserDetails(user)
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
            .addOnFailureListener{
                when (activity){
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }

    }

    fun uploadImageTOCloudStorage(activity: Activity, imageFileURI: Uri?){
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(activity,imageFileURI)
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->

            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    when (activity) {
                        is UserProfileActivity ->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
                .addOnFailureListener{
                    when (activity) {
                        is UserProfileActivity ->{
                            activity.hideProgressDialog()
                        }
                    }
                }
        }
    }

    fun getTotalCounts(activity: Activity) {
        var iCountUsers = 0
        var iCountQuestions = 0
        var iCountAnswers = 0
        mFireStore.collection(Constants.USERS)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    iCountUsers++
                }
                mFireStore.collection(Constants.YNMQUESTION)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            iCountQuestions++
                        }
                        mFireStore.collection(Constants.YNMANSWER)
                            .get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    iCountAnswers++
                                }

                                when(activity) {
                                    is MainActivity -> {
                                        activity.updateUI(iCountUsers,iCountQuestions,iCountAnswers)
                                    }
                                }
                            }

                    }

            }
    }
}
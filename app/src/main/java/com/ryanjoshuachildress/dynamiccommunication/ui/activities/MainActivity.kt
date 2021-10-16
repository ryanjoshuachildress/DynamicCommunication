package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityMainBinding
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.YNMAnswer
import com.ryanjoshuachildress.dynamiccommunication.models.YNMQuestion
import com.ryanjoshuachildress.dynamiccommunication.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var analytics: FirebaseAnalytics

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics

        val sharedPreferences = getSharedPreferences(Constants.DYNAMICCOMMUNICATION_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!


        btnLaunchYNMAdd.setOnClickListener{
            showYNMAdd()
        }

        btnLaunchYNM.setOnClickListener{
                showProgressDialog("Getting Question")
                FirestoreClass().getAllUnansweredYNMQuestions(this)
        }

        FirestoreClass().getTotalCounts(this)



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_log -> {
                startActivity(Intent(this, ViewLogActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

fun validateYNMQuestions(allAnswers: ArrayList<YNMAnswer>,allQuestions: ArrayList<YNMQuestion>) {
        var resultQuestions = ArrayList<YNMQuestion>()
        var bfound = false
        for(question in allQuestions){
            bfound = false
            for(answer in allAnswers){
                if(question.id == answer.questionID){
                    bfound = true
                }
            }
            if(!bfound){
                resultQuestions.add(question)
            }
        }
        hideProgressDialog()
        showYNM(resultQuestions)
    }

    fun showYNM(allQuestions: ArrayList<YNMQuestion>)
    {
        if(allQuestions.size > 0) {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.layout_ynm_question)

            val btnYes = dialog.findViewById(R.id.btnYes) as Button
            val btnNo = dialog.findViewById(R.id.btnNo) as Button
            val btnMaybe = dialog.findViewById(R.id.btnMaybe) as Button
            val tvQuestion = dialog.findViewById(R.id.tvQuestion) as TextView
            val ivQuestionImage = dialog.findViewById(R.id.ivQuestionImage) as ImageView

            val randomIndex = (0..(allQuestions.size - 1)).random()
            tvQuestion.text = allQuestions[randomIndex].question.toString()


            btnYes.setOnClickListener {
                FirestoreClass().answerYNMQuestion(
                    allQuestions[randomIndex].id,
                    allQuestions[randomIndex].question,
                    Constants.ANSWER_YES
                )
                dialog.dismiss()
            }
            btnNo.setOnClickListener {
                FirestoreClass().answerYNMQuestion(
                    allQuestions[randomIndex].id,
                    allQuestions[randomIndex].question,
                    Constants.ANSWER_NO
                )
                dialog.dismiss()
            }
            btnMaybe.setOnClickListener {
                FirestoreClass().answerYNMQuestion(
                    allQuestions[randomIndex].id,
                    allQuestions[randomIndex].question,
                    Constants.ANSWER_MAYBE
                )
                dialog.dismiss()
            }
            if (dialog != null) {
                dialog.show()
            }
        }
        else{
            showErrorSnackbar("You have answered all questions. Add some or try again later",false)
        }
    }

    private fun showYNMAdd() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_ynm_question_add)

        val btnSave = dialog.findViewById(R.id.btnSave) as Button
        val btnExit = dialog.findViewById(R.id.btnExit) as Button
        val etQuestion = dialog.findViewById(R.id.etQuestion) as EditText
        btnSave.setOnClickListener {
            if(etQuestion.text.toString() != "") {
                FirestoreClass().addYNMQuestion(this,etQuestion.text.toString())
                dialog.dismiss()
            } else
            {
                showErrorSnackbar("Failed to add", true)
            }
        }
        btnExit.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun updateUI(userCount: Int, questionCount: Int, answerCount: Int){
        tvUserCount.text = userCount.toString()
        tvQuestionCount.text = questionCount.toString()
        tvAnswerCount.text = answerCount.toString()
        android.os.Handler().postDelayed({FirestoreClass().getTotalCounts(this)},200000)
    }
    override fun onBackPressed() {
        doubleBackToExit()
    }
}
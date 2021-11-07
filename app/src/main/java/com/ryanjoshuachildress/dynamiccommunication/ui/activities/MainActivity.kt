package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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


        btnLaunchYNMAdd.setOnClickListener{
            showYNMAdd()
        }

        btnLaunchYNM.setOnClickListener{
                showProgressDialog(getString(R.string.PleaseWait))
                FirestoreClass().getAllUnansweredYNMQuestions(this)
        }

        ivQuestionImage.setOnClickListener{
            startActivity(Intent(this, QuestionActivity::class.java))
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
        val resultQuestions = ArrayList<YNMQuestion>()
        var bFound: Boolean
        for(question in allQuestions){
            bFound = false
            for(answer in allAnswers){
                if(question.id == answer.questionID){
                    bFound = true
                }
            }
            if(!bFound){
                resultQuestions.add(question)
            }
        }
        hideProgressDialog()
        showYNM(resultQuestions)
    }

    fun showYNMQuestionDetails(question: String,answerCount: Int, yesCount: Int, noCount: Int, maybeCount: Int)
    {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_ynmquestion_details)
        val tvQuestionDetails = dialog.findViewById(R.id.tvQuestionDetails) as TextView
        val tvAnswerCount = dialog.findViewById(R.id.tvAnsweredCountDetails) as TextView
        val tvYesCount = dialog.findViewById(R.id.tvYesCountDetails) as TextView
        val tvYesCountPercentage = dialog.findViewById(R.id.tvYesCountDetailsPercentage) as TextView
        val tvNoCountPercentage = dialog.findViewById(R.id.tvNoCountDetailsPercentage) as TextView
        val tvMaybeCountPercentage = dialog.findViewById(R.id.tvMaybeCountDetailsPercentage) as TextView
        val tvNoCount = dialog.findViewById(R.id.tvNoCountDetails) as TextView
        val tvMaybeCount = dialog.findViewById(R.id.tvMaybeCountDetails) as TextView

        tvQuestionDetails.text = question
        tvAnswerCount.text = answerCount.toString()
        tvYesCount.text = yesCount.toString()
        //tvYesCountPercentage.text = ((answerCount.div(yesCount)).toFloat().times(100).toString()+"%")
        //tvNoCountPercentage.text = ((answerCount.div(noCount)).toFloat().times(100).toString()+"%")
        //tvMaybeCountPercentage.text = ((answerCount.div(maybeCount)).toFloat().times(100).toString()+"%")
        tvNoCount.text = noCount.toString()
        tvMaybeCount.text = maybeCount.toString()

            dialog.show()
            @Suppress("DEPRECATION")
            Handler().postDelayed(
                {
                    dialog.dismiss()
                },3000)
    }

    private fun showYNM(allQuestions: ArrayList<YNMQuestion>)
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

            val randomIndex = (0 until (allQuestions.size - 1)).random()
            tvQuestion.text = allQuestions[randomIndex].question


            btnYes.setOnClickListener {
                FirestoreClass().answerYNMQuestion(
                    allQuestions[randomIndex].id,
                    allQuestions[randomIndex].question,
                    Constants.ANSWER_YES
                )
                FirestoreClass().getQuestionDetails(this,allQuestions[randomIndex])
                dialog.dismiss()
            }
            btnNo.setOnClickListener {
                FirestoreClass().answerYNMQuestion(
                    allQuestions[randomIndex].id,
                    allQuestions[randomIndex].question,
                    Constants.ANSWER_NO
                )
                FirestoreClass().getQuestionDetails(this,allQuestions[randomIndex])

                dialog.dismiss()
            }
            btnMaybe.setOnClickListener {
                FirestoreClass().answerYNMQuestion(
                    allQuestions[randomIndex].id,
                    allQuestions[randomIndex].question,
                    Constants.ANSWER_MAYBE
                )
                FirestoreClass().getQuestionDetails(this,allQuestions[randomIndex])

                dialog.dismiss()

            }

                dialog.show()

        }
        else{
            showSnackbar(getString(R.string.answered_all_questions_error),false)
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
                showSnackbar(getString(R.string.failed_to_add_question_error), true)
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
        Handler().postDelayed({FirestoreClass().getTotalCounts(this)},2000)
    }
    override fun onBackPressed() {
        doubleBackToExit()
    }
}
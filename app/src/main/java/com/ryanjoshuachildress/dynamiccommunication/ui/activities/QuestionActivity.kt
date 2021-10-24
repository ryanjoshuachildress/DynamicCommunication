package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.YNMQuestion
import kotlinx.android.synthetic.main.activity_question.*

class QuestionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        FirestoreClass().getAllYNMQuestions(this)
    }

    fun populateQuestionSpinner(allQuestions: ArrayList<YNMQuestion>){
        val adapter: ArrayAdapter<YNMQuestion> = ArrayAdapter<YNMQuestion>(
            this,
            android.R.layout.simple_spinner_item, allQuestions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerQuestion.setAdapter(adapter)
    }
}
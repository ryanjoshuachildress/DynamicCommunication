package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.YNMQuestion
import kotlinx.android.synthetic.main.activity_question.*

class QuestionActivity : BaseActivity() {

    var allQuestions = ArrayList<YNMQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        FirestoreClass().getAllUnapprovedYNMQuestions(this)

       }

    fun loadAllQuestions(loadQuestions: ArrayList<YNMQuestion>)
    {
        allQuestions = loadQuestions
        rvQuestion.setHasFixedSize(true)
        rvQuestion.layoutManager = LinearLayoutManager(this)
        rvQuestion.itemAnimator = DefaultItemAnimator()
        rvQuestion.adapter = QuestionAdapter(allQuestions, R.layout.layout_rv_question_details)


        rvQuestion.adapter!!.notifyDataSetChanged()
    }


    class QuestionAdapter(val questionData: List<YNMQuestion>, val itemLayout: Int) :
        RecyclerView.Adapter<questionViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): questionViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
            return questionViewHolder(view)
        }

        override fun onBindViewHolder(holder: questionViewHolder, position: Int) {
            var mQuestion = questionData.get(position)
            holder.updateQuestion(mQuestion)
        }

        override fun getItemCount(): Int {
            return questionData.size
        }

    }

    class questionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateQuestion(questionData: YNMQuestion) {
            tvRVQuestionID?.text = questionData.id
            tvRVQuestion?.text = questionData.question

        }

        private var tvRVQuestionID: TextView?
        private var tvRVQuestion: TextView?
        private var ivRVQuestionImage: ImageView?

        init {
            ivRVQuestionImage = itemView.findViewById<ImageView>(R.id.ivRVQuestionImage)
            tvRVQuestion = itemView.findViewById<TextView>(R.id.tvRVQuestion)
            tvRVQuestionID = itemView.findViewById<TextView>(R.id.tvRVQuestionID)
        }
    }
}
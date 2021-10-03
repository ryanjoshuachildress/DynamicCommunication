package com.ryanjoshuachildress.dynamiccommunication.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.databinding.FragmentDashboardBinding
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.YNMQuestion
import com.ryanjoshuachildress.dynamiccommunication.utils.Constants
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.layout_ynm_question.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val mFireStore = FirebaseFirestore.getInstance()
    private var allQuestions = ArrayList<YNMQuestion>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUserCount.text = FirestoreClass().getTotalUsersCount().toString()
        tvQuestionCount.text = FirestoreClass().getTotalQuestionsCount().toString()
        tvAnswerCount.text = FirestoreClass().getTotalAnswersCount().toString()

        btnLaunchYNM.setOnClickListener { view ->
            showYNMQuestion()
        }

        btnLaunchYNMAdd.setOnClickListener{
            showYNMAdd()
        }
    }

    private fun showYNMAdd() {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.layout_ynm_question_add)

        val btnSave = dialog?.findViewById(R.id.btnSave) as Button
        val btnExit = dialog?.findViewById(R.id.btnExit) as Button
        val etQuestion = dialog?.findViewById(R.id.etQuestion) as EditText
        btnSave.setOnClickListener {
            if(etQuestion.text.toString() != "") {
                FirestoreClass().addYNMQuestion(etQuestion.text.toString())
            } else
            {
                dialog.dismiss()
            }
        }
        btnExit.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showYNMQuestion() {
        allQuestions.clear()
        mFireStore.collection(Constants.YNMQUESTION)

            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    allQuestions.add(document.toObject(YNMQuestion::class.java))
                }
                val dialog = activity?.let { Dialog(it) }
                dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog?.setCancelable(false)
                dialog?.setContentView(R.layout.layout_ynm_question)

                val btnYes = dialog?.findViewById(R.id.btnYes) as Button
                val btnNo = dialog?.findViewById(R.id.btnNo) as Button
                val btnMaybe = dialog?.findViewById(R.id.btnMaybe) as Button
                val tvQuestion = dialog?.findViewById(R.id.tvQuestion) as TextView
                val ivQuestionImage = dialog?.findViewById(R.id.ivQuestionImage) as ImageView

                val randomIndex = (0..(allQuestions.size-1)).random()
                tvQuestion.text = allQuestions[randomIndex].question.toString()


                btnYes.setOnClickListener {
                    FirestoreClass().answewrYNMQuestion(allQuestions[randomIndex].id,allQuestions[randomIndex].question,Constants.ANSWER_YES)
                    dialog.dismiss()
                }
                btnNo.setOnClickListener {
                    FirestoreClass().answewrYNMQuestion(allQuestions[randomIndex].id,allQuestions[randomIndex].question,Constants.ANSWER_NO)
                    dialog.dismiss()
                }
                btnMaybe.setOnClickListener {
                    FirestoreClass().answewrYNMQuestion(allQuestions[randomIndex].id,allQuestions[randomIndex].question,Constants.ANSWER_MAYBE)
                    dialog.dismiss()
                }
                if (dialog != null) {
                    dialog.show()
                }
            }
            .addOnFailureListener { exception ->

            }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
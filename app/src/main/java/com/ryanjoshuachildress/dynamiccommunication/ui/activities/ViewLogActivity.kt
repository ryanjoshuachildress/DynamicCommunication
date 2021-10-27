package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityViewLogBinding
import com.ryanjoshuachildress.dynamiccommunication.models.LogData
import kotlinx.android.synthetic.main.activity_view_log.*

class ViewLogActivity : BaseActivity() {

    private var allLogs = ArrayList<LogData>()

    private lateinit var binding: ActivityViewLogBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityViewLogBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )


        }
        binding.btnClose.setOnClickListener {
            finish()
        }
        rvLog.setHasFixedSize(true)
        rvLog.layoutManager = LinearLayoutManager(this)
        rvLog.itemAnimator = DefaultItemAnimator()
        rvLog.adapter = LogAdapter(allLogs, R.layout.layout_rv_log_details)
        loadDataFromFirestore()

    }

    private fun loadDataFromFirestore() {
        showProgressDialog("Please Wait")
        val mFireStore = FirebaseFirestore.getInstance()
        mFireStore.collection("logs").orderBy("dateTime", Query.Direction.DESCENDING)
            .get()

            .addOnSuccessListener { result ->
                for (document in result) {
                    var mLog = document.toObject(LogData::class.java)
                    allLogs.add(mLog)


                }
                rvLog.adapter!!.notifyDataSetChanged()
                hideProgressDialog()
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()
                showErrorSnackbar(exception.message.toString(),true)
            }


    }


    class LogAdapter(val logData: List<LogData>, val itemLayout: Int) :
        RecyclerView.Adapter<logViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): logViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
            return logViewHolder(view)
        }

        override fun onBindViewHolder(holder: logViewHolder, position: Int) {
            var mLog = logData.get(position)
            holder.updateLog(mLog)
        }

        override fun getItemCount(): Int {
            return logData.size
        }

    }

    class logViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateLog(logData: LogData) {
            tvUserID?.text = logData.userID
            tvLogMessage?.text = logData.logEntry
            tvTimeDate?.text = logData.dateTime.toDate().toString()
        }

        private var tvUserID: TextView?
        private var tvLogMessage: TextView?
        private var tvTimeDate: TextView?
        private var ivLogTypeImage: ImageView?

        init {
            ivLogTypeImage = itemView.findViewById<ImageView>(R.id.ivLogTypeImage)
            tvLogMessage = itemView.findViewById<TextView>(R.id.tvLogMessage)
            tvUserID = itemView.findViewById<TextView>(R.id.tvUserID)
            tvTimeDate = itemView.findViewById<TextView>(R.id.tvTimeDate)
        }
    }
}
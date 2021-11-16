package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityNotificationBinding
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.NotificationInfo
import com.ryanjoshuachildress.dynamiccommunication.utils.Users
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationBinding

    var allNotifications = ArrayList<NotificationInfo>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)

        setContentView(binding.root)
        FirestoreClass().getAllNotifications(this)

            btnAddNotification.setOnClickListener {
                addNotification()
            }
    }

    private fun addNotification() {
        if(Users().isAdmin(FirestoreClass().getCurrentUserID())) {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.layout_notification_add)

            val btnSave = dialog.findViewById(R.id.btnSave) as Button
            val btnExit = dialog.findViewById(R.id.btnExit) as Button
            val etQuestion = dialog.findViewById(R.id.etQuestion) as EditText
            btnSave.setOnClickListener {
                if (etQuestion.text.toString() != "") {
                    dialog.dismiss()
                } else {

                }
            }
            btnExit.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            FirestoreClass().getAllNotifications(this)
       }
       else
        {
            showSnackbar("Must be an admin to add notifications", true)
        }
    }

    fun UpdateUI(notificationInfo: ArrayList<NotificationInfo>) {
        allNotifications = notificationInfo
        rvNotifications.setHasFixedSize(true)
        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.itemAnimator = DefaultItemAnimator()
        rvNotifications.adapter =
            NotificationAdapter(allNotifications, R.layout.layout_rv_notification_details)
        rvNotifications.adapter!!.notifyDataSetChanged()
    }

    fun notificationWriteFailure(message: String?) {
        showSnackbar(message!!, true)
    }

    fun notificationWriteSucess() {
        showSnackbar("Notification saved sucessfully", false)
    }

    inner class NotificationAdapter(
        val notificationData: List<NotificationInfo>,
        val itemLayout: Int
    ) :
        RecyclerView.Adapter<notificationViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): notificationViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
            return notificationViewHolder(view)
        }

        override fun onBindViewHolder(holder: notificationViewHolder, position: Int) {
            var mNotification = notificationData.get(position)
            holder.updateQuestion(mNotification)
        }

        override fun getItemCount(): Int {
            return notificationData.size
        }

    }

    inner class notificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateQuestion(notificationData: NotificationInfo) {
            tvRVNotificationTitle.text = notificationData.notificationTitle
        }


        private var tvRVNotificationTitle: TextView

        init {
            tvRVNotificationTitle = itemView.findViewById(R.id.tvRVNotificationTitle)
        }
    }
}
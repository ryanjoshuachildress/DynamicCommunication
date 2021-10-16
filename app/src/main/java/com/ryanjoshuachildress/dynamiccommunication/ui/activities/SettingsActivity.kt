package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivitySettingsBinding
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.LogData
import com.ryanjoshuachildress.dynamiccommunication.models.User
import com.ryanjoshuachildress.dynamiccommunication.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_settings)

        getUserDetails()
        setupActionBar()
        tvEdit.setOnClickListener{
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener{
            FirestoreClass().logToDatabase(LogData(1,"Logged Out"))
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbarMain)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_icon_back)
        }
        toolbarMain.setNavigationOnClickListener { onBackPressed()}
    }
    private fun getUserDetails()
    {
        showProgressDialog("Please Wait")
        FirestoreClass().getUserDetails(this)
        hideProgressDialog()
    }

    fun userDetailSuccess(user: User) {
        hideProgressDialog()

        mUserDetails = user
        GlideLoader(this).loadUserPicture(user.image, ivUserImage)

        tvName.text = "${user.firstName} ${user.lastName}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
}
package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupActionBar()

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnForgotPassword.setOnClickListener{
            sendForgotPasswordLink()
        }
    }

    private fun sendForgotPasswordLink() {
        val email = binding.etEmail.text.toString().trim { it <= ' ' }
        if(email.isBlank()) {
            showSnackbar(getString(R.string.email_blank_error),true)
        } else{
            showProgressDialog(getString(R.string.PleaseWait))
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if(task.isSuccessful) {
                        showSnackbar(getString(R.string.forgot_password_email_sent),false)
                        finish()

                    } else {
                        showSnackbar(task.exception!!.message.toString(),true)

                    }
                }
        }

    }

    private fun setupActionBar() {
        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_icon_back)
        }
    }
}
package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityRegisterBinding
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.LogData
import com.ryanjoshuachildress.dynamiccommunication.models.User

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.tvLogin.setOnClickListener{
            finish()
        }

        binding.btnRegister.setOnClickListener{
           registerUser()
        }
        setupActionBar()
    }



    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim{it <= ' '}) -> {
                showErrorSnackbar("Please enter a First Name", true)
                false
            }

            TextUtils.isEmpty(binding.etLastName.text.toString().trim{it <= ' '}) -> {
                showErrorSnackbar("Please enter a Last Name", true)
                false
            }

            TextUtils.isEmpty(binding.etEmail.text.toString().trim{it <= ' '}) -> {
                showErrorSnackbar("Please enter an email address", true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim{it <= ' '}) -> {
                showErrorSnackbar("Please enter a password", true)
                false
            }
            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim{it <= ' '}) -> {
                showErrorSnackbar("Please enter a password again", true)
                false
            }
            binding.etPassword.text.toString().trim{it <= ' '} != binding.etConfirmPassword.text.toString().trim{it <= ' '} -> {
                showErrorSnackbar("Password and Confirm password don't match",true)
                false
            }
            !binding.cbTermsAndConditions.isChecked -> {
                showErrorSnackbar("Must agree to terms and conditions", true)
                false
            }
            else -> {
                true
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
    private fun registerUser(){
        if(validateRegisterDetails()) {
            showProgressDialog("Please Wait")
            val email: String = binding.etEmail.text.toString().trim{ it <= ' '}
            val password: String = binding.etPassword.text.toString().trim{ it <= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful)
                    {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener { task ->
                                hideProgressDialog()
                                if(task.isSuccessful) {
                                    val firebaseUser: FirebaseUser = task.result!!.user!!
                                    FirestoreClass().logToDatabase(LogData(1,"Registered Successfully"))
                                    val user = User(
                                        firebaseUser.uid,
                                        binding.etFirstName.text.toString().trim{it <= ' '},
                                        binding.etLastName.text.toString().trim{it <= ' '},
                                        binding.etEmail.text.toString().trim{it <= ' '}
                                    )
                                    FirestoreClass().registerUser(this,user)
                                } else {
                                    showErrorSnackbar(task.exception!!.message.toString(),true)
                                    hideProgressDialog()
                                }
                            }
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        showErrorSnackbar("You are registered successfully! USERID = ${firebaseUser.uid}", false)
                        FirebaseAuth.getInstance().signOut()
                        finish()
                    } else {
                        showErrorSnackbar(task.exception!!.message.toString(), true)
                    }

                }

        }
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this,
            "Registered Successfully",
            Toast.LENGTH_SHORT
        ).show()


    }
}
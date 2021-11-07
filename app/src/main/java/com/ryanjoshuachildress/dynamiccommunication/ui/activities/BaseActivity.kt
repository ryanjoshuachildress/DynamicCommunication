package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.app.Dialog
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.ryanjoshuachildress.dynamiccommunication.R

open class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: Dialog
    private lateinit var snackbar: Snackbar
    private var doubleBackToExitPressedOnce = false

    fun showSnackbar(message: String, errorMessage: Boolean) {
        snackbar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        if(errorMessage)
        {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color))
            snackbar.show()
        }
        else {

            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_success_color))
            snackbar.show()
        }
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.tvProgressBar).text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        mProgressDialog.show()
    }

    fun hideProgressDialog()
    {
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true

        Toast.makeText(this,"Press Back Again to Exit",Toast.LENGTH_SHORT).show()

        @Suppress("DEPRECATION")
        android.os.Handler().postDelayed({doubleBackToExitPressedOnce = false},2000)
    }
}
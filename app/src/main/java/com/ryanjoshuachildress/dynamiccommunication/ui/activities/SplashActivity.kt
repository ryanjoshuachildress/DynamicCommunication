package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivitySplashBinding
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.YNMQuestion

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var binding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)

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
        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                val userID = FirebaseAuth.getInstance().currentUser?.uid
                if (userID == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
                else
                {
                    startActivity(Intent(this, DashboardActivity::class.java))

                }
            },1500


        )

    }
}
package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityMainBinding
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivitySplashBinding
import com.ryanjoshuachildress.dynamiccommunication.utils.Constants

class MainActivity : BaseActivity() {

    private lateinit var analytics: FirebaseAnalytics

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics

        val sharedPreferences = getSharedPreferences(Constants.DYNAMICCOMMUNICATION_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!

        binding.tvMain.text = "Hello ${username}"



    }
    override fun onBackPressed() {
        doubleBackToExit()
    }
}
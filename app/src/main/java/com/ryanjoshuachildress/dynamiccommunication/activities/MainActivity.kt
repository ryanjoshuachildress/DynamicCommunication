package com.ryanjoshuachildress.dynamiccommunication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityMainBinding
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivitySplashBinding

class MainActivity : AppCompatActivity() {

    private lateinit var analytics: FirebaseAnalytics

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics




    }
}
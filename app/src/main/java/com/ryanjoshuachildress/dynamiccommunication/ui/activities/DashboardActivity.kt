package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.ui.activities.BaseActivity
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityDashboardBinding
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.LogData
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}
package com.ryanjoshuachildress.dynamiccommunication.activities

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityUserProfileBinding
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.User
import com.ryanjoshuachildress.dynamiccommunication.utils.Constants

class UserProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirestoreClass().getUserDetails(this)

        val sharedPreferences = getSharedPreferences(Constants.DYNAMICCOMMUNICATION_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!

        binding.tvTitle.text = "User Profile: ${username}"

        binding.ivUserPhoto.setOnClickListener{
            selectUserImage()
        }
    }

    private fun selectUserImage() {
        if(ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
            == PackageManager.PERMISSION_GRANTED
        ){
            showErrorToast("You already have the storage permission",false)
        } else
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showErrorToast("the Storage permission is granted", false)
            } else
            {
                Toast.makeText(
                    this,
                    "Permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    fun populateUserDataFromFirebase(userDetails: User)
    {
        binding.etEmail.setText(userDetails.email)
        binding.etEmail.isEnabled = false
        binding.etLastName.setText(userDetails.lastName)
        binding.etFirstName.setText(userDetails.firstName)
    }
}
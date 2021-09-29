package com.ryanjoshuachildress.dynamiccommunication.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.ryanjoshuachildress.dynamiccommunication.R
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityUserProfileBinding
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.LogData
import com.ryanjoshuachildress.dynamiccommunication.models.User
import com.ryanjoshuachildress.dynamiccommunication.utils.Constants
import com.ryanjoshuachildress.dynamiccommunication.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private var mSelectImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""


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
        binding.btnSubmit.setOnClickListener{

            if(validateUserProfileDetails()) {
                showProgressDialog("please wait")
                if (mSelectImageFileUri != null) {
                    FirestoreClass().uploadImageTOCloudStorage(this, mSelectImageFileUri)
                } else
                {
                    updateUserProfileDetails()
                }
            }

        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }
        val firstName = binding.etFirstName.text.toString().trim { it <= ' ' }
        val lastName = binding.etLastName.text.toString().trim { it <= ' ' }

        val gender = when {
            binding.rbMale.isChecked -> {
                Constants.MALE
            }
            binding.rbFemale.isChecked -> {
                Constants.FEMALE
            }
            else -> {
                Constants.OTHER
            }
        }
        if (firstName.isNotEmpty()) {
            userHashMap[Constants.FIRSTNAME] = firstName
        }
        if (lastName.isNotEmpty()) {
            userHashMap[Constants.LASTNAME] = lastName
        }
        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.PROFILE_COMPLETED] = true

        FirestoreClass().updateUserProfileData(this, userHashMap)
        FirestoreClass().logToDatabase(LogData(1,"User Profile Updated"))
    }

    private fun selectUserImage() {
        if(ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
            == PackageManager.PERMISSION_GRANTED
        ){
            Constants.showImageChooser(this)
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
                Constants.showImageChooser(this)
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
        binding.etMobileNumber.setText(userDetails.mobile.toString())
        binding.etLastName.setText(userDetails.lastName)
        binding.etFirstName.setText(userDetails.firstName)
        when(userDetails.gender)
        {
            Constants.MALE -> {
                rbMale.isChecked = true
            }
            Constants.FEMALE -> {
                rbFemale.isChecked = true
            }
            Constants.OTHER -> {
                rbOther.isChecked = true
            }
        }
        GlideLoader(this).loadUserPicture(userDetails.image!!.toUri(),binding.ivUserPhoto)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if(data != null) {
                    try {
                        mSelectImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(mSelectImageFileUri!!,binding.ivUserPhoto)
                    } catch(e: IOException) {
                        Toast.makeText(this, "Image Selection Failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim{it <= ' '}) -> {
                showErrorToast("Please enter a mobile number", true)
                false
            }
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim{it <= ' '}) -> {
                showErrorToast("Please enter a First Name", true)
                false
            }
            TextUtils.isEmpty(binding.etLastName.text.toString().trim{it <= ' '}) -> {
                showErrorToast("Please enter a Last Name", true)
                false
            }

            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }


    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this,"User Profile Updated Successfully",Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }


}
package com.ryanjoshuachildress.dynamiccommunication.activities

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
import com.ryanjoshuachildress.dynamiccommunication.databinding.ActivityUserProfileBinding
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass
import com.ryanjoshuachildress.dynamiccommunication.models.User
import com.ryanjoshuachildress.dynamiccommunication.utils.Constants
import com.ryanjoshuachildress.dynamiccommunication.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private var mSelectImageFileUri: Uri? = null


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
            showProgressDialog("please wait")
            FirestoreClass().uploadImageTOCloudStorage(this, mSelectImageFileUri)
            if(validateUserProfileDetails()){

                val userHashMap = HashMap<String,Any>()

                val mobileNumber = binding.etMobileNumber.text.toString().trim{ it <= ' '}
                val firstName = binding.etFirstName.text.toString().trim{ it <= ' '}
                val lastName = binding.etLastName.text.toString().trim{ it <= ' '}

                val gender = when{
                    binding.rbMale.isChecked -> {
                        Constants.MALE
                    }
                    binding.rbFemale.isChecked -> {
                        Constants.FEMALE
                    }
                    else ->
                    {
                        Constants.OTHER
                    }
                }
                if(mobileNumber.isNotEmpty()) {
                    userHashMap[Constants.FIRSTNAME] = firstName
                }
                if(mobileNumber.isNotEmpty()) {
                    userHashMap[Constants.LASTNAME] = lastName
                }

                if(mobileNumber.isNotEmpty()) {
                    userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                }
                userHashMap[Constants.GENDER] = gender
                showProgressDialog("Please Wait")

                FirestoreClass().updateUserProfileData(this,userHashMap)
            }
        }
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
        binding.etLastName.setText(userDetails.lastName)
        binding.etFirstName.setText(userDetails.firstName)
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
                showErrorToast("Please enter a Lat Name", true)
                false
            }

            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {
        hideProgressDialog()
        showErrorToast("Your image is uploaded sucessfully. image URL is ${imageURL}", false)
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this,"User Profile Updated Successfully",Toast.LENGTH_SHORT).show()

        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }


}
package com.ryanjoshuachildress.dynamiccommunication.utils

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ryanjoshuachildress.dynamiccommunication.models.LogData


object LogDBHelper {


    //LogDBHelper.fWriteToDatabase(LogData(auth.uid,1,"test"))
    fun fWriteToDatabase(logData: LogData) {
        var firebaseDatabase = Firebase.firestore

        firebaseDatabase.collection("Log")
            .add(logData)
            .addOnSuccessListener{  }
            .addOnFailureListener{ Log.e("LogDBHelper","Error Writing to database") }
    }
}
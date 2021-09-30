package com.ryanjoshuachildress.dynamiccommunication.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.ryanjoshuachildress.dynamiccommunication.firestore.FirestoreClass

class LogData(
    val logLevel: Int,
    val logEntry: String,
    val userID: String = FirestoreClass().getCurrentUserID(),
    val dateTime: Timestamp = com.google.firebase.Timestamp.now()
)
{
    constructor() : this(0, "", "")
}
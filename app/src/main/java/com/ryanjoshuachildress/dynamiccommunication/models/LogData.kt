package com.ryanjoshuachildress.dynamiccommunication.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class LogData(
    @PropertyName("User ID") var userID: String,
    @PropertyName("Log Level") var logLevel: Int,
    @PropertyName("Log Entry") var logEntry: String,
    @PropertyName("Date Time") var dateTime: Timestamp = com.google.firebase.Timestamp.now()
)
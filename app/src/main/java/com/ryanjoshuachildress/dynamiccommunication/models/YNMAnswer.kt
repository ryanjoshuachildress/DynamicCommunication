package com.ryanjoshuachildress.dynamiccommunication.models

import com.google.firebase.Timestamp

class YNMAnswer (
    var UserID: String = "",
    var questionID:String = "",
    var question: String = "",
    var answer: String = "",
    val dateTime: Timestamp = Timestamp.now()
    )
package com.ex.ali.bitirmeprojesi

class NesneReminderNotification {

    var reminderNote: String?=null
    var reminderRadius: Int?=null
    var reminderUserID: String?=null
    var reminderName: String?=null
    var reminderLocationLatitude: Double?=null
    var reminderLocationLongitude: Double?=null

    constructor(reminderName:String , reminderRadius:Int , reminderNote:String , reminderUserID:String ,reminderLocationLatitude: Double,reminderLocationLongitude: Double){
        this.reminderName=reminderName
        this.reminderRadius=reminderRadius
        this.reminderNote=reminderNote
        this.reminderUserID=reminderUserID
        this.reminderLocationLatitude=reminderLocationLatitude
        this.reminderLocationLongitude=reminderLocationLongitude
    }

    constructor(){}
}
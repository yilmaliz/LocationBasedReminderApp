package com.ex.ali.bitirmeprojesi

class NesneFriendBasedReminder {
    var reminderNote: String?=null
    var reminderRadius: Int?=null
    var otherUserID: String?=null
    var otherUserUname: String?=null
    var reminderLocationLatitude: Double?=null
    var reminderLocationLongitude: Double?=null
    var reminderName: String?=null

    constructor(reminderName: String,reminderNote:String,reminderRadius:Int,otherUserID:String,otherUserUname:String,reminderLocationLatitude:Double,reminderLocationLongitude:Double){
        this.reminderNote=reminderNote
        this.reminderRadius=reminderRadius
        this.otherUserID=otherUserID
        this.otherUserUname=otherUserUname
        this.reminderLocationLatitude=reminderLocationLatitude
        this.reminderLocationLongitude=reminderLocationLongitude
        this.reminderName=reminderName
    }
    constructor(){}

}

package com.ex.ali.bitirmeprojesi
class NesneKullanici {
    var isim: String?=null
    var soyisim: String?=null
    var pp: String?=null
    var ID: String?=null
    var mail: String?=null
    var konumLatitude : Double?=null
    var konumLognitude : Double?=null
    var uName : String?=null
    constructor(isim:String , soyisim:String , pp:String , ID:String , mail:String , konumLatitude:Double,konumLognitude:Double , uName:String){
        this.isim=isim
        this.soyisim=soyisim
        this.pp=pp
        this.ID=ID
        this.mail=mail
        this.konumLatitude=konumLatitude
        this.konumLognitude=konumLognitude
        this.uName=uName }

    constructor(){}
}

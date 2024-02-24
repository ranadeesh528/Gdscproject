package com.example.gdscproject

import java.io.Serializable

class Dataclass: Serializable {
    var dataname: String? = null
    var dataphonenumber: String? = null
    var datapincode: String? = null
    var datacity: String? = null
    var datastate: String? = null
    var datalocation: String? = null
    var imgurl: String? = null

    constructor(dataname: String? , dataphonenumber: String?,datapincode: String?,datacity: String?, datastate: String?  ,datalocation : String? , imgurl : String?){


        this.dataname = dataname
        this.dataphonenumber = dataphonenumber
        this.datapincode = datapincode
        this.datacity = datacity
        this.datastate = datastate
        this.datalocation = datalocation
        this.imgurl = imgurl
    }
    constructor(){

    }
}



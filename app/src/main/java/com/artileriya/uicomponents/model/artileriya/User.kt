package com.artileriya.uicomponents.model.artileriya

data class User (
    var name_surname    : String?  = null,
    var job            : String?  = null,
    var location_lat    : Double?  = null,
    var location_long  : Double?  = null,
    var location_height: Int?     = null,
    var parent_user     : String?  = null,
    var password     : String?  = null,
    var username     : String?  = null,
    var usertype     : String?  = null,
    var sibling_view    : Boolean? = null
)
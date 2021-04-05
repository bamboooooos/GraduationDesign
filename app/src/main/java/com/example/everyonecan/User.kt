package com.example.everyonecan

class User {
    val userId: String? = null
    val userName: String? = null
    val userPassword: String? = null
    val userPhone: String? = null
    val userAdmire: Integer? = null
    val userPhoto: String? = null
    val userMotto: String? = null
    val userFans: Integer? = null
    override fun toString(): String {
        return "userId:"+userId+",userName:"+userName+",userPassword:"+userPassword+",userPhone:"+userPhone+
                ",userAdmire:"+userAdmire+",userPhoto:"+userPhoto+",userMotto:"+userMotto+",userFans:"+userFans
    }
}
package com.example.everyonecan.api

import com.example.everyonecan.User
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface GetUserData {
    @GET("user/getUserById")
    fun getUserById(@Query("userId") userId:String):Observable<User>
}
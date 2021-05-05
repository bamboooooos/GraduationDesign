package com.example.everyonecan.api

import com.example.everyonecan.ResultModel
import com.example.everyonecan.User
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface GetUserData {
    @GET("user/getUserById")
    fun getUserById(@Query("userId") userId:String):Observable<User>

    @GET("user/loginOrLogout")
    fun loginOrLogout(@Query("userId") userId: String,@Query("uuid") uuid:String,@Query("isLogin") isLogin:Boolean):Observable<ResultModel>

    @GET("user/login")
    fun login(@Query("userId") userId: String,@Query("uuid")uuid: String,@Query("password")password:String):Observable<ResultModel>
}
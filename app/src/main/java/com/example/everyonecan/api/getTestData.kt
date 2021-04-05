package com.example.everyonecan.api

import com.example.everyonecan.User
import io.reactivex.Observable
import retrofit2.http.GET

interface getTestData {
    @GET("user/getTestData")
    fun getTestData():Observable<User>
}
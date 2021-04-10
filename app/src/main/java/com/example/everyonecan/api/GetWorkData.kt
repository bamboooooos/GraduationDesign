package com.example.everyonecan.api

import com.example.everyonecan.Work
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GetWorkData {
    @GET("user/getVideoByAuthorId")
    fun getVideoByAuthorId(@Query("userId") userId:String):Observable<ArrayList<Work>>

    @GET("user/getVideoById")
    fun getVideoById(@Query("workId") workId:String):Observable<Work>

    @GET("user/getPlayList")
    fun getPlayList(@Query("userId") userId: String):Observable<ArrayList<Work>>

    @GET("user/getOneNewVideo")
    fun getOneNewVideo(@Query("userId") userId: String):Observable<Work>

}
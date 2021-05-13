package com.example.everyonecan.api

import com.example.everyonecan.ResultModel
import okhttp3.RequestBody
import retrofit2.http.*


interface UploadVideo{
    /**
     * 上传视频
     * @return
     */
    @POST("record/upload")
    fun uploadVideo(@Body requestBody: RequestBody):io.reactivex.Observable<ResultModel>

}
package com.example.everyonecan.util

import android.content.Context
import com.shuyu.gsyvideoplayer.utils.NetworkUtils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RxUtil {
    fun initHttpConfig(context: Context): OkHttpClient.Builder{
        var okHttpClientBuilder: OkHttpClient.Builder= OkHttpClient.Builder()
        //启用log日志，可以在log日志看网络请求的信息
        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level= HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder.addInterceptor(loggingInterceptor)
        //设置请求超时
        okHttpClientBuilder.connectTimeout(15, TimeUnit.SECONDS)
        //设置缓存
        val cacheInterceptor: Interceptor =object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                var request: Request =chain.request()
                if(!NetworkUtils.isAvailable(context)){
                    request=request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
                }
                val response=chain.proceed(request)
                if(NetworkUtils.isAvailable(context)){
                    //有网络情况下，超过一分钟则重新请求，否则使用缓存数据
                    val maxAge=60
                    var cacheControl:String="public,max-age="+maxAge
                    return response.newBuilder()
                        .header("Cache-Control",cacheControl)
                        .removeHeader("Pragma").build()
                }else{    //无网络时直接获取缓存数据，该数据保存一周
                    var maxStale=60*60*24*7
                    return response.newBuilder()
                        .header("Cache-Control","public,only-if-cached,max-stale="+maxStale)
                        .removeHeader("Pragma").build()
                }
            }
        }
        val cacheFile= File(context.externalCacheDir,"HttpCache")
        val cache= Cache(cacheFile,1024*1024*50) //缓存大小50m
        okHttpClientBuilder.addNetworkInterceptor(cacheInterceptor)
        okHttpClientBuilder.addInterceptor(cacheInterceptor)
        okHttpClientBuilder.cache(cache)
        return okHttpClientBuilder
    }

    fun initRetrofit(okHttpClientBuilder: OkHttpClient.Builder,baseUrl:String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())   //启用Rxjava
            .baseUrl(baseUrl)  //设置基础请求地址
            .client(okHttpClientBuilder.build())
            .build()
    }
}
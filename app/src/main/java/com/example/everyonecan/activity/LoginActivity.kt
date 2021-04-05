package com.example.everyonecan.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.everyonecan.R
import com.example.everyonecan.User
import com.example.everyonecan.api.getTestData
import com.example.everyonecan.rxjava.RxSubscribe
import com.example.everyonecan.util.RxUtil
import com.shuyu.gsyvideoplayer.utils.NetworkUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    //TODO baseUrl为服务器地址

    companion object {
        val baseUrl: String = "http://192.168.31.208:8081/"
    }
    lateinit var mRetrofit: Retrofit
    var userId:String="0002"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mRetrofit=RxUtil.initRetrofit(RxUtil.initHttpConfig(applicationContext),baseUrl)
        initData()
        initListener()
    }

    fun initData(){

    }

    fun getTestData(){
        var mApi=mRetrofit.create(getTestData::class.java)
        var observable:Observable<User> =mApi.getTestData()
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:RxSubscribe<User>(){
                //成功逻辑
                override fun onSuccess(t: User) {
                    testNetData.text=t.toString()
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,"hint",Toast.LENGTH_SHORT).show()
                }
            })

    }

    fun initListener(){
        testNetData.text="test"
        toTheMain.setOnClickListener {
            login()
        }
        getTestDataButton.setOnClickListener {
            getTestData()
        }
    }

    fun login(){
        toMainActivity()
    }

    private fun toMainActivity(){
        val intent:Intent=Intent(this,MainActivity::class.java)
        intent.putExtra("userId",userId)
        startActivity(intent)
        finish()
    }
}
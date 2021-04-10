package com.example.everyonecan.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.everyonecan.R
import com.example.everyonecan.Work
import com.example.everyonecan.api.GetWorkData
import com.example.everyonecan.rxjava.RxSubscribe
import com.example.everyonecan.util.RxUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Retrofit
import kotlin.collections.ArrayList

class LoginActivity : AppCompatActivity() {
    //TODO baseUrl为服务器地址

    companion object {
        //服务器地址
//        val baseUrl: String = "http://1.116.75.147:8081/"
        //本地测试
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
        var mApi=mRetrofit.create(GetWorkData::class.java)
        var observable:Observable<ArrayList<Work>> =mApi.getPlayList("0001")
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:RxSubscribe<ArrayList<Work>>(){
                //成功逻辑
                override fun onSuccess(t: ArrayList<Work>) {
                    var toShow:String=""
                    for(i:Work in t){
                        toShow+=i.toString()
                    }
                    testNetData.text=toShow
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
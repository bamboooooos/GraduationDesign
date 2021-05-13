package com.example.everyonecan.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.everyonecan.R
import com.example.everyonecan.ResultModel
import com.example.everyonecan.api.GetUserData
import com.example.everyonecan.rxjava.RxSubscribe
import com.example.everyonecan.util.ActivityCollector
import com.example.everyonecan.util.MD5Util
import com.example.everyonecan.util.RxUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Retrofit
import java.util.*


class LoginActivity : AppCompatActivity() {

    val TAG="linlin"

    companion object {
        //服务器地址
//        val baseUrl: String = "http://1.116.75.147:8081/"
        //本地测试
        val baseUrl: String = "http://192.168.31.239:8081/"
    }
    lateinit var mRetrofit: Retrofit
    var userId:String="0002"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ActivityCollector.addActivity(this)
        mRetrofit=RxUtil.initRetrofit(RxUtil.initHttpConfig(applicationContext),baseUrl)

        isLogin()
        initData()
        initListener()
    }

    fun getUUID():String{
        var sp:SharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE)
        var uuid=sp.getString("uuid", "0000")!!
        if(uuid=="0000"){
            uuid=UUID.randomUUID().toString()
            val editor=sp.edit()
            editor.putString("uuid",uuid)
            editor.commit()
        }
        Log.d(TAG, "uuid: "+uuid)
        return uuid
    }

    fun isLogin(){
        val uuid=getUUID()
        var sp:SharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE)
        userId=sp.getString("userId","0000")!!
        if(userId=="0000") return
        var mApi=mRetrofit.create(GetUserData::class.java)
        Log.d(TAG, "登录id:"+userId)
        var observable: Observable<ResultModel> =mApi.loginOrLogout(userId,uuid!!,true)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<ResultModel>(){
                //成功逻辑
                override fun onSuccess(t: ResultModel) {
                    Log.d(TAG, "onSuccess: "+t.msg)
                    if(t.data as Boolean){
                        toMainActivity()
                    }else{
                        return
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("linlin", "登录失败 ")
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun initData(){

    }

    fun initListener(){
        btn_login.setOnClickListener {
            when(btn_login.text.toString()){
                "登录"->{
                    login()
                }
                "注册"->{
                    register()
                }
            }

        }
        tv_register.setOnClickListener {
            when(tv_register.text.toString()){
                "注册"->{
                    layout_confirm.visibility= View.VISIBLE
                    tv_register.text="登录"
                    btn_login.text="注册"
                }
                "登录"->{
                    layout_confirm.visibility= View.GONE
                    tv_register.text="注册"
                    btn_login.text="登录"
                }
            }
        }

    }

    fun login(){
        userId=et_account.text.toString()
        val password=MD5Util.Hash.md5(et_password.text.toString())
        val uuid=getUUID()
        var mApi=mRetrofit.create(GetUserData::class.java)
        Log.d(TAG, "登录id:"+userId)
        var observable: Observable<ResultModel> =mApi.login(userId,uuid,password)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<ResultModel>(){
                //成功逻辑
                override fun onSuccess(t: ResultModel) {
                    Log.d(TAG, "onSuccess: "+t.msg)
                    if(t.data as Boolean){
                        var sp:SharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE)
                        val editor=sp.edit()
                        editor.putString("userId",userId)
                        var result=editor.commit()
                        Log.d(TAG, "onSuccess: "+result)
                        toMainActivity()
                    }else{
                        Toast.makeText(this@LoginActivity,"密码错误或者账号不存在",Toast.LENGTH_SHORT).show()
                        et_password.setText("")
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("linlin", "登录失败 ")
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun register(){
        //TODO 注册逻辑
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    private fun toMainActivity(){
        val intent:Intent=Intent(this,MainActivity::class.java)
        intent.putExtra("userId",userId)
        startActivity(intent)
        onDestroy()
    }
}
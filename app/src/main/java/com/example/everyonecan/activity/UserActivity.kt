package com.example.everyonecan.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.everyonecan.R
import com.example.everyonecan.ResultModel
import com.example.everyonecan.User
import com.example.everyonecan.Work
import com.example.everyonecan.adapter.WorksAdapter
import com.example.everyonecan.api.GetUserData
import com.example.everyonecan.api.GetWorkData
import com.example.everyonecan.rxjava.RxSubscribe
import com.example.everyonecan.util.ActivityCollector
import com.example.everyonecan.util.RxUtil
import com.example.everyonecan.view.SpacesItemDecoration
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.content_scrolling.*
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

class UserActivity : AppCompatActivity() {

    var isMyself:Boolean=false
    lateinit var UserId:String
    lateinit var mUser:User
    var userWorkTitleMode:Int=0  //0为作品页，1为其他按钮页
    var videoToPlayArrayList:ArrayList<Work> = ArrayList()
    lateinit var retrofit:Retrofit
    lateinit var worksAdapter:WorksAdapter
    val TAG:String="linlin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        setSupportActionBar(findViewById(R.id.toolbar))
        ActivityCollector.addActivity(this)
        retrofit=RxUtil.initRetrofit(RxUtil.initHttpConfig(applicationContext),LoginActivity.baseUrl)
        initDate()
        initView()
        initListener()
    }

    fun initDate(){
        var intent:Intent=getIntent()
        isMyself=intent.getBooleanExtra("isMyself",false)
        UserId=if(isMyself)MainActivity.userId
        else {
            if(intent.getStringExtra("blogerId")!=null)intent.getStringExtra("blogerId")!!
            else "0001"
        }
        initWorksData()
        initUserData()

    }
    fun initView(){
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        if(isMyself){
            initOwnView()
        }else{
            initGuestView()
        }
        var workRecyclerView:RecyclerView=userWorks
        val layoutManager=StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
        workRecyclerView.layoutManager=layoutManager
        //此处的padding是Recyclerview的padding
        workRecyclerView.setPadding(4,4,4,4)
        //设置每个item的padding
        val duration:SpacesItemDecoration= SpacesItemDecoration(4)
        workRecyclerView.addItemDecoration(duration)
        worksAdapter= WorksAdapter(videoToPlayArrayList,layoutManager,this)
        workRecyclerView.adapter=worksAdapter

    }
    fun initListener(){
        userBack.setOnClickListener {
            this.onBackPressed()
        }
        workTitleButton.setOnClickListener {
            if(userWorkTitleMode==1) {
                //TODO 点击作品按钮事件，此处有网络请求
                changeModeBar.progress=0
                changeModeBar.secondaryProgress=50
                userWorkTitleMode=0
            }
        }
        otherTitleButton.setOnClickListener {
            if(userWorkTitleMode==0) {
                //TODO 点击其他按钮事件，此处有网络请求
                changeModeBar.progress=50
                changeModeBar.secondaryProgress=100
                userWorkTitleMode=1
            }
        }
    }

    fun initOwnView(){
        iv_logout.visibility= View.VISIBLE
        iv_logout.setOnClickListener {
            AlertDialog.Builder(this).setTitle("是否注销当前账号？")
                .setPositiveButton("确认",object:DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        logout()
                    }
                })
                .setNegativeButton("取消",object:DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        return
                    }
                })
                .setCancelable(false)
                .create().show()
        }
    }

    fun initGuestView(){
        //TODO 初始化“游客”页面特有的控件
    }

    fun initUserData(){
        var mApi=retrofit.create(GetUserData::class.java)
        var observable:Observable<User> =mApi.getUserById(UserId)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<User>(){
                //成功逻辑
                override fun onSuccess(t: User) {
                    mUser=t
                    var user=t
                    Log.d("lin", user.toString())
                    userName.text=user.userName
                    userTalk.text=user.userMotto
                    userFansNumber.text="粉丝数"+user.userFans
                    userIdInCan.text="用户id:"+user.userId
                    userLikeNumber.text="❤"+user.userAdmire
                    Picasso.with(this@UserActivity)
                        .load(user.userPhoto) //加载地址
//                        .load("https://p0.qhimg.com/t015f3654b694ad2f8a.jpg")
                        .placeholder(R.drawable.loading) //加载
                        .error(R.drawable.error) //加载失败的图
                        .tag(this@UserActivity)
                        .fit()
                        .into(userHead)//加载
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun initWorksData(){
//        initTestData()
        var mApi=retrofit.create(GetWorkData::class.java)
        var observable:Observable<ArrayList<Work>> =mApi.getVideoByAuthorId(UserId)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<ArrayList<Work>>(){
                //成功逻辑
                override fun onSuccess(t:ArrayList<Work>) {
                    for (i:Work in t){
                        videoToPlayArrayList.add(i)
                    }
                    worksAdapter.notifyDataSetChanged()
                    workTitleButton.text="作品"+videoToPlayArrayList.size
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun initTestData(){
        repeat(4) {
            videoToPlayArrayList.add(Work("0001","测试作者1","1001","武汉加油1","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/280443.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0002","测试作者2","1002","武汉加油2","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276982.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0003","测试作者3","1003","武汉加油3","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276984.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0004","测试作者4","1004","武汉加油4","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276985.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0005","测试作者5","1005","武汉加油5","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276986.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0006","测试作者6","1006","武汉加油6","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276987.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0007","测试作者7","1007","武汉加油7","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276988.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0008","测试作者8","1008","武汉加油8","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276989.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0009","测试作者9","1009","武汉加油9","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276990.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0010","测试作者10","1010","武汉加油10","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276991.mp4",Date().toString()))
        }
    }

    private fun logout(){
        var sp: SharedPreferences =getSharedPreferences("user", Context.MODE_PRIVATE)
        var uuid=sp.getString("uuid", "0000")!!
        Log.d(TAG, "uuid: "+uuid)
        if(uuid=="0000"){
            uuid=UUID.randomUUID().toString()
            val editor=sp.edit()
            editor.putString("uuid",uuid)
            editor.apply()
        }
        var mApi=retrofit.create(GetUserData::class.java)
        var observable: Observable<ResultModel> =mApi.loginOrLogout(MainActivity.userId,uuid,false)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<ResultModel>(){
                //成功逻辑
                override fun onSuccess(t:ResultModel) {
                    Toast.makeText(this@UserActivity,"注销成功",Toast.LENGTH_SHORT).show()
                    ActivityCollector.finishAll()
                    startActivity(Intent(this@UserActivity,LoginActivity::class.java))
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
}
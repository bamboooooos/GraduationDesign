package com.example.everyonecan.activity

import android.content.Intent
import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.everyonecan.R
import com.example.everyonecan.Work
import com.example.everyonecan.adapter.WorksAdapter
import com.example.everyonecan.view.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.content_scrolling.*
import java.util.*
import kotlin.collections.ArrayList

class UserActivity : AppCompatActivity() {

    var isMyself:Boolean=false
    lateinit var UserId:String
    lateinit var UserName:String
    var userWorkTitleMode:Int=0  //0为作品页，1为其他按钮页
    var videoToPlayArrayList:ArrayList<Work> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        setSupportActionBar(findViewById(R.id.toolbar))
        initDate()
        initView()
        initListener()
    }

    fun initDate(){
        var intent:Intent=getIntent()
        isMyself=intent.getBooleanExtra("isMyself",false)
        UserName=if(intent.getStringExtra("userName")!=null) intent.getStringExtra("userName")!! else "默认用户名"
        UserId=if(intent.getStringExtra("userId")!=null) intent.getStringExtra("userId")!! else "00000"
        initTestData()
        //TODO 根据用户id获取用户其他数据，此处有网络请求
    }
    fun initView(){
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        if(isMyself){
            initOwnView()
        }else{
            initGuestView()
        }
        userName.text=UserName
        userIdInCan.text="用户id:"+UserId
        var workRecyclerView:RecyclerView=userWorks
        val layoutManager=StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
        workRecyclerView.layoutManager=layoutManager
        //此处的padding是Recyclerview的padding
        workRecyclerView.setPadding(4,4,4,4)
        //设置每个item的padding
        val duration:SpacesItemDecoration= SpacesItemDecoration(4)
        workRecyclerView.addItemDecoration(duration)
        val worksAdapter:WorksAdapter= WorksAdapter(videoToPlayArrayList,layoutManager,this)
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
        //TODO 初始化“我的”页面特有的控件
    }

    fun initGuestView(){
        //TODO 初始化“游客”页面特有的控件
    }

    fun initTestData(){
        repeat(4) {
            videoToPlayArrayList.add(Work("0001","测试作者1","1001","武汉加油1","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/280443.mp4",Date()))
            videoToPlayArrayList.add(Work("0002","测试作者2","1002","武汉加油2","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276982.mp4",Date()))
            videoToPlayArrayList.add(Work("0003","测试作者3","1003","武汉加油3","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276984.mp4",Date()))
            videoToPlayArrayList.add(Work("0004","测试作者4","1004","武汉加油4","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276985.mp4",Date()))
            videoToPlayArrayList.add(Work("0005","测试作者5","1005","武汉加油5","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276986.mp4",Date()))
            videoToPlayArrayList.add(Work("0006","测试作者6","1006","武汉加油6","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276987.mp4",Date()))
            videoToPlayArrayList.add(Work("0007","测试作者7","1007","武汉加油7","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276988.mp4",Date()))
            videoToPlayArrayList.add(Work("0008","测试作者8","1008","武汉加油8","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276989.mp4",Date()))
            videoToPlayArrayList.add(Work("0009","测试作者9","1009","武汉加油9","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276990.mp4",Date()))
            videoToPlayArrayList.add(Work("0010","测试作者10","1010","武汉加油10","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276991.mp4",Date()))
        }
    }
}
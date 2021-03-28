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

class UserActivity : AppCompatActivity() {

    lateinit var UserId:String
    lateinit var UserName:String
    var userWorkTitleMode:Int=0  //0为作品页，1为其他按钮页
    var workList:ArrayList<Work> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        initDate()
        initView()
        initListener()
    }

    fun initDate(){
        var intent:Intent=getIntent()
        UserName=if(intent.getStringExtra("userName")!=null) intent.getStringExtra("userName")!! else "默认用户名"
        UserId=if(intent.getStringExtra("userId")!=null) intent.getStringExtra("userId")!! else "00000"
        initTestData()
        //TODO 根据用户id获取用户其他数据，此处有网络请求
    }
    fun initView(){
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
        val worksAdapter:WorksAdapter= WorksAdapter(workList,layoutManager)
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
    fun initTestData(){
        repeat(4) {
            workList.add(Work("0001","测试作者1","1001","测试视频卡1","src","src"))
            workList.add(Work("0002","测试作者2","1002","测试视频卡2","src","src"))
            workList.add(Work("0003","测试作者3","1003","测试视频卡3","src","src"))
            workList.add(Work("0004","测试作者4","1004","测试视频卡4","src","src"))
            workList.add(Work("0005","测试作者5","1005","测试视频卡5","src","src"))
            workList.add(Work("0006","测试作者6","1006","测试视频卡6","src","src"))
            workList.add(Work("0007","测试作者7","1007","测试视频卡7","src","src"))
            workList.add(Work("0008","测试作者8","1008","测试视频卡8","src","src"))
            workList.add(Work("0009","测试作者9","1009","测试视频卡9","src","src"))
            workList.add(Work("0010","测试作者10","1010","测试视频卡10","src","src"))
            workList.add(Work("0011","测试作者11","1011","测试视频卡11","src","src"))
            workList.add(Work("0012","测试作者12","1012","测试视频卡12","src","src"))
            workList.add(Work("0013","测试作者13","1013","测试视频卡13","src","src"))
            workList.add(Work("0014","测试作者14","1014","测试视频卡14","src","src"))
            workList.add(Work("0015","测试作者15","1015","测试视频卡15","src","src"))
            workList.add(Work("0016","测试作者16","1016","测试视频卡16","src","src"))
            workList.add(Work("0017","测试作者17","1017","测试视频卡17","src","src"))
            workList.add(Work("0018","测试作者18","1018","测试视频卡18","src","src"))
            workList.add(Work("0019","测试作者19","1019","测试视频卡19","src","src"))
            workList.add(Work("0020","测试作者20","1020","测试视频卡20","src","src"))
        }
    }
}
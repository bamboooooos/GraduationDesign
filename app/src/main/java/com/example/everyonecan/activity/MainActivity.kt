package com.example.everyonecan.activity

import android.content.AbstractThreadedSyncAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.everyonecan.R
import com.example.everyonecan.Work
import com.example.everyonecan.adapter.MainPlayerAdapter
import com.example.everyonecan.listener.MyGestureDetector
import com.example.everyonecan.view.OnViewPagerListener
import com.example.everyonecan.view.VideoListDialog
import com.example.everyonecan.view.VideoPlayLayoutManager
import com.shuyu.gsyvideoplayer.GSYVideoADManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var gestureDetector:GestureDetector
    lateinit var mRecyclerView:RecyclerView
    lateinit var mLayoutManager:VideoPlayLayoutManager
    lateinit var mAdapter: MainPlayerAdapter
    var videoToPlayArrayList:ArrayList<Work> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initView()
        initListener()
    }

    fun initData(){
        initTestData()
    }

    fun initView(){
        mRecyclerView=video_play_recyclerview
        mLayoutManager= VideoPlayLayoutManager(this,OrientationHelper.VERTICAL,false)
        mAdapter= MainPlayerAdapter(videoToPlayArrayList)
        mRecyclerView.layoutManager=mLayoutManager
        mRecyclerView.adapter=mAdapter
    }

    fun initListener():Unit{
        myself.setOnClickListener {
            //TODO 点击进入“我的”页面
            Toast.makeText(this,"触发我的点击事件", Toast.LENGTH_SHORT).show()
            val intentToBloger:Intent=Intent(this,UserActivity().javaClass)
            intentToBloger.putExtra("isMyself",true)
            intentToBloger.putExtra("userName","测试用户名(我的)")
            intentToBloger.putExtra("userId","00002")
            startActivity(intentToBloger)
        }
        blogger.setOnClickListener {
            //TODO 点击进入“博主”页面
            Toast.makeText(this,"触发博主点击事件",Toast.LENGTH_SHORT).show()
            val intentToBloger:Intent=Intent(this,UserActivity().javaClass)
            intentToBloger.putExtra("userName","测试用户名(博主)")
            intentToBloger.putExtra("userId","00001")
            startActivity(intentToBloger)
        }
        toRead.setOnClickListener {
            //点击“待播放”视频列表
            Toast.makeText(this,"触发待播放点击事件",Toast.LENGTH_SHORT).show()
            val dialog:VideoListDialog= VideoListDialog(this,videoToPlayArrayList)
            dialog.show()
        }
        recording.setOnClickListener {
            //TODO 点击录制我的视频
            Toast.makeText(this,"触发录制点击事件",Toast.LENGTH_SHORT).show()
        }
        gestureDetector= GestureDetector(this,MyGestureDetector(this))
        mLayoutManager.setOnViewPagerListener(object:OnViewPagerListener{
            override fun onInitComplete() {

            }

            override fun onPageRelease(isNext: Boolean, position: Int) {
                val videoPlayer:StandardGSYVideoPlayer=video_play_recyclerview.findViewById(R.id.video_view)
                videoPlayer.onVideoPause()
            }

            override fun onPageSelected(position: Int, isBottom: Boolean) {
                val videoPlayer:StandardGSYVideoPlayer=video_play_recyclerview.findViewById(R.id.video_view)
                videoPlayer.startPlayLogic()
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    fun initTestData(){
        repeat(1) {
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

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume(true)
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }
}
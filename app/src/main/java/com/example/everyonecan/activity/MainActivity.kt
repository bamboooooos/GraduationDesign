package com.example.everyonecan.activity

import android.content.AbstractThreadedSyncAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.everyonecan.R
import com.example.everyonecan.Work
import com.example.everyonecan.adapter.MainPlayerAdapter
import com.example.everyonecan.api.GetWorkData
import com.example.everyonecan.listener.MyGestureDetector
import com.example.everyonecan.rxjava.RxSubscribe
import com.example.everyonecan.util.RxUtil
import com.example.everyonecan.view.OnViewPagerListener
import com.example.everyonecan.view.VideoListDialog
import com.example.everyonecan.view.VideoPlayLayoutManager
import com.shuyu.gsyvideoplayer.GSYVideoADManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_scrolling.*
import okhttp3.internal.notifyAll
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var gestureDetector:GestureDetector
    lateinit var mRecyclerView:RecyclerView
    lateinit var mLayoutManager:VideoPlayLayoutManager
    lateinit var mAdapter: MainPlayerAdapter
    var videoToPlayArrayList:ArrayList<Work> = ArrayList()
    lateinit var retrofit: Retrofit
    val GET_VIDEO_LINE:Int=3    //需要有至少3个视频于在播视频之后的播放列表里

    companion object{
        public lateinit var userId:String
        public lateinit var userName:String
        public var playNow:Int=0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        retrofit= RxUtil.initRetrofit(RxUtil.initHttpConfig(applicationContext),LoginActivity.baseUrl)
        initData()
        initView()
        initListener()
    }

    fun initData(){
        MainActivity.userId=if(intent.getStringExtra("userId")!=null) intent.getStringExtra("userId")!! else "0000"
        initToPlayData()
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
            Toast.makeText(this,"触发我的点击事件", Toast.LENGTH_SHORT).show()
            val intentToBloger:Intent=Intent(this,UserActivity().javaClass)
            intentToBloger.putExtra("isMyself",true)
            startActivity(intentToBloger)
        }
        blogger.setOnClickListener {
            Toast.makeText(this,"触发博主点击事件",Toast.LENGTH_SHORT).show()
            val intentToBloger:Intent=Intent(this,UserActivity().javaClass)
            var blogerId=if(videoToPlayArrayList.size>0) videoToPlayArrayList[playNow].workAuthorId else "0000"
            intentToBloger.putExtra("blogerId",blogerId)
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
                playNow=position
                Log.d("linlin", "播放:"+playNow)
                renewPlayList()
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    fun initToPlayData(){
//        initTestData()
        var mApi=retrofit.create(GetWorkData::class.java)
        var observable: Observable<ArrayList<Work>> =mApi.getPlayList(userId)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<ArrayList<Work>>(){
                //成功逻辑
                override fun onSuccess(t:ArrayList<Work>) {
                    for (i:Work in t){
                        videoToPlayArrayList.add(i)
                    }
                    mAdapter.notifyDataSetChanged()
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun initTestData(){
        repeat(1) {
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

    /**
     * 所有上下滑动的操作都需要调用这个方法来刷新播放列表
     * 播放列表设定为下面需要有至少3个视频，当向下滑到不足3个视频时，需要向服务器获取视频资源
     * videoToPlayArrayList
     */
    fun renewPlayList(){
        repeat(GET_VIDEO_LINE + 1 + playNow - videoToPlayArrayList.size){
            getOneNewVideo()
        }
    }

    private fun getOneNewVideo(){
        var mApi=retrofit.create(GetWorkData::class.java)
        var observable: Observable<Work> =mApi.getOneNewVideo(MainActivity.userId)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<Work>(){
                //成功逻辑
                override fun onSuccess(t:Work) {
                    videoToPlayArrayList.add(t)
                    Log.d("linlin", "获取:"+t.workId)
                    mAdapter.notifyDataSetChanged()
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }

}
package com.example.everyonecan.activity

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.everyonecan.R
import com.example.everyonecan.ResultModel
import com.example.everyonecan.Work
import com.example.everyonecan.adapter.MainPlayerAdapter
import com.example.everyonecan.api.GetWorkData
import com.example.everyonecan.listener.MyGestureDetector
import com.example.everyonecan.listener.OnTransitionListener
import com.example.everyonecan.rxjava.RxSubscribe
import com.example.everyonecan.util.ActivityCollector
import com.example.everyonecan.util.RxUtil
import com.example.everyonecan.view.OnViewPagerListener
import com.example.everyonecan.view.VideoListDialog
import com.example.everyonecan.view.VideoPlayLayoutManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    final val TAG:String = "linlin"

    var timer:Timer?=null
    private var transition: Transition? = null
    var isGetting:Boolean=false
    var isTouch:Boolean=false
    var toFeedBackVideoPosition:Int=0
    var lastPlay:Int=0
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
        ActivityCollector.addActivity(this)
        retrofit= RxUtil.initRetrofit(RxUtil.initHttpConfig(applicationContext),LoginActivity.baseUrl)
        initData()
        initView()
        initListener()
        initTimer()
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
//            Toast.makeText(this,"触发我的点击事件", Toast.LENGTH_SHORT).show()
            val intentToBloger:Intent=Intent(this,UserActivity().javaClass)
            intentToBloger.putExtra("isMyself",true)
            startActivity(intentToBloger)
        }
        blogger.setOnClickListener {
//            Toast.makeText(this,"触发博主点击事件",Toast.LENGTH_SHORT).show()
            val intentToBloger:Intent=Intent(this,UserActivity().javaClass)
            var blogerId=if(videoToPlayArrayList.size>0) videoToPlayArrayList[playNow].workAuthorId else "0000"
            intentToBloger.putExtra("blogerId",blogerId)
            startActivity(intentToBloger)
        }
        toRead.setOnClickListener {
            //点击“待播放”视频列表
//            Toast.makeText(this,"触发待播放点击事件",Toast.LENGTH_SHORT).show()
            val dialog:VideoListDialog= VideoListDialog(this,videoToPlayArrayList)
            dialog.show()
        }
        recording.setOnClickListener {
            //TODO 点击录制我的视频
//            Toast.makeText(this,"触发录制点击事件",Toast.LENGTH_SHORT).show()

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
//                initTransition(videoPlayer)
                videoPlayer.isLooping=true
                renewPlayList()
//                playNow=position
            }

            override fun onVideoSlided(position: Int) {
                playNow=position
                Log.d("linlin", "播放:"+playNow)
            }
        })
    }

//    private fun initTransition(videoPlayer:StandardGSYVideoPlayer) {
//        Log.d(TAG, "initTransition:")
//        postponeEnterTransition()
//        ViewCompat.setTransitionName(videoPlayer, "IMG_TRANSITION")
//        addTransitionListener(videoPlayer)
//        startPostponedEnterTransition()
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private fun addTransitionListener(videoPlayer:StandardGSYVideoPlayer): Boolean {
//        transition = window.sharedElementEnterTransition
//        Log.d(TAG, "addTransitionListener:")
//        if (transition != null) {
//            transition!!.addListener(object : OnTransitionListener() {
//                override fun onTransitionEnd(transition: Transition) {
//                    Log.d(TAG, "onTransitionEnd:")
//                    super.onTransitionEnd(transition)
//                    videoPlayer.startPlayLogic()
//                    transition.removeListener(this)
//                }
//            })
//            return true
//        }
//        Log.d(TAG, "false")
//        return false
//    }


    private fun initTimer(){
        timer=Timer("TestVideoIsChange")
        timer!!.schedule(object:TimerTask(){
            var allDuration=1000
            var duration=0
            var lastPlay:Int=0
            override fun run() {
                val videoPlayer:StandardGSYVideoPlayer=video_play_recyclerview.findViewById(R.id.video_view)
                if(lastPlay!= playNow){//视频已经滑动
                    //调用反馈
                    var seedingRate:Int=((duration.toDouble())/(allDuration.toDouble())).toInt()
                    feedbackVideo(videoToPlayArrayList[playNow].workId, userId,seedingRate,true)
//                    Log.d(TAG, "调用反馈机制:"+(duration.toDouble())/(allDuration.toDouble()))
                    lastPlay= playNow
                    duration=0
                    allDuration=1000
                }else{
                    allDuration=videoPlayer.duration
                    Log.d(TAG, "视频总长:"+allDuration)
                    duration=videoPlayer.currentPositionWhenPlaying
                    Log.d(TAG, "当前播放:"+duration)
                }
            }
        },1000,1000)
    }

    fun destroyTimer(){
        if(timer!=null)
        timer!!.cancel()
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
        destroyTimer()
        GSYVideoManager.releaseAllVideos()
        ActivityCollector.removeActivity(this)
    }

    /**
     * 所有上下滑动的操作都需要调用这个方法来刷新播放列表
     * 播放列表设定为下面需要有至少3个视频，当向下滑到不足3个视频时，需要向服务器获取视频资源
     * videoToPlayArrayList
     */
    fun renewPlayList(){
        if(isGetting){

        }else {
            isGetting=true
            Log.d(TAG, "renew正在获取置为true")
            val repeatTime=GET_VIDEO_LINE + 1 + playNow - videoToPlayArrayList.size
            if(repeatTime<=0) isGetting=false
            Log.d(TAG, "renew正在获取置为false")
            repeat(repeatTime) {
                getOneNewVideo()
            }
        }
    }

    private fun getOneNewVideo(){
        var mApi=retrofit.create(GetWorkData::class.java)
        Log.d(TAG, "获取id:"+ userId)
        var observable: Observable<ResultModel> =mApi.getRecommendVideoId(MainActivity.userId)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<ResultModel>(){
                //成功逻辑
                override fun onSuccess(t:ResultModel) {
                    getVideoByRecommendId(t.data as String)
                    Log.d("linlin", "id获取:"+t)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("linlin", "id获取失败 ")
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getVideoByRecommendId(videoId:String){
        var mApi=retrofit.create(GetWorkData::class.java)
        var observable: Observable<Work> =mApi.getVideoById(videoId)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<Work>(){
                //成功逻辑
                override fun onSuccess(t:Work) {
                    videoToPlayArrayList.add(t)
                    Log.d("linlin", "视频获取:"+t.workId)
                    mAdapter.notifyDataSetChanged()
                    isGetting=false
                    Log.d(TAG, "success正在获取置为false")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("linlin", "视频获取失败 ")
                    isGetting=false
                    Log.d(TAG, "error正在获取置为false")
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun feedbackVideo(videoId:String, userId:String, seedingRate:Int, isLike:Boolean){
        var mApi=retrofit.create(GetWorkData::class.java)
        var observable: Observable<ResultModel> =mApi.feedbackVideo(videoId,userId,seedingRate,isLike)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<ResultModel>(){
                //成功逻辑
                override fun onSuccess(t:ResultModel) {
                    Log.d("linlin", "反馈成功")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("linlin", "反馈失败")
                }

                //提示逻辑
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }



}
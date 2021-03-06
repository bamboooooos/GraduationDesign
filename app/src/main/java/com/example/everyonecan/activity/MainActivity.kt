package com.example.everyonecan.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    val GET_VIDEO_LINE:Int=3    //???????????????3????????????????????????????????????????????????

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
        applyPermission()
    }

    fun applyPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO),
                100);
            return
        }
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
//            Toast.makeText(this,"????????????????????????", Toast.LENGTH_SHORT).show()
            val intentToBloger:Intent=Intent(this,UserActivity().javaClass)
            intentToBloger.putExtra("isMyself",true)
            startActivity(intentToBloger)
        }
        blogger.setOnClickListener {
//            Toast.makeText(this,"????????????????????????",Toast.LENGTH_SHORT).show()
            val intentToBloger:Intent=Intent(this,UserActivity().javaClass)
            var blogerId=if(videoToPlayArrayList.size>0) videoToPlayArrayList[playNow].workAuthorId else "0000"
            intentToBloger.putExtra("blogerId",blogerId)
            startActivity(intentToBloger)
        }
        toRead.setOnClickListener {
            //?????????????????????????????????
//            Toast.makeText(this,"???????????????????????????",Toast.LENGTH_SHORT).show()
            val dialog:VideoListDialog= VideoListDialog(this,videoToPlayArrayList)
            dialog.show()
        }
        recording.setOnClickListener {
            //TODO ????????????????????????
            startActivity(Intent(this,RecordActivity().javaClass))
//            Toast.makeText(this,"????????????????????????",Toast.LENGTH_SHORT).show()

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
                Log.d("linlin", "??????:"+playNow)
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
                if(lastPlay!= playNow){//??????????????????
                    //????????????
                    var seedingRate:Int=((duration.toDouble())/(allDuration.toDouble())).toInt()
                    feedbackVideo(videoToPlayArrayList[playNow].workId, userId,seedingRate,true)
//                    Log.d(TAG, "??????????????????:"+(duration.toDouble())/(allDuration.toDouble()))
                    lastPlay= playNow
                    duration=0
                    allDuration=1000
                }else{
                    allDuration=videoPlayer.duration
                    Log.d(TAG, "????????????:"+allDuration)
                    duration=videoPlayer.currentPositionWhenPlaying
                    Log.d(TAG, "????????????:"+duration)
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
                //????????????
                override fun onSuccess(t:ArrayList<Work>) {
                    for (i:Work in t){
                        videoToPlayArrayList.add(i)
                    }
                    mAdapter.notifyDataSetChanged()
                }

                //????????????
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun initTestData(){
        repeat(1) {
            videoToPlayArrayList.add(Work("0001","????????????1","1001","????????????1","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/280443.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0002","????????????2","1002","????????????2","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276982.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0003","????????????3","1003","????????????3","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276984.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0004","????????????4","1004","????????????4","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276985.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0005","????????????5","1005","????????????5","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276986.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0006","????????????6","1006","????????????6","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276987.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0007","????????????7","1007","????????????7","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276988.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0008","????????????8","1008","????????????8","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276989.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0009","????????????9","1009","????????????9","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276990.mp4",Date().toString()))
            videoToPlayArrayList.add(Work("0010","????????????10","1010","????????????10","https://p0.qhimg.com/t015f3654b694ad2f8a.jpg","https://v-cdn.zjol.com.cn/276991.mp4",Date().toString()))
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
     * ???????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????????????????3?????????????????????????????????3???????????????????????????????????????????????????
     * videoToPlayArrayList
     */
    fun renewPlayList(){
        if(isGetting){

        }else {
            isGetting=true
            Log.d(TAG, "renew??????????????????true")
            val repeatTime=GET_VIDEO_LINE + 1 + playNow - videoToPlayArrayList.size
            if(repeatTime<=0) isGetting=false
            Log.d(TAG, "renew??????????????????false")
            repeat(repeatTime) {
                getOneNewVideo()
            }
        }
    }

    private fun getOneNewVideo(){
        var mApi=retrofit.create(GetWorkData::class.java)
        Log.d(TAG, "??????id:"+ userId)
        var observable: Observable<ResultModel> =mApi.getRecommendVideoId(MainActivity.userId)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: RxSubscribe<ResultModel>(){
                //????????????
                override fun onSuccess(t:ResultModel) {
                    getVideoByRecommendId(t.data as String)
                    Log.d("linlin", "id??????:"+t)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("linlin", "id???????????? ")
                }

                //????????????
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
                //????????????
                override fun onSuccess(t:Work) {
                    videoToPlayArrayList.add(t)
                    Log.d("linlin", "????????????:"+t.workId)
                    mAdapter.notifyDataSetChanged()
                    isGetting=false
                    Log.d(TAG, "success??????????????????false")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("linlin", "?????????????????? ")
                    isGetting=false
                    Log.d(TAG, "error??????????????????false")
                }

                //????????????
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
                //????????????
                override fun onSuccess(t:ResultModel) {
                    Log.d("linlin", "????????????")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("linlin", "????????????")
                }

                //????????????
                override fun onHint(hint: String) {
                    Toast.makeText(application,hint, Toast.LENGTH_SHORT).show()
                }
            })
    }



}
package com.example.everyonecan.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.everyonecan.R
import com.example.everyonecan.Work
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_video_play.view.*
import kotlinx.android.synthetic.main.item_work.view.*
import kotlin.time.seconds

class MainPlayerAdapter(val videoList:ArrayList<Work>):RecyclerView.Adapter<MainPlayerAdapter.ViewHolder>() {

    lateinit var mContext:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext=parent.context
        val view:View=LayoutInflater.from(mContext).inflate(R.layout.item_video_play,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //封面
        val imageView:ImageView= ImageView(mContext)
        Picasso.with(mContext)
            .load(videoList[position].workCoverSrc) //加载地址,work.workCoverSrc
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher) //加载失败的图
            .fit() //充满
            .tag(mContext) //标记
            .into(imageView);//加载到的ImageView
        holder.videoPlayer.thumbImageView=imageView
        holder.videoPlayer.titleTextView.visibility=View.INVISIBLE     //设置不显示标题
        holder.videoPlayer.backButton.visibility=View.INVISIBLE      //设置不显示返回键
        holder.videoPlayer.setIsTouchWiget(true)
        holder.videoPlayer.backButton.setOnClickListener {
            (mContext as Activity).onBackPressed()
        }
        holder.videoPlayer.setUp(videoList[position].workAddressSrc,false,videoList[position].workTitle)
        //holder.videoPlayer.startPlayLogic()
    }



    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        lateinit var coverIma:ImageView
        val videoPlayer:StandardGSYVideoPlayer=view.video_view
    }


//    override fun onViewAttachedToWindow(holder: ViewHolder) {
//        super.onViewAttachedToWindow(holder)
//    }

//    override fun onViewDetachedFromWindow(holder: ViewHolder) {
//        super.onViewDetachedFromWindow(holder)
//    }
}
package com.example.everyonecan.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyonecan.R
import com.example.everyonecan.Work
import com.example.everyonecan.adapter.VideoListAdapter
import kotlinx.android.synthetic.main.dialog_video_list.*

class VideoListDialog(val mContext:Context,var videoList:ArrayList<Work>):AlertDialog(mContext,R.style.CustomDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_video_list)
        initData()
        initView()
    }

    fun initData(){
        //TODO 可能的网络请求
    }

    fun initView(){
        val mRecyclerView:RecyclerView=videoToPlayList
        val layoutManager:LinearLayoutManager= LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false)
        mRecyclerView.layoutManager=layoutManager
        mRecyclerView.setPadding(0,10,0,10)
        var mAdapter:VideoListAdapter= VideoListAdapter(videoList,mContext)
        mRecyclerView.adapter=mAdapter
    }
}
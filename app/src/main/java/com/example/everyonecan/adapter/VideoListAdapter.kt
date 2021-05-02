package com.example.everyonecan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.everyonecan.R
import com.example.everyonecan.Work
import kotlinx.android.synthetic.main.item_work_to_play.view.*
import java.text.SimpleDateFormat

class VideoListAdapter(var videoToShowList:ArrayList<Work>,var mContext:Context):RecyclerView.Adapter<VideoListAdapter.ViewHolder>(){


    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        var videoTitle:TextView=view.findViewById(R.id.videoToPlayTitle)
        var videoAuthor:TextView=view.findViewById(R.id.videoToPlayAuthor)
        var videoTime:TextView=view.findViewById(R.id.videoToPlayTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View=LayoutInflater.from(mContext).inflate(R.layout.item_work_to_play,parent,false)
        view.setOnClickListener {
            //TODO 点击视频列表事件
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videoToShowList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.videoTitle.text=videoToShowList[position].workTitle
        holder.videoAuthor.text=videoToShowList[position].workAuthor
        holder.videoTime.text=videoToShowList[position].workUpdateTime
    }

}
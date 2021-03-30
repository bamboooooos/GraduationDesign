package com.example.everyonecan.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.everyonecan.R
import com.example.everyonecan.Work
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_work.view.*

class WorksAdapter(val workList:ArrayList<Work>,val mStaggeredGridLayoutManager: StaggeredGridLayoutManager,private val mContext:Context):RecyclerView.Adapter<WorksAdapter.ViewHolder>(){


    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val workCover:ImageView=view.workCardCover
        var workTitle:TextView=view.workCardTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_work,parent,false)
        val viewHolder=ViewHolder(view)
        viewHolder.workCover.setOnClickListener {
            //TODO 封面点击事件
        }
        viewHolder.workTitle.setOnClickListener {
            //TODO 标题点击事件
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val work:Work=workList[position]
        holder.workTitle.text=work.workTitle
        //视频卡封面联网加载
        //holder.workCover.setImageResource(R.drawable.ic_baseline_more_horiz_24)
        Picasso.with(mContext)
            .load("https://p0.qhimg.com/t015f3654b694ad2f8a.jpg") //TODO 加载地址,work.workCoverSrc
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher) //加载失败的图
            .fit() //充满
            .tag(mContext) //标记
            .into(holder.workCover);//加载到的ImageView 
        var params=holder.itemView.layoutParams
        /**itemview包括一个封面和标题，标题高固定为25，设置封面为正方形则设置itemview高与宽相同，而itemview还未初始化，宽为-1
         * 需要通过mStaggeredGridLayoutManager获取的宽以及列数
         */
        params.height=mStaggeredGridLayoutManager.width/mStaggeredGridLayoutManager.spanCount+30
        //Log.d("lin",""+params.height+","+params.width)
        //holder.itemView.layoutParams=params
    }

    override fun getItemCount(): Int {
        return workList.size
    }
}
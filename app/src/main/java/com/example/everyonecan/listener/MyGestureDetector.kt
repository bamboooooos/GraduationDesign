package com.example.everyonecan.listener

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast


// e1为点击移动前的event,e2为移动后,velocityX为x方向的速度，y同理
class MyGestureDetector:GestureDetector.SimpleOnGestureListener{

    val mContext:Context
    val MIN_DISTANT:Int=100   //移动最小判定距离

    constructor(context:Context){
        mContext=context
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if(e1==null||e2==null) return false;
        var distantX:Float=e2.getX()-e1.getX()  //移动在X方向的距离，正为右，负为左
        var distantY:Float=e2.getY()-e1.getY()  //移动在y方向的距离，正为下，负为上
        if(Math.abs(distantX)>Math.abs(distantY)){  //左右距离大于上下距离
            if(distantX>MIN_DISTANT){
                //TODO 右滑事件
                Toast.makeText(mContext,"触发右滑事件",Toast.LENGTH_SHORT).show()
            }
            if(distantX<-MIN_DISTANT){
                //TODO 左滑事件
                Toast.makeText(mContext,"触发左滑事件",Toast.LENGTH_SHORT).show()
            }
        }else{
            if(distantY>MIN_DISTANT){
                //TODO 下滑事件
                Toast.makeText(mContext,"触发下滑事件",Toast.LENGTH_SHORT).show()
            }
            if(distantY<-MIN_DISTANT){
                //TODO 上滑事件
                Toast.makeText(mContext,"触发上滑事件",Toast.LENGTH_SHORT).show()
            }
        }
        return true;
    }
}
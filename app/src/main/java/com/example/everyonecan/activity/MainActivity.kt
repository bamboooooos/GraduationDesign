package com.example.everyonecan.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import com.example.everyonecan.R
import com.example.everyonecan.listener.MyGestureDetector
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var gestureDetector:GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListener()
    }

    fun initListener():Unit{
        myself.setOnClickListener {
            //TODO 点击进入“我的”页面
            Toast.makeText(this,"触发我的点击事件", Toast.LENGTH_SHORT).show()
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
            //TODO 点击“待播放”视频列表
            Toast.makeText(this,"触发待播放点击事件",Toast.LENGTH_SHORT).show()
        }
        recording.setOnClickListener {
            //TODO 点击录制我的视频
            Toast.makeText(this,"触发录制点击事件",Toast.LENGTH_SHORT).show()
        }
        gestureDetector= GestureDetector(this,MyGestureDetector(this))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}
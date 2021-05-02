package com.example.everyonecan.listener

import android.annotation.TargetApi
import android.os.Build
import android.transition.Transition


/**
 * 重载了过渡动画的方法
 * Created by shuyu on 2016/8/15.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
open class OnTransitionListener : Transition.TransitionListener {
    override fun onTransitionStart(transition: Transition) {}
    override fun onTransitionEnd(transition: Transition) {}
    override fun onTransitionCancel(transition: Transition) {}
    override fun onTransitionPause(transition: Transition) {}
    override fun onTransitionResume(transition: Transition) {}
}
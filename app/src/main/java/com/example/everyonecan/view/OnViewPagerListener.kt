package com.example.everyonecan.view

interface OnViewPagerListener {
    /**
     * 初始化完成
     */
    fun onInitComplete()

    /**
     * 释放的监听
     * @param isNext
     * @param position
     */
    fun onPageRelease(isNext: Boolean, position: Int)

    /**
     * 选中的监听以及判断是否滑动到底部
     * @param position
     * @param isBottom
     */
    fun onPageSelected(position: Int, isBottom: Boolean)

    /**
     * 视频滑动
     */
    fun onVideoSlided(position: Int)
}
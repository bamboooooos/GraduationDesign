package com.example.everyonecan.view


import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import androidx.recyclerview.widget.RecyclerView.Recycler


class VideoPlayLayoutManager : LinearLayoutManager, OnChildAttachStateChangeListener {
    //位移，用来判断移动方向
    private var mDrift = 0
    private var mPagerSnapHelper: PagerSnapHelper? = null
    private var mOnViewPagerListener: OnViewPagerListener? = null

    constructor(context: Context?) : super(context) {}
    constructor(
        context: Context?,
        orientation: Int,
        reverseLayout: Boolean
    ) : super(context, orientation, reverseLayout) {
        mPagerSnapHelper = PagerSnapHelper()
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        view.addOnChildAttachStateChangeListener(this)
        mPagerSnapHelper!!.attachToRecyclerView(view)
        super.onAttachedToWindow(view)
        mOnViewPagerListener!!.onInitComplete()
    }

    /**
     * 当Item添加进来了  调用这个方法
     * 播放视频操作 即将要播放的是上一个视频 还是下一个视频
     * @param view
     */
    override fun onChildViewAttachedToWindow(view: View) {
        if (mDrift > 0) {
//            向上
            if (mOnViewPagerListener != null) {
                mOnViewPagerListener!!.onPageSelected(getPosition(view), true)
            }
        } else {
            if (mOnViewPagerListener != null) {
                mOnViewPagerListener!!.onPageSelected(getPosition(view), false)
            }
        }
    }

    fun setOnViewPagerListener(mOnViewPagerListener: OnViewPagerListener?) {
        this.mOnViewPagerListener = mOnViewPagerListener
    }

    /**
     * OnScrollListener.SCROLL_STATE_FLING; //屏幕处于甩动状态
     * OnScrollListener.SCROLL_STATE_IDLE; //停止滑动状态
     * OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;// 手指接触状态
     *
     * @param state
     */
    override fun onScrollStateChanged(state: Int) {
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                val view = mPagerSnapHelper!!.findSnapView(this)
                val position = getPosition(view!!)
                if (mOnViewPagerListener != null) {
                    mOnViewPagerListener!!.onVideoSlided(position)
                    mOnViewPagerListener!!.onPageSelected(position, position == itemCount - 1)

                }
            }
            else -> {
//                val view = mPagerSnapHelper!!.findSnapView(this)
//                val position = getPosition(view!!)
            }
        }
        super.onScrollStateChanged(state)
    }

    /**
     * 暂停播放操作
     * @param view
     */
    override fun onChildViewDetachedFromWindow(view: View) {
        if (mDrift >= 0) {
            if (mOnViewPagerListener != null) {
                mOnViewPagerListener!!.onPageRelease(true, getPosition(view))
            }
        } else {
            if (mOnViewPagerListener != null) {
                mOnViewPagerListener!!.onPageRelease(false, getPosition(view))
            }
        }
    }

    /**
     * 监听移动方向
     * @param dy
     * @param recycler
     * @param state
     * @return
     */
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: Recycler,
        state: RecyclerView.State
    ): Int {
        mDrift = dy
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    override fun canScrollVertically(): Boolean {
        return true
    }
}
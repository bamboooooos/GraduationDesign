package com.example.everyonecan.view

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration:RecyclerView.ItemDecoration{

    private val space:Int

    constructor(space:Int){
        this.space=space
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        outRect.left=space
        outRect.right=space
        outRect.bottom=space
        outRect.top=space
    }
}
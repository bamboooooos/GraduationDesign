package com.example.everyonecan

import com.example.everyonecan.util.StringAndIntUtil
import java.util.*


/**
 * 作品类，可能包括封面图，视频地址，标题，作者，id等等
 */
class Work {
    lateinit var workId: String
    lateinit var workAuthor:String
    lateinit var workAuthorId:String
    lateinit var workTitle:String
    lateinit var workCoverSrc:String
    lateinit var workAddressSrc:String
    lateinit var workUpdateTime:String
    constructor(
        workId: String,
        workAuthor: String,
        workAuthorId: String,
        workTitle: String,
        workCoverSrc: String,
        workAddressSrc: String,
        workUpdateTime:String
    ) {
        if(StringAndIntUtil.StringIsInt(workId)){
            this.workId = workId
        }else{
            this.workId = "0000"
        }
        this.workAuthor = workAuthor
        if(StringAndIntUtil.StringIsInt(workAuthorId)) {
            this.workAuthorId = workAuthorId
        }else{
            this.workAuthorId = "0000"
        }
        this.workTitle = workTitle
        this.workCoverSrc = workCoverSrc
        this.workAddressSrc = workAddressSrc
        this.workUpdateTime=workUpdateTime
    }

    override fun toString(): String {
        return workId+","+workAuthor+","+workAuthorId+","+workTitle+","+workCoverSrc+","+workAddressSrc+","+workUpdateTime
    }
}
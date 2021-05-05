package com.example.everyonecan.util

import android.app.Activity

class ActivityCollector {

    companion object {
        public var activities:ArrayList<Activity> = ArrayList()

        fun addActivity(activity: Activity){
            activities.add(activity)
        }

        fun removeActivity(activity: Activity){
            activities.remove(activity)
        }

        fun finishAll(){
            for (a in activities){
                if(!a.isFinishing){
                    a.finish()
                }
            }
            activities.clear()
        }
    }

}
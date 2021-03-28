package com.example.everyonecan.util

object StringAndIntUtil {
    fun StringIsInt(str:String):Boolean{
        for(a:Char in str){
            if(!Character.isDigit(a)){
                return false
            }
        }
        return true
    }
}
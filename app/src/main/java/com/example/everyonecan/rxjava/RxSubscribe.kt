package com.example.everyonecan.rxjava

import android.util.Log
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.Exception

abstract class RxSubscribe<T>:Observer<T>{
    public lateinit var disposable:Disposable

    override fun onSubscribe(d: Disposable) {
        this.disposable=d
    }

    override fun onNext(t: T) {
        try {
            onSuccess(t)
        }catch (e:Exception){
            e.printStackTrace()
            onHint("数据异常Exception:"+e.message)
        }
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
    }

    override fun onComplete() {
        Log.d("rxSub","End the subscribe")
    }

    @Throws(Exception::class)
    public abstract fun onSuccess(t: T)

    public abstract fun onHint(hint:String)
}
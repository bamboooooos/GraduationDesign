package com.example.everyonecan.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.everyonecan.R
import com.example.everyonecan.ResultModel
import com.example.everyonecan.activity.LoginActivity
import com.example.everyonecan.activity.MainActivity
import com.example.everyonecan.api.UploadVideo
import com.example.everyonecan.rxjava.RxSubscribe
import com.example.everyonecan.util.RxUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.dialog_upload.*
import kotlinx.android.synthetic.main.dialog_video_list.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import java.io.File


class VideoUploadDialog(
    val mContext: Context,
    var imageView: ImageView,
    var filePath: String,
    var fileName: String
): AlertDialog(
    mContext,
    R.style.CustomDialog
) {

    var imagePath:String=""
    lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_upload)
        initListener()
        retrofit= RxUtil.initRetrofit(
            RxUtil.initHttpConfig(mContext),
            LoginActivity.baseUrl
        )
        setCancelable(false)
        this.window!!.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    }

    fun initListener(){

        iv_select_cover.setOnClickListener {
            val intent:Intent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            (mContext as Activity).startActivityForResult(intent, 1)
        }

        btn_upload_cancel_dialog.setOnClickListener {
            dismiss()
        }

        btn_upload.setOnClickListener {
            var mApi=retrofit.create(UploadVideo::class.java)
            var toUploadFile:File=File(filePath)
            var workScoreData:String=getCheckBoxData()
            mApi.uploadVideo(parseRequestBody(toUploadFile, File(imagePath),MainActivity.userId, workScoreData))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : RxSubscribe<ResultModel>() {
                    override fun onSuccess(t: ResultModel) {
                        Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }

                    override fun onHint(hint: String) {
                        Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show()
                    }
                })

        }
    }



    override fun dismiss() {
        super.dismiss()
        imageView.visibility= View.GONE
    }

    fun parseRequestBody(file: File, image:File,userId: String, workScoreData: String): RequestBody {
        return MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody("video/mpeg4".toMediaTypeOrNull())
            )
            .addFormDataPart(
                "image",
                image.name,
                image.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .addFormDataPart("authorId", userId)
            .addFormDataPart("workScoreData", workScoreData)
            .addFormDataPart("workTitle", et_video_title.text.toString())
            .build()
    }

    fun getCheckBoxData():String{
        var result:StringBuilder= StringBuilder()
        if(btn_upload_type_funny.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        if(btn_upload_type_strive.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        if(btn_upload_type_music.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        if(btn_upload_type_dance.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        if(btn_upload_type_knowledge.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        if(btn_upload_type_live.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        if(btn_upload_type_game.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        if(btn_upload_type_film.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        if(btn_upload_type_clip.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        if(btn_upload_type_pet.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        if(btn_upload_type_beauty.isChecked){
            result.append("+")
        }else{
            result.append("-")
        }
        return result.toString()
    }

    fun UploadFileInfo(path: String, value: String, url: String): Call {

        // 获取要上传的文件
        val mFile = File(path)
        val client: OkHttpClient =  OkHttpClient.Builder().build()
        // 设置文件以及文件上传类型封装
        val requestBody: RequestBody = RequestBody.create("video/mpeg4".toMediaType(), mFile)
        // 文件上传的请求体封装
        val multipartBody: MultipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", mFile.name, requestBody)
            .addFormDataPart("u_id", value)
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .post(multipartBody)
            .build()
        return client.newCall(request)
    }

    fun resultImage(imagePath: String){
        this.imagePath=imagePath
        val bm = BitmapFactory.decodeFile(imagePath)
        iv_select_cover.setImageBitmap(bm)
    }

}
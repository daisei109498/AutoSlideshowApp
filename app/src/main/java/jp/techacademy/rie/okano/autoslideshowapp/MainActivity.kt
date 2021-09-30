package jp.techacademy.rie.okano.autoslideshowapp

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.os.Handler
import android.widget.Toast


class MainActivity : AppCompatActivity(),View.OnClickListener {
    private val context: Context? = null
    private var resolver: ContentResolver? = null
    private var cursor: Cursor? = null
    private var mTimer: Timer? = null
    private var mHandler = Handler()

    private val PERMISSIONS_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        back_button.setOnClickListener(this)
        next_button.setOnClickListener(this)
        auto_button.setOnClickListener{
            if (auto_button.text == "再生"){
                // クリックを無効にする
                back_button.isClickable = false
                // クリックを無効にする
                next_button.isClickable = false
                 auto_button.text = "停止"
                if (mTimer == null){
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mHandler.post {
                                getContentsNext()
                            }
                        }
                    }, 2000, 2000) // 最初に始動させるまで100ミリ秒、ループの間隔を100ミリ秒 に設定
                }
            }else{
                auto_button.text = "再生"
                // クリックを有効にする
                back_button.isClickable = true
                // クリックを有効にする
                next_button.isClickable = true
                if (mTimer != null){
                    mTimer!!.cancel()
                    mTimer = null
                }
            }
        }

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
        
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }else{
                    Toast.makeText(applicationContext, "パーミッションの許可が必要です", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {
                    R.id.back_button -> {
                        Log.d("ANDROID", "back")
                        getContentsBack()

                    }
                    R.id.next_button -> {
                        Log.d("ANDROID", "next")
                        getContentsNext()

                    }
                    R.id.auto_button -> {
                        Log.d("ANDROID", "auto")
                    }
                    else -> {
                        //
                    }
                }

            }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        resolver = contentResolver
        cursor = resolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }
    }

    private fun getContentsBack() {
        if (!cursor!!.moveToPrevious()) {
            cursor!!.moveToLast();
        }
        // indexからIDを取得し、そのIDから画像のURIを取得する
        val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor!!.getLong(fieldIndex)
        val imageUri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
        )

        imageView.setImageURI(imageUri)
    }

    private fun getContentsNext() {
        if (!cursor!!.moveToNext()) {
            cursor!!.moveToFirst()
        }
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            imageView.setImageURI(imageUri)
    }

}
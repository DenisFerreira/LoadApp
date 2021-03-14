package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val id = intent?.extras?.getLong(PARAM_DOWNLOAD_FILE_ID, -1)
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.cancel(MainActivity.NOTIFICATION_ID)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val statusField: TextView = findViewById(R.id.statusValue)
        val nameField: TextView = findViewById(R.id.fileNameValue)

        //DownloadManager.Query() is used to filter DownloadManager queries
        val query = DownloadManager.Query()
        query.setFilterById(id!!)

        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            nameField.text = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> statusField.text = "Success"
                else -> statusField.text = "Failed"
            }
        }
        val button: Button = findViewById(R.id.back_to_main_button)
        button.setOnClickListener {
            val myIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(myIntent)
            finish()
        }
        val motionLayout: MotionLayout = findViewById(R.id.motion_detail_layout)
        motionLayout.transitionToEnd()

    }

    companion object {
        const val PARAM_DOWNLOAD_FILE_ID = "download_id"
    }


}

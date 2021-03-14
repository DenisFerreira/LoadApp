package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var URL = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        notificationManager = getSystemService(
            NotificationManager::class.java
        )
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel(CHANNEL_ID, CHANNEL_NAME)

        val customButton: LoadingButton = findViewById(R.id.custom_button)
        customButton.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val contentIntent = Intent(applicationContext, DetailActivity::class.java)
            contentIntent.putExtra(DetailActivity.PARAM_DOWNLOAD_FILE_ID, id)
            val contentPendingIntent = PendingIntent.getActivity(
                applicationContext,
                NOTIFICATION_ID,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_view, getString(R.string.notification_button), contentPendingIntent)
                .setContentIntent(contentPendingIntent)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if(android.os.Build.VERSION.SDK_INT>= android.os.Build.VERSION_CODES.O) {
            val notificationChannel= NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationChannel.apply{
                enableLights(true)
                lightColor= Color.RED
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        } else{
            TODO("VERSION.SDK_INT < O")
        }
    }


    private fun download() {
        if (!URL.isEmpty()) {
            val request =
                DownloadManager.Request(Uri.parse(URL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else Toast.makeText(
            applicationContext,
            getString(R.string.ask_to_select_file),
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "LOADAPPChannel"
        const val NOTIFICATION_ID = 0

    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            if (view.isChecked)

            // Check which radio button was clicked
                URL = when (view.getId()) {
                    R.id.glide_radio_btn -> "https://github.com/bumptech/glide"
                    R.id.project_radio_btn -> "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                    R.id.retrofit_radio_btn -> "https://github.com/square/retrofit"
                    else -> ""
                }
            else URL = ""
        }
    }


}

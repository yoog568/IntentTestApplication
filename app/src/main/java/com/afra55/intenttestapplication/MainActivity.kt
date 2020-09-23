package com.afra55.intenttestapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.Build.VERSION_CODES.N
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import androidx.work.*
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.logging.MoPubLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import com.mopub.mobileads.MoPubInterstitial
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    companion object {
        var isResume = false
    }

    override fun onResume() {
        super.onResume()
        isResume = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WorkManager.getInstance(this).cancelAllWorkByTag("KEEP_ALIVE")
        val uploadWorkRequest = OneTimeWorkRequestBuilder<KeepLiveWork>()
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS  ,
                        TimeUnit.MILLISECONDS)
                .addTag("KEEP_ALIVE")
                .build()

        WorkManager.getInstance(this).enqueue(uploadWorkRequest)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            AlarmReceiver.setAlarm(this, true)
        }

    }
}

class KeepLiveWork(val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {
    companion object {
        var number = 0

    }

    override fun doWork(): Result {


        GlobalScope.launch(Dispatchers.Main) {

                    val intent = Intent(appContext, OnPixActivity::class.java)
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                    appContext.startActivity(intent)
                    Log.i("KeepAlive", "doWork start ${Thread.currentThread().name}")

        }
        return Result.retry()
    }


}


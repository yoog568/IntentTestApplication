package com.afra55.intenttestapplication

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.xdandroid.hellodaemon.DaemonEnv
import com.xdandroid.hellodaemon.IntentWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


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

        TraceServiceImpl.sShouldStopService = false
        DaemonEnv.startServiceMayBind(TraceServiceImpl::class.java)

        IntentWrapper.whiteListMatters(this, "轨迹跟踪服务的持续运行")
    }

    //防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
    override fun onBackPressed() {
        IntentWrapper.onBackPressed(this)
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


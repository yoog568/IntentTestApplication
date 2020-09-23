package com.afra55.intenttestapplication

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.view.View
import com.xdandroid.hellodaemon.AbsWorkService
import com.xdandroid.hellodaemon.DaemonEnv
import com.xdandroid.hellodaemon.IntentWrapper
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TraceServiceImpl : AbsWorkService() {
    /**
     * 是否 任务完成, 不再需要服务运行?
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    override fun shouldStopService(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Boolean {
        return sShouldStopService
    }

    override fun startWork(
        intent: Intent?,
        flags: Int,
        startId: Int
    ) {
        Log.i("KeepAlive", "检查磁盘中是否有上次销毁时保存的数据")
        sDisposable = Observable
            .interval(3, TimeUnit.SECONDS) //取消任务时取消定时唤醒
            .doOnDispose {
                Log.i("KeepAlive", "保存数据到磁盘。")
                cancelJobAlarmSub()
            }
            .subscribe { count: Long ->
                Log.i("KeepAlive", "每 3 秒采集一次数据... count = $count")
                if (count > 0 && count % 18 == 0L) Log.i(
                    "KeepAlive",
                    "保存数据到磁盘。 saveCount = " + (count / 18 - 1)
                )
//                GlobalScope.launch(Dispatchers.Main) {

                try {
                    val intent = Intent(this, OnPixActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Log.i("KeepAlive", "doWork start from Trace ${Thread.currentThread().name}")
                } catch (e: Exception) {
                } finally {
                }
                playMusic()

//                }
            }
    }

    override fun stopWork(
        intent: Intent?,
        flags: Int,
        startId: Int
    ) {
        stopService()
    }

    /**
     * 任务是否正在运行?
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    override fun isWorkRunning(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Boolean {
        //若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable!!.isDisposed
    }

    override fun onBind(
        intent: Intent?,
        v: Void?
    ): IBinder? {
        return null
    }

    override fun onServiceKilled(rootIntent: Intent) {
        Log.i("KeepAlive", "保存数据到磁盘。")
        DaemonEnv.startServiceMayBind(TraceServiceImpl::class.java)

    }

    var mediaPlayerFromScene: MediaPlayer? = null

    fun playMusic(){
        if (mediaPlayerFromScene == null) {
            mediaPlayerFromScene = MediaPlayer()
            if (mediaPlayerFromScene?.isPlaying != true) {

                mediaPlayerFromScene?.setOnPreparedListener {
                    it.isLooping = true
                    it.setVolume(0f, 0f)
                    it.start()
                }
                mediaPlayerFromScene?.apply {
                    val openFd = assets.openFd("music/ripple.mp3")
                    setDataSource(openFd.fileDescriptor, openFd.startOffset, openFd.length)
                    prepareAsync()
                }
            }
        }

    }


    companion object {
        //是否 任务完成, 不再需要服务运行?
        var sShouldStopService = false
        var sDisposable: Disposable? = null
        fun stopService() {
            //我们现在不再需要服务运行了, 将标志位置为 true
            sShouldStopService = true
            //取消对任务的订阅
            if (sDisposable != null) sDisposable!!.dispose()
            //取消 Job / Alarm / Subscription
            cancelJobAlarmSub()
        }
    }
}
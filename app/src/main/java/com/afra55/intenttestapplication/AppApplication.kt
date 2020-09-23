package com.afra55.intenttestapplication

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.xdandroid.hellodaemon.DaemonEnv
import me.weishu.leoric.Leoric
import me.weishu.leoric.LeoricConfigs
import me.weishu.leoric.LeoricConfigs.LeoricConfig

/**
 * @author Afra55
 * @date 2020/9/22
 * A smile is the best business card.
 * 没有成绩，连呼吸都是错的。
 */
class AppApplication : Application() {

    companion object{
        var context:Context? = null

        fun toast(str:String) {
            if (context != null) {
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        Leoric.init(
            base, LeoricConfigs(
                LeoricConfig(
                    "$packageName:resident",
                    Service1::class.java.canonicalName,
                    Receiver1::class.java.canonicalName,
                    Activity1::class.java.canonicalName
                ),
                LeoricConfig(
                    "android.media",
                    Service2::class.java.canonicalName,
                    Receiver2::class.java.canonicalName,
                    Activity2::class.java.canonicalName
                )
            )
        )
    }

    override fun onCreate() {
        super.onCreate()
        context = this;

        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(this, TraceServiceImpl::class.java, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL)
        TraceServiceImpl.sShouldStopService = false
        DaemonEnv.startServiceMayBind(TraceServiceImpl::class.java)
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
    class MessageEvent { /* Additional fields if needed */

    }


}
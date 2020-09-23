package com.afra55.intenttestapplication

import android.app.Application
import android.content.Context
import android.widget.Toast
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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

    override fun onCreate() {
        super.onCreate()
        context = this;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
    class MessageEvent { /* Additional fields if needed */

    }


}
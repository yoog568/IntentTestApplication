package com.afra55.intenttestapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.logging.MoPubLog
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubInterstitial
import kotlin.math.abs


/**
 * @author Afra55
 * @date 2020/9/22
 * A smile is the best business card.
 * 没有成绩，连呼吸都是错的。
 */
class OnPixActivity : AppCompatActivity() {

    val sp by lazy {
        getSharedPreferences("time_", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window: Window = window
        window.setGravity(Gravity.START or Gravity.TOP)
        val layoutParams: WindowManager.LayoutParams = window.attributes
        layoutParams.x = 0
        layoutParams.y = 0
        layoutParams.width = 1
        layoutParams.height = 1
        window.setAttributes(layoutParams)
        initAd(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (!isAdInited) {
            initAd(this)
        } else {
            if (!isAdShowing && abs(System.currentTimeMillis() - sp.getLong("time_ad_show", 0)) > 10 * 1000) {
                showInterstitial()
                sp.edit().putLong("time_ad_show", System.currentTimeMillis()).apply()
            }
        }

        Toast.makeText(this, "弹个广告 ${KeepLiveWork.number++}", Toast.LENGTH_SHORT).show()
    }

    var mInterstitial: MoPubInterstitial? = null
    var isAdInited = false
    var isLoadingAd = false
    var isAdShowing = false
    fun initAd(context: Context) {
        val sdkConfiguration: SdkConfiguration =
            SdkConfiguration.Builder("24534e1901884e398f1253216226017e")
                .withLogLevel( MoPubLog.LogLevel.DEBUG)
                .withLegitimateInterestAllowed(false)
                .build()
        MoPub.initializeSdk(context, sdkConfiguration){
            isAdInited = true
            showInterstitial()
        }
    }

    fun showInterstitial() {
        if (mInterstitial == null) {
            mInterstitial = MoPubInterstitial(this, "24534e1901884e398f1253216226017e")
            mInterstitial!!.interstitialAdListener = object :MoPubInterstitial.InterstitialAdListener{
                override fun onInterstitialLoaded(interstitial: MoPubInterstitial?) {
                    interstitial?.show()
                    isLoadingAd = false

                }

                override fun onInterstitialShown(interstitial: MoPubInterstitial?) {
                    isAdShowing = true
                }

                override fun onInterstitialFailed(
                    interstitial: MoPubInterstitial?,
                    errorCode: MoPubErrorCode?
                ) {
                    isLoadingAd = false
                }

                override fun onInterstitialDismissed(interstitial: MoPubInterstitial?) {
                    isAdShowing = false
                }

                override fun onInterstitialClicked(interstitial: MoPubInterstitial?) {
                    
                }

            }
        }
        if (!isLoadingAd) {

            mInterstitial?.load()
            isLoadingAd = true
        }
    }
}
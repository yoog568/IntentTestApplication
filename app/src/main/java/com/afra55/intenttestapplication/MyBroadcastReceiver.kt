package com.afra55.intenttestapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent != null) {
            val str = StringBuilder()
            str
                .append("\n")
                .append("action:").append(intent.action)
                .append("\n")
                .append("dataString:").append(intent.dataString)
                .append("\n")
                .append("package:").append(intent.`package`)
                .append("\n")
                .append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            Log.i("MyBroadcastReceiver", str.toString())
        }
    }
}

package bean.sample.screenbroadcastsample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.util.Log

class SystemReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("test","onReceive context: $context")
        context?.startActivity(Intent(context, MainActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK).addFlags(
            FLAG_ACTIVITY_CLEAR_TASK))
    }
}
package bean.sample.screenbroadcastsample

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class DeviceLockService : Service(){
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startActivity(Intent(application, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//        Log.d("test", "DeviceLockService onStartCommand $this")
        return super.onStartCommand(intent, flags, startId)
    }
}
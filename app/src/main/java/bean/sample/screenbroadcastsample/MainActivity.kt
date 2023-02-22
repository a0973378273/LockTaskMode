package bean.sample.screenbroadcastsample

import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bean.sample.screenbroadcastsample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val broadcast = SystemReceiver()
    val packages = ArrayList<String>().apply {
        add("com.android.settings")
        add("com.android.dialer")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(localClassName, "MainActivity onCreate $this")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (intent.getBooleanExtra("reStart",false)) {
//            Log.d(localClassName, "MainActivity reStart")
//            startActivity(
//                Intent(this, MainActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK).addFlags(
//                    FLAG_ACTIVITY_SINGLE_TOP
//                )
//            )
//            finish()
//        }


//        registerSystemReceiver()
        hideActionBar()
        hideStatusAndNavigation()
        setUnLockButton()
        setCallButton()
        setWifiButton()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(localClassName,"onNewIntent")
    }

    override fun onResume() {
        super.onResume()
        MainScope().launch(Dispatchers.Main) {
            delay(2000)
            setLockTask(DeviceAdminSample::class.java, packages)
            delay(2000)
            Log.d(localClassName, "onResume, DO:${isDeviceOwnerPermission()}, lock: ${isLockTaskModeRunning()}")
            binding.textViewDeviceOwner.visibility = if (isDeviceOwnerPermission()) View.VISIBLE else View.GONE
            binding.textViewLockTaskMode.visibility = if (isLockTaskModeRunning()) View.VISIBLE else View.GONE
            setDefaultHomeApp(DeviceAdminSample::class.java, MainActivity::class.java)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(localClassName, "onPause")
        //Always front
//        moveTaskToFront()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(localClassName, "onDestroy $this")
//        unregisterReceiver(broadcast)
    }

    //Disable back button
    override fun onBackPressed() {}
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return true
    }

    private fun registerSystemReceiver() {
        val filter = IntentFilter()
        filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS")
        registerReceiver(broadcast, filter)
    }

    private fun setUnLockButton() {
        binding.buttonUnlock.setOnClickListener {
            clearDefaultHomeApp(DeviceAdminSample::class.java)
            removeLockTask()
            removeDeviceOwner()
            finish()
        }
    }

    private fun setCallButton() {
        binding.buttonCall.setOnClickListener {
            openCall()
        }
    }

    private fun setWifiButton() {
        binding.buttonWifi.setOnClickListener {
            openWifiSetting()
        }
    }

    private fun openCall() {
        val intent = Intent(ACTION_DIAL)
        startActivity(intent)
    }

    private fun openWifiSetting() {
        val intent = Intent(ACTION_WIFI_SETTINGS)
        startActivity(intent)
    }
}
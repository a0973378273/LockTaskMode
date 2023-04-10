package bean.sample.screenbroadcastsample

import android.Manifest
import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import bean.sample.screenbroadcastsample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private val broadcast = SystemReceiver()
//    private val register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//}

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
        binding.textViewDeviceOwner.visibility = if (isDeviceOwnerPermission()) View.VISIBLE else View.GONE
        binding.textViewLockTaskMode.visibility = if (setLockTask(AdminReceiver::class.java, getWhitePackage())) View.VISIBLE else View.GONE
        binding.textViewHomeLauncher.visibility = if (setDefaultHomeApp(AdminReceiver::class.java, MainActivity::class.java)) View.VISIBLE else View.GONE
        setUnLockButton()
        setCallButton()
        setEmergencyCall()
        setWifiButton()
        setLockTaskModeButton()
        setHomeLauncher()

        AdminReceiver.adminEnableLiveData.observe(this) {
            binding.textViewDeviceOwner.visibility = if (it) View.VISIBLE else View.GONE
//            Toast.makeText(this,"setLockTask $it", Toast.LENGTH_LONG).show()
            Log.d(localClassName, "Thread: ${Thread.currentThread().name} , admin: $it")
//            MainScope().launch(Dispatchers.Main) {
//                delay(1000)
//                if (it) Log.d(
//                    localClassName,
//                    "setLockTask: ${setLockTask(AdminReceiver::class.java, getWhitePackage())}"
//                )
//            }
        }

        AdminReceiver.lockTaskEnableLiveData.observe(this) {
            binding.textViewLockTaskMode.visibility = if (it) View.VISIBLE else View.GONE
//            Toast.makeText(this,"setDefaultHomeApp $it", Toast.LENGTH_LONG).show()
            Log.d(localClassName, "Thread: ${Thread.currentThread().name} , lock mode: $it")
//            if (it) {
//                binding.textViewHomeLauncher.visibility = if (setDefaultHomeApp(AdminReceiver::class.java, MainActivity::class.java)) View.VISIBLE else View.GONE
//                Log.d(localClassName, "setDefaultHomeApp: ${}")
//            }
        }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        Log.d(localClassName,"onNewIntent")
//        when (intent?.action){
//
//        }
//    }

//    override fun onResume() {
//        super.onResume()
//        Log.d(localClassName, "onResume, DO:${isDeviceOwnerPermission()}, lock: ${isLockTaskModeRunning()}")
//        MainScope().launch(Dispatchers.Main) {
//            delay(2000)
//            setLockTask(AdminRecediver::class.java, packages)
//            delay(2000)
//            Log.d(localClassName, "onResume-2s, DO:${isDeviceOwnerPermission()}, lock: ${isLockTaskModeRunning()}")
//            binding.textViewDeviceOwner.visibility = if (isDeviceOwnerPermission()) View.VISIBLE else View.GONE
//            binding.textViewLockTaskMode.visibility = if (isLockTaskModeRunning()) View.VISIBLE else View.GONE
//            setDefaultHomeApp(AdminRecediver::class.java, MainActivity::class.java)
//        }
//    }

//    override fun onPause() {
//        super.onPause()
//        Log.d(localClassName, "onPause")
        //Always front
//        moveTaskToFront()
//    }

//    override fun onResume() {
//        super.onResume()
//        register.launch(Intent(ACTION_DIAL))
//    }

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

    private fun setScreenText(){
        binding.buttonSetText.setOnClickListener {

        }
    }

//    private fun registerSystemReceiver() {
//        val filter = IntentFilter()
//        filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS")
//        registerReceiver(broadcast, filter)
//    }

    private fun setUnLockButton() {
        binding.buttonUnlock.setOnClickListener {
            clearDefaultHomeApp(AdminReceiver::class.java)
            removeLockTask()
            removeDeviceOwner()
            finish()
        }
    }

    private fun setCallButton() {
        binding.buttonCall.setOnClickListener {
            openCall("123456789")
        }
    }

    private fun setEmergencyCall() {
        binding.buttonEmergency.setOnClickListener {
            openEmergencyCall()
        }
    }

    private fun setWifiButton() {
        binding.buttonWifi.setOnClickListener {
            openWifiSetting()
        }
    }

    private fun setHomeLauncher() {
        binding.buttonHomeLauncher.setOnClickListener {
            val isHomeLauncher = setDefaultHomeApp(AdminReceiver::class.java, MainActivity::class.java)
            binding.textViewHomeLauncher.visibility = if (isHomeLauncher) View.VISIBLE else View.GONE
            if(isHomeLauncher)
                Toast.makeText(this,"Home Launcher Entering",Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this,"no device owner permission",Toast.LENGTH_LONG).show()
        }
    }

    private fun setLockTaskModeButton() {
        binding.buttonLockTaskMode.setOnClickListener {
            setLockTask(AdminReceiver::class.java, getWhitePackage())
        }
    }

    private fun getWhitePackage(): ArrayList<String>{
        val packages = ArrayList<String>()
        packageManager.queryIntentActivities(Intent(ACTION_DIAL),0).forEach {
            packages.add(it.activityInfo.packageName)
            Log.d(localClassName,"ACTION_DIAL package: ${it.activityInfo.packageName}")
        }
        packageManager.queryIntentActivities(Intent(ACTION_WIFI_SETTINGS),0).forEach {
            packages.add(it.activityInfo.packageName)
            Log.d(localClassName,"ACTION_WIFI_SETTINGS package: ${it.activityInfo.packageName}")
        }
        return packages
    }

}
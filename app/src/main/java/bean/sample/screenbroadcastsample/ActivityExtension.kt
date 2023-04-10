package bean.sample.screenbroadcastsample

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.DEVICE_POLICY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

/**
 * Need Device owner permission
 */
fun <T,H> Activity.setDefaultHomeApp(adminReceiverClass: Class<T>, homeClass: Class<H>): Boolean {
    val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponentName = ComponentName(this, adminReceiverClass)
    return if (devicePolicyManager.isDeviceOwnerApp(this.packageName)) {
        val intent = IntentFilter(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        devicePolicyManager.addPersistentPreferredActivity(
            adminComponentName, intent, ComponentName(this, homeClass)
        )
        true
    } else {
        false
    }
}

/**
 * Need Device owner permission
 */
fun <T> Activity.clearDefaultHomeApp(adminReceiverClass: Class<T>) {
    val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponentName = ComponentName(this, adminReceiverClass)
    if (devicePolicyManager.isDeviceOwnerApp(this.packageName)) {
        devicePolicyManager.clearPackagePersistentPreferredActivities(adminComponentName,packageName)
    }
}

/**
 * Need Device owner permission
 */
fun Activity.isDeviceOwnerPermission(): Boolean{
    val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
    return devicePolicyManager.isDeviceOwnerApp(this.packageName)
}

fun Activity.hideActionBar() = actionBar?.hide()

fun Activity.hideStatusAndNavigation() {
    window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_FULLSCREEN or SYSTEM_UI_FLAG_HIDE_NAVIGATION
    window.decorView.setOnSystemUiVisibilityChangeListener {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or SYSTEM_UI_FLAG_HIDE_NAVIGATION or SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}

/**
 * Need Device owner permission
 */
fun <T> Activity.setLockTask(adminReceiverClass: Class<T>, packages: ArrayList<String>): Boolean {
    val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
    Log.d(localClassName,"isLockTaskModeRunning: ${isLockTaskModeRunning()}, isLockTaskPermitted: ${devicePolicyManager.isLockTaskPermitted(packageName)}")
    packages.add(packageName)
    if (devicePolicyManager.isDeviceOwnerApp(packageName)) {
        devicePolicyManager.setLockTaskPackages(ComponentName(this,adminReceiverClass),packages.toTypedArray())
        if (!isLockTaskModeRunning()) {
            try {
                startLockTask()
            } catch (e: Exception) {
                Toast.makeText(this,"startLockTask fail", Toast.LENGTH_LONG).show()
            }
            return true
        } else {
            Toast.makeText(this,"lock task mode is running",Toast.LENGTH_LONG).show()
        }
    } else {
        Toast.makeText(this,"no device owner permission",Toast.LENGTH_LONG).show()
    }
    return false
}

fun Activity.removeLockTask(){
    val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
    Log.d(localClassName,"isLockTaskModeRunning: ${isLockTaskModeRunning()}")
    if (devicePolicyManager.isDeviceOwnerApp(packageName)) {
        if (isLockTaskModeRunning())
            stopLockTask()
    }
}

fun Activity.removeDeviceOwner() {
    val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
    if (devicePolicyManager.isDeviceOwnerApp(packageName)) {
        try {
            devicePolicyManager.clearDeviceOwnerApp(packageName)
        } catch (e: SecurityException) {
            Log.e(localClassName, "exception: ${e.stackTrace}")
        }
    }
}

fun Activity.isLockTaskModeRunning(): Boolean {
    val activityManager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    var isLockTaskModeRunning = false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        isLockTaskModeRunning = activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_LOCKED
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // Deprecated in API level 23.
        isLockTaskModeRunning = activityManager.isInLockTaskMode
    }
    return isLockTaskModeRunning
}

/**
 * Manifest.xml defined permission "android.permission.REORDER_TASKS"
 */
fun Activity.moveTaskToFront(){
    val activityManager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.moveTaskToFront(taskId, 0)
}

fun Activity.openWifiSetting() {
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
    startActivity(intent)
}

fun AppCompatActivity.openCall(number: String? = null) {
    number?.let {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 0)
        }
    } ?:run {
        val intent = Intent(Intent.ACTION_DIAL)
        startActivity(intent)
    }
}

fun AppCompatActivity.openEmergencyCall() {
        startActivity(Intent().apply {
            action = "com.android.phone.EmergencyDialer.DIAL"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
}



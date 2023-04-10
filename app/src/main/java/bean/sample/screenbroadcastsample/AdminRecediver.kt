package bean.sample.screenbroadcastsample

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.UserManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdminReceiver : DeviceAdminReceiver() {

    companion object {
        val adminEnableLiveData = MutableLiveData<Boolean>()
        val lockTaskEnableLiveData = MutableLiveData<Boolean>()
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d("DeviceAdminSample", "onEnabled")
        Toast.makeText(context, " device owner enable ", Toast.LENGTH_LONG).show()
        adminEnableLiveData.value = true
//        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
//        val adminComponentName = ComponentName(context, AdminReceiver::class.java)
//        devicePolicyManager.addUserRestriction(adminComponentName, UserManager.DISALLOW_OUTGOING_CALLS)
//        devicePolicyManager.setCrossProfileCallerIdDisabled(adminComponentName, "tel:123456789")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.d("DeviceAdminSample", "onDisabled")
        Toast.makeText(context, " device owner disable ", Toast.LENGTH_LONG).show()
        adminEnableLiveData.value = false
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        return super.onDisableRequested(context, intent)
        Toast.makeText(context, " admin disable ", Toast.LENGTH_LONG).show()
        Log.d("DeviceAdminSample", "onDisableRequested")
    }

    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        super.onProfileProvisioningComplete(context, intent)
        Toast.makeText(context, " admin disable ", Toast.LENGTH_LONG).show()
        Log.d("DeviceAdminSample", "onProfileProvisioningComplete")
    }

    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
        super.onLockTaskModeEntering(context, intent, pkg)
        Toast.makeText(context, " LockTaskModeEntering ", Toast.LENGTH_LONG).show()
        lockTaskEnableLiveData.value = true
        Log.d("DeviceAdminSample", "onLockTaskModeEntering")
    }

    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        super.onLockTaskModeExiting(context, intent)
        Toast.makeText(context, "LockTaskModeExiting", Toast.LENGTH_LONG).show()
        lockTaskEnableLiveData.value = false
        Log.d("DeviceAdminSample", "onLockTaskModeExiting")
    }

}
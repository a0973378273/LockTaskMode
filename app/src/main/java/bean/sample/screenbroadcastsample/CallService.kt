package bean.sample.screenbroadcastsample

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log

class CallService : CallScreeningService() {
    private val TAG = javaClass.name
    override fun onScreenCall(callDetails: Call.Details) {
        Log.d(TAG, "onScreenCall: $callDetails")
        when (callDetails.handle.schemeSpecificPart) {
            "1234" -> {
                // 接受撥打
                respondToCall(callDetails, CallResponse.Builder().build())
            }
//            "555-5678" -> {
//                // 靜音
//                respondToCall(callDetails, CallResponse.Builder().setSilenceCall(true).build())
//            }
            else -> {
                respondToCall(callDetails, CallResponse.Builder().build())
                // 拒絕撥打
//                respondToCall(callDetails, CallResponse.Builder().setDisallowCall(true).build())
            }
        }
    }
}
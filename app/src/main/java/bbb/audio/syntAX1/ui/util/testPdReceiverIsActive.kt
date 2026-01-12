package bbb.audio.syntAX1.ui.util

import android.util.Log

fun testPdReceiverIsActive() {
    try {
        Log.i("PdTest", "Testing if PdBase receiver is active...")
        
        Log.i("PdTest", "Sending test float to PdBase...")
        //PdBase.sendFloat("android_step_number", 5f)
        
        Log.i("PdTest", "✓ Test signal sent successfully")
        Log.i("PdTest", "If receiveFloat() is active, you should see a callback in PdReceiverImpl")
        
    } catch (e: Exception) {
        Log.e("PdTest", "Error: ${e.message}", e)
    }
}
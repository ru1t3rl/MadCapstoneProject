package tech.ru1t3rl.madcapstoneproject

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService

class Stopwatch : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        onHandleIntent(intent)
    }

    private fun onHandleIntent(intent: Intent) {

    }

    /*
    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, , 1, intent)
        }
    }
     */
}
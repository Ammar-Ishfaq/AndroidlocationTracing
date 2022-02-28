package com.ammar.locationtrackingservice

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import java.util.*


class BackgroundService : Service() {
    var locationTrack: LocationTrack? = null
    override fun onCreate() {}
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Thread {
            for (i in 0..10) {
                locationTrack = LocationTrack(this)
                if (locationTrack!!.canGetLocation()) {
                    val longitude: Double = locationTrack!!.getLongitude()
                    val latitude: Double = locationTrack!!.getLatitude()
                    showToast(
                        """
     Longitude:$longitude
     Latitude:$latitude
     """.trimIndent()
                    )
                } else {
                    locationTrack!!.showSettingsAlert()
                }
                Thread.sleep(1500)
            }
        }.start()
        showToast("Service started by user.")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun showToast(msg: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                this@BackgroundService.applicationContext,
                msg,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
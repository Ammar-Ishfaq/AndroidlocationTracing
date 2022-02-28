package com.ammar.locationtrackingservice

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    private var permissionsToRequest: String = ""
    private var permissionsRejected = arrayListOf<String>()
    private var permissions = arrayListOf<String>()

    private val ALL_PERMISSIONS_RESULT = 101
    var locationTrack: LocationTrack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION
                ), ALL_PERMISSIONS_RESULT
            );
        }
        val btn: Button = findViewById<View>(R.id.btn) as Button
        val serviceStart: Button = findViewById<View>(R.id.serviceStart) as Button


        btn.setOnClickListener {
            locationTrack = LocationTrack(this@MainActivity)
            if (locationTrack!!.canGetLocation()) {
                val longitude: Double = locationTrack!!.getLongitude()
                val latitude: Double = locationTrack!!.getLatitude()
                Toast.makeText(
                    applicationContext, """
     Longitude:${java.lang.Double.toString(longitude)}
     Latitude:${java.lang.Double.toString(latitude)}
     """.trimIndent(), Toast.LENGTH_SHORT
                ).show()
            } else {
                locationTrack!!.showSettingsAlert()
            }
        }
        serviceStart.setOnClickListener {
            startService(Intent(this@MainActivity, BackgroundService::class.java))

        }


    }

    private fun findUnAskedPermissions(wanted: ArrayList<String>): String {
        val result = ArrayList<String>()
        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }
        return result.toString()
    }

    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }

    private fun canMakeSmores(): Boolean {
        return VERSION.SDK_INT > VERSION_CODES.LOLLIPOP_MR1
    }


    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationTrack!!.stopListener()
    }
}
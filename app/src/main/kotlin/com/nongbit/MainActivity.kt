package com.nongbit

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = updateUI(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tvGithub).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nongbit")))
        }

        handlePermissions()
    }

    private fun handlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        } else {
            BatteryService.start(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Apapun hasilnya (diizinkan atau tidak), service tetap dijalankan (agar widget tetap update)
        BatteryService.start(this)
    }

    private fun updateUI(intent: Intent) {
        val data = BatteryUtils.parseIntent(intent)
        findViewById<TextView>(R.id.battery_level).text = "Level: ${data.level}%"
        findViewById<TextView>(R.id.battery_status).text = "Status: ${data.statusText}"
        findViewById<TextView>(R.id.battery_health).text = "Health: ${data.healthText}"
        findViewById<TextView>(R.id.battery_temperature).text = "Temperature: ${data.temp}°C"
        findViewById<TextView>(R.id.battery_voltage).text = "Voltage: ${data.voltage}V"
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)?.let { updateUI(it) }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryReceiver)
    }
}
package com.nongbit

import android.os.BatteryManager
import android.content.Intent

data class BatteryData(
    val level: Int,
    val statusText: String,
    val healthText: String,
    val temp: Double,
    val voltage: Double,
    val isCharging: Boolean
)

object BatteryUtils {
    fun parseIntent(intent: Intent): BatteryData {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000.0
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)

        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val statusText = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "Fast Charging (AC)"
            BatteryManager.BATTERY_PLUGGED_USB -> "Slow Charging (USB)"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Charging"
            else -> if (isCharging) "Charging" else "On Battery"
        }

        val healthText = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            else -> "Unknown"
        }

        return BatteryData(level, statusText, healthText, temp, voltage, isCharging)
    }
}
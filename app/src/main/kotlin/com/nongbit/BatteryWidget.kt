package com.nongbit

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class BatteryWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, awm: AppWidgetManager, ids: IntArray) {
        BatteryService.start(context)
    }
}
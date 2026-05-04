package com.example.notesappwidget.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isOnline = isNetworkAvailable(context)


        val localIntent = Intent(ACTION_CONNECTIVITY_CHANGE).apply {
            putExtra(IS_ONLINE, isOnline)
        }
        context.sendBroadcast(localIntent)
    }

    companion object {
        const val ACTION_CONNECTIVITY_CHANGE = "com.example.notesappwidget.CONNECTIVITY_CHANGE"
        const val IS_ONLINE = "IS_ONLINE"

        fun isNetworkAvailable(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
    }
}
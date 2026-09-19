package com.example.data.engine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.BatteryManager
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BatterySyncState(
    val batteryLevel: Int = 100,
    val isCharging: Boolean = false,
    val isPowerSaveMode: Boolean = false,
    val isNetworkAvailable: Boolean = false,
    val isMeteredConnection: Boolean = false,
    val isWifiConnected: Boolean = false,
    val wifiSimulationActive: Boolean = false,
    val syncThrottleActive: Boolean = false
) {
    val isWifiRequirementMet: Boolean
        get() = isWifiConnected || wifiSimulationActive

    val isWifiSimulated: Boolean
        get() = wifiSimulationActive
}

class BatteryEfficiencyManager(private val context: Context) {

    private val _state = MutableStateFlow(BatterySyncState())
    val state: StateFlow<BatterySyncState> = _state.asStateFlow()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    private val powerManager =
        context.getSystemService(Context.POWER_SERVICE) as? PowerManager

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { updateBatteryInfo(it) }
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            checkNetworkStatus()
        }

        override fun onLost(network: Network) {
            checkNetworkStatus()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            checkNetworkStatus()
        }
    }

    init {
        // Initial battery check
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val initialBatteryIntent = context.registerReceiver(batteryReceiver, intentFilter)
        initialBatteryIntent?.let { updateBatteryInfo(it) }

        // Network monitoring
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        try {
            connectivityManager?.registerNetworkCallback(request, networkCallback)
        } catch (_: Exception) {
            // Ignore if permission or restricted
        }
        checkNetworkStatus()
    }

    private fun updateBatteryInfo(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 100

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val isPowerSave = powerManager?.isPowerSaveMode ?: false
        val shouldThrottle = !isCharging && (batteryPct < 20 || isPowerSave)

        _state.value = _state.value.copy(
            batteryLevel = batteryPct,
            isCharging = isCharging,
            isPowerSaveMode = isPowerSave,
            syncThrottleActive = shouldThrottle
        )
    }

    private fun checkNetworkStatus() {
        val activeNetwork = connectivityManager?.activeNetwork
        val caps = connectivityManager?.getNetworkCapabilities(activeNetwork)
        val isConnected = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val isMetered = connectivityManager?.isActiveNetworkMetered == true

        _state.value = _state.value.copy(
            isNetworkAvailable = isConnected,
            isMeteredConnection = isMetered,
            isWifiConnected = isWifi
        )
    }

    fun toggleWifiSimulation(enabled: Boolean) {
        _state.value = _state.value.copy(wifiSimulationActive = enabled)
    }

    fun isSafeForIntensiveWork(): Boolean {
        val s = _state.value
        // If battery is extremely low (<10%) and not charging, advise user
        return s.isCharging || s.batteryLevel >= 15
    }
}

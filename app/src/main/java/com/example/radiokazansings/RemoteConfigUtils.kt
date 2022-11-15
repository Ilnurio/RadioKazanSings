package com.example.radiokazansings

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

private const val TAG = "RemoteConfigUtils"
private const val CHANGE_URL_64 = "change_url_64"
private const val CHANGE_URL_128 = "change_url_128"
private const val CHANGE_URL_192 = "change_url_192"
private const val CHANGE_URL_320 = "change_url_320"
class RemoteConfigUtils(private val application: KazanRadioApplication) {


    private val DEFAULTS: HashMap<String, Any> =
        hashMapOf(
            CHANGE_URL_64 to "https://stream01.hitv.ru:8443/kazansings-64kb",
            CHANGE_URL_128 to "https://stream01.hitv.ru:8443/kazansings-128kb",
            CHANGE_URL_192 to "https://stream01.hitv.ru:8443/kazansings-192kb",
            CHANGE_URL_320 to "https://stream01.hitv.ru:8443/kazansings-320kb"
        )
    @SuppressLint("StaticFieldLeak")
    private lateinit var remoteConfig: FirebaseRemoteConfig

    fun init() {
        remoteConfig = getFirebaseRemoteConfig()
    }

    private fun getFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG){
                0 //kept 0 for quick debug purpose
            } else {
                60 * 60 // based on Requirement
            }
        }
        remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(DEFAULTS)
            fetchAndActivate().addOnCompleteListener {
                Log.d(TAG, "Remote Config Fetch Complete")
                application.getPlayer().onPlayerConfigUpdated(
                    PlayerConfig(
                        remoteConfig.getString(CHANGE_URL_64),
                        remoteConfig.getString(CHANGE_URL_128),
                        remoteConfig.getString(CHANGE_URL_192),
                        remoteConfig.getString(CHANGE_URL_320),
                    )
                )
            }
        }
        return remoteConfig
    }
    fun getChangeUrlText(): String = remoteConfig.getString(CHANGE_URL_64)

}
package com.asemlab.quakes.base

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.asemlab.quakes.BuildConfig
import com.asemlab.quakes.R
import com.asemlab.quakes.remote.FirebaseDB
import com.blankj.utilcode.util.LogUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            val myIntent =
                Intent(
                    Settings.ACTION_APP_NOTIFICATION_SETTINGS
                ).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)

            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.dialog_permission_title))
                setMessage(getString(R.string.dialog_permission_text))
                setPositiveButton(getString(R.string.dialog_permission_button)) { dialog, _ ->
                    try {
                        startActivity(myIntent)
                        dialog.dismiss()
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS))
                        dialog.dismiss()
                    }
                }
                setCancelable(true)
            }.show()
        }
        FirebaseDB.getVersion()
        getFCMToken()

    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                LogUtils.e("Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            if (BuildConfig.DEBUG)
                LogUtils.d("FCM Token", task.result)
        })
    }
}
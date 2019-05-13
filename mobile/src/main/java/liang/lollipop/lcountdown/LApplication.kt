package liang.lollipop.lcountdown

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import liang.lollipop.lcountdown.activity.CrashActivity
import liang.lollipop.lcountdown.utils.LogHelper
import java.io.File


/**
 * @date: 2019-05-13 21:18
 * @author: lollipop
 * 应用上下文，用于初始化一些东西
 */
class LApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        LogHelper.init(this, logDir) {logFile, context ->
            val intent = Intent(context, CrashActivity::class.java)
            intent.putExtra(CrashActivity.ARG_LOG_PATH, logFile?.absolutePath?:"")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val restartIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

            val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent)
        }
    }

    val rootDir: File
        get() {
            return filesDir
        }

    val logDir: File
        get() {
            return getDir("log")
        }

    private fun getDir(dir:String): File {
        return File(rootDir, dir)
    }

}
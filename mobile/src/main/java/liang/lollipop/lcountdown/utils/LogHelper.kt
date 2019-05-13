package liang.lollipop.lcountdown.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Looper
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * @date: 2019-05-13 22:11
 * @author: lollipop
 * log 辅助工具
 */
object LogHelper {

    fun init(context: Context, logDir:File, crashListener: ((File?, Context) -> Unit)? = null) {
        CrashHandler(context, logDir, crashListener).init()
    }

    private class CrashHandler(private val context: Context,
                               private val logDir:File = Environment.getExternalStorageDirectory(),
                               private val crashListener: ((File?, Context) -> Unit)? = null) : Thread.UncaughtExceptionHandler {

        //系统默认的UncaughtException处理类
        private val defaultHandler: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        //用来存储设备信息和异常信息
        private val infos = HashMap<String, String>()
        //用于格式化日期,作为日志文件名的一部分
        private val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINESE)

        fun init() {
            //设置该CrashHandler为程序的默认处理器
            Thread.setDefaultUncaughtExceptionHandler(this)
        }

        override fun uncaughtException(thread: Thread?, ex: Throwable?) {
            if(ex==null){
                defaultHandler.uncaughtException(thread,ex)
                return
            }
            if (!handleException(ex)) {
                //如果用户没有处理则让系统默认的异常处理器来处理
                defaultHandler.uncaughtException(thread, ex)
            } else {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }

        /**
         * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
         *
         * @param ex
         * @return true:如果处理了该异常信息;否则返回false.
         */
        private fun handleException(ex: Throwable?): Boolean {
            if (ex == null) {
                return false
            }
            //保存日志文件
            val file = saveCrashInfo2File(context, ex)
            object : Thread() {
                override fun run() {
                    Looper.prepare()
                    crashListener?.let { it(file, context) }
                    android.os.Process.killProcess(android.os.Process.myPid())
                    Looper.loop()
                }
            }.start()
            return true
        }

        /**
         * 保存错误信息到文件中
         *
         * @param ex
         * @return 返回文件名称, 便于将文件传送到服务器
         */
        private fun saveCrashInfo2File(context: Context?, ex: Throwable): File? {

            val stringBuffer = StringBuffer()
            if (context != null) {
                val deviceInfo = collectDeviceInfo(context)
                for ((key, value) in deviceInfo) {
                    stringBuffer.append("$key=$value\n")
                }
            }

            val writer = StringWriter()
            val printWriter = PrintWriter(writer)
            ex.printStackTrace(printWriter)
            var cause: Throwable? = ex.cause
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }
            printWriter.close()
            val result = writer.toString()
            stringBuffer.append(result)
            return outputTextFile(stringBuffer.toString())
        }

        /**
         * 收集设备参数信息
         *
         * @param ctx
         */
        private fun collectDeviceInfo(ctx: Context): HashMap<String, String> {
            val deviceInfo = infos
            deviceInfo.clear()
            try {
                val pm = ctx.packageManager
                val pi = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
                if (pi != null) {
                    val versionName = pi.versionName ?: "null"
                    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        pi.longVersionCode.toString() + ""
                    } else {
                        pi.versionCode.toString() + ""
                    }
                    deviceInfo["versionName"] = versionName
                    deviceInfo["versionCode"] = versionCode
                }
            } catch (e: PackageManager.NameNotFoundException) {
            }

            val fields = Build::class.java.declaredFields
            for (field in fields) {
                try {
                    field.isAccessible = true
                    deviceInfo[field.name] = field.get(null).toString()
                } catch (e: Exception) {
                }

            }
            return deviceInfo
        }

        private fun outputTextFile(value: String): File? {
            return outputTextFile(value, formatter.format(Date()))
        }

        private fun outputTextFile(value: String, name: String): File? {
            return try {
                val file = File(logDir, "$name.txt")
                val path = logDir
                if (!path.exists()) {
                    path.mkdirs()
                }
                val outStream = FileOutputStream(file, true)
                val writer = OutputStreamWriter(outStream, "gbk")
                writer.write(value)
                writer.flush()
                writer.close()//记得关闭
                file
            } catch (e: Exception) {
                null
            }
        }

    }

    fun printLog(file: File, maxLength: Long = 200): String {
        if (!file.exists() || !file.canRead() || file.isDirectory) {
            return ""
        }
        // 建立一个输入流对象reader
        val reader = InputStreamReader(FileInputStream(file))
        // 建立一个对象，它把文件内容转成计算机能读懂的语言
        val br = BufferedReader(reader)
        var line: String?
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n")
        line = br.readLine()
        var lineIndex = 1
        while (line != null && lineIndex < maxLength) {
            stringBuilder.append(line)
            stringBuilder.append("\n")
            line = br.readLine() // 一次读入一行数据
            lineIndex ++
        }
        return stringBuilder.toString()
    }

    fun findLastLog(logDir: File): File? {
        val log = logDir
        if (log.exists() && log.isDirectory) {
            val children = log.listFiles()
            var lastTime = Long.MAX_VALUE
            var lastLog: File? = null
            for (child in children) {
                val time = child.lastModified()
                if (time < lastTime) {
                    lastTime = time
                    lastLog = child
                }
            }
            if (lastLog != null) {
                return lastLog
            }
        }
        return null
    }

    fun clearLog(logDir: File) {
        if (logDir.exists() && logDir.canWrite()) {
            deleteDir(logDir)
        }
    }

    private fun deleteDir(dir: File) {
        if (dir.isDirectory) {
            val children = dir.list()
            //递归删除目录中的子目录下
            for (child in children) {
                deleteDir(File(dir, child))
            }
        }
        // 目录此时为空，可以删除
        if (dir.canWrite()) {
            dir.delete()
        }
    }

}
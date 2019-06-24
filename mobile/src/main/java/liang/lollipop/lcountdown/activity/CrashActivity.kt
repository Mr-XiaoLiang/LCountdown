package liang.lollipop.lcountdown.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_crash.*
import liang.lollipop.lbaselib.base.BaseActivity
import liang.lollipop.lcountdown.LApplication
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.utils.LogHelper
import liang.lollipop.lcountdown.utils.MailUtil
import java.io.File


/**
 * @author Lollipop
 * 发生异常时，跳转到的 Activity
 */
class CrashActivity : BaseActivity() {

    companion object {
        const val ARG_LOG_PATH = "ARG_LOG_PATH"
    }

    private var logFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)
        initView()
        initData(intent.getStringExtra(ARG_LOG_PATH)?:"")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent?.getStringExtra(ARG_LOG_PATH)?:"")
    }

    private fun initView() {
        setToolbar(toolbar)
        errorView.movementMethod = ScrollingMovementMethod.getInstance()
        sendMailBtn.setOnClickListener {
            MailUtil.to(getString(R.string.lollipop_email)) {
                subject = getString(R.string.log_mail_title)
                content = getString(R.string.log_mail_centent)
                chooserTitle = getString(R.string.mail_chooser_title)
                noResolveTitle = getString(R.string.no_resolve_title)
                noResolveMessage = getString(R.string.no_resolve_message)
                if (logFile != null) {
                    content += LogHelper.printLog(logFile!!)
                }
                file = logFile
            }.send(this)
        }
        copyAddress.setOnClickListener {
            //获取剪贴板管理器：
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 创建普通字符型ClipData
            val clipData = ClipData.newPlainText("email", getString(R.string.lollipop_email))
            // 将ClipData内容放到系统剪贴板里。
            cm.primaryClip = clipData
            Snackbar.make(mailAddress,R.string.copy_completed,Snackbar.LENGTH_LONG).show()
        }
    }

    private fun initData(filePath: String) {
        val errorInfo = StringBuilder()
        logFile = if (filePath.isEmpty()) {
            errorInfo.append(getString(R.string.log_not_found))
            errorInfo.append("\n")
            val logDir = (application as LApplication).logDir
            LogHelper.findLastLog(logDir)
        } else {
            File(filePath)
        }
        if (logFile == null || !logFile!!.exists()) {
            errorInfo.append(filePath)
            errorInfo.append("\n")
            errorInfo.append(getString(R.string.no_log))
        } else {
            errorInfo.append(LogHelper.printLog(logFile!!))
        }
        errorView.text = errorInfo.toString()
    }

}

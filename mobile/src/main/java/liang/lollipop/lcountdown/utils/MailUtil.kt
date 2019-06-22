package liang.lollipop.lcountdown.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import java.io.File




/**
 * 发送电子邮件的辅助类
 * @author Lollipop
 */
class MailUtil private constructor(private val recipient: Array<String>) {

    companion object {
        fun to(vararg recipient: String, run: MailUtil.() -> Unit): MailUtil {
            val address = Array(recipient.size){ recipient[it] }
            return MailUtil(address).apply(run)
        }
    }

    /**
     * 标题
     */
    var subject = ""
    /**
     * 内容
     */
    var content = ""
    /**
     * 文件
     */
    var file: File? = null
    /**
     * 选择框标题
     */
    var chooserTitle = ""
    /**
     * 抄送地址
     */
    var cc: Array<String>? = null
    /**
     * 密送地址
     */
    var bcc: Array<String>? = null

    /**
     * 找不到邮箱应用时，提示框的 title
     */
    var noResolveTitle = ""

    /**
     * 没有邮箱应用时，提示框的消息内容
     */
    var noResolveMessage = ""

    /**
     * 发送
     */
    fun send(context: Context, noResolve: (() -> Boolean)? = null) {
        if (recipient.isEmpty()) {
            throw RuntimeException("recipient is empty")
        }
        val email = if (recipient.size > 1 || file != null) {
            Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc882"
            }
        } else {
            val uri = Uri.parse("mailto:${recipient[0]}")
            Intent(Intent.ACTION_SENDTO, uri)
        }
        // 附件
        if (file != null) {
            log("File: " + file?.absolutePath)
            //邮件发送类型：带附件的邮件
//            email.type = "application/octet-stream"
            val photoUri = FileProvider.getUriForFile(context,
                    "${context.packageName}.provider", file!!)
            email.putExtra(Intent.EXTRA_STREAM, photoUri)
        }
        // 设置邮件地址
        email.putExtra(Intent.EXTRA_EMAIL, recipient)
        // 抄送
        if (cc != null) {
            email.putExtra(Intent.EXTRA_CC, cc)
        }
        // 密送
        if (bcc != null) {
            email.putExtra(Intent.EXTRA_BCC, bcc)
        }
        // 设置邮件标题
        email.putExtra(Intent.EXTRA_SUBJECT, subject)
        // 设置发送的内容
        email.putExtra(Intent.EXTRA_TEXT, content)
        // 检查是否有可用的邮箱应用
        val resolveActivity = email.resolveActivity(context.packageManager)
        if (resolveActivity == null ) {
            val notShowDialog = noResolve?.let { it() } ?: false
            if (!notShowDialog) {
                alertDialog(context)
            }
            return
        }
        // 调用系统的邮件系统
        context.startActivity(Intent.createChooser(email, chooserTitle))
    }

    private fun alertDialog(context: Context) {
        if (noResolveTitle.isEmpty() || noResolveMessage.isEmpty()) {
            return
        }
        AlertDialog.Builder(context)
                .setTitle(noResolveTitle)
                .setMessage(noResolveMessage)
                .show()
    }

    private fun log(value: String) {
        Log.d("Lollipop", "MailUtil: $value")
    }

}
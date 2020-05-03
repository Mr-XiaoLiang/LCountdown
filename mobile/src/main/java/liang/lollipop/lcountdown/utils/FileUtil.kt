package liang.lollipop.lcountdown.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest


/**
 * @author lollipop
 * @date 2020/5/3 17:17
 * 文件工具类
 */
object FileUtil {

    private const val WIDGET_IMAGE = "widget"
    private const val TIMER_IMAGE = "timer"
    private const val MAX_LENGTH = 720

    private fun getDir(context: Context, dir: String): File {
        return File(context.filesDir, dir)
    }

    private fun copyFile(context: Context, src: Uri, target: File): Boolean {
        var outputStream: FileOutputStream? = null
        var inputStream: InputStream? = null
        var bmp: Bitmap? = null
        try {
            inputStream = context.contentResolver.openInputStream(src)
            val input = inputStream ?: return false
            bmp = BitmapFactory.decodeStream(input) ?: return false
            val width: Int
            val height: Int
            if (bmp.width < bmp.height) {
                height = MAX_LENGTH
                width = (bmp.width * 1F / bmp.height * MAX_LENGTH).toInt()
            } else {
                width = MAX_LENGTH
                height = (bmp.height * 1F / bmp.width * MAX_LENGTH).toInt()
            }
            val new = Bitmap.createScaledBitmap(bmp, width, height, true)
            bmp.recycle()
            bmp = new
            outputStream = FileOutputStream(target)
            bmp.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
            outputStream.flush()
            return true
        } catch (e: Throwable) {
            return false
        } finally {
            inputStream?.tryClose()
            outputStream?.tryClose()
            try {
                bmp?.recycle()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun Closeable.tryClose() {
        try {
            this.close()
        } catch (e: Throwable) {
        }
    }

    private fun getNameById(id: Int): String {
        return id.toString(Character.MAX_RADIX) + ".png"
    }

    fun copyTimer(context: Context, src: Uri, id: Int): Boolean {
        return copyImage(context, src, TIMER_IMAGE, id)
    }

    fun copyWidget(context: Context, src: Uri, id: Int): Boolean {
        return copyImage(context, src, WIDGET_IMAGE, id)
    }

    private fun getImage(context: Context, dir: String, id: Int): File {
        return File(getDir(context, dir), getNameById(id))
    }

    fun getTimerImage(context: Context, id: Int): File {
        return File(getDir(context, TIMER_IMAGE), getNameById(id))
    }

    fun getWidgetImage(context: Context, id: Int): File {
        return File(getDir(context, WIDGET_IMAGE), getNameById(id))
    }

    private fun copyImage(context: Context, src: Uri, dirName: String, id: Int): Boolean {
        val dir = getDir(context, dirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val targetFile = File(dir, getNameById(id))
        if (targetFile.exists()) {
            targetFile.delete()
        }
        return copyFile(context, src, targetFile)
    }

    private fun loadTo(dir: String, imageView: ImageView, id: Int, blur: Boolean) {
        val image = getImage(imageView.context, dir, id)
        if (!image.exists()) {
            imageView.setImageDrawable(null)
            return
        }
        Glide.with(imageView).load(image)
                .signature(FileUpdateKey(image.lastModified())).apply {
                    if (blur) {
                        apply(RequestOptions
                                .bitmapTransform(GlideBlurTransformation(imageView.context)))
                    }
                }
                .transition(DrawableTransitionOptions
                        .withCrossFade(DrawableCrossFadeFactory.Builder()
                                .setCrossFadeEnabled(true)
                                .build())
                ).into(imageView)
    }

    fun loadWidgetImage(imageView: ImageView, id: Int, blur: Boolean = false) {
        loadTo(WIDGET_IMAGE, imageView, id, blur)
    }

    fun loadTimerImage(imageView: ImageView, id: Int, blur: Boolean = false) {
        loadTo(TIMER_IMAGE, imageView, id, blur)
    }

    private fun removeImage(context: Context, dir: String, id: Int) {
        getImage(context, dir, id).let {
            if (it.exists()) {
                it.delete()
            }
        }
    }

    fun removeTimerImage(context: Context, id: Int) {
        doAsync {
            removeImage(context, TIMER_IMAGE, id)
        }
    }

    fun removeWidgetImage(context: Context, id: Int) {
        doAsync {
            removeImage(context, WIDGET_IMAGE, id)
        }
    }

    private class FileUpdateKey(private val updateTime: Long) : Key {
        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            messageDigest.update(updateTime.toString(Character.MAX_RADIX).toByteArray(Key.CHARSET))
        }
    }

}
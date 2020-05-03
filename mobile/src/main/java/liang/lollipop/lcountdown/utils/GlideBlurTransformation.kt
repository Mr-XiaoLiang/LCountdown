package liang.lollipop.lcountdown.utils

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import java.security.MessageDigest

/**
 * @author lollipop
 * @date 2020/2/26 23:27
 */
class GlideBlurTransformation(private val context: Context) : CenterCrop() {

    companion object {
        private val ID = GlideBlurTransformation::class.java.name
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun transform(pool: BitmapPool,
                           toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val inBitmap = super.transform(pool, toTransform, outWidth, outHeight)
        val result = pool.get(outWidth, outHeight,
            toTransform.config ?: Bitmap.Config.ARGB_8888)
        BlurUtil.blurBitmap(context, inBitmap, result)
        return result
    }

}
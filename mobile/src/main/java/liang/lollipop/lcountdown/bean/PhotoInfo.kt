package liang.lollipop.lcountdown.bean

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import liang.lollipop.lcountdown.base.BaseBean

/**
 * @author lollipop
 * @date 2020-02-12 21:49
 * 图片信息
 */
class PhotoInfo(val path: Uri, val size: Int, val name: String): BaseBean() {

    companion object {
        val Empty = PhotoInfo(Uri.EMPTY, 0, "")
    }

    fun loadTo(view: ImageView) {
        if (path == Uri.EMPTY) {
            view.setImageDrawable(null)
            return
        }
        Glide.with(view).load(path).into(view)
    }
}
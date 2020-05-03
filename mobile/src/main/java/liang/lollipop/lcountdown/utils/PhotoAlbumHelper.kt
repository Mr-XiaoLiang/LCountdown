package liang.lollipop.lcountdown.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import liang.lollipop.lcountdown.bean.PhotoInfo

/**
 * @author lollipop
 * @date 2020-02-12 21:28
 * 相册辅助器
 */
class PhotoAlbumHelper {

    companion object {
        private const val MIN_INTERVAL = 1000L * 10
    }

    val data = ArrayList<PhotoInfo>()

    val selected = ArrayList<PhotoInfo>()

    private var onCompleteCallback: ((PhotoAlbumHelper) -> Unit)? = null
    private var onErrorCallback: ((Throwable) -> Unit)? = null

    private var lastInit = 0L

    val isEmpty: Boolean
        get() {
            return data.isEmpty()
        }

    val selectedSize: Int
        get() {
            return selected.size
        }

    fun get(position: Int) : PhotoInfo {
        return data[position]
    }

    fun selectedIndex(info: PhotoInfo) : Int {
        return selected.indexOf(info)
    }

    fun selectedIndexByPosition(position: Int) : Int {
        return selectedIndex(get(position))
    }

    fun positionBySelectedIndex(index: Int) : Int {
        return data.indexOf(selected[index])
    }

    fun isChecked(position: Int): Int {
        if (isEmpty) {
            return 0
        }
        if (position < 0 || position >= data.size) {
            return 0
        }
        return selectedIndexByPosition(position)
    }

    fun onComplete(callback: ((PhotoAlbumHelper) -> Unit)?): PhotoAlbumHelper {
        onCompleteCallback = callback
        return this
    }

    fun onError(callback: ((Throwable) -> Unit)?): PhotoAlbumHelper {
        onErrorCallback = callback
        return this
    }

    fun initData(context: Context): PhotoAlbumHelper {
        if (System.currentTimeMillis() - lastInit < MIN_INTERVAL) {
            return this
        }
        doAsync({
            data.clear()
            onErrorCallback?.invoke(it)
        }) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selection = "${MediaStore.Images.Media.MIME_TYPE} = ? or " +
                    " ${MediaStore.Images.Media.MIME_TYPE} = ?"
            val selectionArgs = arrayOf("image/jpeg", "image/png")
            val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} desc"
            val columns = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME)
            val cursor = context.contentResolver.query(uri,
                columns, selection, selectionArgs, sortOrder)
            data.clear()
            while (cursor?.moveToNext() == true) {
                val id = cursor.getLongByColumn(MediaStore.Images.Media._ID)
                val size = cursor.getIntByColumn(MediaStore.Images.Media.SIZE)
                val name = cursor.getStringByColumn(MediaStore.Images.Media.DISPLAY_NAME)
                data.add(PhotoInfo(ContentUris.withAppendedId(uri, id), size, name))
            }
            cursor?.close()
            lastInit = System.currentTimeMillis()
            onUI {
                onCompleteCallback?.invoke(this@PhotoAlbumHelper)
            }
        }
        return this
    }

    private fun Cursor.getStringByColumn(col: String): String {
        return getString(getColumnIndex(col))
    }

    private fun Cursor.getIntByColumn(col: String): Int {
        return getInt(getColumnIndex(col))
    }

    private fun Cursor.getLongByColumn(col: String): Long {
        return getLong(getColumnIndex(col))
    }

}
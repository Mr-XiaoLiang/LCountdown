package liang.lollipop.lcountdown.utils

import android.content.Context
import android.text.TextUtils

/**
 * App的设置
 * @author Lollipop
 */
object AppSettings {

    private const val KEY_END_TIME = "KEY_END_TIME_"
    private const val KEY_NAME = "KEY_NAME_"

    private const val KEY_IDS = "KEY_IDS"

    private const val SPACER = ","

    fun getEndTime(context: Context, id: Int): Long = SharedPreferencesUtils[context, "$KEY_END_TIME$id", System.currentTimeMillis()]
            ?: System.currentTimeMillis()

    fun setEndTime(context: Context, id: Int, value: Long) = SharedPreferencesUtils.put(context, "$KEY_END_TIME$id", value)

    fun getName(context: Context, id: Int): String = SharedPreferencesUtils[context, "$KEY_NAME$id", ""]
            ?: ""

    fun setName(context: Context, id: Int, value: String) = SharedPreferencesUtils.put(context, "$KEY_NAME$id", value)

    private fun copyName(context: Context, newId: Int, oldId: Int) {
        setName(context, newId, getName(context, oldId))
    }

    private fun copyEndTime(context: Context, newId: Int, oldId: Int) {
        setEndTime(context, newId, getEndTime(context, oldId))
    }

    fun copyData(context: Context, newId: Int, oldId: Int) {
        copyName(context, newId, oldId)
        copyEndTime(context, newId, oldId)
    }

    fun getIds(context: Context): IntArray {

        val ids = SharedPreferencesUtils[context, KEY_IDS, ""] ?: ""

        if (TextUtils.isEmpty(ids)) {
            return IntArray(0)
        }

        val idArray = ids.split(SPACER).filterNot { TextUtils.isEmpty(it) }
        return IntArray(idArray.size) { idArray[it].toInt() }

    }

    fun setIds(context: Context, idArray: IntArray) {

        val stringBuilder = StringBuilder()

        for (id in idArray) {

            stringBuilder.append(id)
            stringBuilder.append(SPACER)

        }

        if (stringBuilder.isNotEmpty()) {
            stringBuilder.delete(stringBuilder.length - 1, stringBuilder.length)
        }

        SharedPreferencesUtils.put(context, KEY_IDS, stringBuilder.toString())

    }

}
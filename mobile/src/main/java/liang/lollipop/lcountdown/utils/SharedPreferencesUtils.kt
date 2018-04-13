package liang.lollipop.lcountdown.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by lollipop on 2017/12/10.
 * @author Lollipop
 * 持久化储存的工具类
 */
object SharedPreferencesUtils {

    private val USER = "AWord"

    fun <T> put(context: Context?, key: String, value: T) {
        if (context == null)
            return
        val mShareConfig = context.getSharedPreferences(USER, Context.MODE_PRIVATE)
        put(mShareConfig, key, value)
    }

    fun <T> put(mShareConfig: SharedPreferences?, key: String, value: T) {

        if (mShareConfig == null)
            return
        if (notNull(value)) {
            val conEdit = mShareConfig.edit()
            when (value) {
                is String -> conEdit.putString(key, (value as String).trim { it <= ' ' })
                is Long -> conEdit.putLong(key, value as Long)
                is Boolean -> conEdit.putBoolean(key, value as Boolean)
                is Int -> conEdit.putInt(key, value as Int)
                is Float -> conEdit.putFloat(key, value as Float)
            }
            conEdit.commit()
        }
    }

    operator fun <T> get(context: Context?, key: String, defValue: T): T? {
        if (context == null)
            return null
        val mShareConfig = context.getSharedPreferences(USER, Context.MODE_PRIVATE)
        return get(mShareConfig, key, defValue)
    }

    operator fun <T> get(mShareConfig: SharedPreferences?, key: String, defValue: T): T? {
        var value: T? = null
        if (notNull(key)) {
            if (null != mShareConfig) {
                when (defValue) {
                    is String -> value = mShareConfig.getString(key, defValue as String) as T
                    is Long -> value = java.lang.Long.valueOf(mShareConfig.getLong(key, defValue as Long)) as T
                    is Boolean -> value = java.lang.Boolean.valueOf(mShareConfig.getBoolean(key, defValue as Boolean)) as T
                    is Int -> value = Integer.valueOf(mShareConfig.getInt(key, defValue as Int)) as T
                    is Float -> value = java.lang.Float.valueOf(mShareConfig.getFloat(key, defValue as Float)) as T
                }
            }
        }
        return value
    }

    /**
     * object not null
     */
    fun notNull(obj: Any?): Boolean {
        return null != obj
    }

}
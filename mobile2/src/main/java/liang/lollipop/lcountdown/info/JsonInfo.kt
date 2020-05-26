package liang.lollipop.lcountdown.info

import org.json.JSONArray
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2020/5/26 23:44
 */
open class JsonInfo private constructor(private var infoObject: JSONObject = JSONObject()){

    constructor(): this(JSONObject())

    companion object {
        private fun createWith(info: JsonInfo): JsonInfo {
            return JsonInfo(copyOut(info))
        }

        private fun copyOut(info: JsonInfo): JSONObject {
            return copyObj(info.infoObject)
        }

        private fun copyObj(obj: JSONObject): JSONObject {
            val newObj = JSONObject()
            obj.keys().forEach {
                if (it != null) {
                    newObj.put(it, newObj.opt(it)?.copyValue())
                }
            }
            return newObj
        }

        private fun copyArray(array: JSONArray): JSONArray {
            val newArray = JSONArray()
            for (index in 0 until array.length()) {
                newArray.put(array.opt(index)?.copyValue())
            }
            return newArray
        }

        private fun Any.copyValue(): Any {
            return when (this) {
                is JSONObject -> {
                    copyObj(this)
                }
                is JSONArray -> {
                    copyArray(this)
                }
                is JsonInfo -> {
                    createWith(this)
                }
                else -> {
                    this
                }
            }
        }
    }

    fun copy(info: JsonInfo) {
        this.infoObject = copyOut(info)
    }

    fun <T> invoke(c: Class<T>): T {
        return c.newInstance()
    }

    inline fun <reified S: JsonInfo, reified T: JsonInfo> S.convertTo(): T {
        if (this is T) {
            return this
        }
        val newObj = T::class.java.newInstance()
        newObj.copy(this)
        return newObj
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T: Any> get(key: String, def: T): T {
        try {
            val result = when (def) {
                is String -> {
                    infoObject.optString(key)
                }
                is Boolean -> {
                    infoObject.optBoolean(key)
                }
                is Int -> {
                    infoObject.optInt(key)
                }
                is Long -> {
                    infoObject.optLong(key)
                }
                is Float -> {
                    infoObject.optDouble(key).let {
                        if (!it.isNaN()) {
                            it.toFloat()
                        } else {
                            def
                        }
                    }
                }
                is Double -> {
                    infoObject.optDouble(key).let {
                        if (it.isNaN()) {
                            def
                        } else {
                            it
                        }
                    }
                }
                is JSONObject -> {
                    infoObject.optJSONObject(key)
                }
                is JSONArray -> {
                    infoObject.optJSONArray(key)
                }
                else -> {
                    infoObject.opt(key)
                }
            } ?: def
            return result as T
        } catch (e: Throwable) {
            return def
        }
    }

    protected fun set(key: String, value: Any) {
        infoObject.put(key, value)
    }
}
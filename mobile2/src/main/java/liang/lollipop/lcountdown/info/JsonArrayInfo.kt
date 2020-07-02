package liang.lollipop.lcountdown.info

import android.util.SparseArray
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2020/5/26 23:44
 * 基础的信息类
 */
open class JsonArrayInfo (val infoArray: JSONArray = JSONArray()) {

    companion object {

        private val TYPE_INFO = JSONObject()
        private val TYPE_ARRAY = JSONArray()

        private fun createWith(info: JsonArrayInfo): JsonArrayInfo {
            return JsonArrayInfo(copyOut(JSONArray(), info))
        }

        private fun copyOut(newObj: JSONArray, info: JsonArrayInfo): JSONArray {
            return copyArray(newObj, info.infoArray)
        }

        private fun copyObj(newObj: JSONObject, obj: JSONObject): JSONObject {
            obj.keys().forEach {
                if (it != null) {
                    newObj.put(it, obj.opt(it)?.copyValue())
                }
            }
            return newObj
        }

        private fun copyArray(newArray: JSONArray, array: JSONArray): JSONArray {
            for (index in 0 until array.length()) {
                newArray.put(array.opt(index)?.copyValue())
            }
            return newArray
        }

        private fun Any.copyValue(): Any {
            return when (this) {
                is JSONObject -> {
                    copyObj(JSONObject(), this)
                }
                is JSONArray -> {
                    copyArray(JSONArray(), this)
                }
                is JsonArrayInfo -> {
                    createWith(this)
                }
                else -> {
                    this
                }
            }
        }

        private val INFO_FACTORY = { JSONObject() }

        private val ARRAY_FACTORY = { JSONArray() }

    }

    private val cache = SparseArray<Any>()

    fun copy(info: JsonArrayInfo) {
        cache.clear()
        for (index in 0 until info.infoArray.length()) {
            this.infoArray.remove(index)
        }
        copyOut(this.infoArray, info)
    }

    fun <T> invoke(c: Class<T>): T {
        return c.newInstance()
    }

    operator fun <T: Any> get(key: Int, def: T): T {
        return get(key, def, null)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: Any> get(key: Int, type: T, def: (() -> T)?): T {
        try {
            val result = when (type) {
                is String -> {
                    infoArray.optString(key)
                }
                is Boolean -> {
                    infoArray.optBoolean(key)
                }
                is Int -> {
                    infoArray.optInt(key)
                }
                is Long -> {
                    infoArray.optLong(key)
                }
                is Float -> {
                    infoArray.optDouble(key).let {
                        if (!it.isNaN()) {
                            it.toFloat()
                        } else {
                            null
                        }
                    }
                }
                is Double -> {
                    infoArray.optDouble(key).let {
                        if (it.isNaN()) {
                            null
                        } else {
                            it
                        }
                    }
                }
                is JSONObject -> {
                    infoArray.optJSONObject(key)
                }
                is JSONArray -> {
                    infoArray.optJSONArray(key)
                }
                is JsonArrayInfo -> {
                    JsonArrayInfo(infoArray.optJSONArray(key)?: JSONArray())
                }
                is JsonInfo -> {
                    JsonInfo(infoArray.optJSONObject(key)?: JSONObject())
                }
                else -> {
                    infoArray.opt(key)
                }
            } ?: def?.invoke() ?: type
            return result as T
        } catch (e: Throwable) {
            return def?.invoke() ?: type
        }
    }

    fun optInfo(key: Int): JsonInfo {
        val cacheValue = cache[key]
        if (cacheValue is JsonInfo) {
            return cacheValue
        }
        // 尝试获取信息
        val resultObj = get(key, TYPE_INFO, INFO_FACTORY)
        // 二次绑定对象
        set(key, resultObj)
        // 缓存
        val result = JsonInfo(resultObj)
        cache.put(key, result)
        // 包裹对象返回
        return result
    }

    fun optArray(key: Int): JsonArrayInfo {
        val cacheValue = cache[key]
        if (cacheValue is JsonArrayInfo) {
            return cacheValue
        }
        // 尝试获取信息
        val resultObj = get(key, TYPE_ARRAY, ARRAY_FACTORY)
        // 二次绑定对象
        set(key, resultObj)
        // 缓存
        val result = JsonArrayInfo(resultObj)
        cache.put(key, result)
        // 包裹对象返回
        return result
    }

    operator fun set(key: Int, value: Any) {
        when (value) {
            is JsonArrayInfo -> {
                infoArray.put(key, value.infoArray)
            }
            is JsonInfo -> {
                infoArray.put(key, value.infoObject)
            }
            else -> {
                infoArray.put(key, value)
            }
        }
        // 更新数据后更新缓存
        cache.remove(key)
    }

    fun put(value: Any) {
        if (checkPut(value)) {
            when (value) {
                is JsonArrayInfo -> {
                    infoArray.put(value.infoArray)
                }
                is JsonInfo -> {
                    infoArray.put(value.infoObject)
                }
                else -> {
                    infoArray.put(value)
                }
            }
        }
    }

    fun remove(index: Int) {
        infoArray.remove(index)
    }

    val size: Int
        get() {
            return infoArray.length()
        }

    override fun toString(): String {
        return infoArray.toString()
    }

    protected open fun checkPut(value: Any): Boolean {
        return true
    }

}
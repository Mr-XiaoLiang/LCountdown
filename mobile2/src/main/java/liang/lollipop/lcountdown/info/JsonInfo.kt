package liang.lollipop.lcountdown.info

import org.json.JSONArray
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2020/5/26 23:44
 * 基础的信息类
 */
open class JsonInfo (val infoObject: JSONObject = JSONObject()) {

    companion object {

        private fun createWith(info: JsonInfo): JsonInfo {
            return JsonInfo(copyOut(JSONObject(), info))
        }

        private fun copyOut(newObj: JSONObject, info: JsonInfo): JSONObject {
            return copyObj(newObj, info.infoObject)
        }

        private fun copyObj(newObj: JSONObject, obj: JSONObject): JSONObject {
            obj.keys().forEach {
                if (it != null) {
                    newObj.put(it, obj.opt(it)?.copyValue())
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
                    copyObj(JSONObject(), this)
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
        val allKey = ArrayList<String>()
        this.infoObject.keys().forEach {
            allKey.add(it)
        }
        allKey.forEach {
            this.infoObject.remove(it)
        }
        copyOut(this.infoObject, info)
    }

    fun <T> invoke(c: Class<T>): T {
        return c.newInstance()
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(key: String, def: T): T {
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
                is JsonInfo -> {
                    JsonInfo(infoObject.optJSONObject(key)?: JSONObject())
                }
                is JsonArrayInfo -> {
                    JsonArrayInfo(infoObject.optJSONArray(key)?: JSONArray())
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

    fun optInfo(key: String): JsonInfo {
        // 尝试获取信息
        val resultObj = get(key, JSONObject())
        // 二次绑定对象
        set(key, resultObj)
        // 包裹对象返回
        return JsonInfo(resultObj)
    }

    fun optArray(key: String): JsonArrayInfo {
        // 尝试获取信息
        val resultObj = get(key, JSONArray())
        // 二次绑定对象
        set(key, resultObj)
        // 包裹对象返回
        return JsonArrayInfo(resultObj)
    }

    operator fun set(key: String, value: Any) {
        when (value) {
            is JsonArrayInfo -> {
                infoObject.put(key, value.infoArray)
            }
            is JsonInfo -> {
                infoObject.put(key, value.infoObject)
            }
            else -> {
                infoObject.put(key, value)
            }
        }
    }

    override fun toString(): String {
        return infoObject.toString()
    }
}
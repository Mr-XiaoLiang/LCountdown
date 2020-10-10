package liang.lollipop.lcountdown.info

import android.text.TextUtils
import kotlin.reflect.KProperty

/**
 * @author lollipop
 * @date 2020/5/27 01:17
 * JsonInfo的代理对象
 */
class IntDelegate(private val info: JsonInfo, private val def: Int = 0) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return info[property.name, def]
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        info[property.name] = value
    }
}

class StringDelegate(private val info: JsonInfo, private val def: String = "") {
    operator fun getValue(thisRef: Any, property: KProperty<*>): String {
        return info[property.name, def]
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        info[property.name] = value
    }
}

class LongDelegate(private val info: JsonInfo, private val def: Long = 0L) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return info[property.name, def]
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        info[property.name] = value
    }
}

class BooleanDelegate(private val info: JsonInfo, private val def: Boolean = false) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return info[property.name, def]
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        info[property.name] = value
    }
}

class FloatDelegate(private val info: JsonInfo, private val def: Float = 0F) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Float {
        return info[property.name, def]
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
        info[property.name] = value
    }
}

class DoubleDelegate(private val info: JsonInfo, private val def: Double = 0.0) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Double {
        return info[property.name, def]
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Double) {
        info[property.name] = value
    }
}

class JsonInfoDelegate<T: JsonInfo>(private val info: JsonInfo, private val caseTo: (JsonInfo) -> T) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        val srcObj = info.optInfo(property.name)
        val newObj = caseTo(srcObj)
        if (srcObj != newObj) {
            info[property.name] = newObj
        }
        return newObj
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: JsonInfo) {
        info[property.name] = value
    }
}

class EnumDelegate<T: Enum<T>>(private val info: JsonInfo,
                               private val def: T,
                               private val valueOf: (String) -> T) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        var value = info[property.name, def.name]
        if (TextUtils.isEmpty(value)) {
            value = def.name
        }
        return valueOf(value)
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        info[property.name] = value
    }
}

class JsonArrayDelegate<T: JsonArrayInfo>(private val info: JsonInfo, private val caseTo: (JsonArrayInfo) -> T) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        val srcArray = info.optArray(property.name)
        val newArray = caseTo(srcArray)
        if (srcArray != newArray) {
            info[property.name] = newArray
        }
        return newArray
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: JsonArrayInfo) {
        info[property.name] = value
    }
}

inline fun <reified S: JsonInfo, reified T: JsonInfo> S.convertTo(): T {
    if (this is T) {
        return this
    }
    val newObj = T::class.java.newInstance()
    newObj.copy(this)
    return newObj
}

inline fun <reified S: JsonArrayInfo, reified T: JsonArrayInfo> S.convertTo(): T {
    if (this is T) {
        return this
    }
    val newObj = T::class.java.newInstance()
    newObj.copy(this)
    return newObj
}
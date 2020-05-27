package liang.lollipop.lcountdown.info

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
        return caseTo(info.optInfo(property.name))
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: JsonInfo) {
        info[property.name] = value
    }
}
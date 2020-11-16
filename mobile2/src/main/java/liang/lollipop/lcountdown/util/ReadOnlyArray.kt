package liang.lollipop.lcountdown.util

/**
 * @author lollipop
 * @date 2020/6/26 21:37
 * 只读数组
 */
class ReadOnlyArray<out T> private constructor(private val values: Array<T>) {

    companion object {
        fun <T> create(vararg value: T): ReadOnlyArray<T> {
            return ReadOnlyArray(value)
        }
    }

    operator fun get(index: Int): T {
        return values[index]
    }

    val size = values.size

    val indices: IntRange
        get() = 0 until size

}
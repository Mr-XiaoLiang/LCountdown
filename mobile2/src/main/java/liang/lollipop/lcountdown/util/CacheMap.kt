package liang.lollipop.lcountdown.util

/**
 * @author lollipop
 * @date 7/6/20 00:10
 * 一个限制大小的Map，主要用来做缓存
 */
class CacheMap<K: Any, V: Any>(private val maxCount: Int): LinkedHashMap<K, V>() {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size >= maxCount
    }

}
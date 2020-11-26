package liang.lollipop.lcountdown.listener

/**
 * @author lollipop
 * @date 2020/5/13 23:29
 * 窗口缩进的提供者
 */
interface OnWindowInsetsProvider {

    fun addOnWindowInsetsListener(listener: OnWindowInsetsListener)

    fun removeOnWindowInsetsListener(listener: OnWindowInsetsListener)

}
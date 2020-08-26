package liang.lollipop.lcountdown.provider

/**
 * @author lollipop
 * @date 2020/6/30 00:14
 * 文本信息提供者
 */
interface BackgroundColorProvider {

    /**
     * 颜色数量
     */
    val colorCount: Int

    /**
     * 获取一个颜色
     */
    fun getColor(index: Int): Int

    /**
     * 设置一个颜色
     */
    fun setColor(index: Int, color: Int)

    /**
     * 添加一个颜色
     */
    fun addColor(color: Int)
}
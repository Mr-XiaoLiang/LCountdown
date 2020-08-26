package liang.lollipop.lcountdown.provider

/**
 * @author lollipop
 * @date 2020/6/30 00:14
 * 文本信息提供者
 */
interface BackgroundColorProvider {

    companion object {
        const val Linear = 0
        const val Sweep = 1
        const val Radial = 2
    }
    
    /**
     * 颜色数量
     */
    val colorCount: Int

    /**
     * 渲染类型
     */
    var gradientType: Int

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

    /**
     * 渐变色渲染的起点的X
     */
    var startX: Float
    /**
     * 渐变色渲染的起点的Y
     */
    var startY: Float

    /**
     * 渐变色渲染的终点的X
     */
    var endX: Float
    /**
     * 渐变色渲染的终点的Y
     */
    var endY: Float

}
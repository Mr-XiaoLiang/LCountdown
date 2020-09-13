package liang.lollipop.lcountdown.provider

/**
 * @author lollipop
 * @date 9/13/20 21:30
 * 文本位置
 */
interface TextLocationProvider {

    /**
     * 文本数量
     */
    val textCount: Int

    /**
     * 获取一个文本信息
     */
    fun getText(index: Int): String

    /**
     * 获取位置信息
     */
    fun getGravity(index: Int): Int

    /**
     * 获取位置信息
     */
    fun setGravity(index: Int, gravity: Int)

    /**
     * 获取X轴偏移量
     */
    fun getOffsetX(index: Int): Float

    /**
     * 设置一个X轴偏移量
     */
    fun setOffsetX(index: Int, offset: Float)

    /**
     * 获取X轴偏移量
     */
    fun getOffsetY(index: Int): Float

    /**
     * 设置一个X轴偏移量
     */
    fun setOffsetY(index: Int, offset: Float)

}
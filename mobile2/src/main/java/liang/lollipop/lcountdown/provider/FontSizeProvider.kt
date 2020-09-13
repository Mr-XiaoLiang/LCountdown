package liang.lollipop.lcountdown.provider

/**
 * @author lollipop
 * @date 2020/6/30 00:14
 * 文本信息提供者
 */
interface FontSizeProvider {

    /**
     * 文本数量
     */
    val textCount: Int

    /**
     * 获取一个文本信息
     */
    fun getText(index: Int): String

    /**
     * 获取一个字体大小
     */
    fun getFontSize(index: Int): Float

    /**
     * 设置一个字体大小
     */
    fun setFontSize(index: Int, value: Float)

}
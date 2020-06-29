package liang.lollipop.lcountdown.provider

/**
 * @author lollipop
 * @date 2020/6/30 00:14
 * 文本信息提供者
 */
interface TextInfoProvider {

    /**
     * 文本数量
     */
    val textCount: Int

    /**
     * 获取一个文本信息
     */
    fun getText(index: Int): String

    /**
     * 设置一个文本内容
     */
    fun setText(index: Int, value: String)

    /**
     * 添加一个文本信息
     */
    fun addText(value: String)

}
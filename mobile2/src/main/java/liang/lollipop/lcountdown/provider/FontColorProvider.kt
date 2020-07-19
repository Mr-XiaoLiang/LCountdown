package liang.lollipop.lcountdown.provider

import liang.lollipop.lcountdown.info.TextColor

/**
 * @author lollipop
 * @date 2020/6/30 00:14
 * 文本信息提供者
 */
interface FontColorProvider {

    /**
     * 文本数量
     */
    val textCount: Int

    /**
     * 获取一个文本信息
     */
    fun getText(index: Int): String

    /**
     * 获取一个文本信息
     */
    fun getFontColor(index: Int): TextColor

}
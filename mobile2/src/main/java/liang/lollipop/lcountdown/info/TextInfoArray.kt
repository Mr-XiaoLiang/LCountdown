package liang.lollipop.lcountdown.info

import android.util.SparseArray
import liang.lollipop.lcountdown.provider.FontColorProvider
import liang.lollipop.lcountdown.provider.FontSizeProvider
import liang.lollipop.lcountdown.provider.TextInfoProvider

/**
 * @author lollipop
 * @date 2020/6/27 22:47
 * 文字信息的数据集合
 */
class TextInfoArray: JsonArrayInfo(), TextInfoProvider, FontSizeProvider, FontColorProvider {

    private fun optTextInfo(index: Int): TextInfoImpl {
        return optInfo(index).convertTo()
    }

    override val textCount: Int
        get() { return this.size }

    override fun getFontSize(index: Int): Float {
        return optTextInfo(index).fontSize
    }

    override fun setFontSize(index: Int, value: Float) {
        optTextInfo(index).fontSize = value
    }

    override fun getText(index: Int): String {
        return optTextInfo(index).textValue
    }

    override fun getFontColor(index: Int): TextColor {
        return optTextInfo(index)
    }

    override fun setText(index: Int, value: String) {
        optTextInfo(index).textValue = value
    }

    override fun addText(value: String) {
        put(TextInfoImpl().apply {
            textValue = value
        })
    }

    override fun removeText(index: Int) {
        remove(index)
    }

    override fun checkPut(value: Any): Boolean {
        if (value !is TextInfoImpl) {
            return false
        }
        return super.checkPut(value)
    }

    private class TextInfoImpl: JsonInfo(), Text, Location, FontSize, TextColor {

        override var textValue by StringDelegate(this)

        override var left by IntDelegate(this)

        override var top by IntDelegate(this)

        override var right by IntDelegate(this)

        override var bottom by IntDelegate(this)

        override var fontSize by FloatDelegate(this, 16F)

        private val colorCache = SparseArray<TextTint?>()

        private val colorArray: ColorJsonArray by JsonArrayDelegate(this) {
            it.convertTo<JsonArrayInfo, ColorJsonArray>()
        }

        override val colorSize: Int
            get() {
                return colorArray.size
            }

        override fun getColor(index: Int): TextTint {
            var colorInfo = colorCache[index]
            if (colorInfo == null) {
                val color = colorArray.getColor(index)
                colorInfo = TextTint(color.start, color.length, color.color)
                colorCache.put(index, colorInfo)
            }
            return colorInfo
        }

        override fun setColor(index: Int, textTint: TextTint) {
            colorCache.put(index, textTint)
            val colorInfo = colorArray.getColor(index)
            colorInfo.start = textTint.start
            colorInfo.length = textTint.length
            colorInfo.color = textTint.color
            colorArray.setColor(index, colorInfo)
        }

        override fun addColor(textTint: TextTint) {
            colorArray.put(TextColorImpl().apply {
                start = textTint.start
                length = textTint.length
                color = textTint.color
            })
        }

        override fun removeColor(index: Int) {
            colorCache.remove(index)
            colorArray.remove(index)
        }

    }

    private class ColorJsonArray: JsonArrayInfo() {
        fun getColor(index: Int): TextColorImpl {
            return optInfo(index).convertTo()
        }
        fun setColor(index: Int, color: TextColorImpl) {
            set(index, color)
        }
    }

    private class TextColorImpl: JsonInfo() {
        var start by IntDelegate(this)
        var length by IntDelegate(this)
        var color by IntDelegate(this)
    }

}
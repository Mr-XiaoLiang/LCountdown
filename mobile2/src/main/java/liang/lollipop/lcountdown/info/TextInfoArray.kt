package liang.lollipop.lcountdown.info

import liang.lollipop.lcountdown.provider.TextInfoProvider

/**
 * @author lollipop
 * @date 2020/6/27 22:47
 * 文字信息的数据集合
 */
class TextInfoArray: JsonArrayInfo(), TextInfoProvider {

    private fun optTextInfo(index: Int): TextInfoImpl {
        return optInfo(index).convertTo()
    }

    override val textCount: Int
        get() { return this.size }

    override fun getText(index: Int): String {
        return optTextInfo(index).textValue
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

    fun getLocation(index: Int): Location {
        return optInfo(index).convertTo<JsonInfo, TextInfoImpl>()
    }

    fun getSize(index: Int): FontSize {
        return optInfo(index).convertTo<JsonInfo, TextInfoImpl>()
    }

    fun getColor(index: Int): TextColor {
        return optInfo(index).convertTo<JsonInfo, TextInfoImpl>()
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

        private val colorArray: ColorJsonArray by JsonArrayDelegate(this) {
            it.convertTo<JsonArrayInfo, ColorJsonArray>()
        }

        override val colorSize: Int
            get() {
                return colorArray.size
            }

        private val colorCache = ArrayList<TextTint?>()

        private val EMPTY_COLOR = TextTint(0, 0, 0)

        override fun getColor(index: Int): TextTint {
            if (keepColorSize(index)) {
                var colorInfo = colorCache[index]
                if (colorInfo == null) {
                    val color = colorArray.getColor(index)
                    colorInfo = TextTint(color.start, color.length, color.color)
                    colorCache[index] = colorInfo
                }
                return colorInfo
            }
            return EMPTY_COLOR
        }

        override fun setColor(index: Int, color: TextTint) {
            if (keepColorSize(index)) {
                colorCache[index] = color
                val colorInfo = colorArray.getColor(index)
                colorInfo.start = color.start
                colorInfo.length = color.length
                colorInfo.color = color.color
                colorArray.setColor(index, colorInfo)
            }
        }

        override fun addColor(textTint: TextTint) {
            if (keepColorSize(0)) {
                colorCache.add(textTint)
                colorArray.put(TextColorImpl().apply {
                    start = textTint.start
                    length = textTint.length
                    color = textTint.color
                })
            }
        }

        private fun keepColorSize(index: Int): Boolean {
            if (index < 0) {
                return false
            }
            if (index > 0 && index >= colorSize) {
                return false
            }
            while (colorCache.size < colorSize) {
                colorCache.add(null)
            }
            return true
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
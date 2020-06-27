package liang.lollipop.lcountdown.info

/**
 * @author lollipop
 * @date 2020/6/27 22:47
 * 文字信息的数据集合
 */
class TextInfoArray: JsonArrayInfo() {

    interface Text {
        var textValue: String
    }

    interface Location {
        var left: Int
        var top: Int
        var right: Int
        var bottom: Int
    }

    interface Size {
        var fontSize: Float
    }

    interface Color {
        val colorSize: Int
        fun getColor(index: Int): TextColor
        fun setColor(index: Int, color: TextColor)
        fun addColor(color: TextColor)
    }

    data class TextColor(val start: Int, val length: Int, val color: Int)

    fun getText(index: Int): Text {
        return optInfo(index).convertTo<JsonInfo, TextInfoImpl>()
    }

    fun getLocation(index: Int): Location {
        return optInfo(index).convertTo<JsonInfo, TextInfoImpl>()
    }

    fun getSize(index: Int): Size {
        return optInfo(index).convertTo<JsonInfo, TextInfoImpl>()
    }

    fun getColor(index: Int): Color {
        return optInfo(index).convertTo<JsonInfo, TextInfoImpl>()
    }

    fun addText(value: String) {
        val textInfoImpl = TextInfoImpl()
        textInfoImpl.textValue = value
        put(textInfoImpl)
    }

    override fun checkPut(value: Any): Boolean {
        if (value !is TextInfoImpl) {
            return false
        }
        return super.checkPut(value)
    }

    private class TextInfoImpl: JsonInfo(), Text, Location, Size, Color {

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

        private val colorCache = ArrayList<TextColor?>()

        private val EMPTY_COLOR = TextColor(0, 0, 0)

        override fun getColor(index: Int): TextColor {
            if (keepColorSize(index)) {
                var colorInfo = colorCache[index]
                if (colorInfo == null) {
                    val color = colorArray.getColor(index)
                    colorInfo = TextColor(color.start, color.length, color.color)
                    colorCache[index] = colorInfo
                }
                return colorInfo
            }
            return EMPTY_COLOR
        }

        override fun setColor(index: Int, color: TextColor) {
            if (keepColorSize(index)) {
                colorCache[index] = color
                val colorInfo = colorArray.getColor(index)
                colorInfo.start = color.start
                colorInfo.length = color.length
                colorInfo.color = color.color
                colorArray.setColor(index, colorInfo)
            }
        }

        override fun addColor(textColor: TextColor) {
            if (keepColorSize(0)) {
                colorCache.add(textColor)
                colorArray.put(TextColorImpl().apply {
                    start = textColor.start
                    length = textColor.length
                    color = textColor.color
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
package liang.lollipop.lcountdown.info

/**
 * @author lollipop
 * @date 2020/6/30 00:16
 */
interface Text {
    var textValue: String
}

interface Location {
    var left: Int
    var top: Int
    var right: Int
    var bottom: Int
}

interface FontSize {
    var fontSize: Float
}

interface TextColor {
    val colorSize: Int
    fun getColor(index: Int): TextTint
    fun setColor(index: Int, color: TextTint)
    fun addColor(color: TextTint)
}

data class TextTint(val start: Int, val length: Int, val color: Int)
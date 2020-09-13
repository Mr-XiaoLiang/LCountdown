package liang.lollipop.lcountdown.info

/**
 * @author lollipop
 * @date 2020/6/30 00:16
 */
interface Text {
    var textValue: String
}

interface Location {
    var gravity: Int
    var offsetX: Float
    var offsetY: Float
}

interface FontSize {
    var fontSize: Float
}

interface TextColor {
    val colorSize: Int
    fun getColor(index: Int): TextTint
    fun setColor(index: Int, textTint: TextTint)
    fun addColor(textTint: TextTint)
    fun removeColor(index: Int)
}

data class TextTint(val start: Int, val length: Int, val color: Int)
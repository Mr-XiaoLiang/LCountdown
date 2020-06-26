package liang.lollipop.lcountdown.util

/**
 * @author lollipop
 * @date 2020/6/26 21:34
 * 文字格式化
 */
object TextFormat {

    val KEYS = ReadOnlyArray.create(
            Part(0, "")
    )

    data class Part(val name: Int, val value: String)

}
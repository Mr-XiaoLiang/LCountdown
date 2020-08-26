package liang.lollipop.lcountdown.info

import liang.lollipop.lcountdown.provider.BackgroundColorProvider

/**
 * @author lollipop
 * @date 8/26/20 00:30
 * 背景描述信息
 */
class BackgroundInfo: JsonInfo() {

    /**
     * 渐变色渲染的起点的X
     */
    var startX: Float by FloatDelegate(this)
    /**
     * 渐变色渲染的起点的Y
     */
    var startY: Float by FloatDelegate(this)

    /**
     * 渐变色渲染的终点的X
     */
    var endX: Float by FloatDelegate(this)
    /**
     * 渐变色渲染的终点的Y
     */
    var endY: Float by FloatDelegate(this)

    /**
     * 颜色的集合
     */
    var colorList: ColorJsonArray by JsonArrayDelegate(this) {
        it.convertTo<JsonArrayInfo, ColorJsonArray>()
    }

    /**
     * 图片的路径
     */
    var imagePath: String by StringDelegate(this)

    /**
     * 左上角的圆角
     */
    var cornerLeftTop: Float by FloatDelegate(this)

    /**
     * 右上角的圆角
     */
    var cornerRightTop: Float by FloatDelegate(this)

    /**
     * 左下角的圆角
     */
    var cornerLeftBottom: Float by FloatDelegate(this)

    /**
     * 右下角的圆角
     */
    var cornerRightBottom: Float by FloatDelegate(this)

    class ColorJsonArray: JsonArrayInfo(), BackgroundColorProvider {
        override val colorCount: Int
            get() {
                return size
            }

        override fun getColor(index: Int): Int {
            return get(index, 0)
        }
        override fun setColor(index: Int, color: Int) {
            set(index, color)
        }
        override fun addColor(color: Int) {
            put(color)
        }
    }

}
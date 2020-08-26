package liang.lollipop.lcountdown.info

import liang.lollipop.lcountdown.provider.BackgroundColorProvider

/**
 * @author lollipop
 * @date 8/26/20 00:30
 * 背景描述信息
 */
class BackgroundInfo: JsonInfo(), BackgroundColorProvider {

    /**
     * 渐变色渲染的起点的X
     */
    override var startX: Float by FloatDelegate(this)
    /**
     * 渐变色渲染的起点的Y
     */
    override var startY: Float by FloatDelegate(this)

    /**
     * 渐变色渲染的终点的X
     */
    override var endX: Float by FloatDelegate(this)
    /**
     * 渐变色渲染的终点的Y
     */
    override var endY: Float by FloatDelegate(this)

    /**
     * 绘制类型
     */
    override var gradientType: Int by IntDelegate(this)

    /**
     * 颜色的集合
     */
    private var colorList: ColorJsonArray by JsonArrayDelegate(this) {
        it.convertTo<JsonArrayInfo, ColorJsonArray>()
    }

    override val colorCount: Int
        get() {
            return colorList.colorCount
        }

    override fun getColor(index: Int): Int {
        return colorList.getColor(index)
    }

    override fun setColor(index: Int, color: Int) {
        colorList.setColor(index, color)
    }

    override fun addColor(color: Int) {
        colorList.addColor(color)
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

    private class ColorJsonArray: JsonArrayInfo() {
        val colorCount: Int
            get() {
                return size
            }

        fun getColor(index: Int): Int {
            return get(index, 0)
        }
        fun setColor(index: Int, color: Int) {
            set(index, color)
        }
        fun addColor(color: Int) {
            put(color)
        }
    }

}
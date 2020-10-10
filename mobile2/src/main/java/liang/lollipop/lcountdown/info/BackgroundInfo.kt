package liang.lollipop.lcountdown.info

import liang.lollipop.lcountdown.drawable.GradientDrawable
import liang.lollipop.lcountdown.provider.BackgroundCardProvider
import liang.lollipop.lcountdown.provider.BackgroundColorProvider

/**
 * @author lollipop
 * @date 8/26/20 00:30
 * 背景描述信息
 */
class BackgroundInfo: JsonInfo(), BackgroundColorProvider, BackgroundCardProvider {

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
     * 将int类型的渲染类型转换为GradientDrawable能接受的枚举
     * 这是一个只读方法
     */
    val gradientDrawableType: GradientDrawable.Type
        get() {
            return when(gradientType) {
                BackgroundColorProvider.Sweep -> {
                    GradientDrawable.Type.Sweep
                }
                BackgroundColorProvider.Radial -> {
                    GradientDrawable.Type.Radial
                }
                else -> {
                    GradientDrawable.Type.Linear
                }
            }
        }

    /**
     * 颜色的集合
     */
    private val colorList: ColorJsonArray by JsonArrayDelegate(this) {
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

    override fun removeColor(index: Int) {
        colorList.removeColor(index)
    }

    fun getColorArray(): IntArray {
        return colorList.getColorArray()
    }

    /**
     * 图片的路径
     */
    var imagePath: String by StringDelegate(this)

//    /**
//     * 左上角的圆角
//     */
//    var cornerLeftTop: Float by FloatDelegate(this)
//
//    /**
//     * 右上角的圆角
//     */
//    var cornerRightTop: Float by FloatDelegate(this)
//
//    /**
//     * 左下角的圆角
//     */
//    var cornerLeftBottom: Float by FloatDelegate(this)
//
//    /**
//     * 右下角的圆角
//     */
//    var cornerRightBottom: Float by FloatDelegate(this)

    private class ColorJsonArray: JsonArrayInfo() {

        private var colorCacheArray: IntArray? = null

        private var colorChanged = false

        val colorCount: Int
            get() {
                return size
            }

        fun getColor(index: Int): Int {
            return get(index, 0)
        }
        fun setColor(index: Int, color: Int) {
            set(index, color)
            colorChanged = true
        }
        fun addColor(color: Int) {
            put(color)
            colorChanged = true
        }
        fun removeColor(index: Int) {
            remove(index)
            colorChanged = true
        }

        fun getColorArray(): IntArray {
            var colorCache = colorCacheArray
            if (colorCache == null || colorCache.size != colorCount) {
                colorCache = IntArray(colorCount)
                colorChanged = true
            }
            if (colorChanged) {
                for (index in colorCache.indices) {
                    colorCache[index] = getColor(index)
                }
                colorCacheArray = colorCache
                colorChanged = false
            }
            return colorCache
        }

    }

    override var isShow by BooleanDelegate(this)

    override var corner by FloatDelegate(this)

    override var marginLeft by FloatDelegate(this)

    override var marginTop by FloatDelegate(this)

    override var marginRight by FloatDelegate(this)

    override var marginBottom by FloatDelegate(this)

    override var elevation by FloatDelegate(this)

    override var width by FloatDelegate(this)

    override var height by FloatDelegate(this)

}
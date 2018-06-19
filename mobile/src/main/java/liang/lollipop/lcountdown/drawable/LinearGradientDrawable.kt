package liang.lollipop.lcountdown.drawable

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * 线性渐变的Drawable
 * @author Lollipop
 */
class LinearGradientDrawable(private var orientation: Orientation = Orientation.Horizontal): Drawable() {

    private val colorList = ArrayList<Int>()
    private val positionsList = ArrayList<Float>()

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
    }

    override fun draw(canvas: Canvas?) {
        canvas?.drawRect(bounds,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    enum class Orientation{
        Horizontal,Vertical
    }

    fun changeOrientation(o: Orientation){
        this.orientation = o
        callUpdate()
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        callUpdate()
    }

    fun callUpdate(){

        paint.shader = if(orientation == Orientation.Horizontal){
            LinearGradient(bounds.left.toFloat(),bounds.top.toFloat(),bounds.right.toFloat(),bounds.top.toFloat(),
                    colorList.toIntArray(),positionsList.toFloatArray(),Shader.TileMode.CLAMP)
        }else{
            LinearGradient(bounds.left.toFloat(),bounds.top.toFloat(),bounds.left.toFloat(),bounds.bottom.toFloat(),
                    colorList.toIntArray(),positionsList.toFloatArray(),Shader.TileMode.CLAMP)
        }

        invalidateSelf()

    }

    fun onColorChange(vararg colors: Int){
        colorList.clear()
        positionsList.clear()

        val step = 1.0F / colors.size
        for(index in 0 until colors.size){
            val color = colors[index]
            colorList.add(color)
            positionsList.add(step*index)
        }
    }

    fun onPositionsChange(vararg positions: Float){
        if(positions.size != colorList.size){
            throw RuntimeException("positions.size = ${positions.size},colors.size = ${colorList.size}")
        }
        positionsList.clear()
        for(position in positions){
            positionsList.add(position)
        }

    }

}
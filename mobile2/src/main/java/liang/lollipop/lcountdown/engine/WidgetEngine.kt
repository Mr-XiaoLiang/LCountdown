package liang.lollipop.lcountdown.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.drawable.GradientDrawable
import liang.lollipop.lcountdown.info.BackgroundInfo
import liang.lollipop.lcountdown.info.TextColor
import liang.lollipop.lcountdown.info.TextInfoArray
import liang.lollipop.lcountdown.info.WidgetInfo
import liang.lollipop.lcountdown.util.toDip
import liang.lollipop.lcountdown.util.toSp
import kotlin.math.abs

/**
 * @author lollipop
 * @date 10/2/20 20:24
 */
class WidgetEngine(private val widgetRoot: FrameLayout): RenderEngine() {

    companion object {
        fun create(context: Context): WidgetEngine {
            return WidgetEngine(FrameLayout(context))
        }

    }

    private val context = widgetRoot.context

    private val textViews = ArrayList<TextView>()

    private val recyclerViews = ArrayList<View>()

    private val textViewCreator: () -> TextView by lazy {
        {
            TextView(context)
        }
    }

    override fun draw(canvas: Canvas) {
        widgetRoot.draw(canvas)
    }

    fun updateAll(widgetInfo: WidgetInfo) {
        updateCard(widgetInfo.backgroundInfo)
        updateBackground(widgetInfo.backgroundInfo)
        updateText(widgetInfo.textInfoArray)
        measure(widgetRoot,
                widgetInfo.width.toDip(widgetRoot).toInt(),
                widgetInfo.height.toDip(widgetRoot).toInt())
        layout(widgetRoot,
                widgetInfo.width.toDip(widgetRoot).toInt(),
                widgetInfo.height.toDip(widgetRoot).toInt())
    }

    fun updateCard(backgroundInfo: BackgroundInfo) {
        var cardGroup: CardView? = find(R.id.widgetCard)
        if (cardGroup == null) {
            val card = findCardView()
            card.id = R.id.widgetCard
            add(widgetRoot, card)
            cardGroup = card
        }
        val show = backgroundInfo.isShow
        if (show) {
            cardGroup.cardElevation = backgroundInfo.elevation.toDip(context)
            cardGroup.radius = backgroundInfo.corner.toDip(context)
            cardGroup.setCardBackgroundColor(Color.WHITE)
        } else {
            cardGroup.cardElevation = 0F
            cardGroup.radius = 0F
            cardGroup.setCardBackgroundColor(Color.TRANSPARENT)
        }

        matchParent(cardGroup)
        val cardLayoutParams = findFrameLayoutParams(cardGroup)
        if (show) {
            cardLayoutParams.setMargins(
                    backgroundInfo.marginLeft.toDip(cardGroup).toInt(),
                    backgroundInfo.marginTop.toDip(cardGroup).toInt(),
                    backgroundInfo.marginRight.toDip(cardGroup).toInt(),
                    backgroundInfo.marginBottom.toDip(cardGroup).toInt(),
            )
        } else {
            cardLayoutParams.setMargins(0, 0, 0, 0)
        }
        cardGroup.layoutParams = cardLayoutParams
    }

    fun updateBackground(backgroundInfo: BackgroundInfo) {
        var backgroundView: ImageView? = find(R.id.widgetBackground)
        if (backgroundInfo.isShow) {
            if (backgroundView == null) {
                val background = findImageView()
                background.id = R.id.widgetBackground
                background.scaleType = ImageView.ScaleType.CENTER_CROP
                add(getCardGroup(), background)
                backgroundView = background
            }
            backgroundView.visibility = View.VISIBLE
            matchParent(backgroundView)
            updateBackgroundColor(backgroundView, backgroundInfo)
            updateBackgroundImage(backgroundView, backgroundInfo)
        } else {
            backgroundView?.visibility = View.GONE
        }
    }

    private fun updateBackgroundColor(backgroundView: ImageView, backgroundInfo: BackgroundInfo) {
        var background = backgroundView.background
        if (background == null || background !is GradientDrawable) {
            background = GradientDrawable()
            backgroundView.background = background
        }
        background.changeColor(*backgroundInfo.getColorArray())
        background.changeStart(backgroundInfo.startX, backgroundInfo.startY)
        background.changeEnd(backgroundInfo.endX, backgroundInfo.endY)
        background.gradientType = backgroundInfo.gradientDrawableType
        background.updateGradient()
    }

    private fun updateBackgroundImage(backgroundView: ImageView, backgroundInfo: BackgroundInfo) {
        // TODO
    }

    fun updateText(textInfoArray: TextInfoArray) {
        // 需要的文本数量
        val textCount = textInfoArray.textCount
        // 文本的容器
        val textGroup = getTextGroup()
        // 移除多余的文本View
        while(textViews.size > textCount) {
            recycler(textViews.removeAt(0))
        }
        // 补充文本View的数量
        while (textViews.size < textCount) {
            val view = findTextView()
            add(textGroup, view)
        }

        // 获取偏移量的步长
        val offsetStepX = textGroup.width * 0.001F
        val offsetStepY = textGroup.height * 0.001F

        // 按顺序排版
        for (index in textViews.indices) {
            val textView = textViews[index]
            // 寻找或创建LayoutParams
            val layoutParams = findFrameLayoutParams(textView)
            val gravity = textInfoArray.getGravity(index)
            val offsetX = textInfoArray.getOffsetX(index)
            val offsetY = textInfoArray.getOffsetY(index)
            var left = 0
            var top = 0
            var right = 0
            var bottom = 0
            if (offsetX < 0) {
                right = (offsetX * -1 * offsetStepX).toInt()
            } else {
                left = (offsetX * offsetStepX).toInt()
            }
            if (offsetY < 0) {
                top = (offsetY * -1 * offsetStepY).toInt()
            } else {
                bottom = (offsetY * offsetStepY).toInt()
            }
            // 判定是否参数发生了变化，如果没有发生变化，
            // 那么可以一定程度的减少重新布局带来的消耗
            if (layoutParams.leftMargin != left ||
                    layoutParams.topMargin != top ||
                    layoutParams.rightMargin != right ||
                    layoutParams.bottomMargin != bottom ||
                    layoutParams.gravity != gravity) {
                layoutParams.gravity = gravity
                layoutParams.setMargins(left, top, right, bottom)
                textView.layoutParams = layoutParams
            }

            textView.tintText(textInfoArray.getFontColor(index),
                    formatInfo(textInfoArray.getText(index)))
            // 如果字体大小没有发生变化，那么不做修改，这可以避免引发不必要的重新布局
            val fontSize = textInfoArray.getFontSize(index).toSp(textView)
            // 在这里，计算差值，是为了避免浮点计算造成的公差，
            // 同时，认为小于千分之一的修改不生效
            if (abs(fontSize - textView.textSize) > 0.001) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textInfoArray.getFontSize(index))
            }
        }
    }

    private fun TextView.tintText(fontColor: TextColor, value: String) {
        val colorSize = fontColor.colorSize
        if (colorSize < 2) {
            this.text = value
            if (colorSize == 0) {
                this.setTextColor(Color.BLACK)
            } else {
                this.setTextColor(fontColor.getColor(0).color)
            }
            return
        }
        val builder = SpannableStringBuilder(value)
        for (index in 0 until colorSize) {
            val tint = fontColor.getColor(index)
            builder.setSpan(ForegroundColorSpan(tint.color),
                    tint.start, tint.start + tint.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        this.text = builder
    }

    private fun formatInfo(value: String): String {
        // TODO
        return value
    }

    private fun getTextGroup(): FrameLayout {
        return getCardGroup()
    }

    private fun getCardGroup(): CardView {
        return find(R.id.widgetCard)?:throw EngineException("Widget card not found")
    }

    private fun recycler(view: View) {
        remove(view)
        recyclerViews.add(view)
    }

    private fun findTextView(): TextView {
        return findFromList(recyclerViews, textViewCreator)
    }

    private fun findCardView(): CardView {
        return findFromList(recyclerViews) {
            CardView(context)
        }
    }

    private fun findImageView(): ImageView {
        return findFromList(recyclerViews) {
            ImageView(context)
        }
    }

    private inline fun <reified T: View> find(id: Int): T? {
        try {
            return widgetRoot.findViewById(id)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }


}
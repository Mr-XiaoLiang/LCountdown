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
import liang.lollipop.lcountdown.util.InfoStuffHelper
import liang.lollipop.lcountdown.util.dp2px
import liang.lollipop.lcountdown.util.load
import liang.lollipop.lcountdown.util.sp2px
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
        private const val TOLERANCE = 0.001F
    }

    private val context = widgetRoot.context

    private val textViews = ArrayList<TextView>()

    private val recyclerViews = ArrayList<View>()

    private val infoStuffHelper = InfoStuffHelper(context)

    private val textViewCreator: () -> TextView by lazy {
        { TextView(context) }
    }

    override fun draw(canvas: Canvas) {
        widgetRoot.draw(canvas)
    }

    fun updateAll(widgetInfo: WidgetInfo) {
        infoStuffHelper.updateTarget(widgetInfo)
        updateCard(widgetInfo.backgroundInfo)
        updateBackground(widgetInfo.backgroundInfo)
        updateText(widgetInfo.textInfoArray)

        val width = widgetInfo.width.dp2px().toInt()
        val height = widgetInfo.height.dp2px().toInt()

        val layoutParams = widgetRoot.layoutParams
        if (layoutParams != null) {
            layoutParams.width = width
            layoutParams.height = height
            widgetRoot.layoutParams = layoutParams
        } else {
            measure(widgetRoot, width, height)
            layout(widgetRoot, width, height)
        }
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
        val elevation: Float
        val corner: Float
        if (show) {
            elevation = backgroundInfo.elevation.dp2px()
            corner = backgroundInfo.corner.dp2px()
            cardGroup.setCardBackgroundColor(Color.WHITE)
        } else {
            elevation = 0F
            corner = 0F
            cardGroup.setCardBackgroundColor(Color.TRANSPARENT)
        }
        if (abs(cardGroup.cardElevation - elevation) > TOLERANCE) {
            cardGroup.cardElevation = elevation
        }
        if (abs(cardGroup.radius - corner) > TOLERANCE) {
            cardGroup.radius = corner
        }

        matchParent(cardGroup)
        val cardLayoutParams = findFrameLayoutParams(cardGroup)
        val layoutChanged = if (show) {
            changeMarginIfDiff(cardLayoutParams,
                    backgroundInfo.marginLeft.dp2px().toInt(),
                    backgroundInfo.marginTop.dp2px().toInt(),
                    backgroundInfo.marginRight.dp2px().toInt(),
                    backgroundInfo.marginBottom.dp2px().toInt())
        } else {
            changeMarginIfDiff(cardLayoutParams,
                    0, 0, 0, 0)
        }
        if (layoutChanged) {
            cardGroup.layoutParams = cardLayoutParams
        }
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
        backgroundView.load(backgroundInfo.imagePath)
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
            textViews.add(view)
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
            if (changeMarginIfDiff(layoutParams, left, top, right, bottom)
                    || layoutParams.gravity != gravity) {
                // TODO 此处存在疑问
                layoutParams.gravity = gravity
                textView.layoutParams = layoutParams
            }

            textView.tintText(textInfoArray.getFontColor(index),
                    formatInfo(textInfoArray.getText(index)))
            // 如果字体大小没有发生变化，那么不做修改，这可以避免引发不必要的重新布局
            val fontSize = textInfoArray.getFontSize(index).sp2px()
            // 在这里，计算差值，是为了避免浮点计算造成的公差，
            // 同时，认为小于千分之一的修改不生效
            if (abs(fontSize - textView.textSize) > TOLERANCE) {
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
        return infoStuffHelper.stuff(value)
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
package liang.lollipop.lcountdown.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import liang.lollipop.lcountdown.info.TextColor
import liang.lollipop.lcountdown.info.WidgetInfo

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

    private var textGroup: FrameLayout? = null

    private var cardGroup: CardView? = null

    private var backgroundView: ImageView? = null

    override fun draw(canvas: Canvas) {
        widgetRoot.draw(canvas)
    }

    fun update(widgetInfo: WidgetInfo) {
        updateCard(widgetInfo)
        updateBackground(widgetInfo)
        updateText(widgetInfo)
        measure(widgetRoot, widgetInfo.width, widgetInfo.height)
        layout(widgetRoot, widgetInfo.width, widgetInfo.height)
    }

    private fun updateCard(widgetInfo: WidgetInfo) {
        if (cardGroup == null) {
//            cardGroup =
        }
        // TODO
    }

    private fun updateBackground(widgetInfo: WidgetInfo) {
        updateBackgroundColor(widgetInfo)
        updateBackgroundImage(widgetInfo)
    }

    private fun updateBackgroundColor(widgetInfo: WidgetInfo) {
        // TODO
    }

    private fun updateBackgroundImage(widgetInfo: WidgetInfo) {
        // TODO
    }

    private fun updateText(widgetInfo: WidgetInfo) {
        val textInfoArray = widgetInfo.textInfoArray
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
            layoutParams.gravity = textInfoArray.getGravity(index)
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
            layoutParams.setMargins(left, top, right, bottom)
            textView.layoutParams = layoutParams

            textView.tintText(textInfoArray.getFontColor(index), textInfoArray.getText(index))
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textInfoArray.getFontSize(index))
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

    private fun findFrameLayoutParams(view: View): FrameLayout.LayoutParams {
        var layoutParams = view.layoutParams
        if (layoutParams is FrameLayout.LayoutParams) {
            return layoutParams
        }
        layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        view.layoutParams = layoutParams
        return layoutParams
    }

    private fun getTextGroup(): FrameLayout {
        val group = textGroup
        if (group != null) {
            return group
        }
        val frameLayout = findFrameLayout()
        TODO("创建文本容器")
    }

    private fun recycler(view: View) {
        remove(view)
        recyclerViews.add(view)
    }

    private fun findTextView(): TextView {
        return findFromList(recyclerViews) {
            TextView(context)
        }
    }

    private fun findCardView(): CardView {
        return findFromList(recyclerViews) {
            CardView(context)
        }
    }

    private fun findFrameLayout(): FrameLayout {
        return findFromList(recyclerViews) {
            FrameLayout(context)
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
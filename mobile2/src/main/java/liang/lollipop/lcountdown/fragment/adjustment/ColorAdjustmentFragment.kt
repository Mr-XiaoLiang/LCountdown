package liang.lollipop.lcountdown.fragment.adjustment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.info.TextColor
import liang.lollipop.lcountdown.info.TextTint
import liang.lollipop.lcountdown.provider.FontColorProvider

/**
 * @author lollipop
 * @date 7/14/20 00:13
 * 颜色调整的fragment
 */
class ColorAdjustmentFragment: CardAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_color
    override val icon: Int
        get() = R.drawable.ic_baseline_color_lens_24
    override val title: Int
        get() = R.string.title_color
    override val colorId: Int
        get() = R.color.focusColorAdjust

    private val fontColorProvider = FontColorProviderWrapper(null)
    private var onFontColorChangeCallback: ((Int, TextColor) -> Unit)? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            fontColorProvider.provider = context.getFontColorProvider()
            onFontColorChangeCallback = { index, fontColor ->
                context.onFontColorChange(index, fontColor)
            }
        } else {
            parentFragment?.let { parent ->
                if (parent is Callback) {
                    fontColorProvider.provider = parent.getFontColorProvider()
                    onFontColorChangeCallback = { index, fontColor ->
                        parent.onFontColorChange(index, fontColor)
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        fontColorProvider.provider = null
    }

    private class ItemHolder(
            view: View,
            private val onClick: (Int) -> Unit,
            private val onLongClick: (Int, Boolean) -> Unit) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup,
                       onClick: (Int) -> Unit,
                       onLongClick: (Int, Boolean) -> Unit): ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_in_color_panel, parent, false),
                        onClick, onLongClick)
            }
        }

        private val textView: TextView by lazy {
            itemView.findViewById<TextView>(R.id.textView)
        }

        fun bind(info: String, isSelected: Boolean) {
            textView.text = info
        }
    }

    private class FontColorProviderWrapper(var provider: FontColorProvider?) : FontColorProvider {

        private val emptyTint = TextTint(0, 0, 0)

        private val emptyColor = object : TextColor {
            override val colorSize: Int
                get() = 0

            override fun getColor(index: Int): TextTint {
                return emptyTint
            }

            override fun setColor(index: Int, textTint: TextTint) {
            }

            override fun addColor(textTint: TextTint) {
            }

            override fun removeColor(index: Int) {
            }

        }

        override val textCount: Int
            get() = provider?.textCount?:0

        override fun getText(index: Int): String {
            return provider?.getText(index)?:""
        }

        override fun getFontColor(index: Int): TextColor {
            return provider?.getFontColor(index)?:emptyColor
        }

    }

    interface Callback {
        fun getFontColorProvider(): FontColorProvider
        fun onFontColorChange(index: Int, fontColor: TextColor)
    }

}
package liang.lollipop.lcountdown.fragment.adjustment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_adjustment_color.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.info.TextColor
import liang.lollipop.lcountdown.info.TextTint
import liang.lollipop.lcountdown.listener.TextFocusProvider
import liang.lollipop.lcountdown.provider.FontColorProvider
import liang.lollipop.lcountdown.view.CheckableTextView

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
    private var textFocusProvider: TextFocusProvider? = null

    private val focusIndex: Int
        get() {
            return textFocusProvider?.getSelectedIndex()?:-1
        }

    private val focusItemAdapter = FocusItemAdapter(fontColorProvider, {
        focusIndex
    }, { index ->
        focusChange(index)
    }, { index, isPressed ->
        showInfoDetail(index, isPressed)
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        recycleView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recycleView.adapter = focusItemAdapter
    }

    private fun showInfoDetail(index: Int, isPressed: Boolean) {
        // TODO
    }

    private fun focusChange(index: Int) {
        if (focusIndex == index) {
            return
        }
        textFocusProvider?.onTextSelected(index)
        // TODO
    }

    private fun onColorChange() {
        // TODO
    }

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
        if (context is TextFocusProvider) {
            textFocusProvider = context
        } else {
            parentFragment?.let { parent ->
                if (parent is TextFocusProvider) {
                    textFocusProvider = parent
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        fontColorProvider.provider = null
        textFocusProvider = null
    }

    private class FocusItemAdapter(
            private val fontColorProvider: FontColorProvider,
            private val focusIndex: () -> Int,
            private val onClick: (Int) -> Unit,
            private val onLongClick: (Int, Boolean) -> Unit): RecyclerView.Adapter<ItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder.create(parent, onClick, onLongClick)
        }

        override fun getItemCount(): Int {
            return fontColorProvider.textCount
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bind(fontColorProvider.getText(position), focusIndex() == position)
        }

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

        private val textView: CheckableTextView by lazy {
            itemView.findViewById<CheckableTextView>(R.id.textView)
        }

        fun bind(info: String, isSelected: Boolean) {
            textView.text = info
            textView.isChecked = isSelected
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
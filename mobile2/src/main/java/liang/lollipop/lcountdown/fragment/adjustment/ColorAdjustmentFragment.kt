package liang.lollipop.lcountdown.fragment.adjustment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_adjustment_color.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.info.TextColor
import liang.lollipop.lcountdown.info.TextTint
import liang.lollipop.lcountdown.listener.TextFocusProvider
import liang.lollipop.lcountdown.provider.FontColorProvider
import liang.lollipop.lcountdown.util.parseColor
import liang.lollipop.lcountdown.view.CheckableTextView
import java.util.*

/**
 * @author lollipop
 * @date 7/14/20 00:13
 * 颜色调整的fragment
 */
class ColorAdjustmentFragment: BaseAdjustmentFragment() {
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
    private var targetColor = 0

    private val focusItemAdapter = FocusItemAdapter(fontColorProvider, {
        focusIndex
    }, { index ->
        focusChange(index)
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        huePalette.onHueChange { hue, _ ->
            satValPalette.onHueChange(hue.toFloat())
        }
        satValPalette.onHSVChange { _, color, isUser ->
            targetColor = color and 0x00FFFFFF or (targetColor and 0xFF000000.toInt())
            if (isUser) {
                onColorChange()
            }
        }
        transparencyPalette.onTransparencyChange { _, alphaI, isUser ->
            targetColor = targetColor and 0x00FFFFFF or (alphaI shl 24)
            if (isUser) {
                onColorChange()
            }
        }

        colorValueView.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                parserValue(colorValueView.text?.toString()?:"")
                true
            } else {
                false
            }
        }

        recycleView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recycleView.adapter = focusItemAdapter

        parser(Color.WHITE)
    }

    private fun parserValue(value: String) {
        if (value.isEmpty()) {
            colorToValue()
            return
        }
        val color: Int = value.parseColor()
        parser(color)
    }

    private fun parser(color: Int) {
        targetColor = color
        transparencyPalette.parser(Color.alpha(color))
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        satValPalette.parser(hsv[1], hsv[2])
        huePalette.parser(hsv[0])
        colorToValue()
    }

    @SuppressLint("SetTextI18n")
    private fun colorToValue() {
        val alpha = Color.alpha(targetColor).format()
        val red = Color.red(targetColor).format()
        val green = Color.green(targetColor).format()
        val blue = Color.blue(targetColor).format()
        colorValueView.setText("${alpha}${red}${green}${blue}")
    }

    private fun Int.format(): String {
        return this.toString(16).toUpperCase(Locale.US).let {
            if (it.length < 2) {
                "0$it"
            } else {
                it
            }
        }
    }

    private fun focusChange(index: Int) {
        textFocusProvider?.onTextSelected(index)
        parser(if (index < 0) {
            Color.WHITE
        } else {
            getColorByIndex(index)
        })
    }

    private fun onColorChange() {
        val index = textFocusProvider?.getSelectedIndex()?:-1
        if (index < 0) {
            return
        }
        val fontColor = setColorByIndex(index, targetColor)
        onFontColorChangeCallback?.invoke(index, fontColor)
    }

    private fun getColorByIndex(index: Int): Int {
        val fontColor = fontColorProvider.getFontColor(index)
        if (fontColor.colorSize < 1) {
            return Color.WHITE
        }
        return fontColor.getColor(0).color
    }

    private fun setColorByIndex(index: Int, color: Int): TextColor {
        val fontColor = fontColorProvider.getFontColor(index)
        if (fontColor.colorSize < 1) {
            fontColor.addColor(TextTint(-1, -1, color))
        } else {
            fontColor.setColor(0, TextTint(-1, -1, color))
        }
        return fontColor
    }

    override fun onResume() {
        super.onResume()
        focusItemAdapter.notifyDataSetChanged()
        parser(textFocusProvider?.let { getColorByIndex(it.getSelectedIndex()) }?:Color.WHITE)
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
            private val onClick: (Int) -> Unit): RecyclerView.Adapter<ItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder.create(parent, onClick)
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
            private val onClick: (Int) -> Unit) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup,
                       onClick: (Int) -> Unit): ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_in_color_panel, parent, false),
                        onClick)
            }
        }

        init {
            itemView.setOnClickListener {
                onClick(this.adapterPosition)
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
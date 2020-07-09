package liang.lollipop.lcountdown.fragment.adjustment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_adjustment_font.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.provider.FontSizeProvider
import liang.lollipop.lcountdown.util.CacheMap
import liang.lollipop.lcountdown.view.InnerDialogProvider
import liang.lollipop.lcountdown.view.LSeekBar
import liang.lollipop.lpunch.utils.StringToColorUtil

/**
 * @author lollipop
 * @date 2020/6/20 20:55
 * 文字调整的碎片页
 */
class FontAdjustmentFragment: CardAdjustmentFragment() {
    override val layoutId = R.layout.fragment_adjustment_font

    override val icon = R.drawable.ic_baseline_format_size_24

    override val title = R.string.title_font

    override val colorId = R.color.focusFontAdjust

    private var onFontSizeChangeCallback: ((Int, Float) -> Unit)? = null

    private val fontSizeProvider = FontSizeProviderWrapper(null)

    private val adapter = FontItemAdapter(fontSizeProvider, this::onItemClick, this::onItemSizeChange)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recycleView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun onItemClick(position: Int) {
        // TODO
    }

    private fun onItemSizeChange(position: Int, size: Float) {
        // TODO
    }

    private class AdjustmentProvider: InnerDialogProvider() {
        override val layoutId = R.layout.dialog_adjustment_font
    }

    private class FontItemAdapter(
            private val fontSizeProvider: FontSizeProviderWrapper,
            private val onClickListener: (Int) -> Unit,
            private val onSizeChangedListener: (Int, Float) -> Unit):
            RecyclerView.Adapter<FontItemHolder>()  {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontItemHolder {
            return FontItemHolder.create(parent, onSizeChangedListener, onClickListener)
        }

        override fun getItemCount(): Int {
            return fontSizeProvider.textCount
        }

        override fun onBindViewHolder(holder: FontItemHolder, position: Int) {
            val name = fontSizeProvider.getText(position)
            holder.bind(name, fontSizeProvider.getColor(name), fontSizeProvider.getFontSize(position))
        }

    }

    private class FontItemHolder(view: View,
        private val changeListener: (Int, Float) -> Unit,
        private val clickListener: (Int) -> Unit): RecyclerView.ViewHolder(view) {
        companion object {
            fun create(parent: ViewGroup,
                       changeListener: (Int, Float) -> Unit,
                       clickListener: (Int) -> Unit): FontItemHolder {
                return FontItemHolder(
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_widget_font, parent, false),
                        changeListener,
                        clickListener)
            }
        }

        private val seekBar: LSeekBar = itemView.findViewById(R.id.seekBar)

        init {
            seekBar.onProgressChange { _, progress ->
                changeListener(adapterPosition, progress)
            }
            itemView.setOnClickListener {
                clickListener(adapterPosition)
            }
        }

        private val nameView: TextView by lazy {
            itemView.findViewById<TextView>(R.id.textView)
        }

        fun bind(name: String, color: Int, size: Float) {
            nameView.text = name
            seekBar.setTheme(color)
            seekBar.setProgress(size, false)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            fontSizeProvider.provider = context.getFontSizeProvider()
            onFontSizeChangeCallback = { index, fontSize ->
                context.onFontSizeChange(index, fontSize)
            }
        } else {
            parentFragment?.let { parent ->
                if (parent is Callback) {
                    fontSizeProvider.provider = parent.getFontSizeProvider()
                    onFontSizeChangeCallback = { index, fontSize ->
                        parent.onFontSizeChange(index, fontSize)
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        fontSizeProvider.provider = null
        onFontSizeChangeCallback = null
    }

    interface Callback {
        fun getFontSizeProvider(): FontSizeProvider
        fun onFontSizeChange(index: Int, fontSize: Float)
    }

    private class FontSizeProviderWrapper(var provider: FontSizeProvider?): FontSizeProvider {
        override val textCount: Int
            get() {
                return provider?.textCount?:0
            }

        private val colorCache = CacheMap<String, Int>(20)

        fun getColor(value: String): Int {
            if (TextUtils.isEmpty(value)) {
                return Color.BLACK
            }
            val cache = colorCache[value]
            if (cache != null) {
                return cache
            }
            val color = StringToColorUtil.format(value)
            colorCache[value] = color
            return color
        }

        override fun getText(index: Int): String {
            return provider?.getText(index)?:""
        }

        override fun setText(index: Int, value: String) {
        }

        override fun addText(value: String) {
        }

        override fun removeText(index: Int) {
        }

        override fun getFontSize(index: Int): Float {
            return provider?.getFontSize(index)?:0F
        }

        override fun setFontSize(index: Int, value: Float) {
            provider?.setFontSize(index, value)
        }

    }

}